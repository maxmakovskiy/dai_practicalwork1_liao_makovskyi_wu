package ch.heigvd.bm25.utils;

import ch.heigvd.bm25.exceptions.IndexException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

import java.util.ArrayList;
import java.util.List;

public class Index {
    public DSparseMatrixLIL matrix; // matrix of score
    private ArrayList<String> vocabulary; //  list of words present
    private ArrayList<String> documentNames; // list of file name
    private int numOfDocs; // number of file saved
    private int vocabSize; // size of vocabulary

    public Index(int vocabSize, int numOfDocs, ArrayList<String> vocab, ArrayList<String> documentNames){
        this.vocabSize = vocabSize;
        this.numOfDocs = numOfDocs;
        this.vocabulary = vocab;
        this.documentNames = documentNames;
        this.matrix = new DSparseMatrixLIL(numOfDocs, vocabSize);
    }

    /**
     * create an Index object from a string
     * @param stringIndex Index as a string
     * @return a Index object build from a string
     */
    public static Index importIndex(String stringIndex) {
        int numOfDocs; // number of document file
        int vocabSize; // size of all words present

        // separate each line in the table
        ArrayList<String> tableStringIndex = new ArrayList<>(
                List.of(stringIndex.split("\n")));

        if (!tableStringIndex.get(0).equals("docNames")) {
            throw new IndexException("ill formated string : no docNames field");
        }
        String[] docNames = tableStringIndex.get(1).split("\\|");

        if (!tableStringIndex.get(2).equals("numOfDocs")) {
            throw new IndexException("ill formated string : no numOfDocs field");
        }
        numOfDocs = Integer.parseInt(tableStringIndex.get(3));

        if (!tableStringIndex.get(4).equals("vocabSize")) {
            throw new IndexException("ill formated string : no vocabSize field");
        }
        vocabSize = Integer.parseInt(tableStringIndex.get(5));

        if (!tableStringIndex.get(6).equals("vocabulary")) {
            throw new IndexException("ill formated string : no vocab field");
        }
        String[] vocab = tableStringIndex.get(7).split(" ");

        if (!tableStringIndex.get(8).equals("matrixScores")) {
            throw new IndexException("ill formated string : no matrixScores field");
        }

        Index index = new Index(
                vocabSize,
                numOfDocs,
                new ArrayList<>(List.of(vocab)),
                new ArrayList<>(List.of(docNames))
        );

        index.matrix = new DSparseMatrixLIL(
                new ArrayList<>(tableStringIndex.subList(9, tableStringIndex.size())));

        return index;
    }

    /**
     * get numOfDocs
     * @return the number of document file saved in Index
     */
    public int getNumOfDocs(){
        return numOfDocs;
    }

    /**
     * get the vocabulary size
     * @return vocabulary size
     */
    public int getVocabSize(){
        return vocabSize;
    }

    /**
     * get the vocabulary
     * @return arrayList of the vocabulary present
     */
    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }

    /**
     * get the name of document fron its id
     * @param docIdx index of the document file
     * @return name of the document file from id docIdx
     */
    public String getDocumentName(int docIdx) {
        return this.documentNames.get(docIdx);
    }

    /**
     * convert Index object into a string
     * @return a string fromat of Index object
     */
    public String toString(){

        // StringBuilder allow us to build string faster
        StringBuilder result = new StringBuilder();

        result.append("docNames\n");
        result.append(String.join("|", documentNames));

        result.append("\nnumOfDocs\n");
        result.append(this.numOfDocs);
        result.append("\nvocabSize\n");
        result.append(this.vocabSize);
        result.append("\nvocabulary\n");

        result.append(String.join(" ", vocabulary));
        result.append("\nmatrixScores\n");
        result.append(matrix.toString());

        return result.toString();
    }

    /**
     * Produces json string from current instance of Index
     * <p><strong>Example of output layout</strong>:</p>
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
     * @throws JsonProcessingException if json generation goes wrong
     * @return json string
     * */
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
     * <p><strong>Example of expected layout</strong>:</p>
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
     * @throws JsonProcessingException if json parsing goes wrong
     * @return instance of Index
     * */
    public static Index fromJSON(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        ArrayList<String> docNames = mapper.treeToValue(
                root.get("documentNames"),
                new TypeReference<ArrayList<String>>() { }
        );
        ArrayList<String> vocab = mapper.treeToValue(
                root.get("vocabulary"),
                new TypeReference<ArrayList<String>>() { }
        );

        String matrixJsonStr =  root.get("matrix").toString();
        DSparseMatrixLIL matrix = DSparseMatrixLIL.fromJSON(matrixJsonStr);

        Index index = new Index(vocab.size(), docNames.size(), vocab, docNames);
        index.matrix = matrix;

        return index;
    }



}