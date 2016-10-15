package shunting.yard.functions;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public abstract class Function extends Token {

    private String name;
    private int parameterCount;

    public Function(String name, int parameterCount) {
        super(Token.Type.FUNCTION);
        setName(name);
        setParameterCount(parameterCount);
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

    private void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);
}
