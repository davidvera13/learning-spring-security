package springboot.app.brewery.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springboot.app.brewery.repositories.BeerInventoryRepository;
import springboot.app.brewery.repositories.BeerRepository;
import springboot.app.brewery.repositories.CustomerRepository;
import springboot.app.brewery.services.BeerService;
import springboot.app.brewery.services.BreweryService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseIntegrationTest {
    @Autowired
    public WebApplicationContext wac;

    public MockMvc mockMvc;

    @MockBean
    public BeerRepository beerRepository;

    @MockBean
    public BeerInventoryRepository beerInventoryRepository;

    @MockBean
    public BreweryService breweryService;

    @MockBean
    public CustomerRepository customerRepository;

    @MockBean
    public BeerService beerService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }
}

