package compilerbuilding.syntactic;

import compilerbuilding.lexical.Token;
import compilerbuilding.syntactic.exception.SyntaxException;

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

    private boolean nameMatches(String name) {
        return token.getName().equals(name);
    }

    private boolean typeMatches(String type) {
        return token.getType().equals(type);
    }

    /**
     * Input pattern must be: program identifier;
     */
    private void checkProgram() throws SyntaxException {
        if (!nameMatches("program")) throw new SyntaxException("program", token);

        nextToken();
        if (!typeMatches(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (!nameMatches(";")) throw new SyntaxException(";", token);

        nextToken();
    }

    /**
     * Input pattern expected:
     * <p>
     * variables declaration list -> variables_declaration_list identifiers_list : type; | identifiers_list : type;
     */
    private void checkVariables() throws SyntaxException {
        if (!token.getName().equals("var")) return;

        nextToken();
        while (typeMatches(IDENTIFIER)) {
            nextToken();

            // Optionally, could be a list of variables. Example: a, b : integer;
            if (nameMatches(",")) {
                nextToken();
                continue;
            }

            if (!nameMatches(":")) throw new SyntaxException(":", token);

            nextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (!nameMatches(";")) throw new SyntaxException(";", token);

            nextToken();
        }
    }

    private boolean notDataType() {
        return !(nameMatches("integer") || nameMatches("real") || nameMatches("boolean"));
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
        if (!nameMatches("procedure")) return;

        nextToken();
        checkParameters();
        checkVariables();
        checkCompoundCommand();
    }

    private void checkParameters() {
        if (!typeMatches(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (!nameMatches("(")) throw new SyntaxException("(", token);

        nextToken();
        while (typeMatches(IDENTIFIER)) {

            nextToken();
            if (!nameMatches(":")) throw new SyntaxException(":", token);

            nextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            nextToken();
            if (!nameMatches(";")) break;

            nextToken();
        }

        if (!nameMatches(")")) throw new SyntaxException(")", token);

        nextToken();
        if (!nameMatches(";")) throw new SyntaxException(";", token);

        nextToken();
    }

    private void checkCompoundCommand() {
        if (!nameMatches("begin")) throw new SyntaxException("begin", token);
        nextToken();

        while (nameMatches("end")) {
            checkCommands();
        }
        nextToken();
    }

    private void checkCommands() {
        checkCommand();
        if (!nameMatches(";")) throw new SyntaxException(";", token);
        nextToken();
    }

    private void checkCommand() {
        // Variable or procedure call
        if (typeMatches(IDENTIFIER)) {
            nextToken();
            if (typeMatches(ATTRIBUTION)) {
                nextToken();
                checkExpression();
            }
            // else if procedure call
        }
        // else if compound command
        // else if [if then]
        // else if [while do]

    }

    private void checkExpression() {
        checkSimpleExpression();
        if (typeMatches(RELATIONAL_OPERATOR)) {
            nextToken();
            checkSimpleExpression();
        }
    }

    private void checkSimpleExpression() {
        checkTerm();
        // | signal term
        // | simple_exp add_op term
    }

    private void checkTerm() {
        checkFactor();
//        checkTerm();
    }

    private void checkFactor() {
        if(typeMatches(IDENTIFIER) || typeMatches(INTEGER) || typeMatches(REAL)) {
            nextToken();
        }
    }
}
