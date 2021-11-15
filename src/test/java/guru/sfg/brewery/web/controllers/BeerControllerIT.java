package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

//    // 방법 1
//    // 유저이름이 달라도 테스트는 PASS 하기 때문에 아래 방법 2로 진행
//    // @WithMockUser("spring")
//    @Test
//    void findBeers() throws Exception {
//        mockMvc.perform(get("/beers/find"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//    // 방법 2
//    @Test
//    void findBeersWithHttpBasic() throws Exception {
//        //mockMvc.perform(get("/beers/find").with(httpBasic("spring", "kimc")))
//        mockMvc.perform(get("/beers/find").with(anonymous()))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }


    @Test
    void findBeersWithHttpBasic() throws Exception {
        mockMvc.perform(get("/beers/find").with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

//    @Test
//    void findBeerADMIN() throws Exception {
//        mockMvc.perform(get("/beers/find").param("beerName", "")
//                        .with(httpBasic("spring", "kimc")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//    @Test
//    void findBeerUSER() throws Exception {
//        mockMvc.perform(get("/beers/find").param("beerName", "")
//                        .with(httpBasic("user", "password")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }
//
//    @Test
//    void findBeerCUSTOMER() throws Exception {
//        mockMvc.perform(get("/beers/find").param("beerName", "")
//                        .with(httpBasic("scott", "tiger")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
    void findBeerFormAuth(String user, String pwd) throws Exception{
        mockMvc.perform(get("/beers").param("beerName", "")
                        .with(httpBasic(user, pwd)))
                .andExpect(status().isOk());
    }



}
