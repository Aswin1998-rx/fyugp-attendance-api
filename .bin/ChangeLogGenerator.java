import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.output.MarkdownRender;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class ChangeLogGenerator {
    public static void main(String[] args) throws IOException, InterruptedException {
        final var references = getReferences();
        final var diff = OpenApiCompare.fromContents(
                getFileFromRef(references[0]),
                getFileFromRef(references[1])
        );

        Path target = Path.of("target");
        if (!Files.exists(target)) {
            Files.createDirectory(target);
        }

        FileWriter fileWriter = new FileWriter("target/changelog.md");
        final var renderer = new MarkdownRender();
        renderer.setShowChangedMetadata(true);
        renderer.render(diff, fileWriter);
    }

    private static String[] getReferences() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("use default settings? [Y/n] ");
        String defaults = scanner.nextLine();
        if (defaults.equals("Y") || defaults.equals("")) {
            return new String[]{"main", getCurrentBranch()};
        }
        System.out.print("base ref (default main): ");
        String base = scanner.nextLine().strip();
        if (base.equals("")) {
            base = "main";
        }
        System.out.print("current ref (default HEAD) : ");
        String current = scanner.nextLine().strip();
        if (current.equals("")) {
            current = getCurrentBranch();
        }
        scanner.close();
        return new String[]{base, current};
    }

    private static String getFileFromRef(String ref) throws IOException, InterruptedException {
	    return exec("git", "show", "%s:src/main/resources/static/openapi.yaml".formatted(ref));
   }

    private static String getCurrentBranch() throws IOException, InterruptedException {
	    return exec("git", "rev-parse", "--abbrev-ref","HEAD").replace("\n", "").replace("\r", "");
    }

    private static String exec(String... args) throws IOException, InterruptedException {
	    final var processBuilder = new ProcessBuilder(args);
	    System.out.println(Arrays.toString(args));
	    processBuilder.redirectErrorStream(true);
	    final var process = processBuilder.start();
	    final var mainOpenApiFile = new StringBuilder();
	    final var mainFileReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = null;
	    while ((line = mainFileReader.readLine()) != null) {
		    mainOpenApiFile.append(line).append('\n');
	    }
	    mainFileReader.close();
	    final var exitCode = process.waitFor();
	    if (exitCode != 0) {
		    System.exit(1);
	    }
	    return mainOpenApiFile.toString();
    }


}
