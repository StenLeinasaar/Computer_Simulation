/**
* Test program for the Cache Memory Simulation
*/
import java.util.*;
import java.io.*;

public class Prog5Test {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String dataFile = "example.data";
        int cacheSize; // size of the cache in Bytes
        int lineSize; // size of one cache line in Bytes
        int linesPerSet; // number of cache lines per set

        int numReads = 50000; // number of read requests to perform

        Random random = new Random(70798889); // A random number generator object

        // Create a MainMemory object
        MainMemory mainMemory = new MainMemory(dataFile);
        int mainMemSize = mainMemory.getSize();
        System.out.println("Size of Main Memory in Bytes: " + mainMemSize);


        /////////////////// Small Cache Test //////////////////////////////////

        System.out.println("*********** Small Cache ***********");
        // Create a small set-associative cache
        // This is similar to the cache in Lab 8 Problem 3
        // 2 lines per set (this is a 2-way set associative cache)
        // 4 Bytes per line
        // Cache size is 32 Bytes
        cacheSize = 32;
        lineSize = 4;
        linesPerSet = 2;
        CacheMemory smallCache = new CacheMemory(mainMemory, cacheSize, lineSize, linesPerSet);
        smallTest(smallCache);
        mainMemory.writeToFile("smallTest.data");
        System.out.println("Report from small cache test: ");
        smallCache.reportStats();
        System.out.println();

    } // end of main method

    /**
    * A small test of the cache memory.
    * Refer to Cache_Contents.docx
    * @param cache The CacheMemory object to read from.
    */
    public static void smallTest(CacheMemory cache) {
        boolean[] address;
        boolean[] toWrite;

        // Memory address in Lab 8 is the line address.
        // Add two bits to the least significant side of the Lab 8
        // address to get the memory Byte address.

        address = Binary.uDecToBin(4L, 32); // Operation 1: address 000100
        cache.readWord(address);

        address = Binary.uDecToBin(16L, 32); // Operation 2: address 010000
        cache.readWord(address);

        address = Binary.uDecToBin(24L, 32); // Operation 3: address 011000
        cache.readWord(address);

        address = Binary.uDecToBin(40L, 32); // Operation 4: address 101000
        cache.readWord(address);

        address = Binary.uDecToBin(60L, 32); // Operation 5: address 111100
        cache.readWord(address);

        address = Binary.uDecToBin(32L, 32); // Operation 6: address 100000
        toWrite = Binary.uDecToBin(32L, 32);
        cache.writeWord(address,toWrite);

        address = Binary.uDecToBin(0L, 32); // Operation 7: address 000000
        cache.readWord(address);

        address = Binary.uDecToBin(52L, 32); // Operation 8: address 110100
        cache.readWord(address);

        address = Binary.uDecToBin(28L, 32); // Operation 9: address 011100
        cache.readWord(address);

        address = Binary.uDecToBin(0L, 32); // Operation 10: address 000000
        toWrite = Binary.uDecToBin(0L, 32);
        cache.writeWord(address,toWrite);

        address = Binary.uDecToBin(32L, 32); // Operation 11: address 100000
        cache.readWord(address);

        address = Binary.uDecToBin(44L, 32); // Operation 12: address 101100
        cache.readWord(address);

        address = Binary.uDecToBin(0L, 32); // Operation 13: address 000000
        cache.readWord(address);

        address = Binary.uDecToBin(60L, 32); // Operation 14: address 111100
        cache.readWord(address);


    }

}
