package ru.sejapoe.digitalhotelserver.user.session;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Pair<Session, String> decrypt(String data) throws Unauthorized, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String[] strings = data.split(":");
        BigInteger sessionId = new BigInteger(strings[0], 16);
        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) throw new Unauthorized();
        Session session = optionalSession.get();
        Cipher aes = Cipher.getInstance("RC4");
        Key key = new SecretKeySpec(session.getSessionKey().asByteArray(), "RC4");
        aes.init(Cipher.DECRYPT_MODE, key);
        byte[] decode = Base64.getDecoder().decode(strings[1].getBytes(StandardCharsets.UTF_8));
        byte[] bytes = aes.doFinal(decode);
        return Pair.of(session, new String(bytes, StandardCharsets.UTF_8));
    }

    public static class Unauthorized extends Exception {
    }
}
