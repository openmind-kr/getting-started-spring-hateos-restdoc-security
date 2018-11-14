package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
            .quantity(-1)
            .build();

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].rejectedValue").hasJsonPath());
    }

    @Test
    public void createProduct() throws Exception {
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
            .name("anyName")
            .description("anyDescription")
            .beginSaleDateTime(ZonedDateTime.of(LocalDateTime.of(2001, 1, 1, 0, 0), ZoneId.systemDefault()))
            .closeSaleDateTime(ZonedDateTime.of(LocalDateTime.of(9999, 12, 31, 23, 59, 59), ZoneId.systemDefault()))
            .salePrice(2_000_000L)
            .quantity(30_000)
            .build();

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(not(-1))) // for generated value
            .andExpect(jsonPath("saleStatus").hasJsonPath());
    }
}
