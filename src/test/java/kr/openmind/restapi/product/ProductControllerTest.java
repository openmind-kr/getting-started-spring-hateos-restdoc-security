package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createProductEmptyParameter() throws Exception {
        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProductWrongParameter() throws Exception {
        Product product = Product.builder()
            .name("anyName")
            .quantity(-1)
            .salePrice(2_000_000L)
            .quantity(30_000)
            .build();

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].rejectedValue").hasJsonPath());
    }

    @Test
    public void createProduct() throws Exception {
        Product product = Product.builder()
            .name("anyName")
            .beginSaleDateTime(ZonedDateTime.of(LocalDateTime.of(2018, 1, 1, 0, 0), ZoneId.systemDefault()))
            .closeSaleDateTime(ZonedDateTime.of(LocalDateTime.of(2999, 12, 31, 23, 59), ZoneId.systemDefault()))
            .salePrice(2_000_000L)
            .quantity(30_000)
            .build();

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").hasJsonPath())
            .andExpect(jsonPath("saleStatus").hasJsonPath());
    }
}
