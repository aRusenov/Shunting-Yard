package shunting.yard.misc;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public class Operand extends Token {

    private BigDecimal value;

    public Operand(BigDecimal value) {
        super(Token.Type.OPERAND);
        this.value = value;
    }

    public Operand(String strValue) {
        this(new BigDecimal(strValue));
    }

    public BigDecimal getValue() {
        return value;
    }
}
