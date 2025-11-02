package pt.psoft.g1.psoftg1.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("mongodb")
@Order(2) // executa primeiro
public class MongoClean implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoClean(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        mongoTemplate.getDb().drop();
        System.out.println("🧹 Banco MongoDB limpo antes do bootstrap.");
    }
}