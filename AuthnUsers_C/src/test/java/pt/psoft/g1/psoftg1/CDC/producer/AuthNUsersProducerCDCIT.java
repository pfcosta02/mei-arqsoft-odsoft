package pt.psoft.g1.psoftg1.CDC.producer;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;

import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.dto.RealUserDTO;
import pt.psoft.g1.psoftg1.usermanagement.dto.RoleDTO;
import pt.psoft.g1.psoftg1.usermanagement.services.EditUserMapper;
import pt.psoft.g1.psoftg1.usermanagement.publishers.AuthNUsersEventsPublisher;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserTempRepository;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.publishers.impl.AuthNUsersEventsRabbitmqPublisher;
import pt.psoft.g1.psoftg1.configs.TestConfig;

import java.util.HashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

@Import(TestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
        ,classes = {AuthNUsersEventsRabbitmqPublisher.class}
        , properties = {
        "stubrunner.amqp.mockConnection=true"
}
)
@Provider("authnusers_event-producer")
@PactBroker(
        url = "${pact.broker.url:http://localhost:9292}",
        authentication = @PactBrokerAuth(username = "${pact.broker.username:admin}", password = "${pact.broker.password:admin}")
)
public class AuthNUsersProducerCDCIT
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthNUsersProducerCDCIT.class);

    @Autowired
    AuthNUsersEventsPublisher eventsPublisher;

    @MockBean
    RabbitTemplate template;

    @MockBean
    DirectExchange direct;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(Pact pact, Interaction interaction, PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @PactVerifyProvider("a permanent user reader created event")
    public MessageAndMetadata userCreated() throws JsonProcessingException
    {
        User user = User.newUser("joaquina@email.com", "password123", "Joaquina Miguel", Role.READER);
        user.setId("1");

        RealUserDTO userDTO = new RealUserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getName().getName(), user.getVersion(), user.isEnabled(),
                user.getAuthorities()
                        .stream()
                        .map(role -> new RoleDTO(role.getAuthority()))
                        .collect(Collectors.toSet())
        );

        eventsPublisher.publishUserCreatedEvent(new AuthNUsersMessageBuilder().getPayloadFromUser(userDTO));

        Message<String> message = new AuthNUsersMessageBuilder().withUser(userDTO).build();

        return generateMessageAndMetadata(message);
    }

    private MessageAndMetadata generateMessageAndMetadata(Message<String> message)
    {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        message.getHeaders().forEach((k, v) -> metadata.put(k, v));

        return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
    }
}