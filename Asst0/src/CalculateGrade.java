import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

//This assignment was created by 
//Dr. Aarathi Prasad on 9/9/21
/**
 * 
 * student who completed it: Sten Leinasaar @sleinasa
 * 
 * The goal of this assignment is to review the following 
 * Java topics which you will be required to be familiar with 
 * in order to complete the remaining programming assignments - 
 * types, arithmetic expressions, variable scope, loops, 
 * if/else, public/private, static/non-static methods, 
 * arrays and ArrayLists operations, arrays pass-by-reference, file input/output
 *
 */


public class CalculateGrade {

	
	/**
	 *  
	 * The method reads through a file and while there is something to read, adds the value to the matching array. If lab, then to the lab array, 
	 * if assignment, then to the assignments array, else to the exam array. 
	 * 
	 * Under each condition extra reader.next() is implemented to skip over the reading of the total score. 
	 * It assures that when arriving to the top of the while loop, next thing we are going to read will be the type of the assignment. 
	 * 
	 * @param filename name of csv file
	 * @param labs	doubles array for lab scores
	 * @param assts	doubles array for assignments
	 * @param exams	doubles array for exams
	 */
	private static void readFile(String filename, double[] labs, double[] assts, double[] exams) {
		
		try {
			Scanner reader = new Scanner(new File(filename));
			/**
			 * Define indexes for different arrays.
			 * Set the delimiter
			 */
			reader.useDelimiter(", |\n");
			int labIndex = 0;
			int asstsIndex = 0;
			int examsIndex = 0;
			/**
			 * While there is something to read, read the first string.
			 * layout of the file ==> type of assignment, score, totalPossible 
			 */
			while (reader.hasNext()) {
				
				String assignment = reader.next();
				//if the assignment is lab
				if (assignment.equalsIgnoreCase("lab")) {
					labs[labIndex] = reader.nextDouble();
					labIndex++;
					//read the totalPossible
					assignment = reader.next();
				//else if it is assignment	
				} else if (assignment.equalsIgnoreCase("asst")) {
					assts[asstsIndex] = reader.nextDouble();
					asstsIndex++;
					//read the totalPossible
					assignment = reader.next();
				//else it is exam
				} else {
					exams[examsIndex] = reader.nextDouble();
					examsIndex++;
					//read the totalPossible
					assignment = reader.next();
					
				}
			}
			
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	// STUDENT SHOULD NOT MODIFY THIS METHOD
	public static String myGrade(String filename, double[] labs, double[] assts, double[] exams) {
		
		// populate the different variables
		readFile(filename, labs, assts, exams);

		// obtain the total score for labs out of 100
		double lab_total = calculateLabGrade100(labs);

		// obtain the total score for assignments out of 100
		double asst_total = calculateAsstGrade100(assts);

		// obtain the total score out of 100
		double total = calculateScaledTotal(exams, lab_total, asst_total);

		// A print statement added just to see the scaled values for all the categories
		// Maybe commented out if unnecessary
		System.out.println("lab_total: " + lab_total + ", asst_total: " + asst_total + ", total: " + total);
		return calculateGrade(total);
	}



/**
 * 
 * This method finds the weighted total lab grade by taking in the array of lab scores.
 * 
 * Array is sorted using Arrays.sort() method to ascending order. The four lowest scores
 * are equally weighted at 10%.
 * The four higher scores are equally weighted at 15%. 
 * 
 * @param labs An Array of lab scores.
 * @return The total lab grade after adding the weights.
 */
	public static double calculateLabGrade100(double[] labs) {
		double labGrade = 0.0;
		Arrays.sort(labs);
		for (int i = 0; i < labs.length; i++) {
			if (i < 4) {
				labGrade += (labs[i] * 0.1);
			} else {
				labGrade += (labs[i] * 0.15);
			}
		}
		return labGrade;
	}


	/**
	 * The first assignment will be weighted at 10%. After implementing it, the value at assts[0] is 
	 * set to -50 to ensure it will be at the position 0 in order to skip it in the loop. 
	 * 
	 * Array is then sorted in ascending order and loop is implemented to weight each assignment score accordingly.
	 * There will be 6 programming assignments. Assignment 0 would be at 10%, among the remaining, the lower two grades would
	 * be weighted at 16% and the higher two grades would be weighted at 20% and the
	 * remaining one at 18%.
	 * 
	 * @param assts An array of assignment scores
	 * @return The weighted assignment grade
	 */
	public static double calculateAsstGrade100(double[] assts) {
		// replace return value with the value you compute
		double asstGrade = 0.0;
		//add the assignment 0 to total grade with weight 10%
		//must be done before sorting.
		asstGrade += (assts[0] * 0.1);
		//set to -50 to ensure it will maintain the position 0. 
		assts[0] = -50.0;
		Arrays.sort(assts);
		
		/**
		 * Loops through the array. If certain position, then calculates the weighter grade.
		 * The two lower ones (excluding 0) are 16%, two higher ones are 20 %, else
		 * the remaining assignment is 18%. 
		 */
		for (int i = 0; i < assts.length; i++) {
			if (i == 0) {
				continue;
			} else if (i == 1 || i == 2) {
				asstGrade += (assts[i] * 0.16);
			} else if (i == 4 || i == 5) {
				asstGrade += (assts[i] * 0.2);
			} else {
				asstGrade += (assts[i] * 0.18);
			}
		}

		return asstGrade;
	}

	
	
	/**
	 * 
	 * All weighted scores are now scaled to the total. Weighted labs are 15% of the total grade, assignments
	 * are 30% and exams are 55% ( midterm being 25% and final 30%). 
	 * 
	 * @param exams  Array of exam scores. Not sorted. first is midterm, second is final. 
	 * @param lab_total The weighted grade of all the labs
	 * @param asst_total  The weighted grade of all the assignments
	 * @return The total scaled grade. 
	 */
	public static double calculateScaledTotal(double[] exams, double lab_total, double asst_total) {
		return (.25 * exams[0]) + (.3 * exams[1]) + (0.15 * lab_total) + (0.3 * asst_total);
	}

	
	/**
	 * 
	 * The letter grade starts as "A" and is changed according to the ranges provided. 
	 * If matches with some range, the letter grade will be changed accordingly. 
	 * 
	 * @param total total scaled grade
	 * @return The letter grade assigned based on the total scaled grade. "A", "A-" "B+" etc.
	 */
	public static String calculateGrade(double total) {
		String grade = "A";	
		
		// 95<=T<100 A
		if (total <= 100 && total >= 95) {
			grade = "A";
		}
		// 90<=T<95 A-
		else if (total < 95 && total >= 90) {
			grade = "A-";
		}
		// 85<=T<90 B+
		else if (total < 90 && total >= 85) {
			grade = "B+";
		}
		// 80<=T<85 B
		else if (total < 85 && total >= 80) {
			grade = "B";
		}
		// 75<=T<80 B-
		else if (total < 80 && total >= 75) {
			grade = "B-";
		}
		// 70<=T<75 C+
		else if (total < 75 && total >= 70) {
			grade = "C+";
		}
		// 65<=T<70 C
		else if (total < 70 && total >= 65) {
			grade = "C";
		}
		// 60<=T<65 C-
		else if (total < 65 && total >= 60) {
			grade = "C-";
		}
		// 55<=T<60 D+
		else if (total < 60 && total >= 55) {
			grade = "D+";
		}
		// 50<=T<55 D
		else if (total < 55 && total >= 50) {
			grade = "D";
		}
		// <50 F
		else {
			grade = "F";
		}
		return grade;
	}

//	//STUDENT MUST ADD CODE TO THIS METHOD
	/**
	 * The method run series of tests to check the correct reading of the file. In addition
	 * tests will check if the computation is done correctly.
	 * 
	 * @param labs array of lab scores
	 * @param assts	array of assignment scores
	 * @param exams	array of exam scores
	 */
	public static void runTests(double[] labs, double[] assts, double[] exams) {
		boolean allTestsPassed = true;
		readFile("test_scores.csv", labs, assts, exams);
		// TEST 1a - Checking if midterm value is read correctly!
		if (exams[0] != 90.0) {
			System.out.println("ERROR in Test 1a: Incorrect value in midterm.");
			System.out.println("Expected to find a value of 90, but found " + exams[0] + " instead");
			allTestsPassed = false;
		}
		
		//Test 1b - Checking if final examl value is read correctly.
		if(exams[1] != 95.0) {
			System.out.println("ERROR in Test 1b: Incorrect value in midterm.");
			System.out.println("Expected to find a value of 95, but found " + exams[1] + " instead");
			allTestsPassed = false;
		}
		
		
		// TEST 2a- Checking if labs are read correctly
		if (labs[1] != 90.0 || labs[2] != 70.0) {
			System.out.println("ERROR in Test 2a: Incorrect values read into labs array.");
			System.out.println("Expected to find a values of labs[1]=90.0 and labs[2]=70.0, but found labs[1]="
					+ labs[1] + " and labs[2]=" + labs[2] + " instead");
			allTestsPassed = false;
		}
		
		//Test 2b --> Checking if assignment values are read correctly
		if (assts[1] != 75.0 || assts[2] != 80.0) {
			System.out.println("ERROR in Test 2b: Incorrect values read into assts array.");
			System.out.println("Expected to find a values of assts[1]=75.0 and labs[2]=80.0, but found assts[1]="
					+ assts[1] + " and assts[2]=" + assts[2] + " instead");
			allTestsPassed = false;
		}
		
		
		// Test 3a- Checking if weighted lab score out of 100 is correct
		double lab_total = calculateLabGrade100(labs);
		if (lab_total != 88) {
			System.out.println("Test 3A failed: Calculation of weighted lab score out of 100 is incorrect.");
			System.out.println("Expected 88, but got " + lab_total + " instead.");
			allTestsPassed = false;
		}
		
		// Test 3b- Checking if weighted assignment score out of 100 is correct
		double asst_total = calculateAsstGrade100(assts);
		if (asst_total != 88) {
			System.out.println("Test 3B failed: Calculation of weighted assignment score out of 100 is incorrect.");
			System.out.println("Expected 88, but got " + asst_total + " instead.");
			allTestsPassed = false;
		}
		// Test 4- Checking if scaled total score out of 100 is correct
		double scaled_total = calculateScaledTotal(exams, lab_total, asst_total);
		if (scaled_total != 90.6) {
			System.out.println("Test 4 failed: Calculation of scaled total of 100 is incorrect.");
			System.out.println("Expected 94.6, but got " + scaled_total + " instead.");
			allTestsPassed = false;
		}
		

		// Test 5- Checking if grade is correct
		String grade = calculateGrade(scaled_total);
		if (grade.compareTo("A-") != 0) {
			System.out.println("Test 5 failed: Calculation of grade is incorrect.");
			System.out.println("Expected A-, but got " + grade + " instead");
			allTestsPassed = false;
		}

		// If allTestsPassed is still true, then all tests have passed
		// and print so!
		if (allTestsPassed)
			System.out.println("All tests passed!");

	}

	// STUDENT SHOULD NOT MODIFY THIS METHOD
	/**
	 * This method enters zero in all the arrays
	 * @param arr --> An array of doubles
	 */
	public static void zeroTheArray(double[] arr) {
		for (int i = 0; i < arr.length; ++i)
			arr[i] = 0.0;
	}

	// STUDENT SHOULD NOT MODIFY THIS METHOD
	public static void main(String[] args) {

		double labs[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		double assts[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		double exams[] = { 0.0, 0.0 };

		// Runs all tests to make sure your code works correctly with test_scores.csv
		// IMPORTANT: make sure you have not modified test_scores.csv
		// If you think you have, re-download the .csv file from theSpring
		runTests(labs, assts, exams);

		zeroTheArray(labs);
		zeroTheArray(assts);
		zeroTheArray(exams);

		Scanner scn = new Scanner(System.in);
		System.out.println("Enter a filename, including the .csv extension, for example my_scores.csv");
		String filename = scn.nextLine();
		System.out.println(
				"Your grade, based on scores in " + filename + " is : " + myGrade(filename, labs, assts, exams));
		//closing the scanner.
		scn.close();
	}
}