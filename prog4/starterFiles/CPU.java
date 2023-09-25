
/**
* Represents a simple CPU based on the ARMv8 datapath.
*
* CS318 Programming Assignment 4
* Name:
*
*/
import java.io.*;
import java.util.Arrays;

import javax.naming.ldap.HasControls;

public class CPU {

	/** Memory unit for instructions */
	private Memory instructionMemory;

	/** Memory unit for data */
	private Memory dataMemory;

	/** Register unit */
	private Registers registers;

	/** Arithmetic and logic unit */
	private ALU alu;

	/** Adder for incrementing the program counter */
	private ALU adderPC;

	/** Adder for computing branches */
	private ALU adderBranch;

	/** Control unit */
	private SimpleControl control;

	/** Multiplexor output connects to Read Register 2 */
	private Multiplexor2 muxRegRead2;

	/** Mulitplexor ouptut connects to ALU input B */
	private Multiplexor2 muxALUb;

	/** Multiplexor output connects to Register Write Data */
	private Multiplexor2 muxRegWriteData;

	/** Multiplexor output connects to Program Counter */
	private Multiplexor2 muxPC;

	/** Program counter */
	private boolean[] pc;

	/**
	 * STUDENT SHOULD NOT MODIFY THIS METHOD
	 *
	 * Constructor initializes all data members.
	 *
	 * @param iMemFile Path to the file with instruction memory contents.
	 * @param dMemFile Path to the file with data memory contents.
	 * @exception FileNotFoundException if a file cannot be opened.
	 */
	public CPU(String iMemFile, String dMemFile) throws FileNotFoundException {

		// Create objects for all data members
		instructionMemory = new Memory(iMemFile);
		dataMemory = new Memory(dMemFile);
		registers = new Registers();
		alu = new ALU();
		control = new SimpleControl();
		muxRegRead2 = new Multiplexor2(5);
		muxALUb = new Multiplexor2(32);
		muxRegWriteData = new Multiplexor2(32);
		muxPC = new Multiplexor2(32);

		// Activate adderPC with ADD operation, and inputB set to 4
		// Send adderPC output to muxPC input 0
		adderPC = new ALU();
		adderPC.setControl(2);
		boolean[] four = Binary.uDecToBin(4L, 32);
		adderPC.setInputB(four);

		// Initalize adderBranch with ADD operation
		adderBranch = new ALU();
		adderBranch.setControl(2);

		// initialize program counter to 0
		pc = new boolean[32];
		for (int i = 0; i < 32; i++) {
			pc[i] = false;
		}

	}

	/**
	 * STUDENT SHOULD NOT MODIFY THIS METHOD
	 *
	 * Runs the CPU in single cycle (non-pipelined) mode. Stops when a halt
	 * instruction is decoded.
	 *
	 * This method can be used with any (assembled) assembly language program.
	 */
	public void singleCycle() {

		int cycleCount = 0;

		// Start the first cycle.
		boolean[] instruction = fetch();
		boolean op = decode(instruction);

		// Loop until a halt instruction is decoded
		while (op) {
			execute();

			memoryAccess();

			writeBack();
			cycleCount++;

			// Start the next cycle
			instruction = fetch();

			op = decode(instruction);

			System.out.println("Cycle count " + cycleCount + " cycles.");
		}

		System.out.println("CPU halt after " + cycleCount + " cycles.");
	}

	/**
	 * STUDENT MAY MODIFY THIS METHOD
	 *
	 * Compares the fetched instruction with the expected instruction
	 *
	 * This method can be used with any (assembled) assembly language program.
	 */

	private void testInstrFetch(int cycleCount, boolean[] instrFetched, boolean[] expected) {
		if (!Arrays.equals(instrFetched, expected)) {
			System.out.println("FAIL: cycle " + cycleCount + " did not fetch correct instruction:");
			System.out.println("------ fetch returned: " + Binary.toString(instrFetched));
			System.out.println("------ correct instruction: " + Binary.toString(expected));
		} else {
			System.out.println("PASS: Fetch works as intended for cycle: " + cycleCount);
		}

	}

