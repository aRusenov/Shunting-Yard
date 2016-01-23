package shunting.yard;

class Token {

    private static final String STRING_EMPTY = "";

    private TokenType tokenType;
    private String value;

    Token(TokenType type, String value) {
        this.setValue(value);
        this.setTokenType(type);
    }

    static Token getEmpty() {
        return new Token(TokenType.NONE, STRING_EMPTY);
    }

    Token(TokenType type, char value) {
        this(type, value + "");
    }

    String getValue() {
        return value;
    }

    TokenType getTokenType() {
        return this.tokenType;
    }

    void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "shunting.yard.Token value cannot be null");
        }

        this.value = value;
    }

    void setValue(char value) {
        this.setValue(value + "");
    }

    void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
