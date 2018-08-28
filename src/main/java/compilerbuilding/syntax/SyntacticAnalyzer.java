package compilerbuilding.syntax;

import compilerbuilding.lexical.Token;
import compilerbuilding.syntax.exception.SyntaxException;

import java.util.List;

import static compilerbuilding.lexical.TokenType.*;

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
        checkProgram();
    }

    /**
     * Input pattern must be: program identifier;
     */
    private void checkProgram() throws SyntaxException {
        if (!token.getName().equals("program")) throw new SyntaxException("program", token);

        nextToken();
        checkIdentifier();

        nextToken();
        if (!isSemiColon()) throw new SyntaxException(";", token);

        nextToken();
    }

    private void checkIdentifier() throws SyntaxException {
        if (!token.getType().equals(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);
    }

    private boolean isSemiColon() {
        return token.getName().equals(";");
    }
}
