package compilerbuilding.syntactic.exception;

import compilerbuilding.lexical.Token;

public class SyntaxException extends RuntimeException {

    public SyntaxException(String shouldBe, Token token) {
        super("At line " + token.getLine() + ". " +
                "Expected '" + shouldBe + "' but '" + token.getName() + "' was found");
    }
}
