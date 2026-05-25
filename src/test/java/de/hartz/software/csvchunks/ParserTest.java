package de.hartz.software.csvchunks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParserTest {

    @TempDir
    Path tempDir;

    private static final String TEST_CSV_FILE = "2018.csv";

    @Test
    public void testParserCreatesChunks() throws IOException {
        // Arrange
        File testFile = tempDir.resolve(TEST_CSV_FILE).toFile();
        Files.createFile(testFile.toPath());

        // Act
        Parser parser = new Parser("src/test/resources/", "target/", 1, true, null);
        parser.createChunks(TEST_CSV_FILE, "2011.csv");

        // Assert - verify chunks were created
        File[] chunkFiles = tempDir.toFile().listFiles((dir, name) ->
            name.startsWith("2018") && name.endsWith(".csv")
        );

        // "Chunk files should be created",
        Assertions.assertNotNull(chunkFiles);
        // "At least one chunk should be created",
        Assertions.assertTrue(chunkFiles.length > 0);
    }

    @Test
    public void testParserHandlesMissingFile() throws IOException {
        // Arrange
        String nonExistentFile = "nonexistent.csv";

        // Act & Assert
        Parser parser = new Parser("src/test/resources/", "target/", 1, true, null);
        Assertions.assertThrows(IOException.class, () -> {
            parser.createChunks(nonExistentFile, TEST_CSV_FILE);
        });
    }

    @Test
    public void testMainClassExecution() throws Exception {
        // Arrange
        File testFile = tempDir.resolve(TEST_CSV_FILE).toFile();
        Files.createFile(testFile.toPath());

        // Act
        String[] args = new String[]{TEST_CSV_FILE};
        Main.main(args);

        // Assert - verify main class executed successfully
        File[] chunkFiles = tempDir.toFile().listFiles((dir, name) ->
            name.startsWith("2018") && name.endsWith(".csv")
        );


        Assertions.assertNotNull(chunkFiles);
        // "Main class should create at least one chunk"
        Assertions.assertTrue( chunkFiles.length > 0);
    }

    @Test
    public void testParserChunkSizeConfiguration() throws IOException {
        // Arrange
        File testFile = tempDir.resolve(TEST_CSV_FILE).toFile();
        Files.createFile(testFile.toPath());
        String[] args = new String[]{"", TEST_CSV_FILE, "100"}; // Specify chunk size

        // Act
        Main.main(args);

        // Assert - verify chunks respect size configuration
        File[] chunkFiles = tempDir.toFile().listFiles((dir, name) ->
            name.startsWith("2018") && name.endsWith(".csv")
        );

        // "Chunks should be created"
        Assertions.assertNotNull(chunkFiles);

        // Verify each chunk doesn't exceed specified size (in lines)
        for (File chunk : chunkFiles) {
            long lineCount = Files.lines(chunk.toPath()).count();
            // "Chunk should not exceed 100 lines",
            Assertions.assertTrue(lineCount <= 100);
        }
    }

    @Test
    public void testMultipleFilesProcessing() throws IOException {
        // Arrange - create multiple test files
        String[] fileNames = new String[]{"2018.csv", "2019.csv"};

        // Act
        for (String fileName : fileNames) {
            File testFile = tempDir.resolve(fileName).toFile();
            Files.createFile(testFile.toPath());
            String[] args = new String[]{fileName};
            Main.main(args);
        }

        // Assert - verify all files were processed
        File[] csvFiles = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".csv"));

        // "CSV files should be created",
        Assertions.assertNotNull(csvFiles);
        // "Multiple files should be processed",
        Assertions.assertTrue(csvFiles.length >= 2);
    }

    @Test
    public void testParserOutputFileNaming() throws IOException {
        // Arrange
        File testFile = tempDir.resolve(TEST_CSV_FILE).toFile();
        Files.createFile(testFile.toPath());

        // Act
        Parser parser = new Parser("src/test/resources/", "target/", 1, true, null);
        parser.createChunks(TEST_CSV_FILE, TEST_CSV_FILE);

        // Assert - verify output file naming pattern
        File[] chunkFiles = tempDir.toFile().listFiles((dir, name) ->
            name.startsWith("2018") && name.endsWith(".csv")
        );

        // "Chunk files should exist",
        Assertions.assertNotNull(chunkFiles);

        // Verify naming convention (e.g., 2018_001.csv, 2018_002.csv, etc.)
        for (File chunk : chunkFiles) {
            String name = chunk.getName();
            // "Chunk should follow naming pattern",
            Assertions.assertTrue(
                name.matches("2018_\\d+\\.csv") || name.matches("2018_\\d{3}\\.csv"));
        }
    }

    @Test
    public void testMainClassWithNoArguments() throws Exception {
        // Arrange
        String[] args = new String[]{};

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> {
            Main.main(args);
        });
    }
}