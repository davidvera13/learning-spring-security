package springboot.app.brewery.web.controllers.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.app.brewery.domain.Beer;
import springboot.app.brewery.repositories.BeerOrderRepository;
import springboot.app.brewery.repositories.BeerRepository;
import springboot.app.brewery.web.controllers.BaseIntegrationTest;
import springboot.app.brewery.web.model.BeerStyleEnum;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

//@WebMvcTest
@SpringBootTest
public class BeerRestControllerIntegrationTest  extends BaseIntegrationTest {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {

        public Beer beerToDelete() {
            Random rand = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build());
        }

        @Test
        void deleteBeerHttpBasic() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("spring", "boot")))
                    .andExpect(status().is2xxSuccessful());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamNotAdmin")
        void deleteBeerHttpBasicNotAuth(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("List Beers")
    @Nested
    class ListBeers {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer/"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void findBeersAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beer/").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By ID")
    @Nested
    class GetBeerById {
        @Test
        void findBeerById() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void findBeerByIdAUTH(String user, String pwd) throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Find By UPC")
    class FindByUPC {
        @Test
        void findBeerByUpc() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("springboot.app.brewery.web.controllers.BeerControllerIntegrationTest#getStreamAllUsers")
        void findBeerByUpcAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }
//    @Autowired
//    BeerRepository beerRepository;
//
//    @Autowired
//    BeerOrderRepository beerOrderRepository;
//
//    @Test
//    void deleteBeer() throws Exception {
//        mockMvc.perform(
//                    delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                        .header("Api-Key", "john") // username
//                        .header("Api-Secret", "wick") // password
//                )
//                .andExpect(status().isOk());
//    }
//    @Test
//    void deleteBeerUrl() throws Exception {
//        mockMvc.perform(
//                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                                .param("Api-Key", "john") // username
//                                .param("Api-Secret", "wick") // password
//                )
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void deleteBeerBadCreds() throws Exception {
//        mockMvc.perform(
//                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                                .header("Api-Key", "admin") // username
//                                .header("Api-Secret", "notAdminPassword") // password
//                )
//                .andExpect(status().isUnauthorized());
//    }
//    @Test
//    void deleteBeerBadCredsUrl() throws Exception {
//        mockMvc.perform(
//                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                                .param("Api-Key", "admin") // username
//                                .param("Api-Secret", "notAdminPassword") // password
//                )
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void deleteBeerHttpBasic() throws Exception{
//        Beer beer = beerRepository.findAll().get(0);
//        mockMvc.perform(delete("/api/v1/beer/" + beer.getId())
//                        .with(httpBasic("john", "wick")))
//                .andExpect(status().is2xxSuccessful());
//    }
//    @Test
//    void deleteBeerHttpBasicUserRole() throws Exception{
//        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                        .with(httpBasic("fox", "mulder")))
//                .andExpect(status().isForbidden());
//    }
//    @Test
//    void deleteBeerHttpBasicCustomerRole() throws Exception{
//        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                        .with(httpBasic("scott", "tiger")))
//                // .andExpect(status().isUnauthorized());
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void listBreweriesCUSTOMER() throws Exception {
//        mockMvc.perform(get("/brewery/breweries")
//                        .with(httpBasic("scott", "tiger")))
//                .andExpect(status().is2xxSuccessful());
//    }
//    @Test
//    void listBreweriesADMIN() throws Exception {
//        mockMvc.perform(get("/brewery/breweries")
//                        .with(httpBasic("john", "wick")))
//                .andExpect(status().is2xxSuccessful());
//    }
//    @Test
//    void listBreweriesUSER() throws Exception {
//        mockMvc.perform(get("/brewery/breweries")
//                        .with(httpBasic("fox", "mulder")))
//                .andExpect(status().isForbidden());
//    }
//    @Test
//    void getBreweriesJsonCUSTOMER() throws Exception {
//        mockMvc.perform(get("/brewery/api/v1/breweries")
//                        .with(httpBasic("scott", "tiger")))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    void getBreweriesJsonADMIN() throws Exception {
//        mockMvc.perform(get("/brewery/api/v1/breweries")
//                        .with(httpBasic("john", "wick")))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    void getBreweriesJsonUSER() throws Exception {
//        mockMvc.perform(get("/brewery/api/v1/breweries")
//                        .with(httpBasic("fox", "mulder")))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void deleteBeerNoAuth() throws Exception{
//        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    // these tests will return 401 instead of 200, we need to update SecurityConfig class
//    // java.lang.AssertionError: Status expected:<200> but was:<401>
//    @Test
//    void findBeers() throws Exception {
//        mockMvc.perform(get("/api/v1/beer/"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void findBeerById() throws Exception {
//        Beer beer = beerRepository.findAll().get(0);
//
//        mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void findBeerByUpc() throws Exception {
//        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void findBeerFormADMIN() throws Exception {
//        mockMvc.perform(get("/beers").param("beerName", "")
//                        .with(httpBasic("john", "wick")))
//                .andExpect(status().isOk());
//    }

}
