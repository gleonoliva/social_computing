import java.util.*;
import java.util.stream.Collectors;

public class KM {

    private final boolean DEBUG = true;

    int[][] matrix;
    int[][] assign;
    int[][] eqGraph;
    
    int[] xLabeling;
    int[] yLabeling;
    
    public KM(int[][] matrix) {
        this.matrix = matrix;
        this.assign = new int[matrix.length][matrix.length];
        this.eqGraph = new int[matrix.length][matrix.length];
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
        
        if (DEBUG) {
            System.out.println("X Labels:");
            System.out.println(Arrays.toString(xLabeling));
            
            System.out.println("Y Labels:");
            System.out.println(Arrays.toString(yLabeling));
        }
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
            _edges.add(p);
        }

        public void addToS(HashSet<Integer> vertices) {
            _S.addAll(vertices);
        }

        public void addToT(HashSet<Integer> vertices) {
            _T.addAll(vertices);
        }

        public Set<Integer> getS() {
            return _S;
        }

        public Set<Integer> getT() {
            return _T;
        }

        public int getCost() {
            return _edges.stream()
                .mapToInt(p -> matrix[p.row][p.col])
                .sum();
        }

        public int getU() {
            Set<Integer> matchedX = _edges.stream()
                .mapToInt(p -> p.row)
                .collect(Collectors.toCollection(HashSet::new));
            
            Set<Integer> allX = IntStream.range(0, matrix.length)
                .collect(Collectors.toCollection(HashSet::new));

            allX.removeAll(matchedX);
            
            // There must be at least one item
            int u = allX.iterator().next();
            
            return u;
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
        
        public int matchedWith(int axis, int id) {
            
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getCost() + "\n");
            for (Pair p : _edges) {
                sb.append(String.format("(%d,%d)\n", p.row, p.col));
            }
            return sb.toString();
        }
    }

    private HashSet<Pair> eqGraphEdges = new HashSet<Pair>();
    
    private void computeEqualityGraph() {
        eqGraphEdges.clear();
        for (int i = 0; i < eqGraph.length; i++) {
            for (int j = 0; j < eqGraph[i].length; j++) {
                if (matrix[i][j] == xLabeling[i] + yLabeling[j]) {
                    eqGraphEdges.add(new Pair(i, j));
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
    
    private Set<Integer> getNeighbors(Set<Integer> s) {
        // N_l(S)
        return eqGraphEdges.stream()
            .filter(edge -> s.contains(edge.row))
            .mapToInt(edge -> edge.col)
            .collect(Collectors.toCollection(HashSet::new));
    }
    
    private int computeAlpha(Set<Integer> s, Set<Integer> t) {
        Set<Integer> allY =-IntStream.range(0, matrix.length)
            .collect(Collectors.toCollection(HashSet::new));
        allY.removeAll(t);
        
        int min = Integer.MAX_VALUE;
        for (int x : s) {
            for (int y : allY) {
                min = Math.min(min, xLabeling[x] + yLabeling[y] - matrix[x][y]);
            }
        }
        return min;
    }
    
    private void updateLabeling(int alpha, Set<Integer> s, Set<Integer> t) {
        for (int x : s) {
            xLabeling[x] -= alpha;
        }
        
        for (int y : t) {
            yLabeling[y] += alpha;
        }
    }
    
    private void flipAugmentingPath(Set<Integer> s, Set<Integer> t) {
        
    }
    

    public String run() {
        
        // 1. Generate initial labeling l and matching M in E_l
        findInitialFeasibleLabeling();
        computeEqualityGraph();
        generateInitialMatching();
        
        // 2. If M perfect stop!
        while(!matching.isPerfect()) {
        
            int u = matching.getU(); // free vertex
            Set<Integer> S = matching.getS();
            Set<Integer> T = matching.getT();

            S.add(u);

            Set<Integer> neighbors = getNeighbors(S);

            if (neighbors.equals(T)) {
                int alpha = computeAlpha(S, T);
                updateLabeling(alpha, S, T);
                computeEqualityGraph();
            } else {
                neighbors.removeAll(T);
                int y = neighbors.iterator().next();
                int xMatching = matching.matchedWith(1, y);

                if (xMatching >= 0) {
                    // y is matched, so extend alt tree.
                    S.add(xMatching);
                    T.add(y); // Go to 3
                } else {
                    // y is free
                    T.add(y);
                    flipAugmentingPath(S, T); // Go to 2
                    S.clear();
                    T.clear();
                }
            }
        }

        return mathing.toString();
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // Number of rows and column
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                matrix[i][j] = in.nextInt();
                
        KM km = new KM(matrix);

        String result = km.run();
        System.out.print(result);
    }

}
