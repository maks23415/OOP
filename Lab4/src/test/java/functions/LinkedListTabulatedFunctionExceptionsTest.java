package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTabulatedFunctionExceptionsTest {
    @Test
    public void testConstructorWithValidArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        assertNotNull(function);
    }

    @Test
    public void testConstructorWithDifferentLengthArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testConstructorWithUnsortedXValues() {
        double[] xValues = {1.0, 3.0, 2.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testInterpolationException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.apply(1.5);

        assertEquals(2.5, function.apply(1.5), 0.0001);
        assertEquals(6.5, function.apply(2.5), 0.0001);
    }
}
