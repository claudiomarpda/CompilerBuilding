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
        checkVariables();
        checkSubprograms();
    }

    /**
     * Input pattern must be: program identifier;
     */
    private void checkProgram() throws SyntaxException {
        if (!token.getName().equals("program")) throw new SyntaxException("program", token);

        nextToken();
        if (!isIdentifier()) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (notSemiColon()) throw new SyntaxException(";", token);

        nextToken();
    }

    private boolean isIdentifier() throws SyntaxException {
        return token.getType().equals(IDENTIFIER);
    }

    private boolean notSemiColon() {
        return !token.getName().equals(";");
    }

    /**
     * Input pattern expected:
     * <p>
     * variables declaration list -> variables_declaration_list identifiers_list : type; | identifiers_list : type;
     */
    private void checkVariables() throws SyntaxException {
        if (!token.getName().equals("var")) return;

        nextToken();
        while (isIdentifier()) {
            nextToken();

            // Optionally, could be a list of variables. Example: a, b : integer;
            if(isComma()) {
                nextToken();
                continue;
            }

            if (notColon()) throw new SyntaxException(":", token);

            nextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (notSemiColon()) throw new SyntaxException(";", token);

            nextToken();
        }
    }

    private boolean notDataType() {
        String name = token.getName();
        return !(name.equals("integer") || name.equals("real") || name.equals("boolean"));
    }

    private boolean notColon() {
        return !token.getName().equals(":");
    }

    /**
     * subprogram declarations -> subprogram declarations subprogram declaration | E
     */
    private void checkSubprograms() {
        while (token.getName().equals("procedure")) {
            checkSubprogram();
        }
    }

    private void checkSubprogram() {
        if (!token.getName().equals("procedure")) return;

        nextToken();
        checkParameters();
        // checkVariables();
        // checkCompoundCommand();
    }

    private void checkParameters() {
        if (!isIdentifier()) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (notOpenParentheses()) throw new SyntaxException("(", token);

        nextToken();
        while (isIdentifier()) {

            nextToken();
            if (notColon()) throw new SyntaxException(":", token);

            nextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (notSemiColon()) break;

            nextToken();
        }

        if (notClosedParentheses()) throw new SyntaxException(")", token);

        nextToken();
        if (notSemiColon()) throw new SyntaxException(";", token);

        nextToken();
    }

    private boolean notOpenParentheses() {
        return !token.getName().equals("(");
    }

    private boolean notClosedParentheses() {
        return !token.getName().equals(")");
    }

    private void checkCompoundCommand() {
        if (notBegin()) throw new SyntaxException("begin", token);
    }

    private boolean notBegin() {
        return !token.getName().equals("begin");
    }

    private boolean isComma() {
        return token.getName().equals(",");
    }
}
