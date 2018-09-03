import java.util.*;
import java.util.stream.Collectors;

public class KM {

    int[][] matrix;
    int[][] assign;
    int[][] eqGraph;

    ArrayList<Solution> possibleSolutions;
    
    int[] xLabeling;
    int[] yLabeling;
    
    public KM(int[][] matrix) {
        this.matrix = matrix;
        this.assign = new int[matrix.length][matrix.length];
        this.eqGraph = new int[matrix.length][matrix.length];
        this.possibleSolutions = new ArrayList<>();
        
        for (int i = 0; i < assign.length; i++) {
            copyTo(assign[i], matrix[i]);
        }
    }
    
    private void findInitialFeasibleLabeling() {
        xLabeling = new int[matrix.length];
        yLabeling = new int[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            int max = Integer.MIN_VALUE;
            for (int j = 0; j < matrix[i].length; j++) {
                max = Math.max(matrix[i][j], max);
            }
            xLabeling[i] = max;
        }
        
        System.out.println("X Labels:");
        System.out.println(Arrays.toString(xLabeling));
        
        System.out.println("Y Labels:");
        System.out.println(Arrays.toString(yLabeling));
    }

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
    
    class Solution implements Cloneable {
        HashSet<Pair> _solution;

        public Solution() {
            _solution = new HashSet<>();
        }

        public void addPair(Pair p) {
            _solution.add(p);
        }

        public void removePair(Pair p) {
            _solution.remove(p);
        }

        public boolean isRepOk() {
            HashSet<Integer> x = new HashSet<>();
            HashSet<Integer> y = new HashSet<>();

            for (Pair p : _solution) {
                x.add(p.row);
                y.add(p.col);
            }

            return x.size() == y.size();
        }
        
        public int computeCost() {
            int cost = 0;
            for (Pair p : _solution) {
                cost += matrix[p.row][p.col];
            }
            return cost;
        }

        public Object clone() {
            Solution clone = null;
            try {
                clone = (Solution) super.clone();
                clone._solution = (HashSet<Pair>)this._solution.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return clone;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("%d\n", computeCost()));
            for (Pair p : _solution) {
                sb.append(String.format("(%d, %d)\n", p.row + 1, p.col + 1));
            }

            return sb.toString();
        }
    }

    private int lowest(int[] array) {
        int val = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            val = Math.min(val, array[i]);
        }
        return val;
    }

    private void copyTo(int[] to, int[] from) {
        if (to.length != from.length) return;

        for(int i = 0;i < to.length; i++)
            to[i] = from[i];
    }
    
    private void findAllPossibleSolutions(int row, Solution solution) {

        if (row == matrix.length) {
            // We must have a solution here.
            if (solution.isRepOk()) {
                possibleSolutions.add((Solution)solution.clone());
            }
            return;
        }

        for (int col = 0; col < matrix.length; col++) {
            if (assign[row][col] == 0) {
                Pair p = new Pair(row, col);
                solution.addPair(p);
                
                findAllPossibleSolutions(row + 1, solution);
                solution.removePair(p); // Back-track
            }
        }
    }

    private Solution findOptimalSolution() {
        Solution optimalSolution = null;
        int optimalCost = Integer.MAX_VALUE;
        for (Solution solution : possibleSolutions) {
            int cost = solution.computeCost();
            if (cost < optimalCost) {
                optimalCost = cost;
                optimalSolution = solution;
            }
        }
        return optimalSolution;
    }

    private void findSolution() {
        Solution solution = new Solution();
        
        long start = System.currentTimeMillis();
        
        findAllPossibleSolutions(0, solution);
        
        long end = System.currentTimeMillis();
        
        Solution optimalSolution = findOptimalSolution();
        if (optimalSolution == null) {
            System.out.println("No solution found :(");
            return;
        }
        
        int solutionCost = optimalSolution.computeCost();
        
        System.out.printf("Kuhn Munkres found %d possible solutions.\n", possibleSolutions.size());
        
        System.out.printf("Optimal solution found in %d ms:\n", end - start);
        System.out.println(optimalSolution);
    }
    
    private Matching matching;
    
    private void generateInitialMatching() {
        matching = new Matching();
        
        for (int i = 0; i < eqGraph.length; i++)
            for (int j = 0; j < eqGraph.length; j++)
                if (eqGraph[i][j] != 0) {
                    Pair initialMatch = new Pair(i, j);
                    matching.addEdge(initialMatch);
                    return;
                }
    }
    
    class Matching {
        Set<Pair> _edges;
        Set<Integer> _S;
        Set<Integer> _T;
        
        public Matching() {
            _edges = new HashSet<Pair>();
            _S = new HashSet<>();
            _T = new HashSet<>();
        }
        
        public void addEdge(Pair p) {
            _S.add(p.row);
            
            _edges.add(p);
        }
        
        public boolean isPerfect() {
            boolean[] x = new boolean[matrix.length];
            boolean[] y = new boolean[matrix.length];
            for (Pair p : _edges) {
                x[p.row] = true;
                y[p.col] = true;
            }
            
            for (boolean b : x)
                if (!b) return false;
                
            for (boolean b : y)
                if (!b) return false;
                
            return true;
        }
        
        public Set<Integer> getFreeVertexS() {
            return _edges.stream().map(pair -> pair.row)
                .collect(Collectors.toCollection(HashSet::new));
        }
        
        public Set<Integer> getFreeVertexT() {
            return _edges.stream().map(p -> p.col)
                .collect(Collectors.toCollection(HashSet::new));
        }
    }
    
    private void computeEqualityGraph() {
        for (int i = 0; i < eqGraph.length; i++) {
            for (int j = 0; j < eqGraph[i].length; j++) {
                if (matrix[i][j] == xLabeling[i] + yLabeling[j]) {
                    eqGraph[i][j] = matrix[i][j];
                } else {
                    eqGraph[i][j] = 0;
                }
            }
        }
        
        System.out.println("Equality Graph:");
        for (int[] row : eqGraph)
            System.out.println(Arrays.toString(row));
    }
    
    

    public void run() {
        
        // 1. Generate initial labeling l and matching M in E_l
        findInitialFeasibleLabeling();
        computeEqualityGraph();
        generateInitialMatching();
        
        // 2. If M perfect stop!
        while(!matching.isPerfect()) {
        
            if ()
        
            return;
        }

//        // Step one
//        for (int i = 0 ; i < matrix.length; i++ ){
//            int low = Arrays.stream(matrix[i]).min().getAsInt();
//            for (int j = 0; j < matrix[i].length; j++) {
//                assign[i][j] -= low;
//            }
//        }

//        // Step two
//        for (int i = 0; i < assign.length; i++) {
//            int min = Integer.MAX_VALUE;
//            for (int j = 0; j < assign.length; j++) {
//                min = Math.min(min, matrix[j][i]);
//            }
//            
//            for (int j = 0; j < assign.length; j++) {
//                assign[j][i] = Math.max(0, assign[j][i] - min);
//            }
//        }

//        // Step three
//        findSolution();
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        System.out.println("Hola Kuhn-Munkres");
        int n = in.nextInt(); // Number of rows and column
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                matrix[i][j] = in.nextInt();
                
        KM km = new KM(matrix);

        km.run();
    }

}
