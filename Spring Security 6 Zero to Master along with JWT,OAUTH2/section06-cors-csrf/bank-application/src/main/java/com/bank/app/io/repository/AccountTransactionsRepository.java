package com.bank.app.io.repository;

import java.util.List;

import com.bank.app.io.entity.AccountTransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AccountTransactionsRepository extends CrudRepository<AccountTransactionEntity, Long> {
	
	List<AccountTransactionEntity> findByCustomerIdOrderByTransactionDtDesc(long customerId);

}
