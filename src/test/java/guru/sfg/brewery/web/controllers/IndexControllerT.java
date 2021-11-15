package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class IndexControllerT extends BaseIT {

    @Test
    void testGetIndexSlash() throws Exception {
        mockMvc.perform(get("/", "/webjars/**"))
                .andExpect(status().isOk());
    }
}
