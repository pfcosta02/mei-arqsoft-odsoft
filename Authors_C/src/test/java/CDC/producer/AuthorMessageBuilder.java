package CDC.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;

public class AuthorMessageBuilder {
    private final ObjectMapper mapper = new ObjectMapper();
    private AuthorViewAMQP authorViewAMQP;

    public String getPayloadFromAuthor(AuthorViewAMQP authorViewAMQP) throws JsonProcessingException
    {
        return mapper.writeValueAsString(authorViewAMQP);
    }

    public AuthorMessageBuilder withAuthor(AuthorViewAMQP authorViewAMQP)
    {
        this.authorViewAMQP = authorViewAMQP;

        return this;
    }

    public Message<String> build() throws JsonProcessingException
    {
        return MessageBuilder.withPayload(getPayloadFromAuthor(this.authorViewAMQP))
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
