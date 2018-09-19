package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.lexical.Token;
import compilerbuilding.semantic.SemanticAnalysis;
import compilerbuilding.semantic.SemanticAnalyzer;
import compilerbuilding.syntactic.SyntacticAnalyzer;
import compilerbuilding.syntactic.SyntaxException;
import compilerbuilding.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        List<Token> tokens = runLexical(9);
        runSyntactic(9, tokens);

    }

    private static List<Token> runLexical(int index) throws IOException {
        String fileName = "input" + index + ".pas";

        String input = FileUtil.readFileAsString("input/" + fileName);
        List<Token> tokens = new LexicalAnalyzer().analyze(input);
        FileUtil.writeTokensToFile("output/tokens-" + fileName, tokens);
        return tokens;
    }

    private static void runSyntactic(int index, List<Token> tokens) {
        SemanticAnalysis semanticAnalysis = new SemanticAnalyzer();
        try {
            new SyntacticAnalyzer(tokens, semanticAnalysis).analyze();
        } catch (SyntaxException e) {
            System.err.println("Syntax error in program of index " + index);
            e.printStackTrace();
        }
        semanticAnalysis.showResult();
    }

}
