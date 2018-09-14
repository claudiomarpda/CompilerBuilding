package compilerbuilding.semantic;

import compilerbuilding.semantic.exception.SemanticException;

import java.util.Stack;

import static compilerbuilding.semantic.exception.SemanticException.DECLARED_VARIABLE;

public class SemanticAnalyzer implements SemanticAnalysis {

    private static final String MARK = "$";

    private int scopeCounter;
    private Stack<String> stack;

    public SemanticAnalyzer() {
        stack = new Stack<>();
    }

    @Override
    public void startProgram() {
        stack.push(MARK);
    }

    @Override
    public boolean exists(String identifier) {
        // Search method returns -1 when an object is not found
        return stack.search(identifier) != -1;
    }

    @Override
    public void push(String identifier) {
        if (exists(identifier)) {
            throw new SemanticException(DECLARED_VARIABLE, identifier);
        } else {
            stack.push(identifier);
        }
    }

    @Override
    public String pop() {
        return stack.pop();
    }

    @Override
    public void openScope() {
        stack.push(MARK);
    }

    @Override
    public void closeScope() {
        while (!stack.pop().equals(MARK));
    }

    public Stack<String> getStack() {
        return stack;
    }
}
