package calculator;

import java.math.BigInteger;
import java.util.*;

public class Calculator {

    private HashMap<String, String> savedVariables;

    public Calculator() {
        savedVariables = new HashMap<>();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("/exit")) {
                System.out.println("Bye!");
                return;
            } else if (input.matches("/.+")) {
                System.out.println("Unknown command");
            } else if (CalcUtil.isAssignment(input)) {
                save(input);
            } else if (CalcUtil.isExpression(input)) {
                calculate(input);
            } else if (CalcUtil.isWord(input)) {
                printVariable(input);
            } else if (!input.isEmpty()) {
                System.out.println("Invalid expression");
            }
        }
    }

    private void printVariable(String input) {
        System.out.println(savedVariables.getOrDefault(input, "Unknown variable"));
    }

    private void save(String input) {
        String[] split = input.split("=");
        split[0] = split[0].trim();
        split[1] = split[1].trim();
        if (!CalcUtil.isWord(split[0])) {
            System.out.println("Invalid identifier");
        } else if (!CalcUtil.isNumber(split[1]) && !CalcUtil.isWord(split[1]) || split.length != 2) {
            System.out.println("Invalid assignment");
        } else if (CalcUtil.isNumber(split[1])) {
            savedVariables.put(split[0], determineValue(split[1]));
        } else if (CalcUtil.isWord(split[1])) {
            savedVariables.put(split[0], determineValue(split[1]));
        } else {
            System.out.println("Unknown variable");
        }
    }

    private void calculate(String input) {
        ArrayList<String> infix = CalcUtil.split(input);
        if (!CalcUtil.checkParentheses(infix)) {
            System.out.println("Invalid expression");
            return;
        }
        ArrayList<String> postfix = CalcUtil.convertToPostfix(infix);
        if (!areAllVariablesKnown(postfix)) {
            System.out.println("Unknown variable");
            return;
        }
        System.out.println(calcBigInteger(postfix));
    }

    private BigInteger calcBigInteger(ArrayList<String> postfix) {
        Deque<BigInteger> stack = new ArrayDeque<>();
        for (String s : postfix) {
            switch (s) {
                case "-":
                    stack.push(stack.pop().negate().add(stack.pop()));
                    break;
                case "+":
                    stack.push(stack.pop().add(stack.pop()));
                    break;
                case "*":
                    stack.push(stack.pop().multiply(stack.pop()));
                    break;
                case "/":
                    BigInteger divisor = stack.pop();
                    BigInteger dividend = stack.pop();
                    stack.push(dividend.divide(divisor));
                    break;
                case "^":
                    int exponent = Integer.parseInt(stack.pop().toString());
                    BigInteger number = stack.pop();
                    stack.push(number.pow(exponent));
                    break;
                default:
                    stack.push(new BigInteger(determineValue(s)));
                    break;
            }
        }
        return stack.pop();
    }

    private String determineValue(String value) {
        if (CalcUtil.isWord(value)) {
            return savedVariables.get(value);
        }
        return value;
    }

    private boolean areAllVariablesKnown(ArrayList<String> expression) {
        for (String variable : expression) {
            if (CalcUtil.isWord(variable) && !savedVariables.containsKey(variable)) {
                return false;
            }
        }
        return true;
    }

}

