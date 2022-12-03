package ru.sejapoe.digitalhotelserver.core.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;

@NoRepositoryBean
public interface BaseRepository<T> extends MongoRepository<T, BigInteger> {
}
