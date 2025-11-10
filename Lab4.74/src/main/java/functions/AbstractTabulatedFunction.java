package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTabulatedFunction.class);

    protected int count;

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        logger.trace("Checking array lengths: xValues.length={}, yValues.length={}",
                xValues.length, yValues.length);

        if (xValues.length != yValues.length) {
            logger.error("Arrays have different lengths: xValues={}, yValues={}",
                    xValues.length, yValues.length);
            throw new DifferentLengthOfArraysException("Массивы имеют разную длину");
        }

        logger.trace("Array length check passed");
    }

    public static void checkSorted(double[] xValues) {
        logger.trace("Checking if array is sorted. Length: {}", xValues.length);

        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                logger.error("Array is not sorted at index {}: {} <= {}",
                        i, xValues[i], xValues[i - 1]);
                throw new ArrayIsNotSortedException("Массив не отсортирован");
            }
        }

        logger.trace("Array sorted check passed");
    }

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        logger.trace("Performing linear interpolation: x={}, interval=[{}, {}], values=[{}, {}]",
                x, leftX, rightX, leftY, rightY);

        if (Math.abs(rightX - leftX) < 1e-10) {
            logger.debug("Zero interval detected in interpolation, returning leftY: {}", leftY);
            return leftY;
        }

        double result = leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
        logger.trace("Interpolation result: {}", result);
        return result;
    }

    protected double applyInterpolated(double x, int floorIndex) {
        logger.debug("Applying interpolation for x={} at floorIndex={}", x, floorIndex);

        if (x < getX(floorIndex) || (floorIndex < getCount() - 1 && x > getX(floorIndex + 1))) {
            logger.error("x={} is outside interpolation range at floorIndex={}. Range: [{}, {}]",
                    x, floorIndex, getX(floorIndex),
                    floorIndex < getCount() - 1 ? getX(floorIndex + 1) : "N/A");
            throw new InterpolationException("x вышел за предел interpolation");
        }

        double result;
        if (floorIndex == 0) {
            logger.debug("Using left extrapolation for x={}", x);
            result = extrapolateLeft(x);
        } else if (floorIndex == getCount() - 1) {
            logger.debug("Using right extrapolation for x={}", x);
            result = extrapolateRight(x);
        } else if (getX(floorIndex + 1) != getX(floorIndex)) {
            logger.debug("Using interpolation for x={} at floorIndex={}", x, floorIndex);
            result = interpolate(x, floorIndex);
        } else {
            logger.debug("Zero interval, returning y at floorIndex {}: {}", floorIndex, getY(floorIndex));
            result = getY(floorIndex);
        }

        logger.debug("Interpolated result for x={}: {}", x, result);
        return result;
    }

    @Override
    public int getCount() {
        logger.trace("Getting count: {}", count);
        return count;
    }

    @Override
    public double apply(double x) {
        logger.debug("Applying function for x={}", x);

        if (x < leftBound()) {
            logger.debug("x={} is less than left bound {}, using left extrapolation", x, leftBound());
            double result = extrapolateLeft(x);
            logger.debug("Left extrapolation result for x={}: {}", x, result);
            return result;
        }

        if (x > rightBound()) {
            logger.debug("x={} is greater than right bound {}, using right extrapolation", x, rightBound());
            double result = extrapolateRight(x);
            logger.debug("Right extrapolation result for x={}: {}", x, result);
            return result;
        }

        int index = indexOfX(x);
        if (index != -1) {
            double result = getY(index);
            logger.debug("Exact match found at index {} for x={}, returning y={}", index, x, result);
            return result;
        }

        logger.debug("No exact match for x={}, finding floor index", x);
        int floorIndex = floorIndexOfX(x);
        logger.debug("Found floor index {} for x={}", floorIndex, x);

        double result = applyInterpolated(x, floorIndex);
        logger.debug("Final result for x={}: {}", x, result);
        return result;
    }

    @Override
    public String toString() {
        logger.trace("Generating string representation");

        StringBuilder sb = new StringBuilder();

        String className = getClass().getSimpleName();
        int currentCount = getCount();

        sb.append(className)
                .append(" size = ")
                .append(currentCount)
                .append("\n");

        logger.debug("Building string for {} with {} points", className, currentCount);

        int pointCount = 0;
        for (Point point : this) {
            sb.append("[")
                    .append(point.x)
                    .append("; ")
                    .append(point.y)
                    .append("]\n");
            pointCount++;
            logger.trace("Added point {} to string: [{}, {}]", pointCount, point.x, point.y);
        }

        String result = sb.toString();
        logger.trace("String representation generated with {} points", pointCount);
        return result;
    }
}