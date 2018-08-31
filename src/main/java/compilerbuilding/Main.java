package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.lexical.Token;
import compilerbuilding.syntactic.SyntacticAnalyzer;
import compilerbuilding.syntactic.exception.SyntaxException;
import compilerbuilding.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        for (int i = 0; i <= 6; i++) {
            if(i == 3) continue;

            List<Token> tokens = run(i);
            try {
                new SyntacticAnalyzer(tokens).analyze();
            } catch (SyntaxException e) {
                System.out.println("Syntax error in program of index " + i);
                e.printStackTrace();
            }
        }
    }

    private static List<Token> run(int index) throws IOException {
        String fileName = "input" + index + ".pas";

        String input = FileUtil.readFileAsString("input/" + fileName);
        List<Token> tokens = new LexicalAnalyzer().analyze(input);
        FileUtil.writeTokensToFile("output/tokens-" + fileName, tokens);
        return tokens;
    }

}
