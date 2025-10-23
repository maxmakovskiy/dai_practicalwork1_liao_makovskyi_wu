package ch.heigvd;

import ch.heigvd.commands.Build;
import ch.heigvd.commands.Search;
import java.io.File;
import picocli.CommandLine;

@CommandLine.Command(
        description = "Best Match 25-th iteration",
        version = "1.0.0",
        showDefaultValues = true,
        subcommands = {
            Build.class,
            Search.class,
        },
        scope = CommandLine.ScopeType.INHERIT,
        mixinStandardHelpOptions = true)
public class Main {
    public static void main(String[] args) {

        // Source: https://stackoverflow.com/a/11159435
        String jarFilename =
                new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                        .getName();

        int exitCode = new CommandLine(new Main()).setCommandName(jarFilename).execute(args);
    }
}
