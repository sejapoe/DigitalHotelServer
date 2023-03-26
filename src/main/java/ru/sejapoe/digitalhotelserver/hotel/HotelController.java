package ru.sejapoe.digitalhotelserver.hotel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sejapoe.digitalhotelserver.core.security.AuthorizationRequired;
import ru.sejapoe.digitalhotelserver.core.security.SessionServiceHolder;
import ru.sejapoe.digitalhotelserver.hotel.info.HotelInfoService;
import ru.sejapoe.digitalhotelserver.user.session.SessionService;

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

    @AuthorizationRequired
    @GetMapping(value = "/hello")
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("hello", HttpStatus.OK);
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
