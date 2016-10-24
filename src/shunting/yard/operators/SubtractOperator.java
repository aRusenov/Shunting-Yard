package shunting.yard.operators;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class SubtractOperator extends Operator {

    public SubtractOperator() {
        super("-", 1, true);
    }

    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.subtract(arg2);
    }
}
