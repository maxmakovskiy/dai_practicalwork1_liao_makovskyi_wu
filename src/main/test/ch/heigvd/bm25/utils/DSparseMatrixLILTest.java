package ch.heigvd.bm25.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class DSparseMatrixLILTest {

    @Test
    void emptySparseMatrix() {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(4, 4);
        assertEquals(0.0, matrix.get(1, 2));
    }

}
