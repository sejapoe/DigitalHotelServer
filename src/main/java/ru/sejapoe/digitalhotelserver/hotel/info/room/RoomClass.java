package ru.sejapoe.digitalhotelserver.hotel.info.room;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record RoomClass(String name, int count, double price, int priority) {
}
