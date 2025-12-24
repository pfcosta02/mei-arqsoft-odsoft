package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;

//@Profile("!test")
@Configuration
public  class RabbitmqClientConfig {

    /* ========= Exchanges ========= */

    @Bean
    public FanoutExchange authorCreatedExchange() {
        return new FanoutExchange(AuthorEvents.AUTHOR_CREATED);
    }

    @Bean
    public FanoutExchange authorUpdatedExchange() {
        return new FanoutExchange(AuthorEvents.AUTHOR_UPDATED);
    }

    @Bean
    public FanoutExchange authorDeletedExchange() {
        return new FanoutExchange(AuthorEvents.AUTHOR_DELETED);
    }

    /* ========= Queues ========= */

    @Bean
    public Queue autoDeleteQueue_Author_Created() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue autoDeleteQueue_Author_Updated() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue autoDeleteQueue_Author_Deleted() {
        return new AnonymousQueue();
    }

    /* ========= Bindings ========= */

    @Bean
    public Binding bindAuthorCreated(
            FanoutExchange authorCreatedExchange,
            Queue autoDeleteQueue_Author_Created) {
        return BindingBuilder.bind(autoDeleteQueue_Author_Created)
                .to(authorCreatedExchange);
    }

    @Bean
    public Binding bindAuthorUpdated(
            FanoutExchange authorUpdatedExchange,
            Queue autoDeleteQueue_Author_Updated) {
        return BindingBuilder.bind(autoDeleteQueue_Author_Updated)
                .to(authorUpdatedExchange);
    }

    @Bean
    public Binding bindAuthorDeleted(
            FanoutExchange authorDeletedExchange,
            Queue autoDeleteQueue_Author_Deleted) {
        return BindingBuilder.bind(autoDeleteQueue_Author_Deleted)
                .to(authorDeletedExchange);
    }
}
