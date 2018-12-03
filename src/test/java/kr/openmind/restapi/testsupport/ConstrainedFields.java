package kr.openmind.restapi.testsupport;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class ConstrainedFields {

    private static final String FIELD_ATTRIBUTE_NAME = "constraints";

    private final ConstraintDescriptions constraintDescriptions;

    public ConstrainedFields(Class<?> clazz) {
        constraintDescriptions = new ConstraintDescriptions(clazz);
    }

    public FieldDescriptor withPath(String path) {
        return fieldWithPath(path)
            .attributes(
                key(FIELD_ATTRIBUTE_NAME)
                .value(StringUtils.collectionToDelimitedString(constraintDescriptions.descriptionsForProperty(path), ". ")));
    }
}
