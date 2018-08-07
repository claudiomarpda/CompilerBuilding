package compilerbuilding.lexical;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static compilerbuilding.lexical.PascalPattern.*;
import static compilerbuilding.lexical.TokenType.*;

public final class LexicalAnalyzer {

    private int lineIndex = 0;

    private List<Token> tokens = new ArrayList<>();

    public List<Token> analyze(String input) {
        for (String line : input.split("\n")) {
            checkLine(line);
        }

        tokens.forEach(c -> c.setType(identifyTokenType(c.getName())));
        return tokens.stream().filter(c -> !c.getType().equals(COMMENT)).collect(Collectors.toList());
    }

    private boolean matchesPatterns(String token) {
        return PATTERNS_UNION.matcher(token).matches();
    }

    private void checkLine(String line) {
        lineIndex++;
        for (String token : line.split(" ")) {
            token = token.trim();
            if (token.equals("")) continue;
            checkToken(token);
        }
    }

    private void checkToken(String token) {
        if (matchesPatterns(token)) {
            tokens.add(new Token(token, UNDEFINED, lineIndex));
        } else {
            for (String s : checkTokenCharacters(token)) {
                if (matchesPatterns(s)) {
                    tokens.add(new Token(s, UNDEFINED, lineIndex));
                } else if (!s.equals("")) {
                    tokens.add(new Token(s, UNKNOWN, lineIndex));
                }
            }
        }
    }

    private List<String> checkTokenCharacters(String token) {
        Matcher matcher = PATTERNS_UNION.matcher(token);
        List<String> tokenList = new ArrayList<>();
        String part = "";

        // Moves the cursor forward until a character is not recognized
        while (matcher.find() && !matcher.hitEnd()) {

            if (PascalPattern.containsSymbol(matcher.group())) {
                if (!part.equals("")) {
                    tokenList.add(part);
                }
                tokenList.add(matcher.group());
            } else {
                part += matcher.group();
                if (PascalPattern.containsSymbol(part)) {
                    tokenList.add(part);
                    part = "";
                }
            }
        }
        return tokenList;
    }

    private String identifyTokenType(String token) {
        if (KEYWORDS.contains(token)) {
            return KEYWORD;
        } else if (DELIMITERS.contains(token)) {
            return DELIMITER;
        } else if (RELATIONAL_OPERATORS.contains(token)) {
            return RELATIONAL_OPERATOR;
        } else if (ADDITIVE_OPERATORS.contains(token)) {
            return ADDITIVE_OPERATOR;
        } else if (MULTIPLICATIVE_OPERATORS.contains(token)) {
            return MULTIPLICATIVE_OPERATOR;
        } else if (ATTRIBUTION_COMMAND.equals(token)) {
            return ATTRIBUTION;
        } else if (INTEGER_PATTERN.matcher(token).matches()) {
            return INTEGER;
        } else if (REAL_PATTERN.matcher(token).matches()) {
            return REAL;
        } else if (IDENTIFIER_PATTERN.matcher(token).matches()) {
            return IDENTIFIER;
        }
        return UNKNOWN;
    }

}
