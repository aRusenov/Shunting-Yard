package shunting.yard;

import shunting.yard.functions.AverageFunction;
import shunting.yard.functions.Function;
import shunting.yard.functions.MaxFunction;
import shunting.yard.functions.MinFunction;
import shunting.yard.misc.LeftParenthesis;
import shunting.yard.misc.Operand;
import shunting.yard.operators.*;

import java.math.BigDecimal;
import java.util.*;

public class Evaluator {

    private final LeftParenthesis LEFT_PARENTHESIS = new LeftParenthesis();

    private Map<String, Function> functions;
    private Map<String, Operator> operators;

    public Evaluator() {
        operators = new HashMap<>();
        addOperator(new AddOperator());
        addOperator(new SubtractOperator());
        addOperator(new MultiplyOperator());
        addOperator(new DivideOperator());
        addOperator(new PowerOperator());

        functions = new HashMap<>();
        addFunction(new MinFunction());
        addFunction(new MaxFunction());
        addFunction(new AverageFunction());
    }

    public void addOperator(Operator operator) {
        operators.put(operator.getName(), operator);
    }

    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public BigDecimal eval(String expression) {
        String cleaned = expression.replace(" ", "");
        Queue<Token> tokens = convertToPostfixNotation(cleaned);
        return evaluate(tokens);
    }

    private static BigDecimal evaluate(Queue<Token> tokens) {
        Stack<BigDecimal> operands = new Stack<>();
        while (tokens.size() > 0) {
            Token token = tokens.remove();
            Token.Type type = token.getType();

            if (type == Token.Type.OPERAND) {
                operands.push(((Operand) token).getValue());
            } else if (type == Token.Type.OPERATOR){
                Operator operator = (Operator) token;
                BigDecimal operand2 = operands.pop();
                BigDecimal operand1 = operands.pop();
                List<BigDecimal> params = new ArrayList<>();
                params.add(operand1);
                params.add(operand2);
                // TODO: Binary/Unary
                BigDecimal result = operator.eval(params);
                operands.push(result);
            } else if (type == Token.Type.FUNCTION) {
                Function func = (Function) token;
                List<BigDecimal> params = new ArrayList<>();
                int expectedParams = func.getParameterCount();
                if (expectedParams == -1) {
                    while (operands.size() > 0) {
                        params.add(operands.pop());
                    }
                } else {
                    if (operands.size() < expectedParams) {
                        String error = String.format("Function %s expected %d params, got %d instead.",
                                func.getName(), expectedParams, operands.size());
                        throw new InvalidExpressionException(error);
                    }

                    for (int i = 0; i < expectedParams; i++) {
                        params.add(operands.pop());
                    }
                }

                BigDecimal result = func.eval(params);
                operands.push(result);
            }
        }

        return operands.pop();
    }

    private Queue<Token> convertToPostfixNotation(String input) {
        Stack<Token> operatorStack = new Stack<>();
        Queue<Token> output = new ArrayDeque<>();
        Token lastToken = Token.getEmpty();

        for (int i = 0; i < input.length(); i++) {
            Token currentToken = Token.getEmpty();
            char currentCharacter = input.charAt(i);

            boolean isMinus = currentCharacter == '-' &&
                    lastToken.getType() == Token.Type.OPERATOR;
            if (Character.isDigit(currentCharacter) || isMinus) {
                String numberAsString = getNextNumber(input, i);
                currentToken = new Operand(numberAsString);
                output.add(currentToken);

                i += numberAsString.length() - 1;
            } else if (operators.containsKey(String.valueOf(currentCharacter))) {
                validateOperatorConsistency(lastToken, currentCharacter);

                // Pop operators with higher or equal precedence and add them to output
                currentToken = operators.get(String.valueOf(currentCharacter));
                popToOutput(operatorStack, output, currentToken);

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
                        output.add(operator);
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
                    output.add(operator);
                }
            } else if (Character.isAlphabetic(currentCharacter)) {
                String function = getNextFunction(input, i);
                int leftParenthesisIndex = i + function.length();

                currentToken = getFunctionByName(input, function, leftParenthesisIndex);
                operatorStack.push(currentToken);

                i += function.length();
            } else {
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

    private void clearOperatorStack(Stack<Token> operators, Queue<Token> output) {
        while (operators.size() > 0) {
            Token token = operators.pop();
            Token.Type type = token.getType();

            if (type == Token.Type.LEFT_PARENTHESIS || type == Token.Type.RIGHT_PARENTHESIS) {
                throw new InvalidExpressionException(
                        String.format(
                                "Inconsistent number of '%s' tokens",
                                token.getClass().getSimpleName()));
            }

            output.add(token);
        }
    }

    private static void popToOutput(Stack<Token> operatorStack, Queue<Token> output, Token currentToken) {
        while (operatorStack.size() > 0) {
            Token peekedToken = operatorStack.peek();
            boolean isOperator = peekedToken.getType() == Token.Type.OPERATOR;
            if (! isOperator) {
                return;
            }

            boolean hasPrecedence = ((Operator)peekedToken).getPrecedence() >=
                    ((Operator)currentToken).getPrecedence();

            if (hasPrecedence) {
                Token poppedToken = operatorStack.pop();
                output.add(poppedToken);
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

    private Function getFunctionByName(String input, String functionName, int openingIndex) {
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
