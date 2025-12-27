package pt.psoft.g1.psoftg1.usermanagement.api;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
import pt.psoft.g1.psoftg1.usermanagement.dto.RealUserDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthNUsersRabbitmqRpcController {
    @Autowired
    private UserService service;

    @RabbitListener(queues = "LMS.rpcUser.requests")
    @SendTo("Receiver")
    public List<RealUserDTO> send()
    {
        System.out.println("[x] Received LMS.rpcUser.request");
        return service.usersToDTO();
    }
}