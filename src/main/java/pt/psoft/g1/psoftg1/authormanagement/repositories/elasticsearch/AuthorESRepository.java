package pt.psoft.g1.psoftg1.authormanagement.repositories.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch.AuthorES;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorESMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingESMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class AuthorESRepository implements AuthorRepository {

    private final ElasticsearchClient client;
    private final AuthorESMapper mapper;

    private static final String INDEX = "authors";

    @Autowired
    public AuthorESRepository(ElasticsearchClient client, AuthorESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Optional<Author> findByAuthorNumber(String authorNumber) {
        try {
            SearchResponse<AuthorES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.term(t -> t.field("authorNumber").value(authorNumber))),
                    AuthorES.class
            );

            return response.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to search author by number in Elasticsearch", e);
        }
    }

    @Override
    public List<Author> searchByNameNameStartsWith(String name) {
        try {
            SearchResponse<AuthorES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .match(p -> p.field("name").query(name.toLowerCase()))
                            ),
                    AuthorES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search authors by name prefix", e);
        }
    }

    @Override
    public List<Author> searchByNameName(String name) {
        try {
            SearchResponse<AuthorES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .match(m -> m.field("name").query(name))
                            ),
                    AuthorES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search authors by name", e);
        }
    }

    @Override
    public Author save(Author author) {
        try {
            AuthorES authorES = mapper.toEntity(author);

            // IMPORTANTE: Usar o authorNumber como ID se existir, senão deixar o ES gerar
            String docId = author.getAuthorNumber() != null
                    ? String.valueOf(author.getAuthorNumber())
                    : null;

            client.index(IndexRequest.of(i -> {
                var builder = i.index(INDEX).document(authorES).refresh(co.elastic.clients.elasticsearch._types.Refresh.True);
                if (docId != null) {
                    builder.id(docId);
                }
                return builder;
            }));

            return author;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save author in Elasticsearch", e);
        }
    }

    @Override
    public Iterable<Author> findAll() {
        try {
            SearchResponse<AuthorES> response = client.search(s -> s
                            .index(INDEX)
                            .size(1000)
                            .sort(sort -> sort.field(f -> f.field("name.keyword").order(SortOrder.Asc))),
                    AuthorES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch all authors from Elasticsearch", e);
        }
    }

    @Override
    public List<AuthorLendingView> findTopAuthorByLendings(Pageable pageableRules) {
        return List.of();
    }


    @Override
    public void delete(Author author) {
        try {
            if (author.getAuthorNumber() != null) {
                client.delete(DeleteRequest.of(d -> d
                        .index(INDEX)
                        .id(String.valueOf(author.getAuthorNumber()))
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete author in Elasticsearch", e);
        }
    }

    @Override
    public List<Author> findCoAuthorsByAuthorNumber(String authorNumber) {
        return List.of();
    }




}