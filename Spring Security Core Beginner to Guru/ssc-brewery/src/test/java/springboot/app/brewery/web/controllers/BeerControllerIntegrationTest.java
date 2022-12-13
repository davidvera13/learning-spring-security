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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.app.brewery.domain.Beer;
import springboot.app.brewery.repositories.BeerRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@SpringBootTest
class BeerControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm{

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void initCreationFormAuth(String user, String pwd) throws Exception {

            mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }


    @DisplayName("Init Find Beer Form")
    @Nested
    class FindForm{
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void findBeersFormAUTH(String user, String pwd) throws Exception{
            mockMvc.perform(get("/beers/find")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersWithAnonymous() throws Exception{
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindForm{
        @Test
        void findBeerForm() throws Exception {
            mockMvc.perform(get("/beers").param("beerName", ""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void findBeerFormAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By Id")
    @Nested
    class GetByID {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void getBeerByIdAUTH(String user, String pwd) throws Exception{
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void getBeerByIdNoAuth() throws Exception{
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }


//    @Test
//    void initCreationForm() throws Exception {
//        mockMvc.perform(get("/beers/new")
//                        .with(httpBasic("fox", "mulder")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//    @Test
//    void initAdminCreationForm() throws Exception {
//        mockMvc.perform(get("/beers/new")
//                        .with(httpBasic("john", "wick")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//    @Test
//    void initCustomerCreationForm() throws Exception {
//        mockMvc.perform(get("/beers/new").with(httpBasic("scott", "tiger")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//
//    @Test
//    @WithMockUser("anyUserValue")
//    void findBeers() throws Exception {
//        // fake a call to controller
//        mockMvc.perform(get("/beers/find"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
////        verifyNoInteractions(beerRepository);
//    }
//
////    @Test
////    // @WithMockUser("root")
////    void findBeersWithHttpBasic() throws Exception {
////        // fake a call to controller
////        mockMvc.perform(
////                    get("/beers/find").with(httpBasic("root", "root")))
////                .andExpect(status().isOk())
////                .andExpect(view().name("beers/findBeers"))
////                .andExpect(model().attributeExists("beer"));
////        verifyNoInteractions(beerRepository);
////    }
}