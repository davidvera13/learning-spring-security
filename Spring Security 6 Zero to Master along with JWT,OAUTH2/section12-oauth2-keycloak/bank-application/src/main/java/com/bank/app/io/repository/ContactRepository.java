package com.bank.app.io.repository;

import com.bank.app.io.entity.ContactEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRepository extends CrudRepository<ContactEntity, Long> {
	
	
}
