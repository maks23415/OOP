package operations;

import functions.MathFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SteppingDifferentialOperatorsTest {

    @Test
    public void testSteppingDifferentialOperatorConstructor() {

        assertDoesNotThrow(() -> new LeftSteppingDifferentialOperator(0.1));

        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(-1));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.NaN));
    }

    @Test
    public void testLeftSteppingDifferentialOperator() {
        double step = 0.0001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        double x = 2.0;
        double expectedDerivative = 2 * x;
        double actualDerivative = derivative.apply(x);

        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Левая производная x^2 при x=2 должна быть приблизительно равна 4");

        x = 3.0;
        expectedDerivative = 2 * x;
        actualDerivative = derivative.apply(x);
        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Левая производная x^2 при x=3 должна быть приблизительно равна 6");
    }

    @Test
    public void testRightSteppingDifferentialOperator() {
        double step = 0.0001;
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(step);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        double x = 2.0;
        double expectedDerivative = 2 * x;
        double actualDerivative = derivative.apply(x);

        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Правая производная x^2 при x=2 должна быть приблизительно равна 4");

        x = 3.0;
        expectedDerivative = 2 * x;
        actualDerivative = derivative.apply(x);
        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Правая производная x^2 при x=3 должна быть приблизительно равна 6");
    }

    @Test
    public void testMiddleSteppingDifferentialOperator() {
        double step = 0.0001;
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(step);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        double x = 2.0;
        double expectedDerivative = 2 * x;
        double actualDerivative = derivative.apply(x);

        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Средняя производная x^2 при x=2 должна быть приблизительно равна 4");

        x = 3.0;
        expectedDerivative = 2 * x;
        actualDerivative = derivative.apply(x);
        assertEquals(expectedDerivative, actualDerivative, 0.01,
                "Средняя производная x^2 при x=3 должна быть приблизительно равна 6");
    }

    @Test
    public void testStepGetterAndSetter() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertEquals(0.1, operator.getStep(), 1e-10);

        operator.setStep(0.05);
        assertEquals(0.05, operator.getStep(), 1e-10);

        assertThrows(IllegalArgumentException.class, () -> operator.setStep(0));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(-1));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testDifferentStepSizes() {
        SqrFunction sqr = new SqrFunction();

        double[] steps = {0.01, 0.001, 0.0001};
        double x = 2.0;
        double expected = 4.0;

        for (double step : steps) {
            LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
            MathFunction derivative = operator.derive(sqr);
            double actual = derivative.apply(x);

            assertEquals(expected, actual, 0.1 ,
                    "Производная с шагом " + step + " должна быть точной");
        }
    }

    @Test
    public void testComparisonBetweenOperators() {
        double step = 0.1;
        SqrFunction sqr = new SqrFunction();
        double x = 2.0;

        double expectedLeft = 3.9;
        double expectedRight = 4.1;
        double expectedMiddle = 4.0;

        LeftSteppingDifferentialOperator leftOp = new LeftSteppingDifferentialOperator(step);
        RightSteppingDifferentialOperator rightOp = new RightSteppingDifferentialOperator(step);
        MiddleSteppingDifferentialOperator middleOp = new MiddleSteppingDifferentialOperator(step);

        double leftDerivative = leftOp.derive(sqr).apply(x);
        double rightDerivative = rightOp.derive(sqr).apply(x);
        double middleDerivative = middleOp.derive(sqr).apply(x);

        assertEquals(expectedLeft, leftDerivative, 0.0001, "Левая производная должна быть 3.9");
        assertEquals(expectedRight, rightDerivative, 0.0001, "Правая производная должна быть 4.1");
        assertEquals(expectedMiddle, middleDerivative, 0.0001, "Средняя производная должна быть 4.0");

        double exactDerivative = 4.0;
        double middleError = Math.abs(middleDerivative - exactDerivative);
        double leftError = Math.abs(leftDerivative - exactDerivative);
        double rightError = Math.abs(rightDerivative - exactDerivative);

        assertTrue(middleError <= leftError,
                "Средняя производная должна быть точнее левой");
        assertTrue(middleError <= rightError,
                "Средняя производная должна быть точнее правой");
    }
}
