package shunting.yard.operators;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class DivideOperator extends Operator {

    public DivideOperator() {
        super("/", 2, true);
    }

    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.divide(arg2, MathContext.DECIMAL128);
    }
}
