package ch.heigvd.bm25.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


}
