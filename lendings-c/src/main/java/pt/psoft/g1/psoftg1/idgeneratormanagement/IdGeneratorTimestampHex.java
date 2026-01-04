package pt.psoft.g1.psoftg1.idgeneratormanagement;

import java.security.SecureRandom;

public class IdGeneratorTimestampHex implements IdGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        byte[] randomBytes = new byte[3]; // 3 bytes = 6 hex digits
        secureRandom.nextBytes(randomBytes);
        String hexSuffix = bytesToHex(randomBytes).toUpperCase(); // 6 chars hex
        return timestamp + hexSuffix;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}