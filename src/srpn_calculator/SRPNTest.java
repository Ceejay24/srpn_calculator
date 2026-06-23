package srpn_calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SRPNTest {

	private SRPN srpn;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        srpn = new SRPN();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    private String getOutput() {
        System.out.flush();
        return outputStream.toString().trim();
    }

    private void resetOutput() {
        outputStream.reset();
    }

    // ===================================================================
    // 1. Single Arithmetic Operations (Normal Data)
    // ===================================================================

    /**
     * Test 1.1: Addition
     * Purpose: Verify the calculator can add two numbers correctly.
     * Input: 10, 2, +, =
     * Expected Output: 12
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.1 Addition - Normal Data")
    void testAddition() {
        srpn.processCommand("10");
        srpn.processCommand("2");
        srpn.processCommand("+");
        srpn.processCommand("=");
        assertEquals("12", getOutput());
    }

    /**
     * Test 1.2: Subtraction
     * Purpose: Verify the calculator can subtract two numbers correctly.
     * Input: 11, 3, -, =
     * Expected Output: 8
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.2 Subtraction - Normal Data")
    void testSubtraction() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("-");
        srpn.processCommand("=");
        assertEquals("8", getOutput());
    }

    /**
     * Test 1.3: Multiplication
     * Purpose: Verify the calculator can multiply two numbers correctly.
     * Input: 9, 4, *, =
     * Expected Output: 36
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.3 Multiplication - Normal Data")
    void testMultiplication() {
        srpn.processCommand("9");
        srpn.processCommand("4");
        srpn.processCommand("*");
        srpn.processCommand("=");
        assertEquals("36", getOutput());
    }

    /**
     * Test 1.4: Division
     * Purpose: Verify the calculator can perform integer division correctly.
     * Input: 11, 3, /, =
     * Expected Output: 3
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.4 Division - Normal Data")
    void testDivision() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("/");
        srpn.processCommand("=");
        assertEquals("3", getOutput());
    }

    /**
     * Test 1.5: Modulo
     * Purpose: Verify the calculator can perform modulo operation correctly.
     * Input: 11, 3, %, =
     * Expected Output: 2
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.5 Modulo - Normal Data")
    void testModulo() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("%");
        srpn.processCommand("=");
        assertEquals("2", getOutput());
    }

    /**
     * Test 1.6: Power
     * Purpose: Verify the calculator can raise a number to a power.
     * Input: 2, 10, ^, =
     * Expected Output: 1024
     * Category: Normal Data
     */
    @Test
    @DisplayName("1.6 Power - Normal Data")
    void testPower() {
        srpn.processCommand("2");
        srpn.processCommand("10");
        srpn.processCommand("^");
        srpn.processCommand("=");
        assertEquals("1024", getOutput());
    }

    // ===================================================================
    // 2. Multiple Operations (Normal Data)
    // ===================================================================

    /**
     * Test 2.1: Compound Expression (3*3 + 4*4)
     * Purpose: Verify the calculator handles multiple numbers and operations in sequence.
     * Input: 3, 3, *, 4, 4, *, +, =
     * Expected Output: 25
     * Category: Normal Data
     */
    @Test
    @DisplayName("2.1 Compound expression 3*3 + 4*4 - Normal Data")
    void testCompoundExpression() {
        srpn.processCommand("3");
        srpn.processCommand("3");
        srpn.processCommand("*");
        srpn.processCommand("4");
        srpn.processCommand("4");
        srpn.processCommand("*");
        srpn.processCommand("+");
        srpn.processCommand("=");
        assertEquals("25", getOutput());
    }

    /**
     * Test 2.2: Multiple Values with Peek (p command)
     * Purpose: Verify 'p' prints the entire stack and '+' correctly accumulates values.
     * Input: 1234, 2345, 3456, p, +, p, +, p
     * Expected Output:
     *   1234\n2345\n3456
     *   1234\n5801
     *   7035
     * Category: Normal Data
     */
    @Test
    @DisplayName("2.2 Multiple values with p command - Normal Data")
    void testMultipleValuesWithPeek() {
        srpn.processCommand("1234");
        srpn.processCommand("2345");
        srpn.processCommand("3456");
        srpn.processCommand("p");
        String afterFirstP = getOutput();
        assertEquals("1234\r\n2345\r\n3456", afterFirstP.replace("\n", "\r\n").replace("\r\r\n", "\r\n"));

        resetOutput();
        srpn.processCommand("+");
        srpn.processCommand("p");
        String afterSecondP = getOutput();
        assertEquals("1234\r\n5801", afterSecondP.replace("\n", "\r\n").replace("\r\r\n", "\r\n"));

        resetOutput();
        srpn.processCommand("+");
        srpn.processCommand("p");
        String afterThirdP = getOutput();
        assertEquals("7035", afterThirdP.trim());
    }

    // ===================================================================
    // 3. Stack Commands (Normal Data)
    // ===================================================================

    /**
     * Test 3.1: Print Top (= command) with single value
     * Purpose: Verify '=' prints only the top value of the stack.
     * Input: 42, =
     * Expected Output: 42
     * Category: Normal Data
     */
    @Test
    @DisplayName("3.1 Print top with = command - Normal Data")
    void testPrintTop() {
        srpn.processCommand("42");
        srpn.processCommand("=");
        assertEquals("42", getOutput());
    }

    /**
     * Test 3.2: Print Stack (p command) after reverse
     * Purpose: Verify 'p' prints all stack values from bottom to top after 'f' reverses.
     * Input: 5, 6, 7, f, p
     * Expected Output: 7\n6\n5
     * Category: Normal Data
     */
    @Test
    @DisplayName("3.2 Print stack after reverse - Normal Data")
    void testPrintStackAfterReverse() {
        srpn.processCommand("5");
        srpn.processCommand("6");
        srpn.processCommand("7");
        srpn.processCommand("f");
        srpn.processCommand("p");
        String output = getOutput();
        String normalised = output.replace("\r\n", "\n").trim();
        assertEquals("7\n6\n5", normalised);
    }

    /**
     * Test 3.3: Clear Stack (c command)
     * Purpose: Verify 'c' clears all elements from the stack.
     * Input: 20, 6, c, =
     * Expected Output: Empty stack.
     * Category: Normal Data
     */
    @Test
    @DisplayName("3.3 Clear stack with c command - Normal Data")
    void testClearStack() {
        srpn.processCommand("20");
        srpn.processCommand("6");
        srpn.processCommand("c");
        srpn.processCommand("=");
        assertEquals("Empty stack.", getOutput());
    }

    /**
     * Test 3.4: Reverse Stack (f command)
     * Purpose: Verify 'f' reverses the order of stack elements.
     * Input: 1, 2, 3, f, =
     * Expected Output: 1 (top of reversed stack)
     * Category: Normal Data
     */
    @Test
    @DisplayName("3.4 Reverse stack with f command - Normal Data")
    void testReverseStack() {
        srpn.processCommand("1");
        srpn.processCommand("2");
        srpn.processCommand("3");
        srpn.processCommand("f");
        srpn.processCommand("=");
        assertEquals("1", getOutput());
    }

    /**
     * Test 3.5: Push Random (r command)
     * Purpose: Verify 'r' pushes the first pseudo-random number from the fixed sequence.
     * Input: r, =
     * Expected Output: 44245652
     * Category: Normal Data
     */
    @Test
    @DisplayName("3.5 Push random with r command - Normal Data")
    void testPushRandom() {
        srpn.processCommand("r");
        srpn.processCommand("=");
        assertEquals("44245652", getOutput());
    }

    // ===================================================================
    // 4. Saturation (Boundary Data)
    // ===================================================================

    /**
     * Test 4.1: Addition Overflow Saturation
     * Purpose: Verify addition saturates at Integer.MAX_VALUE (2147483647) instead of wrapping.
     * Input: 2147483647, 1, +, =
     * Expected Output: 2147483647
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.1 Addition overflow saturation - Boundary Data")
    void testAdditionOverflowSaturation() {
        srpn.processCommand("2147483647");
        srpn.processCommand("1");
        srpn.processCommand("+");
        srpn.processCommand("=");
        assertEquals("2147483647", getOutput());
    }

    /**
     * Test 4.2: Subtraction Underflow Saturation
     * Purpose: Verify subtraction saturates at Integer.MIN_VALUE (-2147483648) instead of wrapping.
     * Input: -2147483647, 1, -, =
     * Expected Output: -2147483648
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.2 Subtraction underflow saturation - Boundary Data")
    void testSubtractionUnderflowSaturation() {
        srpn.processCommand("-2147483647");
        srpn.processCommand("1");
        srpn.processCommand("-");
        srpn.processCommand("=");
        assertEquals("-2147483648", getOutput());
    }

    /**
     * Test 4.3: Multiplication Saturation
     * Purpose: Verify multiplication saturates at MAX_VALUE when result would overflow.
     * Input: 100000, 100000, *, =
     * Expected Output: 2147483647
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.3 Multiplication overflow saturation - Boundary Data")
    void testMultiplicationSaturation() {
        srpn.processCommand("100000");
        srpn.processCommand("100000");
        srpn.processCommand("*");
        srpn.processCommand("=");
        assertEquals("2147483647", getOutput());
    }

    /**
     * Test 4.4: Input at Exact MAX_VALUE
     * Purpose: Verify pushing Integer.MAX_VALUE directly works correctly.
     * Input: 2147483647, =
     * Expected Output: 2147483647
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.4 Input at exact MAX_VALUE - Boundary Data")
    void testInputMaxValue() {
        srpn.processCommand("2147483647");
        srpn.processCommand("=");
        assertEquals("2147483647", getOutput());
    }

    /**
     * Test 4.5: Input at Exact MIN_VALUE
     * Purpose: Verify pushing Integer.MIN_VALUE directly works correctly.
     * Input: -2147483648, =
     * Expected Output: -2147483648
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.5 Input at exact MIN_VALUE - Boundary Data")
    void testInputMinValue() {
        srpn.processCommand("-2147483648");
        srpn.processCommand("=");
        assertEquals("-2147483648", getOutput());
    }

    /**
     * Test 4.6: Multiplication by Zero After Overflow
     * Purpose: Verify multiplying a saturated value by zero produces zero.
     * Input: 100000, 100000, *, 0, *, =
     * Expected Output: 0
     * Category: Boundary Data
     */
    @Test
    @DisplayName("4.6 Multiply saturated value by zero - Boundary Data")
    void testMultiplySaturatedByZero() {
        srpn.processCommand("100000");
        srpn.processCommand("100000");
        srpn.processCommand("*");
        srpn.processCommand("0");
        srpn.processCommand("*");
        srpn.processCommand("=");
        assertEquals("0", getOutput());
    }

    // ===================================================================
    // 5. Comments (Normal Data)
    // ===================================================================

    /**
     * Test 5.1: Full Line Comment
     * Purpose: Verify a comment enclosed in # symbols is ignored entirely.
     * Input: # This is a comment #, then 1, 2, +, =
     * Expected Output: 3
     * Category: Normal Data
     */
    @Test
    @DisplayName("5.1 Full line comment ignored - Normal Data")
    void testFullLineComment() {
        srpn.processCommand("# This is a comment #");
        srpn.processCommand("1");
        srpn.processCommand("2");
        srpn.processCommand("+");
        srpn.processCommand("=");
        assertEquals("3", getOutput());
    }

    /**
     * Test 5.2: Inline Comment
     * Purpose: Verify inline comments are stripped and remaining tokens are processed.
     * Input: "1 2 + # And so is this #", then p
     * Expected Output: 3
     * Category: Normal Data
     */
    @Test
    @DisplayName("5.2 Inline comment stripped - Normal Data")
    void testInlineComment() {
        srpn.processCommand("1 2 + # And so is this #");
        srpn.processCommand("p");
        assertEquals("3", getOutput());
    }

    // ===================================================================
    // 6. Inline Expressions (Normal Data)
    // ===================================================================

    /**
     * Test 6.1: Inline Addition with Print
     * Purpose: Verify that expressions written inline without spaces are parsed and executed.
     * Input: 11+1+1+p
     * Expected Output: "Not enough elements on stack." (first + has only one operand)
     *                   then subsequent operations produce 13 (11+1=12, 12+1=13)
     * Category: Normal Data
     */
    @Test
    @DisplayName("6.1 Inline expression 11+1+1+p - Normal Data")
    void testInlineExpression() {
        srpn.processCommand("11+1+1+p");
        String output = getOutput().replace("\r\n", "\n").trim();
        assertEquals("Not enough elements on stack.\n13", output);
    }

    // ===================================================================
    // 7. Error Handling (Error Data)
    // ===================================================================

    /**
     * Test 7.1: Empty Stack on Print Top
     * Purpose: Verify '=' on an empty stack produces the correct error message.
     * Input: =
     * Expected Output: Empty stack.
     * Category: Error Data
     */
    @Test
    @DisplayName("7.1 Empty stack error on = - Error Data")
    void testEmptyStackError() {
        srpn.processCommand("=");
        assertEquals("Empty stack.", getOutput());
    }

    /**
     * Test 7.2: Not Enough Operands for Operation
     * Purpose: Verify an operation with insufficient operands produces the correct error.
     * Input: 1, +
     * Expected Output: Not enough elements on stack.
     * Category: Error Data
     */
    @Test
    @DisplayName("7.2 Not enough operands error - Error Data")
    void testNotEnoughOperands() {
        srpn.processCommand("1");
        srpn.processCommand("+");
        assertEquals("Not enough elements on stack.", getOutput());
    }

    /**
     * Test 7.3: Division by Zero
     * Purpose: Verify division by zero produces the correct error message.
     * Input: 10, 0, /
     * Expected Output: Cannot divide by 0.
     * Category: Error Data
     */
    @Test
    @DisplayName("7.3 Division by zero error - Error Data")
    void testDivisionByZero() {
        srpn.processCommand("10");
        srpn.processCommand("0");
        srpn.processCommand("/");
        assertEquals("Cannot divide by 0.", getOutput());
    }

    /**
     * Test 7.4: Modulo by Zero
     * Purpose: Verify modulo by zero produces the correct error message.
     * Input: 10, 0, %
     * Expected Output: Cannot divide by 0.
     * Category: Error Data
     */
    @Test
    @DisplayName("7.4 Modulo by zero error - Error Data")
    void testModuloByZero() {
        srpn.processCommand("10");
        srpn.processCommand("0");
        srpn.processCommand("%");
        assertEquals("Cannot divide by 0.", getOutput());
    }

    /**
     * Test 7.5: Stack Full
     * Purpose: Verify pushing more than 21 elements produces the correct error.
     * Input: Push 22 numbers (1 pushed 22 times)
     * Expected Output: Stack full. (on the 22nd push)
     * Category: Error Data
     */
    @Test
    @DisplayName("7.5 Stack full error - Error Data")
    void testStackFull() {
        for (int i = 0; i < 21; i++) {
            srpn.processCommand("1");
        }
        resetOutput();
        srpn.processCommand("1");
        assertEquals("Stack full.", getOutput());
    }

    /**
     * Test 7.6: Unrecognised Input
     * Purpose: Verify an unrecognised character produces the correct error message.
     * Input: a
     * Expected Output: The input a is unrecognised.
     * Category: Error Data
     */
    @Test
    @DisplayName("7.6 Unrecognised input error - Error Data")
    void testUnrecognisedInput() {
        srpn.processCommand("a");
        assertEquals("The input a is unrecognised.", getOutput());
    }

    /**
     * Test 7.7: Division by Zero Preserves Stack
     * Purpose: Verify that after a divide-by-zero error, both operands remain on the stack.
     * Input: 10, 0, /, =
     * Expected Output: "Cannot divide by 0." then "0"
     * Category: Error Data
     */
    @Test
    @DisplayName("7.7 Division by zero preserves stack - Error Data")
    void testDivisionByZeroPreservesStack() {
        srpn.processCommand("10");
        srpn.processCommand("0");
        srpn.processCommand("/");
        resetOutput();
        srpn.processCommand("=");
        assertEquals("0", getOutput());
    }

    // ===================================================================
    // 8. Random Number Sequence (Normal Data)
    // ===================================================================

    /**
     * Test 8.1: Multiple Random Pushes
     * Purpose: Verify 'r' cycles through the fixed pseudo-random sequence correctly.
     * Input: r, r, r, =
     * Expected Output: 235758559 (third random number is on top)
     * Category: Normal Data
     */
    @Test
    @DisplayName("8.1 Multiple random pushes - Normal Data")
    void testMultipleRandomPushes() {
        srpn.processCommand("r");
        srpn.processCommand("r");
        srpn.processCommand("r");
        srpn.processCommand("=");
        assertEquals("235758559", getOutput());
    }

    // ===================================================================
    // 9. Negative Numbers (Normal Data)
    // ===================================================================

    /**
     * Test 9.1: Push Negative Number
     * Purpose: Verify negative numbers are correctly parsed and pushed onto the stack.
     * Input: -5, =
     * Expected Output: -5
     * Category: Normal Data
     */
    @Test
    @DisplayName("9.1 Push negative number - Normal Data")
    void testPushNegativeNumber() {
        srpn.processCommand("-5");
        srpn.processCommand("=");
        assertEquals("-5", getOutput());
    }

    /**
     * Test 9.2: Subtraction vs Negative Number
     * Purpose: Verify that minus sign is treated as subtraction between stacked values.
     * Input: 10, 3, -, =
     * Expected Output: 7
     * Category: Normal Data
     */
    @Test
    @DisplayName("9.2 Subtraction vs negative number - Normal Data")
    void testSubtractionVsNegative() {
        srpn.processCommand("10");
        srpn.processCommand("3");
        srpn.processCommand("-");
        srpn.processCommand("=");
        assertEquals("7", getOutput());
    }

    // ===================================================================
    // 10. Power Edge Cases (Boundary Data)
    // ===================================================================

    /**
     * Test 10.1: Power of Zero
     * Purpose: Verify any number raised to the power of zero equals 1.
     * Input: 5, 0, ^, =
     * Expected Output: 1
     * Category: Boundary Data
     */
    @Test
    @DisplayName("10.1 Power of zero - Boundary Data")
    void testPowerOfZero() {
        srpn.processCommand("5");
        srpn.processCommand("0");
        srpn.processCommand("^");
        srpn.processCommand("=");
        assertEquals("1", getOutput());
    }

    /**
     * Test 10.2: Zero to Any Power
     * Purpose: Verify zero raised to any positive power equals zero.
     * Input: 0, 5, ^, =
     * Expected Output: 0
     * Category: Boundary Data
     */
    @Test
    @DisplayName("10.2 Zero to any power - Boundary Data")
    void testZeroToAnyPower() {
        srpn.processCommand("0");
        srpn.processCommand("5");
        srpn.processCommand("^");
        srpn.processCommand("=");
        assertEquals("0", getOutput());
    }

    /**
     * Test 10.3: Power Saturation
     * Purpose: Verify that large powers saturate at MAX_VALUE.
     * Input: 2, 31, ^, =
     * Expected Output: 2147483647
     * Category: Boundary Data
     */
    @Test
    @DisplayName("10.3 Power saturation at MAX_VALUE - Boundary Data")
    void testPowerSaturation() {
        srpn.processCommand("2");
        srpn.processCommand("31");
        srpn.processCommand("^");
        srpn.processCommand("=");
        assertEquals("2147483647", getOutput());
    }

}
