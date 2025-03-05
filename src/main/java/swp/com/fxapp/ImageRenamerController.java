package swp.com.fxapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageRenamerController {
    @FXML
    private ListView<String> fileListView;
    @FXML
    private ImageView previewImageView;
    @FXML
    private TextField newNameTextField;
    @FXML
    private TextField prefixTextField;
    @FXML
    private CheckBox addCounterCheckBox;
    @FXML
    private Button renameButton;
    @FXML
    private Button selectFolderButton;
    @FXML
    private Label statusLabel;

    private File currentDirectory;
    private final ObservableList<String> imageFiles = FXCollections.observableArrayList();
    private final List<String> supportedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
    private FileRenameService renameService;

    @FXML
    public void initialize() {
        renameService = new FileRenameService();
        fileListView.setItems(imageFiles);
        setupListeners();
        renameButton.setDisable(true);
        ImageLogger.logInfo("Controller Initialized");
    }

    private void setupListeners() {
        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayImagePreview(newValue);
                renameButton.setDisable(false);
            } else {
                renameButton.setDisable(true);
            }
        });
    }

    @FXML
    protected void onSelectFolderClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Image Folder");
        currentDirectory = directoryChooser.showDialog(selectFolderButton.getScene().getWindow());

        if (currentDirectory != null) {
            loadImagesFromDirectory();
            ImageLogger.logInfo("Selected directory: " + currentDirectory.getAbsolutePath());
        }
    }

    private void loadImagesFromDirectory() {
        imageFiles.clear();
        try {
            List<String> files = Files.list(currentDirectory.toPath())
                    .filter(path -> isImageFile(path))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());

            imageFiles.addAll(files);
            statusLabel.setText(files.size() + " image files found");
            ImageLogger.logInfo("Loaded " + files.size() + " image files");
        } catch (Exception e) {
            showError("Error loading files", e.getMessage());
            ImageLogger.logWarning("Error loading files: " + e.getMessage());
        }
    }

    private boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return supportedExtensions.stream().anyMatch(fileName::endsWith);
    }

    private void displayImagePreview(String fileName) {
        try {
            File imageFile = new File(currentDirectory, fileName);
            Image image = new Image(imageFile.toURI().toString());
            previewImageView.setImage(image);
            previewImageView.setFitWidth(300);
            previewImageView.setFitHeight(300);
            previewImageView.setPreserveRatio(true);
        } catch (Exception e) {
            previewImageView.setImage(null);
            ImageLogger.logWarning("Failed to load preview for " + fileName + ": " + e.getMessage());
        }
    }

    @FXML
    protected void onRenameButtonClick() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile == null || currentDirectory == null) {
            return;
        }

        String newName = newNameTextField.getText();
        String prefix = prefixTextField.getText();
        boolean addCounter = addCounterCheckBox.isSelected();

        if (newName.isEmpty() && prefix.isEmpty()) {
            showError("Error", "Please provide a new name or prefix");
            return;
        }

        try {
            File file = new File(currentDirectory, selectedFile);
            String extension = getFileExtension(selectedFile);

            String finalName = prefix;

            if (!newName.isEmpty()) {
                finalName += newName;
            } else {
                finalName += removeExtension(selectedFile);
            }

            if (addCounter) {
                int count = findNextAvailableNumber(finalName, extension);
                finalName += "_" + count;
            }

            finalName += extension;

            File newFile = new File(currentDirectory, finalName);

            if (renameService.renameFile(file, newFile)) {
                ImageLogger.logInfo("Renamed: " + selectedFile + " -> " + finalName);
                statusLabel.setText("Renamed: " + selectedFile + " -> " + finalName);
                loadImagesFromDirectory();
            } else {
                showError("Rename Failed", "Could not rename the file");
            }
        } catch (Exception e) {
            showError("Error", e.getMessage());
            ImageLogger.logWarning("Error renaming file: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(dotIndex) : "";
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
    }

    private int findNextAvailableNumber(String baseName, String extension) {
        int counter = 1;
        File file = new File(currentDirectory, baseName + "_" + counter + extension);

        while (file.exists()) {
            counter++;
            file = new File(currentDirectory, baseName + "_" + counter + extension);
        }

        return counter;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}