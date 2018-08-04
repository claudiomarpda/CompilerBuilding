package compilerbuilding.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    public static String readFileAsString(String fullPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fullPath)));
    }

    public static void writeStringToFile(String fullPath, String data) throws IOException {
        Files.write(Paths.get(fullPath), data.getBytes());
    }

}
