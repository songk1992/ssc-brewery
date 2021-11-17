package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerOrderControllerTest extends BaseIT {
    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer example1customer;
    Customer example2customer;
    Customer example3customer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        example1customer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE1_USER).orElseThrow();
        example2customer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE2_USER).orElseThrow();
        example3customer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE3_USER).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @DisplayName("Create Test")
    @Nested
    class creatOrderedTest {

        @WithUserDetails("spring")
        @Test
        void createOrderNotAuth() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(example1customer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + example1customer.getId() + "/orders")
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isUnauthorized());
        }

        @WithUserDetails(DefaultBreweryLoader.EXAMPLE2_USER)
        @Test
        void createOrderUserAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(example1customer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + example1customer.getId() + "/orders")
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }

        @WithUserDetails(DefaultBreweryLoader.EXAMPLE3_USER)
        @Test
        void createOrderUserNOTAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(example1customer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + example1customer.getId() + "/orders")
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + example2customer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + example2customer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + example2customer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.EXAMPLE3_USER)
    @Test
    void listOrdersCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + example2customer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + example2customer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Disabled
    @Test
    void pickUpOrderNotAuth() {
    }

    @Disabled
    @Test
    void pickUpOrderNotAdminUser() {
    }

    @Disabled
    @Test
    void pickUpOrderCustomerUserAUTH() {
    }

    @Disabled
    @Test
    void pickUpOrderCustomerUserNOT_AUTH() {
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }

}
