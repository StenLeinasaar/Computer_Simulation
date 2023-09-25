import java.util.ArrayList;
import java.util.List;

public class Program {

	public static boolean isValidSubsequence(List<Integer> array, List <Integer> sequence) {
		int sequenceIndex = 0;
		int arrayIndex = 0;
		while(arrayIndex < array.size() && sequenceIndex < sequence.size()) {
			if(array.get(arrayIndex).equals(sequence.get(sequenceIndex))) {
				sequenceIndex++;
			}
			arrayIndex++;
		}
	
		return sequenceIndex == sequence.size();
	}
	
	public static void main(String[] args) {
		ArrayList <Integer> array = new ArrayList<Integer>();
		array.add(5);
		array.add(1);
		array.add(22);
		array.add(25);
		array.add(6);
		array.add(-1);
		array.add(9);
		array.add(10);
		
		ArrayList <Integer> sequence = new ArrayList <Integer>();
		sequence.add(10);
		sequence.add(25);
		sequence.add(-1);
		sequence.add(9);
		
		System.out.println(isValidSubsequence(array, sequence));
	}
}
