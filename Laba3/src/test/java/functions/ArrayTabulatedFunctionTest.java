package functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionTest {

    private ArrayTabulatedFunction arrayFunction;

    @BeforeEach
    public void setUp() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {2, 4, 6, 8};
        arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
    }

    @Test
    public void testArrayTabulatedFunctionConstructor() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {2, 4, 6, 8};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1, function.getX(0));
        assertEquals(8, function.getY(3));
    }

    @Test
    public void testConstructorFromMathFunction() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 0, 4, 5);

        assertEquals(5, function.getCount());
        assertEquals(0, function.getX(0), 0.0001);
        assertEquals(16, function.getY(4), 0.0001);
    }

    @Test
    public void testInvalidConstructor() {
        double[] xValues = {2, 1};
        double[] yValues = {2, 3};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testApply() {
        assertEquals(4, arrayFunction.apply(2), 0.0001);
        assertEquals(5, arrayFunction.apply(2.5), 0.0001);
        assertEquals(0, arrayFunction.apply(0), 0.0001);
        assertEquals(10, arrayFunction.apply(5), 0.0001);
    }

    @Test
    public void testLeftBound() {
        double expected = 1;
        double actual = arrayFunction.leftBound();
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testRightBound() {
        double expected = 4;
        double actual = arrayFunction.rightBound();
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testGetX() {
        assertEquals(3, arrayFunction.getX(2), 0.0001);
    }

    @Test
    public void testGetY() {
        assertEquals(6, arrayFunction.getY(2), 0.0001);
    }

    @Test
    public void testIndexOfX() {
        assertEquals(1, arrayFunction.indexOfX(2));
        assertEquals(-1, arrayFunction.indexOfX(10));
    }

    @Test
    public void testIndexOfY() {
        assertEquals(2, arrayFunction.indexOfY(6));
        assertEquals(-1, arrayFunction.indexOfY(10));
    }

    @Test
    public void testFloorIndexOfX() {
        assertEquals(0, arrayFunction.floorIndexOfX(1.5));
        assertEquals(0, arrayFunction.floorIndexOfX(1.5));
        assertEquals(1, arrayFunction.floorIndexOfX(2.0));
        assertEquals(1, arrayFunction.floorIndexOfX(2.5));
        assertEquals(2, arrayFunction.floorIndexOfX(3.0));
        assertEquals(3, arrayFunction.floorIndexOfX(4.0));
        assertEquals(3, arrayFunction.floorIndexOfX(5.0));
    }

    @Test
    public void testInterpolate() {
        double expected = 5;
        double actual = arrayFunction.interpolate(2.5, 1);
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testExtrapolateLeft() {
        double expected = 0;
        double actual = arrayFunction.extrapolateLeft(0);
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testExtrapolateRight() {
        double expected = 10;
        double actual = arrayFunction.extrapolateRight(5);
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testSetY() {
        arrayFunction.setY(1, 10);
        assertEquals(10, arrayFunction.getY(1), 0.0001);
    }

    @Test
    public void testInvalidSetYIndex() {
        assertThrows(IllegalArgumentException.class, () ->
                arrayFunction.setY(-1, 5));
        assertThrows(IllegalArgumentException.class, () ->
                arrayFunction.setY(10, 5));
    }

    @Test
    public void testInvalidGetIndex() {
        assertThrows(IllegalArgumentException.class, () ->
                arrayFunction.getX(-1));
        assertThrows(IllegalArgumentException.class, () ->
                arrayFunction.getY(10));
    }

    @Test
    public void testInsertWhenXExists() {
        arrayFunction.insert(2.0, 10.0);

        assertEquals(4, arrayFunction.getCount());
        assertEquals(10.0, arrayFunction.getY(1), 0.0001);
        assertEquals(2.0, arrayFunction.getY(0), 0.0001);
        assertEquals(6.0, arrayFunction.getY(2), 0.0001);
    }

    @Test
    public void testInsertAtBeginning() {
        arrayFunction.insert(0.5, 1.0);

        assertEquals(5, arrayFunction.getCount());
        assertEquals(0.5, arrayFunction.getX(0), 0.0001);
        assertEquals(1.0, arrayFunction.getY(0), 0.0001);
        assertEquals(1.0, arrayFunction.getX(1), 0.0001);
        assertEquals(2.0, arrayFunction.getY(1), 0.0001);
    }

    @Test
    public void testInsertAtEnd() {
        arrayFunction.insert(5.0, 10.0);

        assertEquals(5, arrayFunction.getCount());
        assertEquals(4.0, arrayFunction.getX(3), 0.0001);
        assertEquals(8.0, arrayFunction.getY(3), 0.0001);
        assertEquals(5.0, arrayFunction.getX(4), 0.0001);
        assertEquals(10.0, arrayFunction.getY(4), 0.0001);
    }

    @Test
    public void testInsertInMiddle() {
        arrayFunction.insert(2.5, 5.0);

        assertEquals(5, arrayFunction.getCount());
        assertEquals(2.0, arrayFunction.getX(1), 0.0001);
        assertEquals(4.0, arrayFunction.getY(1), 0.0001);
        assertEquals(2.5, arrayFunction.getX(2), 0.0001);
        assertEquals(5.0, arrayFunction.getY(2), 0.0001);
        assertEquals(3.0, arrayFunction.getX(3), 0.0001);
        assertEquals(6.0, arrayFunction.getY(3), 0.0001);
    }

    @Test
    public void testInsertMultipleElements() {
        arrayFunction.insert(1.5, 3.0);
        arrayFunction.insert(2.5, 5.0);
        arrayFunction.insert(3.5, 7.0);

        assertEquals(7, arrayFunction.getCount());

        assertEquals(1.0, arrayFunction.getX(0), 0.0001);
        assertEquals(1.5, arrayFunction.getX(1), 0.0001);
        assertEquals(2.0, arrayFunction.getX(2), 0.0001);
        assertEquals(2.5, arrayFunction.getX(3), 0.0001);
        assertEquals(3.0, arrayFunction.getX(4), 0.0001);
        assertEquals(3.5, arrayFunction.getX(5), 0.0001);
        assertEquals(4.0, arrayFunction.getX(6), 0.0001);

        assertEquals(2.0, arrayFunction.getY(0), 0.0001);
        assertEquals(3.0, arrayFunction.getY(1), 0.0001);
        assertEquals(4.0, arrayFunction.getY(2), 0.0001);
        assertEquals(5.0, arrayFunction.getY(3), 0.0001);
        assertEquals(6.0, arrayFunction.getY(4), 0.0001);
        assertEquals(7.0, arrayFunction.getY(5), 0.0001);
        assertEquals(8.0, arrayFunction.getY(6), 0.0001);
    }

    @Test
    public void testInsertWithCapacityExpansion() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction smallFunction = new ArrayTabulatedFunction(xValues, yValues);

        smallFunction.insert(0.5, 5.0);
        smallFunction.insert(1.5, 15.0);
        smallFunction.insert(2.5, 25.0);
        smallFunction.insert(3.0, 30.0);

        assertEquals(6, smallFunction.getCount());
        assertEquals(0.5, smallFunction.getX(0), 0.0001);
        assertEquals(5.0, smallFunction.getY(0), 0.0001);
        assertEquals(3.0, smallFunction.getX(5), 0.0001);
        assertEquals(30.0, smallFunction.getY(5), 0.0001);
    }

    @Test
    public void testInsertMaintainsOrderWithRandomSequence() {
        arrayFunction.insert(2.5, 5.0);
        arrayFunction.insert(0.5, 1.0);
        arrayFunction.insert(4.5, 9.0);

        assertEquals(7, arrayFunction.getCount());
        for (int i = 1; i < arrayFunction.getCount(); i++) {
            assertTrue(arrayFunction.getX(i) > arrayFunction.getX(i - 1),
                    "Значение X должно увеличиваться по x " + i);
        }

        assertEquals(0.5, arrayFunction.getX(0), 0.0001);
        assertEquals(1.0, arrayFunction.getX(1), 0.0001);
        assertEquals(2.0, arrayFunction.getX(2), 0.0001);
        assertEquals(2.5, arrayFunction.getX(3), 0.0001);
        assertEquals(3.0, arrayFunction.getX(4), 0.0001);
        assertEquals(4.0, arrayFunction.getX(5), 0.0001);
        assertEquals(4.5, arrayFunction.getX(6), 0.0001);
    }

    @Test
    public void testInsertSameValueTwice() {
        arrayFunction.insert(2.5, 5.0);
        arrayFunction.insert(2.5, 10.0);

        assertEquals(5, arrayFunction.getCount());
        assertEquals(10.0, arrayFunction.getY(2), 0.0001);
    }

    @Test
    public void testInsertAndThenApply() {
        arrayFunction.insert(2.5, 5.0);

        assertEquals(5.0, arrayFunction.apply(2.5), 0.0001);

        assertEquals(4.5, arrayFunction.apply(2.25), 0.0001);
        assertEquals(5.5, arrayFunction.apply(2.75), 0.0001);
    }

    @Test
    public void testInsertBoundaryValues() {
        arrayFunction.insert(1.0000001, 2.0000001);
        arrayFunction.insert(3.9999999, 7.9999999);
        assertEquals(6, arrayFunction.getCount());

        assertEquals(1.0, arrayFunction.getX(0), 0.0001);
        assertEquals(1.0000001, arrayFunction.getX(1), 0.0001);
        assertEquals(3.9999999, arrayFunction.getX(4), 0.0001);
        assertEquals(4.0, arrayFunction.getX(5), 0.0001);
    }

    @Test
    public void testInsertIntoSinglePointFunction() {
        double[] initialX = {1.0, 3.0};
        double[] initialY = {10.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(initialX, initialY);

        function.insert(2.0, 20.0);
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 0.0001);
        assertEquals(10.0, function.getY(0), 0.0001);
        assertEquals(2.0, function.getX(1), 0.0001);
        assertEquals(20.0, function.getY(1), 0.0001);

        function.insert(1.5, 15.0);
        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.0001);
        assertEquals(1.5, function.getX(1), 0.0001);
        assertEquals(2.0, function.getX(2), 0.0001);
        assertEquals(3.0, function.getX(3), 0.0001);
    }

    @Test
    public void testInsertPreservesFunctionality() {
        int originalCount = arrayFunction.getCount();

        arrayFunction.insert(2.5, 5.0);

        assertEquals(originalCount + 1, arrayFunction.getCount());
        assertEquals(1.0, arrayFunction.leftBound(), 0.0001);
        assertEquals(4.0, arrayFunction.rightBound(), 0.0001);
        assertEquals(1, arrayFunction.indexOfX(2.0));
        assertEquals(3, arrayFunction.indexOfY(6.0));

        assertEquals(0, arrayFunction.floorIndexOfX(1.0));
        assertEquals(0, arrayFunction.floorIndexOfX(1.5));
        assertEquals(4, arrayFunction.floorIndexOfX(4.0));
        assertEquals(4, arrayFunction.floorIndexOfX(5.0));
    }
    @Test
    void testRemoveFirstElement() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(2, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getY(0));
        assertEquals(9.0, function.getY(1));
    }

    @Test
    void testRemoveLastElement() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(1.0, function.getY(0));
        assertEquals(4.0, function.getY(1));
    }

    @Test
    void testRemoveMiddleElement() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(1.0, function.getY(0));
        assertEquals(9.0, function.getY(1));
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(2));
    }

    @Test
    void testRemoveSingleElement() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {2.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getY(0), 1e-10);

        function.remove(0);
        assertEquals(0, function.getCount());

        assertThrows(IllegalArgumentException.class, () -> function.getX(0));
    }

    @Test
    public void testConstructorWithEmptyArrays() {
        double[] emptyX = {};
        double[] emptyY = {};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(emptyX, emptyY);
        });
    }

    @Test
    public void testConstructorWithDifferentLengthArrays() {
        double[] xValues = {1, 2, 3};
        double[] yValues = {1, 2};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testConstructorWithNonIncreasingX() {
        double[] xValues = {1, 3, 2, 4};
        double[] yValues = {1, 2, 3, 4};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testConstructorWithEqualXValues() {
        double[] xValues = {1, 2, 2, 4};
        double[] yValues = {1, 2, 3, 4};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testConstructorFromMathFunctionWithInvalidCount() {
        MathFunction sqr = new SqrFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(sqr, 0, 4, 1);
        });
    }

    @Test
    public void testConstructorFromMathFunctionWithEqualBounds() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 2, 2, 3);

        assertEquals(3, function.getCount());
        assertEquals(2, function.getX(0), 0.0001);
        assertEquals(2, function.getX(1), 0.0001);
        assertEquals(2, function.getX(2), 0.0001);
        assertEquals(4, function.getY(0), 0.0001);
        assertEquals(4, function.getY(1), 0.0001);
        assertEquals(4, function.getY(2), 0.0001);
    }

    @Test
    public void testConstructorFromMathFunctionWithReversedBounds() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 4, 0, 5);

        assertEquals(5, function.getCount());
        assertEquals(0, function.getX(0), 0.0001);
        assertEquals(1, function.getX(1), 0.0001);
        assertEquals(2, function.getX(2), 0.0001);
        assertEquals(3, function.getX(3), 0.0001);
        assertEquals(4, function.getX(4), 0.0001);
    }

    @Test
    public void testCapacityExpansionOnMultipleInsertions() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction smallFunction = new ArrayTabulatedFunction(xValues, yValues);

        for (int i = 3; i <= 20; i++) {
            smallFunction.insert(i * 1.0, i * 10.0);
        }

        assertEquals(20, smallFunction.getCount());
        assertEquals(1.0, smallFunction.getX(0), 0.0001);
        assertEquals(20.0, smallFunction.getX(19), 0.0001);
        assertEquals(10.0, smallFunction.getY(0), 0.0001);
        assertEquals(200.0, smallFunction.getY(19), 0.0001);
    }

    @Test
    public void testApplyWithSinglePointFunction() {
        double[] xValues = {2.0, 5.0};
        double[] yValues = {4.0, 10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4.0, function.apply(2.0), 0.0001);
        assertEquals(10.0, function.apply(5.0), 0.0001);
        assertEquals(7.0, function.apply(3.5), 0.0001);
        assertEquals(0.0, function.apply(0.0), 0.0001);
        assertEquals(16.0, function.apply(8.0), 0.0001);
    }

    @Test
    public void testInterpolateWithEqualXValues() {
        double result = arrayFunction.interpolate(2.5, 2.0, 2.0, 4.0, 6.0);
        assertEquals(4.0, result, 0.0001);
    }

    @Test
    public void testInterpolateWithInvalidFloorIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            arrayFunction.interpolate(2.5, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            arrayFunction.interpolate(2.5, 10);
        });
    }

    @Test
    public void testExtrapolateWithSinglePoint() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 2.0};
        ArrayTabulatedFunction function  = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function .extrapolateLeft(0.0), 0.0001);
        assertEquals(-1.0, function .extrapolateRight(-1.0), 0.0001);
        assertEquals(3.0, function.extrapolateRight(3.0), 0.0001);
        assertEquals(4.0, function.extrapolateRight(4.0), 0.0001);
    }

    @Test
    public void testGetXWithInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.getX(10));
    }

    @Test
    public void testGetYWithInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.getY(10));
    }

    @Test
    public void testSetYWithInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.setY(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> arrayFunction.setY(10, 5));
    }


    @Test
    public void testIndexOfYWithPrecision() {
        assertEquals(2, arrayFunction.indexOfY(6.0));
        assertEquals(2, arrayFunction.indexOfY(6.0 + 0.9e-10));
        assertEquals(2, arrayFunction.indexOfY(6.0 - 0.9e-10));
        assertEquals(-1, arrayFunction.indexOfY(6.0 + 1.1e-10));
        assertEquals(-1, arrayFunction.indexOfY(6.1));
    }

    @Test
    public void testFloorIndexOfXBoundaryCases() {
        assertEquals(0, arrayFunction.floorIndexOfX(1.0));
        assertEquals(0, arrayFunction.floorIndexOfX(1.5));
        assertEquals(1, arrayFunction.floorIndexOfX(2.0));
        assertEquals(1, arrayFunction.floorIndexOfX(2.5));
        assertEquals(2, arrayFunction.floorIndexOfX(3.0));
        assertEquals(2, arrayFunction.floorIndexOfX(3.5));
        assertEquals(3, arrayFunction.floorIndexOfX(4.0));
        assertEquals(3, arrayFunction.floorIndexOfX(4.0001));
        assertEquals(3, arrayFunction.floorIndexOfX(5.0));
    }

    @Test
    public void testRemoveAllElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);
        function.remove(0);

        assertEquals(0, function.getCount());

        assertThrows(IllegalArgumentException.class, () -> function.getX(0));
        assertThrows(IllegalArgumentException.class, () -> function.getY(0));

    }

    @Test
    public void testRemoveWithLastElement() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2);
        function.remove(1);

        assertEquals(1, function.getCount());
        assertEquals(1.0, function.getX(0), 0.0001);
        assertEquals(1.0, function.getY(0), 0.0001);
    }

    @Test
    public void testApplyWithEmptyFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {2.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.remove(0);
        function.remove(0);
        assertEquals(0, function.getCount());

        assertThrows(Exception.class, () -> function.apply(1.0));
    }

    @Test
    public void testHighPrecisionInterpolation() {
        double[] xValues = {1.0, 1.000000001, 2.0};
        double[] yValues = {1.0, 1.000000001, 4.0};
        ArrayTabulatedFunction preciseFunction = new ArrayTabulatedFunction(xValues, yValues);

        double result = preciseFunction.apply(1.0000000005);
        assertTrue(result > 1.0 && result < 1.000000001);
    }

    @Test
    public void testMultipleOperationsPreserveOrder() {
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(
                new double[]{1.0, 3.0},
                new double[]{1.0, 9.0}
        );

        function.insert(2.0, 4.0);
        function.insert(4.0, 16.0);
        function.insert(0.5, 0.25);
        function.remove(2);

        assertEquals(4, function.getCount());
        for (int i = 1; i < function.getCount(); i++) {
            assertTrue(function.getX(i) > function.getX(i - 1));
        }

        assertEquals(0.5, function.getX(0), 0.0001);
        assertEquals(1.0, function.getX(1), 0.0001);
        assertEquals(3.0, function.getX(2), 0.0001);
        assertEquals(4.0, function.getX(3), 0.0001);
    }

    @Test
    public void testSingleElementConstructor() {
        double[] xValues = {1.0, 3.0};
        double[] yValues = {2.0, 6.0};
        ArrayTabulatedFunction  minFunction = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(2,  minFunction.getCount());
        assertEquals(1.0,  minFunction.leftBound(), 0.0001);
        assertEquals(3.0,  minFunction.rightBound(), 0.0001);
        assertEquals(2.0,  minFunction.apply(1.0), 0.0001);
        assertEquals(6.0,  minFunction.apply(3.0), 0.0001);
        assertEquals(0.0,  minFunction.apply(0.0), 0.0001);
        assertEquals(8.0, minFunction.apply(4.0), 0.0001);
        assertEquals(4.0, minFunction.apply(2.0), 0.0001);
    }

    @Test
    public void testInterpolateWithLastIndex() {
        double result = arrayFunction.interpolate(3.5, 2);
        assertEquals(7.0, result, 0.0001);
    }

    @Test
    public void testExtrapolateRightWithLastTwoPoints() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        double result = function.extrapolateRight(5.0);
        double expected = function.interpolate(5.0, 2);
        assertEquals(expected, result, 0.0001);
    }


}