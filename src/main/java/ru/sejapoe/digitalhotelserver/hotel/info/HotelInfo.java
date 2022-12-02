package ru.sejapoe.digitalhotelserver.hotel.info;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.sejapoe.digitalhotelserver.hotel.info.room.RoomClass;

import java.util.List;

@Document
public final class HotelInfo {
    @Field(name = "name")
    private String name;

    @Field(name = "rooms")
    private List<RoomClass> rooms;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoomClass> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomClass> rooms) {
        this.rooms = rooms;
    }
}
