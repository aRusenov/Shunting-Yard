package shunting.yard.operators;

import java.math.BigDecimal;
import java.util.List;

public class MultiplyOperator extends Operator {

    public MultiplyOperator() {
        super("*", 2, true);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).multiply(args.get(1));
    }
}
