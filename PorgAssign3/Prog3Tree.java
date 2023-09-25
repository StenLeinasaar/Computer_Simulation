public class Prog3Tree {

	//returns array index where key is found
	public static int p_search(int[] treeA, int key, int i) {
		//if i is equal to greater than length of array, return -1
		if(i>=treeA.length)
			return -1;

		//if key equal to current node, then return indexof current node
		else if(key==treeA[i])
			return i;

		//if key greater than current node, then compare with right child
		else if(key>treeA[i])
			return p_search(treeA, key, (2*i)+2);

		//if key is less than current node then compare with left child
		else if(key<treeA[i])
			return p_search(treeA, key, (2*i)+1);

		return -1;
	}

	public static void main(String[] args) {
		int[] treeA= {57,39,72,23,50,62,87,20,27,49,53,60,63,81,95};
		System.out.println("Found 57 at "+ p_search(treeA, 57, 0));
		System.out.println("Found 20 at "+ p_search(treeA, 20, 0));
		System.out.println("Found 95 at "+ p_search(treeA, 95, 0));
		System.out.println("Did not find 122, so returning "+ p_search(treeA, 122, 0));
	}
}