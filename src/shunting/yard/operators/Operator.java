package shunting.yard.operators;

import com.sun.istack.internal.Nullable;
import shunting.yard.Token;

import java.math.BigDecimal;

public abstract class Operator extends Token {

    private String name;
    private int precedence;
    private boolean isBinary;

    public Operator(String name, int precedence, boolean isBinary) {
        super(Token.Type.OPERATOR);
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

    public abstract BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2);

    @Override
    public String toString() {
        return this.name;
    }
}
