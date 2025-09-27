package functions;

import functions.CubicBSplineFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CubicBSplineFunctionTest {

    private final CubicBSplineFunction spline = new CubicBSplineFunction();
    private final double delta = 1e-9;

    @Test
    public void testApplyWithZero() {
        assertEquals(2.0/3.0, spline.apply(0.0), delta);
    }

    @Test
    public void testApplyWithIntegerPoints() {
        assertEquals(1.0/6.0, spline.apply(1.0), delta);
        assertEquals(1.0/6.0, spline.apply(-1.0), delta);
        assertEquals(0.0, spline.apply(2.0), delta);
        assertEquals(0.0, spline.apply(-2.0), delta);
    }

    @Test
    public void testApplyWithFractionalPoints() {
        assertEquals(0.47916666666666663, spline.apply(0.5), delta);
        double expectedAt1_5 = Math.pow(0.5, 3) / 6.0;
        assertEquals(expectedAt1_5, spline.apply(1.5), delta);
    }

    @Test
    public void testApplyOutsideDomain() {
        assertEquals(0.0, spline.apply(3.0), delta);
        assertEquals(0.0, spline.apply(-3.0), delta);
    }

    @Test
    public void testSymmetry() {
        assertEquals(spline.apply(0.5), spline.apply(-0.5), delta);
        assertEquals(spline.apply(1.2), spline.apply(-1.2), delta);
    }
}