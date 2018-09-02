import org.junit.Test;

import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class KMTests {
    
    private int[][] loadMatrix(String filePath) {
        
                    
        Scanner in = null;
        try {
            in = new Scanner(new File(filePath));
        } catch (FileNotFoundException fnfe) {
            return null;
        }

        int n = in.nextInt(); // Number of rows and column
        
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                matrix[i][j] = in.nextInt();
                
        return matrix;
    }
    
    @Test
    public void testInput0() {
        String inputFilePath = "input.txt";
        
        int[][] m = loadMatrix(inputFilePath);
        System.out.println("Is this the real life?");
        assertNotNull(m);
        KM km = new KM();
        km.runKuhnMunkresAlgorithm(m);
    }
}
