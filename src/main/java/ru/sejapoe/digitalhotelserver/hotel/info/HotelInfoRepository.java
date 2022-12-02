package ru.sejapoe.digitalhotelserver.hotel.info;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HotelInfoRepository extends MongoRepository<HotelInfo, Long> {
}
