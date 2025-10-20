package ch.heigvd.bm25;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import ch.heigvd.bm25.utils.Index;
import ch.heigvd.bm25.utils.RankingResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class BM25Test {

    @Test
    void emptyBM25() {
        BM25 bm25 = new BM25();
        assertNull(bm25.getIndex());
    }

    @Test
    void tokenize() {
        String doc = "a cat is a feline and likes to eat bird";

        BM25 bm25 = new BM25();

        ArrayList<String> tokenized = bm25.tokenize(doc);

        assertArrayEquals(
            new String[] {"cat", "felin", "like", "eat", "bird"},
            tokenized.toArray()
        );
    }

    @Test
    void buildIndex() {
        ArrayList<String> docs = new ArrayList<>(List.of(
                "a cat is a feline and likes to eat bird",
                "a dog is the human's best friend and likes to play",
                "a bird is a beautiful animal that can fly"
        ));
        ArrayList<String> filenames = new ArrayList<>(List.of(
                "file1.txt", "file2.txt", "file3.txt"
        ));

        BM25 bm25 = new BM25();

        ArrayList<ArrayList<String>> corpus = bm25.tokenize(docs);

        assertNull(bm25.getIndex());

        bm25.buildIndex(corpus, filenames);

        Index index = bm25.getIndex();

        assertNotNull(index);
    }

    @Test
    void retrieveTopK() {
        ArrayList<String> vocab = new ArrayList<>(List.of(
            "like", "best", "plai", "can", "fly",
                "beauti", "cat", "bird", "friend", "eat",
                "anim", "dog", "human", "felin"
        ));
        ArrayList<String> docNames = new ArrayList<>(List.of(
                "file1.txt", "file2.txt", "file3.txt"
        ));

        Index index = new Index(
                vocab.size(), docNames.size(),
                vocab, docNames
        );

        index.matrix.set(0, 0, 0.2192);
        index.matrix.set(0, 6, 0.4575);
        index.matrix.set(0, 7, 0.2192);
        index.matrix.set(0, 9, 0.4575);
        index.matrix.set(0, 13, 0.4575);

        index.matrix.set(1, 0, 0.2032);
        index.matrix.set(1, 1, 0.4241);
        index.matrix.set(1, 2, 0.4241);
        index.matrix.set(1, 8, 0.4241);
        index.matrix.set(1, 11, 0.4241);
        index.matrix.set(1, 12, 0.4241);

        index.matrix.set(2, 3, 0.4575);
        index.matrix.set(2, 4, 0.4575);
        index.matrix.set(2, 5, 0.4575);
        index.matrix.set(2, 7, 0.2192);
        index.matrix.set(2, 10, 0.4575);

        BM25 bm25 = new BM25(index);

        ArrayList<String> query = new ArrayList<>(List.of(
                "anim", "human", "best", "friend"
        ));

        ArrayList<RankingResult> res = bm25.retrieveTopK(query, 3);

        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getDocIndex());
        assertEquals(2, res.get(1).getDocIndex());
    }

}
