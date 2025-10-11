package functions;

public class ConstantFunction implements MathFunction {
    private final double arg;

    public double getArg() {
        return arg;
    }

    public ConstantFunction(double arg) {
        this.arg = arg;
    }
    @Override
    public double apply(double x) {
        return arg;
    }
}