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

    private void testBase(String inputFileName, String resultFileName, String optimality) {
        SMP smp = setup(getClass().getResourceAsStream(inputFileName), optimality);
        String result = smp.run();
        String expected = loadResult(getClass().getResourceAsStream(resultFileName));
        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInputM() {
        testBase("input.txt", "m_result.txt", "m");
    }

    @Test
    public void testInputW() {
        testBase("input.txt", "w_result.txt", "w");
    }

    @Test
    public void test1M() {
        testBase("test1.txt", "test1_m_result.txt", "m");
    }

    @Test
    public void test1W() {
        testBase("test1.txt", "test1_w_result.txt", "w");
    }

    @Test
    public void test2M() {
        testBase("test2.txt", "test2_m_result.txt", "m");
    }

    @Test
    public void test2W() {
        testBase("test2.txt", "test2_w_result.txt", "w");
    }

    @Test
    public void test3M() {
        testBase("test3.txt", "test3_m_result.txt", "m");
    }

    @Test
    public void test3W() {
        testBase("test3.txt", "test3_w_result.txt", "w");
    }

    @Test
    public void test4M() {
        testBase("test4.txt", "test4_m_result.txt", "m");
    }

    @Test
    public void test4W() {
        testBase("test4.txt", "test4_w_result.txt", "w");
    }
}
