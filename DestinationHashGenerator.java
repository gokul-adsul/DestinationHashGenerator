package com.example.destinationhash;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            // Read JSON file
            JSONObject jsonObject = readJsonFile(jsonFilePath);

            // Find the value associated with "destination"
            String destinationValue = findDestinationValue(jsonObject);

            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in JSON file.");
                System.exit(1);
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate strings and compute MD5 hash
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = computeMd5Hash(concatenatedString);

            // Print the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject readJsonFile(String filePath) throws Exception {
        try (InputStream is = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(is);
            return new JSONObject(tokener);
        }
    }

    private static String findDestinationValue(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if ("destination".equals(key)) {
                    return value.toString();
                }
                String nestedValue = findDestinationValue(value);
                if (nestedValue != null) {
                    return nestedValue;
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (Object element : jsonArray) {
                String nestedValue = findDestinationValue(element);
                if (nestedValue != null) {
                    return nestedValue;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String computeMd5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
