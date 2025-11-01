package ch.heigvd.commands;

import ch.heigvd.dai.bm25.BM25;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = "build",
        description = "create index file from collection of documents",
        mixinStandardHelpOptions = true)
public class Build implements Runnable {

    @Parameters(paramLabel = "TARGET_DIR", description = "directory with files to build index from")
    File targetDir;

    @Option(
            names = {"-I", "--index"},
            paramLabel = "INDEX",
            description = "specify filename for index")
    String indexFilename = "index.txt";

    @Option(
            names = {"--fail-silently"},
            description =
                    "avoid printing all errors that happen during build stage. True by default",
            negatable = true,
            defaultValue = "true",
            fallbackValue = "true")
    boolean isFailSilently;

    @Override
    public void run() {
        System.out.println("Building index...");

        // Step 1: collect the names of all the files inside given folder
        File[] files = targetDir.listFiles();
        if (files == null) {
            System.out.println("You have provided an empty folder : " + targetDir.getPath());
            System.exit(0);
        }

        // Step 2: read content of each file and store it along with its name
        ArrayList<String> docNames = new ArrayList<>();
        ArrayList<String> docs = new ArrayList<>();

        for (File file : files) {
            if (file.isFile()) {
                docNames.add(file.getName());

                StringBuilder content = new StringBuilder();

                try (FileReader reader = new FileReader(file.getPath(), StandardCharsets.UTF_8);
                        BufferedReader buf = new BufferedReader(reader); ) {
                    int c;
                    while ((c = buf.read()) != -1) {
                        content.append((char) c);
                    }
                } catch (IOException e) {
                    if (!isFailSilently) {
                        System.err.print(e);
                    }

                    System.out.println("Impossible to read : " + file.getPath());
                    System.out.println("Skipping ...");

                    continue;
                }

                docs.add(content.toString());
            }
        }

        // Step 3: tokenize docs + build index
        BM25 bm25 = new BM25();
        ArrayList<ArrayList<String>> corpusTokens = bm25.tokenize(docs);
        bm25.buildIndex(corpusTokens, docNames);

        // Step 4: save index to the file
        // ref : https://stackoverflow.com/a/412495
        try (FileWriter writer = new FileWriter(new File(targetDir.getParent(), indexFilename));
                BufferedWriter buf = new BufferedWriter(writer); ) {
            String indexStr = bm25.getIndex().toJSON();
            buf.write(indexStr);
        } catch (IOException e) {
            if (!isFailSilently) {
                System.err.println(e);
            }

            System.out.println("Impossible to create index file : " + indexFilename);
            System.exit(1);
        }
    }
}
