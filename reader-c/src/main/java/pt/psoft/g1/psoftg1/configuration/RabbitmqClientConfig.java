package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.psoft.g1.psoftg1.shared.model.ReaderEvents;

@Configuration
public class RabbitmqClientConfig {
    @Bean
    public DirectExchange direct() {
        return new DirectExchange("LMS.AuthNUsers");
    }

    // =========================================================================================== //
    //                                       User Temp Criado                                      //
    // =========================================================================================== //
    @Bean
    public Queue Queue_User_Temp_Created(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userTempBinding(DirectExchange direct, Queue Queue_User_Temp_Created){
        return BindingBuilder.bind(Queue_User_Temp_Created)
                .to(direct)
                .with(ReaderEvents.TEMP_USER_CREATED);
    }

    // =========================================================================================== //
    //                                          RPC Reader                                         //
    // =========================================================================================== //
    @Bean
    public DirectExchange directExchangeReader()
    {
        return new DirectExchange("LMS.rpcReader");
    }

    @Bean
    public Queue queueRPCReader()
    {
        return new Queue("LMS.rpcReader.requests",false);
    }

    @Bean
    public Binding bindingReader(DirectExchange directExchangeReader, Queue queueRPCReader)
    {
        return BindingBuilder.bind(queueRPCReader).to(directExchangeReader).with("keyReader");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}