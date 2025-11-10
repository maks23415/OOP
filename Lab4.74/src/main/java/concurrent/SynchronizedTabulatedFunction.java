
package concurrent;

import functions.TabulatedFunction;
import functions.Point;
import operations.TabulatedFunctionOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedTabulatedFunction.class);

    private final TabulatedFunction function;
    private final Object lock;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = function;
        this.lock = this;
        logger.debug("SynchronizedTabulatedFunction created for function with {} points",
                function != null ? function.getCount() : 0);
    }

    @Override
    public synchronized int getCount() {
        logger.trace("getCount() called by thread: {}", Thread.currentThread().getName());
        int count = function.getCount();
        logger.trace("getCount() returned: {}", count);
        return count;
    }

    @Override
    public synchronized double getX(int index) {
        logger.trace("getX({}) called by thread: {}", index, Thread.currentThread().getName());
        double x = function.getX(index);
        logger.trace("getX({}) returned: {}", index, x);
        return x;
    }

    @Override
    public synchronized double getY(int index) {
        logger.trace("getY({}) called by thread: {}", index, Thread.currentThread().getName());
        double y = function.getY(index);
        logger.trace("getY({}) returned: {}", index, y);
        return y;
    }

    @Override
    public synchronized void setY(int index, double value) {
        logger.debug("setY({}, {}) called by thread: {}", index, value, Thread.currentThread().getName());
        double oldValue = function.getY(index);
        function.setY(index, value);
        logger.debug("setY({}): changed Y from {} to {}", index, oldValue, value);
    }

    @Override
    public synchronized int indexOfX(double x) {
        logger.trace("indexOfX({}) called by thread: {}", x, Thread.currentThread().getName());
        int index = function.indexOfX(x);
        logger.trace("indexOfX({}) returned: {}", x, index);
        return index;
    }

    @Override
    public synchronized int indexOfY(double y) {
        logger.trace("indexOfY({}) called by thread: {}", y, Thread.currentThread().getName());
        int index = function.indexOfY(y);
        logger.trace("indexOfY({}) returned: {}", y, index);
        return index;
    }

    @Override
    public synchronized double leftBound() {
        logger.trace("leftBound() called by thread: {}", Thread.currentThread().getName());
        double left = function.leftBound();
        logger.trace("leftBound() returned: {}", left);
        return left;
    }

    @Override
    public synchronized double rightBound() {
        logger.trace("rightBound() called by thread: {}", Thread.currentThread().getName());
        double right = function.rightBound();
        logger.trace("rightBound() returned: {}", right);
        return right;
    }

    @Override
    public synchronized Iterator<Point> iterator() {
        logger.debug("iterator() called by thread: {}", Thread.currentThread().getName());

        Point[] pointsCopy = TabulatedFunctionOperationService.asPoints(function);
        logger.trace("Created iterator with {} points", pointsCopy.length);

        return new Iterator<Point>() {
            private int currentIndex = 0;
            private final Point[] points = pointsCopy;

            @Override
            public boolean hasNext() {
                boolean hasNext = currentIndex < points.length;
                logger.trace("Iterator.hasNext() returned: {} (currentIndex: {})", hasNext, currentIndex);
                return hasNext;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.warn("Iterator.next() called but no more elements available");
                    throw new NoSuchElementException("No more elements in iterator");
                }
                Point point = points[currentIndex++];
                logger.trace("Iterator.next() returned: Point(x={}, y={}) at index {}",
                        point.x, point.y, currentIndex - 1);
                return point;
            }

            @Override
            public void remove() {
                logger.warn("Iterator.remove() called - operation not supported");
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    @Override
    public synchronized double apply(double x) {
        logger.debug("apply({}) called by thread: {}", x, Thread.currentThread().getName());
        double result = function.apply(x);
        logger.debug("apply({}) returned: {}", x, result);
        return result;
    }

    public interface Operation<T> {
        T apply(SynchronizedTabulatedFunction function);
    }

    public <T> T doSynchronously(Operation<? extends T> operation) {
        logger.debug("doSynchronously() called by thread: {}", Thread.currentThread().getName());
        synchronized (lock) {
            logger.trace("Acquired lock in doSynchronously() by thread: {}", Thread.currentThread().getName());
            try {
                T result = operation.apply(this);
                logger.debug("doSynchronously() completed successfully, result: {}", result);
                return result;
            } catch (Exception e) {
                logger.error("Error in doSynchronously() operation", e);
                throw e;
            } finally {
                logger.trace("Releasing lock in doSynchronously() by thread: {}", Thread.currentThread().getName());
            }
        }
    }

    public synchronized String toString() {
        String description = function.toString();
        logger.trace("toString() called, returning: {}", description);
        return description;
    }
}