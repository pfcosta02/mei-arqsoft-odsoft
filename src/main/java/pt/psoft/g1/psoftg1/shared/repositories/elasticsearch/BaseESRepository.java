package pt.psoft.g1.psoftg1.shared.repositories.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

public abstract class BaseESRepository<T> {

    protected final ElasticsearchOperations elasticsearchOperations;
    protected final Class<T> entityClass;

    public BaseESRepository(ElasticsearchOperations elasticsearchOperations, Class<T> entityClass) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.entityClass = entityClass;

        var indexOps = elasticsearchOperations.indexOps(entityClass);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping();
        }
    }
}

