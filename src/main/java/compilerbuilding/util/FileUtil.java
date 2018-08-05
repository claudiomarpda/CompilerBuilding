package compilerbuilding.util;

import compilerbuilding.lexical.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    public static String readFileAsString(String fullPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fullPath)));
    }

    public static void writeStringToFile(String fullPath, String data) throws IOException {
        Files.write(Paths.get(fullPath), data.getBytes());
    }

    public static void writeTokensToFile(List<Token> tokens) {
        final StringBuilder sb = new StringBuilder();
        tokens.forEach(c -> {
            sb.append(c.getName()).append("-------")
                    .append(c.getType()).append("-------")
                    .append(c.getLine()).append("-------")
                    .append("\n");
        });

        try {
            writeStringToFile("output/output.txt", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
