package ru.sejapoe.digitalhotelserver.core.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.List;

public abstract class BaseService<V extends BaseDocument> {
    private final MongoRepository<V, BigInteger> repository;

    public BaseService(MongoRepository<V, BigInteger> repository) {
        this.repository = repository;
    }

    public V create(V hotelInfo) {
        return repository.save(hotelInfo);
    }

    public List<V> readAll() {
        return repository.findAll();
    }

    public V read(BigInteger id) {
        return repository.findById(id).orElse(null);
    }

    public boolean update(V hotelInfo, BigInteger id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.save(hotelInfo);
        return true;
    }

    public boolean delete(BigInteger id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
