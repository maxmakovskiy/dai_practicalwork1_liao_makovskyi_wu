package ch.heigvd.commands;

import ch.heigvd.bm25.BM25;
import ch.heigvd.bm25.utils.Index;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.io.*;
import java.nio.charset.StandardCharsets;

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


    @Override
    public void run() {
//        We collect content of src/main/resources/index.txt to string
//    String queryFull = String.join(" ", query);
    // Now parse index.txt
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
            System.out.println(e);
        }

        // Build index from string
        Index index = Index.importIndex(indexBuilder.toString());
//        Then we create new instance of BM25
        BM25 bm25 = new BM25(index);


//        And call bm25.retrieveTopK(bm25.tokenize(queryFull), 3)


//        In the end we could print user query and what is more importantly show the results of bm25.retrieveTopK

        System.out.println("Query : \"" + queryFull + "\"\n");

    }

}

