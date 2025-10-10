package ch.heigvd.bm25;

import ch.heigvd.bm25.utils.RankingResult;
import ch.heigvd.bm25.utils.Stopword;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* This class represents search engine built on top of BM25 algorithm
* */
public class BM25 {
    private final double K1 = 1.2;
    private final double B = 0.75;

    /**
     * Tokenizes a line of text while igonring inessential words (ex: a/the/is/etc)
     * @param text line of text to tokenize
     * @return ArrayList of tokens
     * */
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

    /**
     * Tokenizes collection of documents
     * @param corpus collection of documents to tokenize
     * @return ArrayList of tokenized documents where each document is ArrayList itself
     * */
    public ArrayList<ArrayList<String>> tokenize(ArrayList<String> corpus) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();

        for (String text : corpus) {
            ArrayList<String> cleanDoc = tokenize(text);
            res.add(cleanDoc);
        }

        return res;
    }

    /**
     * Takes all tokens of given corpus and builds index that latter will be used
     * to rank relevant documents with respect to query
     * @param corpusTokens collection of documents that were tokenized
     * */
    public void buildIndex(ArrayList<ArrayList<String>> corpusTokens) {
        // build vocabulary
        ArrayList<String> vocab = buildVocabulary(corpusTokens);

        // create new index

        // compute matrix of scores so-called Index
        computeScoresMatrix(corpusTokens, vocab);
    }

    /**
     * Ranks relevant documents with respect to query
     * @param queryTokens user's query that has been already tokenized
     * @param k number of first most relevant documents
     * @return ArrayList of RankingResult that have been found with use of Index
     * with respect to user's query
     * @see RankingResult
     * @see Index
     * */
    public ArrayList<RankingResult> retrieveTopK(ArrayList<String> queryTokens, int k) {
        return new ArrayList<>();
    }

    /**
     * Collects all the uniques words from the corpus
     * @param corpusTokens collection of documents that were tokenized
     * @return ArrayList of unique words over all corpus so-called vocabulary
     * */
    private ArrayList<String> buildVocabulary(ArrayList<ArrayList<String>> corpusTokens) {
        ArrayList<String> allTokens = new ArrayList<>();

        for (ArrayList<String> doc : corpusTokens) {
            allTokens.addAll(doc);
        }

        // ref: https://stackoverflow.com/a/2235687
        HashSet<String> uniqueTokens = new HashSet<>(allTokens);

        return new ArrayList<>(uniqueTokens);
    }

    /**
     * Calculates score for each token inside each document and
     * latter builds matrix of scores NxM where N is number of documents
     * and M is number of tokens in the vocabulary
     * @param corpusTokens collection of documents that were tokenized
     * @param vocabulary collection of unique words over all documents inside the corpus
     * */
    private void computeScoresMatrix(
        ArrayList<ArrayList<String>> corpusTokens,
        ArrayList<String> vocabulary
    ) {
        // Compute avg doc len and number of docs
        double avgDocLen = computeAvgDocLength(corpusTokens);
        int numOfDocs = corpusTokens.size();

        // Step 1: Calculate the number of documents containing each token
        HashMap<String, Integer> docFreqs = calculateDocumentFrequencies(
                corpusTokens,
                vocabulary
        );

        // Step 2: Calculate the idf for each token using the document frequencies
        HashMap<String, Double> idf = calculateInverseDocumentFrequencies(
                docFreqs, numOfDocs
        );


        // Step 3 Calculate the BM25 scores for each token in each document
    }

    /**
     * Calculates average length of document inside corpus
     * @param corpusTokens collection of documents that were tokenized
     * @return average document length inside the corpus
     * */
    private double computeAvgDocLength(ArrayList<ArrayList<String>> corpusTokens) {
        double result = 0.0;

        for (ArrayList<String> docTokens : corpusTokens) {
            result += docTokens.size();
        }

        return result / corpusTokens.size();
    }

    /**
     * For each token in the vocabulary calculates number of document containing it
     * @param corpusTokens collection of documents that were tokenized
     * @param vocabulary collection of unique words over all documents inside the corpus
     * @return dictionary of (token, number of documents containing it) pairs
     * */
    private HashMap<String, Integer> calculateDocumentFrequencies(
        ArrayList<ArrayList<String>> corpusTokens,
        ArrayList<String> vocabulary
    ) {
        HashMap<String, Integer> docFrequencies = new HashMap<>();
        HashSet<String> vocabSet = new HashSet<>(vocabulary);

        for (String token : vocabSet) {
            // ref: https://stackoverflow.com/questions/4157972/how-to-update-a-value-given-a-key-in-a-hashmap
            docFrequencies.put(token, 0);
        }

        for (ArrayList<String> docTokens : corpusTokens) {
            // get intersection of unique tokens and the tokens in the document
            HashSet<String> sharedTokens = new HashSet<>(docTokens);
            sharedTokens.retainAll(vocabSet);

            // then we count number of docs for tokens in the intersection
            for (String token : sharedTokens) {
                docFrequencies.put(token, docFrequencies.get(token) + 1);
            }

        }

        return docFrequencies;
    }

    /**
     * Calculates inverse document frequency score for individual token
     * @param dfForToken document frequency for token of interest
     * @param numOfDocs number of documents in the corpus
     * @return idf score
     * */
    private double idfScore(int dfForToken, int numOfDocs) {
        return Math.log(
                ((numOfDocs - dfForToken + 0.5)  / (dfForToken + 0.5)) + 1.0
        );
    }


    /**
     * Calculates inverse document frequencies for each document in vocabulary
     * @param docFreqs dictionary of (token, number of times it appears of in each document) pairs
     * @param numOfDocs number of documents in the corpus
     * @return dictionary of (token, inverse document frequency) pairs
     * */
    private HashMap<String, Double> calculateInverseDocumentFrequencies(
        HashMap<String, Integer> docFreqs,
        int numOfDocs
    ) {
        HashMap<String, Double> idf = new HashMap<>();

        // ref: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
        for (HashMap.Entry<String, Integer> entry : docFreqs.entrySet()) {
            String token = entry.getKey();
            Integer dfForToken = entry.getValue();

            if (dfForToken != 0) {
                idf.put(token, idfScore(dfForToken, numOfDocs));
            }
        }

        return idf;
    }


}
