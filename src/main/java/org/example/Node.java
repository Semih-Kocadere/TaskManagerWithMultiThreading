package org.example;
import java.util.*;

public class Node extends Thread {
    String name; // Name of the node
    ArrayList<Node> waiting = new ArrayList<>(); // Nodes this node is waiting for
    ArrayList<Node> whoIsWaitingForMe = new ArrayList<>(); // Nodes waiting for this node
    boolean isFinished = false; // Status of the node
    public Object lock = new Object(); // Lock object for synchronization

    Random random = new Random();
    int noOfWait; // Number of nodes this node is waiting for

    public Node(String name) {
        this.name = name;
    }

    // Helper function to get names of nodes this node is waiting for
    public String writeWaitFunc(ArrayList<Node> waitFor) {
        String x = "";
        for (Node n : waitFor) {
            x += n.name + " ";
        }
        return x;
    }

    // Function to handle waiting condition
    public void noConditionOtherwise() {
        while (noOfWait > 0) {
            synchronized (lock) {
                System.out.println("Node " + this.name + " is waiting for Node " + this.writeWaitFunc(this.waiting));
                try {
                    lock.wait(waiting.size() * 2000); // Wait for a certain time
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        perform(); // Perform the task once waiting is over
    }

    // Function to notify nodes waiting for this node
    public void block() {
        for (Node n : this.whoIsWaitingForMe) {
            n.noOfWait--;
            if (n.noOfWait == 0 && !isFinished) {
                n.lock.notify(); // Notify the waiting node
            }
        }
    }

    // Function to perform the node's task
    public void perform() {
        if (!this.isFinished) {
            System.out.println("Node " + this.name + " being started");
            try {
                Thread.sleep(random.nextInt(2000)); // Simulate task execution time
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Node " + this.name + " is completed");
            this.isFinished = true;
            block(); // Notify other nodes after completion
        }
    }

    @Override
    public void run() {
        noConditionOtherwise(); // Start the waiting and performing process
    }
}