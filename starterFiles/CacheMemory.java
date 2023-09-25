
/**
* Simulates cache memory
*
* @author student's name
*/

import java.util.*;

public class CacheMemory {

	/** Set to true to print additional messages for debugging purposes */
	private static final boolean DEBUG = false;

	/** The number of bytes read or written by one CPU operation */
	public static final int WORD_SIZE = 4; // 4 bytes = 32 bits

	/** The Main Memory this cache is connected to. */
	private MainMemory mainMemory;

	/** Simulate cache as an array of CacheSet objects. */
	private CacheSet[] cache;

	/**
	 * Number of bits used for selecting one byte within a cache line. These are the
	 * least significant bits of the memory address.
	 */
	private int numByteBits;

	/**
	 * Number of bits used for specifying the cache set that a memory adddres
	 * belongs to. These are the middle bits of the memory address.
	 */
	private int numSetBits;

	/**
	 * Number of bits used for specifying the tag associated with the memory
	 * address. These are the most significant bits of the memory address.
	 */
	private int numTagBits;

	/**
	 * Count of the total number of cache requests. This is used for implementing
	 * the least recently used replacement algorithm; and for reporting information
	 * about the cache simulation.
	 */
	private int requestCount;

	/**
	 * Count of the number of times a cache request is a hit. This is used for
	 * reporting information about the cache simulation.
	 */
	private int hitCount;

	/**
	 * Track the "cost" of a hit. For each cache hit, record the number of cache
	 * lines that are searched in order to determine this is a hit. This data member
	 * is an accumulator for the hit cost (each hit will add its cost to this data
	 * member). This is used for reporting information about the cache simulation.
	 */
	private int hitCost;

	/**
	 * Count of the number of cache requests that are performed during the warmUp
	 * method. This is used for reporting information about the cache simulation.
	 */
	private int warmUpRequests;

