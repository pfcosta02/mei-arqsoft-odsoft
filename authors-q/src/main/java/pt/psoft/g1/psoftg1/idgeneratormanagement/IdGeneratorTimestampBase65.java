package pt.psoft.g1.psoftg1.idgeneratormanagement;

import java.security.SecureRandom;

public class IdGeneratorTimestampBase65 implements IdGenerator {

    private static final String BASE65_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateId() {
        long timestamp = System.currentTimeMillis();
        String base65Timestamp = longToBase65(timestamp);
        String randomSuffix = generateBase65Random(6);
        return base65Timestamp + randomSuffix;
    }

    private String longToBase65(long value) {
        if (value == 0) return BASE65_ALPHABET.substring(0, 1);
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.insert(0, BASE65_ALPHABET.charAt((int) (value % 65)));
            value /= 65;
        }
        return sb.toString(); // Comprimento variável (~8-10 chars para timestamps)
    }

    private String generateBase65Random(int length) {
        long randomValue = secureRandom.nextLong() & Long.MAX_VALUE;
        randomValue %= (long) Math.pow(65, length);
        return longToBase65(randomValue); // Reusa a função, mas limita a 'length' chars
    }
}