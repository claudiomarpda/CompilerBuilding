package compilerbuilding.lexical;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PascalPattern {

    public static final List<String> KEYWORDS = Arrays.asList(
            "program", "var", "integer", "real", "boolean", "procedure",
            "begin", "end", "if", "then", "else", "while", "do", "not");

    public static final List<String> DELIMITERS = Arrays.asList(",", ";", ".", ":", "(", ")");
    public static final List<String> RELATIONAL_OPERATORS = Arrays.asList("=", "<", ">", "<=", ">=", "<>");
    public static final List<String> ADDITIVE_OPERATORS = Arrays.asList("+", "-", "or");
    public static final List<String> MULTIPLICATIVE_OPERATORS = Arrays.asList("*", "/", "and");
    public static final String ATTRIBUTION_COMMAND = ":=";

    public static final Pattern PATTERNS_UNION;


    static {
        // \w: A word character, short for [a-zA-Z_0-9]
        String identifier = "^[a-zA-Z]\\w*";
        String integer = "\\d+";
        String real = "\\d+\\.\\d*";

        StringBuilder union = new StringBuilder();
        union.append(String.join("|", KEYWORDS));
        union.append("|").append(String.join("|", DELIMITERS));
        union.append("|").append(String.join("|", RELATIONAL_OPERATORS));
        union.append("|\\").append(String.join("|", ADDITIVE_OPERATORS));
        union.append("|\\").append(String.join("|", MULTIPLICATIVE_OPERATORS));
        union.append("|").append(ATTRIBUTION_COMMAND);
        union.append('|').append(identifier);
        union.append('|').append(integer);
        union.append('|').append(real);

        PATTERNS_UNION = Pattern.compile(union.toString());
        System.out.println("UNION OF ALL REGULAR EXPRESSIONS");
        System.out.println(PATTERNS_UNION);
    }

}
