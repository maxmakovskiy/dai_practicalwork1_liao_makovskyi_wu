package ch.heigvd.commands;

import ch.heigvd.bm25.BM25;
import ch.heigvd.bm25.utils.Index;
import ch.heigvd.bm25.utils.RankingResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(
        name = "search",
        description = "search thought your documents using index file",
        mixinStandardHelpOptions = true)
public class Search implements Runnable {

    @Parameters(index = "0", paramLabel = "INDEX", description = "index file to work with")
    File indexFile;

    @Parameters(arity = "1..*", index = "1..*", paramLabel = "QUERY", description = "user's query")
    String[] query;

    @Option(
            names = {"-K", "--topK"},
            defaultValue = "3",
            description = "top k-results to show")
    int topK;

    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(Build.class);

        String queryFull = String.join(" ", query);

        // Step 1: read content of index file to string
        StringBuilder indexBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(indexFile, StandardCharsets.UTF_8);
                BufferedReader buf = new BufferedReader(reader); ) {
            int c;
            while ((c = buf.read()) != -1) {
                indexBuilder.append((char) c);
            }
        } catch (IOException e) {
            logger.error(String.valueOf(e));

            System.out.println("Impossible to read index file : " + indexFile.getPath());
            System.exit(1);
        }

        // Step 2: restore index
        Index index = null;
        try {
            index = Index.fromJSON(indexBuilder.toString());
        } catch (JsonProcessingException e) {
            logger.error(String.valueOf(e));

            System.out.println("Impossible to restore index from : " + indexFile.getPath());
            System.exit(1);
        }

        // Step 3: create new instance of BM25
        BM25 bm25 = new BM25(index);

        // Step 4: do the search and show results
        ArrayList<RankingResult> results = bm25.retrieveTopK(bm25.tokenize(queryFull), topK);

        System.out.println("Query : \"" + queryFull + "\"\n");

        for (RankingResult result : results) {
            int docIdx = result.getDocIndex();
            double score = result.getScore();
            System.out.println(
                    "file : " + bm25.getIndex().getDocumentName(docIdx) + " => score = " + score);
        }
    }
}