	/**
	 * STUDENT MUST MODIFY THIS METHOD
	 *
	 * Verifies the controls for various instructions
	 *
	 * This method can be used with any (assembled) assembly language program.
	 */

	private void testDecodeControls(int cycleCount, SimpleControl control, boolean[] expectedControls, int aluControl) {
		/**
		 * / *** PROG 4. STUDENT MUST ADD // implement the method testDecodeControls //
		 * example below must test that when cycleCount is 0, the control signals // are
		 * correctly set for an ADD instruction
		 * 
		 * //controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
		 * Branch, Uncondbranch //ALU control is 2 for ADD testDecodeControls(0,
		 * control, expectedControls, 2);
		 */
		boolean[] controlsActual = { control.Reg2Loc, control.ALUSrc, control.MemtoReg, control.RegWrite,
				control.MemRead, control.MemWrite, control.Branch, control.Uncondbranch };

		if (!(Arrays.equals(controlsActual, expectedControls))) {

			System.out.println("FAIL: cycle " + cycleCount + " did not decode controls correctly.:");
			System.out.println("------  expected controls: " + Binary.toString(expectedControls));
			System.out.println("------ controls actually were: " + Binary.toString(controlsActual));
		} else {
			System.out.println("PASS: cycle " + cycleCount + " decoded controls correctly.:");
		}

	}

	/**
	 * STUDENT MUST MODIFY THIS METHOD
	 *
	 * Verifies the ALU output for various instructions
	 *
	 * This method can be used with any (assembled) assembly language program.
	 */

	private void testExecuteALUOutput(int cycleCount, boolean[] aluOutput, boolean[] correctALU) {

		if (!(Arrays.equals(aluOutput, correctALU))) {

			System.out.println("FAIL: cycle " + cycleCount + " did not execute properly. Output is wrong.");
			System.out.println("------  expected output fromALU: " + Binary.toString(correctALU));
			System.out.println("------ output actually was: " + Binary.toString(aluOutput));
		} else {
			System.out.println("PASS: cycle " + cycleCount + " executed properly.");
		}

	}

