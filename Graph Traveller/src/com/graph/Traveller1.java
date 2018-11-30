package com.graph;

import java.util.ArrayList;
import static com.graph.Conversion.*;
/**
 * @since 2.0
 * @version 1.1
 * @author marco
 *
 * The traveller is at a given node, checks where he can go.
 * Once he moves, he can't go back. So we must keep track of where he's been
 * in order to forbid him to go back to that node.
 *
 *
 * SIMULATION:
 * You're in S. You can go E or NE.
 * go east
 * You're in A. You can go E or N.   //notice he can't go West
 * go east
 * You're in D. You can go NE.      //notice he can only go ne
 * go NE.
 * Store score, which is the total distance.
 *
 */
public class Traveller1 {

    private Node position;
    private int totalDistance;
    private int number_of_movements;
    private ArrayList<String> allowed = new ArrayList<>(8); //to be reset at every loop


    public int getTotalDistance() {
        return totalDistance;
    }

    public Traveller1(){
        totalDistance = 0;
        position = map();
    }

    /**
     * Extracted from MIT lectures
     * @return
     */
    private Node map(){
        Node START = new Node('S');
        Node nodeA = new Node('A');
        Node nodeB = new Node('B');
        Node nodeC = new Node('C');
        Node nodeD = new Node('D');
        Node nodeE = new Node('E');
        Node GOAL = new Node('G', true);

        START.linkTo(nodeA, E, 3);
        START.linkTo(nodeB, NE, 5);
        nodeA.linkTo(nodeB, N, 4);
        nodeB.linkTo(nodeC, N, 4);
        nodeC.linkTo(nodeE, E, 6);
        nodeA.linkTo(nodeD, E, 3);
        nodeD.linkTo(GOAL, NE, 5);

        return START;
    }


    /**
     * Move
     *
     * @param destination where we go
     * @return the distance covered
     */
    @Deprecated
    private double move(String destination) {
        double length = 0;
        if (destination.equals(N) && isAllowed(N)) {
            length = position.links[north].length;
            position = position.north;
        }
        if (destination.equals(E) && isAllowed(E)) {
            length = position.links[east].length;
            position = position.east;
        }
        if (destination.equals(S) && isAllowed(S)) {
            length = position.links[south].length;
            position = position.south;
        }
        if (destination.equals(W) && isAllowed(W)) {
            length = position.links[west].length;
            position = position.west;
        }
        if (destination.equals(NW) && isAllowed(NW)) {
            length = position.links[northWest].length;
            position = position.northWest;
        }
        if (destination.equals(SW) && isAllowed(SW)) {
            length = position.links[southWest].length;
            position = position.southWest;
        }
        if (destination.equals(NE) && isAllowed(NE)) {
            length = position.links[northEast].length;
            position = position.northEast;
        }
        if (destination.equals(SE) && isAllowed(SE)) {
            length = position.links[southEast].length;
            position = position.southEast;
        }
        totalDistance += length;
        return length;
    }

    /**
     * Move better, by printing additional information
     * @since 2.0
     * @param destination where you wanna go
     * @return the distance
     */
    @Deprecated
    private double moveBetter(String destination){
        double distance = move(destination);
        if(distance <= 0) //This can't happen now because it will always choose an allowed direction
            System.out.println("Moving " + lengthen(destination.toLowerCase()) +" was not an option");
        else
            System.out.println("I've moved by " + distance + " to " + lengthen(destination.toLowerCase()));

        number_of_movements++;
        return distance;
    }

