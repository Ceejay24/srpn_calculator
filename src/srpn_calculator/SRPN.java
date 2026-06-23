package srpn_calculator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SRPN {
	/** Define maximum stack size */
    private static final int MAX_STACK_SIZE = 21;

    /** Define minimum integer value */
    private static final int MIN_VALUE = Integer.MIN_VALUE;

    /** Define maximum integer value */
    private static final int MAX_VALUE = Integer.MAX_VALUE;

    /**
     * An array of fixed preprogrammed pseudo-random numbers.
     * These values is used to replicate the output of 
     * the old srpn program's 'r' command,
     * which cycles through exactly 10 values.
     */
    private static final int[] RANDOM_NUMBERS = {
        44245652, 903121759, 235758559, 501379754, 982102748,
        915653778, 734522384, 55873735, 303150701, 325696894
    };

    /** Define stack used throughout the calculator */
    private final List<Integer> stack;

    /** Current index into the RANDOM_NUMBERS array, 
     * wraps around at array length. 
     */
    private int randomIndex;

    /** Checks whether it is in a block of comment */
    private boolean inComment;

    /** Tracks if there is a stack overflow */
    private boolean stackOverflow;

    /**
     * Constructs a new SRPN calculator instance with an empty stack.
     */
    public SRPN() {
        this.stack = new ArrayList<>();
        this.randomIndex = 0;
        this.inComment = false;
        this.stackOverflow = false;
    }

    /**
     * Processes a single line of input from the user.
     * We first split into individual numbers, operators, and commands,
     * then each command will be processed sequentially against the stack.
     *
     * @param s the input line which is a string to process
     */
    public void processCommand(String s) {
        List<String> commands = splitIntoCommands(s);

        for (String command : commands) {
            processCommands(command);
        }
    }

    /**
     * Splits an input string into individual commands, numbers, and operators.
     * Also handles comments and ensures that inline expressions, multi-digit 
     * numbers, negative numbers, operators, and other commands are correctly parsed.
     *
     * @param input the raw input string
     * @return a list of individual commands to process
     */
    private List<String> splitIntoCommands(String input) {
        List<String> commands = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // Handle comments when we encounter a '#' character
            if (c == '#') {
                inComment = !inComment; // Toggle comment state
                i++; // Move past the '#' character
                continue;
            }

            // If we are currently in a comment, skip all characters until we exit the comment
            if (inComment) {
                i++;
                continue;
            }

            // Ensures we skip whitespace characters (eg. spaces, tabs, etc.)
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // We can build multi-digit number tokens
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    num.append(input.charAt(i));
                    i++;
                }
                commands.add(num.toString());
                continue;
            }

            // We handle minus sign by treating as negative of a number when followed by a digit
            if (c == '-') {
                if (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                    StringBuilder values = new StringBuilder();
                    values.append('-');
                    i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        values.append(input.charAt(i));
                        i++;
                    }
                    commands.add(values.toString());
                    continue;
                }
                // Minus not followed by digit is treated as subtraction operator
                commands.add("-");
                i++;
                continue;
            }

            // Recognise single-character operators and commands
            if (isOperatorChar(c) || isCommandChar(c)) {
                commands.add(String.valueOf(c));
                i++;
                continue;
            }

            // Any other character is unrecognised; pass it through for error handling
            commands.add(String.valueOf(c));
            i++;
        }
        return commands;
    }

    /**
     * Checks whether a character is one of the supported arithmetic operators.
     *
     * @param c the character to check
     * @return true if the character is +, -, *, /, %, or ^
     */
    private boolean isOperatorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    /**
     * Checks whether a character is supported.
     *
     * @param c the character to check from the user input 
     * @return true if the character is =, p, f, r, or c
     */
    private boolean isCommandChar(char c) {
        return c == '=' || c == 'p' || c == 'f' || c == 'r' || c == 'c';
    }

    /**
     * This is where we process an individual command, operator, or number entered by user,
     * assuming the input has been validated.
     *
     * @param command the individual command to process
     */
    private void processCommands(String command) {
        // Implementation of command processing logic goes here

        // Handle single-character tokens (operators, commands, or single digits)
        if (command.length() == 1) {
            char c = command.charAt(0);

            if (isOperatorChar(c)) {
                performOperation(c);
                return;
            }

            if (c == '=') {
                printTop();
                return;
            }

            if (c == 'p') {
                printStack();
                return;
            }

            if (c == 'f') {
                reverseStack();
                return;
            }

            if (c == 'r') {
                pushRandom();
                return;
            }

            if (c == 'c') {
                stack.clear();
                return;
            }

            // Single digit numbers
            if (Character.isDigit(c)) {
                pushNumber(Character.getNumericValue(c));
                return;
            }

            // Unrecognised single character
            System.out.println("The input " + command + " is unrecognised.");
            return;
        }

        // Multi-character tokens are parsed as numbers
        // We will use a long to handle values which exceed int range before saturating is applied
        try {
            long value = Long.parseLong(command);
            int saturated = saturate(value);
            pushNumber(saturated);
        } catch (NumberFormatException e) {
            System.out.println("The input " + command + " is unrecognised.");
        }
    }

    /**
     * Pushes a number onto the stack.
     * If the stack is full, print "Stack full." to match
     * the srpn program's error message.
     *
     * @param value the integer value to push
     */
    private void pushNumber(int value) {
        if (stack.size() >= MAX_STACK_SIZE) {
            System.out.println("Stack full.");
            return;
        }
        stack.add(value);
    }

    /**
     * Prints the value at the top of the stack without removing it.
     * If the stack is empty, prints "Empty stack." to match srpn program's behaviour. 
     * 
     * <Strong>This is triggered by the '=' command.</Strong>
     */
    private void printTop() {
        if (stack.isEmpty()) {
            System.out.println("Empty stack.");
            return;
        }
        System.out.println(stack.get(stack.size() - 1));
    }

    /**
     * <p>Prints all values in the stack from bottom to top.</p>
     * <p>If the stack is empty, produce no output to match srpn program's behavior.</p>
     * <p>
     * If the stack has overflowed, prints the stack then terminates to replicate 
     * the srpn segmentation fault behaviour.
     * </p>
     * <p>
     * <Strong >This is triggered by the 'p' command.</Strong>
     * </p>
     */
    private void printStack() {
        if (stackOverflow) {
            for (int val : stack) {
                System.out.println(val);
            }
            System.exit(1);
        }
        for (int val : stack) {
            System.out.println(val);
        }
    }

    /**
     * Reverses the order of all elements in the stack.
     * Produces no output. This is triggered by the 'f' command and
     * matches the srpn software behaviour where 'f' flips the stack.
     */
    private void reverseStack() {
        Collections.reverse(stack);
    }

    /**
     * <p>
     * Pushes the next value from the fixed pseudo-random sequence onto the stack.
     * The sequence index advances on each successful push. If the stack is full, 
     * prints "Stack full." once and marks the stack as corrupted; subsequent 
     * overflow attempts cause program to terminate.
     * </p>
     * <p>
     * <Strong> Note: </Strong>
     * This replicates srpn program's behaviour where the stack size counter 
     * gets corrupted after the first overflow and then leads to segmentation fault.
     * </p>
     */
    private void pushRandom() {
        if (stack.size() >= MAX_STACK_SIZE) {
            if (!stackOverflow) {
                System.out.println("Stack full.");
                stackOverflow = true;
            }
            return;
        }
        stack.add(RANDOM_NUMBERS[randomIndex % RANDOM_NUMBERS.length]);
        randomIndex++;
    }

    /**
     * <p>
     * Performs a binary arithmetic operation on the top two stack values.
     * Pops two operands (a and b, where b is the top), applies the operator,
     * and pushes the saturated result back onto the stack.
     * </p>
     * <p>
     * <Strong>Note:</Strong>
     * <ul>
     * <li>If the stack has fewer than two elements, prints "Not enough elements on stack."</li>
     * <li>For division and modulo, if the divisor is zero, prints "Cannot divide by 0." 
     * and restores both operands to the stack.</li>
     * <li>For power, if the exponent is negative, prints "Negative power." and restores both operands.</li>
     * </ul>
     *
     * @param op the supportedoperator character (+, -, *, /, %, or ^)
     */
    private void performOperation(char op) {
        if (stack.size() < 2) {
            System.out.println("Not enough elements on stack.");
            return;
        }

        // Pop operands: b is top of stack, a is second from top
        int b = stack.remove(stack.size() - 1);
        int a = stack.remove(stack.size() - 1);
        
        long result;
        switch (op) {
            case '+' -> result = (long) a + (long) b;
            case '-' -> result = (long) a - (long) b;
            case '*' -> result = (long) a * (long) b;
            case '/' -> {
                if (b == 0) {
                    System.out.println("Cannot divide by 0.");
                    stack.add(a);
                    stack.add(b);
                    return;
                }
                result = (long) a / (long) b;
            }
            case '%' -> {
                if (b == 0) {
                    System.out.println("Cannot divide by 0.");
                    stack.add(a);
                    stack.add(b);
                    return;
                }
                result = (long) a % (long) b;
            }
            case '^' -> {
                if (b < 0) {
                    long posResult = power(a, -b);
                    result = posResult == 0 ? 0 : 1 / posResult;
                } else {
                    result = power(a, b);
                }
            }
            default -> {
                // in case of Unknown operator, restore operands
                stack.add(a);
                stack.add(b);
                return;
            }
        }

        // Saturate the result to int range and push onto stack
        stack.add(saturate(result));
    }

    /**
     * Dedicated method to compute the exponential value of a base using iterative multiplication.
     *
     * @param base the base value
     * @param exp  the exponent, only supports non-negative values.
     * @return the result of the base raised to the power of the exponent, or 0 if the result is too small.
     */
    private long power(int base, int exp) {
        if (exp == 0) {
            return 1;
        }

        long result = 1;
        long b = base;

        for (int i = 0; i < exp; i++) {
            result *= b;
        }

        return result;
    }

    /**
     * Saturates a long value to stay within the int range.
     * Values exceeding Integer.MAX_VALUE are saturated to Integer.MAX_VALUE.
     * Values below Integer.MIN_VALUE are saturated to Integer.MIN_VALUE.
     *
     * @param value the long value to saturate
     * @return the saturated int value
     */
    private int saturate(long value) {
        if (value > MAX_VALUE) {
            return MAX_VALUE;
        }
        if (value < MIN_VALUE) {
            return MIN_VALUE;
        }
        return (int) value;
    }

}
