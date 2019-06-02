package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class NodeApplication extends Application {

    private static NodeApplication instance;
    private Label counter;

    public NodeApplication() {
        instance = this;
    }

    synchronized static NodeApplication getInstance() {
        try {
            if (instance == null) {
                new Thread(() -> Application.launch(NodeApplication.class)).start();
                while (instance == null) {
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void start(Stage stage) {
        counter = new Label();
        counter.setAlignment(Pos.CENTER);
        counter.setFont(Font.font(28));

        StackPane root = new StackPane();
        root.getChildren().add(counter);
        root.setAlignment(Pos.CENTER);

        stage.setTitle("ZNode Counter");
        stage.setMinWidth(100);
        stage.setMinHeight(100);
        stage.setScene(new Scene(root));
        stage.show();
    }

    void update(String text) {
        Platform.runLater(() -> counter.setText(text));
    }
}
