package functions;

public class CubicBSplineFunction implements MathFunction {

    @Override
    public double apply(double x) {
        double t = Math.abs(x);

        if (t >= 2) {
            return 0.0;
        }

        if (t <= 1) {
            return (2.0 / 3.0) - t * t + (t * t * t) / 2.0;
        } else {
            double u = 2 - t;
            return (u * u * u) / 6.0;
        }
    }
}
