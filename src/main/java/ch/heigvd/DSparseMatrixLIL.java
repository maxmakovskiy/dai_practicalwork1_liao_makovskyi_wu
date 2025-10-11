package ch.heigvd;

import java.util.ArrayList;

public class DSparseMatrixLIL {

    private int nRows; // DocID
    private int nCols; // WordID
    private ArrayList<ArrayList<Integer>> indices; // column positions
    private ArrayList<ArrayList<Double>>  scores;


    // empty matrix c-tor
    public DSparseMatrixLIL(int nRows, int nCols){

        this.nRows = nRows;
        this.nCols = nCols;

        this.indices = new ArrayList<>(nRows);
        this.scores = new ArrayList<>(nRows);

        // ref : https://www.w3schools.com/java/ref_arraylist_add.asp
        for (int i = 0; i < nRows; i++){
            indices.add(new ArrayList<>());
            scores.add(new ArrayList<>());
        }

    }


    // Construct matrix from one long string or array of strings
    // public DSparseMatrixLIL(ArrayList<String> matrixRows)
    // public DSparseMatrixLIL(String matrix)

    // public double get(int rowIdx, int colIdx)
    // public void set(int rowIdx, int colIdx, double value)

    // public String toString()

}
