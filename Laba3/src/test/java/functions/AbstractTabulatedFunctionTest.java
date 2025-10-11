package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
