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
        checkProgramDeclaration();
        checkVariablesDeclaration();
    }

    /**
     * Input pattern must be: program identifier;
     */
    private void checkProgramDeclaration() throws SyntaxException {
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

    private void checkVariablesDeclaration() throws SyntaxException {
        if(!token.getName().equals("var")) return;

        nextToken();
        while (token.getType().equals(IDENTIFIER)) {
            checkIdentifier();

            nextToken();
            if (!isColon()) throw new SyntaxException(":", token);

            nextToken();
            if (!isDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (!isSemiColon()) throw new SyntaxException(";", token);

            nextToken();
        }
    }

    private boolean isDataType() {
        String name = token.getName();
        return name.equals("integer") || name.equals("real") || name.equals("boolean");
    }

    private boolean isColon() {
        return token.getName().equals(":");
    }
}
