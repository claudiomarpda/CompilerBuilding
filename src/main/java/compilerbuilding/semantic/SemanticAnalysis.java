package compilerbuilding.semantic;

import compilerbuilding.lexical.Token;

public interface SemanticAnalysis {

    void startProgram();

    void push(Token token);

    void openScope();

    void closeScope();

    void showResult();

    boolean hasError();

    void checkType(Token token);

    void identifyType(int identifiers, String type);
}
