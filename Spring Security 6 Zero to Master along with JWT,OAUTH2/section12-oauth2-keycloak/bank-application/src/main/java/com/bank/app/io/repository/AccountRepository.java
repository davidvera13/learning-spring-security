package com.bank.app.io.repository;

import com.bank.app.io.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
	AccountEntity findByCustomerId(long customerId);
}
