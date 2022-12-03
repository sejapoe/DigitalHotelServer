package ru.sejapoe.digitalhotelserver.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.sejapoe.digitalhotelserver.core.security.BitArray256;

import java.math.BigInteger;

@Document
public class User {
    private final BitArray256 salt;
    private final BigInteger verifier;
    @Id
    private String username;

    public User(String username, BitArray256 salt, BigInteger verifier) {
        this.username = username;
        this.salt = salt;
        this.verifier = verifier;
    }

    public String getUsername() {
        return username;
    }

    public BitArray256 getSalt() {
        return salt;
    }

    public BigInteger getVerifier() {
        return verifier;
    }
}
