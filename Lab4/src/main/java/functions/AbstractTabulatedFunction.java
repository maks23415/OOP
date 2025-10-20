package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    protected int count;

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new DifferentLengthOfArraysException("Массивы имеют разную длину");
        }
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new ArrayIsNotSortedException("Массив не отсортирован");
            }
        }
    }

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }

    protected double applyInterpolated(double x, int floorIndex) {
        if (x < getX(floorIndex) || (floorIndex < getCount() - 1 && x > getX(floorIndex + 1))) {
            throw new InterpolationException("x вышел за предел interpolation");
        }
        if (floorIndex == 0) {
            return extrapolateLeft(x);
        } else if (floorIndex == getCount() - 1) {
            return extrapolateRight(x);
        } else if (getX(floorIndex + 1) != getX(floorIndex)) {
            return interpolate(x, floorIndex);
        } else {
            return getY(floorIndex);
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double apply(double x) {
        if (x < leftBound()) {
            return extrapolateLeft(x);
        }
        if (x > rightBound()) {
            return extrapolateRight(x);
        }

        int index = indexOfX(x);
        if (index != -1) {
            return getY(index);
        }

        int floorIndex = floorIndexOfX(x);
        return applyInterpolated(x, floorIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName())
                .append(" size = ")
                .append(getCount())
                .append("\n");

        for (Point point : this) {
            sb.append("[")
                    .append(point.x)
                    .append("; ")
                    .append(point.y)
                    .append("]\n");
        }
        return sb.toString();
    }
}