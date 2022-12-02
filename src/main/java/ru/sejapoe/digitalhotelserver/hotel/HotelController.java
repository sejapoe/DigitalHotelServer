package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HotelController {
    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping(value = "/hotel")
    public ResponseEntity<?> create(@RequestBody Hotel client) {
        hotelService.create(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/hotels")
    public List<Hotel> hotels() {
        return hotelService.readAll();
    }
}
