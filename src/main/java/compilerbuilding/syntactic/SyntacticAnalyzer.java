package compilerbuilding.syntactic;

import compilerbuilding.lexical.Token;
import compilerbuilding.syntactic.exception.SyntaxException;

import java.util.List;

import static compilerbuilding.lexical.TokenType.*;

/**
 * Implements a Context Free Grammar for an syntactical analyzer.
 * Use the 'analyze' method to run the syntax analysis.
 * If something goes wrong, a SyntaxException is thrown.
 */
public class SyntacticAnalyzer {

    // Tokens obtained from lexical analyzer
    private List<Token> tokens;

    // Current token
    private Token token;

    // Index of the current token
    private int current;

    public SyntacticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.get(0);
    }

    private void nextToken() {
        token = tokens.get(++current);
    }

    /**
     * program -> program id; variable_declarations subprogram_declarations compound_command .
     *
     * @throws SyntaxException with analysis info
     */
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
     * program id;
     */
    private void checkProgram() {
        if (!nameMatches("program")) throw new SyntaxException("program", token);

        nextToken();
        if (!typeMatches(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);

        nextToken();
        if (!nameMatches(";")) throw new SyntaxException(";", token);

        nextToken();
    }

    /**
     * variables declaration list -> variables_declaration_list identifiers_list : type; | identifiers_list : type;
     */
    private void checkVariables() {
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

    /**
     * subprogram declaration ->
     * <p>
     * procedure id arguments;
     * variable_declarations
     * compound_command
     */
    private void checkSubprogram() {
        if (!nameMatches("procedure")) return;

        nextToken();
        checkParameters();
        checkVariables();
        checkCompoundCommand();
    }

    /**
     * parameters -> (parameters_list) | E
     * <p>
     * parameters_list -> identifier_list : type | parameters_list; identifier_list : type
     */
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

    /**
     * compound_command -> begin optional_commands end
     * <p>
     * optional_command -> command_list | E
     */
    private void checkCompoundCommand() {
        if (!nameMatches("begin")) throw new SyntaxException("begin", token);
        nextToken();

        // command; command_list
        while (!nameMatches("end")) {
            checkCommands();
        }
        nextToken();
    }

    /**
     * command_list -> command | command_list; command
     */
    private void checkCommands() {
        // command;
        checkCommand();
        if (!nameMatches(";")) throw new SyntaxException(";", token);
        nextToken();
    }

    /**
     * command -> variable := expression | procedure_call | compound_command
     * | if expression then command esle_part
     * | while expression do command
     * <p>
     * else_part -> else command | E
     */
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
                if (!nameMatches(")")) throw new SyntaxException(")", token);
                nextToken();
            }
        }
        // else if [if then]
        // else if [while do]

    }

    /**
     * expression -> simple expression | simple_expression relational_op simple_expression
     */
    private void checkExpression() {
        checkSimpleExpression();
        if (typeMatches(RELATIONAL_OPERATOR)) {
            nextToken();
            checkSimpleExpression();
        }
    }

    /**
     * simple_expression -> term | signal term | simple_expression additive_op term
     */
    private void checkSimpleExpression() {
        // term
        checkTerm();

        // | signal term
        if (nameMatches("+") || nameMatches("-")) {
            nextToken();
            checkTerm();
        }

        // | term additive_op simple_exp
        // additive_op -> + | - | or
        if (typeMatches(ADDITIVE_OPERATOR) || nameMatches("or")) {
            nextToken();
            checkSimpleExpression();
        }
    }

    /**
     * term -> factor | term multiplicative_op factor
     */
    private void checkTerm() {
        // factor
        checkFactor();

        // | factor multiplicative_op term
        // multiplicative_op -> * | / | and
        if (typeMatches(MULTIPLICATIVE_OPERATOR) || nameMatches("and")) {
            nextToken();
            checkFactor();
            checkTerm();
        }
    }

    /**
     * factor -> id | id(list_of_expressions) | integer | real | true | false | (expression) | not factor
     */
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
        // | not factor
        else if (nameMatches("not")) {
            nextToken();
            checkFactor();
        }
    }
}
