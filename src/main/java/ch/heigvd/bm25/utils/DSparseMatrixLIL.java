package ch.heigvd.bm25.utils;

// ref : https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays; // Arrays.aslist(arr)
import java.lang.String; // StringBuilder


/**
 * Sparse matrix in List-of-Lists (LIL) format
 * @see <a href="https://matteding.github.io/2019/04/25/sparse-matrices/">
 *      Sparse matrices (LIL) – intro</a>
 */
public class DSparseMatrixLIL {

    private int nRows; // DocID
    private int nCols; // WordID
    private ArrayList<ArrayList<Integer>> indices; // column positions
    private ArrayList<ArrayList<Double>> data;


    /**
     * Create an empty {@code nRows} x {@code nCols} sparse matrix
     * @param nRows number of rows (documents)
     * @param nCols number of columns (terms)
     * @throws IllegalArgumentException if {@code nRows < 0} or {@code nCols < 0}
     */
    public DSparseMatrixLIL(int nRows, int nCols){

        if (nRows < 0 || nCols < 0) {
            throw new IllegalArgumentException("nRows and nCols must be non-negative.");
        }

        this.nRows = nRows;
        this.nCols = nCols;

        this.indices = new ArrayList<>(nRows);
        this.data = new ArrayList<>(nRows);

        // ref : https://www.w3schools.com/java/ref_arraylist_add.asp
        for (int i = 0; i < nRows; i++){
            indices.add(new ArrayList<>());
            data.add(new ArrayList<>());
        }

    }

    /**
     * Retrieve value at a given matrix position ({@code rowIdx}, {@code colIdx})
     * @param rowIdx zero-based row index
     * @param colIdx zero-based column index
     * @return the stored value. Return {@code 0.0} if not found.
     * @throws IndexOutOfBoundsException if {@code rowIdx < 0 } or {@code rowIdx >= nRows}
     *                                      {@code colIdx < 0} or {@code colIdx >= nCols}
     */
    public double get(int rowIdx, int colIdx) {

        if (rowIdx < 0 || rowIdx >= nRows || colIdx < 0 || colIdx >= nCols){
            // ref : https://stackoverflow.com/questions/7312767/manually-adding-indexoutofbounds-exception
            throw new IndexOutOfBoundsException(
                    "Cannot find indices for rowIdx: " + rowIdx + " and colIdx: " + colIdx
            );
        }

        // ref: https://www.geeksforgeeks.org/java/list-get-method-in-java-with-examples/
        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = data.get(rowIdx);

        int cIndex = rowIndicesList.indexOf(colIdx);
        if (cIndex != -1){
            return rowScoresList.get(cIndex);
        }

        return 0.0;

    }

    /**
     * Set a value at a given matrix position  ({@code rowIdx}, {@code colIdx})
     *
     * <p>If an entry at ({@code rowIdx}, {@code colIdx}) already exists, its value
     * is updated. Otherwise, the column index and value are appended to that row's
     * lists (note: column indices within a row are not automatically sorted).</p>
     *
     * @param rowIdx zero-based row index
     * @param colIdx zero-based column index
     * @param value value to store ( cannot be {@code 0.0})
     * @throws IllegalArgumentException if {@code rowIdx < 0 } or {@code rowIdx >= nRows}
     *                                      {@code colIdx < 0} or {@code colIdx >= nCols}
     *                                      or {@code value == 0.0}
     */

    public void set(int rowIdx, int colIdx, double value) {

        if (rowIdx < 0 || rowIdx >= nRows || colIdx < 0 || colIdx >= nCols ){
            throw new IndexOutOfBoundsException(
                    "Cannot set value at rowIdx: " + rowIdx + " and colIdx: " + colIdx
            );
        } else if (value < 0.0) {
            throw new IllegalArgumentException("Value must be non-negative.");
        }

        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = data.get(rowIdx);

        int cIndex = rowIndicesList.indexOf(colIdx);
        if (cIndex != -1){
            rowScoresList.set(cIndex, value);
            return;
        }

        rowIndicesList.add(colIdx);
        rowScoresList.add(value);

    }

