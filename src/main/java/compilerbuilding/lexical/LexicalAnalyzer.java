package compilerbuilding.lexical;

public final class LexicalAnalyzer {

    private int lineIndex = 0;

    public void analyze(String input) {
        for (String line : input.split("\n")) {
            checkLine(line);
        }
    }

    private void checkLine(String line) {
        lineIndex++;
        for (String token : line.split(" ")) {
            token = token.trim().toLowerCase();
            System.out.println(":" + token + ".");
        }
    }

}
