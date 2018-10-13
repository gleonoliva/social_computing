import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMP {

    int[][] pref1;
    int[][] pref2;
    int[] proposed;
    boolean[][] assignment;

    public SMP(int[][] pref1, int[][] pref2, String optimality) {
        if (optimality.compareTo("m") == 0) {
            this.pref1 = pref1;
            this.pref2 = pref2;
        } else {
            this.pref1 = pref2;
            this.pref2 = pref1;
        }
        proposed = new int[pref1.length];
        assignment = new boolean[pref1.length][pref1.length];
    }


    public String run() {
        ArrayList<Integer> freeList = IntStream.range(0, pref1.length).
                boxed().
                collect(Collectors.toCollection(ArrayList::new));

        while(!freeList.isEmpty()) {
            Integer freePerson = freeList.remove(0); // This could be randomized
            Integer preferredPerson = findPreferredPerson(freePerson);
            Integer pairedPerson = proposeToPerson(freePerson, preferredPerson);

            if (pairedPerson == null) {
                // How lucky! preferred person accepted!
                assign(freePerson, preferredPerson);
                continue;
            } else {
                Integer rejected;
                if (prefers(preferredPerson, freePerson, pairedPerson)) {
                    rejected = freePerson;
                } else {
                    rejected = pairedPerson;
                    assign(freePerson, preferredPerson);
                }
                freeList.add(rejected);
            }
        }


        return formatAssignment();
    }

    private String formatAssignment() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < assignment.length; i++) {
            for (int j = 0; j < assignment[i].length; j++) {
                if (assignment[i][j]) {
                    sb.append(String.format("(%d,%d)\n", i+1, j+1));
                }
            }
        }

        return sb.toString();
    }

    private void assign(Integer freePerson, Integer preferredPerson) {
        int f = freePerson.intValue();
        for (int i = 0; i < assignment.length; i++) {
            assignment[i][preferredPerson] = i == f? true : false;
        }
    }

    private boolean prefers(Integer preferredPerson, Integer freePerson, Integer pairedPerson) {
        return pref2[preferredPerson][freePerson] < pref2[preferredPerson][pairedPerson];
    }

    private int findPreferredPerson(int freePerson) {
        return pref1[freePerson][proposed[freePerson]] - 1;
    }

    private Integer proposeToPerson(Integer freePerson, Integer preferredPerson) {
        Integer pairedWith = null;
        for (int i = 0; i < pref1.length; i++) {
            if (assignment[i][preferredPerson]) {
                pairedWith = i;
                break;
            }
        }
        proposed[freePerson]++;
        return pairedWith;
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
        if (!"m".equals(args[1]) && !"w".equals(args[1])) {
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
