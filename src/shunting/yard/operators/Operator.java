package shunting.yard.operators;

import shunting.yard.Token;
import shunting.yard.misc.EvaluableToken;

import java.math.BigDecimal;
import java.util.List;

public abstract class Operator extends EvaluableToken {

    private String name;
    private int precedence;
    private boolean isBinary;

    public Operator(String name, int precedence, boolean isBinary) {
        super(Token.Type.OPERATOR, isBinary ? 2 : 1);
        this.name = name;
        this.precedence = precedence;
        this.isBinary = isBinary;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getPrecedence() {
        return precedence;
    }

    private void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);

    @Override
    public String toString() {
        return this.name;
    }
}
