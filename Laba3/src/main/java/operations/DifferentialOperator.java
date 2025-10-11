package operations;

public interface DifferentialOperator <T extends functions.MathFunction>{
    T derive(T function);
}
