package shunting.yard;

import java.util.Stack;

class TokenProcessor {

    static double processFunction(String function, Stack<Double> operands) {
        double result = 0;
        switch (function) {
            case "min":
                result = Math.min(operands.pop(), operands.pop());
                break;
            case "max":
                result = Math.max(operands.pop(), operands.pop());
                break;
            case "sin":
                result = Math.sin(operands.pop());
                break;
            case "deg":
                result = Math.toDegrees(operands.pop());
                break;
            case "rad":
                result = Math.toRadians(operands.pop());
                break;
            case "sqrt":
                result = Math.sqrt(operands.pop());
                break;
            default:
                throw new InvalidExpressionException("Function not supported");
        }

        return result;
    }

    static double processOperation(String operator, double opA, double opB) {
        double result = 0;
        switch (operator) {
            case "+":
                result = opA + opB;
                break;
            case "-":
                result = opB - opA;
                break;
            case "/":
                result = opB / opA;
                break;
            case "*":
                result = opA * opB;
                break;
            case "^":
                result = Math.pow(opB, opA);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported operator '" + operator + "'");
        }

        return result;
    }
}
