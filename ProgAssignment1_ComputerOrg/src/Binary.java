
/**
 * Methods for converting between binary and decimal.
 *
 * @author Sten Leinasaar
 */
public class Binary {

	/** Constant defines the maximum length of binary numbers. */
	private static final int MAX_LENGTH = 32;

	/**
	 * 
	 * Converts a two's complement binary number to signed decimal. MSB is at
	 * length-1.
	 *
	 * @param b The two's complement binary number as an array of booleans
	 * @return The equivalent decimal value
	 * @exception IllegalArgumentException Parameter array length is longer than
	 *                                     MAX_LENGTH.
	 */
	public static long binToSDec(boolean[] b) {

		// Check for the array out of bounds exceptions. Length is bigger than allowed.
		if (b.length > MAX_LENGTH) {
			throw new IllegalArgumentException("parameter array is longer than " + MAX_LENGTH + " bits.");
		}
		// if MSB is 0, then positive representation and we convert to decimal right
		// away.
		if (b[b.length - 1] == false) {
			return positiveBinaryToDecimal(b);
		}
		// MSB is 1, hence negative number
		else {
			// use simple negation to convert to positive representation.
			b = simpleNeg(b);

			return -1 * positiveBinaryToDecimal(b);

		}

	}

	/**
	 * Takes in the positive number two's complement represenation and converts it
	 * into decimal.
	 * 
	 * @param b an array of boolean values that represents two's complement for a
	 *          positive number.
	 * @return decimal value in type long.
	 */
	private static long positiveBinaryToDecimal(boolean[] b) {
		long decimalValue = 0;
		for (int i = 0; i < b.length -1; i++) {
			// convert only when it is 1 (true).
			if (b[i] == true) {
				decimalValue += Math.pow((double) 2, (double) i);
			}
		}
		return decimalValue;
	}

	/**
	 * 
	 * 
	 * 
	 * Converts an unsigned binary number to unsigned decimal
	 *
	 * @param b The unsigned binary number
	 * @return The equivalent decimal value
	 * @exception IllegalArgumentException Parameter array length is longer than
	 *                                     MAX_LENGTH.
	 */
	public static long binToUDec(boolean[] b) {
		// PROGRAM 1: Student must complete this method
		// return value is a placeholder, student should replace with correct return
		long decimalValue = 0;
		for (int i = 0; i < b.length; i++) {
			// convert only when it is 1 (true).
			if (b[i] == true) {
				decimalValue += Math.pow((double) 2, (double) i);
			}
		}
		return decimalValue;
	}

	/**
	 * Method that implements simple negation to take the positive binary
	 * representation of a negative signed binary and convert it into two's
	 * complement.
	 * 
	 * 
	 * @param a An array of boolean values to represent a positive binary
	 *          representation of a negative decimal number
	 * @return an equivalent two's complement for that negative decimal number.
	 */
	public static boolean[] simpleNeg(boolean[] a) {
		for (int i = 0; i < a.length; i++) {
			// if we find a 1 increment the index to start flipping from the next bit.
			if (a[i] == true) {
				i++;
				// loop through rest of the array.
				for (int j = i; j < a.length; j++) {
					// if false, then change to true
					if (a[j] == false) {
						a[j] = true;
					}
					// else change true to false.
					else {
						a[j] = false;
					}
				}
				// break the original loop because we don't want to continue.
				break;
			}
		}
		// return the modified array of boolean values.
		return a;
	}

