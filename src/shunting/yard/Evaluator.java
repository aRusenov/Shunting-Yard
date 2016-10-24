package shunting.yard;

import shunting.yard.functions.AverageFunction;
import shunting.yard.functions.Function;
import shunting.yard.functions.MaxFunction;
import shunting.yard.functions.MinFunction;
import shunting.yard.misc.Operand;
import shunting.yard.operators.*;

import java.math.BigDecimal;
import java.util.*;

public class Evaluator {

    private Map<String, Function> functions;
    private Map<String, Operator> operators;
    private PostfixConverter postfixConverter;

    public Evaluator() {
        operators = new HashMap<>();
        addOperator(new AddOperator());
        addOperator(new SubtractOperator());
        addOperator(new MultiplyOperator());
        addOperator(new DivideOperator());
        addOperator(new PowerOperator());
        addOperator(new MinusOperator());

        functions = new HashMap<>();
        addFunction(new MinFunction());
        addFunction(new MaxFunction());
        addFunction(new AverageFunction());

        postfixConverter = new PostfixConverter(operators, functions);
    }

    public void addOperator(Operator operator) {
        operators.put(operator.getName(), operator);
    }

    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public BigDecimal eval(String expression) {
        Queue<Token> tokens = postfixConverter.convertToPostfixNotation(expression);
        return evaluate(tokens);
    }

    private static BigDecimal evaluate(Queue<Token> tokens) {
        Stack<Token> operands = new Stack<>();
        ArrayList<BigDecimal> functionArgsList = new ArrayList<>(4); // Reuse array
        while (tokens.size() > 0) {
            Token token = tokens.remove();
            if (token.getType() == Token.Type.LEFT_PARENTHESIS || token.getType() == Token.Type.OPERAND) {
                operands.push(token);
            } else if (token.getType() == Token.Type.OPERATOR) {
                Operator operator = (Operator) token;
                int expectedArgs = operator.isBinary() ? 2 : 1;
                if (operands.size() < expectedArgs) {
                    throw new InvalidExpressionException(
                            String.format("Operator %s expects %d arguments. Available: %d",
                                    operator.getName(), expectedArgs, operands.size()));
                }

                BigDecimal result = evaluateOperator(operands, operator);
                operands.push(new Operand(result));
            } else if (token.getType() == Token.Type.FUNCTION) {
                Function function = (Function) token;
                BigDecimal result = evaluateFunction(operands, functionArgsList, function);
                operands.push(new Operand(result));
                functionArgsList.clear();
            }
        }

        if (operands.size() != 1) {
            throw new InvalidExpressionException("The expression is invalid");
        }

        return ((Operand)operands.pop()).getValue();
    }

    private static BigDecimal evaluateFunction(Stack<Token> operands, ArrayList<BigDecimal> argsList, Function function) {
        int givenArgs = 0;
        while (operands.size() > 0 && givenArgs < function.getMaxArgs()) {
            Token operand = operands.pop();
            if (operand.getType() == Token.Type.LEFT_PARENTHESIS) {
                break;
            }

            argsList.add(((Operand) operand).getValue());
            givenArgs++;
        }

        if (givenArgs < function.getMinArgs()) {
            throw new InvalidExpressionException(
                    String.format("Function %s expects at least %d argument(s). Available: %d.",
                            function.getName(), function.getMinArgs(), givenArgs));
        }

        Collections.reverse(argsList);
        return function.eval(argsList);
    }

    private static BigDecimal evaluateOperator(Stack<Token> operands, Operator operator) {
        BigDecimal valB = null, valA = null;
        Token opB = operands.pop();
        checkNotLeftParenthesis(opB);
        valB = ((Operand)opB).getValue();
        if (operator.isBinary()) {
            Token opA = operands.pop();
            checkNotLeftParenthesis(opA);
            valA = ((Operand)opA).getValue();
        }

        return operator.eval(valB, valA);
    }

    private static void checkNotLeftParenthesis(Token token) {
        if (token != null && token.getType() == Token.Type.LEFT_PARENTHESIS) {
            throw new InvalidExpressionException("Expected argument. Got '(' instead.");
        }
    }
}
