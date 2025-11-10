package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractTabulatedFunctionTest {

    @Test
    public void testCheckLengthIsTheSameWithValidArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
    }

    @Test
    public void testCheckLengthIsTheSameWithDifferentLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    public void testCheckSortedWithValidArray() {
        double[] sortedArray = {1.0, 2.0, 3.0, 4.0};
        AbstractTabulatedFunction.checkSorted(sortedArray);
    }

    @Test
    public void testCheckSortedWithUnsortedArray() {
        double[] unsortedArray = {1.0, 3.0, 2.0, 4.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(unsortedArray);
        });
    }

    @Test
    public void testCheckSortedWithDuplicateValues() {
        double[] arrayWithDuplicates = {1.0, 2.0, 2.0, 3.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(arrayWithDuplicates);
        });
    }

    @Test
    public void testToStringWithArrayTabulatedFunction() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();
        String expected = "ArrayTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]\n";

        assertEquals(expected, result);
    }

    @Test
    public void testToStringWithLinkedListTabulatedFunction() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String result = function.toString();
        String expected = "LinkedListTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]\n";

        assertEquals(expected, result);
    }

    @Test
    public void testToStringWithTwoPoints() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();
        String expected = "ArrayTabulatedFunction size = 2\n[1.0; 3.0]\n[2.0; 4.0]\n";

        assertEquals(expected, result);
    }

    @Test
    public void testToStringWithManyPoints() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String result = function.toString();

        assertTrue(result.startsWith("LinkedListTabulatedFunction size = 5"));
        assertTrue(result.contains("[1.0; 10.0]"));
        assertTrue(result.contains("[2.0; 20.0]"));
        assertTrue(result.contains("[3.0; 30.0]"));
        assertTrue(result.contains("[4.0; 40.0]"));
        assertTrue(result.contains("[5.0; 50.0]"));
        assertEquals(6, result.split("\n").length);

    }

    @Test
    public void testToStringWithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();

        assertTrue(result.contains("[-2.0; 4.0]"));
        assertTrue(result.contains("[-1.0; 1.0]"));
        assertTrue(result.contains("[0.0; 0.0]"));
        assertTrue(result.contains("[1.0; 1.0]"));
        assertTrue(result.contains("[2.0; 4.0]"));
    }

    @Test
    public void testToStringFormatConsistency() {
        double[] xValues = {1.5, 2.5, 3.5};
        double[] yValues = {2.25, 6.25, 12.25};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();
        String[] lines = result.split("\n");

        assertEquals("ArrayTabulatedFunction size = 3", lines[0]);
        assertTrue(lines[1].matches("\\[\\d+\\.\\d+; \\d+\\.\\d+\\]"));
        assertTrue(lines[2].matches("\\[\\d+\\.\\d+; \\d+\\.\\d+\\]"));
        assertTrue(lines[3].matches("\\[\\d+\\.\\d+; \\d+\\.\\d+\\]"));
    }

    @Test
    public void testToStringUsesCorrectClassName() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        String arrayResult = arrayFunction.toString();
        assertTrue(arrayResult.startsWith("ArrayTabulatedFunction"));

        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);
        String linkedListResult = linkedListFunction.toString();
        assertTrue(linkedListResult.startsWith("LinkedListTabulatedFunction"));
    }

    @Test
    public void testToStringLineEndings() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();
        String[] lines = result.split("\n");

        assertEquals(3, lines.length);
        assertEquals("ArrayTabulatedFunction size = 2", lines[0]);
        assertEquals("[1.0; 1.0]", lines[1]);
        assertEquals("[2.0; 4.0]", lines[2]);
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void testToStringWithDecimalValues() {
        double[] xValues = {0.1, 0.2, 0.3};
        double[] yValues = {0.01, 0.04, 0.09};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String result = function.toString();

        assertTrue(result.contains("[0.1; 0.01]"));
        assertTrue(result.contains("[0.2; 0.04]"));
        assertTrue(result.contains("[0.3; 0.09]"));
    }

    @Test
    public void testToStringWithZeroValues() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 0.0, 0.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();

        assertTrue(result.contains("[0.0; 0.0]"));
        assertTrue(result.contains("[1.0; 0.0]"));
        assertTrue(result.contains("[2.0; 0.0]"));
    }
}
