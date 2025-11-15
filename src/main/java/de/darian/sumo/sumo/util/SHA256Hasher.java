package de.darian.sumo.sumo.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hasher {

    /**
     * Hash a file. The path has to be relative to the working directory!!
     * @param path The realtive path. Example: src/main/java/.../Main.java
     * @return The hash
     */
    public static String hashFileWithPath(String path) {
        // Path has to be relative!!
        path = path.replace("\\", "/");
        Path filePath = Path.of(path);
        if (filePath.isAbsolute()) {
            System.out.println("Path must be a relative path. Returning null.");
            return null;
        }

        // Get SHA-256 algo
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm. Error: " + e.getMessage());
            return null;
        }

        // hash the path first
        digest.update(path.getBytes(StandardCharsets.UTF_8));

        // hash the content next
        // The bytes are read with input stream and a buffer of 8KB to reduce RAM usage if the file is large
        int bufferSize = 8192;
        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        } catch (IOException e) {
            System.out.println("Could not read file. Error: " + e.getMessage());
        }

        byte[] hashBytes = digest.digest();

        // Convert hash to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
