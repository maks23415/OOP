package functions;

public class DerivativeFunction implements MathFunction {
    private MathFunction originalFunction;
    private double deltaX;

    public  DerivativeFunction(MathFunction originalFunction, double deltaX)
    {
        this.originalFunction = originalFunction;
        this.deltaX = deltaX;
    }

    public DerivativeFunction(MathFunction originalFunction) {
        this(originalFunction, 1e-9); // Значение по умолчанию
    }

    @Override
    public double apply(double x) {
        return (originalFunction.apply(x+deltaX)-originalFunction.apply(x))/deltaX;
    }
}
