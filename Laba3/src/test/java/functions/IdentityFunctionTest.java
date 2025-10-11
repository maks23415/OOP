package functions;

import functions.IdentityFunction;
import functions.MathFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityFunctionTest {

    @Test
    void testApply() {
        MathFunction id = new IdentityFunction();

        assertEquals(0.0, id.apply(0.0), 1e-9);
        assertEquals(1.0, id.apply(1.0), 1e-9);
        assertEquals(-5.5, id.apply(-5.5), 1e-9);
        assertEquals(100.0, id.apply(100.0), 1e-9);
    }
}