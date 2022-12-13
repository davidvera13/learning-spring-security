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
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.GrantedAuthorityRepository;
import springboot.app.brewery.repositories.security.UserRepository;


@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultUserLoader implements CommandLineRunner {

    private final GrantedAuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if(authorityRepository.count() == 0)
            loadUserData();
    }

    private void loadUserData() {
        // creating authorities
        GrantedAuthorityEntity admin = authorityRepository.save(GrantedAuthorityEntity.builder().role("ROLE_ADMIN").build());
        GrantedAuthorityEntity userRole = authorityRepository.save(GrantedAuthorityEntity.builder().role("ROLE_USER").build());
        GrantedAuthorityEntity customer = authorityRepository.save(GrantedAuthorityEntity.builder().role("ROLE_CUSTOMER").build());

//        tream.of(Arguments.of("spring" , "boot"),
//                Arguments.of("scott", "tiger"),
//                Arguments.of("user", "password"));
//
        userRepository.save(UserEntity.builder()
                .username("spring")
                .password(passwordEncoder.encode("boot"))
                .authority(admin)
                .build());

        userRepository.save(UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authority(userRole)
                .build());

        userRepository.save(UserEntity.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .authority(customer)
                .build());
        log.debug("Users created: " + userRepository.count());
    }

}
