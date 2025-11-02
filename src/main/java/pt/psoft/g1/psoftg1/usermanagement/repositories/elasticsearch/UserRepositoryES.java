package pt.psoft.g1.psoftg1.usermanagement.repositories.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperES;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.UserES;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class UserRepositoryES implements UserRepository {

    private static final String INDEX = "users"; // nome do índice no Elasticsearch

    private final ElasticsearchClient client;
    private final UserMapperES mapper;


    @Autowired
    public UserRepositoryES(ElasticsearchClient client, UserMapperES mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public <S extends User> S save(S entity) {
        UserES userES = mapper.toElasticsearch(entity);

        try {
            IndexResponse response = client.index(i -> i
                    .index(INDEX)
                    .id(userES.getId() != null ? userES.getId() : UUID.randomUUID().toString())
                    .document(userES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            );
            userES.setId(response.id());
            return (S) mapper.toEntity(userES);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar UserES no Elasticsearch", e);
        }
    }

    @Override
    public Optional<User> findById(String objectId) {
        try {
            GetResponse<UserES> response = client.get(g -> g
                            .index(INDEX)
                            .id(objectId.toString()),
                    UserES.class
            );
            if (response.found()) {
                return Optional.of(mapper.toEntity(response.source()));
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar UserES por ID", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            SearchResponse<UserES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.term(m -> m.field("username").value(username))),
                    UserES.class
            );

//            System.out.println(search.hits().hits().size());

            return search.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toEntity);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar UserES por username", e);
        }
    }

    @Override
    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        try {
            // construção dinâmica da query
            List<Query> mustQueries = new ArrayList<>();

            if (StringUtils.hasText(query.getUsername())) {
                mustQueries.add(Query.of(q -> q
                        .term(t -> t.field("username").value(query.getUsername()))));
            }

            if (StringUtils.hasText(query.getFullName())) {
                mustQueries.add(Query.of(q -> q
                        .match(m -> m.field("fullName").query(query.getFullName()))));
            }

            SearchResponse<UserES> search = client.search(s -> s
                            .index(INDEX)
                            .from((page.getNumber() - 1) * page.getLimit())
                            .size(page.getLimit())
                            .query(q -> q.bool(b -> b.must(mustQueries))),
                    UserES.class
            );

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toEntity)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao realizar pesquisa de utilizadores", e);
        }
    }

    @Override
    public List<User> findByNameName(String name) {
        return findByNameField("name.keyword", name);
    }

    @Override
    public List<User> findByNameNameContains(String name) {
        return findByNameField("name", name);
    }

    private List<User> findByNameField(String field, String name) {
        try {
            SearchResponse<UserES> search = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.match(m -> m.field(field).query(name))),
                    UserES.class
            );

            return search.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toEntity)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao procurar utilizadores por nome", e);
        }
    }

    @Override
    public void delete(User user) {
        try {
            client.delete(d -> d
                    .index(INDEX)
                    .id(String.valueOf(user.getId()))
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao eliminar UserES", e);
        }
    }
}
