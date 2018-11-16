package kr.openmind.restapi.index;

import kr.openmind.restapi.testsupport.TestRestControllerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestRestControllerConfig
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/api/"))
            .andExpect(status().isOk())
            .andDo(document("index",
                            links(
                                linkWithRel("product").description("The <<resources-product,Product resource>>")
                            )
            ));
    }
}
