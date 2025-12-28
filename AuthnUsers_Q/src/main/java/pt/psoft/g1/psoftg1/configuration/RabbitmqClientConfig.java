package pt.psoft.g1.psoftg1.configuration;

import pt.psoft.g1.psoftg1.shared.model.AuthNUsersEvents;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

// RabbitMQ
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.BindingBuilder;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitmqClientConfig {

    @Bean
    public DirectExchange direct() {
        return new DirectExchange("LMS.AuthNUser");
    }

    @Bean
    public DirectExchange rpcDirect(){
        return new DirectExchange("LMS.rpcUser");
    }

    // =========================================================================================== //
    //                                         User Criado                                         //  
    // =========================================================================================== //
    @Bean
    public Queue Queue_User_Created(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userCreatedBinding(DirectExchange direct, Queue Queue_User_Created){
        return BindingBuilder.bind(Queue_User_Created)
                .to(direct)
                .with(AuthNUsersEvents.USER_CREATED);
    }

    // =========================================================================================== //
    //                                         User Updated                                        //  
    // =========================================================================================== //
    @Bean
    public Queue Queue_User_Updated(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userUpdatedBinding(DirectExchange direct, Queue Queue_User_Updated){
        return BindingBuilder.bind(Queue_User_Updated)
                .to(direct)
                .with(AuthNUsersEvents.USER_UPDATED);
    }

    // =========================================================================================== //
    //                                         User Deleted                                        //  
    // =========================================================================================== //
    @Bean
    public Queue Queue_User_Deleted(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userDeletedBinding(DirectExchange direct, Queue Queue_User_Deleted){
        return BindingBuilder.bind(Queue_User_Deleted)
                .to(direct)
                .with(AuthNUsersEvents.USER_DELETED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
