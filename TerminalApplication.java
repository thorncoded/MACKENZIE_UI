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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TerminalApplication extends Application {

    private static final int MAX_LINES = 23; // Maximum number of lines to display
    private TextFlow terminalOutput;
    private LinkedList ll = new LinkedList("part1.csv");
    private ArrayList<String> lines = new ArrayList<>();
    private int lineIndex = 0; // Index to keep track of which line to display next
    private ObservableList<Node> lineQueue = FXCollections.observableArrayList();
    private boolean isTyping = false; // Flag to indicate if typing is in progress
    ImageView imageView;
    Image originalImage;
    Image lipsyncImage;


    private void processCommand(String command) {
        // Display the command entered by the user in green
        appendText("\n" + "> " + command, Color.GREEN);
        // Simulate typing effect for "Command not recognized"
        slowType("\nCommand not recognized. Type 'help' for a list of commands.", Color.MAGENTA);
    }

    private void slowType(String text, Color color) {
        Timeline timeline = new Timeline();
        final int[] currentIndex = {0}; // Index to track the current character being typed

        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            if (currentIndex[0] < text.length()) {
                char character = text.charAt(currentIndex[0]);
                appendText(Character.toString(character), color); // Append current character to the terminal output
                currentIndex[0]++; // Move to the next character
            } else {
                isTyping = false; // Reset typing flag when typing completes
                startTypingFromQueue(); // Start typing the next queued line
                timeline.stop(); // Stop the timeline
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        isTyping = true; // Set typing flag when typing starts
        timeline.setCycleCount(text.length()); // Set cycle count to the total number of characters
        timeline.setOnFinished(event -> isTyping = false); // Reset isTyping flag when typing finishes
        timeline.play();
    }

    private void slowType(Node node, Color color) {
        String text = "\n" + node.getLine();
        int visibleCharacterCount = node.getLine().length(); // Count of visible characters (excluding newline)
        Timeline timeline = new Timeline();
        final int[] currentIndex = {0}; // Index to track the current character being typed

        // Check if the slow typing is associated with "mak"
        boolean isMak = node.getCharacter().equals("MACKENZIE");

        // Swap to another image when slow typing associated with "mak" starts
        if (isMak) {
            // Swap to another image
            imageView.setImage(lipsyncImage);
        }

        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            System.out.println(isMak);
            if (currentIndex[0] < text.length()) {
                char character = text.charAt(currentIndex[0]);
                appendText(Character.toString(character), color); // Append current character to the terminal output
                currentIndex[0]++; // Move to the next character
                if (currentIndex[0] == text.length() - 1 && isMak) {
                    imageView.setImage(originalImage);
                }
            } else {
                isTyping = false; // Reset typing flag when typing completes
                startTypingFromQueue(); // Start typing the next queued line
                timeline.stop(); // Stop the timeline
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        isTyping = true; // Set typing flag when typing starts
        timeline.setCycleCount(visibleCharacterCount); // Set cycle count to the total number of visible characters
        timeline.setOnFinished(event -> isTyping = false); // Reset isTyping flag when typing finishes
        timeline.play();
    }

    private void appendText(String text, Color color) {
        Text textNode = new Text(text);
        textNode.setFill(color);
        terminalOutput.getChildren().add(textNode);

        // Split the text into lines and count the number of lines
        String fullText = terminalOutput.getChildren().stream()
                .map(node -> ((Text) node).getText())
                .reduce("", (a, b) -> a + b);

        String[] lines = fullText.split("\n", -1); // Use -1 to ensure empty lines are preserved
        int numLines = lines.length;

        // Check if the maximum number of lines has been exceeded
        if (numLines > MAX_LINES) {
            // Calculate the length of the first line to remove it from the text
            int firstLineLength = lines[0].length() + 1; // +1 to account for the newline character

            // Remove the oldest complete line
            terminalOutput.getChildren().remove(0);

            // Remove the corresponding characters from the full text
            fullText = fullText.substring(firstLineLength);

            // Clear and re-add text nodes with the updated full text
            terminalOutput.getChildren().clear();
            appendText(fullText, color);
        }
    }

    // Modify addToQueue() to add nodes to lineQueue
    private void addToQueue(Node node) {
        lineQueue.add(node);
        if (!isTyping) {
            startTypingFromQueue();
        }
    }

    private void startTypingFromQueue() {
        if (!lineQueue.isEmpty()) {
            Node nextNode = lineQueue.remove(0);
            if (nextNode != null && nextNode.getLine() != null && !nextNode.getLine().isEmpty()) {
                Color myColor;
                //TO-DO: Make this more efficient
                if (nextNode.getCharacter().equals("skovak")) { //SKOVAK
                    myColor = Color.web("#5e74d5");

                }
                else if (nextNode.getCharacter().equals("MACKENZIE")) { //MAKSUR
                    myColor = Color.web("#fb01a1");
                }
                else { //LUCAS
                    myColor = Color.GREEN;
                }
                String character = nextNode.getCharacter();
                /*no slow type for lucas for now, might go a step further and create illusion of
                word by word text but that's costly and might not be worth time */
                if (character.equals("LUCAS")) {
                    appendText("\n" + nextNode.getLine(), myColor);
                }
                else {
                    slowType(nextNode, myColor); // Use line from node
                }

            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MACKENZIE.EXE");
        ll.printList();


        // Create a TextFlow for displaying output
        terminalOutput = new TextFlow();
        terminalOutput.setStyle("-fx-font-family: Consolas; -fx-font-size: 12; -fx-background-color: black;");

        // TextField for Future Input
        TextField inputField = new TextField();
        inputField.setStyle("-fx-font-family: Consolas; -fx-font-size: 12; -fx-background-color: black; -fx-text-fill: white;");
        inputField.setOnAction(event -> {
            processCommand(inputField.getText());
            inputField.clear(); // Clear the input field after processing the command
        });
        inputField.setPromptText("AWAITING COMMAND.");

        // Create side panel
        VBox sidePanel = new VBox();
        sidePanel.setAlignment(Pos.TOP_CENTER); // Set vertical alignment to TOP
        sidePanel.setSpacing(10);

        // Add pixel art image and load lipsync one
        originalImage = new Image(getClass().getResourceAsStream("makTest500.png"));
        lipsyncImage = new Image(getClass().getResourceAsStream("talkgif1.gif"));
        imageView = new ImageView(originalImage);
        imageView.setFitWidth(200); // Adjust as needed
        imageView.setFitHeight(200); // Adjust as needed
        sidePanel.getChildren().add(imageView);

        // Add Status Pane
        Image statusImage = new Image(getClass().getResourceAsStream("status.gif"));
        ImageView statusImageView = new ImageView(statusImage);
        statusImageView.setFitWidth(100);
        statusImageView.setFitHeight(46);
        sidePanel.getChildren().add(statusImageView);

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
        primaryStage.setResizable(false);
        primaryStage.show();

        // Focus on input field when application starts
        Platform.runLater(inputField::requestFocus);

        // Add a key event handler to the scene to listen for key presses
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q && !isTyping) {
                if (lineIndex < ll.getSize()) { // Check if end of file is reached
                    // Add the next line from the array to the queue only if typing is not in progress
                    addToQueue(ll.getNodeAtIndex(lineIndex));
                    lineIndex++; // Increment the index
                }
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
