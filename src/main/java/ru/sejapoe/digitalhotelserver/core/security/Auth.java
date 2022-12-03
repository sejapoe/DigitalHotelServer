package ru.sejapoe.digitalhotelserver.core.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Auth {
    public static final BigInteger g = BigInteger.valueOf(2);
    public static final BigInteger N = new BigInteger("EEAF0AB9ADB38DD69C33F80AA8FC5E86072618775FF3C0B9EA2314C9C256576D674DF7", 16);
    public static final BigInteger k;

    static {
        try {
            k = hash(g.toByteArray(), N.toByteArray()).asBigInteger();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger modPow(BitArray256 bitArray256) {
        return g.modPow(bitArray256.asBigInteger(), N);
    }

    public static BitArray256 random256() {
        byte[] salt = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return BitArray256.fromByteArray(salt);
    }

    public static byte[] concatByteArrays(byte[]... arr) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] bytes : arr) {
            outputStream.write(bytes);
        }
        return outputStream.toByteArray();
    }

    public static BitArray256 xorByteArrays(BitArray256 a, BitArray256 b) {
        byte[] res = new byte[32];
        for (int i = 0; i < 32; i++) {
            res[i] = (byte) (a.asByteArray()[i] ^ b.asByteArray()[i]);
        }
        return BitArray256.fromByteArray(res);
    }

    public static BitArray256 hash(byte[] bytes) throws NoSuchAlgorithmException {
        return BitArray256.fromByteArray(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    public static BitArray256 hash(byte[] bytes, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt);
        return BitArray256.fromByteArray(messageDigest.digest(bytes));
    }

}