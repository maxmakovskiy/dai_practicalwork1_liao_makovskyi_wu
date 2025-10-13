package ch.heigvd;

// https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html
import java.util.ArrayList;

// ref: https://matteding.github.io/2019/04/25/sparse-matrices/
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

    private static int parseNRows(ArrayList<String> matrixRows) {

        if (matrixRows == null || matrixRows.isEmpty()){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }

        String nRowStr = matrixRows.get(0);
        String[] nRowStrList = nRowStr.split(":");
        return Integer.parseInt(nRowStrList[1].trim());
    }

    private static int parseNCols(ArrayList<String> matrixRows) {

        if (matrixRows.size() < 2 ){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }

        String nColStr = matrixRows.get(1);
        String[] nColStrList = nColStr.split(":");
        return Integer.parseInt(nColStrList[1].trim());
    }

    // Construct matrix from one long string or array of strings
     public DSparseMatrixLIL(ArrayList<String> matrixRows){

        // call DSparseMatrixLIL(int nRows, int nCols)
        this(parseNRows(matrixRows), parseNCols(matrixRows));

        if (matrixRows.size() < 3){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }


/*
        int lineIdx = 0;

        // "nRows : 3"
        String nRowStr = matrixRows.get(lineIdx++);
        String[] nRowStrList = nRowStr.split(":");
        this.nRows = Integer.parseInt(nRowStrList[1].trim());

        //  "nCols : 14"
         String nColStr = matrixRows.get(lineIdx++);
         String[] nColStrList = nColStr.split(":");
         this.nCols = Integer.parseInt(nColStrList[1].trim());
*/

         //DSparseMatrixLIL(nRows, nCols);

        //  "Indices",
         //    "0 : 0, 6, 7, 9, 13",
         //    "1 : 0, 1, 2, 8, 11, 12",
         //    "2 : 3, 4, 5, 7, 10",


         int lineIdx = 2;

         // Skip  "Indices",
         lineIdx++;

         for (int i =0; i < nRows; i++) {
             String docLine = matrixRows.get(lineIdx++);
             String[] docLine2StrList = docLine.split(":");

             if (docLine2StrList.length > 1) {

                 String indicesStr = docLine2StrList[1].trim();

                if (!indicesStr.isEmpty()){
                    String[] indicesStrList = indicesStr.split(",");

                    for (String idxStr : indicesStrList) {
                        int idxInt = Integer.parseInt(idxStr.trim());
                        indices.get(i).add(idxInt);
                    }
                }
             }
         }

         // skip "Data"
         lineIdx++;

         for (int i =0; i < nRows; i++) {
             String docLine = matrixRows.get(lineIdx++);
             String[] docLine2StrList = docLine.split(":");

             if (docLine2StrList.length > 1) {

                 String scoresStr = docLine2StrList[1].trim();

                 if (!scoresStr.isEmpty()) {
                     String[] scoresStrList = scoresStr.split(",");

                     for (String scoreStr : scoresStrList) {
                         int scoreInt = Integer.parseInt(scoreStr.trim());
                         indices.get(i).add(scoreInt);
                     }

                 }
             }
         }

     }
    // public DSparseMatrixLIL(String matrix)

    // public String toString()

}


