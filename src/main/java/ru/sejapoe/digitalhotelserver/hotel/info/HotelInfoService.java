package ru.sejapoe.digitalhotelserver.hotel.info;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelInfoService {
    private final HotelInfoRepository hotelRepository;

    public HotelInfoService(HotelInfoRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void create(HotelInfo hotelInfo) {
        hotelRepository.save(hotelInfo);
    }

    public List<HotelInfo> readAll() {
        return hotelRepository.findAll();
    }

    public HotelInfo read(long id) {
        return hotelRepository.getReferenceById(id);
    }

    public boolean update(HotelInfo hotelInfo, long id) {
        if (!hotelRepository.existsById(id)) {
            return false;
        }

        hotelInfo.setId(id);
        hotelRepository.save(hotelInfo);
        return true;
    }

    public boolean delete(long id) {
        if (!hotelRepository.existsById(id)) {
            return false;
        }

        hotelRepository.deleteById(id);
        return true;
    }
}
