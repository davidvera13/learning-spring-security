/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package springboot.app.brewery.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import springboot.app.brewery.domain.security.GrantedAuthorityEntity;
import springboot.app.brewery.domain.security.RoleEntity;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.GrantedAuthorityRepository;
import springboot.app.brewery.repositories.security.RoleRepository;
import springboot.app.brewery.repositories.security.UserRepository;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultUserLoader implements CommandLineRunner {

    private final GrantedAuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
        if(authorityRepository.count() == 0)
            loadUserData();
    }

    private void loadUserData() {
        //beer auths
        GrantedAuthorityEntity createBeer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("beer.create").build());
        GrantedAuthorityEntity readBeer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("beer.read").build());
        GrantedAuthorityEntity updateBeer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("beer.update").build());
        GrantedAuthorityEntity deleteBeer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("beer.delete").build());

        //customer auths
        GrantedAuthorityEntity createCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.create").build());
        GrantedAuthorityEntity readCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.read").build());
        GrantedAuthorityEntity updateCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.update").build());
        GrantedAuthorityEntity deleteCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.delete").build());

        //customer brewery
        GrantedAuthorityEntity createBrewery = authorityRepository.save(GrantedAuthorityEntity.builder().permission("brewery.create").build());
        GrantedAuthorityEntity readBrewery = authorityRepository.save(GrantedAuthorityEntity.builder().permission("brewery.read").build());
        GrantedAuthorityEntity updateBrewery = authorityRepository.save(GrantedAuthorityEntity.builder().permission("brewery.update").build());
        GrantedAuthorityEntity deleteBrewery = authorityRepository.save(GrantedAuthorityEntity.builder().permission("brewery.delete").build());


        RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name("ADMIN").build());
        RoleEntity customerRole = roleRepository.save(RoleEntity.builder().name("CUSTOMER").build());
        RoleEntity userRole = roleRepository.save(RoleEntity.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery)));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

//        userRepository.save(UserEntity.builder()
//                .username("spring")
//                .password(passwordEncoder.encode("boot"))
//                .role(adminRole)
//                .build());
//
//        userRepository.save(UserEntity.builder()
//                .username("user")
//                .password(passwordEncoder.encode("password"))
//                .role(userRole)
//                .build());
//
//        userRepository.save(UserEntity.builder()
//                .username("scott")
//                .password(passwordEncoder.encode("tiger"))
//                .role(customerRole)
//                .build());


        UserEntity adminUser = userRepository.save(UserEntity.builder()
                .username("spring")
                .password(passwordEncoder.encode("boot"))
                .role(adminRole)
                .build());

        UserEntity registeredUser = userRepository.save(UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        UserEntity customerUser = userRepository.save(UserEntity.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());
        log.debug("Users created: " + userRepository.count());

        // for test purpose
        System.out.println("Admin authorities");
        adminUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getPermission()));
        System.out.println("User authorities");
        registeredUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getPermission()));
        System.out.println("Customer authorities");
        customerUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getPermission()));

    }

}
