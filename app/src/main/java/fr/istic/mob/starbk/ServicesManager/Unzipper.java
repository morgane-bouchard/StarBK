package fr.istic.mob.starbk.ServicesManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {
    public static void unzip(String filename, String locationPath) throws IOException {
        Unzipper.mkdir("", locationPath);

        FileInputStream in = new FileInputStream(filename);
        ZipInputStream zip = new ZipInputStream(in);

        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                FileOutputStream out = new FileOutputStream(locationPath + "/" + entry.getName());
                BufferedOutputStream buffer = new BufferedOutputStream(out);
                byte[] bytes = new byte[1024];

                int read;
                while ((read = zip.read(bytes)) != -1) {
                    buffer.write(bytes, 0, read);
                }
                buffer.close();
                zip.closeEntry();
                out.close();
            } else {
                Unzipper.mkdir(entry.getName(), locationPath);
            }
        }
        in.close();
    }

    private static boolean mkdir(String dirname, String locationWhereCreate) {
        File file = new File(locationWhereCreate + "/" + dirname);

        if (!file.isDirectory())
            return file.mkdir();

        return false;
    }
}
