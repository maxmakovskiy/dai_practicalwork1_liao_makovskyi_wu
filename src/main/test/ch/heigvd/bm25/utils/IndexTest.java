package ch.heigvd.bm25.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
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

        index.getMatrix().set(0, 0, 0.21);
        index.getMatrix().set(0, 1, 0.45);
        index.getMatrix().set(1, 2, 0.1);
        index.getMatrix().set(1, 3, 0.32);

        String res = index.toString();

        assertFalse(res.isEmpty());

        assertEquals(indexStrExpected, res);
    }

    @Test
    void indexToJsonAndBack() throws JsonProcessingException {
        DSparseMatrixLIL matrix = new DSparseMatrixLIL(2, 4);
        int[] rowIndexes = new int[] {
                0, 0, 0, 1, 1
        };
        int[] columnIndexes = new int[] {
                0, 1, 3, 1, 2
        };
        double[] data = new double[] {
                1.0, 1.1, 1.3, 2.0, 2.1
        };

        for (int i = 0; i < rowIndexes.length; i++) {
            matrix.set(rowIndexes[i], columnIndexes[i], data[i]);
        }

        ArrayList<String> vocab = new ArrayList<>(
                List.of("like", "best", "plai", "can")
        );
        ArrayList<String> docNames = new ArrayList<>(
                List.of("file1.txt", "file2.txt")
        );

        Index srcIndex = new Index(
                vocab.size(), docNames.size(),
                vocab, docNames, matrix
        );

        String json = srcIndex.toJSON();
        assertFalse(json.isEmpty());

        Index dstIndex = Index.fromJSON(json);

        assertArrayEquals(vocab.toArray(), dstIndex.getVocabulary().toArray());
        assertEquals(docNames.get(0), dstIndex.getDocumentName(0));
        assertEquals(docNames.get(1), dstIndex.getDocumentName(1));
    }

}