    /**
     * @since 1.1
     * @param destination where to go
     * @return distance travelled
     */
    private double moveTraveller(String destination){
        double distance = 0;
        if (destination.equals(N) && isAllowed(N)) {
            distance = position.links[north].length;
            position = position.north;
        } else if (destination.equals(E) && isAllowed(E)) {
            distance = position.links[east].length;
            position = position.east;
        } else if (destination.equals(S) && isAllowed(S)) {
            distance = position.links[south].length;
            position = position.south;
        } else if (destination.equals(W) && isAllowed(W)) {
            distance = position.links[west].length;
            position = position.west;
        } else if (destination.equals(NW) && isAllowed(NW)) {
            distance = position.links[northWest].length;
            position = position.northWest;
        } else if (destination.equals(SW) && isAllowed(SW)) {
            distance = position.links[southWest].length;
            position = position.southWest;
        } else if (destination.equals(NE) && isAllowed(NE)) {
            distance = position.links[northEast].length;
            position = position.northEast;
        } else if (destination.equals(SE) && isAllowed(SE)) {
            distance = position.links[southEast].length;
            position = position.southEast;
        }

        System.out.println("I've moved by " + distance + " to " + lengthen(destination.toLowerCase()));
        number_of_movements++;
        totalDistance += distance;
        return distance;
    }

    //-------------------------------------------------------------------

    private void youWon() {
        if (arrived())
            System.out.println("Great job, now you're free");
        else
            System.out.println("Follow the white rabbit");
    }

    /**
     * @return if we are in a node whose final property is true
     * @since 1.0
     */
    private boolean arrived() {
        return position.finalNode;
    }

    private void yourPosition() {
        System.out.println("I am at node " + position.label);
    }


    /**
     * a direction is allowed if the node connected along that direction is not null
     *
     * @param destination where to go
     * @return true if allowed
     * @since 1.0
     */
    private boolean isAllowed(String destination) {
        switch (destination) {
            case N:
                return position.north != null;
            case S:
                return position.south != null;
            case E:
                return position.east != null;
            case W:
                return position.west != null;
            case NE:
                return position.northEast != null;
            case SE:
                return position.southEast != null;
            case NW:
                return position.northWest != null;
            case SW:
                return position.southWest != null;
        }

        return false;
    }

    /**
     * First get an array (expandable) of the possible directions,
     * the try to render it in a human and grammatically correct way
     *
     * @since 1.0
     */
    private void allowedDirections() {
        ArrayList<String> allowed = new ArrayList<>(4); //initialize with  1 <= initial capacity <= 4

        for (String direction : DIRECTIONS)
            if (isAllowed(direction))
                allowed.add(direction);

        allowed.trimToSize();

        if (allowed.size() == 0 && !arrived()) //right now this can't happen
            gameOver();

        else if (allowed.size() == 1)
            System.out.print("I can only go " + allowed.get(0));

        else {
            System.out.print("I can go " + allowed.get(0));
            for (int i = 1; i < allowed.size(); i++)
                if (i < allowed.size() - 1)
                    System.out.print(", " + allowed.get(i));
                else
                    System.out.print(" or " + allowed.get(i));
        }
        System.out.println();
    }
    private void gameOver(){
        System.out.println("Stupid intelligence.\nGAME OVER");
    }


    private static void MITMapDrawing(){
        System.out.println("\tMIT map");
        System.out.println();
        System.out.println("    C------E");
        System.out.println("    |");
        System.out.println("    |");
        System.out.println("    B      G");
        System.out.println("  / |     /");
        System.out.println(" /  |    /");
        System.out.println("S---A---D");
        System.out.println();
    }


    public static void main(String[] args) throws InterruptedException {
        MITMapDrawing();

        Thread.sleep(2000);
        Traveller1 dumbTraveller1 = new Traveller1();

        while (true) {

            System.out.println();
            dumbTraveller1.yourPosition();

            System.out.println("Computing possible directions...");
            for (String direction : DIRECTIONS)
                if (dumbTraveller1.isAllowed(direction))
                    dumbTraveller1.allowed.add(direction);

            dumbTraveller1.allowedDirections();

            int randomPath = (int)(Math.random() * dumbTraveller1.allowed.size());
            String randomDirection = dumbTraveller1.allowed.get(randomPath);

            dumbTraveller1.moveTraveller(randomDirection);

            if (dumbTraveller1.arrived()) {
                dumbTraveller1.allowedDirections();
                System.out.println("In total I've travelled " + dumbTraveller1.getTotalDistance() + " units," +
                        " moving " + dumbTraveller1.number_of_movements + " times");
                System.exit(0);
            }

            //reset the allowed directions
            dumbTraveller1.allowed.clear();
        }

    }


