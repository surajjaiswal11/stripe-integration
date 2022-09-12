package com.mindbowser.stripe.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mindbowser.stripe.constant.QueryConstant;
import com.mindbowser.stripe.entity.Cards;

@Repository
public interface CardRepo extends JpaRepository<Cards, Long> {

	@Query(value = QueryConstant.SELECT + QueryConstant.STAR + QueryConstant.FROM
			+ QueryConstant.IS_DELETED_AND_ADDED_BY_AND_ORDER_BY_ID_DESC + QueryConstant.LIMIT, nativeQuery = true)
	List<Cards> findByIsDeleted(Long id, boolean b, int i, Integer totalPerPage);

	@Query(value = QueryConstant.SELECT + QueryConstant.COUNT + QueryConstant.FROM
			+ QueryConstant.IS_DELETED_AND_ADDED_BY_AND_ORDER_BY_ID_DESC, nativeQuery = true)
	Long countByIdAndIsDeleted(Long id, boolean b);

}
