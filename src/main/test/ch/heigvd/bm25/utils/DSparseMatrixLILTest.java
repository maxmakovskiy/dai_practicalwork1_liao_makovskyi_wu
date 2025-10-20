package ch.heigvd.bm25.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;


public class DSparseMatrixLILTest {

    @Test
    void constructEmptySparseMatrix() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);
        assertEquals(0.0, matrix.get(1, 2));
    }

    @Test
    void breakConstructionSparseMatrix() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> new DSparseMatrixLIL(-4, 4));

        assertEquals("nRows and nCols must be non-negative.", exc.getMessage());
    }

    @Test
    void get() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);

        double res = matrix.get(1,1);

        assertEquals(0.0, res);
    }

    @Test
    void breakingGet() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);
        IndexOutOfBoundsException exc = assertThrows(IndexOutOfBoundsException.class,
                () -> matrix.get(10, 4));

        assertEquals("Cannot find indices for rowIdx: 10 and colIdx: 4", exc.getMessage());
    }

    @Test
    void set() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);

        assertEquals(0.0, matrix.get(1,1));

        double val = 4.0;
        matrix.set(1, 1, val);

        assertEquals(val, matrix.get(1,1));
    }

    @Test
    void breakingSetOutOfBound() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);

        IndexOutOfBoundsException exc = assertThrows(IndexOutOfBoundsException.class,
                () -> matrix.set(1, 10, 4.0));

        assertEquals("Cannot set value at rowIdx: 1 and colIdx: 10", exc.getMessage());
    }

    @Test
    void strToMatrix() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(
            "nRows : 4\n" +
            "nCols : 4\n" +
            "Indices\n" +
            "0 : 0\n" +
            "1 : 1\n" +
            "2 : 2\n" +
            "3 : 3\n" +
            "Data\n" +
            "0 : 0.2\n" +
            "1 : 0.42\n" +
            "2 : 0.45\n" +
            "3 : 0.17\n"
        );

        assertEquals(0.2, matrix.get(0, 0));
        assertEquals(0.42, matrix.get(1, 1));
        assertEquals(0.45, matrix.get(2, 2));
        assertEquals(0.17, matrix.get(3, 3));
    }

    @Test
    void matrixToStr() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);

        matrix.set(0, 0, 1.0);
        matrix.set(1, 1, 2.0);
        matrix.set(2, 2, 3.0);
        matrix.set(3, 3, 4.0);

        String res = matrix.toString();

        assertNotEquals(0.0, res.length());
    }


}
