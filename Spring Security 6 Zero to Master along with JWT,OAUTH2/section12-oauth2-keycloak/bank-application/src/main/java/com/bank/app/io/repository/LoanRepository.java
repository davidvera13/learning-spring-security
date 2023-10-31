package com.bank.app.io.repository;

import java.util.List;

import com.bank.app.io.entity.LoanEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;


@Repository
public interface LoanRepository extends CrudRepository<LoanEntity, Long> {
	//@PreAuthorize("hasRole('USER')")
	List<LoanEntity> findByCustomerIdOrderByStartDtDesc(long customerId);

}
