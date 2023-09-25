// <student's name(s)>
// CS318 Programming Assignment 3
// A64 implementation of Binary Search Tree
	.align 2
	.data
	// Assume that the BST's are full and complete
	// (every node other than the leaves has exactly two children, leaves are all
	// at the same depth).
	// Structure of the tree data:
	// First value is the number of nodes in the tree.
	// This is followed by the values stored in each node. The BST is stored as
	// an array where the childern of the node at index i are located at indexes
	// (2i+1) and (2i+2).
treeA:
	.dword 15 // number of nodes in treeA
	.dword 57,39,72,23,50,62,87,20,27,49,53,60,63,81,95 // BST represented as an array
treeB:
	.dword 63 // number of nodes in treeB
	// BST represented as an array
	.dword 2941,1836,3400,1418,2176,3298,4199,1128,1472,2143
    .dword 2552,3060,3310,3598,4280,1020,1150,1438,1713,2037
    .dword 2154,2219,2634,2987,3104,3305,3362,3487,3674,4242
    .dword 4733,1009,1057,1146,1223,1426,1453,1663,1755,1962
    .dword 2079,2145,2175,2189,2379,2602,2654,2974,3012,3095
    .dword 3162,3300,3307,3325,3373,3458,3511,3632,3912,4222
    .dword 4278,4673,4947
treeC: // empty tree
	.dword 0 // number of nodes in treeC
treeD: // tree has one node
	.dword 1 // number of nodes in treeD
	.dword 12345 // single node in the tree
	.text
	.global main
main:

	////////////////////
	// Test 1: treeA
	// Call the search procedure
	ADR X1,treeA // base memory address of the tree
	ADD X1,X1,#8 // before calling the procedure, put address of first array element into X1
	MOV X2,#87 // key value to search for
	MOV X3,#0 // offset of root node
	MOV X4,#0 // array index of root node
	BL p_search

	MOV X7,#0 //placeholder, after implementing p_search, check if X0 contains 6

	////////////////////
	// Test 2: treeB
	// Call the search procedure
	ADR X1,treeB // base memory address of the tree
	ADD X1,X1,#8 // before calling the procedure, put address of first array element into X1
	MOV X2,#2189 // key value to search for
	MOV X3,#0 // offset of root node
	MOV X4,#0 // array index of root node
	BL p_search

	MOV X7,#0 //placeholder, after implementing p_search, check if X0 contains 2B, i.e., 43

	////////////////////
	// Test 3: treeC

	// Call the search procedure
	ADR X1,treeC // base memory address of the tree
	ADD X1,X1,#8 // before calling the procedure, put address of first array element into X1
	MOV X2,#987 // key value to search for
	MOV X3,#0 // offset of root node
	MOV X4,#0 // array index of root node
	BL p_search

	MOV X7,#0 //placeholder, after implementing p_search, check if X0 contains -1

	////////////////////
	// Test 4: treeD

	// Call the search procedure
	ADR X1,treeD // base memory address of the tree
	ADD X1,X1,#8 // before calling the procedure, put address of first array element into X1
	MOV X2,#12345 // key value to search for
	MOV X3,#0 // offset of root node
	MOV X4,#0 // array index of root node
	BL p_search

	MOV X7,#0 //placeholder, after implementing p_search, check if X0 contains 0

	// End of main procedure, branch to end of program
	B program_end

p_search:
	// Search Procedure (recursive implementation)
	// X0: Returns the array index where the key is found, or -1 if the key is not found.
	// X1: The memory base address of the binary search tree. Assumes the value before this
	// memory address is the number of nodes in the BST, followed by the values in each node
	// of the BST. Assumes the BST is full and complete (procedure will not alter)
	// X2: The key value to search for (procedure will not alter)
	// X3: The memory offset of the current sub-tree root (procedure may alter)
	// X4: The index of the current sub-tree root (procedure may alter)
	//
	// This procedure must use a recursive algorithm that has worst case
	// performance O(tree height).
	//
	// Temporary registers used by this procedure:
	// <student must list the registers; start with X9, and use registers in number order
	// as needed up to X15>
	// Values of the temporary registers used by this procedure must be preserved.
	// When procedure returns, only X0, X3, and X4 may have a different value than they did at the start.

	//    	X9  will store the values of tree in array form
	//		X10 is the length of an array
	//      X11 register for storing random value
	//		X12 to calculate new offset. Counts the iteration for multiplication
	// 		X13 store new offset.


	//******* Write your code for the search procedure here ******/


search:
	//SUB SP, SP, #16
	// First check if index is out of bounds.
	//Load the length into the X10
	LDR X10, [X1, #-8]
	//if length - current index == 0 then we are done.
	SUB X11, X10, X4
	CBZ X11, not_found


	//LOAD the first element to X9, X3 offset starts with 0 in the beginning of the program.


	LDR X9, [X1, X3]
	SUBS X11, X2, X9
	B.EQ recursive_end
	//if less than
	B.LO left_subtree
	//if greater than
	B.HI right_subtree

	//if it reaches here, then we are done.
	B not_found

left_subtree:
	//2 * index + 1
	MOV X11, #1
	ADD X4, X4, X4
	ADD X4, X4, X11
	//Calculate new offset. Index * 8 bytes.
	//clean up x11
	MOV X11, #0
	B new_offset


right_subtree:
	// 2 * index + 2
	MOV X11, #2
	ADD X4, X4, X4
	ADD X4, X4, X11

	//CALCULATE NEW OFFSET Index * 8
	MOV X11, #0
	B new_offset

not_found:
	MOV X0, #-1
	BR X30

new_offset:
	SUB X12, X4, X11
	CBZ X12, set_new_offset
	ADD X11, X11, #1
	ADD X13, X13, #8
	B new_offset

set_new_offset:
	MOV X3, X13
	MOV X13, #0
	B search

recursive_end:
	//RETURN
	MOV X0, X4
	BR X30

	// End of search procedure

program_end:
	MOV X7,#0 // placeholder at end of program
	.end
