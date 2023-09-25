import java.lang.Math;
import java.util.Random;

/**
* Simulates the arithmetic and logic unit (ALU) of a processor. Follows the
* ARMv8 architecture, with the exception of the number of bits used for input
* and output values. Uses the BINARY_LENGTH constant from the Binary class as
* the nubmer of bits for inputs and output.
*
* The ALU must be implemented using logic operations (AND, OR, NOT) on each
* set of input bits because the goal of this assignment is to learn about how
* a computer processor uses logic gates to perform arithmetic. The Java
* arithmetic operations should not be used in this class.
*
* @author Sten Leinasaar
*/
public class ALU {

    /** Number of bits used to represent an integer in this ALU */
    public static final int INT_LENGTH = 32;

    /** Input A: an INT_LENGTH bit binary value */
    private boolean[] inputA = new boolean[INT_LENGTH];

    /** Input B: an INT_LENGTH bit binary value */
    private boolean[] inputB = new boolean[INT_LENGTH];

    /** Output: an INT_LENGTH bit binary value */
    private boolean[] output = new boolean[INT_LENGTH];

    /** ALU Control input */
    private int control;

    /** Zero flag */
    private boolean zeroFlag;

    /** Carry flag */
    private boolean carryFlag;

    /** Overflow flag */
    private boolean overflowFlag;
    /** Random boolean generator*/
    private static final Random rand = new Random();

    /**
    * Constructor initializes inputs and output to random binary values,
    * intializes control to 15, initializes zero flag to false.
    * Inputs and output arrays should have length INT_LENGTH.
    */
    public ALU() {
        // PROGRAM 1: Student must complete this method
        control = 15;
        zeroFlag = false;
        overflowFlag = false;
        for(int i = 0; i < INT_LENGTH; i++) {
                inputA[i] = rand.nextBoolean();
                inputA[i] = rand.nextBoolean();
                output[i] = rand.nextBoolean();
        }	
    }

    /**
    * Sets the value of inputA.
    *
    * @param b The value to set inputA to
    *
    * @exception IllegalArgumentException if array b does not have length
    * INT_LENGTH
    */
    public void setInputA(boolean[] b) {
    	
    	if(b.length != INT_LENGTH) {
    		throw new IllegalArgumentException(" The length of an array is: " + b.length + " but required length is " + INT_LENGTH);
    	}
        // PROGRAM 1: Student must complete this method
    	for(int i =0; i< INT_LENGTH; i++) {
    		inputA[i] = b[i];
    	}
    	
    }

    /**
    * Sets the value of inputB.
    *
    * @param b The value to set inputB to
    *
    * @exception IllegalArgumentException if array b does not have length INT_LENGTH
    */
    public void setInputB(boolean[] b) {
        // PROGRAM 1: Student must complete this method
    	if(b.length != INT_LENGTH) {
    		throw new IllegalArgumentException(" The length of an array is: " + b.length + " but required length is " + INT_LENGTH);
    	}
    	for(int i =0; i< INT_LENGTH; i++) {
    		inputB[i] = b[i];
    	}
    }

    /**
    * Sets the value of the control line to one of the following values. Note
    * that we are not implementing all possible control line values.
    * 0 for AND;
    * 1 for OR;
    * 2 for ADD;
    * 6 for SUBTRACT;
    * 7 for PASS INPUT B.
    *
    * @param c The value to set the control to.
    * @exception IllegalArgumentException if c is not 0, 1, 2, 6, or 7.
    */
    public void setControl(int c) {
        // PROGRAM 1: Student must complete this method
    	if(c != 0 && c != 1 && c != 2 && c != 6 && c != 7) {
    		throw new IllegalArgumentException("Control has to be either 0, 1, 2, 6 or 7 but you wrote: " + c);
    	}
    	control = c;
    }

    /**
    * Returns a copy of the value in the output.
    *
    * @return The value in the output
    */
    public boolean[] getOutput() {
        // PROGRAM 1: Student must complete this method
        // return value is a placeholder, student should replace with correct return
    	boolean [] copyOfOutput = new boolean[INT_LENGTH];
    	for(int i =0; i< INT_LENGTH; i++) {
    		copyOfOutput[i] = output[i];
    	}
        return copyOfOutput;
    }

    /**
    * Returns the value of the zero data member. The zero data member should
    * have been set to true if the result of the operation was zero.
    *
    * @return The value of the zeroFlag data member
    */
    public boolean getZeroFlag() {
    	zeroFlag = true;
        for(int i = 0; i < INT_LENGTH; i++) {
        	if(output[i] == true) {
        		zeroFlag = false;
        	}
        }
        return zeroFlag;
    }

    /**
    * Returns the value of the carryFlag data member. The carryFlag data member
    * is set to true if the ALU addition operation has a carry out of the most
    * significant bit.
    *
    * @return The value of the carryFlag data member
    */
    public boolean getCarryFlag() {
        // PROGRAM 1: Student must complete this method
        // return value is a placeholder, student should replace with correct return
        return carryFlag;
    }

