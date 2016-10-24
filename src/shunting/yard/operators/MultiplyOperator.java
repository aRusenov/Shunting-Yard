package shunting.yard.operators;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class MultiplyOperator extends Operator {

    public MultiplyOperator() {
        super("*", 2, true);
    }

    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.multiply(arg2);
    }
}
