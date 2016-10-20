package shunting.yard;

import shunting.yard.functions.AverageFunction;
import shunting.yard.functions.Function;
import shunting.yard.functions.MaxFunction;
import shunting.yard.functions.MinFunction;
import shunting.yard.misc.EvaluableToken;
import shunting.yard.operators.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Evaluator {

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
        try {
            Queue<EvaluableToken> tokens = PostfixConverter.convertToPostfixNotation(expression, operators, functions);
            return evaluate(tokens);
        } catch (IOException e) {
            throw new InvalidExpressionException(e);
        }
    }

    private static BigDecimal evaluate(Queue<EvaluableToken> tokens) {
        Stack<BigDecimal> operands = new Stack<>();
        ArrayList<BigDecimal> argsList = new ArrayList<>(4); // Reuse array
        while (tokens.size() > 0) {
            EvaluableToken token = tokens.remove();

            argsList.clear();
            getArguments(operands, argsList, token);
            Collections.reverse(argsList);

            BigDecimal result = token.eval(argsList);
            operands.push(result);
        }

        if (operands.size() != 1) {
            throw new InvalidExpressionException("The expression is invalid");
        }

        return operands.pop();
    }

    private static void getArguments(Stack<BigDecimal> operands, List<BigDecimal> output, EvaluableToken token) {
        if (operands.size() < token.getMinArgsCount()) {
            throw new InvalidExpressionException(
                    String.format("Token %s expects at least %d arguments. Available: %d.",
                            token.toString(), token.getMinArgsCount(), operands.size()));
        }

        int expectedMaxArgs = token.getMaxArgsCount();
        int givenArgs = 0;
        while (operands.size() > 0 && givenArgs < expectedMaxArgs) {
            output.add(operands.pop());
            givenArgs++;
        }
    }
}
