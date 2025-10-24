package ch.heigvd.bm25;

import ch.heigvd.bm25.utils.Index;
import ch.heigvd.bm25.utils.RankingResult;
import ch.heigvd.bm25.utils.Stopword;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.stemmer.PorterStemmer;

/*
 * This class represents search engine built on top of BM25 algorithm
 * */
public class BM25 {
    private final double K1 = 1.2;
    private final double B = 0.75;
    private Index index;

    /** An empty constructor for BM25 */
    public BM25() {
        this.index = null;
    }

    /**
     * Constructor that could restore state of BM25 from index
     *
     * @see Index
     */
    public BM25(Index index) {
        this.index = index;
    }

    /**
     * Returns index
     *
     * @see Index
     */
    public Index getIndex() {
        return this.index;
    }

    /**
     * Tokenizes a line of text while ignoring inessential words (ex: a/the/is/etc)
     *
     * @param text line of text to tokenize
     * @return ArrayList of tokens
     */
    public ArrayList<String> tokenize(String text) {
        // ref : https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#sum
        // ChatGPT's hint:
        // \b\w\w+\b matches entire words that are 2+ characters long.
        Pattern p = Pattern.compile("(?u)\\b\\w\\w+\\b");
        Matcher m = p.matcher(text.toLowerCase());

        PorterStemmer stemmer = new PorterStemmer();

        ArrayList<String> tokens = new ArrayList<>();
        while (m.find()) {
            String token = m.group();

            if (!Stopword.eng.contains(token)) {
                // apply stemmer somewhere here
                tokens.add(stemmer.stem(token));
            }
        }

        return tokens;
    }

    /**
     * Tokenizes collection of documents
     *
     * @param corpus collection of documents to tokenize
     * @return ArrayList of tokenized documents where each document is ArrayList itself
     */
    public ArrayList<ArrayList<String>> tokenize(ArrayList<String> corpus) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();

        for (String text : corpus) {
            ArrayList<String> cleanDoc = tokenize(text);
            res.add(cleanDoc);
        }

