package shunting.yard;

import java.math.BigDecimal;

public class Main {

    private static final String EXPRESSION = "55.5^(3)";//"sin(rad(((5+2)-4)^max(max(4, 3),2)-51))";

    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator();

        try {
            BigDecimal result = evaluator.eval(EXPRESSION);
            System.out.printf("%.2f", result);
        } catch (InvalidExpressionException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
