package ch.heigvd.bm25.exceptions;

// ref : https://stackoverflow.com/a/3776335
public class IndexException extends RuntimeException {
    public IndexException(String message) {
        super(message);
    }
}
