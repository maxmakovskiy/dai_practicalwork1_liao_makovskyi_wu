package ch.heigvd.bm25.utils;

import ch.heigvd.bm25.exceptions.IndexException;
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
}