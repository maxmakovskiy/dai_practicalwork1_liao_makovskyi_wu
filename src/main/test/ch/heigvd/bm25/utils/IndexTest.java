package ch.heigvd.bm25.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import ch.heigvd.bm25.exceptions.IndexException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class IndexTest {

    @Test
    void constructEmptyIndex() {
        ArrayList<String> vocab = new ArrayList<>(
            List.of("like", "best", "plai", "can")
        );
        ArrayList<String> docs = new ArrayList<>(
            List.of("file1.txt", "file2.txt")
        );

        Index index = new Index(
            vocab.size(), docs.size(), vocab, docs
        );

        assertEquals(vocab.size(), index.getVocabSize());
        assertArrayEquals(vocab.toArray(), index.getVocabulary().toArray());

        assertEquals(docs.get(0), index.getDocumentName(0));
        assertEquals(docs.get(1), index.getDocumentName(1));

    }

    @Test
    void importingIndex() {
        String indexStr = "docNames\n" +
            "file1.txt|file2.txt\n" +
            "numOfDocs\n" +
            "2\n" +
            "vocabSize\n" +
            "4\n" +
            "vocabulary\n" +
            "like best plai can\n" +
            "matrixScores\n" +
            "nRows : 2\n" +
            "nCols : 4\n" +
            "Indices\n" +
            "0 : 0, 1\n" +
            "1 : 2, 3\n" +
            "Data\n" +
            "0 : 0.21, 0.45\n" +
            "1 : 0.1, 0.32\n";

        Index index = Index.importIndex(indexStr);

        assertEquals(4, index.getVocabSize());
        assertArrayEquals(
            new String[] {"like", "best", "plai", "can"},
            index.getVocabulary().toArray()
        );

        assertEquals(2, index.getNumOfDocs());
        assertEquals("file1.txt", index.getDocumentName(0));
        assertEquals("file2.txt", index.getDocumentName(1));
    }

    @Test
    void breakingImportingIndex() {
        String indexStr = "docNames\n" +
                "file1.txt|file2.txt\n" +
                "numOfDocs\n" +
                "2\n" +
                "vocabSize\n" +
                "4\n" +
                "like best plai can\n" +
                "matrixScores\n" +
                "nRows : 2\n" +
                "nCols : 4\n" +
                "Indices\n" +
                "0 : 0, 1\n" +
                "1 : 2, 3\n" +
                "Data\n" +
                "0 : 0.21, 0.45\n" +
                "1 : 0.1, 0.32\n";

        IndexException exc = assertThrows(IndexException.class,
                () -> Index.importIndex(indexStr));

        assertEquals("ill formated string : no vocab field", exc.getMessage());
    }

    @Test
    void indexToStr() {
        String indexStrExpected = "docNames\n" +
                "file1.txt|file2.txt\n" +
                "numOfDocs\n" +
                "2\n" +
                "vocabSize\n" +
                "4\n" +
                "vocabulary\n" +
                "like best plai can\n" +
                "matrixScores\n" +
                "nRows : 2\n" +
                "nCols : 4\n" +
                "Indices\n" +
                "0 : 0, 1\n" +
                "1 : 2, 3\n" +
                "Data\n" +
                "0 : 0.21, 0.45\n" +
                "1 : 0.1, 0.32\n";

        ArrayList<String> vocab = new ArrayList<>(
                List.of("like", "best", "plai", "can")
        );
        ArrayList<String> docs = new ArrayList<>(
                List.of("file1.txt", "file2.txt")
        );

        Index index = new Index(
                vocab.size(), docs.size(), vocab, docs
        );

        index.matrix.set(0, 0, 0.21);
        index.matrix.set(0, 1, 0.45);
        index.matrix.set(1, 2, 0.1);
        index.matrix.set(1, 3, 0.32);

        String res = index.toString();

        assertFalse(res.isEmpty());

        assertEquals(indexStrExpected, res);

    }

}
