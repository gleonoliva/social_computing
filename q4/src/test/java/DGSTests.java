import org.junit.Test;
import utils.TestHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DGSTests {

    private void testBase(String inputPath, String outputPath) {
        int[][] m = TestHelper.loadMatrix(getClass().getResourceAsStream(inputPath));
        assertNotNull(m);
        DGS dgs = new DGS(m);
        String result = dgs.run();
        String expected = TestHelper.loadResult(getClass().getResourceAsStream(outputPath));

        assertTrue(TestHelper.equivalentResults(expected, result));
    }

    @Test
    public void testInput0() {
        String inputFilePath = "input.txt";
        String outputFilePath = "input_result.txt";

        testBase(inputFilePath, outputFilePath);
    }

    @Test
    public void testInput1() {
        String inputFilePath = "test1.txt";
        String outputFilePath = "test1_result.txt";

        testBase(inputFilePath, outputFilePath);
    }

    @Test
    public void testInput2() {
        String inputFilePath = "test2.txt";
        String outputFilePath = "test2_result.txt";

        testBase(inputFilePath, outputFilePath);
    }

    @Test
    public void testInput3() {
        String inputFilePath = "test3.txt";
        String outputFilePath = "test3_result.txt";

        testBase(inputFilePath, outputFilePath);
    }

    @Test
    public void testInput4() {
        String inputFilePath = "test4.txt";
        String outputFilePath = "test4_result.txt";

        testBase(inputFilePath, outputFilePath);
    }

    @Test
    public void testInputM3() {
        String inputFilePath = "M3.txt";
        String outputFilePath = "M3_result.txt";

        testBase(inputFilePath, outputFilePath);
    }
}
