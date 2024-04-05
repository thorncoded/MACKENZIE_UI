package com.example.mackenzie;

import javafx.scene.paint.Color;

/* Linked list Node*/
public class Node {
    String character;
    String line;
    String fileLocation;
    Color color;



    Integer linesOnScreen;

    Node next;

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void setColor(String character) {
        if (character.equals("skovak")) { //SKOVAK
            this.color = Color.web("#5e74d5");

        }
        else if (character.equals("MACKENZIE")) { //MAKSUR
            this.color = Color.web("#fb01a1");
        }
        else { //LUCAS
            this.color = Color.GREEN;
        }
    }

    public Color getColor() {
        return color;
    }

    public Integer getLinesOnScreen() {
        return linesOnScreen;
    }

    public void setLinesOnScreen(Integer linesOnScreen) {
        this.linesOnScreen = linesOnScreen;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    // Constructor to create a new node
    // Next is by default initialized
    // as null
    Node(String character, String line, String fileLocation) {
        setCharacter(character);
        setLine(line);
        setFileLocation(fileLocation);
        setColor(character);
    }

    Node(String character, String line) {
        setCharacter(character);
        setLine(line);
        setFileLocation("Null");
        setColor(character);
    }



}
