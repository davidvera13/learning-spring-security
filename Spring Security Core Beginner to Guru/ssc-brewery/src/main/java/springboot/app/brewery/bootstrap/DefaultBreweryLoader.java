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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.app.brewery.domain.*;
import springboot.app.brewery.domain.security.GrantedAuthorityEntity;
import springboot.app.brewery.domain.security.RoleEntity;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.*;
import springboot.app.brewery.repositories.security.GrantedAuthorityRepository;
import springboot.app.brewery.repositories.security.RoleRepository;
import springboot.app.brewery.repositories.security.UserRepository;
import springboot.app.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";
    public static final String STPETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";
    public static final String KEYWEST_USER = "keywest";

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final GrantedAuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
        loadSecurityData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();
    }

    private void loadCustomerData() {
        RoleEntity customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        //create customers
        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer keyWestCustomer = customerRepository.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTORS)
                .apiKey(UUID.randomUUID())
                .build());

        //create users
        UserEntity stPeteUserEntity = userRepository.save(UserEntity.builder().username(STPETE_USER)
                .password(passwordEncoder.encode("password"))
                .customer(stPeteCustomer)
                .role(customerRole).build());

        UserEntity dunedinUser = userRepository.save(UserEntity.builder().username(DUNEDIN_USER)
                .password(passwordEncoder.encode("password"))
                .customer(dunedinCustomer)
                .role(customerRole).build());

        UserEntity keywest = userRepository.save(UserEntity.builder().username(KEYWEST_USER)
                .password(passwordEncoder.encode("password"))
                .customer(keyWestCustomer)
                .role(customerRole).build());

        //create orders
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);

        log.debug("Orders Loaded: " + beerOrderRepository.count());
    }

    private BeerOrder createOrder(Customer customer) {
        return  beerOrderRepository.save(BeerOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beerRepository.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
    }


    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }

    private void loadSecurityData() {
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

        //beer order
        GrantedAuthorityEntity createOrder = authorityRepository.save(GrantedAuthorityEntity.builder().permission("order.create").build());
        GrantedAuthorityEntity readOrder = authorityRepository.save(GrantedAuthorityEntity.builder().permission("order.read").build());
        GrantedAuthorityEntity updateOrder = authorityRepository.save(GrantedAuthorityEntity.builder().permission("order.update").build());
        GrantedAuthorityEntity deleteOrder = authorityRepository.save(GrantedAuthorityEntity.builder().permission("order.delete").build());
        GrantedAuthorityEntity pickupOrder = authorityRepository.save(GrantedAuthorityEntity.builder().permission("order.pickup").build());

        GrantedAuthorityEntity createOrderCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.order.create").build());
        GrantedAuthorityEntity readOrderCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.order.read").build());
        GrantedAuthorityEntity updateOrderCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.order.update").build());
        GrantedAuthorityEntity deleteOrderCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.order.delete").build());
        GrantedAuthorityEntity pickupOrderCustomer = authorityRepository.save(GrantedAuthorityEntity.builder().permission("customer.order.pickup").build());


        RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name("ADMIN").build());
        RoleEntity customerRole = roleRepository.save(RoleEntity.builder().name("CUSTOMER").build());
        RoleEntity userRole = roleRepository.save(RoleEntity.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder, pickupOrder)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery, createOrderCustomer, readOrderCustomer,
                updateOrderCustomer, deleteOrderCustomer, pickupOrderCustomer)));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

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
        adminUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));
        System.out.println("User authorities");
        registeredUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));
        System.out.println("Customer authorities");
        customerUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));

        log.debug("Users Loaded: " + userRepository.count());
    }

}
//    public void loadUserData() {
//        //beer auths
//        GrantedAuthorityEntity createBeer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("beer.create").build());
//        GrantedAuthorityEntity readBeer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("beer.read").build());
//        GrantedAuthorityEntity updateBeer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("beer.update").build());
//        GrantedAuthorityEntity deleteBeer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("beer.delete").build());
//
//        //customer auths
//        GrantedAuthorityEntity createCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.create").build());
//        GrantedAuthorityEntity readCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.read").build());
//        GrantedAuthorityEntity updateCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.update").build());
//        GrantedAuthorityEntity deleteCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.delete").build());
//
//        //customer brewery
//        GrantedAuthorityEntity createBrewery = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("brewery.create").build());
//        GrantedAuthorityEntity readBrewery = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("brewery.read").build());
//        GrantedAuthorityEntity updateBrewery = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("brewery.update").build());
//        GrantedAuthorityEntity deleteBrewery = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("brewery.delete").build());
//
//
//        //beer order
//        GrantedAuthorityEntity createOrder = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("order.create").build());
//        GrantedAuthorityEntity readOrder = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("order.read").build());
//        GrantedAuthorityEntity updateOrder = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("order.update").build());
//        GrantedAuthorityEntity deleteOrder = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("order.delete").build());
//
//        GrantedAuthorityEntity createOrderCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.order.create").build());
//        GrantedAuthorityEntity readOrderCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.order.read").build());
//        GrantedAuthorityEntity updateOrderCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.order.update").build());
//        GrantedAuthorityEntity deleteOrderCustomer = authorityRepository
//                .save(GrantedAuthorityEntity.builder().permission("customer.order.delete").build());
//
//
//        RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name("ADMIN").build());
//        RoleEntity customerRole = roleRepository.save(RoleEntity.builder().name("CUSTOMER").build());
//        RoleEntity userRole = roleRepository.save(RoleEntity.builder().name("USER").build());
//
//        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer,
//                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery,
//                createOrder, readOrder, updateOrder, deleteOrder)));
//
//        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery, createOrderCustomer,
//                readOrderCustomer, updateOrderCustomer, deleteOrderCustomer)));
//
//        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));
//
//        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));
//
//        UserEntity adminUser = userRepository.save(UserEntity.builder()
//                .username("spring")
//                .password(passwordEncoder.encode("boot"))
//                .role(adminRole)
//                .build());
//
//        UserEntity registeredUser = userRepository.save(UserEntity.builder()
//                .username("user")
//                .password(passwordEncoder.encode("password"))
//                .role(userRole)
//                .build());
//
//        UserEntity customerUser = userRepository.save(UserEntity.builder()
//                .username("scott")
//                .password(passwordEncoder.encode("tiger"))
//                .role(customerRole)
//                .build());
//        log.debug("Users created: " + userRepository.count());
//
//        // for test purpose
//        System.out.println("Admin authorities");
//        adminUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));
//        System.out.println("User authorities");
//        registeredUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));
//        System.out.println("Customer authorities");
//        customerUser.getAuthorities().forEach(authority -> System.out.println("\t" + authority.getAuthority()));
//    }
//
//}
