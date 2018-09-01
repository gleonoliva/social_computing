import java.util.*;

public class KM {

    int[][] matrix;
    int[][] assign;
    
    ArrayList<HashSet<Pair>> possibleSolutions;

    class Pair {
        int row, col;
        
        public Pair(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        public String toString() {
            return String.format("[%d => %d]", row, col);
        } 
        
    }

    private int lowest(int[] array) {
        int val = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            val = Math.min(val, array[i]);
        }
        return val;
    }
    
    private boolean isValidSolution(HashSet<Pair> solution) {
        HashSet<Integer> x = new HashSet<>();
        HashSet<Integer> y = new HashSet<>();
        
        for (Pair p : solution) {
            x.add(p.row);
            y.add(p.col);
        }
        
        return x.size() == y.size();
    }
    
    private void copyTo(int[] to, int[] from) {
        if (to.length != from.length) return;
        
        for(int i = 0;i < to.length; i++)
            to[i] = from[i];
    }
    
    private void findSolutionCore(int row, HashSet<Pair> solution) {
    
        if (row == matrix.length) {
            // We must have a solution here.
            if (isValidSolution(solution)) {
                possibleSolutions.add((HashSet<Pair>)solution.clone());
            }
            return;
        }
    
        for (int col = 0; col < matrix.length; col++) {
            if (assign[row][col] == 0) {
                Pair p = new Pair(row, col);
                solution.add(p);
                
                findSolutionCore(row + 1, solution);
                solution.remove(p);
            }
        }
    }
    
    private HashSet<Pair> findOptimalSolution() {
        HashSet<Pair> optimalSolution = null;
        int optimalCost = Integer.MAX_VALUE;
        for (HashSet<Pair> solution : possibleSolutions) {
            int cost = computeSolutionCost(solution);
            if (cost < optimalCost) {
                optimalCost = cost;
                optimalSolution = solution;
            }
        }
        return optimalSolution;
    }
    
    private int computeSolutionCost(HashSet<Pair> solution) {
        int cost = 0;
        for (Pair p : solution) {
            cost += matrix[p.row][p.col];
        }
        return cost;
    }
    
    private void findSolution() {
        HashSet<Pair> solution = new HashSet<>();
        
        long start = System.currentTimeMillis();
        
        findSolutionCore(0, solution);
        
        long end = System.currentTimeMillis();
        
        HashSet<Pair> optimalSolution = findOptimalSolution();
        if (optimalSolution == null) {
            System.out.println("No solution found :(");
            return;
        }
        
        int solutionCost = computeSolutionCost(optimalSolution);
        
        System.out.printf("Kuhn Munkres found %d possible solutions.\n", possibleSolutions.size());
        
        System.out.printf("Optimal solution found in %d ms:\nCost: %d\n", end -start, solutionCost);
        for (Pair p : optimalSolution) {
            System.out.println(p);
        }
               
    }

    public void runKuhnMunkresAlgorithm(int[][] matrix) {
        
        this.matrix = matrix;
        this.assign = new int[matrix.length][matrix.length];
        this.possibleSolutions = new ArrayList<>();
        
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
        
        // Step two
        for (int i = 0; i < assign.length; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < assign.length; j++) {
                min = Math.min(min, matrix[j][i]);
            }
            
            for (int j = 0; j < assign.length; j++) {
                assign[j][i] = Math.max(0, assign[j][i] - min);
            }       
        }
        
        // Step three
        findSolution();
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
