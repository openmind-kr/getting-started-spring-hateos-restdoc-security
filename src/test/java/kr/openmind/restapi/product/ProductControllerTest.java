package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.openmind.restapi.testsupport.TestRestControllerConfig;
import kr.openmind.restapi.vendor.VendorRoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@TestRestControllerConfig
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
            .andExpect(jsonPath("$.content[0].rejectedValue").hasJsonPath())
            .andExpect(jsonPath("_links").hasJsonPath());
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
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("id").value(not(-1))) // for generated value
            .andExpect(jsonPath("saleStatus").hasJsonPath())
            .andExpect(jsonPath("_links").hasJsonPath())
            .andExpect(jsonPath("_links.self").hasJsonPath())
            .andExpect(jsonPath("_links.product").hasJsonPath())
            .andExpect(jsonPath("_links.update").hasJsonPath())
            .andExpect(jsonPath("_links.profile").hasJsonPath())
            .andDo(document("product-post",
                            links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("product").description("link to product"),
                                linkWithRel("update").description("link to update"),
                                linkWithRel("profile").description("link to profile")
                            ),
                            requestFields(
                                fieldWithPath("name").description("name of the product"),
                                fieldWithPath("description").description("description of the product"),
                                fieldWithPath("beginSaleDateTime").type(ZonedDateTime.class).description("on sale date"),
                                fieldWithPath("closeSaleDateTime").type(ZonedDateTime.class).description("the end date of sale"),
                                fieldWithPath("salePrice").description("sale price"),
                                fieldWithPath("quantity").description("available quantity"),
                                fieldWithPath("vendorRoleType")
                                    .description("vendor role type. must be one of " + Arrays.toString(VendorRoleType.values())),
                                fieldWithPath("shippingFee").description("shipping fee (optional)")
                            ),
                            relaxedResponseFields(
                                fieldWithPath("id").description("identifier of the product"),
                                fieldWithPath("name").description("name of the product"),
                                fieldWithPath("description").description("description of the product"),
                                fieldWithPath("beginSaleDateTime").type(ZonedDateTime.class).description("on sale date"),
                                fieldWithPath("closeSaleDateTime").type(ZonedDateTime.class).description("the end date of sale"),
                                fieldWithPath("salePrice").description("sale price"),
                                fieldWithPath("quantity").description("available quantity"),
                                fieldWithPath("vendorRoleType").description("vendor role type"),
                                fieldWithPath("shippingFee").description("shipping fee"),
                                fieldWithPath("saleStatus")
                                    .description("sale status. must be one of " + Arrays.toString(ProductSaleStatusType.values()))
                            )
            ));
    }
}
