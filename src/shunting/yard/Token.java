package shunting.yard;

public class Token {

    protected enum Type {
        NONE,
        OPERAND,
        OPERATOR,
        LEFT_PARENTHESIS,
        FUNCTION
    }

    private static final Token TOKEN_EMPTY = new Token(Type.NONE);
    static Token getEmpty() {
        return TOKEN_EMPTY;
    }

    private Type type;

    public Token(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}
