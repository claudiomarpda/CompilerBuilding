package compilerbuilding.semantic;

public interface SemanticAnalysis {

    void startProgram();

    boolean exists(String identifier);

    void push(String identifier);

    String pop();

    void openScope();

    void closeScope();

}
