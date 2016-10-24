package shunting.yard.operators;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class MinusOperator extends Operator {

    public MinusOperator() {
        super("#", 100, false);
    }

    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.negate();
    }
}
