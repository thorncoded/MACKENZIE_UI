package com.example.mackenzie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LinkedList {

    Node head; // head of list

    // Constructor that reads the file and populates the linked list
    public LinkedList(String fileToRead) {
        populateNodes(fileToRead);
    }

    private void populateNodes(String fileToRead) {
        try (InputStream inputStream = LinkedList.class.getResourceAsStream(fileToRead);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2); // Split the line into parts based on delimiter "@"
                if (parts.length >= 2) {
                    String character = parts[0];
                    String dialog = parts[1];
                    insert(character, dialog);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new node
    public void insert(String character, String line) {
        Node new_node = new Node(character, line);
        if (head == null) {
            head = new_node;
        } else {
            Node last = head;
            while (last.next != null) {
                last = last.next;
            }
            last.next = new_node;
        }
    }

    // Method to print the LinkedList.
    public void printList() {
        Node currNode = head;

        System.out.print("LinkedList: ");

        while (currNode != null) {
            System.out.println(currNode.getCharacter() + " " + currNode.getLine());
            currNode = currNode.next;
        }
    }

    // Method to get the size of the LinkedList
    public int getSize() {
        int size = 0;
        Node currentNode = head;
        while (currentNode != null) {
            size++;
            currentNode = currentNode.next;
        }
        return size;
    }

    public Node getNodeAtIndex(int index) {
        if (index < 0 || index >= getSize()) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        Node currentNode = head;
        int currentIndex = 0;
        while (currentIndex < index) {
            currentNode = currentNode.next;
            currentIndex++;
        }
        return currentNode;
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList("dialog.csv");
        list.printList();
        System.out.println("Size of LinkedList: " + list.getSize());
    }
}
