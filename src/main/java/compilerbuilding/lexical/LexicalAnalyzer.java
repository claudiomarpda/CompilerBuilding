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
    private boolean commentOpen;

    public List<Token> analyze(String input) {
        for (String line : input.split("\n")) {
            if (line.length() >= 2 && line.substring(0, 2).equals(COMMENT_LINE)) {
                continue;
            }
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

        if (!commentOpen && THREE_D_PATTERN.matcher(token).matches()) {
            tokens.add(new Token(token, UNDEFINED, lineIndex));
        } else if (!commentOpen && matchesPatterns(token)) {
            tokens.add(new Token(token, UNDEFINED, lineIndex));

        } else if (!commentOpen && !containsCommentSymbol(token) && checkTokenWithoutLastCharacter(token)) {
            tokens.add(new Token(token.substring(0, token.length() - 1), UNDEFINED, lineIndex));
            tokens.add(new Token(token.substring(token.length() - 1), UNDEFINED, lineIndex));

        } else if (!commentOpen && !containsCommentSymbol(token) && checkTokenWithoutFirstCharacter(token)) {
            tokens.add(new Token(token.substring(1), UNDEFINED, lineIndex));
            tokens.add(new Token(token.substring(0, 1), UNDEFINED, lineIndex));

        } else if (!commentOpen && !containsCommentSymbol(token) && checkTokenWithoutFirstAndLastCharacters(token)) {
            tokens.add(new Token(token.substring(1, token.length() - 1), UNDEFINED, lineIndex));
            tokens.add(new Token(token.substring(token.length() - 1), UNDEFINED, lineIndex));
            tokens.add(new Token(token.substring(0, 1), UNDEFINED, lineIndex));

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

    private boolean containsCommentSymbol(String token) {
        return token.contains(COMMENT_OPEN) || token.contains(COMMENT_CLOSE);
    }

    private List<String> checkTokenCharacters(String token) {
        Matcher matcher = PATTERNS_UNION.matcher(token);
        List<String> tokenList = new ArrayList<>();
        String part = "";

        int i = 0;
        // Moves the cursor forward until a character is not recognized
        while (matcher.find() && !matcher.hitEnd()) {
            String current = matcher.group();

            if (current.equals(COMMENT_CLOSE)) {
                commentOpen = false;
                return tokenList;
            } else if (current.equals(COMMENT_OPEN)) {
                commentOpen = true;
                continue;
            }
            if (commentOpen) continue;

            if (INTEGER_PATTERN.matcher(part).matches()) {
                if (current.equals(".")) {
                    part += ".";
                    i++;

                    while (!matcher.hitEnd() && matcher.find() && INTEGER_PATTERN.matcher(matcher.group()).matches()) {
                        part += matcher.group();
                        i++;
                    }
                }
            }

            if (containsSymbol(current)) {
                if (!part.equals("")) {
                    tokenList.add(part);
                    i += part.length();
                    part = "";
                }
                tokenList.add(current);
                i += current.length();
            } else {
                part += matcher.group();

                // This case never happened so far
                if (containsSymbol(part)) {
                    tokenList.add(part);
                    i += part.length();
                    part = "";
                }
            }
        }
        if (i < token.length() && !commentOpen) {
            tokenList.add(token.substring(i));
        }
        return tokenList;
    }

    private boolean checkTokenWithoutLastCharacter(String token) {
        String subToken = token.substring(0, token.length() - 1);
        return PATTERNS_UNION.matcher(subToken).matches();
    }

    private boolean checkTokenWithoutFirstCharacter(String token) {
        String subToken = token.substring(1);
        return PATTERNS_UNION.matcher(subToken).matches();
    }

    private boolean checkTokenWithoutFirstAndLastCharacters(String token) {
        String subToken = token.substring(1, token.length() - 1);
        return PATTERNS_UNION.matcher(subToken).matches();
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
        } else if (THREE_D_PATTERN.matcher(token).matches()) {
            return THREED;
        }
        return UNKNOWN;
    }

}
