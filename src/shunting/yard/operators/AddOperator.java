package shunting.yard.operators;

import java.math.BigDecimal;
import java.util.List;

public class AddOperator extends Operator {

    public AddOperator() {
        super("+", 1, true);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).add(args.get(1));
    }
}
