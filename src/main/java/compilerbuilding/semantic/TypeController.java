package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;
import compilerbuilding.lexical.TokenType;

import java.util.Stack;

import static compilerbuilding.semantic.DataType.*;
import static compilerbuilding.semantic.SemanticResult.INVALID_OPERATION;

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

    public void update(int line) {
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
            } else if (isRelationalOperation(operator)) {
                stack.push(BOOLEAN);
            } else if (isLogicOperation(operator)) {
                System.out.println("logic op");
            } else {
                semanticResult.add(t1, t2, line);
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

        if (variableDataType.equals(INTEGER)) {
            if (!rightSideDataType.equals(INTEGER)) {
                semanticResult.add(variable, INVALID_OPERATION);
            }
        } else if (variableDataType.equals(REAL)) {
            if (!rightSideDataType.equals(REAL) && !rightSideDataType.equals(INTEGER)) {
                semanticResult.add(variable, INVALID_OPERATION);
            }
        }
        else if(variableDataType.equals(BOOLEAN)) {
            if (!rightSideDataType.equals(BOOLEAN)) {
                semanticResult.add(variable, INVALID_OPERATION);
            }
        }
        stack.clear();
    }

    private boolean isNumericOperation(String operator) {
//        return operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/");
        return operator.equalsIgnoreCase(TokenType.ADDITIVE_OPERATOR) ||
                operator.equalsIgnoreCase(TokenType.MULTIPLICATIVE_OPERATOR);
    }

    private boolean isRelationalOperation(String operator) {
        return operator.equalsIgnoreCase(TokenType.RELATIONAL_OPERATOR);
    }

    private boolean isLogicOperation(String operator) {
        return operator.equalsIgnoreCase(TokenType.RELATIONAL_OPERATOR);
    }
}
