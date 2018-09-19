package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticResult {

    public static final String DEFINED_VARIABLE = "Variable already defined: ";
    public static final String DEFINED_SUBPROGRAM = "Subprogram already defined: ";
    public static final String UNDEFINED_VARIABLE = "Variable not defined: ";
    public static final String UNDEFINED_SUBPROGRAM = "Subprogram not defined: ";
    public static final String INVALID_OPERATION = "Invalid operation: ";

    private List<String> results;

    public SemanticResult() {
        this.results = new ArrayList<>();
    }

    public void add(Token token, String cause) {
        results.add(cause + token.getName() + " at line " + token.getLine());
    }

    public void add(String type1, String type2, int line) {
        results.add("Incompatible types: '" + type1 + "' and '" + type2 + "' at line " + line);
    }

    public List<String> getResults() {
        return results;
    }

}
