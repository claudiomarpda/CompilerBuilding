package compilerbuilding.syntax.exception;

import compilerbuilding.lexical.Token;

public class SyntaxException extends RuntimeException {

    public SyntaxException(String shouldBe, Token token) {
        super("At line " + token.getLine() + ". " +
                "Token should be " + shouldBe + " but " + token.getName() + " was found");
    }
}
