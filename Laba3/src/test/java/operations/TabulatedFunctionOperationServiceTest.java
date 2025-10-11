package operations;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TabulatedFunctionOperationServiceTest {

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
