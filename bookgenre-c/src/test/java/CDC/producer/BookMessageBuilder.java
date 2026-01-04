package CDC.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;

public class BookMessageBuilder {
    private final ObjectMapper mapper = new ObjectMapper();
    private BookViewAMQP bookViewAMQP;

    public String getPayloadFromBook(BookViewAMQP bookViewAMQP) throws JsonProcessingException
    {
        return mapper.writeValueAsString(bookViewAMQP);
    }

    public BookMessageBuilder withBook(BookViewAMQP bookViewAMQP)
    {
        this.bookViewAMQP = bookViewAMQP;

        return this;
    }

    public Message<String> build() throws JsonProcessingException
    {
        return MessageBuilder.withPayload(getPayloadFromBook(this.bookViewAMQP))
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
