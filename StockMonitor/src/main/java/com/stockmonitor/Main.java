package com.stockmonitor;

import com.stockmonitor.ui.DashboardController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("/fxml/dashboard.fxml");
        if (resource == null) {
            throw new RuntimeException("Cannot find fxml/dashboard.fxml. Check your resources folder.");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        DashboardController controller = loader.getController();

        primaryStage.setTitle("Real-Time Stock Monitor");
        primaryStage.setScene(new Scene(root, 800, 600));
        
        primaryStage.setOnCloseRequest(e -> {
            controller.shutdown();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
