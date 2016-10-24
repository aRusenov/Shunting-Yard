package shunting.yard;

import shunting.yard.functions.Function;
import shunting.yard.misc.EvaluableToken;
import shunting.yard.misc.LeftParenthesis;
import shunting.yard.misc.Operand;
import shunting.yard.operators.Operator;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
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

    Queue<EvaluableToken> convertToPostfixNotation(String expression) throws IOException {
        lastToken = Token.getEmpty();
        currentToken = null;
        operatorStack.clear();

        Queue<EvaluableToken> output = new ArrayDeque<>();
        List<String> tokens = tokenize(expression);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.equals("-") && (lastToken.getType() == Token.Type.LEFT_PARENTHESIS ||
                    lastToken.getType() == Token.Type.OPERATOR || i == 0)) {
                token = "#"; // Unary minus
            }

            if (tryReadNumber(token)) {
                output.add((Operand) currentToken);
            } else if (tryReadOperator(token)) {
                if (lastToken.getType() == OPERATOR && ((Operator)currentToken).isBinary()) {
                    String message = String.format(
                            "Operator inconsistency. %s cannot follow %s",
                            token, ((Operator)lastToken).getName());

                    throw new InvalidExpressionException(message);
                }

                // Pop operators with higher or equal precedence and add them to output
                popToOutput((Operator) currentToken, output);
                operatorStack.push(currentToken);
            } else if (tryReadFunction(token)) {
                operatorStack.push(currentToken);
                String nextToken = i < tokens.size() - 1 ? tokens.get(i + 1) : null;
                if (nextToken == null || !nextToken.equals("(")) {
                    throw new InvalidExpressionException("Missing left parenthesis after token " + token);
                }

                currentToken = LEFT_PARENTHESIS;
                i++;
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
                        output.add((EvaluableToken) operator);
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
                    output.add((EvaluableToken) operator);
                }
            } else {
                if (token.trim().isEmpty()) {
                    continue;
                }

                throwInvalidToken(token);
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

    private static List<String> tokenize(String s) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
        tokenizer.ordinaryChar('-');  // Don't parse minus as part of numbers.
        List<String> tokens = new ArrayList<>();
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            switch (tokenizer.ttype) {
                case StreamTokenizer.TT_NUMBER:
                    tokens.add(String.valueOf(tokenizer.nval));
                    break;
                case StreamTokenizer.TT_WORD:
                    tokens.add(tokenizer.sval);
                    break;
                default:  // operator
                    tokens.add(String.valueOf((char) tokenizer.ttype));
            }
        }

        return tokens;
    }

    private static void throwInvalidToken(String token) {
        throw new InvalidExpressionException(
                String.format("Invalid token '%s'", token));
    }

    private void clearOperatorStack(Queue<EvaluableToken> output) {
        while (operatorStack.size() > 0) {
            Token token = operatorStack.pop();
            if (token.getType() == Token.Type.LEFT_PARENTHESIS) {
                throw new InvalidExpressionException("Inconsistent number of parenthesis");
            }

            output.add((Operator) token);
        }
    }

    private void popToOutput(Operator currentOperator, Queue<EvaluableToken> output) {
        while (operatorStack.size() > 0) {
            Token topOperator = operatorStack.peek();
            if (topOperator.getType() != OPERATOR) {
                return;
            }

            boolean hasPrecedence = ((Operator) topOperator).getPrecedence() >= currentOperator.getPrecedence();
            if (hasPrecedence) {
                Token poppedToken = operatorStack.pop();
                output.add((Operator) poppedToken);
            } else {
                break;
            }
        }
    }
}
