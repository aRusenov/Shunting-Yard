package shunting.yard;

public class Token {

    public enum Type {
        NONE,
        OPERAND,
        OPERATOR,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS,
        FUNCTION
    }

    private static final Token TOKEN_EMPTY = new Token(Type.NONE);

    private Type type;

    public Token(Type type) {
        this.type = type;
    }

    static Token getEmpty() {
        return TOKEN_EMPTY;
    }

    Type getType() {
        return this.type;
    }
}
