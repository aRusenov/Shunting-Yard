package shunting.yard.functions;

import java.math.BigDecimal;
import java.util.List;

public class MaxFunction extends Function {

    public MaxFunction() {
        super("max", 1, Integer.MAX_VALUE);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        BigDecimal max = null;
        for (BigDecimal current : args) {
            if (max == null || current.compareTo(max) > 0) {
                max = current;
            }
        }

        return max;
    }
}
