package pt.psoft.g1.psoftg1.readermanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pt.psoft.g1.psoftg1.readermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ReaderRabbitmqController {
    @Autowired
    private final ReaderService readerService;

    @RabbitListener(queues = "#{Queue_User_Temp_Created.name}")
    public void consumeMessage_UserTemp(Message msg)
    {
        try
        {
            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received User Temp CREATED by AMQP.");
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO userDTO = objectMapper.readValue(jsonReceived, UserDTO.class);
            readerService.persistTemporary(userDTO);
        }
        catch (Exception ex)
        {
            System.out.println(" [x] Exception receiving User Temp CREATED event from AMQP: '" + ex.getMessage() + "'");
        }

    }
//    @RabbitListener(queues = "#{autoDeleteQueue_Reader_Created.name}")
//    public void receiveReaderCreatedMsg(Message msg) {
//        try {
//            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            CreateReaderDTOAmqp createReaderDTOAmqp = objectMapper.readValue(jsonReceived, CreateReaderDTOAmqp.class);
//
//            System.out.println(" [x] Received Reader Created by AMQP: " + msg + ".");
//            try {
//                readerService.create(createReaderDTOAmqp);
//                System.out.println(" [x] New reader inserted from AMQP: " + msg + ".");
//            } catch (Exception e) {
//                System.out.println(" [x] Reader already exists. No need to store it.");
//            }
//        }
//        catch(Exception ex) {
//            System.out.println(" [x] Exception receiving reader event from AMQP: '" + ex.getMessage() + "'");
//        }
//    }
//
//    @RabbitListener(queues = "#{autoDeleteQueue_Reader_Updated.name}")
//    public void receiveReaderUpdated(Message msg) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            String jsonReceived = new String(msg.getBody(), StandardCharsets.UTF_8);
//            CreateReaderDTOAmqp UpdateReaderDTOAmqp = objectMapper.readValue(jsonReceived, CreateReaderDTOAmqp.class);
//
//            System.out.println(" [x] Received Reader Updated by AMQP: " + msg + ".");
//            try {
//                readerService.update(UpdateReaderDTOAmqp);
//                System.out.println(" [x] Reader updated from AMQP: " + msg + ".");
//            } catch (Exception e) {
//                System.out.println(" [x] Reader does not exists or wrong version. Nothing stored.");
//            }
//        }
//        catch(Exception ex) {
//            System.out.println(" [x] Exception receiving reader event from AMQP: '" + ex.getMessage() + "'");
//        }
//    }
}
