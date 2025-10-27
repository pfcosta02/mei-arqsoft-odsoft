package pt.psoft.g1.psoftg1.shared.repositories;

import java.util.Optional;

public interface CacheRepository<T, K> {
    Optional<T> findById(K key);
    void save(T entity, K key);
    void delete(K key);
}
