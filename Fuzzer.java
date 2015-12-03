import java.lang.Math;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.IOException;
import java.io.InputStream;

import java.security.SecureRandom;
import java.security.MessageDigest;

import java.time.Instant;

public class Fuzzer {
    private Path path;
    private byte[] fileBytes;

    private ProcessBuilder processBuilder;
    private Process process;

    private String output;

    private int fuzzFactor;

    /*
     * Sets path, converts input file to byte array,
     * sets up output path, sets fuzz factor
     */
    public Fuzzer(String filename, int fuzzFactor) throws IOException {
        path = Paths.get(filename);
        fileBytes = Files.readAllBytes(path);
        
        output = "./output/output" + getExtension(path.getFileName().toString());
        this.fuzzFactor = fuzzFactor;
    }

    /*
     * Insert random data into file, attempt to open it, and
     * detect errors caused by insertion of random data
     */
    public void fuzz(int iterations) throws IOException {
        FuzzerLogger logger = buildLogger(path, iterations, fileBytes.length);

        SecureRandom random = new SecureRandom();

        path = Paths.get(output);
        for(int i = 0; i < iterations; i++) {

            int writes = 1 + random.nextInt(fileBytes.length / fuzzFactor);
            
            /*
             * Generate a random byte and assign it to random index
             */ 
            byte[] randomBytes = new byte[1];
            for(int j = 0; j < writes; j++) {
                int randomIndex = random.nextInt(fileBytes.length);
                random.nextBytes(randomBytes);
                fileBytes[randomIndex] = randomBytes[0];
            }

            /*
             * Write modified file to output path
             */ 
            Files.write(path, fileBytes);

            /* 
             * Opens output file in default program (on Mac), 
             * may need to change command on Windows
             */
            processBuilder = new ProcessBuilder("open", output);
            process = processBuilder.start();

            String time = Instant.now().toString();

            /*
             * Log errors and save files that cause errors
             */ 
            InputStream error = process.getErrorStream();
            if(error.available() > 0) {
                String hexDigest = getHashString(time);
                String extension = getExtension(path.getFileName().toString());
                
                String filename = hexDigest + "." + extension;

                logger.createErrorFile(filename, fileBytes);
                logger.addError();
                logger.writeError(filename, error, time);
                logger.printErrorFound(filename);
            }

            /*
             * Write error files to errors directory
             */
            if(logger.getErrors() > 0) {
                String hashString = getHashString(time);
                String extension = getExtension(path.getFileName().toString());

                String filename = hashString + "." + extension;
                logger.createErrorFile(filename, fileBytes);
            }

            try {
                process.waitFor();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                process.destroyForcibly();
            }

            fileBytes = Files.readAllBytes(path);
        }

        logger.printStatistics();
        logger.writeStatisticsToLog();
    }

    /*
     * Sets all requisite information in the logger
     */
    public FuzzerLogger buildLogger(Path path, int iterations, int length) {
        FuzzerLogger logger  = new FuzzerLogger();
        logger.setPath(path);
        logger.setIterations(iterations);
        logger.setFileSize(fileBytes.length);
        return logger;
    }

    /*
     * Gets file extension from file name
     * and returns extension as string
     */
    public String getExtension(String filename) {
        StringBuilder sb = new StringBuilder();

        for(int i = filename.length() - 1; i >= 0; i--) {
            sb.append(filename.charAt(i));

            if(filename.charAt(i) == '.') {
                break;
            }
        }

        String toReturn = sb.reverse().toString();
        return toReturn;
    }

    public String getHashString(String s) {
        String hashString = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());
            byte[] hash  = digest.digest();
            hashString = new String(hash, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashString;
    }

    public static void main(String[] args) throws IOException {
        if(args.length  < 1) {
            System.out.println("\nPlease use the following format:\njava Fuzzer [filename] [fuzz factor] [iterations]\n");
            System.exit(1);
        }

        String filename = args[0];
        int fuzzFactor = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);

        Fuzzer fuzzer = new Fuzzer(filename, fuzzFactor);
        fuzzer.fuzz(iterations);
    }
}