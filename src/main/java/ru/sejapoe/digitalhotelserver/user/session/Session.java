package ru.sejapoe.digitalhotelserver.user.session;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.sejapoe.digitalhotelserver.core.db.BaseDocument;
import ru.sejapoe.digitalhotelserver.core.security.BitArray256;
import ru.sejapoe.digitalhotelserver.user.User;

import java.time.LocalDateTime;

@Document
public class Session extends BaseDocument {
    @DBRef
    private User user;
    private BitArray256 sessionKey;
    @CreatedDate
    private LocalDateTime createdAt;

    public Session(User user, BitArray256 sessionKey) {
        this.user = user;
        this.sessionKey = sessionKey;
        this.createdAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public BitArray256 getSessionKey() {
        return sessionKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
