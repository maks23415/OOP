package functions;

import exceptions.DifferentLengthOfArraysException;
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
        assertThrows(DifferentLengthOfArraysException.class, () -> new LinkedListTabulatedFunction(shortX, longY));
        double[] singleX = {1.0};
        double[] singleY = {2.0};
        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(singleX, singleY));

        double[] emptyX = {};
        double[] emptyY = {};
        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(emptyX, emptyY));
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

        LinkedListTabulatedFunction singlePoint = new LinkedListTabulatedFunction(sqr, 2.0, 4.0, 2);
        assertEquals(2, singlePoint.getCount());
        assertEquals(2.0, singlePoint.getX(0));

        assertThrows(IllegalArgumentException.class, () -> new LinkedListTabulatedFunction(sqr, 0, 1, 1));
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
        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(-1.0));

        assertEquals(0, function.floorIndexOfX(0.0));
        assertEquals(0, function.floorIndexOfX(0.5));
        assertEquals(1, function.floorIndexOfX(1.0));
        assertEquals(1, function.floorIndexOfX(1.5));
        assertEquals(2, function.floorIndexOfX(2.0));
        assertEquals(2, function.floorIndexOfX(2.5));
        assertEquals(3, function.floorIndexOfX(3.0));
        assertEquals(3, function.floorIndexOfX(3.5));
        assertEquals(4, function.floorIndexOfX(4.0));
        assertEquals(4, function.floorIndexOfX(5.0));
    }

    @Test
    void testExtrapolateLeft()
    {
        double result = function.extrapolateLeft(-1.0);
        double expected = -1.0;
        assertEquals(expected, result, 1e-10);

        double[] singleX = {1.0, 2.0};
        double[] singleY = {1.0, 2.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(0.0, singleFunc.extrapolateLeft(0.0),1e-10 );
    }

    @Test
    void testExtrapolateRight()
    {
        double result = function.extrapolateRight(5.0);
        double expected = 23.0;
        assertEquals(expected, result, 1e-10);

        double[] twoX = {1.0, 2.0};
        double[] twoY = {1.0, 4.0};
        LinkedListTabulatedFunction twoPointFunc = new LinkedListTabulatedFunction(twoX, twoY);
        double rightResult = twoPointFunc.extrapolateRight(3.0);
        double rightExpected = 7.0;
        assertEquals(rightExpected, rightResult, 1e-10);
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
        assertEquals(23.0, rightExtrapolated, 1e-10);
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
    void testInsertIntoEmptyList() {
        double[] xValues = {1.0, 3.0};
        double[] yValues = {1.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        function.remove(0);
        function.remove(0);
        function.insert(2.0, 4.0);
        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(4.0, function.getY(0));
    }

    @Test
    void testInsertAtBeginning() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(1.0, 1.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(1.0, function.getY(0));
        assertEquals(2.0, function.getX(1));
    }

    @Test
    void testInsertAtEnd() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(4.0, 16.0);

        assertEquals(4, function.getCount());
        assertEquals(4.0, function.getX(3));
        assertEquals(16.0, function.getY(3));
    }

    @Test
    void testInsertUpdateExisting() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(2.0, 8.0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(1));
        assertEquals(8.0, function.getY(1));
    }

    @Test
    void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getY(0), 1e-10);
        assertEquals(3.0, function.getX(1), 1e-10);
        assertEquals(9.0, function.getY(1), 1e-10);
        assertEquals(4.0, function.getX(2), 1e-10);
        assertEquals(16.0, function.getY(2), 1e-10);
    }

    @Test
    void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(2);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getY(0), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(4.0, function.getY(1), 1e-10);
        assertEquals(4.0, function.getX(2), 1e-10);
        assertEquals(16.0, function.getY(2), 1e-10);
    }

    @Test
    void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(3);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getY(0), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(4.0, function.getY(1), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(9.0, function.getY(2), 1e-10);
    }

    @Test
    void testRemoveSingleElement() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getY(0), 1e-10);

        assertEquals(2.0, function.leftBound(), 1e-10);
        assertEquals(2.0, function.rightBound(), 1e-10);

        function.remove(0);

        assertEquals(0, function.getCount());
        assertThrows(IllegalStateException.class, function::leftBound);
        assertThrows(IllegalStateException.class, function::rightBound);
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(3));
        assertThrows(IllegalArgumentException.class, () -> function.remove(5));
    }

    @Test
    void testRemoveAndCheckBounds() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);
        assertEquals(1.0, function.leftBound(), 1e-10);
        assertEquals(3.0, function.rightBound(), 1e-10);

        function.remove(2);
        assertEquals(1.0, function.leftBound(), 1e-10);
        assertEquals(2.0, function.rightBound(), 1e-10);
    }

    @Test
    void testRemoveAndApplyFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(9.0, function.apply(3.0), 1e-10);
        assertEquals(1.0, function.apply(1.0), 1e-10);
        assertEquals(16.0, function.apply(4.0), 1e-10);

        assertEquals(7.0, function.apply(2.5), 1e-10); // между 1 и 3
    }

    @Test
    void testRemoveAllElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);
        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-10);

        function.remove(0);
        assertEquals(0, function.getCount());
        assertThrows(IllegalStateException.class, () -> function.leftBound());
    }

    @Test
    void testRemoveAndInsert() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);
        assertEquals(2, function.getCount());

        function.insert(4.0, 16.0);
        assertEquals(3, function.getCount());
        assertEquals(4.0, function.getX(1), 1e-10);
        assertEquals(16.0, function.getY(1), 1e-10);
    }

    @Test
    void testRemoveMaintainsCircularStructure() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(3.0, function.getX(1), 1e-10);

        assertEquals(1.0, function.leftBound(), 1e-10);
        assertEquals(3.0, function.rightBound(), 1e-10);

        assertEquals(1.0, function.apply(1.0), 1e-10);
        assertEquals(9.0, function.apply(3.0), 1e-10);
    }

    @Test
    void testRemoveFromComplexFunction() {
        MathFunction sqr = new SqrFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(sqr, 0, 4, 5);

        function.remove(2);

        assertEquals(4, function.getCount());
        assertEquals(0.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getX(1), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(4.0, function.getX(3), 1e-10);

        double interpolated = function.apply(2.0);
        double expected = 5.0;
        assertEquals(expected, interpolated, 1e-10);
    }

    @Test
    void testRemoveAndComplexOperations() {
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);
        function.remove(2);

        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getX(1), 1e-10);
        assertEquals(2.0, function.getX(2), 1e-10);

        assertEquals(0.0, function.leftBound(), 1e-10);
        assertEquals(2.0, function.rightBound(), 1e-10);
        assertEquals(1, function.indexOfX(1.0));
        assertEquals(1.0, function.apply(1.0), 1e-10);

        double interpolated = function.apply(0.5);
        double expected = 0.5;
        assertEquals(expected, interpolated, 1e-10);
    }


    @Test
    void testAllInvalidIndices() {
        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(5));
        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(5));
        assertThrows(IllegalArgumentException.class, () -> function.setY(-1, 1.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(5, 1.0));
        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(5));
    }

    @Test
    void testFloorIndexOfXAllBranches() {
        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(-1.0));
        assertEquals(0, function.floorIndexOfX(0.0));
        assertEquals(4, function.floorIndexOfX(4.0));
        assertEquals(4, function.floorIndexOfX(5.0));
        assertEquals(2, function.floorIndexOfX(2.0));
        assertEquals(1, function.floorIndexOfX(1.5));
    }

    @Test
    void testInterpolateWithInvalidFloorIndex() {
        assertThrows(IllegalArgumentException.class, () -> function.interpolate(1.5, -1));
        assertThrows(IllegalArgumentException.class, () -> function.interpolate(1.5, 4));
        assertThrows(IllegalArgumentException.class, () -> function.interpolate(1.5, 5));
    }

    @Test
    void testConstructorWithSinglePointMathFunction() {
        MathFunction sqr = new SqrFunction();
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(sqr, 2.0, 2.0, 2);

        assertEquals(2, func.getCount());
        assertEquals(2.0, func.getX(0), 1e-10);
        assertEquals(4.0, func.getY(1), 1e-10);
        assertEquals(4.0, func.getY(0), 1e-10);
        assertEquals(4.0, func.getY(1), 1e-10);
    }

    @Test
    void testExtrapolateTwoPoints() {
        double[] twoPointsX = {1.0, 2.0};
        double[] twoPointsY = {1.0, 2.0};
        LinkedListTabulatedFunction twoPointFunc = new LinkedListTabulatedFunction(twoPointsX, twoPointsY);

        assertEquals(0.0, twoPointFunc.extrapolateLeft(0.0), 1e-10);
        assertEquals(3.0, twoPointFunc.extrapolateRight(3.0), 1e-10);
    }

    @Test
    void testInterpolateWithLastIndex() {
        assertThrows(IllegalArgumentException.class, () ->
                function.interpolate(4.5, 4));
    }



}