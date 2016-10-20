package shunting.yard.misc;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public abstract class EvaluableToken extends Token {

    private int minArgsCount;
    private int maxArgsCount;

    public EvaluableToken(Type type, int argumentCount) {
        this(type, argumentCount, argumentCount);
    }

    public EvaluableToken(Type type, int minArgumentCount, int maxArgumentCount) {
        super(type);
        this.minArgsCount = minArgumentCount;
        this.maxArgsCount = maxArgumentCount;
    }

    public int getMinArgsCount() {
        return minArgsCount;
    }

    public int getMaxArgsCount() {
        return maxArgsCount;
    }

    private void setMinArgsCount(int minArgsCount) {
        // TODO: validate
        this.minArgsCount = minArgsCount;
    }

    private void setMaxArgsCount(int maxArgsCount) {
        // TODO: validate
        this.maxArgsCount = maxArgsCount;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);
}
