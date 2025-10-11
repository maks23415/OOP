package operations;

import exceptions.InconsistentFunctionsException;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TabulatedFunctionOperationServiceTest {

    @Test
    void testAddWithArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] yValues2 = {5.0, 6.0, 7.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xValues, yValues2);
        TabulatedFunction result = service.add(func1, func2);
        assertNotNull(result);
        assertEquals(3, result.getCount());
        assertEquals(7.0, result.getY(0), 0.0001);
        assertEquals(9.0, result.getY(1), 0.0001);
        assertEquals(11.0, result.getY(2), 0.0001);
    }

    @Test
    void testSubtractWithDifferentFunctionTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        double[] yValues2 = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues1);
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues2);
        TabulatedFunction result = service.subtract(arrayFunc, linkedListFunc);
        assertNotNull(result);
        assertEquals(3, result.getCount());
        assertEquals(6.0, result.getY(0), 0.0001);
        assertEquals(15.0, result.getY(1), 0.0001);
        assertEquals(24.0, result.getY(2), 0.0001);
    }

    @Test
    void testAddWithDifferentCountsThrowsException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] xValues2 = {1.0, 2.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 2.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);
        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }
    @Test
    public void testAsPointsWithArrayTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 6.0, 7.0, 8.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);

        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 0.0001);
            assertEquals(yValues[i], points[i].y, 0.0001);
        }
    }

    @Test
    public void testAsPointsWithLinkedListTabulatedFunction() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);

        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 0.0001);
            assertEquals(yValues[i], points[i].y, 0.0001);
        }
    }

    @Test
    public void testAsPointsWithSinglePointFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(2, points.length);
        assertEquals(1.0, points[0].x, 0.0001);
        assertEquals(3.0, points[0].y, 0.0001);
        assertEquals(2.0, points[1].x, 0.0001);
        assertEquals(4.0, points[1].y, 0.0001);
    }

    @Test
    public void testAsPointsOrderPreservation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {30.0, 10.0, 20.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        assertTrue(points[0].x < points[1].x);
        assertTrue(points[1].x < points[2].x);
    }

    @Test
    public void testAsPointsWithEmptyFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(2, points.length);
        assertNotNull(points[0]);
        assertNotNull(points[1]);
    }

    @Test
    public void testAsPointsReturnsNewArray() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points1 = TabulatedFunctionOperationService.asPoints(function);
        Point[] points2 = TabulatedFunctionOperationService.asPoints(function);

        assertNotSame(points1, points2);

        assertEquals(points1.length, points2.length);
        for (int i = 0; i < points1.length; i++) {
            assertEquals(points1[i].x, points2[i].x, 0.0001);
            assertEquals(points1[i].y, points2[i].y, 0.0001);
        }
    }

    @Test
    public void testAsPointsWithLargeFunction() {
        int pointCount = 100;
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xValues[i] = i;
            yValues[i] = i * i;
        }

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(pointCount, points.length);

        assertEquals(0.0, points[0].x, 0.0001);
        assertEquals(0.0, points[0].y, 0.0001);

        assertEquals(50.0, points[50].x, 0.0001);
        assertEquals(2500.0, points[50].y, 0.0001);

        assertEquals(99.0, points[99].x, 0.0001);
        assertEquals(9801.0, points[99].y, 0.0001);
    }
}
