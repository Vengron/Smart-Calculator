package calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class CalcUtil {

    public static boolean checkParentheses(ArrayList<String> infix) {
        Deque<String> stack = new ArrayDeque<>();
        for (String s : infix) {
            if (s.equals("(")) {
                stack.push("(");
            } else if (s.equals(")")) {
                try {
                    stack.pop();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * Splits input to elements based on their function in expression
     * (numbers, operators, variables, parentheses)
     *
     * @param input expression inserted by user
     * @return separated elements of expression
     */
    public static ArrayList<String> split(String input) {
        char[] chars = input.toCharArray();
        ArrayList<String> infix = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length;) {
            if (String.valueOf(chars[i]).matches("\\w")) {
                sb.append(chars[i]);
                i++;
                while (i < chars.length && String.valueOf(chars[i]).matches("\\w")) {
                    sb.append(chars[i]);
                    i++;
                }
                infix.add(sb.toString());
                sb.setLength(0);
            } else if (String.valueOf(chars[i]).matches("\\d")) {
                sb.append(chars[i]);
                i++;
                while (i < chars.length && String.valueOf(chars[i]).matches("\\d")) {
                    sb.append(chars[i]);
                    i++;
                }
                infix.add(sb.toString());
                sb.setLength(0);
            } else if (String.valueOf(chars[i]).matches("\\+")) {
                i++;
                // for '+' and '-' is not necessary to check for length because this signs shouldn't be at the end
                while (String.valueOf(chars[i]).matches("\\+")) {
                    i++;
                }
                infix.add("+");
            } else if (String.valueOf(chars[i]).matches("-")) {
                sb.append(chars[i]);
                i++;
                while (String.valueOf(chars[i]).matches("-")) {
                    sb.append(chars[i]);
                    i++;
                }
                if (sb.length() % 2 == 0) {
                    infix.add("+");
                } else {
                    infix.add("-");
                }
                sb.setLength(0);
            } else if (!String.valueOf(chars[i]).matches("\\s+")) {
                infix.add(String.valueOf(chars[i]));
                i++;
            } else {
                i++;
            }
        }
        return infix;
    }

    /**
     * Converts expression in infix form to Polish notation
     * @param expression ArrayList with already separated elements of expression
     * @return ArrayList of elements in postfix notation order
     */
    public static ArrayList<String> convertToPostfix(ArrayList<String> expression) {
        Deque<String> stack = new ArrayDeque<>();
        ArrayList<String> result = new ArrayList<>();
        for (String symbol : expression) {
            if (isWord(symbol) || isNumber(symbol)) {
                result.add(symbol);
            } else {
                if (stack.isEmpty()) {
                    stack.push(symbol);
                } else if (stack.peek().equals("(") || symbol.equals("(")) {
                    stack.push(symbol);
                } else if (symbol.equals(")")) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        result.add(stack.pop());
                    }
                    stack.pop();
                } else {
                    switch (symbol) {
                        case "^":
                            stack.push(symbol);
                            break;
                        case "*":
                        case "/":
                            while (!stack.isEmpty() && !stack.peek().matches("[(+-]")) {
                                result.add(stack.pop());
                            }
                            stack.push(symbol);
                            break;
                        default:
                            while (!stack.isEmpty() && !stack.peek().equals("(")) {
                                result.add(stack.pop());
                            }
                            stack.push(symbol);
                            break;
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }

    public static boolean isWord(String input) {
        return input.matches("[a-zA-Z]+");
    }

    public static boolean isNumber(String input) {
        return input.matches("\\d+");
    }

    public static boolean isAssignment(String input) { return input.matches(".*=.*"); }

    public static boolean isExpression(String input) {
        String numOrVar = "(\\w+|\\d+)";
        String operator = "([*/^]|[+-]+)";
        String brackets = "[ ()]*";
        return input.matches(
                String.format("%s%s%s(%s%s%s%s)*",
                        brackets, numOrVar, brackets, operator, brackets, numOrVar, brackets));
    }
}
