package pt.psoft.g1.psoftg1.usermanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthNUsersRabbitmqController {
    @Autowired
    private UserService service;

    @RabbitListener(queues = "#{Queue_Reader_Temp_Created.name}")
    public void consumeMessage_ReaderTempCreated(Message msg)
    {
        try
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received Reader Temp CREATED by AMQP.");
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO userDTO = objectMapper.readValue(jsonReceived, UserDTO.class);
            service.createUserTemp(userDTO);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving Reader Temp CREATED event from AMQP: '" + ex.getMessage() + "'");
        }
    }

    @RabbitListener(queues = "#{Queue_Reader_Temp_Persisted.name}")
    public void consumeMessage_ReaderPersisted(Message msg)
    {
        try
        {
            String authorId = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received Reader Temp PERSISTED by AMQP.");
            service.persistTemporary(authorId);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving Reader Temp PERSISTED event from AMQP: '" + ex.getMessage() + "'");
        }
    }
}