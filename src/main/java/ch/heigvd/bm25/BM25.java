package ch.heigvd.bm25;

import ch.heigvd.bm25.utils.RankingResult;
import ch.heigvd.bm25.utils.Stopword;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BM25 {
    private final double K1 = 1.2;
    private final double B = 0.75;

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


    public void buildIndex(ArrayList<ArrayList<String>> corpusTokens) {

    }

    public ArrayList<RankingResult> retrieveTopK(ArrayList<String> queryTokens, int k) {
        return new ArrayList<>();
    }

}
