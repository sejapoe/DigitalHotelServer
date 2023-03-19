package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sejapoe.digitalhotelserver.core.security.SessionEncrypted;
import ru.sejapoe.digitalhotelserver.core.security.SessionServiceHolder;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfo;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfoService;
import ru.sejapoe.digitalhotelserver.user.User;
import ru.sejapoe.digitalhotelserver.user.session.SessionService;

import java.util.Collection;
import java.util.List;

@RestController
public class HotelController implements SessionServiceHolder {
    private final HotelInfoService hotelInfoService;
    private final HotelService hotelService;
    private final SessionService sessionService;

    @Autowired
    public HotelController(HotelInfoService hotelInfoService, HotelService hotelService, SessionService sessionService) {
        this.hotelInfoService = hotelInfoService;
        this.hotelService = hotelService;
        this.sessionService = sessionService;
    }

    @SessionEncrypted
    @PostMapping(value = "/hello")
    public ResponseEntity<?> hello(@RequestBody User user, @RequestBody Collection<Number> data) {
        System.out.println(user.getUsername() + " " + data);
        return new ResponseEntity<>("Hello", HttpStatus.OK);
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

    @Override
    public SessionService getSessionService() {
        return sessionService;
    }
}
