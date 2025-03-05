package swp.com.fxapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileRenameServiceTest {

    @TempDir
    Path tempDir;

    private FileRenameService renameService;
    private File sourceFile;

    @BeforeEach
    void setUp() throws IOException {
        renameService = new FileRenameService();
        sourceFile = new File(tempDir.toFile(), "test-image.jpg");
        Files.createFile(sourceFile.toPath());
    }

    @Test
    void testRenameSuccessful() {
        File targetFile = new File(tempDir.toFile(), "renamed-image.jpg");

        boolean result = renameService.renameFile(sourceFile, targetFile);

        assertTrue(result);
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
    }

    @Test
    void testRenameFailsIfSourceDoesNotExist() {
        File nonExistentSource = new File(tempDir.toFile(), "non-existent.jpg");
        File targetFile = new File(tempDir.toFile(), "renamed-image.jpg");

        boolean result = renameService.renameFile(nonExistentSource, targetFile);

        assertFalse(result);
    }

    @Test
    void testRenameFailsIfTargetExists() throws IOException {
        File targetFile = new File(tempDir.toFile(), "existing-target.jpg");
        Files.createFile(targetFile.toPath());

        boolean result = renameService.renameFile(sourceFile, targetFile);

        assertFalse(result);
        assertTrue(sourceFile.exists());
        assertTrue(targetFile.exists());
    }
}