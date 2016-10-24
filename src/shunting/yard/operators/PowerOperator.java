package shunting.yard.operators;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class PowerOperator extends Operator {

    public PowerOperator() {
        super("^", 3, true);
    }

    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.pow(arg2.intValue(), MathContext.DECIMAL128);
    }
}
