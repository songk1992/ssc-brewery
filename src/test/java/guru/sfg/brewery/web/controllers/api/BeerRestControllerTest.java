package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Nested
class BeerRestControllerTest extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

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
                    .upc(String.valueOf(rand.nextInt(999999999)))
                    .build());
        }

        @Test
        void deleteBeerHttpBasic() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("spring", "kimc")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerHttpBasicUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerHttpBasicCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeer() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-Key", "spring").header("Api-secret", "kimc"))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("List Beers")
    @Nested
    class ListBeers {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersAuth(String user, String pwd) throws Exception {
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
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
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
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByUpcAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

//    @Test
//    void initCreationFormWithAdmin() throws Exception {
//        mockMvc.perform(get("/beers/new").with(httpBasic("spring", "kimc")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//    @Test
//    void initCreationForm() throws Exception {
//        mockMvc.perform(get("/beers/new").with(httpBasic("user", "password")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//    @Test
//    void initCreationFormWithScott() throws Exception {
//        mockMvc.perform(get("/beers/new").with(httpBasic("scott", "tiger")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/createBeer"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//
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
//    void findBeerFormADMIN() throws Exception {
//        mockMvc.perform(get("/beers").param("beerName", "")
//                        .with(httpBasic("spring", "kimc")))
//                .andExpect(status().isOk());
//    }


}