    /**
     *
     * Parses the number of rows from the first header line of a serialized matrix.
     * <p>Expects {@code matrixRows.get(0)} to look like: {@code "nRows : <int>"}.</p>
     *
     * @param matrixRows lines of the serialized matrix; must not be {@code null} or empty
     * @return the parsed row count
     * @throws IndexOutOfBoundsException if {@code matrixRows} is null or empty
     */
    private static int parseNRows(ArrayList<String> matrixRows) {

        if (matrixRows == null || matrixRows.isEmpty()){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }

        String nRowStr = matrixRows.get(0);
        String[] nRowStrList = nRowStr.split(":");
        return Integer.parseInt(nRowStrList[1].trim());
    }

    /**
     * Parses the number of columns from the second header line of a serialized matrix.
     * <p>Expects {@code matrixRows.get(1)} to look like: {@code "nCols : <int>"}.</p>
     * @param matrixRows matrixRows lines of the serialized matrix; must contain at least two lines
     * @return the parsed column count
     * @throws IndexOutOfBoundsException if matrix contains less than 2 lines.
     */
    private static int parseNCols(ArrayList<String> matrixRows) {

        if (matrixRows.size() < 2 ){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }

        String nColStr = matrixRows.get(1);
        String[] nColStrList = nColStr.split(":");
        return Integer.parseInt(nColStrList[1].trim());
    }

