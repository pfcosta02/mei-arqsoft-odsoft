package pt.psoft.g1.psoftg1.usermanagement.infrastructure.publishers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.shared.model.AuthNUsersEvents;
import pt.psoft.g1.psoftg1.usermanagement.publishers.AuthNUsersEventsPublisher;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthNUsersEventsRabbitmqPublisher implements AuthNUsersEventsPublisher {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange direct;

    @Autowired
    private ObjectMapper objectMapper;

    /* ========================================================= */
    /*                    Comunicacao com o Reader               */
    /* ========================================================= */
    @Override
    public void publishUserTempCreatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish User Temp CREATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.AuthNUsers", AuthNUsersEvents.TEMP_USER_CREATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* ========================================================= */
    /*               Comunicacao com o AuthNUser Query           */
    /* ========================================================= */
    @Override
    public void publishUserCreatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish User CREATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.AuthNUsers", AuthNUsersEvents.USER_CREATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publishUserUpdatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish User UPDATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.AuthNUsers", AuthNUsersEvents.USER_UPDATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publishUserDeletedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish User DELETED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("text/plain");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.AuthNUsers", AuthNUsersEvents.USER_DELETED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}