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
        return new DirectExchange("LMS.Reader");
    }

    // =========================================================================================== //
    //                                        Reader Criado                                        //
    // =========================================================================================== //
    @Bean
    public Queue Queue_Reader_Created(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding readerCreatedBinding(DirectExchange direct, Queue Queue_Reader_Created){
        return BindingBuilder.bind(Queue_Reader_Created)
                .to(direct)
                .with(ReaderEvents.READER_CREATED);
    }

    // =========================================================================================== //
    //                                       Reader Updated                                        //
    // =========================================================================================== //
    @Bean
    public Queue Queue_Reader_Updated(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding readerUpdatedBinding(DirectExchange direct, Queue Queue_Reader_Updated){
        return BindingBuilder.bind(Queue_Reader_Updated)
                .to(direct)
                .with(ReaderEvents.READER_UPDATED);
    }

    // =========================================================================================== //
    //                                       Reader Deleted                                        //
    // =========================================================================================== //
    @Bean
    public Queue Queue_Reader_Deleted(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding readerDeletedBinding(DirectExchange direct, Queue Queue_Reader_Deleted){
        return BindingBuilder.bind(Queue_Reader_Deleted)
                .to(direct)
                .with(ReaderEvents.READER_DELETED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
