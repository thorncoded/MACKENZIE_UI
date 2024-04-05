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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;

public class TerminalApplication extends Application {

    private static final int MAX_LINES = 10; // Maximum number of lines to display

    private LinkedList ll = new LinkedList("test2.csv");
    private int lineIndex = 0; // Index to keep track of which line to display next
    private int currentOnScreenRows = 0;

    private ArrayList<Color> lineColors = new ArrayList<>(); // ArrayList to store the colors of each line
    private boolean isTyping = false; // Flag to indicate if typing is in progress

    //DOES NOT KEEP TRACK OF WHAT'S ON-SCREEN
    private ObservableList<Node> lineQueue = FXCollections.observableArrayList();

    //ONSCREENLINES
    private ObservableList<Node> onScreenLines = FXCollections.observableArrayList();

    private TextFlow terminalOutput;
    ImageView imageView;
    Image originalImage;
    Image lipsyncImage;


    //CALCULATE HOW MANY LINES A CERTAIN NODE ON SCREEN WILL TAKE UP.
    private Integer calculateLineNumber(Node node) {
        //get node text.
        String text = node.getLine() + "\n";
        int visibleCharacterCount = node.getLine().length(); // Count of visible characters (excluding newline)

        //create "ghost text" to try to calculate how many lines this is going to take up.
        Text ghostText = new Text(text);
        ghostText.setFill(node.getColor());
        ghostText.setFont(new Font("Consolas", 12));
        double terminalWidth = terminalOutput.getWidth();


        // Calculate the number of lines of this ghost text -- THIS WORKS.
        double layoutBoundsWidth = ghostText.getLayoutBounds().getWidth();
        double lineHeight = ghostText.getLineSpacing() + ghostText.getFont().getSize();
        int numLines = (int) Math.ceil(layoutBoundsWidth / terminalWidth);

        System.out.println(numLines);
        return numLines;

    }


    private int calculateLinesOnScreenSum() {
        int sum = 0;
        for (Node node : onScreenLines) {
            sum += node.getLinesOnScreen();
        }
        return sum;
    }


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

    private void slowType(Node node) {
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
            if (currentIndex[0] < text.length()) {
                char character = text.charAt(currentIndex[0]);
                appendText(Character.toString(character), node.getColor()); // Append current character to the terminal output
                currentIndex[0]++; // Move to the next character
                if (currentIndex[0] == text.length() - 1 && isMak) { //if we've reached the end and MAKSUR was talking, switch back to original image
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
        // Get the last text node in the terminal output
        Text lastTextNode = (Text) terminalOutput.getChildren().get(terminalOutput.getChildren().size() - 1);

        // Append the new text to the existing text
        lastTextNode.setText(lastTextNode.getText() + text);

        // Update the color of the text
        lastTextNode.setFill(color);

        // Add the color of the current line to the ArrayList
        lineColors.add(color);
    }




    private void appendMasterText(Node node) {
        /*Should create a new text node that is continuously updated with slowtype to type out message
        if it exceeds maximum line count, the oldest one is removed. while this is going on, make
        sure that the lipsync for MACKENZIE plays.
         */
        
        // Check if the node is associated with MACKENZIE.
        boolean isMak = node.getCharacter().equals("MACKENZIE");


        //creates ghost text to calculate number of lines
        Integer nodeLines = calculateLineNumber(node);
        node.setLinesOnScreen(nodeLines);

        //While the anticipated lines on-screen exceeds maxlines, delete from observable list
        while (calculateLinesOnScreenSum() + nodeLines > MAX_LINES) {
            // If sum exceeds the certain amount, remove objects based on FIFO rule
            Node removedObject = onScreenLines.remove(0); // Remove the first object (FIFO)
            System.out.println("Removed: " + removedObject.getLine()); // Optional: Print removed object
            terminalOutput.getChildren().remove(0);
            lineColors.remove(0);
        }

        //CREATE NEW NODE NOW.
        onScreenLines.add(node);
        lineColors.add(node.getColor());

        //create "ghost text" to try to calculate how many lines this is going to take up.
        Text realText = new Text();
        realText.setFill(node.getColor());
        realText.setFont(new Font("Consolas", 12));

        terminalOutput.getChildren().add(realText);


        if (!node.getCharacter().equals("LUCAS")){
            slowType(node);
        }
        else {
            //STATIC ADD. FOR LUCAS.
            realText.setText("\n" + node.getLine());
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
                myColor = nextNode.getColor();
                String character = nextNode.getCharacter();
                /*no slow type for lucas for now, might go a step further and create illusion of
                word by word text but that's costly and might not be worth time */
                //IF LUCAS IS SPEAKING, JUST APPEND NODE TO SCREEN. IF NOT, SLOW TYPE.
                appendMasterText(nextNode);

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
