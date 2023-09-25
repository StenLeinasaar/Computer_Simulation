
/**
* Assembler for the CS318 simple computer simulation
* 
* Original starting code: Prof.Prasad
* Completed by: Sten Leinasaar
*/
import java.io.*;
import java.util.Scanner;
import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;

public class Assembler {

	private static int bitsOnLine = 0;

	/**
	 * Assembles the code file. When this method is finished, the dataFile and
	 * codeFile contain the assembled data segment and code segment, respectively.
	 *
	 * @param inFile   The pathname to the assembly language file to be assembled.
	 * @param dataFile The pathname where the data segment file should be written.
	 * @param codeFile The pathname where the code segment file should be written.
	 */
	public static void assemble(String inFile, String dataFile, String codeFile)
			throws FileNotFoundException, IOException {

		// DO NOT MAKE ANY CHANGES TO THIS METHOD

		ArrayList<LabelOffset> labels = pass1(inFile, dataFile, codeFile);
		pass2(inFile, dataFile, codeFile, labels);
	}

	/**
	 * First pass of the assembler. Writes the number of bytes in the data segment
	 * and code segment to their respective output files. Returns a list of code
	 * segment labels and their relative offsets.
	 *
	 * @param inFile   The pathname of the file containing assembly language code.
	 * @param dataFile The pathname for the data segment binary file.
	 * @param codeFile The pathname for the code segment binary file.
	 * @return List of the code segment labels and relative offsets.
	 * @exception RuntimeException if the assembly code file does not have the
	 *                             correct format, or another error while processing
	 *                             the assembly code file.
	 */
	private static ArrayList<LabelOffset> pass1(String inFile, String dataFile, String codeFile)
			throws FileNotFoundException {
		// Creates an array of labels and their relative offset from main.
		// Offset for main from main is 0.
		ArrayList<LabelOffset> labelOffset = new ArrayList<LabelOffset>();
		// lineNumber to be used later when calculating offset.
		int lineNumber = 0;
		// Initilize a scanner for reading files.
		Scanner scn = new Scanner(new File(inFile));
		int dataBytes = 0;
		int codeBytes = 0;
		String readFromFile;

		try {
			FileWriter dataWriter = new FileWriter(new File(dataFile));
			FileWriter codeWriter = new FileWriter(new File(codeFile));
			// While there is a next token to be read, the while loop continues.
			while (scn.hasNext()) {
				readFromFile = scn.next();
				// When we read .data we have reached the data section.
				if (readFromFile.equalsIgnoreCase(".data")) {
					// while the token read is not equal to .text, we are in data section.
					while (!readFromFile.equalsIgnoreCase(".text")) {
						readFromFile = scn.next();
						// When reading .word, the next tokens separated by comma are going to be data
						// values.
						// This step happens as many times as there are .word data lines.
						if (readFromFile.equalsIgnoreCase(".word")) {

							readFromFile = scn.next();
							// After splitting at comma, we have an array of values in string format
							// when multiplying the length of the array, we get the value of dataBytes.
							String[] array = readFromFile.split(",");
							dataBytes += (array.length) * 4;
						}
					}
					// after while loop we are at .text
					// write the dataBytes to the file.
					dataWriter.write(String.valueOf(dataBytes));
					// add a new line for pass2.
					dataWriter.write("\n");

					if (readFromFile.equalsIgnoreCase(".text")) {
						// two read next commands to skip over ".global main" line in each assembly
						// code.
						readFromFile = scn.next();
						readFromFile = scn.next();
						// while we are not reading .end, we are in the code section.
						while (!readFromFile.equalsIgnoreCase(".end")) {
							readFromFile = scn.next();

//switch case for different supported code commands for assembly. Each command is 4 bytes.
							switch (readFromFile) {
							case "ADD":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;

							case ".end":
								codeBytes += 4;
								break;
							case "SUB":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "AND":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "ORR":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "LDR":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "STR":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "CBZ":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							case "B":
								codeBytes += 4;
								readFromFile = scn.next();
								lineNumber++;
								break;
							// default case will be fore labels only.
							default:
								// new object for the label and offset is created to be added to arrayList.
								LabelOffset obj = new LabelOffset();
								if (readFromFile.equalsIgnoreCase("main:")) {
									obj.label = "main";
									obj.offset = 0;
									labelOffset.add(obj);

								} else {
									String[] lmao = readFromFile.split(":");
									// hardcoded zero because label is always the first index of the array from
									// splitting.
									obj.label = lmao[0];
									obj.offset = lineNumber * 4;
									labelOffset.add(obj);

								}

								continue;
							}

						}
						codeWriter.write(String.valueOf(codeBytes));
						codeWriter.write("\n");
					}

					dataWriter.flush();
					codeWriter.flush();
					scn.close();
					dataWriter.close();
					codeWriter.close();
					break;

				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return labelOffset;

	}

	/**
	 * Method to print the boolean array of some code command into the codeFile.
	 * This method writes the value from boolean array into the specified file.
	 * Flushes the writer and closes it.
	 * 
	 * @param toPrint boolean array to be iterated through and written to file.
	 * @param file    File to be written into.
	 */
	public static void printToCodeFile(boolean[] toPrint, String file) {
		try {
			// int bitsOnLine = 0;
			FileWriter codeWriter = new FileWriter(new File(file), true);

			for (int j = 0; j < toPrint.length; j++) {

				if (bitsOnLine != 8) {
					codeWriter.write(String.valueOf(toPrint[j]));
					codeWriter.write(" ");
					bitsOnLine++;
				} else {
					bitsOnLine = 1;
					codeWriter.write("\n");
					codeWriter.write(String.valueOf(toPrint[j]));
					codeWriter.write(" ");
				}

			}

			if (bitsOnLine == 8) {
				codeWriter.write("\n");
				bitsOnLine = 0;
			}
			codeWriter.flush();
			codeWriter.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Method to find the offset of a specified label from a command.
	 * 
	 * @param offFromMain the offset of a CBZ or B command from the main
	 * @param labels      Array of labels in the source code
	 * @param labeRead    Specified label attached to CBZ or B command.
	 * @return The offset of a specified label from CBZ or B command.
	 */
	public static Integer findOffset(int offFromMain, ArrayList<LabelOffset> labels, String labelRead) {
		int offSet = 0;

		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i).label.equalsIgnoreCase(labelRead)) {
				offSet = labels.get(i).offset - offFromMain;
				break;
			}
		}
		return offSet;
	}

	/**
	 * Second pass of the assembler. Writes the binary data and code files.
	 * 
	 * @param inFile   The pathname of the file containing assembly language code.
	 * @param dataFile The pathname for the data segment binary file.
	 * @param codeFile The pathname for the code segment binary file.
	 * @param labels   List of the code segment labels and relative offsets.
	 * @exception RuntimeException if there is an error when processing the assembly
	 *                             code file.
	 */
	public static void pass2(String inFile, String dataFile, String codeFile, ArrayList<LabelOffset> labels)
			throws FileNotFoundException, IOException {

		try {
			FileWriter dataWriter = new FileWriter(new File(dataFile), true);
			//FileWriter codeWriter = new FileWriter(new File(codeFile), true);
			Scanner scn = new Scanner(new File(inFile));
			while (scn.hasNext()) {
				String readFromFile = scn.next();

				if (readFromFile.equalsIgnoreCase(".data")) {

					while (!readFromFile.equalsIgnoreCase(".text")) {
						readFromFile = scn.next();

						if (readFromFile.equalsIgnoreCase(".word")) {
							readFromFile = scn.next();
							String[] array = readFromFile.split(",");

							for (int i = 0; i < array.length; i++) {
								boolean[] temp = Binary.sDecToBin(Integer.parseInt(array[i]), 32);
								for (int j = 0; j < temp.length; j++) {
									if (bitsOnLine != 8) {
										dataWriter.write(String.valueOf(temp[j]));
										dataWriter.write(" ");
										bitsOnLine++;
									} else {
										bitsOnLine = 1;
										dataWriter.write("\n");
										dataWriter.write(String.valueOf(temp[j]));
										dataWriter.write(" ");
									}
								}

								if (bitsOnLine == 8) {
									dataWriter.write("\n");
									bitsOnLine = 0;
								}

							}

						}
					}
				}

				if (readFromFile.equalsIgnoreCase(".text")) {

					int destination, sourceOne, sourceTwo;
					boolean[] destReg, sourceFirst, sourceSecond, machineCode;

					String[] array;
					int offset = 0;

					// To get past the .global main line in the source code.
					readFromFile = scn.next();
					readFromFile = scn.next();

					int lineNum = 0;

					while (!readFromFile.equalsIgnoreCase(".end")) {

						readFromFile = scn.next();

						switch (readFromFile) {

						case "ADD":
							machineCode = Opcode.ADD;
							boolean[] addOffset = { false, false, false, false, false, false };

							readFromFile = scn.next();
							array = readFromFile.split(",");
							boolean[] add;
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

							sourceOne = Integer.parseInt((String) array[1].subSequence(1, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, array[2].length()));
							sourceSecond = Binary.uDecToBin(sourceTwo, 5);

							add = ArrayUtils.addAll(destReg, sourceFirst);
							add = ArrayUtils.addAll(add, addOffset);
							add = ArrayUtils.addAll(add, sourceSecond);
							add = ArrayUtils.addAll(add, machineCode);
							printToCodeFile(add, codeFile);

							lineNum++;
							break;

						case ".end":
							machineCode = Opcode.HLT;
							boolean[] haltOffset = new boolean[21];
							for (int i = 0; i < 21; i++) {
								haltOffset[i] = false;
							}
							boolean[] halt;
							halt = ArrayUtils.addAll(haltOffset, machineCode);
							printToCodeFile(halt, codeFile);

							break;
						case "SUB":
							boolean[] subOffset = { false, false, false, false, false, false };
							machineCode = Opcode.SUB;
							readFromFile = scn.next();
							array = readFromFile.split(",");
							boolean[] sub;
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

							sourceOne = Integer.parseInt((String) array[1].subSequence(1, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, array[2].length()));
							sourceSecond = Binary.uDecToBin(sourceTwo, 5);

							sub = ArrayUtils.addAll(destReg, sourceFirst);
							sub = ArrayUtils.addAll(sub, subOffset);
							sub = ArrayUtils.addAll(sub, sourceSecond);
							sub = ArrayUtils.addAll(sub, machineCode);
							printToCodeFile(sub, codeFile);
							lineNum++;
							break;
						case "AND":
							boolean[] andOffset = { false, false, false, false, false, false };
							machineCode = Opcode.AND;
							readFromFile = scn.next();
							array = readFromFile.split(",");
							boolean[] and;
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

							sourceOne = Integer.parseInt((String) array[1].subSequence(1, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, array[2].length()));
							sourceSecond = Binary.uDecToBin(sourceTwo, 5);

							and = ArrayUtils.addAll(destReg, sourceFirst);
							and = ArrayUtils.addAll(and, andOffset);
							and = ArrayUtils.addAll(and, sourceSecond);
							and = ArrayUtils.addAll(and, machineCode);
							printToCodeFile(and, codeFile);
							lineNum++;
							break;

						case "ORR":
							boolean[] orrOffset = { false, false, false, false, false, false };
							machineCode = Opcode.ORR;
							readFromFile = scn.next();
							array = readFromFile.split(",");
							boolean[] orr;
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

							sourceOne = Integer.parseInt((String) array[1].subSequence(1, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, array[2].length()));
							sourceSecond = Binary.uDecToBin(sourceTwo, 5);

							orr = ArrayUtils.addAll(destReg, sourceFirst);
							orr = ArrayUtils.addAll(orr, orrOffset);
							orr = ArrayUtils.addAll(orr, sourceSecond);
							orr = ArrayUtils.addAll(orr, machineCode);
							printToCodeFile(orr, codeFile);
							lineNum++;
							break;

						case "LDR":

							machineCode = Opcode.LDR;
							boolean[] ldrOffset = { false, false };
							readFromFile = scn.next();
							boolean[] load;
							array = readFromFile.split(",");
							// destination is same as data register essentially
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

							// this will be the base register reference
							sourceOne = Integer.parseInt((String) array[1].subSequence(2, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

							// This will refer to immediate value
							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, (array[2].length() - 1)));
							sourceSecond = Binary.uDecToBin(sourceTwo, 9);

							load = ArrayUtils.addAll(destReg, sourceFirst);
							load = ArrayUtils.addAll(load, ldrOffset);
							load = ArrayUtils.addAll(load, sourceSecond);
							load = ArrayUtils.addAll(load, machineCode);
							printToCodeFile(load, codeFile);
							lineNum++;
							break;

						case "STR":

							machineCode = Opcode.STR;
							boolean[] strOffset = { false, false };
							readFromFile = scn.next();
							boolean[] store;
							array = readFromFile.split(",");
// destination is same as data register essentially
							destination = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							destReg = Binary.uDecToBin(destination, 5);

// this will be the base register reference
							sourceOne = Integer.parseInt((String) array[1].subSequence(2, array[1].length()));
							sourceFirst = Binary.uDecToBin(sourceOne, 5);

// This will refer to immediate value
							sourceTwo = Integer.parseInt((String) array[2].subSequence(1, (array[2].length() - 1)));
							sourceSecond = Binary.uDecToBin(sourceTwo, 9);

							store = ArrayUtils.addAll(destReg, sourceFirst);
							store = ArrayUtils.addAll(store, strOffset);
							store = ArrayUtils.addAll(store, sourceSecond);
							store = ArrayUtils.addAll(store, machineCode);
							printToCodeFile(store, codeFile);
							lineNum++;
							break;
						case "CBZ":

							machineCode = Opcode.CBZ;
							readFromFile = scn.next();
							array = readFromFile.split(",");
							int offFromMain = (lineNum) * 4;
							offset = findOffset(offFromMain, labels, array[1]);

							boolean[] immediate = Binary.sDecToBin(offset, 19);
							int reg = Integer.parseInt((String) array[0].subSequence(1, array[0].length()));
							boolean[] register = Binary.uDecToBin(reg, 5);

							boolean[] cbz;

							cbz = ArrayUtils.addAll(register, immediate);
							cbz = ArrayUtils.addAll(cbz, machineCode);
							printToCodeFile(cbz, codeFile);

							lineNum++;

							break;
						case "B":

							machineCode = Opcode.B;
							readFromFile = scn.next();
							array = readFromFile.split(",");

							int offMain = (lineNum) * 4;
							offset = findOffset(offMain, labels, array[0]);

							boolean[] immediateB = Binary.sDecToBin(offset, 26);
							boolean[] toPrintArray = ArrayUtils.addAll(immediateB, machineCode);

							printToCodeFile(toPrintArray, codeFile);

							lineNum++;
							break;

						default:
							continue;
						}

					}
				}
			}

			//codeWriter.flush();
			dataWriter.flush();
			//codeWriter.close();
			dataWriter.close();
			scn.close();

		} catch (FileNotFoundException e) {

		}

	}
}
