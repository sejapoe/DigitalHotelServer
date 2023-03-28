package ru.sejapoe.digitalhotelserver.user.session;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.Key;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Key getKey(String rawSessionId) {
        BigInteger sessionId = new BigInteger(rawSessionId, 16);
        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) return null;
        Session session = optionalSession.get();
        return Keys.hmacShaKeyFor(session.getSessionKey().asByteArray());
    }

    public void endSession(Session session) {
        sessionRepository.delete(session);
    }

    public Session getSession(String rawSessionId) {
        BigInteger sessionId = new BigInteger(rawSessionId, 16);
        return sessionRepository.findById(sessionId).orElse(null);
    }
}