    /**
     * Constructs a matrix from the serialized text format produced by {@link #toString()}.
     *
     * <p><strong>Expected layout</strong> (line numbers are illustrative):</p>
     * <pre>{@code
     * 0:       nRows : <int>
     * 1:       nCols : <int>
     * 2:       Indices
     * 3..N:    <row> : vIdx0, vIdx1, vIdx2..
     * N+1:     Data
     * N+2:     <row> : vScore0, vScore1, vScore2...
     * }</pre>
     * <p>Values are parsed with
     * {@link Integer#parseInt(String)} and {@link Double#parseDouble(String)}.</p>
     *
     * @param matrixRows lines of the serialized matrix; must include headers,
     *                   an {@code Indices} section with {@code nRows} lines,
     *                   and a {@code Data} section with {@code nRows} lines
     * @throws IndexOutOfBoundsException if the {@matrixRows}is too short or a required line is missing
     *
     */
    public DSparseMatrixLIL(ArrayList<String> matrixRows){

        this(parseNRows(matrixRows), parseNCols(matrixRows));

        if (matrixRows.size() < 3){
            throw new IndexOutOfBoundsException("Invalid matrix rows!");
        }

        int lineIdx = 2;

        // Skip  "Indices"
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
                        double scoreDouble = Double.parseDouble(scoreStr.trim());
                        data.get(i).add(scoreDouble);
                    }
                }
            }
        }
    }

    /**
     * Constructs a matrix from a serialized string produced by {@link #toString()}.
     *
     *  <p>The string is split into lines and delegated to {@link #DSparseMatrixLIL(java.util.ArrayList)}.</p>
     *
     *  <p><strong>Expected input layout</strong> (
     *  <pre>{@code
     *  "Line1\nLine2\nLine3\nLine4\nLine5\nLine6\nLine7\nLine8\nLine9\n"
     *  }
     *
     *  <p><strong>Expected output in array list</strong>
     *  <pre>{@code
     *  ["line1","line2", "line3", "line4", "line5", "line6", "line7", "line8", "line9"]
     *  }
     *
     * @param matrix the text (lines separated by newlines)
     * @throws NullPointerException if {@code matrix} is {@code null}
     * @throws IndexOutOfBoundsException if the {@matrixRows}is too short or a required line is missing
     *
     * @see #DSparseMatrixLIL(java.util.ArrayList)
     */
    public DSparseMatrixLIL(String matrix) {
        /*
        String[] lines = matrix.split("\n");                                    // 1. "Line1\nLine2\nLine3\n" -> ["line1","line2", "line3"]
        ArrayList<String> matrixRows = new ArrayList<>(Arrays.asList(lines));   // 2. Array transforms to List
        this(matrixRows);                                                       // 3. call func :  DSparseMatrixLIL(ArrayList<String> matrixRows)
        */
        this(new ArrayList<>(Arrays.asList(matrix.split("\n"))));

    }

    /**
     * Serializes this sparse matrix to a human-readable text format.
     *
     * <p>The output can be parsed back by {@link #DSparseMatrixLIL(String)} and
     * {@link #DSparseMatrixLIL(java.util.ArrayList)}. Layout:</p>
     *
     * <p><strong>Expected output layout</strong> (line numbers are illustrative):</p>
     * <pre>{@code
     * 0:       nRows : <int>
     * 1:       nCols : <int>
     * 2:       Indices
     * 3..N:    <row> : vIdx0, vIdx1, vIdx2..
     * N+1:     Data
     * N+2:     <row> : vScore0, vScore1, vScore2...
     * }</pre>
     *
     * @return the textual representation of this matrix
     *
     * @see <a href="https://stackoverflow.com/questions/7775394/java-concatenate-to-build-string-or-format">
     *        Java concatenate to build string</a>
     */

    // ref :
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //  "nRows : 3"
        sb.append("nRows : ").append(nRows).append("\n");
        //  "nCols : 14"
        sb.append("nCols : ").append(nCols).append("\n");

        sb.append("Indices\n");
        for (int i = 0 ; i < nRows; i++){

            //  "0 : 0, 6, 7, 9, 13",
            sb.append(i).append(" : ");

            ArrayList<Integer> indicesLine = indices.get(i);
            for(int k = 0; k < indicesLine.size(); k++){

                sb.append(indicesLine.get(k));
                if (k != indicesLine.size()-1){
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        sb = sb.append("Data\n");
        for (int i = 0 ; i < nRows; i++){

            //  "0 : 0.22927006304670033, 0.47845329415206167, 0.22927006304670033, 0.47845329415206167, 0.47845329415206167 "
            sb.append(i).append(" : ");

            ArrayList<Double> scoresLine = data.get(i);
            for(int k = 0; k < scoresLine.size(); k++){

                sb.append(scoresLine.get(k));
                if (k != scoresLine.size()-1){
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        String rst = sb.toString();
        return rst;
    }

    /**
     * Produces string containing json node
     * <p><strong>Example of output layout</strong>:</p>
     * <pre>{@code
     * {
     *   "nCols": 4,
     *   "nRows": 4,
     *   "indices": [
     *     [0,1,3],
     *     [1,0],
     *     [2],
     *     [3]
     *   ],
     *   "data":[
     *     [1.0,1.1,1.3],
     *     [2.0,2.1],
     *     [3.0],
     *     [4.0]
     *   ]
     * }
     * }</pre>
     *
     * @return json string
     * @throws JsonProcessingException if generating json goes wrong
     * */
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("nRows", nRows);
        root.put("nCols", nCols);
        root.putPOJO("indices", indices);
        root.putPOJO("data", data);

        return mapper.writeValueAsString(root);
    }

    /**
     * Creates DSparseMatrixLIL from json string that follow certain format.
     * <p><strong>Example of expected layout</strong>:</p>
     * <pre>{@code
     * {
     *   "nCols": 4,
     *   "nRows": 4,
     *   "indices": [
     *     [0,1,3],
     *     [1,0],
     *     [2],
     *     [3]
     *   ],
     *   "data":[
     *     [1.0,1.1,1.3],
     *     [2.0,2.1],
     *     [3.0],
     *     [4.0]
     *   ]
     * }
     * }</pre>
     *
     * @return an instance of DSparseMatrixLIL
     * @throws JsonProcessingException if json parsing goes wrong
     * */
    public static DSparseMatrixLIL fromJSON(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        int nRows = root.get("nRows").intValue();
        int nCols = root.get("nCols").intValue();

        DSparseMatrixLIL matrix = new DSparseMatrixLIL(nRows, nCols);

        matrix.indices = mapper.treeToValue(
                root.get("indices"),
                new TypeReference<ArrayList<ArrayList<Integer>>>() { }
        );

        matrix.data = mapper.treeToValue(
                root.get("data"),
                new TypeReference<ArrayList<ArrayList<Double>>>() { }
        );

        return matrix;
    }

}


