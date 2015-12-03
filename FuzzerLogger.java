import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardCopyOption;

import java.time.Instant;

public class FuzzerLogger {
    public Path path;

    public int iterations;
    public int errors;
    public int fileSize;

    public String statsLog = "./logs/stats.txt";
    public String errorsLog = "./logs/errors.txt";

    public FuzzerLogger() {
        this.iterations = 0;
        errors = 0;
    }

    public void addError() {
        errors++;
    }

    public int getErrors() {
        return errors;
    }

    public void writeError(String filename, InputStream error, String time) throws IOException {
        String data = "\n\nFilename: " + filename + "\nTime: " + time + "\n";
        Files.write(Paths.get(errorsLog), data.getBytes(), StandardOpenOption.APPEND);

        byte[] errorBytes = new byte[error.available()];
        error.read(errorBytes);

        Files.write(Paths.get(errorsLog), errorBytes, StandardOpenOption.APPEND);
    }

    public void printErrorFound(String filename) {
        System.out.println("Error found in " + filename + ". Please check error logs.");
    }

    public void createErrorFile(String filename, byte[] fileBytes) throws IOException {
        Path errorPath = Paths.get("./errors/" + filename);
        Files.write(errorPath, fileBytes);
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    
    public void printStatistics() {
        System.out.println("STATS\n------");
        System.out.println("Path: " + path);
        System.out.println("File Size: " + fileSize + " bytes");
        System.out.println("Iterations: " + iterations);
        System.out.println("Errors: " + errors);
    }

    public void writeStatisticsToLog() throws IOException {
        String time = Instant.now().toString();
        String stats = "Time: " + time + "\nPath: " + path + "\nFile Size: " + fileSize + 
                       " bytes" + "\nIterations: " + iterations + "\nErrors: " + errors + "\n\n";

        Files.write(Paths.get(statsLog), stats.getBytes(), StandardOpenOption.APPEND);
    }

}