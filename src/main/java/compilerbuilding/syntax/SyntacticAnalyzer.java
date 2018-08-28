package compilerbuilding.syntax;

import compilerbuilding.lexical.Token;
import compilerbuilding.syntax.exception.SyntaxException;

import java.util.List;

public class SyntacticAnalyzer {

    private List<Token> tokens;
    private Token token;
    private int current;

    public SyntacticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.get(0);
    }

    private void nextToken() {
        token = tokens.get(++current);
    }

    public void analyze() throws SyntaxException {

    }
}
