package de.darian.sumo.sumo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Sumo {

    public static void main(String[] args) {
        new Sumo();
    }

    public Sumo() {
        try {
            addCommand("target", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommand(String path, boolean relative) throws IOException {
        if (relative) {
            Path root = Paths.get(System.getProperty("user.dir"));
            path = String.valueOf(root.relativize(Path.of(root + File.separator + path)));
        }
        File dir = new File(path);

        if(dir.exists()) {
            int layer = 0;
            getRecursiveDirectory(dir, layer);
        } else {
            System.out.println(dir.getCanonicalPath() + " is not a valid path");
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
