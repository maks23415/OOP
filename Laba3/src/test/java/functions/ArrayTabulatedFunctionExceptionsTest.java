package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionExceptionsTest {
    @Test
    public void testConstructorWithValidArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        assertNotNull(function);
    }

    @Test
    public void testConstructorWithDifferentLengthArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testConstructorWithUnsortedXValues() {
        double[] xValues = {1.0, 3.0, 2.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testInterpolationException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(2.5, function.apply(1.5), 0.0001); // между 1.0 и 2.0
        assertEquals(6.5, function.apply(2.5), 0.0001);
    }

    @Test
    public void testValidInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.apply(1.5);
        function.apply(2.0);
        function.apply(0.5);
        function.apply(3.5);
    }
}
