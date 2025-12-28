package pt.psoft.g1.psoftg1.usermanagement.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.usermanagement.model.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuthNUsersRabbitmqRpcController
{
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    public void bootstrapHelper()
    {
        System.out.println("Initial bootstrapHelper");
        List<User> users = new ArrayList<>();
        
        List<UserDTO> response = template.convertSendAndReceiveAsType(
            "LMS.rpcUser",
            "keyUser",
            "",
            new ParameterizedTypeReference<List<UserDTO>>() {}
        );

        if (response == null)
        {
            System.out.println("[Bootstrap] response is empty");
            return;
        }
        else
        {
            System.out.println("[Bootstrap] response is populated");
            for (UserDTO userDTO : response)
            {
                userService.createEvent(userDTO);
            }
        }
    }
}