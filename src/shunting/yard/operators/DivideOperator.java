package shunting.yard.operators;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class DivideOperator extends Operator {

    public DivideOperator() {
        super("/", 2, true);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).divide(args.get(1), MathContext.DECIMAL128);
    }
}