	/**
	 * STUDENT MUST ADD MORE TESTING CODE TO THIS METHOD AS INDICATED BY COMMENTS
	 * WIHTIN THIS METHOD.
	 *
	 * DO NOT CHANGE the calls to the CPU private methods.
	 *
	 *
	 * Runs the CPU in single cycle (non-pipelined) mode. Stops when a halt
	 * instruction is decoded.
	 *
	 * This method should only be used with the assembled testProg3.s because this
	 * method verifies correct values based on that specific program.
	 */
	public void runTestProg3() throws FileNotFoundException {

		int cycleCount = 0;

		// Start the first cycle.
		boolean[] instruction = fetch();

		// Example Test: Verify that when cycleCount is 0 the insruction returned by
		// fetch is the
		// binary version of the first instruction from testProg3.s ADD R9,R31,R31
		boolean[] firstInstr = { true, false, false, true, false, true, true, true, true, true, false, false, false,
				false, false, false, true, true, true, true, true, false, false, false, true, true, false, true, false,
				false, false, true };

		testInstrFetch(cycleCount, instruction, firstInstr);

		// *** PROG 4. STUDENT MUST ADD
		// implement the method testDecodeControls
		// example below must test that when cycleCount is 0, the control signals
		// are correctly set for an ADD instruction
		boolean op = decode(instruction);
		// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
		// Branch, Uncondbranch
		boolean[] expectedControls = { false, false, false, true, false, false, false, false };
		// ALU control is 2 for ADD
		testDecodeControls(cycleCount, control, expectedControls, 2);

		// Loop until a halt instruction is decoded
		while (op) {

			// *** PROG. 4 STUDENT MUST ADD
			// test code to show that they used to verify that decode worked
			// for e.g., for cycleCount 3, register 1 content must be 6
			// for cycleCount 7, register 2 must be 11
			// for cycleCount 0, muxRegRead2(false) must contain 31
			// for cycleCount 7, muxRegRead2(true) must contain 5
			// Student must create and call a test method to verify
			// - register file inputs and register reads
			// - muxReadReg2() output,

			// ################################### ALU OUTPUT TESTING
			// ############################################
			// TODO need to write expectedOutpUts.
			execute();

			// ** PROG. 4 STUDENT MUST ADD
			// test code to show that they used to verify execute worked
			// for e.g., for cycleCount 1, alu output must be 16 after execute
			//output0
			if (cycleCount == 0) {
				boolean[] expectedOutput0 = { false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false };
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput0);

			}
			//output 16
			if (cycleCount == 1) {
				boolean[] expectedOutput1 = {false, false, false, false, true, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput1);
			}
			//output has to be 32
			if (cycleCount == 2) {
				boolean[] expectedOutput2 = {false, false, false, false, false, true, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput2);
			}
			//output for sub has to be 1
			if (cycleCount == 3) {
				boolean[] expectedOutput3 = {true, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput3);
			}
			//output of 1 for CBZ, same as before
			if (cycleCount == 4) {
				boolean[] expectedOutput4 = {true, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput4);
			}
			//output 11 for add
			if (cycleCount == 5) {
				boolean[] expectedOutput5 = {true, true, false, true, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput5);
			}
			//Output 6 is B it doesn't mean for it.
			//Output 7 
			if (cycleCount == 7) {
				boolean[] expectedOutput7 = {false, false, false, true, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false};
				testExecuteALUOutput(cycleCount, alu.getOutput(), expectedOutput7);
			}
			//Cycle 8 is a halt for testProg3.

			memoryAccess();

		
		
			writeBack();

			

			cycleCount++;

			// Start the next cycle
			instruction = fetch();

			// ############################################# FETCH TESTING  ################################################

			// binary version of the second instruction from testProg3.s LDR R10,[R9, #16]
			if (cycleCount == 1) {
				boolean[] secondInstr = { false, true, false, true, false, true, false, false, true, false, false,
						false, false, false, false, false, true, false, false, false, false, false, true, false, false,
						false, false, true, true, true, true, true };
				testInstrFetch(cycleCount, instruction, secondInstr);

			}
			// binary version of the third instruction from testProg3.s LDR R11,[R9, #32]
			if (cycleCount == 2) {
				boolean[] thirdInstr = { true, true, false, true, false, true, false, false, true, false, false, false,
						false, false, false, false, false, true, false, false, false, false, true, false, false, false,
						false, true, true, true, true, true };
				testInstrFetch(cycleCount, instruction, thirdInstr);
			}
			// binary version of the fourth instruction from testProg3.s SUB R12,R10, R,11
			if (cycleCount == 3) {
				boolean[] fourthInstr = { false, false, true, true, false, false, true, false, true, false, false,
						false, false, false, false, false, true, true, false, true, false, false, false, false, true,
						true, false, true, false, false, true, true };
				testInstrFetch(cycleCount, instruction, fourthInstr);
			}
			// Binary version of the fifth instruction from testProg3.s CBZ R12, if
			if (cycleCount == 4) {
				boolean[] fifthInstr = { false, false, true, true, false, false, false, true, true, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, true, false, true, true, false, true };
				testInstrFetch(cycleCount, instruction, fifthInstr);
			}
			// Binary version of the sixth instruction from testProg3.s ADD R5, R10, R11
			if (cycleCount == 5) {
				boolean[] sixthInstr = { true, false, true, false, false, false, true, false, true, false, false, false,
						false, false, false, false, true, true, false, true, false, false, false, false, true, true,
						false, true, false, false, false, true };
				testInstrFetch(cycleCount, instruction, sixthInstr);
			}
			// Binary version of the seventh instruction from testProg3.s B, afterif
			if (cycleCount == 6) {
				boolean[] seventhInstr = { false, false, false, true, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, true, false, true, false, false, false };
				testInstrFetch(cycleCount, instruction, seventhInstr);

			}
			// Binary version of the eigth instruction from testProg3.s STR, R5, [R9, #8]
			if (cycleCount == 7) {
				boolean[] eigthInstr = { true, false, true, false, false, true, false, false, true, false, false, false,
						false, false, false, true, false, false, false, false, false, false, false, false, false, false,
						false, true, true, true, true, true };
				testInstrFetch(cycleCount, instruction, eigthInstr);

			}
			// Binary version of the ninth instruction from testProg3.s HLT
			if (cycleCount == 8) {
				boolean[] ninthInstr = { false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, true, false, false,
						false, true, false, true, false, true, true };
				testInstrFetch(cycleCount, instruction, ninthInstr);
			}

			op = decode(instruction);
			
			//############################   Testing DECODE ######################################

			if (cycleCount == 1) {
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				// LDR R10,[R9,#16]

				boolean[] expectedControls2 = { false, true, true, true, true, false, false, false };

				testDecodeControls(cycleCount, control, expectedControls2, 2);
			}
			if (cycleCount == 2) {
				// LDR R11,[R9,#32]
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls3 = { false, true, true, true, true, false, false, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls3, 2);
			}
			if (cycleCount == 3) {
				// SUB R12,R10,R11
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls4 = { false, false, false, true, false, false, false, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls4, 6);
			}
			if (cycleCount == 4) {
				// CBZ R12,if
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls5 = { true, false, false, false, false, false, true, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls5, 7);
			}
			if (cycleCount == 5) {
				// ADD R5,R10,R11
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls6 = { false, false, false, true, false, false, false, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls6, 2);
			}
			if (cycleCount == 6) {
				// B afterif
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls7 = { false, false, false, true, false, false, false, true };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls7, 3);

			}
			if (cycleCount == 7) {
				// STR R5,[R9,#8]
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls8 = { true, true, false, false, false, true, false, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls8, 2);
			}

