package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.util.FileUtil;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String input = FileUtil.readFileAsString("input/input1.txt");
        new LexicalAnalyzer().analyze(input);

    }

}
