package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfo;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfoService;

import java.util.List;

@RestController
public class HotelController {
    private final HotelInfoService hotelInfoService;
    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelInfoService hotelInfoService, HotelService hotelService) {
        this.hotelInfoService = hotelInfoService;
        this.hotelService = hotelService;
    }

    @PostMapping(value = "/hotel")
    public ResponseEntity<?> create(@RequestBody HotelInfo client) {
        HotelInfo info = hotelInfoService.create(client);
        hotelService.create(new Hotel(info));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/hotels")
    public List<Hotel> hotels() {
        return hotelService.readAll();
    }
}
