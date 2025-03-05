package swp.com.fxapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageRenamerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImageRenamerApp.class.getResource("image_renamer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Image File Renamer");

        // Try different approaches to load CSS
        URL cssResource = null;

        // Try different possible paths
        String[] cssPaths = {
                "styles.css",
                "/swp/com/fxapp/styles.css",
                "/styles.css"
        };

        for (String path : cssPaths) {
            cssResource = getClass().getResource(path);
            if (cssResource != null) {
                ImageLogger.logInfo("Found CSS at: " + path);
                scene.getStylesheets().add(cssResource.toExternalForm());
                break;
            }
        }

        if (cssResource == null) {
            ImageLogger.logWarning("Could not find CSS file. Please create styles.css in resources directory.");
        }

        // Try to load icon similarly
        InputStream iconStream = ImageRenamerApp.class.getResourceAsStream("icon.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        } else {
            ImageLogger.logWarning("Could not find icon.png");
        }

        stage.setScene(scene);
        stage.show();

        ImageLogger.logImageOperation("Application Start", "Application launched successfully");
    }

    public static void main(String[] args) {
        launch();
    }
}