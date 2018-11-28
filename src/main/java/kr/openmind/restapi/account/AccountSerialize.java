package kr.openmind.restapi.account;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerialize extends JsonSerializer<Account> {

    @Override
    public void serialize(Account account, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("id", account.getId());
        generator.writeEndObject();
    }
}
