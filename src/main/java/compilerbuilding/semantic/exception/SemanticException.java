package compilerbuilding.semantic.exception;

public class SemanticException extends RuntimeException {

    public static final String DECLARED_VARIABLE = "Variable already declared: ";

    public SemanticException(String cause, String identifier) {
        super(cause + identifier);
    }
}
