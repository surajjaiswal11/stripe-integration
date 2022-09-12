package com.mindbowser.stripe.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.mindbowser.stripe.constant.ExceptionConstant;
import com.mindbowser.stripe.constant.StaticKey;
import com.mindbowser.stripe.entity.Cards;
import com.mindbowser.stripe.entity.Customers;
import com.mindbowser.stripe.entity.Invoice;
import com.mindbowser.stripe.entity.Subscriptions;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CardModel;
import com.mindbowser.stripe.model.CustomerModel;
import com.mindbowser.stripe.model.SubscriptionModel;
import com.mindbowser.stripe.repo.CardRepo;
import com.mindbowser.stripe.repo.CoustomerRepo;
import com.mindbowser.stripe.repo.InvoiceRepo;
import com.mindbowser.stripe.repo.SubscriptionRepo;
import com.mindbowser.stripe.service.CustomerService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.Token;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
	private Mapper mapper = new DozerBeanMapper();

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.webhook.key}")
	private String stripeWebhookKey;

	@Autowired
	private Environment environment;

	@Autowired
	private CoustomerRepo coustomerRepo;

	@Autowired
	private SubscriptionRepo subscriptionRepo;

	@Autowired
	private CardRepo cardRepo;

	@Autowired
	private InvoiceRepo invoiceRepo;

	@Override
	public CustomerModel createCoustomer(@Valid CustomerModel model) throws CustomException {
		Customers custmoers = null;
		Optional<Customers> customer = coustomerRepo.findByEmailAndIsDeleted(model.getEmail(), false);
		try {
			if (customer.isPresent()) {
				if (customer.get().getStripeCustomerId() == null) {
					Map<String, Object> customerData = new HashMap<>();
					Map<String, Object> shipping = new HashMap<>();
					Map<String, Object> address = new HashMap<>();

					Stripe.apiKey = stripeApiKey;

					if (!model.getFirstName().isEmpty())
						customerData.put(StaticKey.NAME, model.getFirstName());
					if (!model.getFirstName().isEmpty())
						customerData.put(StaticKey.DESCRIPTION, model.getDescription());
					if (!model.getEmail().isEmpty())
						customerData.put(StaticKey.EMAIL_KEY, model.getEmail());
					if (!model.getPhone().isEmpty())
						customerData.put(StaticKey.PHONE, model.getPhone());
					if (!model.getCity().isEmpty())
						address.put(StaticKey.CITY, model.getCity());
					if (!model.getState().isEmpty())
						address.put(StaticKey.STATE, model.getState());
					if (!model.getCountry().isEmpty())
						address.put(StaticKey.COUNTRY, model.getCountry());
					if (!model.getZipCode().isEmpty())
						address.put(StaticKey.POSTAL_CODE, model.getZipCode());
					if (!model.getAddress().isEmpty()) {
						address.put(StaticKey.LINE_1, model.getAddress());
						address.put(StaticKey.LINE_2, model.getAddress());
					}

					shipping.put(StaticKey.ADDRESS_KEY, address);
					shipping.put(StaticKey.NAME, model.getFirstName());
					customerData.put(StaticKey.ADDRESS_KEY, address);
					customerData.put(StaticKey.SHIPPING, shipping);

					Customer c = Customer.create(customerData);

					customer.get().setStripeCustomerId(c.getId());

					custmoers = coustomerRepo.save(customer.get());

				} else {

					throw new CustomException(environment.getProperty(ExceptionConstant.USER_ALREADY_EXISTS));

				}
				custmoers.setPassword(null);
				return mapper.map(custmoers, CustomerModel.class);

			} else {
				// throw error user not found
				throw new CustomException(environment.getProperty(ExceptionConstant.EXC_USER_NOT_FOUND));

			}
		} catch (StripeException e) {
			log.error("context" + e.getMessage());
			throw new CustomException(e.getMessage());
		} catch (Exception e) {
			log.error("context" + e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));
		}
	}

	@Override
	public Object addCoustomerCard(@Valid CardModel model) throws CustomException {
		Stripe.apiKey = stripeApiKey;
		Map<String, Object> card = new HashMap<>();
		card.put(StaticKey.NUMBER, model.getCardNumber());
		card.put(StaticKey.EXP_MONTH, model.getCardExpMonth());
		card.put(StaticKey.EXP_YEAR, model.getCardExpYear());
		card.put(StaticKey.CVC, model.getCardCvc());
		Map<String, Object> params = new HashMap<>();
		params.put(StaticKey.CARD, card);

		try {
			Token token = Token.create(params);

			Map<String, Object> retrieveParams = new HashMap<>();
			List<String> expandList = new ArrayList<>();
			expandList.add(StaticKey.SOURCES);
			retrieveParams.put(StaticKey.EXPAND, expandList);
			Customer customer = Customer.retrieve(model.getCustomerId(), retrieveParams, null);

			Map<String, Object> cardsParams = new HashMap<>();
			cardsParams.put(StaticKey.SOURCE, token.getId());

			customer.getSources().create(cardsParams);
			Customers c = coustomerRepo.findByStripeCustomerId(model.getCustomerId());

			List<Cards> cardList = new ArrayList<>();
			Cards cardData = new Cards();
			cardData.setCardType(token.getType());
			cardData.setCardDigit(token.getCard().getLast4());
			cardData = cardRepo.save(cardData);
			cardList.add(cardData);
			c.setCards(cardList);
			coustomerRepo.save(c);

		} catch (StripeException e) {
			throw new CustomException(e.getMessage());

		} catch (Exception e) {
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void stripeWebhook(HttpServletRequest request) throws CustomException {
		String sigHeader = request.getHeader(StaticKey.STRIPE_SIGNATURE);
		final ObjectMapper objectMapper = new ObjectMapper();
		String payload = null;
		Event event = null;
		Map<String, Object> data = null;
		Stripe.apiKey = stripeApiKey;
		Invoice invoice = new Invoice();
		try {
			payload = IOUtils.toString(request.getInputStream());
			event = Webhook.constructEvent(payload, sigHeader, stripeWebhookKey);

			data = objectMapper.convertValue(event.getData().getObject(), Map.class);

			switch (event.getType()) {
			case StaticKey.INVOICE_PAYMENT_SUCCEEDED: {

				Map<String, String> linesData = objectMapper.convertValue(data.get(StaticKey.LINES), Map.class);

				List<Map<String, String>> invoiceDataList = objectMapper.convertValue(linesData.get(StaticKey.DATA),
						List.class);
				Map<String, Object> invoiceData = objectMapper.convertValue(invoiceDataList.get(0), Map.class);

				invoice.setQuantity(invoiceData.get(StaticKey.QUANTITY).toString());
				invoice.setInvoiceId(invoiceData.get(StaticKey.ID).toString());

				invoice.setAmount(invoiceData.get(StaticKey.AMOUNT).toString());
				if (data.containsKey(StaticKey.INVOICE_PDF))
					invoice.setInvoicePdf(data.get(StaticKey.INVOICE_PDF).toString());

				if (data.containsKey(StaticKey.CUSTOMER))
					invoice.setCustomerId(data.get(StaticKey.CUSTOMER).toString());
				invoiceRepo.save(invoice);
				break;
			}
			case "invoice.payment_failed": {

				break;

			}

			default:
				log.info("Unhandled event type: " + event.getType());
			}

		} catch (IOException | JsonSyntaxException | SignatureVerificationException e) {
			throw new CustomException(e.getMessage());
		} catch (Exception e) {
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));
		}

	}

	@Override
	public Object getCoustomerCard(Integer currentPage, Integer totalPerPage) throws CustomException {
		Map<String, Object> data = new HashMap<>();

		List<Cards> cards = null;
		try {
			Customers customer = getCurrentLoggedUser();
			if (null != customer) {

				cards = cardRepo.findByIsDeleted(customer.getId(), false, currentPage - 1, totalPerPage);

				if (!cards.isEmpty()) {
					data.put(StaticKey.CARD_LIST, cards);
					data.put(StaticKey.TOTAL_RECORD, cardRepo.countByIdAndIsDeleted(customer.getId(), false));
				} else {
					data.put(StaticKey.CARD_LIST, null);
					data.put(StaticKey.TOTAL_RECORD, 0);
				}
			} else {
				throw new CustomException(environment.getProperty(ExceptionConstant.CURRENT_LOGGED_USER_NOT_FOUND));

			}
		} catch (Exception e) {
			log.error("context" + e);
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));
		}
		return data;
	}

	/**
	 * Get current logged manager
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */
	private Customers getCurrentLoggedUser() throws CustomException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<Customers> customer = coustomerRepo.findByEmailAndIsDeleted(auth.getName(), false);
		if (customer.isPresent()) {
			return customer.get();
		} else {
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_USER_NOT_FOUND));

		}

	}

	@Override
	public Object createSubscription(SubscriptionModel model) throws CustomException {

		Stripe.apiKey = stripeApiKey;
		Subscriptions sub = new Subscriptions();
		List<Object> items = new ArrayList<>();
		Map<String, Object> item1 = new HashMap<>();
		item1.put(StaticKey.PRICE, model.getPriceId());
		items.add(item1);
		Map<String, Object> params = new HashMap<>();
		params.put(StaticKey.CUSTOMER, model.getCustomerId());
		params.put(StaticKey.ITEMS, items);

		try {
			Subscription subscription = Subscription.create(params);

			if (null != subscription) {
				if (null != subscription.getStartDate())
					sub.setSubscriptionStartDate(new Timestamp(subscription.getCurrentPeriodStart() * 1000));
				if (null != subscription.getCurrentPeriodEnd())
					sub.setSubscriptionEndDate(new Timestamp(subscription.getCurrentPeriodEnd() * 1000));
				if (!subscription.getItems().getData().get(0).getPrice().getType().isEmpty())
					sub.setType(subscription.getItems().getData().get(0).getPrice().getType());
				if (!model.getCustomerId().isEmpty())
					sub.setCustomerId(model.getCustomerId());
				if (null != subscription.getItems().getData().get(0).getPrice().getUnitAmount())
					sub.setRate(subscription.getItems().getData().get(0).getPrice().getUnitAmount().toString());
			}
			log.info("Sub:==========:{}", sub);
			subscriptionRepo.save(sub);
		} catch (StripeException e) {
			throw new CustomException(e.getMessage());

		} catch (Exception e) {
			log.error("context" + e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));
		}
		return null;
	}

}
