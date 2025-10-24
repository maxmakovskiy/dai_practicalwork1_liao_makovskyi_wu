package ch.heigvd.bm25.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.util.ArrayList;

/**
 * This class represents Index abstraction. Index is basically everything that we need to know for
 * searching through the given corpus of documents. It consists of: matrix of scores assigned to
 * every token in the corpus, vocabulary which is unique words over the corpus, names of the
 * documents since we need to say what is relevant for the query and what is not.
 */
public class Index {
    private DSparseMatrixLIL matrix;
    private ArrayList<String> vocabulary;
    private ArrayList<String> documentNames;

    /**
     * Constructs Index
     *
     * @param vocab collection of unique tokens over the corpus
     * @param documentNames names of all documents in the corpus
     *     <p><strong>Corpus</strong> is a collection of treated documents
     */
    public Index(ArrayList<String> vocab, ArrayList<String> documentNames) {
        this(vocab, documentNames, new DSparseMatrixLIL(documentNames.size(), vocab.size()));
    }

    /**
     * Constructs Index
     *
     * @param vocab collection of unique tokens over the corpus
     * @param documentNames names of all documents in the corpus
     * @param scoreMatrix instance of DSparseMatrixLIL containing scores for every token in the
     *     corpus
     *     <p><strong>Corpus</strong> is a collection of treated documents
     * @see DSparseMatrixLIL
     */
    public Index(
            ArrayList<String> vocab,
            ArrayList<String> documentNames,
            DSparseMatrixLIL scoreMatrix) {
        this.vocabulary = vocab;
        this.documentNames = documentNames;
        this.matrix = scoreMatrix;
    }

    /**
     * @return the number of the represented documents
     */
    public int getNumOfDocs() {
        return documentNames.size();
    }

    /**
     * @return vocabulary size
     */
    public int getVocabSize() {
        return vocabulary.size();
    }

    /**
     * @return list of unique tokens over all the documents, so-called vocabulary
     */
    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }

    /**
     * @param docIdx index of the document of interest
     * @return name of the document by its id
     */
    public String getDocumentName(int docIdx) {
        return this.documentNames.get(docIdx);
    }

    /**
     * Serializes this instance of Index to a human-readable text format.
     *
     * <p><strong>Expected output layout</strong> (line numbers are illustrative):
     *
     * <pre>{@code
     * 0:        docNames
     * 1:        fileName0, fileName1, fileName2, ...
     * 2:        numOfDocs
     * 3:        <int>
     * 4:        vocabSize
     * 5:        <int>
     * 6:        vocabulary
     * 7:        word0, word1, word2, ...
     * 8:        matrixScores
     * 9:        nRows : <int>
     * 10:       nCols : <int>
     * 11:       Indices
     * 12..N:    <row> : vIdx0, vIdx1, vIdx2, ...
     * N+1:      Data
     * N+2:      <row> : vScore0, vScore1, vScore2, ...
     * }</pre>
     *
     * @return string with textual representation of this Index instance
     */
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("docNames\n");
        result.append(String.join("|", documentNames));

        result.append("\nnumOfDocs\n");
        result.append(this.documentNames.size());
        result.append("\nvocabSize\n");
        result.append(this.vocabulary.size());
        result.append("\nvocabulary\n");

        result.append(String.join(" ", vocabulary));
        result.append("\nmatrixScores\n");
        result.append(matrix.toString());

        return result.toString();
    }

    /**
     * Produces json string from current instance of Index
     *
     * <p><strong>Example of output layout</strong>:
     *
     * <pre>{@code
     * {
     *   "documentNames": ["file1.txt","file2.txt"],
     *   "vocabulary": ["like","best","plai","can"],
     *   "matrix": {
     *      "nRows": 2,
     *      "nCols": 4,
     *      "indices":[[0,1,3],[1,2]],
     *      "data":[[1.0,1.1,1.3],[2.0,2.1]]
     *   }
     * }
     * }</pre>
     *
     * @return json string
     * @throws JsonProcessingException if json generation goes wrong
     */
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Printing every array's element from new line
        // can be done by following : https://stackoverflow.com/a/40044685
        //        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        ObjectNode root = mapper.createObjectNode();

        root.putPOJO("documentNames", documentNames);
        root.putPOJO("vocabulary", vocabulary);
        root.putRawValue("matrix", new RawValue(matrix.toJSON()));

        //        return mapper.writer(prettyPrinter).writeValueAsString(root);
        return mapper.writeValueAsString(root);
    }

    /**
     * Creates an instance of Index from json string
     *
     * <p><strong>Example of expected layout</strong>:
     *
     * <pre>{@code
     * {
     *   "documentNames": ["file1.txt","file2.txt"],
     *   "vocabulary": ["like","best","plai","can"],
     *   "matrix": {
     *      "nRows": 2,
     *      "nCols": 4,
     *      "indices":[[0,1,3],[1,2]],
     *      "data":[[1.0,1.1,1.3],[2.0,2.1]]
     *   }
     * }
     * }</pre>
     *
     * @return instance of Index
     * @throws JsonProcessingException if json parsing goes wrong
     */
    public static Index fromJSON(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        ArrayList<String> docNames =
                mapper.treeToValue(
                        root.get("documentNames"), new TypeReference<ArrayList<String>>() {});
        ArrayList<String> vocab =
                mapper.treeToValue(
                        root.get("vocabulary"), new TypeReference<ArrayList<String>>() {});

        String matrixJsonStr = root.get("matrix").toString();
        DSparseMatrixLIL matrix = DSparseMatrixLIL.fromJSON(matrixJsonStr);

        Index index = new Index(vocab, docNames);
        index.matrix = matrix;

        return index;
    }

    /**
     * @return matrix of scores for corpus
     */
    public DSparseMatrixLIL getMatrix() {
        return matrix;
    }
}
