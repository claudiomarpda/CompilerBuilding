package compilerbuilding.util;

import compilerbuilding.lexical.Token;
import compilerbuilding.lexical.TokenType;

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

    public static void writeTokensToFile(String fullPath, List<Token> tokens) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%25s %25s %25s\n\n", "NAME", "TYPE", "LINE"));

        tokens.forEach(c -> {
            String s = String.format("%25s %25s %25s", c.getName(), c.getType(), c.getLine());
            sb.append(s).append("\n");
        });

        writeStringToFile(fullPath, sb.toString());
    }
}
