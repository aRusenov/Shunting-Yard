package shunting.yard.operators;

import java.math.BigDecimal;
import java.util.List;

public class MinusOperator extends Operator {

    public MinusOperator() {
        super("#", 100, false);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).negate();
    }
}
