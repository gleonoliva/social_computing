import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.*;

/*
* Kuhn-Munkres Algorithm
* */
public class KM {

    int[][] matrix;

    int[] xLabeling;
    int[] yLabeling;
    private HashSet<Edge> eqGraphEdges = new HashSet<Edge>();
    private Matching matching;


    public KM(int[][] matrix) {
        this.matrix = matrix;
    }

    class Edge {
        int row, col;

        public Edge(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public String toString() {
            return String.format("(%d,%d)", row + 1, col + 1);
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (getClass() != o.getClass()) return false;
            Edge p = (Edge)o;
            return this.row == p.row
                    && this.col == p.col;
        }

        public int hashCode() {
            return toString().hashCode();
        }

    }

    class Matching {
        Set<Edge> _edges;

        public Matching() {
            _edges = new HashSet<Edge>();
        }

        public void addEdge(Edge p) {
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
            BitSet x = new BitSet(matrix.length);
            BitSet y = new BitSet(matrix.length);
            for (Edge p : _edges) {
                x.set(p.row);
                y.set(p.col);
            }

            boolean allXMatched = x.nextClearBit(0) < 0 || x.nextClearBit(0) >= matrix.length;
            boolean allYMatched = y.nextClearBit(0) < 0 || y.nextClearBit(0) >= matrix.length;

            return allXMatched && allYMatched;
        }

        public int matchedWith(int axis, int id) {
            if (axis == 0) {
                // x
                for (Edge p : _edges) {
                    if (p.row == id)
                        return p.col;
                }

            } else if (axis == 1) {
                // y
                for (Edge p : _edges) {
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
        private boolean isAugmentingPath(Set<Edge> altTree) {
            BitSet x = new BitSet(matrix.length); // O(n) bits
            BitSet y = new BitSet(matrix.length);

            for (Edge edge : altTree) {
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

        public void flipAugmentingPath(HashSet<Edge> altTree) {
            // Proposed alt augmenting path is restricted to E_l
            HashSet<Edge> altEqTree = (HashSet<Edge>) altTree.clone();
            altEqTree.retainAll(eqGraphEdges);
            // Maybe
            //altEqTree.addAll(eqGraphEdges);

            if (isAugmentingPath(altEqTree)) {
                Set<Edge> newMatchings = (Set<Edge>) altEqTree.clone();
                newMatchings.removeAll(_edges);

                Set<Edge> matchingsInAltTree = (Set<Edge>) altEqTree.clone();
                matchingsInAltTree.retainAll(_edges);

                _edges.removeAll(matchingsInAltTree);
                _edges.addAll(newMatchings);
            } else {
                String errorMsg = String.format("Oh No! There is no augmenting path :(\nAlternating Tree:%s\nEdges:%s\n",
                        altEqTree, _edges);
                throw new RuntimeException(errorMsg);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getCost() + "\n");
            for (Edge edge : _edges) {
                sb.append(edge.toString() + "\n");
            }
            return sb.toString();
        }
    }

    public String run() {
        List<Integer> S = new ArrayList<>(),
                T = new ArrayList<>();
        int state = 1;
        int u = -1;
        boolean exit = false;
        Set<Integer> neighbors;
        HashSet<Edge> altTree;

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
                    u = matching.getU(); // free vertex
                    S = new ArrayList<>();
                    T = new ArrayList<>();

                    S.add(u);
                    state = 3;
                    break;
                case 3:
                    neighbors = getNeighbors(S);
                    if (neighbors.equals(new HashSet<Integer>(T))) {
                        int alpha = computeAlpha(S, T);
                        updateLabeling(alpha, S, T);
                        computeEqualityGraph();
                    }
                    state = 4;
                    break;
                case 4:
                    int y = chooseY(S, T);
                    int xMatching = matching.matchedWith(1, y);

                    if (xMatching >= 0) {
                        S.add(xMatching);
                        T.add(y);
                        state = 3;
                    } else {
                        // y is free
                        T.add(y);
                        altTree = getAlternatingTree(S, T);
                        matching.flipAugmentingPath(altTree); // Go to 2
                        state = 2;
                    }
                    break;
                default:
                    throw new RuntimeException("Wring state " + state);
            }
        }

        return matching.toString();
    }

    Random r = new Random();

    private int chooseY(List<Integer> S, List<Integer> T) {
        Set<Integer> neighbors = getNeighbors(S);
        neighbors.removeAll(T);
        int length = neighbors.size();
        int y = neighbors.stream().skip(r.nextInt(length)).findFirst().get();
        return y;

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

    }

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
    }

    private void computeEqualityGraph() {
        eqGraphEdges.clear();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == xLabeling[i] + yLabeling[j]) {
                    eqGraphEdges.add(new Edge(i, j));
                }
            }
        }
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

    private HashSet<Edge> getAlternatingTree(List<Integer> s, List<Integer> t) {
        HashSet<Edge> altTree = new HashSet<>();
        boolean i_turn = true;
        for (int i = 0, j = 0; i < s.size();) {
            altTree.add(new Edge(s.get(i), t.get(j)));
            if (i_turn) {
                i++;
                i_turn = false;
                i_turn = false;
            } else {
                j++;
                i_turn = true;
            }
        }
        return altTree;
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