    /**
    * Returns the value of the overflowFlag data member. The overflowFlag data
    * member is set to true if the ALU addition operation has a result that
    * is overflow when the operands are signed integers.
    *
    * @return The value of the overflowFlag data member
    */
    public boolean getOverflowFlag() {
    	overflowFlag = false;
    	//if input a MSB and Input b MSB are the same, but output MSB is different, then overflow
    	if((inputA[INT_LENGTH -1] == inputB[INT_LENGTH -1]) && (output[INT_LENGTH -1] != inputA[INT_LENGTH -1])) {
    		overflowFlag = true;
    		}
        // PROGRAM 1: Student must complete this method
        // return value is a placeholder, student should replace with correct return
        return overflowFlag;
    }

    /**
    * Activates the ALU so that the ALU performs the operation specified by
    * the control data member on the two input values. When this method is
    * finished, the output data member contains the result of the operation.
    * 
    * 0 for AND;
    * 1 for OR;
    * 2 for ADD;
    * 6 for SUBTRACT;
    * 7 for PASS INPUT B.
    *
    * @exception RuntimeException if the control data member is not set to
    * a valid operation code.
    */
    public void activate() {
        // PROGRAM 1: Student must complete this method
    	switch(control) {
    		case 0:
    			and();
    			break;
    		case 1:
    			or();
    			break;
    		case 2:
    			add();
    			break;
    		case 6:
    			sub();
    			break;
    		case 7:
    			passB();
    			break;
    	}
    }

    /**
    * Performs the bitwise AND operation: output = inputA AND inputB
    */
    private void and() {
        // PROGRAM 1: Student must complete this method
    	for(int i =0; i < INT_LENGTH; i++) {
    		if(inputA[i] && inputB[i]) {
        		output[i] = true;
        		//any other case it is false.
        	}else {
        		output[i] =false;
        	}
    	}
    	
    }

    /**
    * Performs the bitwise OR operation: output = inputA OR inputB
    */
    private void or() {
        // PROGRAM 1: Student must complete this method
    	for(int i =0; i < INT_LENGTH; i++) {
    		if(inputA[i] || inputB[i]) {
        		output[i] = true;
        		//any other case it is false.
        	}else {
        		output[i] =false;
        	}
    	}
    	
    }

    /**
    * Performs the addition operation using ripple-carry addition of each bit:
    * output = inputA + inputB
    */
    private void add() {
        // PROGRAM 1: Student must complete this method
        // This method must use the addBit method for bitwise addition.
    	//the two bit array being sent back has sum in [0] and carry flag[1] 
    	//output[i] = addBit(inputA[i], inputB[i], 0);
    	boolean carry = false;
    	boolean[] temp = new boolean[2];
    	for(int i = 0; i < INT_LENGTH; i++) {
    		temp = addBit(inputA[i], inputB[i], carry);
    		output[i] = temp[0];
    		carry = temp[1];
    	}
  		carryFlag = carry;
    	
    	
    		
//    	
    }

    /**
    * Performs the subtraction operation using a ripple-carry adder:
    * output = inputA - inputB
    * In order to perform subtraction, set the first carry-in to 1 and invert
    * the bits of inputB.
    */
    private void sub() {
        // PROGRAM 1: Student must complete this method
        // This method must use the addBit method for bitwise subtraction.
    	
    	//if inputB is leading 1, then simple negation would give that numbers positive representation in two complement, and I can add
    	if(inputB[INT_LENGTH-1] == true) {
    		inputB = Binary.simpleNeg(inputB);
    		add();
    	//else it is is leading 0, then simple negation will give that numbers negative representation. And I can call add
    	}else {
    		inputB = Binary.simpleNeg(inputB);
    		add();
    	}	
    }

    /**
    * Copies inputB to the output: output = inputB
    */
    private void passB() {
        // PROGRAM 1: Student must complete this method
    	for(int i = 0; i < INT_LENGTH; i++) {
    		output[i] = inputB[i];
    	}
    }

    /**
    * Simulates a 1-bit adder.
    *
    * @param a Represents an input bit
    * @param b Represents an input bit
    * @param c Represents the carry in bit
    * @return An array of length 2, index 0 holds the output bit and index 1
    * holds the carry out
    */
    private boolean[] addBit(boolean a, boolean b, boolean c) {
    	boolean[] addBits = new boolean[2];
//    	//for sum (A XOR B) XOR CARRY IN
//    	if((a^b)^c) {
//    		//sum is true aka 1
//    		addBits[0] = true;
//    	}else {
//    		addBits[0] = false;
//    	}
//    	//Carry out is (A and B) or (A XOR B) and carry in 
//    	if((a&&b) || ((a^b) && c)) {
//    		addBits[1] = true;
//    	}else {
//    		addBits[1] = false;
//    	}
//    	
    	boolean temp = logicalXOR(a,b);
    	if(logicalXOR(temp, c)) {
    		addBits[0] = true;
    	}
    	else {
    		addBits[0] = false;
      }
    	if((a&&b) || ((temp) && c)) {
    		addBits[1] = true;
    	}
    	else {
    		addBits[1] = false;
      }
        // return value is a placeholder, student should replace with correct return
        return addBits;
    }
    
    private static boolean logicalXOR(boolean a, boolean b) {
        return (a && !b) || (!a && b);
        		
    }

}
