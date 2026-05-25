package de.hartz.software.csvchunks;

import java.io.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Parser {
    private String inputDir;
    private String outputDir;
    private long maxFileSizeBytes;
    private boolean writeHeader;
    private String fileNamePattern;

    public Parser(String inputDir, String outputDir, long maxFileSizeMB, boolean writeHeader, String fileNamePattern) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;
        this.writeHeader = writeHeader;
        this.fileNamePattern = fileNamePattern != null && !fileNamePattern.isEmpty() ? fileNamePattern : "{name}-chunk-{counter}.csv";
    }

    public void processFiles() throws IOException {
        Path inputPath = Paths.get(inputDir);
        if (!Files.isDirectory(inputPath)) {
            throw new IOException("Input directory does not exist: " + inputDir);
        }

        Files.list(inputPath)
            .filter(path -> path.toString().endsWith(".csv"))
            .forEach(path -> {
                try {
                    processFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private void processFile(Path filePath) throws IOException {
        String fileName = filePath.getFileName().toString();
        createChunks(filePath.toString(), fileName);
    }

    public void createChunks(String fileName, String originalFileName) throws IOException {
        String headerLine = "";
        boolean first = true;
        int fileSize = 0;
        int fileCounter = 1;
        BufferedWriter fos = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (first) {
                    headerLine = line;
                    first = false;
                }

                // Check if we need a new chunk
                if (fileSize + line.getBytes(StandardCharsets.UTF_8).length > maxFileSizeBytes) {
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }

                    fileCounter++;
                    fos = new BufferedWriter(new FileWriter(getChunkName(originalFileName, fileCounter)));

                    if (writeHeader) {
                        fos.write(headerLine + "\n");
                        fileSize = headerLine.getBytes(StandardCharsets.UTF_8).length + 1;
                    } else {
                        fileSize = 0;
                    }
                    fos.write(line + "\n");
                    fileSize += line.getBytes(StandardCharsets.UTF_8).length + 1;
                } else {
                    fos.write(line + "\n");
                    fileSize += line.getBytes(StandardCharsets.UTF_8).length + 1;
                }
            }
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
    }

    private String getChunkName(String fileName, int counter) {
        String baseName = fileName.replace(".csv", "");
        String outputFileName = fileNamePattern
            .replace("{name}", baseName)
            .replace("{counter}", String.valueOf(counter));

        return outputDir + File.separator + outputFileName;
    }
}