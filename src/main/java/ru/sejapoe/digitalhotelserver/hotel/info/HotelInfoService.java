package ru.sejapoe.digitalhotelserver.hotel.info;

import org.springframework.stereotype.Service;
import ru.sejapoe.digitalhotelserver.core.db.BaseService;

@Service
public class HotelInfoService extends BaseService<HotelInfo> {
    public HotelInfoService(HotelInfoRepository hotelInfoRepository) {
        super(hotelInfoRepository);
    }
}
