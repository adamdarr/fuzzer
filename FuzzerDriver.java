import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;

public class FuzzerDriver {

    public static void fuzzDirectory(String dir) throws IOException {
        DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(dir));
        for(Path path : ds) {
            System.out.println("\nFuzzing " + path.toString() + "\n");
            Fuzzer fuzzer = new Fuzzer(path.toString(), 200);
            fuzzer.fuzz(100);
        }
    }

    public static void main(String[] args) throws IOException {
        fuzzDirectory("./files/docx/");
        fuzzDirectory("./files/pdf/");
        fuzzDirectory("./files/txt/");
    }
}