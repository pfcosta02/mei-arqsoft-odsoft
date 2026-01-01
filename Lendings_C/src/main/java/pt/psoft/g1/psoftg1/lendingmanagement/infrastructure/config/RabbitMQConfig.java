package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String LENDINGS_EXCHANGE = "lendings.exchange";

    // Queues
    public static final String LENDINGS_CREATED_QUEUE = "lendings.created";
    public static final String BOOK_RETURNED_QUEUE = "book.returned";
    public static final String LENDING_OVERDUE_QUEUE = "lending.overdue";

    // Routing Keys
    public static final String LENDING_CREATED_KEY = "lending.created";
    public static final String BOOK_RETURNED_KEY = "book.returned";
    public static final String LENDING_OVERDUE_KEY = "lending.overdue";

    @Bean
    public TopicExchange lendingsExchange() {
        return new TopicExchange(LENDINGS_EXCHANGE, true, false);
    }

    @Bean
    public Queue lendingsCreatedQueue() {
        return new Queue(LENDINGS_CREATED_QUEUE, true, false, false);
    }

    @Bean
    public Queue bookReturnedQueue() {
        return new Queue(BOOK_RETURNED_QUEUE, true, false, false);
    }

    @Bean
    public Queue lendingOverdueQueue() {
        return new Queue(LENDING_OVERDUE_QUEUE, true, false, false);
    }

    @Bean
    public Binding lendingsCreatedBinding(Queue lendingsCreatedQueue, TopicExchange lendingsExchange) {
        return BindingBuilder.bind(lendingsCreatedQueue)
                .to(lendingsExchange)
                .with(LENDING_CREATED_KEY);
    }

    @Bean
    public Binding bookReturnedBinding(Queue bookReturnedQueue, TopicExchange lendingsExchange) {
        return BindingBuilder.bind(bookReturnedQueue)
                .to(lendingsExchange)
                .with(BOOK_RETURNED_KEY);
    }

    @Bean
    public Binding lendingOverdueBinding(Queue lendingOverdueQueue, TopicExchange lendingsExchange) {
        return BindingBuilder.bind(lendingOverdueQueue)
                .to(lendingsExchange)
                .with(LENDING_OVERDUE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setDefaultReceiveQueue(LENDINGS_CREATED_QUEUE);
        return template;
    }
}