package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void create(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public List<Hotel> readAll() {
        return hotelRepository.findAll();
    }

    public Hotel read(long id) {
        return hotelRepository.getReferenceById(id);
    }

    public boolean update(Hotel hotel, long id) {
        if (!hotelRepository.existsById(id)) {
            return false;
        }

        hotel.setId(id);
        hotelRepository.save(hotel);
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
