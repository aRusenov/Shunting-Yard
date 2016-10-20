package shunting.yard.operators;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class PowerOperator extends Operator {

    public PowerOperator() {
        super("^", 3, true);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).pow(args.get(1).intValue(), MathContext.DECIMAL128);
    }
}
