package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTabulatedFunctionTest
{

    private final double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
    private final double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
    private final LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

    @Test
    void testConstructorWithArrays()
    {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(5, func.getCount());
        assertEquals(0.0, func.leftBound());
        assertEquals(4.0, func.rightBound());

        double[] shortX = {1.0, 2.0};
        double[] longY = {1.0, 2.0, 3.0};
        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(shortX, longY));
    }

    @Test
    void testConstructorWithFunction()
    {
        MathFunction sqr = new SqrFunction();
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(sqr, 0, 4, 5);

        assertEquals(5, func.getCount());
        assertEquals(0.0, func.getX(0));
        assertEquals(4.0, func.getX(4));
        assertEquals(0.0, func.getY(0));
        assertEquals(16.0, func.getY(4));

        LinkedListTabulatedFunction singlePoint = new LinkedListTabulatedFunction(sqr, 2.0, 2.0, 1);
        assertEquals(1, singlePoint.getCount());
        assertEquals(2.0, singlePoint.getX(0));
        assertEquals(4.0, singlePoint.getY(0));

        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(sqr, 0, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(sqr, 0, 1, -1));
    }

    @Test
    void testGetCount()
    {
        assertEquals(5, function.getCount());
    }

    @Test
    void testGetX()
    {
        assertEquals(0.0, function.getX(0));
        assertEquals(2.0, function.getX(2));
        assertEquals(4.0, function.getX(4));

        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(5));
    }

    @Test
    void testGetY()
    {
        assertEquals(0.0, function.getY(0));
        assertEquals(4.0, function.getY(2));
        assertEquals(16.0, function.getY(4));

        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(5));
    }

    @Test
    void testSetY()
    {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        func.setY(2, 10.0);
        assertEquals(10.0, func.getY(2));

        assertEquals(1.0, func.getY(1));
        assertEquals(9.0, func.getY(3));

        assertThrows(IllegalArgumentException.class, () -> func.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> func.setY(10, 5.0));
    }

    @Test
    void testIndexOfX()
    {
        assertEquals(0, function.indexOfX(0.0));
        assertEquals(2, function.indexOfX(2.0));
        assertEquals(4, function.indexOfX(4.0));
        assertEquals(-1, function.indexOfX(5.0));
        assertEquals(-1, function.indexOfX(-1.0));
    }

    @Test
    void testIndexOfY()
    {
        assertEquals(0, function.indexOfY(0.0));
        assertEquals(2, function.indexOfY(4.0));
        assertEquals(4, function.indexOfY(16.0));
        assertEquals(-1, function.indexOfY(100.0));
        assertEquals(-1, function.indexOfY(-5.0));
    }

    @Test
    void testLeftBound()
    {
        assertEquals(0.0, function.leftBound());
    }

    @Test
    void testRightBound()
    {
        assertEquals(4.0, function.rightBound());
    }

    @Test
    void testFloorIndexOfX()
    {
        assertEquals(0, function.floorIndexOfX(-1.0));
        assertEquals(0, function.floorIndexOfX(0.0));
        assertEquals(1, function.floorIndexOfX(0.5));
        assertEquals(2, function.floorIndexOfX(2.0));
        assertEquals(3, function.floorIndexOfX(2.5));
        assertEquals(5, function.floorIndexOfX(5.0));
    }

    @Test
    void testExtrapolateLeft()
    {
        double result = function.extrapolateLeft(-1.0);
        double expected = -1.0;
        assertEquals(expected, result, 1e-10);

        double[] singleX = {2.0};
        double[] singleY = {4.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.extrapolateLeft(1.0));
    }

    @Test
    void testExtrapolateRight()
    {
        double result = function.extrapolateRight(5.0);
        double expected = 25.0;
        assertEquals(expected, result, 1e-10);

        double[] singleX = {2.0};
        double[] singleY = {4.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.extrapolateRight(3.0));
    }

    @Test
    void testInterpolateWithIndex()
    {
        double result = function.interpolate(1.5, 1);
        double expected = 2.5;
        assertEquals(expected, result, 1e-10);

        assertThrows(IllegalArgumentException.class, () -> function.interpolate(1.5, -1));
        assertThrows(IllegalArgumentException.class, () -> function.interpolate(1.5, 4));
    }

    @Test
    void testInterpolateWithValues()
    {
        double result = function.interpolate(1.5, 1.0, 2.0, 1.0, 4.0);
        double expected = 2.5;
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testApply()
    {
        assertEquals(0.0, function.apply(0.0));
        assertEquals(4.0, function.apply(2.0));
        assertEquals(16.0, function.apply(4.0));

        double interpolated = function.apply(1.5);
        double expected = 2.5;
        assertEquals(expected, interpolated, 1e-10);

        double leftExtrapolated = function.apply(-1.0);
        assertEquals(-1.0, leftExtrapolated, 1e-10);

        double rightExtrapolated = function.apply(5.0);
        assertEquals(25.0, rightExtrapolated, 1e-10);
    }

    @Test
    void testComplexScenarios() {

        MathFunction sqr = new SqrFunction();
        LinkedListTabulatedFunction reversed = new LinkedListTabulatedFunction(sqr, 4, 0, 5);
        assertEquals(0.0, reversed.getX(0), 1e-10);  // первая точка
        assertEquals(4.0, reversed.getX(4), 1e-10);  // последняя точка

        double[] negX = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] negY = {4.0, 1.0, 0.0, 1.0, 4.0};
        LinkedListTabulatedFunction negFunc = new LinkedListTabulatedFunction(negX, negY);

        assertEquals(-2.0, negFunc.leftBound(), 1e-10);
        assertEquals(2.0, negFunc.rightBound(), 1e-10);
        assertEquals(4.0, negFunc.apply(-2.0), 1e-10);
        assertEquals(0.0, negFunc.apply(0.0), 1e-10);
    }

    @Test
    void testEmptyFunction()
    {
        double[] singleX = {1.0};
        double[] singleY = {2.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(1, singleFunc.getCount());
        assertEquals(1.0, singleFunc.leftBound());
        assertEquals(1.0, singleFunc.rightBound());
    }
}