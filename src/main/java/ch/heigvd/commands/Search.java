package ch.heigvd.commands;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.io.*;

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
        System.out.println("Searching...");
    }

}

