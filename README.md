# Shunting-Yard Expression Evaluator
Java Expression Evaluator using the Shunting Yard algorithm

## Usage

```java
final String EXPRESSION = "5.5*(-3)+max(2,9,7)";
Evaluator evaluator = new Evaluator();

try {
    BigDecimal result = evaluator.eval(EXPRESSION);
    System.out.printf(result.toString());
} catch (InvalidExpressionException ex) {
    System.out.println(ex.getMessage());
}
```

## Custom Functions
Function constructor takes **function name**, **min arguments count** and **max arguments count** (-1 for variadic):
```java
final String EXPRESSION = "abs(-5)";
Evaluator evaluator = new Evaluator();
evaluator.addFunction(new Function("abs", 1, 1) {
    @Override
    public BigDecimal eval(List<BigDecimal> args) {
        return args.get(0).abs();
    }
});
```

## Custom Operators
Operator constructor takes **operator name**, **precedence** as integer value and **isBinary** boolean flag (set *false* for unary): 
```java
final String EXPRESSION = "7 % 2";
Evaluator evaluator = new Evaluator();
evaluator.addOperator(new Operator("%", 3, true) {
    @Override
    public BigDecimal eval(BigDecimal arg1, @Nullable BigDecimal arg2) {
        return arg1.remainder(arg2, MathContext.DECIMAL128);
    }
});
```
