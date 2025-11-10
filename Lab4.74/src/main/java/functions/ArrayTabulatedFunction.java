package functions;

import exceptions.InterpolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunction.class);

    @Serial
    private static final long serialVersionUID = -5868741821402628735L;
    private double[] xValues;
    private double[] yValues;
    private int count;

    private static final int INITIAL_CAPACITY = 10;
    private int capacity;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        logger.debug("Creating ArrayTabulatedFunction from arrays: xValues.length={}, yValues.length={}",
                xValues.length, yValues.length);

        if (xValues.length < 2) {
            logger.error("Attempt to create function with insufficient points: {}", xValues.length);
            throw new IllegalArgumentException("Длина должна быть 2");
        }

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.count = xValues.length;
        this.capacity = Math.max(count * 2, INITIAL_CAPACITY);
        this.xValues = Arrays.copyOf(xValues, capacity);
        this.yValues = Arrays.copyOf(yValues, capacity);
        System.arraycopy(xValues, 0, this.xValues, 0, count);
        System.arraycopy(yValues, 0, this.yValues, 0, count);

        logger.info("ArrayTabulatedFunction created successfully. Count: {}, Capacity: {}", count, capacity);
        logger.debug("X range: [{}, {}], Y range: [{}, {}]",
                xValues[0], xValues[count-1],
                yValues[0], yValues[count-1]);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        logger.debug("Creating ArrayTabulatedFunction from source function: xFrom={}, xTo={}, count={}",
                xFrom, xTo, count);

        if (count < 2) {
            logger.error("Invalid count provided: {}", count);
            throw new IllegalArgumentException("Кол-во не менее 2");
        }

        this.count = count;
        this.capacity = Math.max(count * 2, INITIAL_CAPACITY);
        this.xValues = new double[capacity];
        this.yValues = new double[capacity];

        if (xFrom > xTo) {
            logger.debug("Swapping xFrom and xTo: {} -> {}", xFrom, xTo);
            double temp = xTo;
            xTo = xFrom;
            xFrom = temp;
        }

        if (xFrom == xTo) {
            logger.debug("Creating constant function at x={}", xFrom);
            Arrays.fill(xValues, 0, count, xFrom);
            double yValue = source.apply(xFrom);
            Arrays.fill(yValues, 0, count, yValue);
            logger.debug("Constant function created with y={}", yValue);
        } else {
            double step = (xTo - xFrom) / (count - 1);
            logger.debug("Creating tabulated function with step={}", step);
            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
                logger.trace("Point {}: x={}, y={}", i, xValues[i], yValues[i]);
            }
        }

        logger.info("ArrayTabulatedFunction created from source. Count: {}, Capacity: {}", count, capacity);
    }

    @Override
    public void insert(double x, double y) {
        logger.debug("Inserting point: x={}, y={}", x, y);

        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            logger.debug("Point with x={} already exists at index {}. Updating y from {} to {}",
                    x, existingIndex, yValues[existingIndex], y);
            yValues[existingIndex] = y;
            return;
        }

        int insertIndex = findInsertIndex(x);
        logger.debug("Insert index determined: {}", insertIndex);

        if (count == capacity) {
            logger.debug("Capacity reached ({}/{}). Increasing capacity to {}", count, capacity, capacity * 2);
            increaseCapacity();
        }

        if (insertIndex < count) {
            logger.trace("Shifting array elements from index {}", insertIndex);
            System.arraycopy(xValues, insertIndex, xValues, insertIndex + 1, count - insertIndex);
            System.arraycopy(yValues, insertIndex, yValues, insertIndex + 1, count - insertIndex);
        }

        xValues[insertIndex] = x;
        yValues[insertIndex] = y;
        count++;

        logger.info("Point inserted at index {}. New count: {}", insertIndex, count);
    }

    private int findInsertIndex(double x) {
        logger.trace("Finding insert index for x={}", x);
        for (int i = 0; i < count; i++) {
            if (x < xValues[i]) {
                logger.trace("Insert index found: {}", i);
                return i;
            }
        }
        logger.trace("Insert index found: {} (end of array)", count);
        return count;
    }

    private void increaseCapacity() {
        int oldCapacity = capacity;
        capacity *= 2;
        double[] newXValues = new double[capacity];
        double[] newYValues = new double[capacity];

        System.arraycopy(xValues, 0, newXValues, 0, count);
        System.arraycopy(yValues, 0, newYValues, 0, count);

        xValues = newXValues;
        yValues = newYValues;

        logger.debug("Capacity increased from {} to {}", oldCapacity, capacity);
    }

    @Override
    public int getCount() {
        logger.trace("Getting count: {}", count);
        return count;
    }

    @Override
    public double getX(int index) {
        logger.trace("Getting x at index: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Invalid index for getX: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        double result = xValues[index];
        logger.trace("x[{}] = {}", index, result);
        return result;
    }

    @Override
    public double getY(int index) {
        logger.trace("Getting y at index: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Invalid index for getY: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        double result = yValues[index];
        logger.trace("y[{}] = {}", index, result);
        return result;
    }

    @Override
    public void setY(int index, double value) {
        logger.debug("Setting y at index {} to {}", index, value);
        if (index < 0 || index >= count) {
            logger.error("Invalid index for setY: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        double oldValue = yValues[index];
        yValues[index] = value;
        logger.debug("y[{}] changed from {} to {}", index, oldValue, value);
    }

    @Override
    public double leftBound() {
        double bound = xValues[0];
        logger.trace("Left bound: {}", bound);
        return bound;
    }

    @Override
    public double rightBound() {
        double bound = xValues[count - 1];
        logger.trace("Right bound: {}", bound);
        return bound;
    }

    @Override
    public int indexOfX(double x) {
        logger.trace("Searching for x={}", x);
        for (int i = 0; i < count; i++) {
            if (Math.abs(xValues[i] - x) < 1e-10) {
                logger.trace("x={} found at index {}", x, i);
                return i;
            }
        }
        logger.trace("x={} not found", x);
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        logger.trace("Searching for y={}", y);
        for (int i = 0; i < count; i++) {
            if (Math.abs(yValues[i] - y) < 1e-10) {
                logger.trace("y={} found at index {}", y, i);
                return i;
            }
        }
        logger.trace("y={} not found", y);
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        logger.trace("Finding floor index for x={}", x);
        if (x < xValues[0]) {
            logger.error("x={} is less than left bound {}", x, xValues[0]);
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }
        for (int i = 1; i < count; i++) {
            if (xValues[i] > x) {
                logger.trace("Floor index found: {}", i - 1);
                return i - 1;
            }
        }
        logger.trace("Floor index found: {} (last element)", count - 1);
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        logger.debug("Left extrapolation for x={}", x);
        double result = interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
        logger.debug("Left extrapolation result: {}", result);
        return result;
    }

    @Override
    protected double extrapolateRight(double x) {
        logger.debug("Right extrapolation for x={}", x);
        double result = interpolate(x, xValues[count - 2], xValues[count - 1],
                yValues[count - 2], yValues[count - 1]);
        logger.debug("Right extrapolation result: {}", result);
        return result;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        logger.debug("Interpolating x={} at floorIndex={}", x, floorIndex);
        if (floorIndex < 0 || floorIndex >= count - 1) {
            logger.error("Invalid floorIndex: {} (count: {})", floorIndex, count);
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }
        if (x < xValues[floorIndex] || x > xValues[floorIndex + 1]) {
            logger.error("x={} out of interpolation range [{}, {}]",
                    x, xValues[floorIndex], xValues[floorIndex + 1]);
            throw new InterpolationException("x вышел за пределы interpolation");
        }

        double result = interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
        logger.debug("Interpolation result: {}", result);
        return result;
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        logger.trace("Linear interpolation: x={}, interval=[{}, {}], values=[{}, {}]",
                x, leftX, rightX, leftY, rightY);
        if (Math.abs(leftX - rightX) < 1e-10) {
            logger.trace("Constant interpolation (zero interval)");
            return leftY;
        }
        double result = leftY + (x - leftX) * (rightY - leftY) / (rightX - leftX);
        logger.trace("Interpolation result: {}", result);
        return result;
    }

    @Override
    public void remove(int index) {
        logger.debug("Removing element at index: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Invalid remove index: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс выходит за границы: " + index);
        }

        double removedX = xValues[index];
        double removedY = yValues[index];

        if (index < count - 1) {
            logger.trace("Shifting array elements after removal");
            System.arraycopy(xValues, index + 1, xValues, index, count - index - 1);
            System.arraycopy(yValues, index + 1, yValues, index, count - index - 1);
        }

        if (count > 0) {
            xValues[count - 1] = 0;
            yValues[count - 1] = 0;
        }

        count--;
        logger.info("Element removed at index {}: x={}, y={}. New count: {}",
                index, removedX, removedY, count);
    }

    @Override
    public Iterator<Point> iterator() {
        logger.trace("Creating iterator");
        return new Iterator<Point>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                boolean hasNext = i < count;
                logger.trace("Iterator hasNext: {} (i={}, count={})", hasNext, i, count);
                return hasNext;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.error("Iterator next() called with no elements remaining");
                    throw new NoSuchElementException();
                }
                Point point = new Point(xValues[i], yValues[i]);
                logger.trace("Iterator next: point[{}] = ({}, {})", i, point.x, point.y);
                i++;
                return point;
            }
        };
    }
}