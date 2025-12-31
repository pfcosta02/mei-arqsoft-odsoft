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
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.dto.NameDTO;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDTO;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.dto.BirthDateDTO;

import pt.psoft.g1.psoftg1.readermanagement.publishers.ReaderEventsPublisher;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.publishers.impl.ReaderEventsRabbitmqPublisherImpl;
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
        ,classes = {ReaderEventsRabbitmqPublisherImpl.class}
        , properties = {
        "stubrunner.amqp.mockConnection=true"
}
)
@Provider("readers_event-producer")
@PactBroker(
        url = "${pact.broker.url:http://localhost:9292}",
        authentication = @PactBrokerAuth(username = "${pact.broker.username:admin}", password = "${pact.broker.password:admin}")
)
public class ReaderProducerCDCIT
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderProducerCDCIT.class);

    @Autowired
    ReaderEventsPublisher eventsPublisher;

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

    @PactVerifyProvider("a permanent reader with his details created event")
    public MessageAndMetadata readerCreated() throws JsonProcessingException
    {
        Reader reader = new Reader("1", "1", "Pedro Soares", "pedro@email.com");

        ReaderDetails readerDetails = new ReaderDetails(
                1,
                reader,
                "2000-01-01",
                "919191919",
                true,
                true,
                true,
                "photo.jpg",
                java.util.Collections.emptyList()
        );

        readerDetails.setId("1");

        ReaderDTO readerDTO = new ReaderDTO( reader.getReaderId(), reader.getUserId(), new NameDTO(reader.getName().getName()), reader.getEmail());

        ReaderDetailsDTO dto = new ReaderDetailsDTO(
                readerDetails.getId(),
                readerDTO,
                new ReaderNumber(readerDetails.getReaderNumber()), // ReaderNumber
                new BirthDateDTO(readerDetails.getBirthDate().toString()), // BirthDateDTO
                new PhoneNumber(readerDetails.getPhoneNumber()), // PhoneNumber
                readerDetails.isGdprConsent(),
                readerDetails.isMarketingConsent(),
                readerDetails.isThirdPartySharingConsent(),
                readerDetails.getVersion(),
                readerDetails.getInterestList(), // List<String>
                readerDetails.getPhoto().getPhotoFile() // String photo
        );


        eventsPublisher.publishReaderCreatedEvent(new ReaderMessageBuilder().getPayloadFromReader(dto));

        Message<String> message = new ReaderMessageBuilder().withReader(dto).build();

        return generateMessageAndMetadata(message);
    }

    private MessageAndMetadata generateMessageAndMetadata(Message<String> message)
    {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        message.getHeaders().forEach((k, v) -> metadata.put(k, v));

        return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
    }

}

