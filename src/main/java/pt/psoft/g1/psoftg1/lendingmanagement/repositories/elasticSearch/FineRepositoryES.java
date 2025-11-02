package pt.psoft.g1.psoftg1.lendingmanagement.repositories.elasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.FineESMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch.FineES;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class FineRepositoryES implements FineRepository {

    private final ElasticsearchClient client;
    private final FineESMapper mapper;
    private static final String INDEX = "fines";

    @Autowired
    public FineRepositoryES(ElasticsearchClient client, FineESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Optional<Fine> findByLendingNumber(String lendingNumber) {
        try {
            SearchResponse<FineES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .term(t -> t
                                            .field("lending.lendingNumber.lendingNumber")
                                            .value(lendingNumber)))
                            .size(1),
                    FineES.class);

            return search.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar fine por lending number no Elasticsearch", e);
        }
    }

    @Override
    public Iterable<Fine> findAll() {
        try {
            SearchResponse<FineES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.matchAll(m -> m))
                            .size(1000),
                    FineES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao obter todas as multas do Elasticsearch", e);
        }
    }

    @Override
    public Fine save(Fine fine) {
        try {
            FineES fineES = mapper.toEntity(fine);
            IndexResponse response = client.index(i -> i
                    .index(INDEX)
                    .id(fineES.getId() != null ? fineES.getId() : null)
                    .document(fineES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );

            return fine;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar fine no Elasticsearch", e);
        }
    }
}