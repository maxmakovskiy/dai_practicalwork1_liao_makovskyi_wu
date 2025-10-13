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


    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader("TextBufferReadAndWriteFileExample.java", StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(reader);

        Writer writer = new FileWriter("TextBufferReadAndWriteFileExample.txt", StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(writer);

        // -1 indicates the end of the file
        int c;
        while ((c = br.read()) != -1) {
            bw.write(c);
        }

        // Flush the buffer to write the remaining bytes
        bw.flush();
        bw.close();
        br.close();
    }
}