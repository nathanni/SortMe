# SortMe
Sort A extreme large file by character frequence

Compiled OS: macOS Sierra 10.12.1     JDK: 1.8.0_66-b17
   

Steps to run:

    1. $cd src
    2. $java CreateTestData <LINES> <CHAR_TO_GENERATE> 
    3. $java Sortme INPUT_FILENAME OUTPUT_FILENAME


Directly run "$java Sortme" to see help command



Thinking Process:

    Since we need to sort an extreme large file, we can't just load all contents into memory and sort.  We'd better use some divide-conquer strategies. Here I come up two solutions: External Sort and Map Reduce. 

External Sort:

    Algorithm: Quick sort + Merge k sorted arrays;

    Time Complexity:    k = FILE_SIZE / CHUNK_SIZE  (k files after split)  
                        s = AVG (LINE_LEN)      (average size of sentence)
                        n = CHUNK_SIZE / s      (n lines string in 1 file)
                         
                        O(k*nlogn*s) + O(s*n*k*logk)



    1.  Split the file into small pieces. (default chunk size is 1M)
        * Different JVM has different maximum stack size, I set default to 1M in order to avoid getting stack_over_flow in quicksort. If you still encounter stack_over_flow issue, please manually run with "-m 100000" to shrink chunk size.


    2.  Load the small pieces, quicksort lines and save to a temporary file.
        * save chunk files' names into arraylist as reference.


    3.  Merge sorted chunks and save to final file.



    P.S
        you may think I call getFrequency() (O(len) time) function very frequently when compare strings. Actually I tried to use hashmap<String, Integer> to record the relationship. But I noticed that hash() function in hashmap to hash string still takes relative O(len) time. So there is no need to use hashmap to record the frequency of each sentence.

    Further Optimize:
        1.  pre calculate user's memory space and set the maximum chunk size.
        2.  Add frequency in the end of each line for further reference (what minimize the call of getFrequency()). Delete this number when merge to final file.



Map Reduce:
I didn't implement this but I will briefly describe the strategy.

    1. Mapper uses a hashmap<String, Integer> to record the sentence - frequency relationship.

    2. Transfer to different node, each node is in charge of a range of frequency.

    2. Reducer is resposible for merging.

    

