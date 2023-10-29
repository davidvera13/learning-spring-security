package com.bank.app.io.repository;

import java.util.List;

import com.bank.app.io.entity.LoanEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoanRepository extends CrudRepository<LoanEntity, Long> {
	
	List<LoanEntity> findByCustomerIdOrderByStartDtDesc(long customerId);

}
