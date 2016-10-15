package shunting.yard.operators;

import java.math.BigDecimal;
import java.util.List;

public class SubtractOperator extends Operator {

    public SubtractOperator() {
        super("-", 1, Associativity.LEFT, Type.BINARY);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).subtract(args.get(1));
    }
}
