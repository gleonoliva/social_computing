import java.util.*;

public class KM {


    private int lowest(int[] array) {
        int val = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            val = Math.min(val, array[i]);
        }
        return val;
    }
    
    private boolean isAssignmentValid(int[][] assignment) {
        return false;
    }
    
    private void printSolution(int[][] assignment, int[][] matrix) {
        
    }
    
    private void copyTo(int[] to, int[] from) {
        if (to.length != from.length) return;
        
        for(int i = 0;i < to.length; i++)
            to[i] = from[i];
    }

    public void runKuhnMunkresAlgorithm(int[][] matrix) {
    
        int[][] assign = new int[matrix.length][matrix.length];
        
        for (int i = 0; i < assign.length; i++) {
            copyTo(assign[i], matrix[i]);
        }
    
        // Step one
        for (int i = 0 ; i < matrix.length; i++ ){
            int low = Arrays.stream(matrix[i]).min().getAsInt();
            for (int j = 0; j < matrix[i].length; j++) {
                assign[i][j] -= low;
            }
        }
        
        if (isAssignmentValid(assign)) {
            printSolution(assign, matrix);
            return;
        }
        
        // Step two
        
        // Step three    
    }

    public static void main(String args[]) {
    
        Scanner in = new Scanner(System.in);
    
        System.out.println("Hola Kuhn-Munkres");
        
        int n = in.nextInt(); // Number of rows and column
        
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                matrix[i][j] = in.nextInt();
                
        KM km = new KM();      
                
        km.runKuhnMunkresAlgorithm(matrix);
    }
    
}
