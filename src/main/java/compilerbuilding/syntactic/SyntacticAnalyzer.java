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

        // command; command_list
        while (!nameMatches("end")) {
            checkCommands();
        }
        nextToken();
    }

    private void checkCommands() {
        // command;
        checkCommand();
        if (!nameMatches(";")) throw new SyntaxException(";", token);
        nextToken();
    }

    private void checkCommand() {
        // variable := expression
        if (typeMatches(IDENTIFIER)) {
            nextToken();
            if (typeMatches(ATTRIBUTION)) {
                nextToken();
                checkExpression();
            }
            // | procedure call
            else if (nameMatches("(")) {
                nextToken();
                checkExpression();
                while (nameMatches(",")) {
                    nextToken();
                    checkExpression();
                }
                if(!nameMatches(")")) throw new SyntaxException(")", token);
                nextToken();
            }
        }
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
        // term
        checkTerm();

        // | signal term
        if (nameMatches("+") || nameMatches("-")) {
            nextToken();
            checkTerm();
        }

        // | term add_op simple_exp
        // add_op -> + | - | or
        if (typeMatches(ADDITIVE_OPERATOR) || typeMatches("or")) {
            nextToken();
            checkSimpleExpression();
        }
    }

    private void checkTerm() {
        // factor
        checkFactor();

        // | factor multiplicative_op term
        // multi_op -> * | / | and
        if (typeMatches(MULTIPLICATIVE_OPERATOR) || typeMatches("and")) {
            nextToken();
            checkFactor();
            checkTerm();
        }
    }

    private void checkFactor() {
        // id | integer | real
        if (typeMatches(IDENTIFIER) || typeMatches(INTEGER) || typeMatches(REAL)) {
            nextToken();
        }
        // | (expression)
        else if (nameMatches("(")) {
            nextToken();
            checkExpression();

            if (!nameMatches(")")) throw new SyntaxException(")", token);
            nextToken();
        }
        // | true
        // | false
        else if (nameMatches("true") || nameMatches("false")) {
            nextToken();
        }
    }
}
