package shunting.yard.misc;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public class Operand extends EvaluableToken {

    private BigDecimal value;

    public Operand(BigDecimal value) {
        super(Token.Type.OPERAND, 0);
        this.value = value;
    }

    public Operand(String strValue) {
        this(new BigDecimal(strValue));
    }


    public BigDecimal getValue() {
        return value;
    }

    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return value;
    }
}
