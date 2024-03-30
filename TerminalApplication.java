package com.example.mackenzie;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TerminalApplication extends Application {

    private TextFlow terminalOutput;

    private void processCommand(String command) {
        // Display the command entered by the user in green
        appendText("> " + command + "\n", Color.GREEN);

        // Simulate typing effect for "Command not recognized"
        slowType("Command not recognized. Type 'help' for a list of commands. \n", Color.MAGENTA);
    }

    private void slowType(String text, Color color) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(50 * i), event -> {
                appendText(text.substring(index, index + 1), color);
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    private void appendText(String text, Color color) {
        Text textNode = new Text(text);
        textNode.setFill(color);
        terminalOutput.getChildren().add(textNode);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Terminal");

        // Create a TextFlow for displaying output
        terminalOutput = new TextFlow();
        terminalOutput.setStyle("-fx-font-family: Consolas; -fx-font-size: 12; -fx-background-color: black;");

        // Create a text field for user input
        TextField inputField = new TextField();
        inputField.setStyle("-fx-font-family: Consolas; -fx-font-size: 12; -fx-background-color: black; -fx-text-fill: white;");
        inputField.setOnAction(event -> {
            processCommand(inputField.getText());
            inputField.clear(); // Clear the input field after processing the command
        });
        inputField.setPromptText("Type your command here");

        // Create a border pane to hold the terminal components
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: black;");
        root.setCenter(terminalOutput);
        root.setBottom(inputField);

        // Set up the scene
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Focus on input field when application starts
        Platform.runLater(inputField::requestFocus);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
