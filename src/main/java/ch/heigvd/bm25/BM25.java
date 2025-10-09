package ch.heigvd.bm25;

import ch.heigvd.bm25.utils.RankingResult;
import ch.heigvd.bm25.utils.Stopword;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* This class represents search engine of BM25 algorithm
* */
public class BM25 {
    private final double K1 = 1.2;
    private final double B = 0.75;

    // Tokenize a line of text while throwing away inessential words
    public ArrayList<String> tokenize(String text) {
        // ref : https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#sum
        // ChatGPT's hint:
        // \b\w\w+\b matches entire words that are 2+ characters long.
        Pattern p = Pattern.compile("(?u)\\b\\w\\w+\\b");
        Matcher m = p.matcher(text.toLowerCase());

        ArrayList<String> tokens = new ArrayList<>();
        while (m.find()) {
            String token = m.group();

            if (!Stopword.eng.contains(token)) {
//                  apply stemmer somewhere here
                tokens.add(token);
            }
        }

        return tokens;
    }

    // Tokenize collection of documents
    // The same thing as BM25.tokenize(String)
    // This method just applies is over ArrayList
    public ArrayList<ArrayList<String>> tokenize(ArrayList<String> corpus) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();

        for (String text : corpus) {
            ArrayList<String> cleanDoc = tokenize(text);
            res.add(cleanDoc);
        }

        return res;
    }

    // Takes all tokens of given corpus
    // and builds index that latter will be used
    // to rank relevant documents with respect to query
    // Idiomatic use: BM25.buildIndex(BM25.tokenize(documents))
    public void buildIndex(ArrayList<ArrayList<String>> corpusTokens) {

    }

    // Ranks relevant documents with respect to query
    // Idiomatic use: results = BM25.retrieveTopK(BM25.tokenize(query))
    public ArrayList<RankingResult> retrieveTopK(ArrayList<String> queryTokens, int k) {
        return new ArrayList<>();
    }

}
