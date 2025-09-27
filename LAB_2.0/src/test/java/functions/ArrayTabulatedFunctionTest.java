package functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ArrayTabulatedFunctionTest {

    private ArrayTabulatedFunction arrayFunction;

    @BeforeEach
    public void setUp() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {2, 4, 6, 8};
        arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
    }

    @Test
    public void testArrayTabulatedFunctionConstructor() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {2, 4, 6, 8};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertEquals(4, function.getCount());
        Assertions.assertEquals(1, function.getX(0));
        Assertions.assertEquals(8, function.getY(3));
    }

    @Test
    public void testConstructorFromMathFunction() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 0, 4, 5);

        Assertions.assertEquals(5, function.getCount());
        Assertions.assertEquals(0, function.getX(0), 0.0001);
        Assertions.assertEquals(16, function.getY(4), 0.0001);
    }

    @Test
    public void testInvalidConstructor() {
        double[] xValues = {2, 1}; // не отсортированы
        double[] yValues = {2, 3};

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testApply() {
        // Точное значение
        Assertions.assertEquals(4, arrayFunction.apply(2), 0.0001);
        // Интерполяция
        Assertions.assertEquals(5, arrayFunction.apply(2.5), 0.0001);
        // Экстраполяция слева
        Assertions.assertEquals(0, arrayFunction.apply(0), 0.0001);
        // Экстраполяция справа
        Assertions.assertEquals(10, arrayFunction.apply(5), 0.0001);
    }

    @Test
    public void testLeftBound() {
        double expected = 1;
        double actual = arrayFunction.leftBound();
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testRightBound() {
        double expected = 4;
        double actual = arrayFunction.rightBound();
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testGetX() {
        Assertions.assertEquals(3, arrayFunction.getX(2), 0.0001);
    }

    @Test
    public void testGetY() {
        Assertions.assertEquals(6, arrayFunction.getY(2), 0.0001);
    }

    @Test
    public void testIndexOfX() {
        Assertions.assertEquals(1, arrayFunction.indexOfX(2));
        Assertions.assertEquals(-1, arrayFunction.indexOfX(10)); // не существует
    }

    @Test
    public void testIndexOfY() {
        Assertions.assertEquals(2, arrayFunction.indexOfY(6));
        Assertions.assertEquals(-1, arrayFunction.indexOfY(10)); // не существует
    }

    @Test
    public void testFloorIndexOfX() {
        Assertions.assertEquals(1, arrayFunction.floorIndexOfX(2.5));
        Assertions.assertEquals(0, arrayFunction.floorIndexOfX(0.5)); // слева от первого
        Assertions.assertEquals(3, arrayFunction.floorIndexOfX(5)); // справа от последнего
    }

    @Test
    public void testInterpolate() {
        double expected = 5;
        double actual = arrayFunction.interpolate(2.5, 1);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testExtrapolateLeft() {
        double expected = 0;
        double actual = arrayFunction.extrapolateLeft(0);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testExtrapolateRight() {
        double expected = 10;
        double actual = arrayFunction.extrapolateRight(5);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testSetY() {
        arrayFunction.setY(1, 10);
        Assertions.assertEquals(10, arrayFunction.getY(1), 0.0001);
    }

    @Test
    public void testInvalidSetYIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () ->
                arrayFunction.setY(-1, 5));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () ->
                arrayFunction.setY(10, 5));
    }

    @Test
    public void testInvalidGetIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () ->
                arrayFunction.getX(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () ->
                arrayFunction.getY(10));
    }
}