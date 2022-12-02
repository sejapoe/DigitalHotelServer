package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.stereotype.Service;
import ru.sejapoe.digitalhotelserver.core.db.BaseService;

@Service
public class HotelService extends BaseService<Hotel> {
    public HotelService(HotelRepository hotelRepository) {
        super(hotelRepository);
    }
}
