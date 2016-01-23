package shunting.yard;

import java.util.*;

public class ExpressionEvaluator {

    private static Map<String, Integer> operatorsPrecedence;
    private static Set<String> functions;

    static {
        operatorsPrecedence = new HashMap<>();
        operatorsPrecedence.put("^", 3);
        operatorsPrecedence.put("*", 2);
        operatorsPrecedence.put("/", 2);
        operatorsPrecedence.put("+", 1);
        operatorsPrecedence.put("-", 1);

        functions = new HashSet<>();
        functions.add("min");
        functions.add("max");
        functions.add("sqrt");
        functions.add("sin");
        functions.add("cos");
        functions.add("deg");
        functions.add("rad");
    }

    public static double evaluate(String expression) {
        String sanitizedExpression = sanitizeExpression(expression);
        Queue<Token> tokens = convertToPostfixNotation(sanitizedExpression);
        double result = evaluate(tokens);

        return result;
    }

    private static String sanitizeExpression(String expression) {
        return expression.replace(" ", "");
    }

    private static double evaluate(Queue<Token> tokens) {
        Stack<Double> operands = new Stack<>();
        while (tokens.size() > 0) {
            Token token = tokens.remove();
            TokenType type = token.getTokenType();

            if (type == TokenType.OPERAND) {
                double value = Double.parseDouble(token.getValue());
                operands.push(value);
            } else if (type == TokenType.OPERATOR){
                double operandA = operands.pop();
                double operandB = operands.pop();
                String operator = token.getValue();

                double result = TokenProcessor.processOperation(operator, operandA, operandB);
                operands.push(result);
            } else if (type == TokenType.FUNCTION) {
                double result = TokenProcessor.processFunction(token.getValue(), operands);
                operands.push(result);
            }
        }

        double result = operands.pop();

        return result;
    }

    private static Queue<Token> convertToPostfixNotation(String input) {
        Stack<Token> operators = new Stack<>();
        Queue<Token> output = new ArrayDeque<>();
        Token lastToken = Token.getEmpty();

        for (int i = 0; i < input.length(); i++) {
            Token currentToken = Token.getEmpty();
            char currentCharacter = input.charAt(i);

            boolean isMinus = currentCharacter == '-' &&
                    lastToken.getTokenType() == TokenType.OPERATOR;
            if (Character.isDigit(currentCharacter) || isMinus) {
                String numberAsString = getNextNumber(input, i);
                currentToken = new Token(TokenType.OPERAND, numberAsString);
                output.add(currentToken);

                i += numberAsString.length() - 1;
            } else if (operatorsPrecedence.containsKey(currentCharacter + "")) {
                validateOperatorConsistency(lastToken, currentCharacter);

                // Pop operators with higher or equal precedence and add them to output
                currentToken = new Token(TokenType.OPERATOR, currentCharacter);
                popToOutput(operators, output, currentToken);

                operators.push(currentToken);
            } else if (currentCharacter == '(') {
                currentToken = new Token(TokenType.LEFT_PARENTHESIS, currentCharacter);
                operators.push(currentToken);
            } else if (currentCharacter == ')') {
                while (true) {
                    if (operators.size() == 0) {
                        throw new InvalidExpressionException("Inconsistent number of ')'");
                    }

                    Token operator = operators.pop();
                    TokenType type = operator.getTokenType();

                    if (type == TokenType.LEFT_PARENTHESIS) {
                        break;
                    } else {
                        output.add(operator);
                        if (type == TokenType.FUNCTION) {
                            break;
                        }
                    }
                }
            } else if (Character.isAlphabetic(currentCharacter)) {
                String function = getNextFunction(input, i);
                int leftParenthesisIndex = i + function.length();
                validateFunctionToken(input, function, leftParenthesisIndex);

                currentToken = new Token(TokenType.FUNCTION, function);
                operators.push(currentToken);

                i += function.length();
            }

            lastToken = currentToken;
        }

        clearOperatorStack(operators, output);

        return output;
    }

    private static void clearOperatorStack(Stack<Token> operators, Queue<Token> output) {
        while (operators.size() > 0) {
            Token token = operators.pop();
            TokenType type = token.getTokenType();

            if (type == TokenType.LEFT_PARENTHESIS || type == TokenType.RIGHT_PARENTHESIS) {
                throw new InvalidExpressionException(
                        String.format(
                                "Inconsistent number of '%s' tokens",
                                token.getValue()));
            }

            output.add(token);
        }
    }

    private static void popToOutput(Stack<Token> operators, Queue<Token> output, Token currentToken) {
        while (operators.size() > 0) {
            Token peekedToken = operators.peek();

            boolean isOperator = peekedToken.getTokenType() == TokenType.OPERATOR;
            boolean hasPrecedence = isOperator &&
                    operatorsPrecedence.get(peekedToken.getValue()) >= operatorsPrecedence.get(currentToken.getValue());
            if (hasPrecedence) {
                Token poppedToken = operators.pop();
                output.add(poppedToken);
            } else {
                break;
            }
        }
    }

    private static String getNextNumber(String input, int startIndex) {
        int endIndex = startIndex + 1;
        while (endIndex < input.length() &&
                (Character.isDigit(input.charAt(endIndex)) ||
                        input.charAt(endIndex) == '.')) {
            endIndex++;
        }

        String numberAsString = input.substring(startIndex, endIndex);

        return numberAsString;
    }

    private static String getNextFunction(String input, int startIndex) {
        int endIndex = startIndex + 1;
        while (endIndex < input.length() &&
                Character.isAlphabetic(input.charAt(endIndex))) {
            endIndex++;
        }

        String functionString = input.substring(startIndex, endIndex);

        return functionString;
    }

    private static void validateFunctionToken(String input, String function, int openingIndex) {

        if (!functions.contains(function)) {
            throw new InvalidExpressionException(
                    "Function '" + function + "' does not exist"
            );
        }

        if (openingIndex >= input.length() || input.charAt(openingIndex) != '(') {
            throw new InvalidExpressionException(
                    String.format(
                            "Missing left parenthesis after token '%s'",
                            function));
        }
    }

    private static void validateOperatorConsistency(Token lastToken, char currentCharacter) {
        if (lastToken.getTokenType() == TokenType.OPERATOR) {
            String message = String.format(
                    "Operator inconsistency. %s cannot follow %s",
                    currentCharacter,
                    lastToken.getValue()
            );

            throw new InvalidExpressionException(message);
        }
    }
}
