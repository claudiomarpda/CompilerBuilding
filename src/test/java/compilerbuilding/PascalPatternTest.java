package compilerbuilding;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static compilerbuilding.lexical.PascalPattern.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
