package pt.psoft.g1.psoftg1.genremanagement.repositories.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreESMapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class GenreRepositoryES implements GenreRepository  {

    private final ElasticsearchClient client;
    private final GenreESMapper mapper;
    private static final String INDEX = "genres";

    @Autowired
    public GenreRepositoryES(ElasticsearchClient client, GenreESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Iterable<Genre> findAll() {
        try {
            SearchResponse<GenreES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.matchAll(m -> m))
                            .size(1000),
                    GenreES.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao obter todos os géneros do Elasticsearch", e);
        }
    }

    @Override
    public Optional<Genre> findByString(String genreName) {
        try {
            SearchResponse<GenreES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field("genre")
                                            .query(genreName)))
                            .size(1),
                    GenreES.class);

            return search.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar género por nome no Elasticsearch", e);
        }
    }

    @Override
    public Genre save(Genre genre) {
        try {
            GenreES genreES = mapper.toEntity(genre);
            IndexResponse response = client.index(i -> i
                    .index(INDEX)
                    .id(genreES.getId() != null ? genreES.getId().toString() : null)
                    .document(genreES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );

            return genre;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar género no Elasticsearch", e);
        }
    }

    @Override
    public List<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable) {
        return List.of();
    }



    @Override
    public void delete(Genre genre) {

    }



    // --- Estes métodos são apenas implementados no JPA ---
    // No Elasticsearch não há cálculos agregados ainda


    @Override
    public List<GenreLendingsDTO> getAverageLendingsInMonth(LocalDate month, pt.psoft.g1.psoftg1.shared.services.Page page) {
        return List.of();
    }

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsPerMonthLastYearByGenre() {
        return List.of();
    }

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsAverageDurationPerMonth(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }


}
