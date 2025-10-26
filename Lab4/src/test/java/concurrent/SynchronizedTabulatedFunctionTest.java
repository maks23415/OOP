package concurrent;

import functions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedTabulatedFunctionTest
{
    @Test
    public void testGetCount() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(11, syncFunction.getCount());
    }

    @Test
    public void testGetX() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0.0, syncFunction.getX(0), 1e-9);
        assertEquals(5.0, syncFunction.getX(5), 1e-9);
        assertEquals(10.0, syncFunction.getX(10), 1e-9);
    }

    @Test
    public void testGetY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(1.0, syncFunction.getY(0), 1e-9);
        assertEquals(1.0, syncFunction.getY(5), 1e-9);
        assertEquals(1.0, syncFunction.getY(10), 1e-9);
    }

    @Test
    public void testSetY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        syncFunction.setY(5, 42.0);
        assertEquals(42.0, syncFunction.getY(5), 1e-9);
        assertEquals(1.0, syncFunction.getY(4), 1e-9);
    }

    @Test
    public void testIndexOfX() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(5, syncFunction.indexOfX(5.0));
        assertEquals(-1, syncFunction.indexOfX(15.0));
    }

    @Test
    public void testIndexOfY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0, syncFunction.indexOfY(1.0));
        assertEquals(-1, syncFunction.indexOfY(99.0));
    }

    @Test
    public void testLeftBound() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0.0, syncFunction.leftBound(), 1e-9);
    }

    @Test
    public void testRightBound() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(10.0, syncFunction.rightBound(), 1e-9);
    }

    @Test
    public void testApply() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(1.0, syncFunction.apply(3.5), 1e-9);
        assertEquals(1.0, syncFunction.apply(7.8), 1e-9);
    }

    @Test
    public void testIterator() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertEquals(count, point.x, 1e-9);
            assertEquals(1.0, point.y, 1e-9);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void testDoSynchronouslyWithDoubleReturn() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x * x, 0, 3, 4);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Double result = syncFunction.doSynchronously(func -> {
            double sum = 0;
            for (int i = 0; i < func.getCount(); i++) {
                sum += func.getY(i);
            }
            return sum;
        });

        assertEquals(14, result, 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithVoidReturn() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x, 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Void result = syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 10);
            }
            return null;
        });

        assertNull(result);
        assertEquals(0, syncFunction.getY(0), 1e-9);
        assertEquals(10, syncFunction.getY(1), 1e-9);
        assertEquals(20, syncFunction.getY(2), 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithIntegerReturn() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x, 0, 4, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Integer count = syncFunction.doSynchronously(func -> {
            int counter = 0;
            for (int i = 0; i < func.getCount(); i++) {
                if (func.getY(i) > 2) {
                    counter++;
                }
            }
            return counter;
        });

        assertEquals(2, count);
    }

    @Test
    public void testDoSynchronouslyWithStringReturn() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> Math.sin(x), 0, Math.PI, 4);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        String result = syncFunction.doSynchronously(func -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Points: ").append(func.getCount())
                    .append(", Range: [").append(func.leftBound())
                    .append(", ").append(func.rightBound()).append("]");
            return sb.toString();
        });

        assertEquals("Points: 4, Range: [0.0, 3.141592653589793]", result);
    }

    @Test
    public void testDoSynchronouslyWithBooleanReturn() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x, -2, 2, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Boolean hasNegative = syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                if (func.getY(i) < 0) {
                    return true;
                }
            }
            return false;
        });

        assertTrue(hasNegative);
    }

    @Test
    public void testDoSynchronouslyWithAnonymousClass() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x, 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Double average = syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<Double>() {
            @Override
            public Double apply(SynchronizedTabulatedFunction function) {
                double sum = 0;
                for (int i = 0; i < function.getCount(); i++) {
                    sum += function.getY(i);
                }
                return sum / function.getCount();
            }
        });

        assertEquals(1.0, average, 1e-9);
    }

    @Test
    public void testDoSynchronouslyComplexMultiMethodOperation() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x * x - 2 * x + 1, -1, 3, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        String result = syncFunction.doSynchronously(func -> {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double sum = 0;

            for (int i = 0; i < func.getCount(); i++) {
                double y = func.getY(i);
                if (y < min) min = y;
                if (y > max) max = y;
                sum += y;
            }

            double average = sum / func.getCount();
            return String.format("Min: %.3f, Max: %.3f, Avg: %.3f", min, max, average);
        });

        System.out.println("Actual result: " + result);

        assertTrue(result.contains("Min: 0"));
        assertTrue(result.contains("Max: 4"));
        assertTrue(result.contains("Avg: 2"));
        assertTrue(result.contains("000"));
    }

    @Test
    public void testDoSynchronouslyModificationOperation() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 3, 4);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Integer modifiedCount = syncFunction.doSynchronously(func -> {
            int count = 0;
            for (int i = 0; i < func.getCount(); i++) {
                if (i % 2 == 0) {
                    func.setY(i, func.getY(i) * 100);
                    count++;
                }
            }
            return count;
        });

        assertEquals(2, modifiedCount);
        assertEquals(100, syncFunction.getY(0), 1e-9);
        assertEquals(1, syncFunction.getY(1), 1e-9);
        assertEquals(100, syncFunction.getY(2), 1e-9);
        assertEquals(1, syncFunction.getY(3), 1e-9);
    }
}