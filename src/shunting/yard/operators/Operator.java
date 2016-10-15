package shunting.yard.operators;

import shunting.yard.Token;

import java.math.BigDecimal;
import java.util.List;

public abstract class Operator extends Token {

    public enum Associativity {
        LEFT,
        RIGHT
    }

    public enum Type {
        UNARY,
        BINARY
    }

    private String name;
    private int precedence;
    private Associativity associativity;
    private Type type;

    public Operator(String name, int precedence, Associativity associativity, Type type) {
        super(Token.Type.OPERATOR);
        this.name = name;
        this.precedence = precedence;
        this.associativity = associativity;
        this.type = type;
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

    public Associativity getAssociativity() {
        return associativity;
    }

    private void setAssociativity(Associativity associativity) {
        this.associativity = associativity;
    }

    public Type getType() {
        return type;
    }

    private void setType(Type type) {
        this.type = type;
    }

    public abstract BigDecimal eval(List<BigDecimal> args);
}
