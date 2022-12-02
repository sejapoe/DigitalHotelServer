package ru.sejapoe.digitalhotelserver.hotel.info;

import jakarta.persistence.*;
import ru.sejapoe.digitalhotelserver.hotel.info.room.RoomClass;
import ru.sejapoe.digitalhotelserver.hotel.info.room.RoomClassConverter;

import java.util.List;

@Entity
@Table(name = "hotels_info")
public final class HotelInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;

    @Column(name = "rooms")
    @Convert(converter = RoomClassConverter.class)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
