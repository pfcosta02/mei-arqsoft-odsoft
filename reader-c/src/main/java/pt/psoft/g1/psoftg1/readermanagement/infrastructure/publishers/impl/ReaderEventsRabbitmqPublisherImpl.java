package pt.psoft.g1.psoftg1.readermanagement.infrastructure.publishers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.readermanagement.publishers.ReaderEventsPublisher;
import pt.psoft.g1.psoftg1.shared.model.ReaderEvents;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ReaderEventsRabbitmqPublisherImpl implements ReaderEventsPublisher {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange direct;

    @Autowired
    private ObjectMapper objectMapper;

    /* ========================================================= */
    /*                  Comunicacao com o AuthNUser              */
    /* ========================================================= */
    @Override
    public void publishReaderTempCreatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish Reader Temp CREATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.Reader", ReaderEvents.TEMP_READER_CREATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publishReaderPersistedEvent(String authorId)
    {
        try
        {
            System.out.println(" [x] Publish Reader Temp PERSISTED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("text/plain");
            Message message = new Message(authorId.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.Reader", ReaderEvents.TEMP_READER_PERSISTED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /* ========================================================= */
    /*                 Comunicacao com o Reader Query            */
    /* ========================================================= */
    @Override
    public void publishReaderCreatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish Reader CREATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.Reader", ReaderEvents.READER_CREATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publishReaderUpdatedEvent(String payload)
    {
        try
        {
            System.out.println(" [x] Publish Reader UPDATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.Reader", ReaderEvents.READER_UPDATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publishReaderDeletedEvent(String readerId)
    {
        try
        {
            System.out.println(" [x] Publish Reader DELETED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("text/plain");
            Message message = new Message(readerId.getBytes(StandardCharsets.UTF_8), props);
            template.send("LMS.Reader", ReaderEvents.READER_DELETED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}