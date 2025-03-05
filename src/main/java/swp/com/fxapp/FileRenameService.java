package swp.com.fxapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileRenameService {

    /**
     * Renames a file from source to target
     * @param sourceFile The original file
     * @param targetFile The destination file with new name
     * @return true if rename successful, false otherwise
     */
    public boolean renameFile(File sourceFile, File targetFile) {
        if (!sourceFile.exists()) {
            ImageLogger.logWarning("Source file does not exist: " + sourceFile.getPath());
            return false;
        }

        if (targetFile.exists()) {
            ImageLogger.logWarning("Target file already exists: " + targetFile.getPath());
            return false;
        }

        try {
            Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            ImageLogger.logImageOperation("File Rename",
                    "Renamed " + sourceFile.getName() + " to " + targetFile.getName());
            return true;
        } catch (IOException e) {
            ImageLogger.logError("Failed to rename file", e);
            return false;
        }
    }
}