package pt.psoft.g1.psoftg1.idgeneratormanagement;

import java.security.SecureRandom;

public class IdGeneratorBase65Random implements IdGenerator {

    private static final String BASE65_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static final int ID_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateId() {
        long randomValue = secureRandom.nextLong() & Long.MAX_VALUE;
        randomValue %= (long) Math.pow(65, ID_LENGTH);
        return longToBase65(randomValue, ID_LENGTH);
    }

    private String longToBase65(long value, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.insert(0, BASE65_ALPHABET.charAt((int) (value % 65)));
            value /= 65;
        }
        return sb.toString();
    }
}