        return res;
    }

    /**
     * Takes all tokens of given corpus and builds index that latter will be used to rank relevant
     * documents with respect to query
     *
     * @param corpusTokens collection of documents that were tokenized
     */
    public void buildIndex(
            ArrayList<ArrayList<String>> corpusTokens, ArrayList<String> documentNames) {
        // build vocabulary
        ArrayList<String> vocab = buildVocabulary(corpusTokens);

        // create new index
        this.index = new Index(vocab, documentNames);

        // compute matrix of scores so-called Index
        computeScoresMatrix(corpusTokens, vocab);
    }

    /**
     * Ranks relevant documents with respect to query
     *
     * @param queryTokens user's query that has been already tokenized
     * @param k number of first most relevant documents
     * @return ArrayList of RankingResult that have been found with use of Index with respect to
     *     user's query
     * @see RankingResult
     * @see Index
     */
    public ArrayList<RankingResult> retrieveTopK(ArrayList<String> queryTokens, int k) {
        ArrayList<RankingResult> res = new ArrayList<>();

        for (int docIdx = 0; docIdx < this.index.getNumOfDocs(); docIdx++) {
            double docScore = 0.0;

            for (int tokenIdx = 0; tokenIdx < this.index.getVocabSize(); tokenIdx++) {
                if (queryTokens.contains(this.index.getVocabulary().get(tokenIdx))) {
                    docScore += this.index.getMatrix().get(docIdx, tokenIdx);
                }
            }

            if (docScore > 0.0) {
                res.add(new RankingResult(docIdx, docScore));
            }
        }

        // ref : https://stackoverflow.com/a/2784576
        // ref : https://stackoverflow.com/a/55722904
        res.sort(Comparator.comparing(RankingResult::getScore));
        Collections.reverse(res);

        k = Math.min(k, res.size());

        // ref : https://stackoverflow.com/a/16644841
        return new ArrayList<>(res.subList(0, k));
    }

    /**
     * Collects all the uniques words from the corpus
     *
     * @param corpusTokens collection of documents that were tokenized
     * @return ArrayList of unique words over all corpus so-called vocabulary
     */
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
     * Calculates score for each token inside each document and latter builds matrix of scores NxM
     * where N is number of documents and M is number of tokens in the vocabulary
     *
     * @param corpusTokens collection of documents that were tokenized
     * @param vocabulary collection of unique words over all documents inside the corpus
     */
    private void computeScoresMatrix(
            ArrayList<ArrayList<String>> corpusTokens, ArrayList<String> vocabulary) {
        // Compute avg doc len and number of docs
        double avgDocLen = computeAvgDocLength(corpusTokens);
        int numOfDocs = corpusTokens.size();

        // Step 1: Calculate the number of documents containing each token
        HashMap<String, Integer> docFreqs = calculateDocumentFrequencies(corpusTokens, vocabulary);

        // Step 2: Calculate the idf for each token using the document frequencies
        HashMap<String, Double> idf = calculateInverseDocumentFrequencies(docFreqs, numOfDocs);

        // Step 3 Calculate the BM25 scores for each token in each document
        computeScores(corpusTokens, idf, vocabulary, numOfDocs, avgDocLen);
    }

    /**
     * Calculates average length of document inside corpus
     *
     * @param corpusTokens collection of documents that were tokenized
     * @return average document length inside the corpus
     */
    private double computeAvgDocLength(ArrayList<ArrayList<String>> corpusTokens) {
        double result = 0.0;

        for (ArrayList<String> docTokens : corpusTokens) {
            result += docTokens.size();
        }

        return result / corpusTokens.size();
    }

    /**
     * For each token in the vocabulary calculates number of document containing it
     *
     * @param corpusTokens collection of documents that were tokenized
     * @param vocabulary collection of unique words over all documents inside the corpus
     * @return dictionary of (token, number of documents containing it) pairs
     */
    private HashMap<String, Integer> calculateDocumentFrequencies(
            ArrayList<ArrayList<String>> corpusTokens, ArrayList<String> vocabulary) {
        HashMap<String, Integer> docFrequencies = new HashMap<>();
        HashSet<String> vocabSet = new HashSet<>(vocabulary);

        for (String token : vocabSet) {
            // ref:
            // https://stackoverflow.com/questions/4157972/how-to-update-a-value-given-a-key-in-a-hashmap
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
     *
     * @param dfForToken document frequency for token of interest
     * @param numOfDocs number of documents in the corpus
     * @return idf score
     */
    private double idfScore(int dfForToken, int numOfDocs) {
        return Math.log(((numOfDocs - dfForToken + 0.5) / (dfForToken + 0.5)) + 1.0);
    }

    /**
     * Calculates inverse document frequencies for each document in vocabulary
     *
     * @param docFreqs dictionary of (token, number of times it appears of in each document) pairs
     * @param numOfDocs number of documents in the corpus
     * @return dictionary of (token, inverse document frequency) pairs
     */
    private HashMap<String, Double> calculateInverseDocumentFrequencies(
            HashMap<String, Integer> docFreqs, int numOfDocs) {
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

    /**
     * Computes term frequency component of the BM25 score using Robertson variant of formula <a
     * href="https://cs.uwaterloo.ca/~jimmylin/publications/Kamphuis_etal_ECIR2020_preprint.pdf">taken
     * from here</a>
     *
     * @param termFreq number of times given token appears in the current document
     * @param docLen length of a document
     * @param averageDocLength average document length inside the corpus
     * @param k1 parameter responsible for normalizing influence of term frequency component
     * @param b document-length normalisation parameter, in other words how much document length
     *     influences final score
     * @see #calculateTermFrequencies
     */
    private double termFreqScore(
            double termFreq, int docLen, double averageDocLength, double k1, double b) {
        return termFreq / (k1 * (1.0 - b + b * (double) docLen / averageDocLength) + termFreq);
    }

    /**
     * Fills index with scores while using precomputed statistics
     *
     * @param corpusTokens collection of documents that were tokenized
     * @param idf dictionary that stores inverse document frequency score for each token in
     *     vocabulary
     * @param vocabulary collection of unique words over all documents inside the corpus
     * @param numOfDocs number of documents in the corpus
     * @param averageDocLength average document length inside the corpus
     * @see Index
     */
    private void computeScores(
            ArrayList<ArrayList<String>> corpusTokens,
            HashMap<String, Double> idf,
            ArrayList<String> vocabulary,
            int numOfDocs,
            double averageDocLength) {
        int vocabSize = vocabulary.size();

        // Calculate the BM25 score for each token in the document
        for (int docIdx = 0; docIdx < numOfDocs; docIdx++) {
            ArrayList<String> document = corpusTokens.get(docIdx);
            int docLen = document.size();

            //
            ArrayList<Integer> termFreqs = calculateTermFrequencies(document, vocabulary);

            for (int tokenIdx = 0; tokenIdx < vocabSize; tokenIdx++) {
                if (!document.contains(vocabulary.get(tokenIdx))) {
                    continue;
                }

                // ref : https://en.wikipedia.org/wiki/Okapi_BM25
                // calculate term frequency score for each token inside document
                double score =
                        termFreqScore(
                                termFreqs.get(tokenIdx), docLen, averageDocLength, this.K1, this.B);

                double tokenIdf = idf.get(vocabulary.get(tokenIdx));

                if (score > 0.0) {
                    this.index.getMatrix().set(docIdx, tokenIdx, score * tokenIdf);
                }
            }
        }
    }

    /**
     * Calculates number of times each token from vocabulary appears in given document
     *
     * @param document collection of tokens corresponding to the document
     * @param vocabulary collection of unique words over all documents inside the corpus
     * @return ArrayList of numbers of documents per token from vocabulary. <br>
     *     It has built in the way that {@code result.get(vocabulary.indexOf(token))} gives us
     *     number of documents containing token
     */
    private ArrayList<Integer> calculateTermFrequencies(
            ArrayList<String> document, ArrayList<String> vocabulary) {
        HashMap<String, Integer> vocabMap = new HashMap<>();

        for (String token : vocabulary) {
            vocabMap.put(token, 0);
        }

        ArrayList<Integer> res = new ArrayList<>(document.size());

        for (String token : document) {
            vocabMap.put(token, vocabMap.get(token) + 1);
        }

        for (String token : vocabulary) {
            res.add(vocabMap.get(token));
        }

        return res;
    }
}
