package shunting.yard.functions;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public abstract class Function extends Token {

    private String name;
    private int minArgs;
    private int maxArgs;

    public Function(String name, int minArgs, int maxArgs) {
        super(Token.Type.FUNCTION);
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
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

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);

    @Override
    public String toString() {
        return this.name;
    }
}
