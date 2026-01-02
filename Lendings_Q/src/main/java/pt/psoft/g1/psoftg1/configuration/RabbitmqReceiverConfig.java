package pt.psoft.g1.psoftg1.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingEventRabbitmqReceiver;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.shared.model.LendingEvents;

@Configuration
public class RabbitmqReceiverConfig {

    @Bean(name = "lendingsExchange")
    public DirectExchange lendingsExchange() {
        return new DirectExchange("LMS.lendings");
    }

    // ANONYMOUS QUEUES
    @Bean(name = "autoDeleteQueue_Lending_Created")
    public Queue autoDeleteQueue_Lending_Created() {
        return new AnonymousQueue();
    }

    @Bean(name = "autoDeleteQueue_Lending_Updated")
    public Queue autoDeleteQueue_Lending_Updated() {
        return new AnonymousQueue();
    }

    @Bean(name = "autoDeleteQueue_Lending_Deleted")
    public Queue autoDeleteQueue_Lending_Deleted() {
        return new AnonymousQueue();
    }

    @Bean(name = "autoDeleteQueue_Lending_Returned")
    public Queue autoDeleteQueue_Lending_Returned() {
        return new AnonymousQueue();
    }

    // BINDINGS
    @Bean
    public Binding lendingCreatedBinding(
            @Qualifier("lendingsExchange") DirectExchange exchange,
            @Qualifier("autoDeleteQueue_Lending_Created") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(LendingEvents.LENDING_CREATED);
    }

    @Bean
    public Binding lendingUpdatedBinding(
            @Qualifier("lendingsExchange") DirectExchange exchange,
            @Qualifier("autoDeleteQueue_Lending_Updated") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(LendingEvents.LENDING_UPDATED);
    }

    @Bean
    public Binding lendingDeletedBinding(
            @Qualifier("lendingsExchange") DirectExchange exchange,
            @Qualifier("autoDeleteQueue_Lending_Deleted") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(LendingEvents.LENDING_DELETED);
    }

    @Bean
    public Binding lendingReturnedBinding(
            @Qualifier("lendingsExchange") DirectExchange exchange,
            @Qualifier("autoDeleteQueue_Lending_Returned") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(LendingEvents.LENDING_RETURNED);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public LendingEventRabbitmqReceiver lendingReceiver(
            LendingService service,
            ObjectMapper objectMapper) {
        return new LendingEventRabbitmqReceiver(service, objectMapper);
    }
}