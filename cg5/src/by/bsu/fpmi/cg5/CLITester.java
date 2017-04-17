package by.bsu.fpmi.cg5;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class CLITester {

    public static void test() {
       /* PixelMatrix matrix = new PixelMatrix();
        matrix.setSize(10);
        matrix.calculateLine(PixelMatrix.BRESENHAM_ALGO, 1, 3, 9, 9);

        for (int i = 0; i < matrix.getSize(); i++) {
            for (int j = 0; j < matrix.getSize(); j++) {
                System.out.print("" + (matrix.hasPixel(i, j) ? 1 : 0) + "\t");
            }
            System.out.println();
        }*/

        PixelMatrix matrix = new PixelMatrix();
        matrix.setSize(15);
        matrix.calculateCircle(7, 7, 4);

        for (int i = 0; i < matrix.getSize(); i++) {
            for (int j = 0; j < matrix.getSize(); j++) {
                System.out.print("" + (matrix.hasPixel(i, j) ? 1 : 0) + "\t");
            }
            System.out.println();
        }
    }
}
