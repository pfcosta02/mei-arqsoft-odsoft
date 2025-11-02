package pt.psoft.g1.psoftg1.shared.repositories.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.ForbiddenNameESMapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.ForbiddenNameES;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class ForbiddenNameESRepository implements ForbiddenNameRepository {

    private final ElasticsearchClient client;
    private final ForbiddenNameESMapper mapper;
    private static final String INDEX = "forbiddennames";

    @Autowired
    public ForbiddenNameESRepository(ElasticsearchClient client, ForbiddenNameESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        // Criar índice se não existir
        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Iterable<ForbiddenName> findAll() {
        try {
            SearchResponse<ForbiddenNameES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.matchAll(m -> m))
                            .size(1000),
                    ForbiddenNameES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar todos os ForbiddenNames no Elasticsearch", e);
        }
    }

    @Override
    public List<ForbiddenName> findByForbiddenNameIsContained(String pat) {
        try {
            // Busca todos os forbidden names e filtra aqueles que estão contidos no padrão
            SearchResponse<ForbiddenNameES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.matchAll(m -> m))
                            .size(1000),
                    ForbiddenNameES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .filter(fn -> pat != null && pat.toLowerCase().contains(fn.getForbiddenName().toLowerCase()))
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar ForbiddenNames contidos no padrão no Elasticsearch", e);
        }
    }

    @Override
    public ForbiddenName save(ForbiddenName forbiddenName) {
        try {
            ForbiddenNameES forbiddenNameES = mapper.toEntity(forbiddenName);

            // Buscar se já existe um documento com esse forbiddenName
            SearchResponse<ForbiddenNameES> existingSearch = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .term(t -> t
                                            .field("forbiddenName.keyword")
                                            .value(forbiddenName.getForbiddenName())))
                            .size(1),
                    ForbiddenNameES.class);

            String documentId;
            if (!existingSearch.hits().hits().isEmpty()) {
                documentId = existingSearch.hits().hits().get(0).id();
            } else {
                documentId = null;
            }

            IndexResponse response = client.index(i -> i
                    .index(INDEX)
                    .id(documentId != null ? documentId : null)
                    .document(forbiddenNameES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True));

            return forbiddenName;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar ForbiddenName no Elasticsearch", e);
        }
    }

    @Override
    public Optional<ForbiddenName> findByForbiddenName(String forbiddenName) {
        try {
            SearchResponse<ForbiddenNameES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .term(t -> t
                                            .field("forbiddenName.keyword")
                                            .value(forbiddenName)))
                            .size(1),
                    ForbiddenNameES.class);

            return search.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar ForbiddenName no Elasticsearch", e);
        }
    }

    @Override
    public int deleteForbiddenName(String forbiddenName) {
        try {
            DeleteByQueryResponse response = client.deleteByQuery(d -> d
                    .index(INDEX)
                    .query(q -> q
                            .term(t -> t
                                    .field("forbiddenName.keyword")
                                    .value(forbiddenName))));

            return response.deleted().intValue();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar ForbiddenName no Elasticsearch", e);
        }
    }
}