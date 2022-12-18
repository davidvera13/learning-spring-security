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

package springboot.app.brewery.web.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import springboot.app.brewery.domain.Customer;
import springboot.app.brewery.repositories.CustomerRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ExtendWith(MockitoExtension.class)
@SpringBootTest
class CustomerControllerTest extends BaseIntegrationTest {

    @DisplayName("List Customers")
    @Nested
    class ListCustomers{
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAdminCustomer")
        void testListCustomerAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/customers")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

        @Test
        void testListCustomerNotAuth() throws Exception {
            mockMvc.perform(get("/customers")
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());;
        }

        @Test
        void testListCustomerNotLoggedIn() throws Exception {
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isUnauthorized());;
        }
    }

    @DisplayName("Add Customers")
    @Nested
    class AddCustomers {

        @Rollback
        @Test
        void processCreationForm() throws Exception{
            mockMvc.perform(post("/customers/new")
                            .with(csrf())
                            .param("customerName", "Foo Customer")
                            .with(httpBasic("spring", "boot")))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamNotAdmin")
        void processCreationFormNOTAUTH(String user, String pwd) throws Exception{
            mockMvc.perform(post("/customers/new")
                            .param("customerName", "Foo Customer2")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNOAUTH() throws Exception{
            mockMvc.perform(post("/customers/new")
                            .with(csrf())
                            .param("customerName", "Foo Customer"))
                    .andExpect(status().isUnauthorized());
        }
    }



//    @Mock
//    CustomerRepository customerRepository;
//    @InjectMocks
//    CustomerController controller;
//    List<Customer> customerList;
//    UUID uuid;
//    Customer customer;
//
//    MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        customerList = new ArrayList<Customer>();
//        customerList.add(Customer.builder().customerName("John Doe").build());
//        customerList.add(Customer.builder().customerName("John Doe").build());
//
//        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
//        uuid = UUID.fromString(id);
//
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(controller)
//                .build();
//    }
//
//    @Test
//    void findCustomers() throws Exception{
//        mockMvc.perform(get("/customers/find"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("customers/findCustomers"))
//                .andExpect(model().attributeExists("customer"));
//        verifyZeroInteractions(customerRepository);
//    }
////ToDO: Fix stubbing error
//    @Test
//    @Disabled
//    void processFindFormReturnMany() throws Exception{
//        when(customerRepository.findAllByCustomerNameLike("John Doe")).thenReturn(customerList);
//
//        mockMvc.perform(get("/customers"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("customers/customerList"))
//                .andExpect(model().attribute("selections", hasSize(2)));
//    }
//
//    @Test
//    void showCustomer() throws Exception{
//        when(customerRepository.findById(uuid)).thenReturn(Optional.of(Customer.builder().id(uuid).build()));
//        mockMvc.perform(get("/customers/"+uuid))
//                .andExpect(status().isOk())
//                .andExpect(view().name("customers/customerDetails"))
//                .andExpect(model().attribute("customer", hasProperty("id", is(uuid))));
//    }
//
//    @Test
//    void initCreationForm() throws Exception{
//        mockMvc.perform(get("/customers/new"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("customers/createCustomer"))
//                .andExpect(model().attributeExists("customer"));
//        verifyZeroInteractions(customerRepository);
//    }
//
//    @Test
//    void processCreationForm() throws Exception{
//        when(customerRepository.save(ArgumentMatchers.any())).thenReturn(Customer.builder().id(uuid).build());
//        mockMvc.perform(post("/customers/new"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/customers/"+ uuid))
//                .andExpect(model().attributeExists("customer"));
//        verify(customerRepository).save(ArgumentMatchers.any());
//    }
//
//    @Test
//    void initUpdateCustomerForm() throws Exception{
//        when(customerRepository.findById(uuid)).thenReturn(Optional.of(Customer.builder().id(uuid).build()));
//        mockMvc.perform(get("/customers/"+uuid+"/edit"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("customers/createOrUpdateCustomer"))
//                .andExpect(model().attributeExists("customer"));
//        verifyZeroInteractions(customerRepository);
//    }
//
//    @Test
//    void processUpdationForm() throws Exception{
//        when(customerRepository.save(ArgumentMatchers.any())).thenReturn(Customer.builder().id(uuid).build());
//
//        mockMvc.perform(post("/customers/"+uuid+"/edit"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/customers/"+uuid))
//                .andExpect(model().attributeExists("customer"));
//
//        verify(customerRepository).save(ArgumentMatchers.any());
//    }
}