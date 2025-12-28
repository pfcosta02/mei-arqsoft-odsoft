package pt.psoft.g1.psoftg1.external.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Based on https://www.baeldung.com/spring-5-webclient
 */
@Service
@RequiredArgsConstructor
public class ApiNinjasService {
    private final WebClient apiNinjasWebClient;

    private List<HistoricalEventsResponse> getHistoricalEventsFromYearMonth(int year, int month) {
        return apiNinjasWebClient.get()
                .uri("historicalevents?year=" + year + "&month=" + month)
                .retrieve()
                .bodyToFlux(HistoricalEventsResponse.class)
                .collectList()
                .block();
    }

    public String getRandomEventFromYearMonth(int year, int month) {
        final var responseList = getHistoricalEventsFromYearMonth(year, month);

        if (responseList == null || responseList.isEmpty()) {
            // Tratar o caso de lista nula ou vazia, por exemplo:
            throw new IllegalStateException("Nenhum evento histórico encontrado para o mês e ano fornecidos.");
        }

        int randomIndex = (int) (Math.random() * responseList.size());
        return responseList.get(randomIndex).getEvent();
    }
}
