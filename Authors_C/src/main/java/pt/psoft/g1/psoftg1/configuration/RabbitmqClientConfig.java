package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorRabbitmqController;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;

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

    @Bean
    public DirectExchange direct() {
        return new DirectExchange("Author.Events");
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

    @Bean
    public Queue autoDeleteQueue_Book_Temp_Created() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue autoDeleteQueue_Book_Finalized() {
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

    @Bean
    public Binding bindBookTempCreated(
            Queue autoDeleteQueue_Book_Temp_Created) {
        return BindingBuilder.bind(autoDeleteQueue_Book_Temp_Created)
                .to(new DirectExchange("Books.Events"))
                .with(BookEvents.TEMP_BOOK_CREATED);
    }

    @Bean
    public Binding bindBookFinalized(
            Queue autoDeleteQueue_Book_Finalized) {
        return BindingBuilder.bind(autoDeleteQueue_Book_Finalized)
                .to(new DirectExchange("Books.Events"))
                .with(BookEvents.BOOK_FINALIZED);
    }
}
