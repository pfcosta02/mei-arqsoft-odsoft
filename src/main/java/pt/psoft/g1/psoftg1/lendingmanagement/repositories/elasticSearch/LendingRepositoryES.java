package pt.psoft.g1.psoftg1.lendingmanagement.repositories.elasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingESMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch.LendingES;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;
import co.elastic.clients.elasticsearch._types.SortOrder;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class LendingRepositoryES implements LendingRepository {

    private final ElasticsearchClient client;
    private final LendingESMapper mapper;
    private static final String INDEX = "lendings";

    @Autowired
    public LendingRepositoryES(ElasticsearchClient client, LendingESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Optional<Lending> findByLendingNumber(String lendingNumber) {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .term(t -> t
                                            .field("lendingNumber.lendingNumber")
                                            .value(lendingNumber)))
                            .size(1),
                    LendingES.class);

            return search.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar lending por número no Elasticsearch", e);
        }
    }

    @Override
    public List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn) {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m1 -> m1.term(t -> t.field("readerDetails.readerNumber.readerNumber").value(readerNumber)))
                                            .must(m2 -> m2.term(t -> t.field("book.isbn.isbn").value(isbn)))))
                            .size(1000),
                    LendingES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar lendings por reader e isbn no Elasticsearch", e);
        }
    }

    @Override
    public int getCountFromCurrentYear() {
        try {
            int currentYear = LocalDate.now().getYear();
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .range(r -> r
                                            .field("startDate")
                                            .gte(JsonData.fromJson(currentYear + "-01-01"))
                                            .lt(JsonData.fromJson((currentYear + 1) + "-01-01"))))
                            .size(0),
                    LendingES.class);

            return (int) search.hits().total().value();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao contar lendings do ano atual no Elasticsearch", e);
        }
    }

    @Override
    public List<Lending> listOutstandingByReaderNumber(String readerNumber) {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m.term(t -> t.field("readerDetails.readerNumber.readerNumber").value(readerNumber)))
                                            .must(m -> m.term(t -> t.field("returnedDate").value((FieldValue) null)))))
                            .size(1000),
                    LendingES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar lendings pendentes no Elasticsearch", e);
        }
    }

    @Override
    public Double getAverageDuration() {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .aggregations("avg_duration", a -> a
                                    .avg(ag -> ag.field("durationInDays")))
                            .size(0),
                    LendingES.class);

            return search.aggregations()
                    .get("avg_duration")
                    .avg()
                    .value();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao calcular duração média no Elasticsearch", e);
        }
    }

    @Override
    public Double getAvgLendingDurationByIsbn(String isbn) {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.term(t -> t.field("book.isbn.isbn").value(isbn)))
                            .aggregations("avg_duration_by_isbn", a -> a
                                    .avg(ag -> ag.field("durationInDays")))
                            .size(0),
                    LendingES.class);

            return search.aggregations()
                    .get("avg_duration_by_isbn")
                    .avg()
                    .value();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao calcular duração média por ISBN no Elasticsearch", e);
        }
    }

    @Override
    public List<Lending> getOverdue(Page page) {
        try {
            SearchResponse<LendingES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .mustNot(mn -> mn.exists(e -> e.field("returnedDate")))
                                            .must(m -> m.range(r -> r
                                                    .field("limitDate")
                                                    .format("yyyy-MM-dd")
                                                    .lt(JsonData.of(LocalDate.now().toString()))))

                                    ))
                            .from((page.getNumber() - 1) * page.getLimit())
                            .size(page.getLimit())
                            .sort(so -> so.field(f -> f
                                    .field("limitDate")
                                    .order(SortOrder.Asc))),
                    LendingES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar lendings vencidos no Elasticsearch", e);
        }
    }

    @Override
    public List<Lending> searchLendings(Page page, String readerNumber, String isbn, Boolean returned, LocalDate startDate, LocalDate endDate) {
        try {
            SearchResponse<LendingES> search = client.search(s -> {
                s.index(INDEX);

                // Build query with multiple conditions
                s.query(q -> q.bool(b -> {
                    if (readerNumber != null && !readerNumber.isEmpty()) {
                        b.must(m -> m.wildcard(w -> w.field("readerDetails.readerNumber.readerNumber").value("*" + readerNumber + "*")));
                    }
                    if (isbn != null && !isbn.isEmpty()) {
                        b.must(m -> m.wildcard(w -> w.field("book.isbn.isbn").value("*" + isbn + "*")));
                    }
                    if (returned != null) {
                        if (returned) {
                            b.must(m -> m.exists(e -> e.field("returnedDate")));
                        } else {
                            b.mustNot(m -> m.exists(e -> e.field("returnedDate")));
                        }
                    }
                    if (startDate != null) {
                        b.must(m -> m.range(r -> r.field("startDate").gte(JsonData.fromJson(startDate.toString()))));
                    }
                    if (endDate != null) {
                        b.must(m -> m.range(r -> r.field("startDate").lte(JsonData.fromJson(endDate.toString()))));
                    }
                    return b;
                }));

                s.from((page.getNumber() - 1) * page.getLimit());
                s.size(page.getLimit());
                s.sort(so -> so.field(f -> f.field("lendingNumber").order(SortOrder.Asc)));

                return s;
            }, LendingES.class);

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao pesquisar lendings no Elasticsearch", e);
        }
    }

    @Override
    public Lending save(Lending lending) {
        try {
            LendingES lendingES = mapper.toEntity(lending);
            IndexResponse response = client.index(i -> i
                    .index(INDEX)
                    .id(lendingES.getId() != null ? lendingES.getId() : null)
                    .document(lendingES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );

            return lending;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar lending no Elasticsearch", e);
        }
    }

    @Override
    public void delete(Lending lending) {
        try {
            LendingES lendingES = mapper.toEntity(lending);
            client.delete(d -> d
                    .index(INDEX)
                    .id(lendingES.getId()));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao eliminar lending no Elasticsearch", e);
        }
    }
}