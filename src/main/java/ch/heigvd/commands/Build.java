package ch.heigvd.commands;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Command(
        name = "build",
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
    }

}