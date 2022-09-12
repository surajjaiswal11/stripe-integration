package com.mindbowser.stripe.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindbowser.stripe.entity.Subscriptions;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscriptions, Long> {

}
