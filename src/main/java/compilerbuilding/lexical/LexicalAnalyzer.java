package compilerbuilding.lexical;

import compilerbuilding.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static compilerbuilding.lexical.PascalPattern.*;
import static compilerbuilding.lexical.PascalPattern.ATTRIBUTION_COMMAND;
import static compilerbuilding.lexical.TokenType.*;

public final class LexicalAnalyzer {

    private int lineIndex = 0;

    private List<Token> tokens = new ArrayList<>();

    public void analyze(String input) {
        for (String line : input.split("\n")) {
            checkLine(line);
        }

        tokens.forEach(c -> c.setType(identifyTokenType(c.getName())));

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
        String tokenPart = "";
        while (token != null && token.length() > 0) {
            if (tokenMatches(token)) {
                tokens.add(new Token(token, TokenType.UNDEFINED, lineIndex));
                break;
            } else {
                String found = findPatternInString(token);
                if (found != null) {
                    tokenPart += token.substring(0, found.length());
                    token = token.substring(found.length());
                } else {
                    break;
                }
            }
        }
        if(!tokenPart.equals("")) {
            if(tokenMatches(tokenPart)) {
                tokens.add(new Token(tokenPart, UNDEFINED, lineIndex));
            } else {
                tokens.add(new Token(tokenPart, UNKNOWN, lineIndex));
            }
        }
    }

    private boolean tokenMatches(String token) {
        return PATTERNS_UNION.matcher(token).matches();
    }

    private String findPatternInString(String token) {
        Matcher matcher = PATTERNS_UNION.matcher(token);
        if (matcher.find()) {
            return token.substring(0, matcher.end());
        }
        return null;
    }

    private String identifyTokenType(String token) {
        if(KEYWORDS.contains(token)) {
            return KEYWORD;
        }
        else if(DELIMITERS.contains(token)) {
            return DELIMITER;
        }
        else if(RELATIONAL_OPERATORS.contains(token)) {
            return RELATIONAL_OPERATOR;
        }
        else if(ADDITIVE_OPERATORS.contains(token)) {
            return ADDITIVE_OPERATOR;
        }
        else if(MULTIPLICATIVE_OPERATORS.contains(token)) {
            return MULTIPLICATIVE_OPERATOR;
        }
        else if(ATTRIBUTION_COMMAND.equals(token)) {
            return ATTRIBUTION;
        }
        else if(INTEGER_PATTERN.matcher(token).matches()) {
            return INTEGER;
        }
        else if(REAL_PATTERN.matcher(token).matches()) {
            return REAL;
        }
        else if(IDENTIFIER_PATTERN.matcher(token).matches()) {
            return IDENTIFIER;
        }
        return UNKNOWN;
    }

}
