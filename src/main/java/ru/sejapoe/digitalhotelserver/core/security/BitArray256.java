package ru.sejapoe.digitalhotelserver.core.security;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public final class BitArray256 {
    private byte[] arr;

    public BitArray256() {
        arr = new byte[32];
    }

    private BitArray256(byte[] bytes) {
        arr = bytes;
    }

    public static BitArray256 fromBigInteger(BigInteger source) {
        return fromByteArray(source.toByteArray());
    }

    public static BitArray256 fromByteArray(byte[] source) {
        byte[] bytes = new byte[32];
        if (source.length != 0) {
            System.arraycopy(source, 0, bytes, Integer.max(32 - source.length, 0), Integer.min(source.length, 32));
        }
        return new BitArray256(bytes);
    }

    public static BitArray256 fromHexString(String source) {
        return fromBigInteger(new BigInteger(source, 16));
    }

    public byte[] asByteArray() {
        return arr;
    }

    public BigInteger asBigInteger() {
        StringBuilder result = new StringBuilder();
        for (byte b : arr) {
            result.append(Integer.toHexString(b));
        }
        return new BigInteger(result.toString(), 16);
    }

    public String asHexString() {
        return asBigInteger().toString(16);
    }
}
