package shunting.yard;

public class Main {

    private static final String EXPRESSION =
            "sin(rad(((5+2)-4)^max(max(4, 3),2)-51))";
//            "sin(rad(((5+2)-4)^max(max(4, 3),2)+9))";
//                "sin(rad(30))";

    public static void main(String[] args) {
        try {
            double result = ExpressionEvaluator.evaluate(EXPRESSION);
            System.out.printf("%.2f", result);
        } catch (InvalidExpressionException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
