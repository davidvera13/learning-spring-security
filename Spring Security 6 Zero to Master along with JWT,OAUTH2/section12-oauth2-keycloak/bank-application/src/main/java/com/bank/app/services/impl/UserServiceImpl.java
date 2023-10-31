package com.bank.app.services.impl;

import com.bank.app.io.entity.CustomerEntity;
import com.bank.app.io.repository.CustomerRepository;
import com.bank.app.models.response.CustomerResponse;
import com.bank.app.services.UserService;
import com.bank.app.shared.dto.CustomerDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final CustomerRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(CustomerRepository repository) {
        this.repository = repository;
        this.modelMapper = new ModelMapper();
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        List<CustomerEntity> customerEntities = repository.findByEmail(username);
//        if(customerEntities.isEmpty())  // size() = 0
//            throw new UsernameNotFoundException("User details not found for the user: " + username);
//
//        return new User(
//                customerEntities.get(0).getEmail(),
//                customerEntities.get(0).getPwd(),
//                // shorter than creating a list of GrantedAuthority and add SimpleGrantAuthority
//                List.of(new SimpleGrantedAuthority(customerEntities.get(0).getRole())));
//    }

    @Override
    public CustomerDto createUser(CustomerDto customerDto) {
        CustomerEntity customerEntity = modelMapper.map(customerDto, CustomerEntity.class);
        CustomerEntity storedCustomer = repository.save(customerEntity);

        if(storedCustomer.getId() > 0) {
            return modelMapper.map(storedCustomer, CustomerDto.class);
        }
        return null;
    }

    @Override
    public List<CustomerDto> findByEmail(String name) {
        List<CustomerEntity> customers = repository.findByEmail(name);
        return new ModelMapper().map(customers, new TypeToken<List<CustomerDto>>(){}.getType());
    }
}