package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitmqPublisherConfig {

    // EXCHANGES
    @Bean(name = "lendingsExchange")
    public DirectExchange lendingsExchange() {
        return new DirectExchange("LMS.lendings");
    }

    @Bean(name = "booksExchange")
    public DirectExchange booksExchange() {
        return new DirectExchange("LMS.books");
    }

    @Bean(name = "readersExchange")
    public DirectExchange readersExchange() {
        return new DirectExchange("LMS.readers");
    }

    @Bean(name = "usersExchange")
    public DirectExchange usersExchange() {
        return new DirectExchange("LMS.users");
    }

    // MESSAGE CONVERTER
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RABBIT TEMPLATE
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReplyTimeout(10000);
        return template;
    }
}