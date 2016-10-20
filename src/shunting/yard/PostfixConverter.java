package shunting.yard;

import shunting.yard.functions.Function;
import shunting.yard.misc.EvaluableToken;
import shunting.yard.misc.LeftParenthesis;
import shunting.yard.misc.Operand;
import shunting.yard.operators.Operator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

class PostfixConverter {

    private static final LeftParenthesis LEFT_PARENTHESIS = new LeftParenthesis();

    static Queue<EvaluableToken> convertToPostfixNotation(String expression,
                                                          Map<String, Operator> operators,
                                                          Map<String, Function> functions) throws IOException {
        Stack<Token> operatorStack = new Stack<>();
        Queue<EvaluableToken> output = new ArrayDeque<>();
        Token lastToken = Token.getEmpty();

        for (int i = 0; i < expression.length(); i++) {
            Token currentToken = Token.getEmpty();
            char currentCharacter = expression.charAt(i);

            boolean isUnaryMinus = currentCharacter == '-' &&
                    lastToken.getType() == Token.Type.OPERATOR || lastToken.getType() == Token.Type.LEFT_PARENTHESIS;
            if (Character.isDigit(currentCharacter) || isUnaryMinus) {
                String numberAsString = getNextNumber(expression, i);
                currentToken = new Operand(numberAsString);
                output.add((Operand)currentToken);

                i += numberAsString.length() - 1;
            } else if (operators.containsKey(String.valueOf(currentCharacter))) {
                validateOperatorConsistency(lastToken, currentCharacter);

                // Pop operators with higher or equal precedence and add them to output
                currentToken = operators.get(String.valueOf(currentCharacter));
                popToOutput(operatorStack, output, (Operator)currentToken);

                operatorStack.push(currentToken);
            } else if (currentCharacter == '(') {
                currentToken = LEFT_PARENTHESIS;
                operatorStack.push(currentToken);
            } else if (currentCharacter == ')') {
                while (true) {
                    if (operatorStack.size() == 0) {
                        throw new InvalidExpressionException("Inconsistent number of ')'");
                    }

                    Token operator = operatorStack.pop();
                    Token.Type type = operator.getType();

                    if (type == Token.Type.LEFT_PARENTHESIS) {
                        break;
                    } else {
                        output.add((EvaluableToken) operator);
                        if (type == Token.Type.FUNCTION) {
                            break;
                        }
                    }
                }
            } else if (currentCharacter == ',') {
                while (true) {
                    if (operatorStack.size() == 0) {
                        throw new InvalidExpressionException("Inconsistent number of ')'");
                    }

                    if (operatorStack.peek().getType() == Token.Type.FUNCTION) {
                        break;
                    }

                    Token operator = operatorStack.pop();
                    output.add((EvaluableToken) operator);
                }
            } else if (Character.isAlphabetic(currentCharacter)) {
                String function = getNextFunction(expression, i);
                int leftParenthesisIndex = i + function.length();

                currentToken = getFunctionByName(expression, function, functions, leftParenthesisIndex);
                operatorStack.push(currentToken);

                i += function.length();
            } else {
                if (Character.isWhitespace(currentCharacter)) {
                    continue;
                }

                throwInvalidToken(currentCharacter, i);
            }

            lastToken = currentToken;
        }

        clearOperatorStack(operatorStack, output);

        return output;
    }

    private static void throwInvalidToken(char symbol, int index) {
        throw new InvalidExpressionException(
                String.format("Invalid token '%c' at index %d", symbol, index));
    }

    private static void clearOperatorStack(Stack<Token> operators, Queue<EvaluableToken> output) {
        while (operators.size() > 0) {
            Token token = operators.pop();
            if (token.getType() == Token.Type.LEFT_PARENTHESIS || token.getType() == Token.Type.RIGHT_PARENTHESIS) {
                throw new InvalidExpressionException(
                        String.format("Inconsistent number of '%s' tokens",
                                token.getClass().getSimpleName()));
            }

            output.add((EvaluableToken) token);
        }
    }

    private static void popToOutput(Stack<Token> operatorStack, Queue<EvaluableToken> output, Operator currentOperator) {
        while (operatorStack.size() > 0) {
            Token topOperator = operatorStack.peek();
            if (topOperator.getType() != Token.Type.OPERATOR) {
                return;
            }

            boolean hasPrecedence = ((Operator)topOperator).getPrecedence() >= currentOperator.getPrecedence();
            if (hasPrecedence) {
                Token poppedToken = operatorStack.pop();
                output.add((Operator) poppedToken);
            } else {
                break;
            }
        }
    }

    private static String getNextNumber(String input, int startIndex) {
        int endIndex = startIndex + 1;
        int separatorCount = 0;
        while (endIndex < input.length() &&
                (Character.isDigit(input.charAt(endIndex)) || input.charAt(endIndex) == '.')) {
            if (input.charAt(endIndex) == '.') {
                if (separatorCount == 1) {
                    throwInvalidToken('.', endIndex);
                }

                separatorCount++;
            }
            endIndex++;
        }

        return input.substring(startIndex, endIndex);
    }

    private static String getNextFunction(String input, int startIndex) {
        int endIndex = startIndex + 1;
        while (endIndex < input.length() &&
                Character.isAlphabetic(input.charAt(endIndex))) {
            endIndex++;
        }

        return input.substring(startIndex, endIndex);
    }

    private static Function getFunctionByName(String input, String functionName, Map<String, Function> functions, int openingIndex) {
        if (openingIndex >= input.length() || input.charAt(openingIndex) != '(') {
            throw new InvalidExpressionException(
                    String.format(
                            "Missing left parenthesis after token '%s'",
                            functionName));
        }

        Function func = functions.get(functionName);
        if (func == null) {
            throw new InvalidExpressionException(
                    "Function '" + functionName + "' does not exist"
            );
        }

        return func;
    }

    private static void validateOperatorConsistency(Token lastToken, char currentCharacter) {
        if (lastToken.getType() == Token.Type.OPERATOR) {
            String message = String.format(
                    "Operator inconsistency. %s cannot follow %s",
                    currentCharacter,
                    lastToken.toString()
            );

            throw new InvalidExpressionException(message);
        }
    }
}
