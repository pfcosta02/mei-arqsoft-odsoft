package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.psoft.g1.psoftg1.shared.model.AuthNUsersEvents;

@Configuration
public class RabbitmqClientConfig {
    @Bean
    public DirectExchange direct() {
        return new DirectExchange("LMS.Reader");
    }

    // =========================================================================================== //
    //                                      Reader Temp Criado                                     //
    // =========================================================================================== //
    @Bean
    public Queue Queue_Reader_Temp_Created(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userCreatedTempReaderBinding(DirectExchange direct, Queue Queue_Reader_Temp_Created){
        return BindingBuilder.bind(Queue_Reader_Temp_Created)
                .to(direct)
                .with(AuthNUsersEvents.TEMP_READER_CREATED);
    }

    // =========================================================================================== //
    //                                    Reader Temp Persistido                                   //
    // =========================================================================================== //
    @Bean
    public Queue Queue_Reader_Temp_Persisted(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding userPersistedTempReaderBinding(DirectExchange direct, Queue Queue_Reader_Temp_Persisted){
        return BindingBuilder.bind(Queue_Reader_Temp_Persisted)
                .to(direct)
                .with(AuthNUsersEvents.TEMP_READER_PERSISTED);
    }

    // =========================================================================================== //
    //                                           RPC User                                          //
    // =========================================================================================== //
    @Bean
    public DirectExchange directExchangeUser()
    {
        return new DirectExchange("LMS.rpcUser");
    }

    @Bean
    public Queue queueRPCUser()
    {
        return new Queue("LMS.rpcUser.requests",false);
    }

    @Bean
    public Binding bindingUser(DirectExchange directExchangeUser, Queue queueRPCUser)
    {
        return BindingBuilder.bind(queueRPCUser).to(directExchangeUser).with("keyUser");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}