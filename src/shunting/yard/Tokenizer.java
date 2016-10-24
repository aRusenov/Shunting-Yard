package shunting.yard;

class Tokenizer {

    static final int NUMBER = 1;
    static final int WORD = 2;
    static final int OTHER = 3;

    private String str;
    private int index;
    private int count;
    private int type;
    private StringBuilder sb;

    Tokenizer(String str) {
        this.str = str;
        sb = new StringBuilder();
    }

    int getIndex() {
        return index;
    }

    int getCount() {
        return count;
    }

    public int getType() {
        return type;
    }

    boolean hasNext() {
        return index < str.length();
    }

    String getNext() {
        if (index == str.length()) {
            throw new IllegalStateException("Iterator has finished");
        }

        if (tryGetNumber()) {
            type = NUMBER;
        } else if (tryGetWord()) {
            type = WORD;
        } else {
            type = OTHER;
            sb.append(str.charAt(index));
        }

        count++;
        String token = sb.toString();
        index += token.length();
        sb.setLength(0);

        return token;
    }

    private boolean tryGetNumber() {
        int i = index;
        boolean foundSeparator = false;
        while (i < str.length() && (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')) {
            if (str.charAt(i) == '.') {
                if (foundSeparator) {
                    break;
                }
                foundSeparator = true;
            }

            sb.append(str.charAt(i));
            i++;
        }

        if (sb.length() == 0 || (sb.length() == 1 && foundSeparator)) {
            sb.setLength(0);
            return false;
        }

        return true;
    }

    private boolean tryGetWord() {
        int i = index;
        while (i < str.length() && Character.isAlphabetic(str.charAt(i))) {
            i++;
        }

        if (i == index) {
            return false;
        }

        sb.append(str, index, i);
        return true;
    }
}
