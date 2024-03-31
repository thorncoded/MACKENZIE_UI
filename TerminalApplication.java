package com.example.mackenzie;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class TerminalApplication extends Application {

    private TextFlow terminalOutput;
    private String[] lines = {"Line 1", "Line 2", "Line 3"}; // Example array of lines
    private int lineIndex = 0; // Index to keep track of which line to display next
    private ObservableList<String> lineQueue = FXCollections.observableArrayList();

    private void processCommand(String command) {
        // Display the command entered by the user in green
        appendText("> " + command + "\n", Color.GREEN);
        // Simulate typing effect for "Command not recognized"
        slowType("Command not recognized. Type 'help' for a list of commands.\n", Color.MAGENTA, null);
    }

    private void slowType(String text, Color color, Runnable onComplete) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(50 * i), event -> {
                appendText(text.substring(index, index + 1), color);
                if (index == text.length() - 1 && onComplete != null) {
                    onComplete.run(); // Execute the callback after typing completes
                }
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

    private void addToQueue(String line) {
        lineQueue.add(line);
        if (lineQueue.size() == 1) {
            startTypingFromQueue();
        }
    }

    private void startTypingFromQueue() {
        if (!lineQueue.isEmpty()) {
            String nextLine = lineQueue.remove(0);
            slowType(nextLine + "\n", Color.PINK, this::startTypingFromQueue);
        }
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
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

        // Create side panel
        VBox sidePanel = new VBox();
        sidePanel.setAlignment(Pos.TOP_CENTER); // Set vertical alignment to TOP
        sidePanel.setSpacing(10);

        // Add pixel art image
        FileInputStream input = new FileInputStream("src\\main\\java\\com\\example\\mackenzie\\makTest500.png");
        Image pixelArtImage = new Image(input);
        ImageView imageView = new ImageView(pixelArtImage);
        imageView.setFitWidth(200); // Adjust as needed
        imageView.setFitHeight(200); // Adjust as needed
        sidePanel.getChildren().add(imageView);

        // Add static information
        Label staticInfoLabel = new Label("Static Information");
        staticInfoLabel.setTextAlignment(TextAlignment.CENTER);
        sidePanel.getChildren().add(staticInfoLabel);

        // Create a VBox to hold the TextFlow and TextField
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.BOTTOM_LEFT);
        centerBox.getChildren().addAll(terminalOutput, inputField);

        // Create a border pane to hold the terminal components and side panel
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: black;");
        root.setRight(sidePanel); // Placing side panel on the right
        root.setCenter(centerBox); // Terminal on the left, Input field at the bottom

        // Set up the scene
        Scene scene = new Scene(root, 800, 400); // Increased width to accommodate side panel
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Focus on input field when application starts
        Platform.runLater(inputField::requestFocus);

        // Add a key event handler to the scene to listen for key presses
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q && lineQueue.isEmpty()) {
                // Add the next line from the array to the queue only if the queue is empty
                addToQueue(lines[lineIndex]);
                lineIndex = (lineIndex + 1) % lines.length; // Move to the next line, wrap around if necessary
            }
        });

        // Add a mouse event handler to the root pane to remove focus from the input field when clicked outside
        root.setOnMouseClicked(event -> {
            if (!inputField.getBoundsInParent().contains(event.getX(), event.getY())) {
                inputField.getParent().requestFocus(); // Request focus for the parent node of the input field
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
