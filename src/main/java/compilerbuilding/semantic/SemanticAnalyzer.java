package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;
import compilerbuilding.lexical.TokenType;

import java.util.Stack;

import static compilerbuilding.semantic.SemanticResult.DEFINED_VARIABLE;
import static compilerbuilding.semantic.SemanticResult.UNDEFINED_VARIABLE;

public class SemanticAnalyzer implements SemanticAnalysis {

    private static final String MARK = "$";

    private int scopeCounter;
    private Stack<String> stack;
    private SemanticResult result;

    public SemanticAnalyzer() {
        stack = new Stack<>();
        result = new SemanticResult();
    }

    @Override
    public void startProgram() {
        stack.push(MARK);
    }

    public boolean exists(String identifier) {
        // Search method returns -1 when an object is not found
        return stack.search(identifier) != -1;
    }

    @Override
    public void push(Token token) {
        if (existsInCurrentScope(token.getName())) {
            result.add(token, DEFINED_VARIABLE);
        } else {
            stack.push(token.getName());
        }
    }

    @Override
    public void checkDefinition(Token token) {
        // Search method returns -1 when an object is not found
        if(token.getType().equals(TokenType.IDENTIFIER) && stack.search(token.getName()) == -1) {
            result.add(token, UNDEFINED_VARIABLE);
        }
    }

    @Override
    public void openScope() {
        stack.push(MARK);
    }

    @Override
    public void closeScope() {
        while (!stack.pop().equals(MARK));
    }

    @Override
    public void showResult() {
        if(result.getResults().size() > 0) {
            result.getResults().forEach(System.err::println);
        }
        else {
            System.out.println("Semantic OK");
        }
    }

    public Stack<String> getStack() {
        return stack;
    }

    private boolean existsInCurrentScope(String identifier) {
        for(int i = stack.size() - 1; !stack.get(i).equals(MARK); i --) {
            if(stack.get(i).equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasError() {
        return result.getResults().size() > 0;
    }
}
