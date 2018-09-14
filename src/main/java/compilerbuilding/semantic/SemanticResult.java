package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticResult {

    public static final String DEFINED_VARIABLE = "Variable already defined: ";
    public static final String UNDEFINED_VARIABLE = "Variable not defined: ";

    private List<String> results;

    public SemanticResult() {
        this.results = new ArrayList<>();
    }

    public void add(Token token, String cause) {
        results.add(cause + token.getName() + " at line " + token.getLine());
    }

    public List<String> getResults() {
        return results;
    }

}
