package compilerbuilding;


import compilerbuilding.lexical.LexicalAnalyzer;
import compilerbuilding.util.FileUtil;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String input = FileUtil.readFileAsString("input/input1.txt");
        new LexicalAnalyzer().analyze(input);
        System.out.println(":" + "      ".trim() + ".");
    }















    private static String[] line(String code) {
        return code.split("\n");
    }

    public enum Test {
        TESTA("StringA"), TESTB("StringB");

        private String string;

        Test(String s) {
            string = s;
        }


        @Override
        public String toString() {
            return string;
        }

        public String[] all() {
            String[] allStrings = string.split(" ");
            for (String a : allStrings) System.out.println(a);
            return allStrings;
        }

    }
}
