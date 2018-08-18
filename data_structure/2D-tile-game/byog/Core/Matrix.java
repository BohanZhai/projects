package byog.Core;

public class Matrix {
    private int[][] body;
    private int height;
    private int width;

    Matrix(int w, int h) {
        body = new int[w][h];
        height = h;
        width = w;
    }
    Matrix(int[][] m) {
        body = m;
        height = m[0].length;
        width = m.length;
    }
    // i row, j column
    public int getitem(int i, int j) {
        return body[i][j];
    }
    public int getHeigh() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public void givenvalue(int i, int j, int value) {
        body[i][j] = value;
    }

    /** add another matrix onto this*/
    public void matrixadding(Matrix another) {
        if (this.getHeigh() != another.getHeigh()
                || this.getWidth() != another.getWidth()) {
            throw new RuntimeException("matrix should be same size");
        }
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                this.givenvalue(i, j, (getitem(i, j) + another.getitem(i, j)));
            }
        }
    }

    public void print() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(getitem(i, j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
