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


}
