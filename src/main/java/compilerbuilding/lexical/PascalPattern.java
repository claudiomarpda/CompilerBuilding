package compilerbuilding.lexical;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class PascalPattern {

    public static final List<String> KEYWORDS = Arrays.asList(
            "program", "var", "integer", "real", "boolean", "procedure",
            "begin", "end", "if", "then", "else", "while", "do", "not");

    public static final List<String> DELIMITERS = Arrays.asList(",", ";", ".", ":", "(", ")");
    public static final List<String> RELATIONAL_OPERATORS = Arrays.asList("=", "<", ">", "<=", ">=", "<>");
    public static final List<String> ADDITIVE_OPERATORS = Arrays.asList("+", "-", "or");
    public static final List<String> MULTIPLICATIVE_OPERATORS = Arrays.asList("*", "/", "and");
    public static final String ATTRIBUTION_COMMAND = ":=";

    public static final Pattern PATTERNS_UNION;
    public static final Pattern IDENTIFIER_PATTERN;
    public static final Pattern INTEGER_PATTERN;
    public static final Pattern REAL_PATTERN;


    static {
        // \w: A word character, short for [a-zA-Z_0-9]
        String identifierRegex = "^[a-zA-Z]\\w*";
        String integerRegex = "\\d+";
        String realRegex = "\\d+\\.\\d*";

        StringBuilder union = new StringBuilder();
        union.append(String.join("|", KEYWORDS));
        union.append("|").append(String.join("|", DELIMITERS));
        union.append("|").append(String.join("|", RELATIONAL_OPERATORS));
        union.append("|\\").append(String.join("|", ADDITIVE_OPERATORS));
        union.append("|\\").append(String.join("|", MULTIPLICATIVE_OPERATORS));
        union.append("|").append(ATTRIBUTION_COMMAND);
        union.append('|').append(identifierRegex);
        union.append('|').append(integerRegex);
        union.append('|').append(realRegex);

        PATTERNS_UNION = Pattern.compile(union.toString());
        System.out.println("UNION OF ALL REGULAR EXPRESSIONS");
        System.out.println(PATTERNS_UNION);

        IDENTIFIER_PATTERN = Pattern.compile(identifierRegex);
        INTEGER_PATTERN = Pattern.compile(integerRegex);
        REAL_PATTERN= Pattern.compile(realRegex);
    }

}