			if (cycleCount == 8) {
				// .end
				// controls in order: Reg2Loc, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite,
				boolean[] expectedControls9 = { false, false, false, true, false, false, false, false };
				// ALU control is 2 for ADD
				testDecodeControls(cycleCount, control, expectedControls9, 2);
			}


		}

		System.out.println("CPU halt after " + cycleCount + " cycles.");
		// Write memory contents to a file
		dataMemory.writeToFile("checkMem.txt");

	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * Instruction Fetch Step Fetch the instruction from the instruction memory
	 * starting at address pc. Activate the PC adder and place the adder's output
	 * into muxPC input 0.
	 *
	 * @return The instruction starting at address pc
	 */
	private boolean[] fetch() {
		
		adderPC.setInputA(pc);
		adderPC.activate();
		// Reads the instruction boolean[] from adress pc.
		boolean[] instructionToReturn = instructionMemory.read32(pc);

		// putting the result to the muxPC input 0.
		muxPC.setInput0(adderPC.getOutput());
		// Returning the instruction read.
		return instructionToReturn;
	}
	/**
	 * This method sets the R format controls that can then be overwritten if needed
	 * for different ALU controls.
	 */
	private void setRformatControl() {
		control.ALUControl = 0;
		control.ALUSrc = false;
		control.Branch = false;
		control.MemRead = false;
		control.MemtoReg = false;
		control.MemWrite = false;
		control.Reg2Loc = false;
		control.RegWrite = true;
		control.Uncondbranch = false;
	}
	/**
	 * This method sets the common control for LDR and STR.
	 */
	private void ldrAndStrCommon() {
		control.ALUSrc = true;
		control.Branch = false;
		control.Uncondbranch = false;
	}
	
	/**
	 * This method extends any boolean array to 32 bits boolean array.
	 * Cannot handle errors when bigger than 32 size array is passed in. 
	 * 
	 * @param list that is used as a base value
	 * 			has to be less than 32. 
	 * @return a boolean array of size 32. 
	 */
	private boolean[] extendSign(boolean [] list) {
		boolean [] toReturn = new boolean[32];
		int index = 0;
 		for( int i = 0; i< list.length; i++) {
			toReturn[i] = list[i];
			index++;
		}
		Arrays.fill(toReturn, index, 32, false);
		return toReturn;
	}

	/**nary.sDecToBin(Binary.binToSDec(immediateCBZ), 32)
	 * STUDENT MUST COMPLETE THIS METHOD
	 * 28
	 * Instruction Decode inst2 = Arrays.copyOfRange(instruction, 5, 10);and Register Read
	 *
	 * Decode the instruction. Sets the control lines and sends appropriate bits
	 * from the instruction to various inputs within the processor.
	 *
	 * Set the Read Register inputs so that the values to be read from the registers
	 * will be available in the next phase.
	 *
	 * @param instruction The 32-bit instruction to decode
	 * @return false if the opcode is HLT; true for any other opcode
	 */
	private boolean decode(boolean[] instruction) {
		/**
		 * SimpleControl structure
		 * 
		 * int ALUControl boolean ALUSrc boolean Branch boolean MemRead boolean MemtoReg
		 * boolean MemWrite boolean Reg2Loc boolean RegWrite boolean Uncondbranch
		 * 
		 * 
		 */
		adderBranch.setInputA(pc);
		
		boolean[] opCodeCBZ;
		boolean[] opCodeB;
		boolean[] opCodeOther;
		boolean[] immediateCBZ;
		boolean[] immediateB;
		boolean[] immediateLDR;
		boolean[] immediateSTR;
		boolean[] inst;
		boolean[] inst2;
		boolean[] inst3;
		
		//################  Copying the bits  ################
		
		//instruction 1 is 0-4 bits
		inst = Arrays.copyOfRange(instruction, 0, 5);
		//Instruction 2 is 5 - 9 bits
		inst2 = Arrays.copyOfRange(instruction, 5, 10);
		//instruction 3 is 16 -20 bits
		inst3 = Arrays.copyOfRange(instruction, 16, 21);
		//opCodeBZ 31-24
		opCodeCBZ = Arrays.copyOfRange(instruction, 24, 32);
		//opCodeB 26 - 31
		opCodeB = Arrays.copyOfRange(instruction, 26, 32);
		//opCodeOther 21-31
		opCodeOther = Arrays.copyOfRange(instruction, 21, 32);
		//immediateB 0 -24
		immediateB = Arrays.copyOfRange(instruction, 0, 25);
		//immediateCBZ 5 - 23
		immediateCBZ = Arrays.copyOfRange(instruction, 5, 24);
		//ldr immediate 12 -20
		immediateLDR = Arrays.copyOfRange(instruction,12  , 21);
		//STR immediate 12 -20
		immediateSTR = Arrays.copyOfRange(instruction,12  , 21);
		

		
		
		muxRegRead2.setInput0(inst3);
		muxRegRead2.setInput1(inst);
		registers.setRead1Reg(inst2);
		registers.setWriteRegNum(inst);
		
		// Send immediate bits to sign extend
		if (Arrays.equals(Opcode.B, opCodeB)) {
			setRformatControl();
			control.ALUControl = 7;
			control.Uncondbranch = true;

			muxALUb.setInput1(extendSign(immediateB));
			adderBranch.setInputB(extendSign(immediateB));
			// Then set control accordingly

			// send immediate bits to sign extend
		} else if (Arrays.equals(Opcode.CBZ, opCodeCBZ)) {
			// then set control accordingly
			setRformatControl();
			control.ALUControl = 7;
			control.Branch = true;
			control.Reg2Loc = true;
			control.RegWrite = false;
			// immediate is 5-23 bits
			// register 2 will be muxAlub input 0
			adderBranch.setInputB(extendSign(immediateCBZ));
			muxALUb.setInput1(extendSign(immediateCBZ));
			
		} else if (Arrays.equals(Opcode.STR, opCodeOther)) {
			control.ALUControl = 2;
			control.MemRead = false;
			control.MemtoReg = false;
			control.MemWrite = true;
			control.Reg2Loc = true;
			control.RegWrite = false;
			ldrAndStrCommon();
			//immediate bits in STR to refer to offset
			adderBranch.setInputB(extendSign(immediateSTR));
			muxALUb.setInput1(extendSign(immediateSTR));
		} else if (Arrays.equals(Opcode.LDR, opCodeOther)) {
			control.ALUControl = 2;
			control.MemRead = true;
			control.MemtoReg = true;
			control.MemWrite = false;
			control.Reg2Loc = false;
			control.RegWrite = true;
			ldrAndStrCommon();
			//Immediate bits in LDR to refer to offset.
			adderBranch.setInputB(extendSign(immediateLDR));
			muxALUb.setInput1(extendSign(immediateLDR));
		} else if (Arrays.equals(Opcode.HLT, opCodeOther)) {
			setRformatControl();
			return false;
		} else {
			setRformatControl();
			if (Arrays.equals(Opcode.ADD, opCodeOther)) {
				control.ALUControl = 2;
			}
			if (Arrays.equals(Opcode.SUB, opCodeOther)) {
				control.ALUControl = 6;
			}
			if (Arrays.equals(Opcode.AND, opCodeOther)) {
				control.ALUControl = 0;
			}
			if (Arrays.equals(Opcode.ORR, opCodeOther)) {
				control.ALUControl = 1;
			}
		}
		// Read register 2 is dependent on controls.
		registers.setRead2Reg(muxRegRead2.output(control.Reg2Loc));
		registers.setRead1Reg(inst2);
		muxALUb.setInput0(registers.getReadReg2());
		alu.setInputA(registers.getReadReg1());
		alu.setInputB(muxALUb.output(control.ALUSrc));
		
		
		return true;
	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * Execute Phase Activate the ALU to execute an arithmetic or logic operation,
	 * or to calculate a memory address.
	 *
	 * The branch adder is activated during this phase, and the branch adder result
	 * is placed into muxPC input 1.
	 *
	 * This method must make decisions based on the values of the control lines.
	 * This method has no information about the opcode!
	 *
	 */
	private void execute() {

		// What is meant by calculate memory address. Which control check for it?

		adderBranch.activate();

		muxPC.setInput1(adderBranch.getOutput());
		alu.setControl(control.ALUControl);
		alu.activate();
		
		

		
	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * Memory Access Phase Read or write from/to data memory.
	 *
	 * This method must make decisions based on the values of the control lines.
	 * This method has no information about the opcode!
	 */
	private void memoryAccess() {
		//dataMemory.printMemory();
		// if memWrite is 1 then we write to memory.
		
		if (control.MemWrite) {
			dataMemory.write32(alu.getOutput(), registers.getReadReg2());
		}
		
		// if memRead is 1 then we read memory. Etc.
		if (control.MemRead) {
			muxRegWriteData.setInput1(dataMemory.read32(alu.getOutput()));
		}
		

	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * Write Back Phase Perform writes to registers: the PC and the processor
	 * registers.
	 *
	 * This method must make decisions based on the values of the control lines.
	 * This method has no information about the opcode!
	 */
	private void writeBack() {
		
		pc = muxPC.output((control.Branch && alu.getZeroFlag()) || control.Uncondbranch);

		
		muxRegWriteData.setInput0(alu.getOutput());

		registers.setWriteRegData(muxRegWriteData.output(control.MemtoReg));
		// if regwrite is true.
		if (control.RegWrite) {
			registers.activateWrite();
		}

	}
}
