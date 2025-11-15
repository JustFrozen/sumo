package de.darian.sumo.sumo;

import de.darian.sumo.sumo.util.SHA256Hasher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Sumo {

    public static final String APP_NAME = "sumo";
    public static final String RELATIVE_DIR_PATH = System.getProperty("user.dir");
    public static final String SUMO_FOLDER_PATH = RELATIVE_DIR_PATH + File.separator + "." + APP_NAME;
    public static final String SUMO_INIT_FILE_PATH = SUMO_FOLDER_PATH + File.separator + "init";

    public static void main(String[] args) {
        new Sumo();
    }

    public Sumo() {
        boolean s = init();
    }

    private boolean init() {
        boolean created = createSumoFolder();
        if (!created) {
            return false;
        }
        System.out.println("Created \"." + APP_NAME + "\" folder");

        List<Path> l = snapshotDir();
        return true;
    }

    private static boolean createSumoFolder() {
        File sumoFolder = new File(SUMO_FOLDER_PATH);
        if (!sumoFolder.exists()) {
            boolean success = sumoFolder.mkdir();
            if (!success) {
                System.out.println("Failed to create \"." + APP_NAME + "\" folder.");
                return false;
            }
        } else {
            if (sumoFolder.isDirectory()) {
                System.out.println("\"." + APP_NAME + "\" folder already exists");
            } else {
                System.out.println("A file with the reserved sumo folder name \"." + APP_NAME + "\" already exists.");
            }
            return false;
        }

        try {
            Files.setAttribute(Path.of(SUMO_FOLDER_PATH), "dos:hidden", true);
        } catch (IOException e) {
            System.out.println("Failed to hide \"." + APP_NAME + "\" folder. Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private List<Path> snapshotDir() {
//        String exPath = RELATIVE_DIR_PATH + File.separator + "src\\main\\java\\de\\darian\\sumo\\main\\test.txt";
//        System.out.println(RELATIVE_DIR_PATH);
//        System.out.println(SUMO_INIT_FILE_PATH);
//        String hash = SHA256Hasher.hashFileWithPath(exPath);
//        if (hash == null) {
//            System.out.println("Could not snapshot directory. Failed to hash \"" + exPath + "\".");
//            return null;
//        }
//        System.out.println("SHA-256 Hash: " + hash);


        List<Path> snapshot = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(Path.of(RELATIVE_DIR_PATH))) {
            snapshot = stream
                    .filter(file -> {
                        try {
                            return Files.isRegularFile(file) || Files.isDirectory(file) || Files.isHidden(file) || Files.isSymbolicLink(file);
                        } catch (IOException _) {
                            // this is raised by Files#isHidden. We assume here that the directory/file cannot be read,
                            // so we just return false
                            return false;
                        }
                    })
                    .toList();
        } catch (IOException e) {
            System.out.println("Could not walk the root directory. Error: " + e.getMessage());
        }

        return snapshot;
    }

    public void addCommand(String path) {
        File dir = new File(path);

        if(dir.exists()) {
            try {
                dir = dir.getCanonicalFile();
                int layer = 0;
                getRecursiveDirectory(dir, layer);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println(path + " is not a valid path");
        }
    }

    private void getRecursiveDirectory(File dir, int layer) {
        System.out.println(dir.getPath());
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                layer++;
                for (File file : files) {
                    getRecursiveDirectory(file, layer);
                }
            }
        }
    }
}
