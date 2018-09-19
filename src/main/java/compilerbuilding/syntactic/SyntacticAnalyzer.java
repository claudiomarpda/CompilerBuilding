package compilerbuilding.syntactic;

import compilerbuilding.lexical.Token;
import compilerbuilding.lexical.TokenType;
import compilerbuilding.semantic.SemanticAnalysis;

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

    private SemanticAnalysis semanticAnalysis;

    public SyntacticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.get(0);
    }

    public SyntacticAnalyzer(List<Token> tokens, SemanticAnalysis semanticAnalysis) {
        this(tokens);
        this.semanticAnalysis = semanticAnalysis;
    }

    private void goToNextToken() {
        if(++current >= tokens.size()) {
            token = new Token("", TokenType.UNDEFINED, token.getLine());
        }
        else {
            token = tokens.get(current);
        }
    }

    /**
     * program -> program id; variables_declarations subprograms_declarations compound_command .
     *
     * @throws SyntaxException with analysis info
     */
    public void analyze() throws SyntaxException {
        checkProgram();
        checkVariables();
        checkSubprograms();
        checkCompoundCommand();
        if(!nameMatches(".")) throw new SyntaxException(".", token);
        System.out.println("Syntax OK");
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

        goToNextToken();
        if (!typeMatches(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);

        semanticAnalysis.startProgram();

        goToNextToken();
        if (!nameMatches(";")) throw new SyntaxException(";", token);

        goToNextToken();
    }

    /**
     * variables_declarations_list -> variables_declarations_list identifiers_list : type; | identifiers_list : type;
     */
    private void checkVariables() {
        if (!token.getName().equals("var")) return;

        goToNextToken();

        // How many identifiers will be found
        int identifiers = 0;

        while (typeMatches(IDENTIFIER)) {
            identifiers++;
            semanticAnalysis.pushToken(token);

            goToNextToken();

            // Optionally, could be a list of variables. Example: a, b : integer;
            if (nameMatches(",")) {
                goToNextToken();
                continue;
            }

            if (!nameMatches(":")) throw new SyntaxException(":", token);

            goToNextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            // Set identifiers list type
            semanticAnalysis.identifyType(identifiers, token.getName());

            goToNextToken();
            if (!nameMatches(";")) throw new SyntaxException(";", token);

            identifiers = 0;

            goToNextToken();
        }
    }

    private boolean notDataType() {
        return !(nameMatches("integer") || nameMatches("real") || nameMatches("boolean"));
    }

    /**
     * subprograms_declarations -> subprograms_declarations subprogram_declaration | E
     */
    private void checkSubprograms() {
        while (token.getName().equals("procedure")) {
            checkSubprogram();
        }
    }

    /**
     * subprogram declaration -> procedure id arguments; variables_declarations compound_command
     */
    private void checkSubprogram() {
        if (!nameMatches("procedure")) return;

        goToNextToken();

        semanticAnalysis.openScope();

        checkParameters();
        checkVariables();
        checkSubprogram();
        checkCompoundCommand();
        if(!nameMatches(";")) throw new SyntaxException(";", token);

        semanticAnalysis.closeScope();

        goToNextToken();
    }

    /**
     * parameters -> (parameters_list) | E
     * <p>
     * parameters_list -> identifiers_list : type | parameters_list; identifiers_list : type
     */
    private void checkParameters() {
        if (!typeMatches(IDENTIFIER)) throw new SyntaxException(IDENTIFIER, token);

        semanticAnalysis.pushSubprogram(token);

        goToNextToken();
        if (!nameMatches("(")) throw new SyntaxException("(", token);

        goToNextToken();

        // How many identifiers will be found
        int identifiers = 0;

        while (typeMatches(IDENTIFIER)) {
            identifiers++;
            semanticAnalysis.pushToken(token);

            goToNextToken();
            if (!nameMatches(":")) throw new SyntaxException(":", token);

            goToNextToken();
            if (notDataType()) throw new SyntaxException("a data type", token);

            // Set identifiers list type
            semanticAnalysis.identifyType(identifiers, token.getName());

            goToNextToken();
            if (!nameMatches(";")) break;

            goToNextToken();
        }

        if (!nameMatches(")")) throw new SyntaxException(")", token);

        goToNextToken();

        if (!nameMatches(";")) throw new SyntaxException(";", token);

        goToNextToken();
    }

    /**
     * compound_command -> begin optional_commands end
     * <p>
     * optional_command -> commands_list | E
     */
    private void checkCompoundCommand() {
        if (!nameMatches("begin")) throw new SyntaxException("begin", token);
        goToNextToken();

        // command; command_list
        while (!nameMatches("end")) {
            checkCommands();
        }
        goToNextToken();
    }

    /**
     * commands_list -> command | commands_list; command
     */
    private void checkCommands() {
        // command;
        checkCommand();
        if (!nameMatches(";")) throw new SyntaxException(";", token);
        goToNextToken();
    }

    /**
     * command -> variable := expression | procedure_call | compound_command
     * | if expression then command else_part
     * | while expression do command
     * <p>
     * else_part -> else command | E
     */
    private void checkCommand() {
        // variable := expression
        if (typeMatches(IDENTIFIER)) {

            semanticAnalysis.setVariable(token);

            goToNextToken();
            if (typeMatches(ATTRIBUTION)) {
                goToNextToken();
                checkExpression();

                semanticAnalysis.endExpression();

            }
            // | procedure call
            else if (nameMatches("(")) {
                goToNextToken();
                checkExpression();
                while (nameMatches(",")) {
                    goToNextToken();
                    checkExpression();
                }
                if (!nameMatches(")")) throw new SyntaxException(")", token);
                goToNextToken();
            }
        }
        // | if expression then command else_part
        else if (nameMatches("if")) {
            goToNextToken();
            checkExpression();

            if(!nameMatches("then")) throw new SyntaxException("then", token);
            goToNextToken();

            checkCommand();

            // else_part -> else | E
            if(nameMatches("else")) {
                goToNextToken();
                checkCommand();
            }
        }
        // | while expression do command
        else if(nameMatches("while")) {
            goToNextToken();
            checkExpression();

            if(!nameMatches("do")) throw new SyntaxException("do", token);
            goToNextToken();

            checkCommand();
        }
        // | do command while(expression);
        else if(nameMatches("do")) {
            goToNextToken();
            checkCommand();

            if(!nameMatches(";")) throw new SyntaxException(";", token);
            goToNextToken();

            if(!nameMatches("while")) throw new SyntaxException("while", token);
            goToNextToken();

            if(!nameMatches("(")) throw new SyntaxException("(", token);
            goToNextToken();

            checkExpression();

            if(!nameMatches(")")) throw new SyntaxException(")", token);
            goToNextToken();

        }
        // | compound_command
        else if(nameMatches("begin")){
            checkCompoundCommand();
        }
    }

    /**
     * expression -> simple expression | simple_expression relational_op simple_expression
     */
    private void checkExpression() {
        checkSimpleExpression();
        if (typeMatches(RELATIONAL_OPERATOR)) {

            semanticAnalysis.checkType(token);

            goToNextToken();
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

            semanticAnalysis.checkType(token);

            goToNextToken();
            checkTerm();
        }

        // | term additive_op simple_exp
        // additive_op -> + | - | or
        if (typeMatches(ADDITIVE_OPERATOR) || nameMatches("or")) {

            semanticAnalysis.checkType(token);

            goToNextToken();
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

            semanticAnalysis.checkType(token);

            goToNextToken();
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

            semanticAnalysis.checkType(token);

            goToNextToken();
        }
        // | (expression)
        else if (nameMatches("(")) {
            goToNextToken();
            checkExpression();

            if (!nameMatches(")")) throw new SyntaxException(")", token);
            goToNextToken();
        }
        // | true
        // | false
        else if (nameMatches("true") || nameMatches("false")) {

            semanticAnalysis.pushValue(token);

            goToNextToken();
        }
        // | not factor
        else if (nameMatches("not")) {

            semanticAnalysis.pushValue(token);

            goToNextToken();
            checkFactor();
        }else if(typeMatches(REAL_3D)) {
            goToNextToken();
        }
    }
}