	/**
	 * DO NOT MODIFY THIS METHOD
	 *
	 * Constructor creates a CacheMemory object. Note the design rules for valid
	 * values of each parameter. The simulated computer reads or writes a unit of
	 * one WORD_SIZE.
	 *
	 * @param m           The MainMemory object this cache is connected to.
	 * @param size        The size of this cache, in Bytes. Must be a multiple of
	 *                    the lineSize.
	 * @param lineSize    The size of one cache line, in Bytes. Must be a multiple
	 *                    of 4 Bytes.
	 * @param linesPerSet The number of lines per set. The number of lines in the
	 *                    cache must be a multiple of the linesPerSet.
	 *
	 * @exception IllegalArgumentExcepction if a parameter value violates a design
	 *                                      rule.
	 */
	public CacheMemory(MainMemory m, int size, int lineSize, int linesPerSet) {
		
		if (lineSize % WORD_SIZE != 0) {
			throw new IllegalArgumentException("lineSize is not a multiple of " + WORD_SIZE);
		}

		if (size % lineSize != 0) {
			throw new IllegalArgumentException("size is not a multiple of lineSize.");
		}

		// number of lines in the cache
		int numLines = size / lineSize;

		if (numLines % linesPerSet != 0) {
			throw new IllegalArgumentException("number of lines is not a multiple of linesPerSet.");
		}

		// number of sets in the cache
		int numSets = numLines / linesPerSet;

		// Set the main memory
		mainMemory = m;

		// Initialize the counters to zero
		requestCount = 0;
		warmUpRequests = 0;
		hitCount = 0;
		hitCost = 0;

		// Determine the number of bits required for the byte within a line,
		// for the set, and for the tag.
		int value;
		numByteBits = 0; // initialize to zero
		value = 1; // initialize to 2^0
		while (value < lineSize) {
			numByteBits++;
			value *= 2; // increase value by a power of 2
		}

		numSetBits = 0;
		value = 1;
		while (value < numSets) {
			numSetBits++;
			value *= 2;
		}

		// numTagBits is the remaining memory address bits
		numTagBits = 32 - numSetBits - numByteBits;

		System.out.println("CacheMemory constructor:");
		System.out.println("    numLines = " + numLines);
		System.out.println("    numSets = " + numSets);
		System.out.println("    numByteBits = " + numByteBits);
		System.out.println("    numSetBits = " + numSetBits);
		System.out.println("    numTagBits = " + numTagBits);
		System.out.println();

		// Create the array of CacheSet objects and initialize each CacheSet object
		cache = new CacheSet[numSets];
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new CacheSet(lineSize, linesPerSet, numTagBits);
		}
	} // end of constructor

	/**
	 * DO NOT MODIFY THIS METHOD
	 *
	 * "Warm Up" the cache by reading random memory addresses. This method is used
	 * by programs that do not want to run on a "cold" cache. The cache performance
	 * statistics do not include requests from this warm up phase.
	 *
	 * @param numReads The number of warm-up read operations to perform.
	 * @param random   A random number generator object.
	 */
	public void warmUp(int numReads, Random random) {
		int wordsInMainMem = (mainMemory.getSize() / WORD_SIZE);

		for (int i = 0; i < numReads; i++) {
			// Generate a random line address
			int wordAddress = random.nextInt(wordsInMainMem) * WORD_SIZE;
			boolean[] address = Binary.uDecToBin(wordAddress, 32);
			readWord(address, false);
		}
		warmUpRequests = requestCount;
	}

	/**
	 * DO NOT MODIFY THIS METHOD
	 *
	 * Prints the total number of requests and the number of requests that resulted
	 * in a cache hit.
	 */
	public void reportStats() {
		System.out.println("Number of requests: " + (requestCount - warmUpRequests));
		System.out.println("Number of hits: " + hitCount);
		System.out.println("hit ratio: " + (double) hitCount / (requestCount - warmUpRequests));
		System.out.println("Average hit cost: " + (double) hitCost / hitCount);
	}

	/**
	 * DO NOT MODIFY THIS METHOD
	 *
	 * Returns the word that begins at the specified memory address.
	 *
	 * This is the public version of readWord. It calls the private version of
	 * readWord with the recordStats parameter set to true so that the cache
	 * statistics information will be recorded.
	 *
	 * @param address The byte address where the 32-bit value begins.
	 * @return The word read from memory. Index 0 of the array holds the least
	 *         significant bit of the binary value.
	 *
	 */
	public boolean[] readWord(boolean[] address) {
		return readWord(address, true);
	}

	/**
	 * takes in a data from cacheline in 2D array and returns it as 1D array. 
	 * 
	 * @param arr
	 * @return
	 */
	// tag bits
	private boolean[] twoDtoOneD(boolean[][] arr) {
		// 4X8
		// tag is address / (length of a set * blocksize)
		// so 4 lines, 8 bits on each line.
		// CacheSet​(int lineSize == 8 bits, int linesPerSet == 4, int numTagBits ==
		// 32/)
		// Constructor creates a set of CacheLine objects.
		// CacheSet toReturn = new CacheSet(8, 4, numTagBits);

		return null;

	}

	/**
	 * This method looks through the cache set line by line. It compares the tag of
	 * each line with the tag passed to the method.
	 * 
	 * @param set     --> Cacheset from which we loop through.
	 * @param tagBits --> to be looked for.
	 * @return True when tag passed is found in the cacheSet. False otherwise.
	 */
	private int isInCache(CacheSet set, boolean[] tagBits, boolean recordStats) {
		int length = set.size() - 1;
		while (length >= 0) {
			if (set.getLine(length).isValid()) {
				if (Arrays.equals(set.getLine(length).getTag(), tagBits)) {
					if (recordStats) {
						hitCost = set.size() - length;
					}
					return length;
				}
			}
			length--;
		}

		return -1;
	}

	/**
	 * This method finds the least recently used line from the CacheSet.
	 * 
	 * @param set --> A Cacheset being looked through.
	 * @return An integer that refers to the line location that was least recently
	 *         used.
	 */
	private int findLeastRecentlyUsed(CacheSet set) {
		int length = set.size() - 1;
		int leastRecentlyUsed = 0;
		while (length > 0) {
			if (set.getLine(length).getLastUsed() < set.getLine(length - 1).getLastUsed()) {
				leastRecentlyUsed = length;
			} else {
				leastRecentlyUsed = length - 1;
			}
		}
		return leastRecentlyUsed;
	}

	private void updateCache(int setBitsFromAddress, int lineLeastRecentlyUsed, boolean[] tagFromAddress,
			boolean[] blockAddress, boolean[] address) {
		
		cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed).setData(mainMemory.read(blockAddress, 32));
		cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed).setTag(tagFromAddress);
		cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed).setLastUsed(requestCount);

	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * Returns the word that begins at the specified memory address.
	 *
	 * This is the private version of readWord that includes the cache statistic
	 * tracking parameter. When recordStats is false, this method should not update
	 * the cache statistics data members (hitCount and hitCost).
	 *
	 * @param address     The byte address where the 32-bit value begins.
	 * @param recordStats Set to true if cache statistics tracking data members
	 *                    (hitCount and hitCost) should be updated.
	 * @return The word read from memory. Index 0 of the array holds the least
	 *         significant bit of the binary value.
	 * @exception IllegalArgumentException if the address is not valid.
	 */
	private boolean[] readWord(boolean[] address, boolean recordStats) {
		if (address.length > 32) {
			throw new IllegalArgumentException("address parameter must be 32 bits");
		}
		requestCount++;
		// Programming Assignment 5: Complete this method
		// You are encouraged to create and use private methods within this method as
		// needed
		// to reduce redundant code in readWord and writeWord methods
		// blockSize = 2 to the power of numByteBits
		// Identify the line in the cache that the address maps to

		// Get the set bits from the address
		boolean[] setFromAddress = Arrays.copyOfRange(address, numByteBits, (numByteBits + numSetBits));
		// Get the tag bits from the address.
		boolean[] tagFromAddress = Arrays.copyOfRange(address, (numByteBits + numSetBits), address.length);
		// Integer value of the set bits. To be used for cache array.
		int setBitsFromAddress = (int) Binary.binToUDec(setFromAddress);

		// Identify if line is in the cache.
		int indexFound = isInCache(cache[setBitsFromAddress], tagFromAddress, recordStats);

		// if not false. It was found
		if (indexFound != -1) {
			if (recordStats) {  //if recordStats is true, then increment hit count.
				hitCount++;
			}
			//Make a line object. 
			CacheLine line = cache[setBitsFromAddress].getLine(indexFound);

			// CHECK if the line is dirty after it was found. 
			if (line.isDirty()) {
				//Then I write when it is true
				//TODO call write method. With what address..... 
			// It was not dirty. I can just return	
			} else {
				//I will return a data from the cache. When i do getData, it is 2D, I have to convert to 1D
				
				// TODO take the data from cacheLine that is cacheSet. At the index it was
				// found. Read word size.
				//TODO what do you mean wordSize...
				return twoDtoOneD(line.getData());
			}

			
		// not found in cache
		} else {
			// The line number that was least recently used. THat will be overwritten
			int lineLeastRecentlyUsed = findLeastRecentlyUsed(cache[setBitsFromAddress]);
			// Set bits are known.
			// This is blockAddress. Found by excluding the bytebits.
			boolean[] blockAddress = Arrays.copyOfRange(address, numByteBits, address.length);
			//Get the line most recently used.
			CacheLine line = cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed);
			// read the block address, exclude the bytebits. Do I still read to 32?
			// TODO read from memory and update the line in cache.
			//TODO DO I read from memory before I write? Or When am I changing dirty bit from clean to dirty. 
			// before replacing. If it is dirty, write the block in memory.
			// Use main memory write
			// After writing, set it to clean.
			// If dirty. Write to memory first.Then Use Least Recently Used replacement. It
			// was write command
			if (line.isDirty()) {
				// TODO I think the data is not blockAddress
				// TODO second is the data im going to write.
				writeWord(address, blockAddress);
				updateCache(setBitsFromAddress, lineLeastRecentlyUsed, tagFromAddress, blockAddress, address);
				//TODO and now return the data?? 
				// TODO if it wasn't dirty. Then I don't need to rewrite memory, but I do need
				// to update the cache. It wasn't write.
				//It wasn't found in cache and it isn't dirty. I just have to read from memory. 
			} else {
				//Update cache reads from memory to the cache.
				updateCache(setBitsFromAddress, lineLeastRecentlyUsed, tagFromAddress, blockAddress, address);
			}

		}

		// If data from the given address is not currently in cache (a miss), read the
		// corresponding line from memory. Use the LRU (least recently used) replacement
		// scheme to select a line
		// within the set.
		// Before replacing the line, if the line is dirty, write the block in memory.

