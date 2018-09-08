import org.junit.Test;

import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class KMTests {
    
    private int[][] loadMatrix(String filePath) {
        Scanner in = new Scanner(getClass().getResourceAsStream(filePath));

        int n = in.nextInt(); // Number of rows and column
        
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                matrix[i][j] = in.nextInt();
                
        return matrix;
    }

    private boolean equivalentResults(String expect, String actual) {
        HashSet<String> expectedLines =
                new HashSet<String>(Arrays.asList(expect.split("\\s")));
        HashSet<String> actualLines =
                new HashSet<String>(Arrays.asList(actual.split("\\s")));

        return expectedLines.containsAll(actualLines) &&
                actualLines.containsAll(expectedLines);
    }

    private String loadResult(String filePath) {
        Scanner in = new Scanner(getClass().getResourceAsStream(filePath));

        StringBuffer sb = new StringBuffer();

        while (in.hasNext()) {
            sb.append(in.next() + "\n");
        }

        return sb.toString();
    }
    
    @Test
    public void testInput0() {

        String inputFilePath = "input.txt";
        
        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = "23\n(1,1)\n(2,3)\n(3,2)\n";

        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInput1() {
        String inputFilePath = "in1.txt";
        String outputFilePath = "test1_result.txt";

        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = loadResult(outputFilePath);

        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInput2() {
        String inputFilePath = "test2.txt";
        String outputFilePath = "test2_result.txt";

        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = loadResult(outputFilePath);

        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInput3() {
        String inputFilePath = "test3.txt";
        String outputFilePath = "test3_result.txt";

        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = loadResult(outputFilePath);

        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInput4() {
        String inputFilePath = "test4.txt";
        String outputFilePath = "test4_result.txt";

        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = loadResult(outputFilePath);

        assertTrue(equivalentResults(expected, result));
    }

    @Test
    public void testInputM3() {
        String inputFilePath = "M3.txt";
        String outputFilePath = "M3_result.txt";

        int[][] m = loadMatrix(inputFilePath);
        assertNotNull(m);
        KM km = new KM(m);
        String result = km.run();
        String expected = loadResult(outputFilePath);

        assertTrue(equivalentResults(expected, result));
    }
}
