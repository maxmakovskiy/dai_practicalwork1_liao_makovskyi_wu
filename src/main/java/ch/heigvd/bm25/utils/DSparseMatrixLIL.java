package ch.heigvd.bm25.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;

/**
 * Sparse matrix in List-of-Lists (LIL) format
 *
 * @see <a href="https://matteding.github.io/2019/04/25/sparse-matrices/">Sparse matrices (LIL) –
 *     intro</a>
 */
public class DSparseMatrixLIL {

    private int nRows;
    private int nCols;
    private ArrayList<ArrayList<Integer>> indices;
    private ArrayList<ArrayList<Double>> data;

    /**
     * Creates an empty {@code nRows} x {@code nCols} sparse matrix
     *
     * @param nRows number of rows (documents)
     * @param nCols number of columns (terms)
     * @throws IllegalArgumentException if {@code nRows < 0} or {@code nCols < 0}
     */
    public DSparseMatrixLIL(int nRows, int nCols) {

        if (nRows < 0 || nCols < 0) {
            throw new IllegalArgumentException("nRows and nCols must be non-negative.");
        }

        this.nRows = nRows;
        this.nCols = nCols;

        this.indices = new ArrayList<>(nRows);
        this.data = new ArrayList<>(nRows);

        // ref : https://www.w3schools.com/java/ref_arraylist_add.asp
        for (int i = 0; i < nRows; i++) {
            indices.add(new ArrayList<>());
            data.add(new ArrayList<>());
        }
    }

    /**
     * Retrieves value at a given matrix position ({@code rowIdx}, {@code colIdx})
     *
     * @param rowIdx zero-based row index
     * @param colIdx zero-based column index
     * @return the stored value. Return {@code 0.0} if not found.
     * @throws IndexOutOfBoundsException if {@code rowIdx < 0 } or {@code rowIdx >= nRows} {@code
     *     colIdx < 0} or {@code colIdx >= nCols}
     */
    public double get(int rowIdx, int colIdx) {

        boolean isRowIndexOutOfRange = rowIdx < 0 || rowIdx >= nRows;
        boolean isColIndexOutOfRange = colIdx < 0 || colIdx >= nCols;
        if (isRowIndexOutOfRange || isColIndexOutOfRange) {
            // ref :
            // https://stackoverflow.com/questions/7312767/manually-adding-indexoutofbounds-exception
            throw new IndexOutOfBoundsException(
                    "Cannot find indices for rowIdx: " + rowIdx + " and colIdx: " + colIdx);
        }

        // ref: https://www.geeksforgeeks.org/java/list-get-method-in-java-with-examples/
        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = data.get(rowIdx);

        int cIndex = rowIndicesList.indexOf(colIdx);
        if (cIndex != -1) {
            return rowScoresList.get(cIndex);
        }

        return 0.0;
    }

    /**
     * Sets a value at a given matrix position ({@code rowIdx}, {@code colIdx})
     *
     * <p>If an entry at ({@code rowIdx}, {@code colIdx}) already exists, its value is updated.
     * Otherwise, the column index and value are appended to that row's lists (note: column indices
     * within a row are not automatically sorted).
     *
     * @param rowIdx zero-based row index
     * @param colIdx zero-based column index
     * @param value value to store ( cannot be {@code 0.0})
     * @throws IndexOutOfBoundsException if {@code rowIdx < 0 } or {@code rowIdx >= nRows} {@code
     *     colIdx < 0} or {@code colIdx >= nCols}
     */
    public void set(int rowIdx, int colIdx, double value) {
        boolean isRowIndexOutOfRange = rowIdx < 0 || rowIdx >= nRows;
        boolean isColIndexOutOfRange = colIdx < 0 || colIdx >= nCols;

        if (isRowIndexOutOfRange || isColIndexOutOfRange) {
            throw new IndexOutOfBoundsException(
                    "Cannot set value at rowIdx: " + rowIdx + " and colIdx: " + colIdx);
        }

        if (value == 0) {
            return;
        }

        ArrayList<Integer> rowIndicesList = indices.get(rowIdx);
        ArrayList<Double> rowScoresList = data.get(rowIdx);

        int realColumnIndex = rowIndicesList.indexOf(colIdx);
        if (realColumnIndex != -1) {
            rowScoresList.set(realColumnIndex, value);
        } else {
            rowIndicesList.add(colIdx);
            rowScoresList.add(value);
        }
    }

    /**
     * Converts current instance of sparse matrix to a human-readable text format.
     *
     * <p><strong>Expected output layout</strong> (line numbers are illustrative):
     *
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
     * @see <a
     *     href="https://stackoverflow.com/questions/7775394/java-concatenate-to-build-string-or-format">
     *     Java concatenate to build string</a>
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("nRows : ").append(nRows).append("\n");
        sb.append("nCols : ").append(nCols).append("\n");

        sb.append("Indices\n");
        for (int i = 0; i < nRows; i++) {

            //  "0 : 0, 6, 7, 9, 13",
            sb.append(i).append(" : ");

            ArrayList<Integer> indicesLine = indices.get(i);
            for (int k = 0; k < indicesLine.size(); k++) {

                sb.append(indicesLine.get(k));
                if (k != indicesLine.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        sb.append("Data\n");
        for (int i = 0; i < nRows; i++) {

            sb.append(i).append(" : ");

            ArrayList<Double> dataLine = data.get(i);
            for (int k = 0; k < dataLine.size(); k++) {

                sb.append(dataLine.get(k));
                if (k != dataLine.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Produces string containing json node
     *
     * <p><strong>Example of output layout</strong>:
     *
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
     */
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Printing every array's element from new line
        // can be done by following : https://stackoverflow.com/a/40044685
        //        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        ObjectNode root = mapper.createObjectNode();

        root.put("nRows", nRows);
        root.put("nCols", nCols);
        root.putPOJO("indices", indices);
        root.putPOJO("data", data);

        //        return mapper.writer(prettyPrinter).writeValueAsString(root);
        return mapper.writeValueAsString(root);
    }

    /**
     * Creates DSparseMatrixLIL from json string that follow certain format.
     *
     * <p><strong>Example of expected layout</strong>:
     *
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
     */
    public static DSparseMatrixLIL fromJSON(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        int nRows = root.get("nRows").intValue();
        int nCols = root.get("nCols").intValue();

        DSparseMatrixLIL matrix = new DSparseMatrixLIL(nRows, nCols);

        matrix.indices =
                mapper.treeToValue(
                        root.get("indices"), new TypeReference<ArrayList<ArrayList<Integer>>>() {});

        matrix.data =
                mapper.treeToValue(
                        root.get("data"), new TypeReference<ArrayList<ArrayList<Double>>>() {});

        return matrix;
    }

    /** Gets number of rows in a matrix */
    public int getNumOfRows() {
        return nRows;
    }

    /** Gets number of columns in a matrix */
    public int getNumOfCols() {
        return nCols;
    }
}
