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
        checkSubprograms();
    }

    /**
     * Input pattern must be: program identifier;
     */
    private void checkProgramDeclaration() throws SyntaxException {
        if (!token.getName().equals("program")) throw new SyntaxException("program", token);

        nextToken();
        if (!isIdentifier()) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (!isSemiColon()) throw new SyntaxException(";", token);

        nextToken();
    }

    private boolean isIdentifier() throws SyntaxException {
        return token.getType().equals(IDENTIFIER);
    }

    private boolean isSemiColon() {
        return token.getName().equals(";");
    }

    /**
     * Input pattern expected:
     * <p>
     * variables declaration list -> variables_declaration_list identifiers_list : type; | identifiers_list : type;
     */
    private void checkVariablesDeclaration() throws SyntaxException {
        if (!token.getName().equals("var")) return;

        nextToken();
        while (isIdentifier()) {
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

    /**
     * subprogram declarations -> subprogram declarations subprogram declaration | E
     */
    private void checkSubprograms() {
        while (token.getName().equals("procedure")) {
            checkSubprogram();
            checkCompoundCommand();
        }
    }

    private void checkSubprogram() {
        if (!token.getName().equals("procedure")) return;

        nextToken();
        if (!isIdentifier()) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (!isOpenParentheses()) throw new SyntaxException("(", token);

        nextToken();
        while (isIdentifier()) {

            nextToken();
            if (!isColon()) throw new SyntaxException(":", token);

            nextToken();
            if (!isDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (!isSemiColon()) break;

            nextToken();
        }

        if (!isClosedParentheses()) throw new SyntaxException(")", token);

        nextToken();
        if (!isSemiColon()) throw new SyntaxException(";", token);

        nextToken();
    }

    private boolean isOpenParentheses() {
        return token.getName().equals("(");
    }

    private boolean isClosedParentheses() {
        return token.getName().equals(")");
    }

    private void checkCompoundCommand() {
        if (!isBegin()) throw new SyntaxException("begin", token);
    }

    private boolean isBegin() {
        return token.getName().equals("begin");
    }
}
