import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SMP {

    int[][] pref1;
    int[][] pref2;

    public SMP(int[][] pref1, int[][] pref2, String optimality) {
        if (optimality.compareTo("m") == 0) {
            this.pref1 = pref1;
            this.pref2 = pref2;
        } else {
            this.pref1 = pref2;
            this.pref2 = pref1;
        }
    }

    public String run() {
        return null;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage:\njava SMP <input_file> <m|w>");
            return;
        }

        Scanner input;
        try {
            input = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
            System.out.printf("File \"%s\" not found.\n", args[0]);
            return;
        }

        String optimality;
        if (args[1].compareTo("m") != 0 || args[1].compareTo("w") != 0) {
            System.out.println("You must specify an option for optimality.");
            System.out.println("m : Man optimal");
            System.out.println("w : Woman optimal");
            return;
        } else {
            optimality = args[1];
        }

        int n = input.nextInt();

        int[][] man_preferences = new int[n][n];
        int[][] woman_preferences = new int[n][n];

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                man_preferences[i][j] = input.nextInt();

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                woman_preferences[i][j] = input.nextInt();

        SMP smp = new SMP(man_preferences, woman_preferences, optimality);
        System.out.println(smp.run());
    }
}
