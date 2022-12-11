package springboot.app.brewery.web.controllers.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.app.brewery.web.controllers.BaseIntegrationTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

//@WebMvcTest
@SpringBootTest
public class BeerRestControllerIntegrationTest  extends BaseIntegrationTest {

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(
                    delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                        .header("Api-Key", "john") // username
                        .header("Api-Secret", "wick") // password
                )
                .andExpect(status().isOk());
    }
    @Test
    void deleteBeerUrl() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                                .param("Api-Key", "john") // username
                                .param("Api-Secret", "wick") // password
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                                .header("Api-Key", "admin") // username
                                .header("Api-Secret", "notAdminPassword") // password
                )
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteBeerBadCredsUrl() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                                .param("Api-Key", "admin") // username
                                .param("Api-Secret", "notAdminPassword") // password
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                        .with(httpBasic("john", "wick")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerNoAuth() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isUnauthorized());
    }

    // these tests will return 401 instead of 200, we need to update SecurityConfig class
    // java.lang.AssertionError: Status expected:<200> but was:<401>
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }

}