    /**
     * Each node will be connected at least to one other node.
     * Only one link is allowed in each direction,
     * a node can have links in each direction.
     * Each node has a link attribute. There are eight possible links, we initialize an array of
     * links. Each direction is encoded by a magic constant.
     */
    public class Node {
        char label;
        Link[] links = new Link[8];

        Node north, east, south, west, northWest, southWest, northEast, southEast;
        boolean finalNode;

        /* Constructors */
        private Node(char letter, boolean finalNode) {
            label = letter;
            this.finalNode = finalNode;
        }

        private Node(char letter) {
            this(letter, false);
        }
        /* End of constructors*/

        /**
         * Method that links two nodes.
         * Nodes cannot be null.
         * If the direction is already occupied to connect some node, print some error message
         *
         * @param otherNode is the node that is connected to the current one
         * @param direction is the direction in which we move to find the other node
         * @since 1.0
         * @throws Exception when trying to overwrite an existing node
         * <p>
         * to update with new points
         */
        void linkTo(Node otherNode, String direction, double distance) {
            if (otherNode == null) {
                System.out.println("The other node must be initialised"); //want this to manage nodes more easily
                System.exit(1);
            }
            try {
                switch (direction) {
                    case N:
                        if (this.north != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.north.label);
                        this.north = otherNode;
                        otherNode.south = this;
                        this.links[0] = new Link(this, otherNode, distance);
                        otherNode.links[2] = new Link(otherNode, this, distance);
                        break;
                    case E:
                        if (this.east != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.east.label);
                        this.east = otherNode;
                        otherNode.west = this;
                        this.links[1] = new Link(this, otherNode, distance);
                        otherNode.links[3] = new Link(otherNode, this, distance);
                        break;
                    case S:
                        if (this.south != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.south.label);
                        this.south = otherNode;
                        otherNode.north = this;
                        this.links[2] = new Link(this, otherNode, distance);
                        otherNode.links[0] = new Link(otherNode, this, distance);
                        break;
                    case W:
                        if (this.west != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.west.label);
                        this.west = otherNode;
                        otherNode.east = this;
                        this.links[3] = new Link(this, otherNode, distance);
                        otherNode.links[1] = new Link(otherNode, this, distance);
                        break;
                    case NW:
                        if (this.northWest != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.northWest.label);
                        this.northWest = otherNode;
                        otherNode.southEast = this;
                        this.links[4] = new Link(this, otherNode, distance);
                        otherNode.links[7] = new Link(otherNode, this, distance);
                        break;
                    case SW:
                        if (this.southWest != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.southWest.label);
                        this.southWest = otherNode;
                        otherNode.northEast = this;
                        this.links[5] = new Link(this, otherNode, distance);
                        otherNode.links[6] = new Link(otherNode, this, distance);
                        break;
                    case NE:
                        if (this.northEast != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.northEast.label);
                        this.northEast = otherNode;
                        otherNode.southWest = this;
                        this.links[6] = new Link(this, otherNode, distance);
                        otherNode.links[5] = new Link(otherNode, this, distance);
                        break;
                    case SE:
                        if (this.southEast != null)
                            throw new Exception("Trying to overwrite " + this.label + " - " + this.southEast.label);
                        this.southEast = otherNode;
                        otherNode.northWest = this;
                        this.links[7] = new Link(this, otherNode, distance);
                        otherNode.links[4] = new Link(otherNode, this, distance);
                        break;

                    default:
                        throw new Exception("Input was not valid, fatal error!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Class is made of two nodes, defines a link.
     * Has a length.
     */
    private class Link {
        double length;
        Node a, b;

        Link(Node a, Node b, double length) {
            this.length = length;
            this.a = a;
            this.b = b;
        }
    }

}
