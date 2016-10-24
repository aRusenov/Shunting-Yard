package shunting.yard;

import shunting.yard.functions.Function;
import shunting.yard.misc.LeftParenthesis;
import shunting.yard.misc.Operand;
import shunting.yard.operators.Operator;

import java.math.BigDecimal;
import java.util.*;

import static shunting.yard.Token.Type.OPERATOR;

class PostfixConverter {

    private static final LeftParenthesis LEFT_PARENTHESIS = new LeftParenthesis();

    private Map<String, Operator> operators;
    private Map<String, Function> functions;
    private Stack<Token> operatorStack = new Stack<>();
    private Token currentToken;
    private Token lastToken;

    public PostfixConverter(Map<String, Operator> operators, Map<String, Function> functions) {
        this.operators = operators;
        this.functions = functions;
    }

    Queue<Token> convertToPostfixNotation(String expression) {
        lastToken = Token.getEmpty();
        currentToken = null;
        operatorStack.clear();

        Tokenizer tokenizer = new Tokenizer(expression);
        Queue<Token> output = new ArrayDeque<>();
        while (tokenizer.hasNext()) {
            int index = tokenizer.getIndex();
            String token = tokenizer.getNext();
            if (token.equals("-") && (lastToken.getType() == Token.Type.LEFT_PARENTHESIS ||
                    lastToken.getType() == Token.Type.OPERATOR || tokenizer.getCount() == 1)) {
                token = "#"; // Unary minus
            }

            if (tryReadNumber(token)) {
                output.add(currentToken);
            } else if (tryReadOperator(token)) {
                if (lastToken.getType() == OPERATOR && ((Operator)currentToken).isBinary()) {
                    String message = String.format(
                            "Operator inconsistency. %s cannot follow %s at index %d",
                            token, ((Operator)lastToken).getName(), index);

                    throw new InvalidExpressionException(message);
                }

                // Pop operators with higher or equal precedence and add them to output
                popToOutput((Operator) currentToken, output);
                operatorStack.push(currentToken);
            } else if (tryReadFunction(token)) {
                operatorStack.push(currentToken);
                String nextToken = tokenizer.hasNext() ? tokenizer.getNext() : null;
                if (nextToken == null || !nextToken.equals("(")) {
                    throw new InvalidExpressionException(
                            String.format("Missing left parenthesis after token %s at index %d",
                                    token, index));
                }

                currentToken = LEFT_PARENTHESIS;
                output.add(currentToken);
            } else if (token.equals("(")) {
                currentToken = LEFT_PARENTHESIS;
                operatorStack.push(currentToken);
            } else if (token.equals(")")) {
                while (true) {
                    if (operatorStack.size() == 0) {
                        throw new InvalidExpressionException("Inconsistent number of ')'");
                    }

                    Token operator = operatorStack.pop();
                    if (operator.getType() == Token.Type.LEFT_PARENTHESIS) {
                        break;
                    } else {
                        output.add(operator);
                        if (operator.getType() == Token.Type.FUNCTION) {
                            break;
                        }
                    }
                }
            } else if (token.equals(",")) {
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
            } else {
                if (token.trim().isEmpty()) {
                    continue;
                }

                throwInvalidToken(token, index);
            }

            lastToken = currentToken;
        }

        clearOperatorStack(output);
        return output;
    }

    private boolean tryReadNumber(String s) {
        try {
            BigDecimal value = new BigDecimal(s);
            currentToken = new Operand(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean tryReadOperator(String s) {
        Operator operator = operators.get(s);
        if (operator == null) {
            return false;
        } else {
            currentToken = operator;
            return true;
        }
    }

    private boolean tryReadFunction(String s) {
        Function function = functions.get(s);
        if (function == null) {
            return false;
        } else {
            currentToken = function;
            return true;
        }
    }

    private static void throwInvalidToken(String token, int index) {
        throw new InvalidExpressionException(
                String.format("Invalid token '%s' at index %d", token, index));
    }

    private void clearOperatorStack(Queue<Token> output) {
        while (operatorStack.size() > 0) {
            Token token = operatorStack.pop();
            if (token.getType() == Token.Type.LEFT_PARENTHESIS || token.getType() == Token.Type.FUNCTION) {
                throw new InvalidExpressionException("Inconsistent number of parenthesis");
            }

            output.add(token);
        }
    }

    private void popToOutput(Operator currentOperator, Queue<Token> output) {
        while (operatorStack.size() > 0) {
            Token topOperator = operatorStack.peek();
            if (topOperator.getType() != OPERATOR) {
                return;
            }

            boolean hasPrecedence = ((Operator) topOperator).getPrecedence() >= currentOperator.getPrecedence();
            if (hasPrecedence) {
                Token poppedToken = operatorStack.pop();
                output.add(poppedToken);
            } else {
                break;
            }
        }
    }
}
