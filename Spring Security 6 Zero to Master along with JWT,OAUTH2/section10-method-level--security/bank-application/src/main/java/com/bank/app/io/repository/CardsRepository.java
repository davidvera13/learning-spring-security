package com.bank.app.io.repository;

import java.util.List;

import com.bank.app.io.entity.CardEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CardsRepository extends CrudRepository<CardEntity, Long> {
	
	List<CardEntity> findByCustomerId(long customerId);

}
