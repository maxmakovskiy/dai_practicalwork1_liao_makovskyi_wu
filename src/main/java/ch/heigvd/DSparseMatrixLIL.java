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


    // Retrieve value at (rowIdx, colIdx). Return 0.0 when there is no value stored
    public double get(int rowIdx, int colIdx) {

        if (rowIdx < 0 || rowIdx >= nRows || colIdx < 0 || colIdx >= nCols){

            // ref : https://stackoverflow.com/questions/7312767/manually-adding-indexoutofbounds-exception
            throw new IndexOutOfBoundsException(
                "Cannot find indices for rowIdx: " + rowIdx + " and colIdx: " + colIdx
            );
        }

        // ref: https://www.geeksforgeeks.org/java/list-get-method-in-java-with-examples/
        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = scores.get(rowIdx);

        for (int i = 0; i < rowIndicesList.size(); i++){
            if(rowIndicesList.get(i) == colIdx){
                return rowScoresList.get(i);
            }
        }

        return 0.0;

    }


    public void set(int rowIdx, int colIdx, double value) {

        if (rowIdx < 0 || rowIdx >= nRows || colIdx < 0 || colIdx >= nCols){
            throw new IndexOutOfBoundsException(
                    "Cannot set value at rowIdx: " + rowIdx + " and colIdx: " + colIdx
            );
        }

        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = scores.get(rowIdx);

        for ( int i = 0; i < rowIndicesList.size(); i++){

            if (rowIndicesList.get(i) == colIdx){
                rowScoresList.set(i, value);
                return;
            }

        }

        // if it does not exist, add new value in the list
        rowIndicesList.add(colIdx);
        rowScoresList.add(value);

    }

    // Construct matrix from one long string or array of strings
    // public DSparseMatrixLIL(ArrayList<String> matrixRows)
    // public DSparseMatrixLIL(String matrix)

    // public String toString()

}


