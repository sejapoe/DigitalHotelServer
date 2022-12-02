package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.sejapoe.digitalhotelserver.core.db.BaseDocument;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfo;

@Document
public final class Hotel extends BaseDocument {
    @DBRef
    @Field(name = "info")
    private HotelInfo hotelInfo;

    public Hotel(HotelInfo hotelInfo) {
        this.hotelInfo = hotelInfo;
    }

    public HotelInfo getHotelInfo() {
        return hotelInfo;
    }

    public void setHotelInfo(HotelInfo hotelInfo) {
        this.hotelInfo = hotelInfo;
    }
}
