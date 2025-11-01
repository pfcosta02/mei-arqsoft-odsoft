package pt.psoft.g1.psoftg1.idgeneratormanagement.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
import pt.psoft.g1.psoftg1.idgeneratormanagement.impl.IdGeneratorBase65Random;
import pt.psoft.g1.psoftg1.idgeneratormanagement.impl.IdGeneratorTimestampBase65;
import pt.psoft.g1.psoftg1.idgeneratormanagement.impl.IdGeneratorTimestampHex;

@Configuration
public class IdGeneratorFactory {

    @Value("${id.generator.strategy}")
    private String idGeneratorStrategy;

    @Bean
    public IdGenerator createIdGenerator() {
        return switch (idGeneratorStrategy.toLowerCase()) {
            case "base65" -> new IdGeneratorBase65Random();
            case "timestamphex" -> new IdGeneratorTimestampHex();
            case "timestampbase65" -> new IdGeneratorTimestampBase65();
            default -> throw new IllegalArgumentException("Invalid ID generator strategy: " + idGeneratorStrategy);
        };
    }
}