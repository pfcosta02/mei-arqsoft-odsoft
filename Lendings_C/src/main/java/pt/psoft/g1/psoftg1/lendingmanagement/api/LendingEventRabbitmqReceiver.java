package pt.psoft.g1.psoftg1.lendingmanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LendingEventRabbitmqReceiver {

    private final LendingService lendingService;

    @Transactional
    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Created.name}")
    public void receiveLendingCreated(Message msg) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
            LendingViewAMQP lendingViewAMQP = objectMapper.readValue(jsonReceived, LendingViewAMQP.class);

            System.out.println(" [x] Received Lending Created by AMQP: " + msg + ".");
            try {
                lendingService.create(lendingViewAMQP);
                System.out.println(" [x] New lending inserted from AMQP: " + msg + ".");
            } catch (Exception e) {
                System.out.println(" [x] Lending already exists. No need to store it.");
            }
        }
        catch(Exception ex) {
            System.out.println(" [x] Exception receiving lending event from AMQP: '" + ex.getMessage() + "'");
        }
    }

    @Transactional
    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Updated.name}")
    public void receiveLendingUpdated(Message msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
            LendingViewAMQP lendingViewAMQP = objectMapper.readValue(jsonReceived, LendingViewAMQP.class);

            System.out.println(" [x] Received Lending Updated by AMQP: " + msg + ".");
            try {
                lendingService.update(lendingViewAMQP);
                System.out.println(" [x] Lending updated from AMQP: " + msg + ".");
            } catch (Exception e) {
                System.out.println(" [x] Lending does not exists or wrong version. Nothing stored.");
            }
        }
        catch(Exception ex) {
            System.out.println(" [x] Exception receiving lending event from AMQP: '" + ex.getMessage() + "'");
        }
    }

    @Transactional
    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Deleted.name}")
    public void receiveLendingDeleted(String in) {
        System.out.println(" [x] Received Lending Deleted '" + in + "'");
    }
}