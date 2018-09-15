package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;

import java.util.Stack;

import static compilerbuilding.semantic.DataType.INTEGER;
import static compilerbuilding.semantic.DataType.REAL;

public class TypeController {

    private Token variable;
    private String variableDataType;
    private Stack<String> stack;
    private SemanticResult semanticResult;

    public TypeController(SemanticResult semanticResult) {
        this.stack = new Stack<>();
        this.semanticResult = semanticResult;
    }

    public void push(String type) {
        stack.push(type.toLowerCase());
    }

    public void update() {
        if (stack.size() >= 3) {
            String t1 = stack.pop();
            String operator = stack.pop();
            String t2 = stack.pop();

            if (isNumericOperation(operator)) {
                if (t1.equals(INTEGER) && t2.equals(INTEGER)) {
                    stack.push(INTEGER);
                } else if (t1.equals(REAL) && t2.equals(REAL)) {
                    stack.push(REAL);
                } else if (t1.equals(INTEGER) && t2.equals(REAL)) {
                    stack.push(REAL);
                } else if (t1.equals(REAL) && t2.equals(INTEGER)) {
                    stack.push(REAL);
                }
            } else {
                semanticResult.add(t1, t2, -1);
            }
        }
    }

    public void setVariable(Token token, String dataType) {
        this.variable = token;
        this.variableDataType = dataType.toLowerCase();
    }

    public void checkAssignment() {
        if (stack.isEmpty()) {
            return;
        }

        String rightSideDataType = stack.pop();
        if (!variableDataType.equals(rightSideDataType)) {
            semanticResult.add(variable, "Invalid operation: ");
        }
        stack.clear();
    }

    private boolean isNumericOperation(String operator) {
        return operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/");
    }
}
