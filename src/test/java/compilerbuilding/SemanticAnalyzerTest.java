package compilerbuilding;

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
        semanticAnalysis.push("a");
        semanticAnalysis.push("b");
        semanticAnalysis.push("c");
        semanticAnalysis.openScope();
        semanticAnalysis.push("d");
        semanticAnalysis.push("e");
        semanticAnalysis.push("f");

        semanticAnalysis.closeScope();
        boolean isEmpty = ((SemanticAnalyzer) semanticAnalysis).getStack().isEmpty();
        assertFalse(isEmpty);

        semanticAnalysis.closeScope();
        isEmpty = ((SemanticAnalyzer) semanticAnalysis).getStack().isEmpty();
        assertTrue(isEmpty);
    }
}