//        	
//        }

		// Next to copy the contents from memory. Read the entire block from memory
		// using the
		// **first** memory address of the block that contains the requested address.
		// Call the MainMemory read method to read the block.
		// I am returned a cacheLine from memory.
		// TODO implement the twoDtoOneD method. Do I need it?
		// CacheLine fromMemory = twoDtoOneD(mainMemory.read(address, 32));
		// Copy the line read from memory into the cache.

		// Update CacheMemory data members (requestCount, hitCount, hitCost)
		// and CacheLine data members (using the various set methods)

		// replace this placeholder return with the data copied from the cache line
		return new boolean[32];
	}

	/**
	 * DO NOT MODIFY THIS METHOD
	 *
	 * This is the public version of writeWord. It calls the private version of
	 * writeWord with the recordStats parameter set to true so that the cache
	 * statistics information will be recorded.
	 *
	 * @param address The byte address where the 32-bit value should be written to.
	 * @param data    The word to be written to memory. Index 0 of the array holds
	 *                the least significant bit of the binary value.
	 *
	 */
	public void writeWord(boolean[] address, boolean[] data) {
		writeWord(address, data, true);
	}

	/**
	 * STUDENT MUST COMPLETE THIS METHOD
	 *
	 * This is the private version of writeWord that includes the cache statistic
	 * tracking parameter. When recordStats is false, this method should not update
	 * the cache statistics data members (hitCount and hitCost).
	 *
	 * @param address The byte address where the 32-bit value should be written to.
	 * @param data    The word to be written to memory. Index 0 of the array holds
	 *                the least significant bit of the binary value.
	 * @exception IllegalArgumentException if the address is not valid.
	 */
	private void writeWord(boolean[] address, boolean[] data, boolean recordStats) {
		if (address.length > 32) {
			throw new IllegalArgumentException("address parameter must be 32 bits");
		}

		// You are encouraged to create and use private methods within this method as
		// needed
		// to reduce redundant code in readWord and writeWord methods
		// The comments provide a guide for this method.

		// Identify the line in the cache that the address maps to
		// Get the set bits from the address
		boolean[] setFromAddress = Arrays.copyOfRange(address, numByteBits, (numByteBits + numSetBits));
		// Get the tag bits from the address.
		boolean[] tagFromAddress = Arrays.copyOfRange(address, (numByteBits + numSetBits),
				(numByteBits + numSetBits + numTagBits));
		// Integer value of the set bits. To be used for cache array.
		int setBitsFromAddress = (int) Binary.binToUDec(setFromAddress);
		int indexFound = isInCache(cache[setBitsFromAddress], tagFromAddress, recordStats);
		// If it was found. Then I just write through.
		if (indexFound != -1) {

			// It was not found.
			//I have to read it to the cache, before I write over. 
			
		} else {

			// TODO call read. It will read into the cache to the appropriate set.
			readWord(address, recordStats);
			// The line number that was least recently used. Same as it was before,
			int lineLeastRecentlyUsed = findLeastRecentlyUsed(cache[setBitsFromAddress]);
			// Set bits are known.
			// This is blockAddress. Found by excluding the bytebits.
			boolean[] blockAddress = Arrays.copyOfRange(address, numByteBits, address.length);
			// read the block address, exclude the bytebits. Do I still read to 32?

			if (cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed).isDirty()) {
				// TODO Inquire about what exactly to write to memory.
				mainMemory.write(address, null);
				cache[setBitsFromAddress].getLine(lineLeastRecentlyUsed).setClean();
			}

		}

		// If data from the given address is not currently in cache (a miss), read the
		// corresponding line from memory. Use the LRU (least recently used) replacement
		// scheme to select a line
		// within the set.

		// Before replacing the line, if the line is dirty, write the block in memory.
		// Use MainMemory write to write to memory. After writing, set the line as
		// clean. TODO set the line as clean. DONE. WRiting to memory to. TODO make sure
		// that what is being written is correct.

		// Next to copy the contents from memory. Read the entire block from memory
		// using the
		// **first** memory address of the block that contains the requested address.
		// Call the MainMemory read method to read the block.
		// Copy the line read from memory into the cache.

		// Update CacheMemory data members (requestCount, hitCount, hitCost)
		// and CacheLine data members (using the various set methods)
		// as needed for tracking cache hit rate and implementing the
		// least recently used replacement algorithm in the cache set.

		// update the data in the line with the new data passed as a parameter
	}

}
