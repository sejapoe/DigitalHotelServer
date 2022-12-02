package ru.sejapoe.digitalhotelserver.core.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface BaseRepository<T> extends MongoRepository<T, BigInteger> {
}
