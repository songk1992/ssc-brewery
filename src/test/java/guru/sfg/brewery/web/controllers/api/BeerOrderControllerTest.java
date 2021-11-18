package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    WebApplicationContext wac;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE1_DISTRIBUTING).orElseThrow();//ST_PETE_DISTRIBUTING
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE2_DISTRIBUTING).orElseThrow();//DUNEDIN_DISTRIBUTING
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.EXAMPLE3_DISTRIBUTING).orElseThrow();//KEY_WEST_DISTRIBUTORS
        loadedBeers = beerRepository.findAll();
    }

//cant use nested tests bug - https://github.com/spring-projects/spring-security/issues/8793
//    @DisplayName("Create Test")
//    @Nested
//    class createOrderTests {


    @Test
    void createOrderNotAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.EXAMPLE1_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.EXAMPLE3_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

    // }
    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.EXAMPLE1_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void listOrdersCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE1_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void pickUpOrderNotAuth() throws Exception{
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void pickUpOrderNotAdminUser() throws Exception{
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void pickUpOrderAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE1_USER)
    @Test
    void pickUpOrderCustomerUserAUTH() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.EXAMPLE2_USER)
    @Test
    void pickUpOrderCustomerUserNOT_AUTH() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
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