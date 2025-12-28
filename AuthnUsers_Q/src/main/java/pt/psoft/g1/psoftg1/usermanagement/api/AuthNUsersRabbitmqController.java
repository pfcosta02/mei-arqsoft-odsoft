package pt.psoft.g1.psoftg1.usermanagement.api;

import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthNUsersRabbitmqController
{
    @Autowired
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "#{Queue_User_Created.name}")
    public void consumeMessage_UserCreated(Message msg)
    {
        try 
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received User CREATED by AMQP.");
            UserDTO userDTO = objectMapper.readValue(jsonReceived, UserDTO.class);

            service.createEvent(userDTO);
        } 
        catch (Exception ex) 
        {
            System.out.println(" [x] Exception receiving User CREATED event from AMQP: '" + ex.getMessage() + "'");
        }
    }

    @RabbitListener(queues = "#{Queue_User_Updated.name}")
    public void consumeMessage_UserUpdated(Message msg)
    {
        try 
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received User UPDATED by AMQP.");
            UserDTO userDTO = objectMapper.readValue(jsonReceived, UserDTO.class);
      
            service.updateEvent(userDTO);
        } 
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving User UPDATED event from AMQP: '" + ex.getMessage() + "'");
        }
    }

    @RabbitListener(queues = "#{Queue_User_Deleted.name}")
    public void consumeMessage_UserDeleted(Message msg)
    {
        try 
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received User DELETED by AMQP.");

            service.deleteEvent(jsonReceived);
        } 
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving User DELETED event from AMQP: '" + ex.getMessage() + "'");
        }
    }
}