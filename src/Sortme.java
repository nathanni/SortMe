import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 10/28/16.
 */
public class Sortme {

    public static int CHUNK_SIZE = 1000000; //default chunk size to 1M
    public static char target = 's'; //default target letter to 's'
    public static final String CHUNK_PATH = "./__output__/chunks/";


    public static void main(String[] args) {

        //validate no arguments
        if (args == null || args.length == 0) {
            System.out.println("--- Welcome to Nathan's magic sort room! ---");
            System.out.println("--- This tool is for sorting your file ---");
            displayUsage();
            System.exit(-1);
        }

        //validate wrong arguments number
        if (args.length < 2) {
            System.out.println("Wrong Usage!!!!");
            displayUsage();
            System.exit(-1);
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        //filenames validate
        if (!validFileNames(inputFileName, outputFileName)) {
            System.exit(-1);
        }


        //apply user's option
        try {
            for (int param = 2; param < args.length; param += 2) {
                if (args[param].equals("-c")) {
                    //users are only allowed to enter 1 character after -c
                    if (args[param + 1].length() > 1) {
                        throw new Exception();
                    }
                    target = args[param + 1].charAt(0);
                } else if (args[param].equals("-m")) {
                    int memorySize = Integer.parseInt(args[param + 1]);
                    //limit user's input
                    if (memorySize > 10000 && memorySize < 1000000) {
                        CHUNK_SIZE = Integer.parseInt(args[param + 1]);
                    } else {
                        CHUNK_SIZE /= 10; //if users enter -m without any following size, shrink chunk size and divide by 10
                    }
                } else {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            System.out.println("Wrong Usage!!!!");
            displayUsage();
            System.exit(-1);
        }


        //create temporary chunk path
        if (!new File(CHUNK_PATH).exists()) {
            new File(CHUNK_PATH).mkdirs();
        }


        long start = System.currentTimeMillis();

        printMemoryUsage();

        File inputFile = new File(inputFileName);

        //list to save chunks' names
        ArrayList<String> chunkList = new ArrayList<>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(inputFile));

            int index = 0;

            while (true) {

                //split, quick sort and save file
                String chunk = splitFile(bf, index++);

                //reach the end of file
                if (chunk.equals("END")) {
                    break;
                }

                chunkList.add(chunk);
            }

            //merge sorted chunks, write to output file
            String lastFileName = mergeAndOutput(chunkList);


            //rename to outputfile name
            File lastFile = new File(lastFileName);
            File outputFile = new File(outputFileName);
            boolean success = lastFile.renameTo(outputFile);

            long end = System.currentTimeMillis();

            if (success) {
                System.out.println("Sort completed!");
                System.out.println("Spent time: " + (end - start) / 1000 + " seconds.");
            }


            bf.close();

        } catch (IOException e) {
            System.out.println("Errors occur, please contact the administrator.");
        }


    }

    /* Display helper info in command line */
    private static void displayUsage() {
        System.out.println("--- Usage: ");
        System.out.println("    $java Sortme filenameA filenameB [-options] ");
        System.out.println("--- Where options include: ");
        System.out.println("    -c CHARACTER " + "   " + "SINGLE LETTER! Set alphabet's frequency you want to sort by");
        System.out.println("    -s CHUNK_SIZE_IN_BYTE " + "   " + "Customize Chunk size if stackoverflow occurs");
        System.out.println("--- Default Setting:");
        System.out.println("    CHARACTER: s");
        System.out.println("    SIZE: 1000000 bytes = 1 MB");
    }

    /* Validation for input file and output file */
    private static boolean validFileNames(String inputFileName, String outputFileName) {
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);

        if (!inputFile.exists()) {
            System.out.println("Input file not exists, please start over.");
            return false;
        }

        try {
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                System.out.println("Invalid Path, Please enter a valid output file name.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Invalid Path, please enter a valid output file name.");
            return false;
        } finally {

            //create was for testing, remember to delete
            outputFile.delete();
        }

        return true;
    }


    /* Merge k sorted files */
    private static String mergeAndOutput(ArrayList<String> chunkList) throws IOException {

        String lastFileName = mergeHelper(chunkList, 0, chunkList.size() - 1, new int[]{0});
        return lastFileName;
    }


