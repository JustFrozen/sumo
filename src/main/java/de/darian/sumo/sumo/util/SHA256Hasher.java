package de.darian.sumo.sumo.util;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
public class SHA256Hasher {

    /**
     * Hash a file, including its path. The path has to be relative to the working directory!!
     * @param path The relative path. Example: src/main/java/.../Main.java
     * @return The hash
     */
    public static byte[] hashFileWithPath(String path) {
        // Path has to be relative!!
        path = path.replace("\\", "/");
        Path filePath = Path.of(path);
        if (filePath.isAbsolute()) {
            System.out.println("Path must be a relative path. Returning null.");
            return null;
        }

        // Get SHA-256 algo
        final MessageDigest digest = createSHA256Digest();
        if (digest == null) return null;

        // input the path first
        digest.update(hashPathName(filePath));

        // input the content next
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

        // this hashes the input and resets the digest
        return digest.digest();
    }


    /**
     * Hash a directory, including its path name, its files and its subdirectories (recursively). The path has to be relative to the working directory!!
     * @param path The relative path. Example: src/main/java/.../util
     * @return The hash
     */
    public static byte[] hashDirectory(String path) {
        // Path has to be relative!!
        Path dirPath = Path.of(path);
        if (dirPath.isAbsolute()) {
            System.out.println("Path must be a relative path. Returning null.");
            return null;
        }

        // Get SHA-256 algo, this is created only once and then passed on to be cloned for optimized performance
        final MessageDigest digest = createSHA256Digest();
        if (digest == null) return null;

        return hashDirRecursive(dirPath, digest);
    }

    private static byte[] hashDirRecursive(Path currentPath, MessageDigest digest) {
        // get all first level paths in directory with a DirectoryStream
        ArrayList<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
            stream.forEach(e -> entries.add(Path.of(e.toString().replace("\\", "/"))));
        } catch (IOException e) {
            System.out.println("Could not read directory. Returning null. Error: " + e.getMessage());
            return null;
        }

        // sort alphanumerically to ensure content hash
        entries.sort(Comparator.comparing(p -> p.getFileName().toString()));

        MessageDigest combinedDigest;
        try {
            combinedDigest = (MessageDigest) digest.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Could not clone MessageDigest. Returning null. Error: " + e.getMessage());
            return null;
        }

        for (Path entry : entries) {
            if (Files.isDirectory(entry)) {
                // if path is a dir, hash the dir using recursive function
                byte[] dirHash = hashDirRecursive(entry, digest);
                combinedDigest.update(dirHash);

                // with that, hash the path name
                combinedDigest.update(hashPathName(entry));
            } else {
                // if path is a file, use the hash file with path function above
                byte[] fileHash = hashFileWithPath(entry.toString());
                combinedDigest.update(fileHash);
            }
        }

        // if it's an empty directory, just hash the path name
        if (entries.isEmpty()) {
            combinedDigest.update(hashPathName(currentPath));
        }

        // return the combined hash
        return combinedDigest.digest();
    }

    private static @Nullable MessageDigest createSHA256Digest() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm. Returning null. Error: " + e.getMessage());
            return null;
        }
        return digest;
    }

    private static byte[] hashPathName(Path path) {
        return path.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Util static method to convert an SHA-256 hash (byte Array) to a readable Hex-String
     * @param hash the hash (byte Array)
     * @return The readable hes-String
     */
    public static String hashToHexString(byte[] hash) {
        // Convert hash to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
