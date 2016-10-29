import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Nathan on 10/28/16.
 */

/* Tool to generate testing file*/
public class CreateTestData {

    private static long numCount = 3000000;
    private static char target = 's';

    public static void main(String[] args) throws IOException {


        System.out.println("--- Usage: ");
        System.out.println("    $java CreateTestData LINES_OF_SENTENCES TARGET_CHAR_TO_GENERATE");
        System.out.println("--- Default: ");
        System.out.println("    3000000 s");



        try {
            for (int param = 0; param < args.length; param++) {
                if (param == 0) {
                    numCount = Long.parseLong(args[param]);
                } else if (param == 1) {
                    if (args[param].length() > 1) throw new Exception();
                    target = args[param].charAt(0);
                }
            }
        } catch (Exception e) {
            System.out.println("Wrong Usage!!! Please start over.");
            System.exit(-1);
        }

        System.out.println(" ----------- File is generating -----------");

        File file = new File("largefile");

        Random r = new Random();
        if (file.exists()) file.delete();
        BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < numCount; i++) {

            int num = 1 + r.nextInt(100);
            StringBuilder sb = new StringBuilder();
            while (num-- > 0) {
                sb.append(target);
            }
            if (i == numCount - 1) {
                bfw.write(sb.toString());
            } else {
                bfw.write(sb.toString() + "\n");
            }

        }
        bfw.close();

        System.out.println(" ----------- Complete! -----------");
    }
}
