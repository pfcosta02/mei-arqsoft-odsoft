//package pt.psoft.g1.psoftg1.configuration;
//
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import pt.psoft.g1.psoftg1.bookmanagement.api.BookEventRabbitmqReceiver;
//import pt.psoft.g1.psoftg1.bookmanagement.services.BookService;
//import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingEventRabbitmqReceiver;
//import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
//import pt.psoft.g1.psoftg1.readermanagement.api.ReaderEventRabbitmqReceiver;
//import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
//import pt.psoft.g1.psoftg1.shared.model.BookEvents;
//import pt.psoft.g1.psoftg1.shared.model.LendingEvents;
//import pt.psoft.g1.psoftg1.shared.model.ReaderEvents;
//import pt.psoft.g1.psoftg1.shared.model.UserEvents;
//import pt.psoft.g1.psoftg1.usermanagement.api.UserEventRabbitmqReceiver;
//import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
//
//@Profile("!test")
//@Configuration
//public class RabbitmqClientConfig {
//
//    public static final String LENDING_DB_SYNC_QUEUE = "lendings_db_sync_queue";
//    public static final String READER_DB_SYNC_QUEUE = "readers_db_sync_queue";
//    public static final String BOOK_DB_SYNC_QUEUE = "books_db_sync_queue";
//
//    @Bean
//    public Queue lendingDbSyncQueue() {
//        return new Queue(LENDING_DB_SYNC_QUEUE, false);
//    }
//
//    @Bean
//    public Queue readerDbSyncQueue() {
//        return new Queue(READER_DB_SYNC_QUEUE, false);
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setReplyTimeout(10000); // reply timeout to 10 seconds
//        return rabbitTemplate;
//    }
//
//    // Lendings exchange
//    @Bean(name = "lendingsExchange")
//    public DirectExchange lendingsExchange() {
//        return new DirectExchange("LMS.lendings");
//    }
//
//    // Books Exchange
//    @Bean(name = "booksExchange")
//    public DirectExchange booksExchange() {
//        return new DirectExchange("LMS.books");
//    }
//
//    // Readers  Exchange
//    @Bean(name = "readersExchange")
//    public DirectExchange readersExchange() {
//        return new DirectExchange("LMS.readers");
//    }
//
//    // Users Exchange
//    @Bean(name = "usersExchange")
//    public DirectExchange usersExchange() {
//        return new DirectExchange("LMS.users");
//    }
//
//    // Recommendation Exchange
//    @Bean(name = "recommendationsExchange")
//    public DirectExchange recommendationsExchange() {
//        return new DirectExchange("LMS.recommendations");
//    }
//
//    private static class ReceiverConfig {
//
//        // Lending Queues
//        @Bean(name = "autoDeleteQueue_Lending_Created")
//        public Queue autoDeleteQueue_Lending_Created() {
//            System.out.println("autoDeleteQueue_Lending_Created created!");
//            return new AnonymousQueue();
//        }
//
//        @Bean(name = "autoDeleteQueue_Lending_Updated")
//        public Queue autoDeleteQueue_Lending_Updated() {
//            return new AnonymousQueue();
//        }
//
//        @Bean(name = "autoDeleteQueue_Lending_Deleted")
//        public Queue autoDeleteQueue_Lending_Deleted() {
//            return new AnonymousQueue();
//        }
//
//        // Books Queues
//        @Bean(name = "autoDeleteQueue_Book_Created")
//        public Queue autoDeleteQueue_Book_Created() {
//            return new AnonymousQueue();
//        }
//
//        @Bean(name = "autoDeleteQueue_Book_Deleted")
//        public Queue autoDeleteQueue_Book_Deleted() {
//            return new AnonymousQueue();
//        }
//
//        // Readers  Queues
//        @Bean(name = "autoDeleteQueue_Reader_Created")
//        public Queue autoDeleteQueue_Reader_Created() {
//            return new AnonymousQueue();
//        }
//
//        @Bean(name = "autoDeleteQueue_Reader_Deleted")
//        public Queue autoDeleteQueue_Reader_Deleted() {
//            return new AnonymousQueue();
//        }
//
//        // Users Queues
//        @Bean(name = "autoDeleteQueue_User_Created")
//        public Queue autoDeleteQueue_User_Created() {
//            return new AnonymousQueue();
//        }
//
//        // Lending Bindings
//        @Bean
//        public Binding lendingCreatedBinding(@Qualifier("lendingsExchange") DirectExchange direct,
//                                             @Qualifier("autoDeleteQueue_Lending_Created") Queue autoDeleteQueue_Lending_Created) {
//            return BindingBuilder.bind(autoDeleteQueue_Lending_Created)
//                    .to(direct)
//                    .with(LendingEvents.LENDING_CREATED);
//        }
//
//        @Bean
//        public Binding lendingUpdatedBinding(@Qualifier("lendingsExchange") DirectExchange lendingExchange,
//                                             @Qualifier("autoDeleteQueue_Lending_Updated") Queue autoDeleteQueue_Lending_Updated) {
//            return BindingBuilder.bind(autoDeleteQueue_Lending_Updated)
//                    .to(lendingExchange)
//                    .with(LendingEvents.LENDING_UPDATED);
//        }
//
//        @Bean
//        public Binding lendingDeletedBinding(@Qualifier("lendingsExchange") DirectExchange lendingExchange,
//                                             @Qualifier("autoDeleteQueue_Lending_Deleted") Queue autoDeleteQueue_Lending_Deleted) {
//            return BindingBuilder.bind(autoDeleteQueue_Lending_Deleted)
//                    .to(lendingExchange)
//                    .with(LendingEvents.LENDING_DELETED);
//        }
//
//        // Books Bindings
//        @Bean
//        public Binding bookCreatedBinding(@Qualifier("booksExchange") DirectExchange booksExchange,
//                                          @Qualifier("autoDeleteQueue_Book_Created") Queue autoDeleteQueue_Book_Created) {
//            return BindingBuilder.bind(autoDeleteQueue_Book_Created)
//                    .to(booksExchange)
//                    .with(BookEvents.BOOK_CREATED);
//        }
//
//        @Bean
//        public Binding bookDeletedBinding(@Qualifier("booksExchange") DirectExchange booksExchange,
//                                          @Qualifier("autoDeleteQueue_Book_Deleted") Queue autoDeleteQueue_Book_Deleted) {
//            return BindingBuilder.bind(autoDeleteQueue_Book_Deleted)
//                    .to(booksExchange)
//                    .with(BookEvents.BOOK_DELETED);
//        }
//
//        // Readers  Bindings
//        @Bean
//        public Binding readerCreatedBinding(@Qualifier("readersExchange") DirectExchange readersExchange,
//                                                   @Qualifier("autoDeleteQueue_Reader_Created") Queue autoDeleteQueue_Reader_Created) {
//            return BindingBuilder.bind(autoDeleteQueue_Reader_Created)
//                    .to(readersExchange)
//                    .with(ReaderEvents.READER_CREATED);
//        }
//
//        @Bean
//        public Binding readerDeletedBinding(@Qualifier("readersExchange") DirectExchange readersExchange,
//                                                   @Qualifier("autoDeleteQueue_Reader_Deleted") Queue autoDeleteQueue_Reader_Deleted) {
//            return BindingBuilder.bind(autoDeleteQueue_Reader_Deleted)
//                    .to(readersExchange)
//                    .with(ReaderEvents.READER_DELETED);
//        }
//
//        // User Bindings
//        @Bean
//        public Binding userCreatedBinding(@Qualifier("usersExchange") DirectExchange usersExchange,
//                                          @Qualifier("autoDeleteQueue_User_Created") Queue autoDeleteQueue_User_Created) {
//            return BindingBuilder.bind(autoDeleteQueue_User_Created)
//                    .to(usersExchange)
//                    .with(UserEvents.USER_CREATED);
//        }
//
//        // Receivers
//        @Bean
//        public LendingEventRabbitmqReceiver lendingReceiver(LendingService lendingService) {
//            return new LendingEventRabbitmqReceiver(lendingService);
//        }
//
//        @Bean
//        public BookEventRabbitmqReceiver bookReceiver(BookService bookService) {
//            return new BookEventRabbitmqReceiver(bookService);
//        }
//
//        @Bean
//        public ReaderEventRabbitmqReceiver readerReceiver(ReaderService readerService) {
//            return new ReaderEventRabbitmqReceiver(readerService);
//        }
//
//        @Bean
//        public UserEventRabbitmqReceiver userReceiver(UserService userService) {
//            return new UserEventRabbitmqReceiver(userService);
//        }
//    }
//}
