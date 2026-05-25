package de.hartz.software.csvchunks;

public class Main {

    public static void main(String[] args) {
        printUsage();

        // Parse command line arguments
        String inputDir = null;
        String outputDir = null;
        long maxFileSizeMB = 9; // Default: ~10MB
        boolean writeHeader = true;
        String fileNamePattern = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i":
                case "--input":
                    inputDir = args[++i];
                    break;
                case "-o":
                case "--output":
                    outputDir = args[++i];
                    break;
                case "-s":
                case "--size":
                    try {
                        maxFileSizeMB = Long.parseLong(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("Error: Invalid file size value");
                        System.exit(1);
                    }
                    break;
                case "-h":
                case "--header":
                    writeHeader = Boolean.parseBoolean(args[++i]);
                    break;
                case "-p":
                case "--pattern":
                    fileNamePattern = args[++i];
                    break;
                case "-?":
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Unknown option: " + args[i]);
                    printUsage();
                    System.exit(1);
            }
        }

        // Apply defaults
        if (inputDir == null) inputDir = ".";
        if (outputDir == null) outputDir = ".";

        // Create and run parser
        try {
            Parser parser = new Parser(inputDir, outputDir, maxFileSizeMB, writeHeader, fileNamePattern);
            System.out.println("Starting file chunking...");
            System.out.println("Input: " + inputDir);
            System.out.println("Output: " + outputDir);
            System.out.println("Max file size: " + maxFileSizeMB + " MB");
            System.out.println("Write header: " + writeHeader);
            System.out.println("File pattern: " + (fileNamePattern != null ? fileNamePattern : "{name}-chunk-{counter}.csv"));
            System.out.println();

            parser.processFiles();
            System.out.println("Done!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("CSV File Chunker - Splits large CSV files into smaller chunks");
        System.out.println();
        System.out.println("Usage: java Main [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -i, --input <dir>       Input directory containing CSV files (default: .)");
        System.out.println("  -o, --output <dir>      Output directory for chunks (default: .)");
        System.out.println("  -s, --size <MB>         Maximum chunk size in MB (default: 9)");
        System.out.println("  -h, --header <bool>     Write header to each chunk (default: true)");
        System.out.println("  -p, --pattern <pat>     File name pattern (default: {name}-chunk-{counter}.csv)");
        System.out.println("                          Placeholders: {name}, {counter}");
        System.out.println("  -?, --help              Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main -i ./data -o ./chunks -s 5");
        System.out.println("  java Main --pattern \"data-{counter}.csv\" -s 10");
    }
}