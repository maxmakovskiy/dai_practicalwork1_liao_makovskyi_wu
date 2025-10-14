package ch.heigvd.commands;

import ch.heigvd.bm25.BM25;
import ch.heigvd.bm25.utils.Index;
import ch.heigvd.bm25.utils.RankingResult;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


@Command(
        name = "search",
        mixinStandardHelpOptions = true
)
public class Search implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "INDEX",
            description = "index file to work with"
    )
    File indexFile;

    @Parameters(
            arity = "1..*",
            index = "1..*",
            paramLabel = "QUERY",
            description = "user's query"
    )
    String[] query;

    @Option(
            names = { "-K", "--topK" },
            defaultValue = "3",
            description = "top k-results to show"
    )
    int topK;


    @Override
    public void run() {
        String queryFull = String.join(" ", query);

        // We collect content of src/main/resources/index.txt to string
        StringBuilder indexBuilder = new StringBuilder();
        try (
                FileReader reader = new FileReader(indexFile, StandardCharsets.UTF_8);
                BufferedReader buf = new BufferedReader(reader);
        ) {

            int c;
            while ((c = buf.read()) != -1) {
                indexBuilder.append((char)c);
            }

        } catch(IOException e) {
            System.err.println(e);
            System.out.println("Impossible to read index file : " + indexFile.getPath());
            System.exit(1);
        }

        // Build index from string
        Index index = null;
        try {
            index = Index.importIndex(indexBuilder.toString());
        } catch(RuntimeException e) {
            System.err.println(e);
            System.out.println("Impossible to restore index from : " + indexFile.getPath());
            System.exit(1);
        }

        // Then we create new instance of BM25
        BM25 bm25 = new BM25(index);

        // In the end we print user query and what is more importantly show the results of bm25.retrieveTopK
        ArrayList<RankingResult> results = bm25.retrieveTopK(
                bm25.tokenize(queryFull), topK);

        System.out.println("Query : \"" + queryFull + "\"\n");

        for (RankingResult result : results) {
            int docIdx = result.getDocIndex();
            double score = result.getScore();
            System.out.println("file : " + bm25.getIndex().getDocumentName(docIdx) + " => score = " + score);
        }

    }

}

