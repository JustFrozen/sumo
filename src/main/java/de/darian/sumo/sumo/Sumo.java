package de.darian.sumo.sumo;

import de.darian.sumo.cli.CLI;
import de.darian.sumo.sumo.util.SHA256Hasher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Sumo {

    public static final String APP_NAME = "sumo";
    public static final String ABSOLUTE_DIR_PATH = System.getProperty("user.dir");
    public static final String SUMO_FOLDER_PATH = ABSOLUTE_DIR_PATH + File.separator + "." + APP_NAME;

    public static boolean DEBUG;

    public Sumo(boolean debug) {
        Sumo.DEBUG = debug;

        CLI cli = new CLI(this, debug);
    }

    public boolean init() {
        boolean created = createSumoFolder();
        if (!created) {
            return false;
        }
        System.out.println("Successfully created " + APP_NAME + " folder.");

        String dirHash = snapshotDir();
        if (dirHash == null) {
            return false;
        }
        System.out.println("Successfully snapshot working directory.");

        boolean saved = saveSnapshotInSumoFolder(dirHash);
        if (!saved) {
            return false;
        }
        System.out.println("Successfully saved snapshot in " + APP_NAME + " folder.");

        return true;
    }

    private boolean saveSnapshotInSumoFolder(String dirHash) {
        Path snapshotPath = Path.of(ABSOLUTE_DIR_PATH).relativize(Path.of(SUMO_FOLDER_PATH + File.separator + "snapshot"));
        try {
            Files.writeString(snapshotPath, dirHash);
        } catch (IOException e) {
            System.out.println("Failed to save snapshot in sumo folder. Error: " + e.getMessage());
            return false;
        }
        System.out.println("Saved snapshot in \"" + snapshotPath.toString() + "\".");
        return true;
    }

    private boolean createSumoFolder() {
        File sumoFolder = new File(SUMO_FOLDER_PATH);
        if (!sumoFolder.exists()) {
            boolean success = sumoFolder.mkdir();
            if (!success) {
                System.out.println("Failed to create \"." + APP_NAME + "\" folder.");
                return false;
            } else {
                System.out.println("Created \"." + APP_NAME + "\" folder");
            }
        } else {
            if (sumoFolder.isDirectory()) {
                System.out.println("\"." + APP_NAME + "\" folder already exists.");
                // as of now, now following logic
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

    private String snapshotDir() {
        byte[] hash = SHA256Hasher.hashDirectory(".");
        return hash == null ? null : SHA256Hasher.hashToHexString(hash);
    }

    public void add(String path) {
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
