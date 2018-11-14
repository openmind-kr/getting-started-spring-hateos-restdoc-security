package kr.openmind.restapi.testsupport;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

@TestConfiguration
public class RestDocsCustomConfig implements RestDocsMockMvcConfigurationCustomizer {

    private static final String HOST = "openmind.kr";

    @Override
    public void customize(MockMvcRestDocumentationConfigurer configurer) {
        OperationPreprocessor uriModifyingProcessor = Preprocessors.modifyUris().host(HOST).removePort();
        configurer.operationPreprocessors()
            .withRequestDefaults(Preprocessors.prettyPrint(), uriModifyingProcessor)
            .withResponseDefaults(Preprocessors.prettyPrint(), uriModifyingProcessor);
        configurer.snippets().withDefaults(CliDocumentation.httpieRequest(), HttpDocumentation.httpResponse());
    }
}
