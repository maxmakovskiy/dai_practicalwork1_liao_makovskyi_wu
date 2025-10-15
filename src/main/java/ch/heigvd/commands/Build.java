package ch.heigvd.commands;


import ch.heigvd.bm25.BM25;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Command(
        name = "build",
        description = "- create index file from collection of documents",
        mixinStandardHelpOptions = true
)
public class Build implements Runnable {

    @Parameters(
            paramLabel = "TARGET_DIR",
            description = "directory with files to build index from"
    )
    File targetDir;

    @Option(
            names = {"-I", "--index"},
            paramLabel = "INDEX",
            description = "specify filename for index"
    )
    String indexFilename = "index.txt";

    @Override
    public void run() {
        System.out.println("Building index...");

//        collect the names of all the files inside src/main/resources/documents folder
        File[] files = targetDir.listFiles();
        if (files == null) {
            System.out.println("You have provided an empty folder : " + targetDir.getPath());
            System.exit(0);
        }

        ArrayList<String> docNames = new ArrayList<>();
        ArrayList<String> docs = new ArrayList<>();


//        Read content of each file and store it in ArrayList along with its name
        for (File file : files) {
            if (file.isFile()) {
                docNames.add(file.getName());

                StringBuilder content = new StringBuilder();

                try (
                        FileReader reader = new FileReader(file.getPath(), StandardCharsets.UTF_8);
                        BufferedReader buf = new BufferedReader(reader);
                ) {

                    int c;
                    while ((c = buf.read()) != -1) {
                        content.append((char)c);
                    }

                } catch(IOException e) {
                    System.err.print(e);
                    System.out.println("Impossible to read : " + file.getPath());
                    System.out.println("Skipping ...");
                }

                docs.add(content.toString());
            }
        }


        BM25 bm25 = new BM25();
        ArrayList<ArrayList<String>> corpusTokens = bm25.tokenize(docs);
        bm25.buildIndex(corpusTokens,  docNames);

        
        // ref : https://stackoverflow.com/a/412495
        try (
            FileWriter writer = new FileWriter(new File(targetDir.getParent(), indexFilename));
            BufferedWriter buf = new BufferedWriter(writer);
        ) {


            // get current index and covert it to string
            String indexStr = bm25.getIndex().toString();
            buf.write(indexStr);


        } catch(IOException e) {
            System.err.println(e);
            System.out.println("Impossible to create index file : " + indexFilename);
            System.exit(1);
        }



    }

}