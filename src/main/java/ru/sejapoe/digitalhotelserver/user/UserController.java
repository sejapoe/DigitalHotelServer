package ru.sejapoe.digitalhotelserver.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sejapoe.digitalhotelserver.core.security.AuthorizationRequired;
import ru.sejapoe.digitalhotelserver.core.security.SessionServiceHolder;
import ru.sejapoe.digitalhotelserver.core.security.UserSession;
import ru.sejapoe.digitalhotelserver.user.session.Session;
import ru.sejapoe.digitalhotelserver.user.session.SessionService;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

@RestController
public class UserController implements SessionServiceHolder {
    private final UserService userService;
    private final SessionService sessionService;


    @Autowired
    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "/register/start")
    public ResponseEntity<?> registerStart(@RequestBody Pair<String, String> data) {
        try {
            return new ResponseEntity<>(userService.startRegistration(data).asBase64(), HttpStatus.OK);
        } catch (UserService.UserAlreadyExists e) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
    }

    @PostMapping(value = "/register/finish")
    public ResponseEntity<?> registerFinish(@RequestBody Pair<String, BigInteger> data) {
        userService.finishRegistration(data);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login/start")
    public ResponseEntity<?> loginStart(@RequestBody Pair<String, BigInteger> data) {
        try {
            return new ResponseEntity<>(userService.login(data), HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserService.NoSuchUser e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/login/finish")
    public ResponseEntity<?> loginFinish(@RequestBody String data) {
        try {
            return new ResponseEntity<>(userService.confirm(data), HttpStatus.OK);
        } catch (UserService.WrongPasswordException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @AuthorizationRequired
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@UserSession Session session) {
        sessionService.endSession(session);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public SessionService getSessionService() {
        return sessionService;
    }
}
