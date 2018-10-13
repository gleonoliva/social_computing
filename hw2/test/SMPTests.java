import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class SMPTests {

    private static boolean equivalentResults(String expect, String actual) {
        HashSet<String> expectedLines =
                new HashSet<>(Arrays.asList(expect.split("\\s")));
        HashSet<String> actualLines =
                new HashSet<>(Arrays.asList(actual.split("\\s")));

        return expectedLines.containsAll(actualLines) &&
                actualLines.containsAll(expectedLines);
    }

    private static String loadResult(InputStream inStream) {
        Scanner in = new Scanner(inStream);
        StringBuffer sb = new StringBuffer();

        while (in.hasNext()) {
            sb.append(in.next() + "\n");
        }

        return sb.toString();
    }

    private SMP setup(InputStream inputFileStream, String optimality) {
        Scanner in = new Scanner(inputFileStream);
        int n = in.nextInt();
        int [][] mlist = new int[n][n];
        int [][] wlist = new int[n][n];

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                mlist[i][j] = in.nextInt();

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                wlist[i][j] = in.nextInt();

        return new SMP(mlist, wlist, optimality);
    }

    @Test
    public void test1() {
        SMP smp = setup(getClass().getResourceAsStream("input.txt"), "m");
        String result = smp.run();
        String expected = loadResult(getClass().getResourceAsStream("m_result.txt"));
        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void test2() {
        SMP smp = setup(getClass().getResourceAsStream("input.txt"), "w");
        String result = smp.run();
        String expected = loadResult(getClass().getResourceAsStream("w_result.txt"));
        assertTrue(equivalentResults(expected, result));
    }
}
