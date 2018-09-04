import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.*;

public class KM {

    private final boolean DEBUG = true;

    int[][] matrix;
    
    int[] xLabeling;
    int[] yLabeling;
    
    public KM(int[][] matrix) {
        this.matrix = matrix;
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
        
        Pair initialMatch = eqGraphEdges.iterator().next(); // First @ random
        matching.addEdge(initialMatch);
    }
    
    class Matching {
        Set<Pair> _edges;
        
        public Matching() {
            _edges = new HashSet<Pair>();
        }
        
        public void addEdge(Pair p) {
            _edges.add(p);
        }

        public int getCost() {
            return _edges.stream()
                .mapToInt(p -> matrix[p.row][p.col])
                .sum();
        }

        public int getU() {
            Set<Integer> matchedX = _edges.stream()
                .mapToInt(p -> p.row)
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));
            
            Set<Integer> allX = IntStream.range(0, matrix.length)
                .boxed()
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
            if (axis == 0) {
                // x
                for (Pair p : _edges) {
                    if (p.row == id)
                        return p.col;
                }
                
            } else if (axis == 1) {
                // y
                for (Pair p : _edges) {
                    if (p.col == id)
                        return p.row;
                }
            }
            return -1;
        }
        
        public void flipAugmentingPath(List<Integer> s, List<Integer> t) {
            HashSet<Pair> altTree = new HashSet<>();
            boolean i_turn = true;
            for (int i = 0, j = 0; i < s.size();) {
                altTree.add(new Pair(s.get(i), t.get(j)));
                if (i_turn) {
                    i++;
                    i_turn = false;
                } else {
                    j++;
                    i_turn = true;
                }
            }
        
            Set<Pair> matchingsInAltTree = (Set<Pair>) altTree.clone();
            matchingsInAltTree.retainAll(_edges);
            
            Set<Pair> newMatchings = (Set<Pair>) altTree.clone();
            newMatchings.removeAll(_edges);
            
            _edges.removeAll(matchingsInAltTree);
            _edges.addAll(newMatchings);
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
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == xLabeling[i] + yLabeling[j]) {
                    eqGraphEdges.add(new Pair(i, j));
                }
            }
        }
        
        System.out.println("Equality Graph:");
        System.out.println(eqGraphEdges);
    }
    
    private Set<Integer> getNeighbors(List<Integer> s) {
        // N_l(S)
        return eqGraphEdges.stream()
            .filter(edge -> s.contains(edge.row))
            .mapToInt(edge -> edge.col)
            .boxed()
            .collect(Collectors.toCollection(HashSet::new));
    }
    
    private int computeAlpha(List<Integer> s, List<Integer> t) {
        Set<Integer> allY = IntStream.range(0, matrix.length)
            .boxed()
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
    
    private void updateLabeling(int alpha, List<Integer> s, List<Integer> t) {
        for (int x : s) {
            xLabeling[x] -= alpha;
        }
        
        for (int y : t) {
            yLabeling[y] += alpha;
        }
    }

    public String run() {
        List<Integer> S = new ArrayList<>(),
                      T = new ArrayList<>();
        int state = 1;
        boolean exit = false;
        Set<Integer> neighbors;
        
        while(!exit) {
            switch(state) {
                case 1: // Generate initial labeling l and matching M in E_l
                    findInitialFeasibleLabeling();
                    computeEqualityGraph();
                    generateInitialMatching();
                    state = 2;
                    break;
                case 2:
                    if (matching.isPerfect()) {
                        exit = true;
                        break;
                    }
                    int u = matching.getU(); // free vertex
                    S = new ArrayList<>();
                    T = new ArrayList<>();

                    S.add(u);
                    state = 3;
                    break;
                case 3:
                    neighbors = getNeighbors(S);
                    if (neighbors.equals(T)) {
                        int alpha = computeAlpha(S, T);
                        updateLabeling(alpha, S, T);
                        computeEqualityGraph();
                    }
                    state = 4;
                    break;
                case 4:
                    neighbors = getNeighbors(S);
                    neighbors.removeAll(T);
                    int y = neighbors.iterator().next();
                    int xMatching = matching.matchedWith(1, y);

                    if (xMatching >= 0) {
                        // y is matched, so extend alt tree.
                        S.add(xMatching);
                        T.add(y);
                        state = 3;
                    } else {
                        // y is free
                        T.add(y);
                        matching.flipAugmentingPath(S, T); // Go to 2
                        state = 2;
                    }
                    break;
                default:
                    throw new RuntimeException("Wring state " + state);
            }
        }

        return matching.toString();
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
