package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.openmind.restapi.testsupport.SecurityTestSupport;
import kr.openmind.restapi.testsupport.StableProduct;
import kr.openmind.restapi.testsupport.TestRestControllerConfig;
import kr.openmind.restapi.vendor.VendorRoleType;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@TestRestControllerConfig
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SecurityTestSupport securityTestSupport;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void createProductEmptyParameter() throws Exception {
        mockMvc.perform(post("/api/product")
                            .header(HttpHeaders.AUTHORIZATION, securityTestSupport.getBearerAccessToken())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProductWrongParameter() throws Exception {
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
            .quantity(-1)
            .build();

        mockMvc.perform(post("/api/product")
                            .header(HttpHeaders.AUTHORIZATION, securityTestSupport.getBearerAccessToken())
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
                            .header(HttpHeaders.AUTHORIZATION, securityTestSupport.getBearerAccessToken())
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
                            requestFields(productRequestFields()),
                            responseFields(productResponseFields("product-post_links"))
            ));
    }

    @Test
    public void getProductFail() throws Exception {
        Integer notExistsId = -1004;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/product/{id}", notExistsId))
            .andExpect(status().isNotFound())
            .andDo(document("product-get-fail",
                            pathParameters(
                                parameterWithName("id").description("identifier of a product.")
                            )
            ));
    }

    @Test
    public void getProduct() throws Exception {
        Product anySavedProduct = saveProduct(Integer.MIN_VALUE); // any index

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/product/{id}", anySavedProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.self").hasJsonPath())
            .andExpect(jsonPath("_links.update").hasJsonPath())
            .andDo(document("product-get",
                            links(
                                linkWithRel("self").description("link to this product"),
                                linkWithRel("products").description("link to product list"),
                                linkWithRel("update").description("link to update"),
                                linkWithRel("profile").description("link to profile")
                            ),
                            pathParameters(
                                parameterWithName("id").description("identifier of a product.")
                            ),
                            responseFields(productResponseFields("product-get_links"))
            ));
    }

    @Test
    public void getProducts() throws Exception {
        IntStream.range(0, 30).forEach(this::saveProduct);

        mockMvc.perform(get("/api/product")
                            .param("size", "10")
                            .param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links").hasJsonPath())
            .andDo(document("product-get-list",
                            links(
                                linkWithRel("self").description("link to product list"),
                                linkWithRel("product").description("link to a product"),
                                linkWithRel("product-create").description("link to a product create"),
                                linkWithRel("first").description("link to product list first page"),
                                linkWithRel("prev").description("link to product list previous page"),
                                linkWithRel("next").description("link to product list next page"),
                                linkWithRel("last").description("link to product list last page"),
                                linkWithRel("profile").description("link to profile")
                            ),
                            requestParameters(
                                parameterWithName("page").description("page to retrieve, begin with and default is 0").optional(),
                                parameterWithName("size").description("size of the page to retrieve, default 10").optional()
                            ),
                            responseFields(
                                subsectionWithPath("_links").description("<<resources-product-get-list_links, Links>> to other links"),
                                subsectionWithPath("_embedded.productList")
                                    .description("An array of <<resources-product, product resources>>"),
                                fieldWithPath("page.number").description("The number of this page."),
                                fieldWithPath("page.size").description("The size of this page."),
                                fieldWithPath("page.totalPages").description("The total number of pages."),
                                fieldWithPath("page.totalElements").description("The total number of results.")
                            )
            ));
    }

    @Test
    public void updateProduct() throws Exception {
        Product anySavedProduct = saveProduct(Integer.MAX_VALUE); // any index

        String modifiedName = RandomString.make(10);
        int modifiedQuantity = anySavedProduct.getQuantity() + 100;
        Product product = StableProduct.builder()
            .name(modifiedName)
            .quantity(modifiedQuantity)
            .build();

        ProductRequestDto productRequestDto = modelMapper.map(product, ProductRequestDto.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/product/{id}", anySavedProduct.getId())
                            .header(HttpHeaders.AUTHORIZATION, securityTestSupport.getBearerAccessToken())
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .content(objectMapper.writeValueAsString(productRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value(anySavedProduct.getId()))
            .andExpect(jsonPath("name").value(modifiedName))
            .andExpect(jsonPath("quantity").value(modifiedQuantity))
            .andDo(document("product-put",
                            links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("product").description("link to product"),
                                linkWithRel("products").description("link to product list"),
                                linkWithRel("profile").description("link to profile")
                            ),
                            requestFields(productRequestFields()),
                            responseFields(productResponseFields("product-put_links"))
            ));
    }

    private List<FieldDescriptor> productRequestFields() {
        return Lists.newArrayList(
            fieldWithPath("name").description("name of the product"),
            fieldWithPath("description").description("description of the product"),
            fieldWithPath("beginSaleDateTime").type(ZonedDateTime.class).description("on sale date"),
            fieldWithPath("closeSaleDateTime").type(ZonedDateTime.class).description("the end date of sale"),
            fieldWithPath("salePrice").description("sale price"),
            fieldWithPath("quantity").description("available quantity"),
            fieldWithPath("vendorRoleType")
                .description("vendor role type. must be one of " + Arrays.toString(VendorRoleType.values())),
            fieldWithPath("shippingFee").description("shipping fee (optional)").optional()
        );
    }

    private List<FieldDescriptor> productResponseFields(String linkAnchor) {
        return Lists.newArrayList(
            subsectionWithPath("_links").description("<<resources-" + linkAnchor + ", Links>> to other links"),
            fieldWithPath("id").description("identifier of the product"),
            fieldWithPath("name").description("name of the product"),
            fieldWithPath("description").description("description of the product"),
            fieldWithPath("beginSaleDateTime").type(ZonedDateTime.class).description("on sale date"),
            fieldWithPath("closeSaleDateTime").type(ZonedDateTime.class).description("the end date of sale"),
            fieldWithPath("salePrice").description("sale price"),
            fieldWithPath("quantity").description("available quantity"),
            fieldWithPath("vendorRoleType").description("vendor role type"),
            fieldWithPath("shippingFee").description("shipping fee").optional(),
            fieldWithPath("saleStatus")
                .description("sale status. must be one of " + Arrays.toString(ProductSaleStatusType.values())),
            subsectionWithPath("account").description("manager account")
        );
    }

    private Product saveProduct(int index) {
        Product product = StableProduct.builder()
            .name("indexAnyName" + index)
            .build();

        return productRepository.save(product);
    }
}
