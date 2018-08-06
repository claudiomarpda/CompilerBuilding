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
    private boolean commentOn = false;

    public List<Token> analyze(String input) {
        for (String line : input.split("\n")) {
            checkLine(line);
        }

        tokens.forEach(c -> c.setType(identifyTokenType(c.getName())));
        return tokens.stream().filter(c -> !c.getType().equals(COMMENT)).collect(Collectors.toList());
    }

    private void checkLine(String line) {
        lineIndex++;
        for (String token : line.split(" ")) {
            token = token.trim();
            checkToken(token);
        }
    }

    // TODO: Fix comment checking. The first two chars of "{ comment}" and "a{comment}" fails
    private void checkToken(String token) {
        String tokenPart = "";
        while (token != null && token.length() > 0) {

            if (token.equals(COMMENT_CLOSE)) {
                commentOn = false;
                token = token.substring(1);
                continue;
            }


            if (tokenMatches(token) && !commentOn) {
                tokens.add(new Token(token, UNDEFINED, lineIndex));
                break;
            } else {
                String found = findPatternInString(token);
                int i = found.length();

                if (found.contains(COMMENT_CLOSE)) {
                    // Update token with string after "}".
                    // "comment } word" becomes " word"
                    i = found.indexOf(COMMENT_CLOSE);
                    token = found = token.substring(i);
                    this.commentOn = false;
                } else if (found.contains(COMMENT_OPEN)) {
                    this.commentOn = true;
                }

                if (commentOn) {
                    // Removes each character of the token until find a "}", or go to the next token
                    // {comment becomes comment, and so on
                    token = token.substring(1);
                    continue;
                }

                if (!found.equals("")) {
                    tokenPart = found;
                    token = token.substring(i);
                } else {
                    break;
                }
            }
        }
        if (!tokenPart.equals("")) {
            if (tokenMatches(tokenPart)) {
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
        StringBuilder s = new StringBuilder();

        // Moves the cursor forward until a character is not recognized
        while (matcher.find()) ;

        // Start is the first character index that didn't match in regex
        s.append(token, 0, matcher.start() - 1);
        return s.toString();
    }

    private String identifyTokenType(String token) {
//        if (COMMENT_CLOSE.equals(token)) {
//            commentOn = false;
//            return COMMENT;
//        } else if (commentOn) {
//            return COMMENT;
//        } else if (COMMENT_OPEN.equals(token)) {
//            commentOn = true;
//            return COMMENT;
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