	/**
	 * Converts a signed decimal number to two's complement binary
	 *
	 * @param d    The decimal value
	 * @param bits The number of bits to use for the binary number.
	 * @return The equivalent two's complement binary representation.
	 * @exception IllegalArgumentException Parameter is outside valid range that can
	 *                                     be represented with the given number of
	 *                                     bits.
	 */
	public static boolean[] sDecToBin(long d, int bits) {
		// Check for the array out of bounds exceptions. Length is bigger than allowed.
		if (bits > MAX_LENGTH) {
			throw new IllegalArgumentException("parameter array is longer than " + MAX_LENGTH + " bits.");
		}
		boolean[] binaryNumber = new boolean[bits];
		// IF number is less than a <0, we are handling negative number.
		if (d < 0) {
			//take absolute value to find a positive representation
			//positive binary is already two's complement to a positive decimal.
			d = Math.abs(d);
			//use unsigned decimal to binary method.
			binaryNumber = uDecToBin(d, bits);
			//return two's complement representation for a negative decimal value using simple negation.
			return simpleNeg(binaryNumber);
		}
		// else positive number and can handle as unsigned binary.
		else {

			// handle it as unsigned. Use unsigned to binary method
			return uDecToBin(d, bits);

		}
	}

	/**
	 * Converts an unsigned decimal number to binary
	 *
	 * @param d    The decimal value
	 * @param bits The number of bits to use for the binary number.
	 * @return The equivalent binary representation.
	 * @exception IllegalArgumentException Parameter is outside valid range that can
	 *                                     be represented with the given number of
	 *                                     bits.
	 */
	public static boolean[] uDecToBin(long d, int bits) {

		// PROGRAM 1: Student must complete this method
		// return value is a placeholder, student should replace with correct return

		// LSB will be at the index == length - 1
		// MSB will be at the index == 0
		if (bits > MAX_LENGTH) {
			throw new IllegalArgumentException("parameter array is longer than " + MAX_LENGTH + " bits.");
		}

		boolean[] binaryNumber = new boolean[bits];
		// I want to add the reminders. Which means that the reverse storing is good.
		int index = 0;
		while (d > 0) {
			if (index >= 31) {
				throw new IllegalArgumentException("parameter array is longer than " + MAX_LENGTH + " bits.");
			}
			if (d % 2 == 0) {
				binaryNumber[index] = false;
				d = d / 2;

			} else {
				binaryNumber[index] = true;
				d = d / 2;

			}
			index++;
		}

		return binaryNumber;
	}

	/**
	 * Returns a string representation of the binary number. Uses an underscore to
	 * separate each group of 4 bits.
	 *
	 * @param b The binary number
	 * @return The string representation of the binary number.
	 */
	public static String toString(boolean[] b) {
		// PROGRAM 1: Student must complete this method
		// return value is a placeholder, student should replace with correct return
		String binaryToReturn = "";
		int bitCount = 1;
		for (int i = b.length - 1; i >= 0; i--) {

			if (b[i] == true) {
				if (bitCount == 5) {
					binaryToReturn += "_1";
					bitCount = 1;
				} else {
					binaryToReturn += "1";
				}

				bitCount++;

			} else {
				if (bitCount == 5) {
					binaryToReturn += "_0";
					bitCount = 1;
				} else {
					binaryToReturn += "0";
				}
				bitCount++;
			}

		}
		return binaryToReturn;
	}

	/**
	 * Returns a hexadecimal representation of the unsigned binary number. Uses an
	 * underscore to separate each group of 4 characters.
	 *
	 * @param b The binary number
	 * @return The hexadecimal representation of the binary number.
	 */
	public static String toHexString(boolean[] b) {
		// PROGRAM 1: Student must complete this method
		// return value is a placeholder, student should replace with correct return
		String hexaDecimal = "";
		String hexToReturn = "";
		int bitCount = 1;
		long unsignedDecimal = binToUDec(b);
		hexaDecimal = Long.toHexString(unsignedDecimal).toUpperCase();

		char[] hex = hexaDecimal.toCharArray();
		for (int i = 0; i < hex.length - 1; i++) {
			if (bitCount == 5) {
				hexToReturn += "_" + hex[i];
				bitCount = 1;
			}
			hexToReturn += hex[i];
			bitCount++;
		}

		return hexToReturn;
	}

}
