package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static compilerbuilding.lexical.TokenType.IDENTIFIER;
import static compilerbuilding.lexical.TokenType.UNDEFINED;
import static compilerbuilding.semantic.DataType.BOOLEAN;
import static compilerbuilding.semantic.SemanticResult.DEFINED_SUBPROGRAM;
import static compilerbuilding.semantic.SemanticResult.DEFINED_VARIABLE;
import static compilerbuilding.semantic.SemanticResult.UNDEFINED_VARIABLE;

public class SemanticAnalyzer implements SemanticAnalysis {

    private static final String MARK = "$";

    private Stack<String> stack;
    private SemanticResult result;
    private List<Identifier> identifiers;
    private List<Identifier> variableIdentifiers;
    private List<Identifier> subprogramIdentifiers;
    private TypeController typeController;

    public SemanticAnalyzer() {
        stack = new Stack<>();
        result = new SemanticResult();
        identifiers = new ArrayList<>();
        variableIdentifiers = new ArrayList<>();
        subprogramIdentifiers = new ArrayList<>();
        typeController = new TypeController(result);
    }

    @Override
    public void startProgram() {
        stack.push(MARK);
    }

    @Override
    public void pushToken(Token token) {
        if (existsInCurrentScope(token.getName())) {
            result.add(token, DEFINED_VARIABLE);
        } else {
            stack.push(token.getName());

            if (token.getType().equals(IDENTIFIER)) {
                // Identifier data type will be set latter
                identifiers.add(new Identifier(token.getName(), UNDEFINED, token.getLine()));
            }
        }
    }

    @Override
    public void openScope() {
        stack.push(MARK);
    }

    @Override
    public void closeScope() {
        while (!stack.pop().equals(MARK)) {
            if (identifiers.size() > 0) {
                identifiers.remove(identifiers.size() - 1);
            }
        }
    }

    @Override
    public void showResult() {
        if (result.getResults().size() > 0) {
            result.getResults().forEach(System.err::println);
        } else {
            System.out.println("Semantic OK");
        }
    }

    public Stack<String> getStack() {
        return stack;
    }

    private boolean existsInCurrentScope(String identifier) {
        for (int i = stack.size() - 1; !stack.get(i).equals(MARK); i--) {
            if (stack.get(i).equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasError() {
        return result.getResults().size() > 0;
    }

    @Override
    public void checkType(Token token) {
        if (token.getType().equals(IDENTIFIER)) {
            // Search method returns -1 when object is not found
            if (stack.search(token.getName()) == -1) {
                result.add(token, UNDEFINED_VARIABLE);
            } else {
                // Looks for the identifier's type in the current scope until the most outer scope
                String type = findMostRecentIdentifierType(token.getName());
                if(type == null) {
                    result.add(token, UNDEFINED_VARIABLE);
                }
                else {
                    typeController.push(type);
                }
            }
        }
        // It is a literal
        else {
            typeController.push(token.getType());
        }

        typeController.update(token.getLine());
    }

    public void pushValue(Token token) {
        if (token.getName().equals("true") || token.getName().equals("false")) {
            typeController.push(BOOLEAN);
        } else if (token.getName().equals("not")) {
            typeController.push(token.getName());
        }
        typeController.update(token.getLine());
    }

    /**
     * Set the type of a list of variable
     *
     * @param n:    number of variables
     * @param type: the same type for all
     */
    @Override
    public void identifyType(int n, String type) {
        for (int i = identifiers.size() - n; i < identifiers.size(); i++) {
            identifiers.get(i).setType(type);
        }
    }

    /**
     * Find the most recent identifier by name
     *
     * @param name: the name of the identifier
     * @return the type of the identifier or null
     */
    private String findMostRecentIdentifierType(String name) {
        for (int i = identifiers.size() - 1; i >= 0; i--) {
            if (identifiers.get(i).getName().equals(name)) {
                return identifiers.get(i).getType();
            }
        }
        return null;
    }

    @Override
    public void endExpression() {
        typeController.checkAssignment();
    }

    /**
     * Means the start of an attribution
     * @param token of the variable
     */
    @Override
    public void setVariable(Token token) {
        String dataType = findMostRecentIdentifierType(token.getName());
        typeController.setVariable(token, dataType);
    }

    @Override
    public void pushSubprogram(Token token) {
        // If subprogram identifier exists
        if (subprogramIdentifiers.stream().anyMatch(c -> c.getName().equals(token.getName()))) {
            result.add(token, DEFINED_SUBPROGRAM);
        } else {
            subprogramIdentifiers.add(new Identifier(token.getName(), UNDEFINED, token.getLine()));
        }
    }
}
