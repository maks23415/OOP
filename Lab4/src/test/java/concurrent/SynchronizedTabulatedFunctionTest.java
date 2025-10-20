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
}