    /* Merge k sorted helper */
    private static String mergeHelper(ArrayList<String> chunkList, int start, int end, int[] index) throws IOException {

        if (start >= end) {
            return chunkList.get(start);
        }

        int mid = start + (end - start) / 2;

        String leftFileName = mergeHelper(chunkList, start, mid, index);
        String rightFileName = mergeHelper(chunkList, mid + 1, end, index);

        String newFileName = merge(leftFileName, rightFileName, index);


        return newFileName;

    }


    /* Merge two files */
    private static String merge(String leftFileName, String rightFileName, int[] index) throws IOException {
        File leftFile = new File(leftFileName);
        File rightFile = new File(rightFileName);

        String newFileName = CHUNK_PATH + index[0]++;
        File newFile = new File(newFileName);

        BufferedWriter bfw = new BufferedWriter(new FileWriter(newFile));
        BufferedReader lbr = new BufferedReader(new FileReader(leftFile));
        BufferedReader rbr = new BufferedReader(new FileReader(rightFile));

        String leftLine = lbr.readLine();
        String rightLine = rbr.readLine();

        int leftFreq = getFrequency(leftLine);
        int rightFreq = getFrequency(rightLine);

        while (leftLine != null && rightLine != null) {

            if (leftFreq <= rightFreq) {
                bfw.write(leftLine + "\n");
                leftLine = lbr.readLine();
                leftFreq = getFrequency(leftLine);
            } else {
                bfw.write(rightLine + "\n");
                rightLine = rbr.readLine();
                rightFreq = getFrequency(rightLine);
            }

        }

        if (leftLine == null) {
            bfw.write(rightLine + "\n");
            while ((rightLine = rbr.readLine()) != null) {
                bfw.write(rightLine + "\n");
            }
        } else {
            bfw.write(leftLine + "\n");
            while ((leftLine = lbr.readLine()) != null) {
                bfw.write(leftLine + "\n");
            }
        }

        leftFile.delete();
        rightFile.delete();
        lbr.close();
        rbr.close();
        bfw.flush();
        bfw.close();

        return newFileName;


    }

    /* Split file into small chunks */
    private static String splitFile(BufferedReader bf, int index) throws IOException {
        List<String> lines = new ArrayList<>();
        int size = 0;
        String line = "";
        while ((line = bf.readLine()) != null) {
            lines.add(line);
            size += line.length() + 1;

            //threshold
            if (size >= CHUNK_SIZE) {
                break;
            }

        }

        if (lines.size() > 0) {
            String[] linesArr = new String[lines.size()];
            linesArr = lines.toArray(linesArr);

            String chunkName = sortAndSave(linesArr, index);
            return chunkName;
        } else {
            return "END";
        }


    }


    /* Quick sort lines in memory and output to chunk file */
    public static String sortAndSave(String[] lines, int index) throws IOException {

        quickSort(lines, 0, lines.length - 1); //quick sort

        String chunkName = CHUNK_PATH + "chunk" + index;

        //output sorted lines to temporary chunk file
        File chunk = new File(chunkName);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(chunk));
        for (int i = 0; i < lines.length; i++) {
            bfw.write(lines[i] + "\n");
        }

        bfw.close();

        return chunkName;
    }


    /* Quick sort */
    public static void quickSort(String[] lines, int lo, int hi) {
        if (lo >= hi) return;

        int j = partition(lines, lo, hi);

        quickSort(lines, lo, j - 1);
        quickSort(lines, j + 1, hi);

    }

    /* Time complexity: O(n * avg(line.len)) */
    public static int partition(String[] lines, int lo, int hi) {

        String pivot = lines[lo];

        int pivotFrequency = getFrequency(pivot);

        while (lo < hi) {
            while (lo < hi && getFrequency(lines[hi]) >= pivotFrequency) {
                hi--;
            }
            lines[lo] = lines[hi];
            while (lo < hi && getFrequency(lines[lo]) <= pivotFrequency) {
                lo++;
            }
            lines[hi] = lines[lo];

        }

        lines[lo] = pivot;

        return lo;


    }

    /* Function to calculate the frequency of target character */
    private static int getFrequency(String line) {
        if (line == null || line.length() == 0) return 0;
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }


    /* Display memory usage */
    public static void printMemoryUsage() {
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory());
    }


}
