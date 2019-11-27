package com.studybuddy.repositories;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthenticationRepository {

    // Don't make this private. It is used by UserRepo to create users
    public static final int HASHING_ITERATION_COUNT = 65536;
    public static final int HASHING_KEY_LENGTH = 128;

    public static int authenticateUser(Connection connection, String email, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Search for the user based on their email
        var statement = connection.prepareStatement("SELECT id, hashedPassword, hashSalt FROM users WHERE email = ?");
        statement.setString(1, email);
        var result = statement.executeQuery();
        boolean userFound = false;
        String storedHashedPassword = "";
        String hashedPassword = "";
        var id = 0;
        while (result.next()) {
            storedHashedPassword = result.getString("hashedPassword");
            var salt = hexToBytes(result.getString("hashSalt"));
            // Hash password an compare to stored value
            assert password != null;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASHING_ITERATION_COUNT, HASHING_KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            hashedPassword = bytesToHex(hash);
            id = result.getInt("id");
            userFound = true;
        }
        result.close();
        statement.close();
        // If user is not found or password doesn't match, return 0 (indicates no user)
        if (!userFound || (!hashedPassword.equals(storedHashedPassword))) {
            return 0;
        } else {
            // Return the user's id to access their events
            return id;
        }
    }

    // Helper function to convert byte array to a hex string
    // Don't make this private. It is used by UserRepo to create users
    public static String bytesToHex(byte[] byteArray) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuilder.append(byteToHex(b));
        }
        return hexStringBuilder.toString();
    }

    // Helper to convert individual byte to string
    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    // Helper function to convert hex string to byte array
    private static byte[] hexToBytes(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
}
