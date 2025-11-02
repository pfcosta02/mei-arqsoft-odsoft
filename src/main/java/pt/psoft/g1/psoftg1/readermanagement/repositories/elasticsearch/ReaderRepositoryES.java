package pt.psoft.g1.psoftg1.readermanagement.repositories.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.impl.mappers.ReaderDetailsESMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.ReaderDetailsES;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.shared.services.Page;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.json.JsonData;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperES;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class ReaderRepositoryES implements ReaderRepository {

    private final ElasticsearchClient client;
    private final ReaderDetailsESMapper mapper;
    private static final String INDEX = "reader_details";

    public ReaderRepositoryES(ElasticsearchClient client, ReaderDetailsESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber) {
        try {
            SearchResponse<ReaderDetailsES> response = client.search(s -> s
                            .index(INDEX)
                            .size(1)
                            .query(q -> q
                                    .term(t -> t
                                            .field("readerNumber.keyword")
                                            .value(readerNumber))),
                    ReaderDetailsES.class);
            return response.hits().hits().stream()
                    .findFirst()
                    .map(hit -> mapper.toModel(hit.source()));
        } catch (IOException e) {
            throw new RuntimeException("Error searching reader by number", e);
        }
    }

    @Override
    public List<ReaderDetails> findByPhoneNumber(String phoneNumber) {
        try {
            SearchResponse<ReaderDetailsES> response = client.search(s -> s
                            .index(INDEX)
                            .size(50)
                            .query(q -> q.term(t -> t.field("phoneNumber.keyword").value(phoneNumber))),
                    ReaderDetailsES.class);
            return response.hits().hits().stream()
                    .map(hit -> mapper.toModel(hit.source()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error searching readers by phone", e);
        }
    }

    @Override
    public Optional<ReaderDetails> findByUsername(String username) {
        try {
            SearchResponse<ReaderDetailsES> response = client.search(s -> s
                            .index(INDEX)
                            .size(1)
                            .query(q -> q.term(t -> t.field("reader.username").value(username))),
                    ReaderDetailsES.class);
            return response.hits().hits().stream()
                    .findFirst()
                    .map(hit -> mapper.toModel(hit.source()));
        } catch (IOException e) {
            throw new RuntimeException("Error searching reader by username", e);
        }
    }

    @Override
    public Optional<ReaderDetails> findByUserId(String userId) {
        try {
            SearchResponse<ReaderDetailsES> response = client.search(s -> s
                            .index(INDEX)
                            .size(1)
                            .query(q -> q.term(t -> t.field("reader.id").value(userId))),
                    ReaderDetailsES.class);
            return response.hits().hits().stream()
                    .findFirst()
                    .map(hit -> mapper.toModel(hit.source()));
        } catch (IOException e) {
            throw new RuntimeException("Error searching reader by userId", e);
        }
    }


    @Override
    public int getCountFromCurrentYear() {
        try {
            LocalDate start = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            LocalDate end = LocalDate.of(LocalDate.now().getYear(), 12, 31);

            CountResponse response = client.count(c -> c
                    .index(INDEX)
                    .query(q -> q
                            .range(r -> r
                                    .field("reader.createdAt")
                                    .gte(JsonData.fromJson(start.toString()))
                                    .lte(JsonData.fromJson(end.toString())))));
            return (int) response.count();
        } catch (IOException e) {
            throw new RuntimeException("Error counting readers from current year", e);
        }
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails) {
        try {
            ReaderDetailsES es = mapper.toEntity(readerDetails);
            client.index(i -> i.index(INDEX).id(es.getId()).document(es).refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );

            return readerDetails;
        } catch (IOException e) {
            throw new RuntimeException("Error saving reader", e);
        }
    }

    @Override
    public Iterable<ReaderDetails> findAll() {
        // Simplificado, cuidado com grandes volumes
        try {
            SearchResponse<ReaderDetailsES> response = client.search(s -> s.index(INDEX).size(1000), ReaderDetailsES.class);
            return response.hits().hits().stream()
                    .map(hit -> mapper.toModel(hit.source()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error fetching all readers", e);
        }
    }

    @Override
    public List<ReaderDetails> findTopReaders(Pageable pageable) {
        return List.of();
    }

    @Override
    public List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public void delete(ReaderDetails readerDetails) {
        try {
            client.delete(d -> d.index(INDEX).id(readerDetails.getReaderNumber()));
        } catch (IOException e) {
            throw new RuntimeException("Error deleting reader", e);
        }
    }

    @Override
    public List<ReaderDetails> searchReaderDetails(Page page, SearchReadersQuery query) {
        try {
            List<String> filters = new ArrayList<>();
            List<Query> queries = new ArrayList<>();

            if (query.getName() != null && !query.getName().isBlank()) {
                queries.add(Query.of(q -> q.match(m -> m.field("reader.name").query(query.getName()))));
            }
            if (query.getEmail() != null && !query.getEmail().isBlank()) {
                queries.add(Query.of(q -> q.term(t -> t.field("reader.username.keyword").value(query.getEmail()))));
            }
            if (query.getPhoneNumber() != null && !query.getPhoneNumber().isBlank()) {
                queries.add(Query.of(q -> q.term(t -> t.field("phoneNumber.keyword").value(query.getPhoneNumber()))));
            }

            SearchResponse<ReaderDetailsES> response = client.search(s -> s
                            .index(INDEX)
                            .from((page.getNumber() - 1) * page.getLimit())
                            .size(page.getLimit())
                            .query(q -> q.bool(b -> b.should(queries))),
                    ReaderDetailsES.class);

            return response.hits().hits().stream()
                    .map(hit -> mapper.toModel(hit.source()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error searching readers", e);
        }
    }


}
