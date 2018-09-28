import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DGS {

    int[][] weights;
    double[] prices;
    Integer[] owners;

    LinkedList<Integer> queue;


    public DGS(int[][] matrix) {
        weights = matrix;
        prices = new double[matrix.length];
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
            if (weights[i][j] - prices[j] > max) {
                max = weights[i][j] - prices[j];
                j_max = j;
            }
        }
        return j_max;
    }

    public String run() {
        final double DELTA = 1.0 / (prices.length + 1);
        queue = IntStream.range(0, weights.length)
                .boxed().collect(Collectors.toCollection(LinkedList::new));

        while(!queue.isEmpty()) {
            Integer newOwner = queue.removeFirst();

            int favItem = findMax(newOwner);

            if (weights[newOwner][favItem] - prices[favItem] >= 0) {
                if (owners[favItem] != null) {
                    queue.addLast(owners[favItem]);
                }

                owners[favItem] = newOwner;
                prices[favItem] += DELTA;
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

        long startTime = System.nanoTime();
        DGS dgs = new DGS(matrix);
        String result = dgs.run();
        System.out.print(result);
        System.out.printf("Total time takem for DGS is %d\n", System.nanoTime() - startTime);
    }
}
