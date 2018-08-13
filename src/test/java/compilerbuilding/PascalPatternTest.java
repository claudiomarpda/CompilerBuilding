package compilerbuilding;

import compilerbuilding.lexical.PascalPattern;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static compilerbuilding.lexical.PascalPattern.*;
import static org.junit.Assert.*;

public class PascalPatternTest {

    private List<String> allTokens = new ArrayList<>();
    private Pattern patternsUnion;

    @Before
    public void setup() {
        patternsUnion = Pattern.compile(PATTERNS_UNION.pattern());

        allTokens.addAll(KEYWORDS);
        allTokens.addAll(DELIMITERS);
        allTokens.addAll(RELATIONAL_OPERATORS);
        allTokens.addAll(ADDITIVE_OPERATORS);
        allTokens.addAll(MULTIPLICATIVE_OPERATORS);
        allTokens.add(ATTRIBUTION_COMMAND);
    }

    /**
     * Tests each string used for creating pattern against the union of all patterns.
     */
    @Test
    public void shouldMatchAllTokensInPatternsUnion() {
        allTokens.forEach(c -> assertTrue(patternsUnion.matcher(c).matches()));
    }

    @Test
    public void identifierShouldNotMatch() {
        assertFalse(patternsUnion.matcher("_identifier").matches());
        assertFalse(patternsUnion.matcher("1abc21").matches());
        assertFalse(patternsUnion.matcher("121_").matches());
        assertFalse(patternsUnion.matcher("_121").matches());
        assertFalse(patternsUnion.matcher("identifier:").matches());
        assertFalse(patternsUnion.matcher("identifier;").matches());
        assertFalse(patternsUnion.matcher(":identifier").matches());
        assertFalse(patternsUnion.matcher(";identifier").matches());
        assertFalse(patternsUnion.matcher("identifier;;").matches());
        assertFalse(patternsUnion.matcher("identifier;.,").matches());
        assertFalse(patternsUnion.matcher("identi!fier").matches());
        assertFalse(patternsUnion.matcher("id.entifier").matches());

    }

    @Test
    public void punctuationShouldNotMatch() {
        assertFalse(patternsUnion.matcher("a2$").matches());
        assertFalse(patternsUnion.matcher("$$$$$").matches());
        assertFalse(patternsUnion.matcher("$a").matches());

        assertFalse(patternsUnion.matcher("a!").matches());
        assertFalse(patternsUnion.matcher("a@").matches());
        assertFalse(patternsUnion.matcher("#a").matches());
        assertFalse(patternsUnion.matcher("R$").matches());
        assertFalse(patternsUnion.matcher("a$").matches());
        assertFalse(patternsUnion.matcher("a$a").matches());
        assertFalse(patternsUnion.matcher("$a").matches());
        assertFalse(patternsUnion.matcher("&&").matches());

        assertTrue(patternsUnion.matcher("{").matches());
        assertTrue(patternsUnion.matcher("}").matches());

        assertFalse(patternsUnion.matcher("{{").matches());
        assertFalse(patternsUnion.matcher("}}").matches());
        assertFalse(patternsUnion.matcher("{a").matches());
        assertFalse(patternsUnion.matcher("a}").matches());

        // TODO: Reject these dudes alone: !, @, #, $, %
        // Why do they match? They are not in regex. Maybe because they are accepted as punctuation

        /*assertFalse(patternsUnion.matcher("!").matches());
        assertFalse(patternsUnion.matcher("@").matches());
        assertFalse(patternsUnion.matcher("#").matches());
        assertFalse(patternsUnion.matcher("$").matches());
        assertFalse(patternsUnion.matcher("%").matches());
        //assertFalse(patternsUnion.matcher("#").matches());*/

    }

