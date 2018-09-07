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
        
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (getClass() != o.getClass()) return false;
            Pair p = (Pair)o;
            return this.row == p.row 
                    && this.col == p.col;
        }
        
        public int hashCode() {
            return toString().hashCode();
        }

    }
    
    private Matching matching;
    
    private void generateInitialMatching() {
        matching = new Matching();
        
        
        eqGraphEdges.stream()
            .sorted((lhs, rhs) -> matrix[rhs.row][rhs.col] - matrix[lhs.row][lhs.col])
            .forEach((edge) -> {
                if (matching.matchedWith(0, edge.row) < 0 
                    && matching.matchedWith(1, edge.col) < 0) {
                    
                    matching.addEdge(edge);
                }
            });
            
        System.out.println("Initial Matching: " + matching);
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
                
            System.out.println("getU MatchedX: " + matchedX);
            System.out.println("getU allX: " + allX);

            allX.removeAll(matchedX);
            
            System.out.println("getU free vertices: " + allX);
            
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

        /*
        * Augmenting path is valid if there are no alternating trees.
        * This function tries to find a node with more than one free candidate edge
        * not matched.
        * */
        private boolean isAugmentingPath(Set<Pair> altTree) {
            BitSet x = new BitSet(matrix.length); // O(n) bits
            BitSet y = new BitSet(matrix.length);

            for (Pair edge : altTree) {
                if (_edges.contains(edge)) continue;

                if (x.get(edge.row)) {
                    return false;
                } else {
                    x.flip(edge.row);
                }

                if (y.get(edge.col)) {
                    return false;
                } else {
                    y.flip(edge.col);
                }
            }

            return true;
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
            
            System.out.println("Current Matchings: " + _edges);
            System.out.println("Alt Tree: " + altTree);

            // Proposed alt augmenting path is restricted to E_l
            // (E_l n A_t)
            HashSet<Pair> altEqTree = (HashSet<Pair>) altTree.clone();
            altEqTree.retainAll(eqGraphEdges);
            // Maybe
            //altEqTree.addAll(eqGraphEdges);

            if (isAugmentingPath(altEqTree)) {
                Set<Pair> newMatchings = (Set<Pair>) altEqTree.clone();
                newMatchings.removeAll(_edges);

                Set<Pair> matchingsInAltTree = (Set<Pair>) altEqTree.clone();
                matchingsInAltTree.retainAll(_edges);

                _edges.removeAll(matchingsInAltTree);
                _edges.addAll(newMatchings);
            } else {
                System.out.println("\t Oh No! There is no augmenting path :(");
            }
            
            System.out.println("Matching after flipping\n" + this.toString());
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
        
        System.out.println("getNeighbors Edges filtered" + eqGraphEdges.stream()
            .filter(edge -> s.contains(edge.row)).collect(Collectors.toList()));
            
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
        
        System.out.println("X Labels: " + Arrays.toString(xLabeling));
        System.out.println("Y Labels: " + Arrays.toString(yLabeling));
    }

    public String run() {
        List<Integer> S = new ArrayList<>(),
                      T = new ArrayList<>();
        int state = 1;
        int u = -1;
        boolean exit = false;
        Set<Integer> neighbors;
        
        while(!exit) {
            switch(state) {
                case 1: // Generate initial labeling l and matching M in E_l
                    System.out.println("\nCase 1");
                    findInitialFeasibleLabeling();
                    computeEqualityGraph();
                    generateInitialMatching();
                    state = 2;
                    break;
                case 2:
                    System.out.println("\nCase 2:");
                    System.out.println("Matching: " + matching);
                    if (matching.isPerfect()) {
                        System.out.println("Matching is perfect!");
                        exit = true;
                        break;
                    }
                    System.out.println("Matching is not perfect");
                    u = matching.getU(); // free vertex
                    S = new ArrayList<>();
                    T = new ArrayList<>();

                    S.add(u);
                    System.out.println("S: " + S);
                    System.out.println("T: " + T);
                    state = 3;
                    break;
                case 3:
                    System.out.println("\nCase 3");
                    neighbors = getNeighbors(S);
                    System.out.println("Neighbors: " +  neighbors);
                    System.out.println("S: " + S); 
                    System.out.println("T: " + T);
                    if (neighbors.equals(new HashSet<Integer>(T))) {
                        int alpha = computeAlpha(S, T);
                        updateLabeling(alpha, S, T);
                        computeEqualityGraph();
                    }
                    state = 4;
                    break;
                case 4:
                    System.out.println("\nCase 4:");
                    neighbors = getNeighbors(S);
                    System.out.println("Neighbors: " + neighbors);
                    neighbors.removeAll(T);
                    System.out.println("S: " + S);
                    System.out.println("T: " + T);
                    int y = neighbors.iterator().next();
                    int xMatching = matching.matchedWith(1, y);

                    if (xMatching >= 0) {
                        System.out.printf("y%d is matched with x%d\n", y, xMatching);
                        S.add(xMatching);
                        T.add(y);
                        System.out.println("S: " + S);
                        System.out.println("T: " + T);
                        state = 3;
                    } else {
                        // y is free
                        System.out.printf("y%d is free!\n", y);
                        T.add(y);
                        System.out.println("T:" + T);
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
