package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.lexical.Token;
import compilerbuilding.semantic.SemanticAnalyzer;
import compilerbuilding.syntactic.SyntacticAnalyzer;
import compilerbuilding.syntactic.exception.SyntaxException;
import compilerbuilding.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        for (int i = 0; i <= 0; i++) {
            if (i == 3) continue;

            List<Token> tokens = runLexical(i);
            runSyntactic(i, tokens);
        }
    }

    private static List<Token> runLexical(int index) throws IOException {
        String fileName = "input" + index + ".pas";

        String input = FileUtil.readFileAsString("input/" + fileName);
        List<Token> tokens = new LexicalAnalyzer().analyze(input);
        FileUtil.writeTokensToFile("output/tokens-" + fileName, tokens);
        return tokens;
    }

    private static void runSyntactic(int index, List<Token> tokens) {
        try {
            new SyntacticAnalyzer(tokens, new SemanticAnalyzer()).analyze();
        } catch (SyntaxException e) {
            System.err.println("Syntax error in program of index " + index);
            e.printStackTrace();
        }
    }

}
