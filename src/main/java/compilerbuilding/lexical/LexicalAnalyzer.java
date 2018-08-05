package compilerbuilding.lexical;

import compilerbuilding.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static compilerbuilding.lexical.PascalPattern.PATTERNS_UNION;

public final class LexicalAnalyzer {

    private int lineIndex = 0;

    private List<Token> tokens = new ArrayList<>();

    public void analyze(String input) {
        for (String line : input.split("\n")) {
            checkLine(line);
        }

        FileUtil.writeTokensToFile(tokens);
    }

    private void checkLine(String line) {
        lineIndex++;
        for (String token : line.split(" ")) {
            token = token.trim();

            checkToken(token);
        }
    }

    private void checkToken(String token) {
        if (PATTERNS_UNION.matcher(token).matches()) {
            tokens.add(new Token(token, "unknown", lineIndex));
        } else {

        }

    }
}
