package pt.psoft.g1.psoftg1.readermanagement.api;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderRabbitmqRpcController {
    @Autowired
    private ReaderService service;

    @RabbitListener(queues = "LMS.rpcReader.requests")
    @SendTo("Receiver")
    public List<ReaderDetailsDTO> send()
    {
        System.out.println("[x] Received LMS.rpcReader.request");
        return service.readersToDTO();
    }
}
