package pt.psoft.g1.psoftg1.CDC.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import pt.psoft.g1.psoftg1.usermanagement.dto.RealUserDTO;

public class AuthNUsersMessageBuilder
{
    private ObjectMapper mapper = new ObjectMapper();
    private RealUserDTO userDTO;

    public String getPayloadFromUser(RealUserDTO userDTO) throws JsonProcessingException
    {
        return this.mapper.writeValueAsString(userDTO);
    }

    public AuthNUsersMessageBuilder withUser(RealUserDTO userDTO)
    {
        this.userDTO = userDTO;

        return this;
    }

    public Message<String> build() throws JsonProcessingException
    {
        return MessageBuilder.withPayload(getPayloadFromUser(this.userDTO))
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}