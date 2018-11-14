package kr.openmind.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Optional;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    @SneakyThrows
    public void serialize(Errors errors, JsonGenerator generator, SerializerProvider serializers) {
        generator.writeStartArray();
        errors.getFieldErrors().forEach(error -> serializeFieldError(generator, error));
        generator.writeEndArray();
    }

    @SneakyThrows
    private void serializeFieldError(JsonGenerator generator, FieldError error) {
        generator.writeStartObject();
        generator.writeStringField("field", error.getField());
        generator.writeStringField("objectName", error.getObjectName());
        generator.writeStringField("defaultMessage", error.getDefaultMessage());
        generator.writeStringField("rejectedValue", Optional.ofNullable(error.getRejectedValue()).orElse("").toString());
        generator.writeEndObject();
    }
}
