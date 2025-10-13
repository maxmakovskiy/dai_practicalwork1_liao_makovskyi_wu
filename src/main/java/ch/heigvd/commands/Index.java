package ch.heigvd.commands;

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
    public int getVocabSize (){
        return vocabSize;
    }
    public ArrayList<String> getVocabulary(){
        return vocabulary;
    }

    // reuses matrix.toString()
    public String toString(){
        return matrix.toString();
    }


    public static void exportIndex(Index index) throws IOException {
        Writer writer = new FileWriter("Index.txt", StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(writer);

        // write in the file the stringyfied object Index
        int c;
        bw.write(index.toString());

        // Flush the buffer to write the remaining bytes
        bw.flush();
        bw.close();
    }

    public static Index importIndex(Index index) throws IOException {

        Reader reader = new FileReader("Index.txt", StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(reader);

        // Read the file .txt and build the object Index
        int c;
        while ((c = br.read()) != -1) {

            // Complete with parameters
            Index indexImproted = new Index();
        }

        // Flush the buffer to write the remaining bytes
        br.close();

        return index;
    }
}