package pt.psoft.g1.psoftg1.CDC.producer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;

import java.io.IOException;

public class ReaderMessageBuilder {
    private ObjectMapper mapper;
    private ReaderDetailsDTO readerDTO;

    public ReaderMessageBuilder() {
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(PhoneNumber.class, new PhoneNumberSerializer());
        module.addSerializer(ReaderNumber.class, new ReaderNumberSerializer());
        mapper.registerModule(module);
    }

    // Custom serializer for PhoneNumber
    static class PhoneNumberSerializer extends StdSerializer<PhoneNumber> {
        public PhoneNumberSerializer() {
            super(PhoneNumber.class);
        }

        @Override
        public void serialize(PhoneNumber value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getPhoneNumber());
        }
    }

    // Custom serializer for ReaderNumber
    static class ReaderNumberSerializer extends StdSerializer<ReaderNumber> {
        public ReaderNumberSerializer() {
            super(ReaderNumber.class);
        }

        @Override
        public void serialize(ReaderNumber value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getReaderNumber());
        }
    }

    public String getPayloadFromReader(ReaderDetailsDTO readerDTO) throws JsonProcessingException
    {
        return this.mapper.writeValueAsString(readerDTO);
    }

    public ReaderMessageBuilder withReader(ReaderDetailsDTO readerDTO)
    {
        this.readerDTO = readerDTO;

        return this;
    }

    public Message<String> build() throws JsonProcessingException
    {
        return MessageBuilder.withPayload(getPayloadFromReader(this.readerDTO))
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
