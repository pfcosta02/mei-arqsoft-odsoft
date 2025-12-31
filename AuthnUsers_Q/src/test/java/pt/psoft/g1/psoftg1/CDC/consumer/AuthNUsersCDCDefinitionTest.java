package pt.psoft.g1.psoftg1.CDC.consumer;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.configs.TestConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pt.psoft.g1.psoftg1.usermanagement.api.AuthNUsersRabbitmqController;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TestConfig.class)
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
        ,classes = {AuthNUsersRabbitmqController.class, UserService.class}
)
@PactConsumerTest
@PactTestFor(providerName = "authnusers_event-producer", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4)
public class AuthNUsersCDCDefinitionTest
{

  @MockBean
  UserService userService;

  @Autowired
  AuthNUsersRabbitmqController listener;

  /* Criar o Contrato */
  @Pact(consumer = "authnusers_created-consumer")
  V4Pact createUserCreatedPact(MessagePactBuilder builder)
  {
    PactDslJsonBody body = new PactDslJsonBody();
      body.stringType("id", "1");
      body.stringMatcher("version", "[0-9]+", "1");
      body.booleanType("enabled", true);
      body.stringType("username", "joaquina@email.com");
      body.stringType("password", "password123");
      body.stringType("fullname", "Joaquina Miguel");
      body.array("authorities")
              .object()
                .stringType("authority", "READER")
              .closeObject()
              .closeArray();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("Content-Type", "application/json");

    return builder.expectsToReceive("a permanent user reader created event").withMetadata(metadata).withContent(body).toPact();
  }

  // @Pact(consumer = "user_updated-consumer")
  // V4Pact createUserUpdatedPact(MessagePactBuilder builder)
  // {
  //   PactDslJsonBody body = new PactDslJsonBody()
  //           .stringType("isbn", "6475803429671")
  //           .stringType("title", "updated title")
  //           .stringType("description", "description")
  //           .stringType("genre", "Infantil");
  //       body.array("authorIds")
  //           .integerType(1)
  //           .closeArray();

  //   Map<String, Object> metadata = new HashMap<>();
  //   metadata.put("Content-Type", "application/json");

  //   return builder.expectsToReceive("a permanent user reader updated event")
  //           .withMetadata(metadata)
  //           .withContent(body)
  //           .toPact();
  // }


  @Test
  @PactTestFor(pactMethod = "createUserCreatedPact")
  void testUserCreated(List<V4Interaction.AsynchronousMessage> messages) throws Exception
  {
    // Convert the Pact message to a String (JSON payload)
    String jsonReceived = messages.get(0).contentsAsString();

    // Create a Spring AMQP Message with the JSON payload and optional headers
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setContentType("application/json");
    Message message = new Message(jsonReceived.getBytes(StandardCharsets.UTF_8), messageProperties);

    // Simulate receiving the message in the listener
    assertDoesNotThrow(() -> {
      listener.consumeMessage_UserCreated(message);
    });

    // Verify interactions with the mocked service
    verify(userService, times(1)).createEvent(any(UserDTO.class));
  }

  // @Test
  // @PactTestFor(pactMethod = "createBookUpdatedPact")
  // void testBookUpdated(List<V4Interaction.AsynchronousMessage> messages) throws Exception {
//    String jsonReceived = messages.get(0).contentsAsString();
//    MessageProperties messageProperties = new MessageProperties();
//    messageProperties.setContentType("application/json");
//    Message message = new Message(jsonReceived.getBytes(StandardCharsets.UTF_8), messageProperties);
//
//    assertDoesNotThrow(() -> {
//      listener.receiveBookUpdated(message);
//    });
//
//    // Verify interactions with the mocked service
//    verify(bookService, times(1)).update(any(BookViewAMQP.class));
  // }
}