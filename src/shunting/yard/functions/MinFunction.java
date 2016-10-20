package shunting.yard.functions;

import java.math.BigDecimal;
import java.util.List;

public class MinFunction extends Function {

    public MinFunction() {
        super("min", 1, Integer.MAX_VALUE);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        BigDecimal min = null;
        for (BigDecimal current : args) {
            if (min == null || current.compareTo(min) < 0) {
                min = current;
            }
        }

        return min;
    }
}
