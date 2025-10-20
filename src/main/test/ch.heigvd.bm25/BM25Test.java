package ch.heigvd.bm25;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


public class BM25Test {

    @Test
    void emptyBM25() {
        BM25 bm25 = new BM25();
        assertNull(bm25.getIndex());
    }

}
