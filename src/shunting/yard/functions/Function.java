package shunting.yard.functions;

import shunting.yard.Token;
import shunting.yard.misc.EvaluableToken;

import java.math.BigDecimal;
import java.util.List;

public abstract class Function extends EvaluableToken {

    private String name;

    public Function(String name, int minArgumentCount, int maxArgumentCount) {
        super(Token.Type.FUNCTION, minArgumentCount, maxArgumentCount);
        setName(name);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Function name cannot be null");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Function name must be at 2 symbols");
        }

        this.name = name;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);

    @Override
    public String toString() {
        return this.name;
    }
}
