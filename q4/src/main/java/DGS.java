import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DGS {

    int[][] weights;
    double[] goods;
    Integer[] owners;

    LinkedList<Integer> queue;


    public DGS(int[][] matrix) {
        weights = matrix;
        goods = new double[matrix.length];
        owners = new Integer[matrix.length];
    }

    private String currentMatching() {
        StringBuffer sb = new StringBuffer();
        sb.append(getCost() + "\n");
        for (int j = 0; j < owners.length; j++) {
            if (owners[j] != null) {
                sb.append(String.format("(%d,%d)\n", owners[j] + 1, j + 1));
            }
        }
        return sb.toString();
    }

    private int getCost() {
        int cost = 0;
        for (int j = 0; j < owners.length; j++) {
            if (owners[j] != null) {
                cost += weights[owners[j]][j];
            }
        }

        return cost;
    }

    private int findMax(int i) {
        double max = Integer.MIN_VALUE;
        int j_max = -1;
        for (int j = 0; j < weights.length; j++) {
            if (weights[i][j] > max) {
                max = weights[i][j] - goods[j];
                j_max = j;
            }
        }
        return j_max;
    }

    public String run() {
        final double DELTA = 1.0 / (goods.length + 1);
        queue = IntStream.range(0, weights.length)
                .boxed().collect(Collectors.toCollection(LinkedList::new));

        while(!queue.isEmpty()) {
            Integer i = queue.removeFirst();

            int j = findMax(i);

            if (weights[i][j] - goods[j] >= 0) {
                if (owners[j] != null) {
                    queue.addLast(owners[j]);
                }
                owners[j] = i;
                goods[j] += DELTA;
            }
        }

        return currentMatching();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // Number of rows and column
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrix[i][j] = in.nextInt();

        DGS dgs = new DGS(matrix);

        String result = dgs.run();
        System.out.print(result);
    }
}
