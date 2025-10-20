package concurrent;

import functions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SynchronizedTabulatedFunctionIteratorTest
{

    @Test
    public void testIteratorHasNext() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    public void testIteratorNext() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        Point point1 = iterator.next();
        assertEquals(0.0, point1.x, 1e-9);
        assertEquals(1.0, point1.y, 1e-9);

        Point point2 = iterator.next();
        assertEquals(1.0, point2.x, 1e-9);
        assertEquals(1.0, point2.y, 1e-9);

        Point point3 = iterator.next();
        assertEquals(2.0, point3.x, 1e-9);
        assertEquals(1.0, point3.y, 1e-9);
    }

    @Test
    public void testIteratorSequence() {
        TabulatedFunction baseFunction = new ArrayTabulatedFunction(
                new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0}
        );
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testIteratorNoSuchElementException() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 1, 2);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        iterator.next();
        iterator.next();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testIteratorUnsupportedRemove() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 1, 2);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void testIteratorIsIndependentCopy() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        syncFunction.setY(1, 99.0);

        Point point1 = iterator.next();
        assertEquals(0.0, point1.x, 1e-9);
        assertEquals(1.0, point1.y, 1e-9);

        Point point2 = iterator.next();
        assertEquals(1.0, point2.x, 1e-9);
        assertEquals(1.0, point2.y, 1e-9);
    }

    @Test
    public void testMultipleIteratorsAreIndependent() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        Point point1FromIter1 = iterator1.next();

        Point point1FromIter2 = iterator2.next();

        assertEquals(point1FromIter1.x, point1FromIter2.x, 1e-9);
        assertEquals(point1FromIter1.y, point1FromIter2.y, 1e-9);

        syncFunction.setY(0, 50.0);

        Point point2FromIter1 = iterator1.next();
        assertEquals(1.0, point2FromIter1.x, 1e-9);
        assertEquals(1.0, point2FromIter1.y, 1e-9);
    }
}