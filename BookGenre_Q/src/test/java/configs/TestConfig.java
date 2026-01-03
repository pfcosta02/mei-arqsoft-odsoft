package configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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