    @Test
    public void testWithPrintsOnScreen() {
        String v = "identifier#;";
        Matcher matcher;
        int i = 0;
        do {
            matcher = PATTERNS_UNION.matcher(v);
            System.out.println(v + " - " + v.length() + " - " + matcher.matches());
            matcher.find();
            System.out.println("found: " + matcher.group());
            System.out.println("token1: " + v.substring(0, matcher.end() - 1));
            v = v.substring(matcher.end());

            System.out.println();
        } while (i < v.length());
    }

    @Test
    public void identifierShouldMatch() {
        assertTrue(patternsUnion.matcher("identifier12_3Four_Five___six7").matches());
    }

    @Test
    public void realShouldNotMatch() {
        assertFalse(patternsUnion.matcher(".9").matches());
    }

    @Test
    public void integerShouldMatch() {
        assertTrue(patternsUnion.matcher("0").matches());
        assertTrue(patternsUnion.matcher("9").matches());
        assertTrue(patternsUnion.matcher("99999999999").matches());
    }

    @Test
    public void realShouldMatch() {
        assertTrue(patternsUnion.matcher("0.").matches());
        assertTrue(patternsUnion.matcher("0.0").matches());
        assertTrue(patternsUnion.matcher("0.1").matches());
        assertTrue(patternsUnion.matcher("9.").matches());
        assertTrue(patternsUnion.matcher("9.0").matches());
        assertTrue(patternsUnion.matcher("999999999.").matches());
        assertTrue(patternsUnion.matcher("9.11111111").matches());
        assertTrue(patternsUnion.matcher("999999999.11111111").matches());
    }

    @Test
    public void shouldReadPatternAndStopAtUnknown() {
        String s = "identifier#";
        Matcher matcher = PATTERNS_UNION.matcher(s);
        assertFalse(matcher.matches());

        if (matcher.find()) {
            assertEquals("identifier", s.substring(0, matcher.start()));
        }
    }

    @Test
    public void shouldReadPatternAndStopAtComma() {
        String s = "identifier;";
        Matcher matcher = PATTERNS_UNION.matcher(s);
        assertFalse(matcher.matches());

        String firstCharacterOutOfPattern;
        String stringAsPattern;
        if (matcher.find()) {
            firstCharacterOutOfPattern = matcher.group();
            stringAsPattern = s.substring(0, matcher.start());
            assertEquals(";", firstCharacterOutOfPattern);
            assertEquals("identifier", stringAsPattern);
        }
    }

    @Test
    public void shouldReadArithmetic() {
        String[] tokens = {"0 + 0", "0+0", "0+ 0", "0 +0", "(0+0)", "(0 + 0)", "(0+ 0)", "(0 +0)"};
        checkWholeTokenAndCharactersAgainstPattern(tokens);
    }

    @Test
    public void delimitersAndLettersShouldBeSeparated() {
        String[] tokens = {"(identifier)", "( identifier )", "(identifier )", "( identifier)",
                "{identifier}", "{ identifier }", "{identifier }", "{ identifier}",
                "IDENTIFIER,", "n2:", "integer;", "final:", "integer;"};

        checkWholeTokenAndCharactersAgainstPattern(tokens);
    }

    private void checkWholeTokenAndCharactersAgainstPattern(String[] tokens) {
        for (String s : tokens) {
            Matcher matcher = PATTERNS_UNION.matcher(s);
            assertFalse(matcher.matches());
            matcher.reset();

            while (matcher.find()) {
                String c = matcher.group();
                assertTrue(PATTERNS_UNION.matcher(c).matches());
            }
        }
    }

    @Test
    public void symbolsShouldNotContainEmptyString() {
        assertFalse(PascalPattern.containsSymbol(""));
    }

    @Test
    public void inlineCommentShouldNotMatch() {
        String  s = "//";
        Matcher matcher = PATTERNS_UNION.matcher(s);
        assertFalse(matcher.matches());
    }

    @Test
    public void threeDPatternShouldSuccess() {
        String  s = "30.4X5.3Y3.12Z";
        Matcher matcher = PATTERNS_UNION.matcher(s);
        assertTrue(matcher.matches());
    }
}
