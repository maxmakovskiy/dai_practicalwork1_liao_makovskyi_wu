package ch.heigvd.bm25.utils;

/**
 * Represents entity in the ranking results. Basically provides convenient way to represent
 * (document index, its score) pair
 */
public class RankingResult {

    private int docIdx;
    private double score;

    public RankingResult(int documentIndex, double score) {
        this.docIdx = documentIndex;
        this.score = score;
    }

    public int getDocIndex() {
        return this.docIdx;
    }

    public double getScore() {
        return this.score;
    }
}
