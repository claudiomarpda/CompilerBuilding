package compilerbuilding;

import compilerbuilding.lexical.Token;
import compilerbuilding.semantic.SemanticAnalysis;
import compilerbuilding.semantic.SemanticAnalyzer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SemanticAnalyzerTest {

    private SemanticAnalysis semanticAnalysis;

    @Before
    public void setUp() {
        semanticAnalysis = new SemanticAnalyzer();
    }

    @Test
    public void closeScopeShouldSucceed() {
        semanticAnalysis.openScope();
        semanticAnalysis.push(new Token("a", "Undefined", 0));
        semanticAnalysis.push(new Token("b", "Undefined", 0));
        semanticAnalysis.push(new Token("c", "Undefined", 0));
        semanticAnalysis.openScope();
        semanticAnalysis.push(new Token("d", "Undefined", 0));
        semanticAnalysis.push(new Token("e", "Undefined", 0));
        semanticAnalysis.push(new Token("f", "Undefined", 0));

        semanticAnalysis.closeScope();
        boolean isEmpty = ((SemanticAnalyzer) semanticAnalysis).getStack().isEmpty();
        assertFalse(isEmpty);

        semanticAnalysis.closeScope();
        isEmpty = ((SemanticAnalyzer) semanticAnalysis).getStack().isEmpty();
        assertTrue(isEmpty);
    }

    @Test
    public void existsInCurrentScopeShouldFail() {
        semanticAnalysis.openScope();
        semanticAnalysis.push(new Token("a", "Undefined", 0));
        semanticAnalysis.push(new Token("b", "Undefined", 0));
        semanticAnalysis.openScope();
        semanticAnalysis.push(new Token("a", "Undefined", 0));
        semanticAnalysis.push(new Token("b", "Undefined", 0));
        System.out.println("Test hasn't failed so far. Good.");
        semanticAnalysis.push(new Token("b", "Undefined", 0));
        boolean hasError = semanticAnalysis.hasError();
        assertTrue(hasError);
    }
}
