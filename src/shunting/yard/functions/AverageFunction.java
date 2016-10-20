package shunting.yard.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class AverageFunction extends Function {

    public AverageFunction() {
        super("avg", 1, Integer.MAX_VALUE);
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        BigDecimal sum = new BigDecimal(0);
        for (BigDecimal arg : args) {
            sum = sum.add(arg);
        }

        return sum.divide(BigDecimal.valueOf(args.size()), MathContext.DECIMAL128);
    }
}
