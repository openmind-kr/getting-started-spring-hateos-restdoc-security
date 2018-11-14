package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.openmind.restapi.testsupport.StableProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void createProductEmptyParameter() throws Exception {
        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProductWrongParameter() throws Exception {
        Product product = StableProduct.builder()
            .quantity(-1)
            .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].rejectedValue").hasJsonPath());
    }

    @Test
    public void createProduct() throws Exception {
        Product product = StableProduct.builder()
            .id(-1) // for ignore
            .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(not(-1))) // for generated value
            .andExpect(jsonPath("saleStatus").hasJsonPath());
    }
}
