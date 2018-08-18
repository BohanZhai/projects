package byog.Core;
import org.junit.Test;

public class Testformatrix {
    @Test
    public void testmatrixadding() {
        int [][] a = {{1, 0, 0, 3}, {0, 9, 1, 0}, {-1, 0, 0, 1}};
        int [][] b = {{2, 0, 0, 5}, {3, 5, 2, 0}, {0, 0, 7, 2}};
        Matrix A = new Matrix(a);
        Matrix B = new Matrix(b);
        A.print();
        B.print();
        A.matrixadding(B);
        A.print();
    }
}
