package ch.heigvd.bm25.utils;

import ch.heigvd.bm25.exceptions.IndexException;

import java.util.ArrayList;
import java.util.List;

public class Index {
    public DSparseMatrixLIL matrix;

    private ArrayList<String> vocabulary;
    private ArrayList<String> documentNames;
    private int numOfDocs;
    private int vocabSize;

    public Index(int vocabSize, int numOfDocs, ArrayList<String> vocab, ArrayList<String> documentNames){
        this.vocabSize = vocabSize;
        this.numOfDocs = numOfDocs;
        this.vocabulary = vocab;
        this.documentNames = documentNames;
    }

    public static Index importIndex(String stringIndex) {
        int numOfDocs, vocabSize;

        // create an Index with a string
        // separate each line in the table
        ArrayList<String> tableStringIndex = new ArrayList<>(
                List.of(stringIndex.split("\n")));

        if (!tableStringIndex.get(0).equals("docNames")) {
            throw new IndexException("ill formated string : no docNames field");
        }
        String[] docNames = tableStringIndex.get(1).split(" ");

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

    public int getNumOfDocs(){
        return numOfDocs;
    }
    public int getVocabSize(){
        return vocabSize;
    }
    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }
    public String getDocumentName(int docIdx) {
        return this.documentNames.get(docIdx);
    }

    // reuses matrix.toString()
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("numOfDocs\n");
        result.append(this.numOfDocs + "\n");
        result.append("vocabSize\n");
        result.append(this.vocabSize + "\n");
        result.append("vocabulary\n");

        result.append(String.join(" ", vocabulary));
        result.append("\nmatrixScores\n");
        result.append(matrix.toString());

        return result.toString();
    }
}