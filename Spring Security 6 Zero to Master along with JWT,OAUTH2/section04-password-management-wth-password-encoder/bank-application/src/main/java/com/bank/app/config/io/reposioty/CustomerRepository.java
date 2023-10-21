package com.bank.app.config.io.reposioty;

import com.bank.app.config.io.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {
    // abstact method: derived method name query
    List<CustomerEntity> findByEmail(String email);
}
