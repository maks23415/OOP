package functions;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionIteratorTest {

    @Test
    public void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int pointCount = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[pointCount], point.x, 0.0001);
            assertEquals(yValues[pointCount], point.y, 0.0001);
            pointCount++;
        }

        assertEquals(3, pointCount);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorWithForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        int pointCount = 0;

        for (Point point : function) {
            assertEquals(xValues[pointCount], point.x, 0.0001);
            assertEquals(yValues[pointCount], point.y, 0.0001);
            pointCount++;
        }

        assertEquals(3, pointCount);
    }

    @Test
    public void testIteratorThrowsExceptionOnEmpty() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testIteratorOrder() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        Point first = iterator.next();
        assertEquals(0.5, first.x, 0.0001);
        assertEquals(10.0, first.y, 0.0001);

        Point second = iterator.next();
        assertEquals(1.5, second.x, 0.0001);
        assertEquals(20.0, second.y, 0.0001);

        Point third = iterator.next();
        assertEquals(2.5, third.x, 0.0001);
        assertEquals(30.0, third.y, 0.0001);
    }

    @Test
    public void testIteratorWithSingleElement() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        assertTrue(iterator.hasNext());
        Point first = iterator.next();
        assertEquals(1.0, first.x, 0.0001);
        assertEquals(3.0, first.y, 0.0001);

        assertTrue(iterator.hasNext());
        Point second = iterator.next();
        assertEquals(2.0, second.x, 0.0001);
        assertEquals(4.0, second.y, 0.0001);

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorMultipleCalls() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 6.0, 7.0, 8.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        int count1 = 0;
        for (Point point : function) {
            count1++;
        }
        assertEquals(4, count1);

        int count2 = 0;
        for (Point point : function) {
            count2++;
        }
        assertEquals(4, count2);
    }

    @Test
    public void testIteratorHasNextDoesNotAdvance() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        Point point = iterator.next();
        assertEquals(1.0, point.x, 0.0001);
        assertEquals(3.0, point.y, 0.0001);

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        point = iterator.next();
        assertEquals(2.0, point.x, 0.0001);
        assertEquals(4.0, point.y, 0.0001);

        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }
}
