package pt.psoft.g1.psoftg1.readermanagement.api;

import org.springframework.amqp.core.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ReaderRabbitmqController {
    @Autowired
    private ReaderService service;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "#{Queue_Reader_Created.name}")
    public void consumeMessage_ReaderCreated(Message msg)
    {
        try
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received Reader CREATED by AMQP.");
            ReaderDetailsDTO rd = objectMapper.readValue(jsonReceived, ReaderDetailsDTO.class);

            service.createEvent(rd);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving Reader CREATED event from AMQP: '" + ex.getMessage() + "'");
        }

    }

    @RabbitListener(queues = "#{Queue_Reader_Updated.name}")
    public void consumeMessage_ReaderUpdated(Message msg)
    {
        try
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received Reader UPDATED by AMQP.");
            ReaderDetailsDTO rd = objectMapper.readValue(jsonReceived, ReaderDetailsDTO.class);

            service.updateEvent(rd);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving Reader UPDATED event from AMQP: '" + ex.getMessage() + "'");
        }

    }

    @RabbitListener(queues = "#{Queue_Reader_Deleted.name}")
    public void consumeMessage_ReaderDeleted(Message msg)
    {
        try
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received Reader DELETED by AMQP.");
            service.deleteEvent(jsonReceived);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving Reader DELETED event from AMQP: '" + ex.getMessage() + "'");
        }

    }
}
