package bot.upload.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AudioPreparator {
    public List<String> splitAudio(String filename, int sizeLimitMb) {
        int curPart = 1;
        int sizeOfFiles = 1024 * 1024 * sizeLimitMb;

        byte[] buffer = new byte[sizeOfFiles];
        File file = new File(filename);
        List<String> newFilenames = new ArrayList<>();
        try (FileInputStream fin = new FileInputStream(file);
            BufferedInputStream bin = new BufferedInputStream(fin)) {

            int amount;
            while ((amount = bin.read(buffer)) > 0){
                File newFile = File.createTempFile("toSend"+curPart++, ".mp3");
                newFilenames.add(newFile.getAbsolutePath());
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, amount);
                }
            }
            return newFilenames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String retrive(String url) {
        byte[] buffer = new byte[4096];
        int length;
        try {
            InputStream ain = new URL(url).openStream();
            File output = File.createTempFile("tempAudio", ".mp3");// + ain.getFormat().toString());
            FileOutputStream fout = new FileOutputStream(output);

            while ((length = ain.read(buffer)) > 0) {
                fout.write(buffer, 0, length);
            }

            ain.close();
            fout.close();

            return output.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
