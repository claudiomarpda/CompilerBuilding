package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.lexical.Token;
import compilerbuilding.syntax.SyntacticAnalyzer;
import compilerbuilding.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Token> tokens = run(0);
        new SyntacticAnalyzer(tokens).analyze();
    }

    private static List<Token> run(int index) throws IOException {
        String fileName = "input" + index + ".pas";

        String input = FileUtil.readFileAsString("input/" + fileName);
        List<Token> tokens = new LexicalAnalyzer().analyze(input);
        FileUtil.writeTokensToFile("output/" + fileName, tokens);
        return tokens;
    }

}
