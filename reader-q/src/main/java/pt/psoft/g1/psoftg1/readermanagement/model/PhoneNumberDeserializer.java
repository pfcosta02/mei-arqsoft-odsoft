package pt.psoft.g1.psoftg1.readermanagement.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PhoneNumberDeserializer extends JsonDeserializer<PhoneNumber> {
    @Override
    public PhoneNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedStartObjectToken()) { // caso objeto { "phoneNumber": "..." }
            JsonNode node = p.getCodec().readTree(p);
            return new PhoneNumber(node.get("phoneNumber").asText());
        } else { // caso string direta "919191919"
            return new PhoneNumber(p.getText());
        }
    }
}