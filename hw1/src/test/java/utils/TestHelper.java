package utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class TestHelper {


    public static int[][] loadMatrix(InputStream inStream) {
        Scanner in = new Scanner(inStream);

        int n = in.nextInt(); // Number of rows and column

        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrix[i][j] = in.nextInt();

        return matrix;
    }

    public static boolean equivalentResults(String expect, String actual) {
        HashSet<String> expectedLines =
                new HashSet<String>(Arrays.asList(expect.split("\\s")));
        HashSet<String> actualLines =
                new HashSet<String>(Arrays.asList(actual.split("\\s")));

        return expectedLines.containsAll(actualLines) &&
                actualLines.containsAll(expectedLines);
    }

    public static String loadResult(InputStream inStream) {
        Scanner in = new Scanner(inStream);
        StringBuffer sb = new StringBuffer();

        while (in.hasNext()) {
            sb.append(in.next() + "\n");
        }

        return sb.toString();
    }
}
