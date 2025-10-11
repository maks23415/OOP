package functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable
{
    private double[] xValues;
    private double[] yValues;
    private int count;

    private static final int INITIAL_CAPACITY = 10;
    private int capacity;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues)
    {
        if (xValues.length < 2)
        {
            throw new IllegalArgumentException("Длина таблицы меньше минимальной");
        }
        if (xValues.length != yValues.length)
        {
            throw new IllegalArgumentException("Число X должно быть ровно Y");
        }

        for (int i = 1; i < xValues.length; i++)
        {
            if (xValues[i] <= xValues[i - 1])
            {
                throw new IllegalArgumentException("Значение X должно увеличиваться");
            }
        }

        this.count = xValues.length;
        this.capacity = Math.max(count * 2, INITIAL_CAPACITY);
        this.xValues = Arrays.copyOf(xValues, capacity);
        this.yValues = Arrays.copyOf(yValues, capacity);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count)
    {
        if (count < 2)
        {
            throw new IllegalArgumentException("Кол-во не менее 2");
        }

        this.count = count;
        this.capacity = Math.max(count * 2, INITIAL_CAPACITY);
        this.xValues = new double[capacity];
        this.yValues = new double[capacity];

        if (xFrom > xTo)
        {
            double temp = xTo;
            xTo = xFrom;
            xFrom = temp;
        }

        if (xFrom == xTo)
        {
            Arrays.fill(xValues, 0, count, xFrom);
            double yValue = source.apply(xFrom);
            Arrays.fill(yValues, 0, count, yValue);
        } else
        {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++)
            {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
            }
        }
    }

    @Override
    public void insert(double x, double y)
    {
        int existingIndex = indexOfX(x);
        if (existingIndex != -1)
        {
            yValues[existingIndex] = y;
            return;
        }

        int insertIndex = findInsertIndex(x);

        if (count == capacity)
        {
            increaseCapacity();
        }


        if (insertIndex < count)
        {
            System.arraycopy(xValues, insertIndex, xValues, insertIndex + 1, count - insertIndex);
            System.arraycopy(yValues, insertIndex, yValues, insertIndex + 1, count - insertIndex);
        }

        xValues[insertIndex] = x;
        yValues[insertIndex] = y;
        count++;
    }

    private int findInsertIndex(double x)
    {
        for (int i = 0; i < count; i++)
        {
            if (x < xValues[i])
            {
                return i;
            }
        }
        return count;
    }

    private void increaseCapacity()
    {
        capacity *= 2;
        double[] newXValues = new double[capacity];
        double[] newYValues = new double[capacity];

        System.arraycopy(xValues, 0, newXValues, 0, count);
        System.arraycopy(yValues, 0, newYValues, 0, count);

        xValues = newXValues;
        yValues = newYValues;
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public double getX(int index)
    {
        if (index < 0 || index >= count)
        {
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index)
    {
        if (index < 0 || index >= count)
        {
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value)
    {
        if (index < 0 || index >= count)
        {
            throw new IllegalArgumentException("Индекс за пределами допустимого");
        }
        yValues[index] = value;
    }

    @Override
    public double leftBound()
    {
        return xValues[0];
    }

    @Override
    public double rightBound()
    {
        return xValues[count - 1];
    }

    @Override
    public int indexOfX(double x)
    {
        for (int i = 0; i < count; i++)
        {
            if (Math.abs(xValues[i] - x) < 1e-10)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexOfY(double y)
    {
        for (int i = 0; i < count; i++)
        {
            if (Math.abs(yValues[i] - y) < 1e-10)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x)
    {
        if (x < xValues[0])
        {
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }
        for (int i = 1; i < count; i++)
        {
            if (xValues[i] > x)
            {
                return i - 1;
            }
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x)
    {
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    protected double extrapolateRight(double x)
    {
        return interpolate(x, xValues[count - 2], xValues[count - 1],
                yValues[count - 2], yValues[count - 1]);
    }

    @Override
    protected double interpolate(double x, int floorIndex)
    {
        if (floorIndex < 0 || floorIndex >= count - 1) {
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }
        return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY)
    {
        if (Math.abs(leftX - rightX) < 1e-10)
        {
            return leftY;
        }
        return leftY + (x - leftX) * (rightY - leftY) / (rightX - leftX);
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс выходит за границы: " + index);
        }

        if (index < count - 1) {
            System.arraycopy(xValues, index + 1, xValues, index, count - index - 1);
            System.arraycopy(yValues, index + 1, yValues, index, count - index - 1);
        }

        if (count > 0) {
            xValues[count - 1] = 0;
            yValues[count - 1] = 0;
        }


        count--;
    }
}