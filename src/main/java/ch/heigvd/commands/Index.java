package ch.heigvd.commands;

import ch.heigvd.DSparseMatrixLIL;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Index {
    public DSparseMatrixLIL matrix;

    private ArrayList<String> vocabulary;
    private int numOfDocs;
    private int vocabSize;

    public Index(int vocabSize, int numOfDocs, ArrayList<String> vocab){}
    public Index(int vocabSize, int numOfDocs, ArrayList<String> vocab, ArrayList<String> matrixRows){}


    public int getNumOfDocs(){
        return numOfDocs;
    }
    public int getVocabSize(){
        return vocabSize;
    }
    public ArrayList<String> getVocabulary(){
        return vocabulary;
    }

    // reuses matrix.toString()
    public String toString(){
        String result = new String();

        result += "numOfDocs\n";
        result += getNumOfDocs() + "\n";
        result += "vocabSize\n";
        result += getVocabSize() + "\n";
        result += "vocabulary\n";

        for(String word : getVocabulary()){
            result += word + " ";
        }

        result += "\n";
        result += "matrixScores\n";

        return result;
    }

    /**
     * call toString() to make a string with a
     */
    public String exportIndex(Index index) {
        return this.toString();
    }

    public static Index importIndex(String stringIndex) {
        int vocabSize, numOfDocs;
        ArrayList<String> vocab = new ArrayList<>();

        // create an Index with a string
        // separate each line in the table
        String[] tableStringIndex = stringIndex.split("\n");

        vocabSize = Integer.parseInt(tableStringIndex[1]);
        numOfDocs = Integer.parseInt(tableStringIndex[3]);

        String[] tableVocab = new String[vocabSize];
        tableVocab = tableStringIndex[4].split(" ");

        // empty the previous vocabulary for the new one
        if(vocab == null || vocab.size() != 0) {
            vocab.clear();
        }

        for(String word : tableVocab){
            // then add the news words imported
            vocab.add(word);
        }

        // create the string
        Index indexCreated = new Index(vocabSize, numOfDocs, vocab);

        return indexCreated;
    }

    public static void main(String[] args) throws IOException {
    }
}