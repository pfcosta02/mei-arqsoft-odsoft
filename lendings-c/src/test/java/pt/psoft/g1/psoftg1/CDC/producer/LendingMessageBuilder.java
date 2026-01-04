package pt.psoft.g1.psoftg1.CDC.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;

public class LendingMessageBuilder {
    private final ObjectMapper mapper;
    private LendingEventAMQP lendingEvent;

    public LendingMessageBuilder() {
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        this.mapper.registerModule(module);

        // ✅ Datas como STRING (ISO-8601), não timestamps/arrays
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // ✅ Inclui SEMPRE campos, mesmo quando null
        this.mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // (Se em algum lado tiveres Include.NON_NULL, retira-o para este builder)
    }

    public String getPayloadFromLending(LendingEventAMQP lendingEvent) throws JsonProcessingException {
        return this.mapper.writeValueAsString(lendingEvent);
    }

    public LendingMessageBuilder withLending(LendingEventAMQP lendingEvent) {
        this.lendingEvent = lendingEvent;
        return this;
    }

    public Message<String> build() throws JsonProcessingException {
        return MessageBuilder.withPayload(getPayloadFromLending(this.lendingEvent))
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
