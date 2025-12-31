package pt.psoft.g1.psoftg1.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class TestConfig
{
    @Bean
    ObjectMapper objectMapper()
    {
        // Usa o mapper real, igual ao da app
        return new ObjectMapper();
    }
}