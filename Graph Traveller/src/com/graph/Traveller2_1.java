package com.graph;
import java.util.ArrayList;

import static com.graph.Conversion.*;

/**
 * @author marco
 * <p>
 * The traveller is at a given node, checks where he can go.
 * Once he moves, he can't go back.
 * <p>
 * It should have memory of the it has gone in the previous runtime
 * If it reaches the goal, it wins, if it is stuck in a deadend, it loses
 * @version 2.1
 *
 * Loops the code thousands of times, to evaluate some statistics
 */
public class Traveller2_1 {

    /*----------------ATTRIBUTES--------------*/
    private Node position;
    private int totalDistance, number_of_movements;
    private ArrayList<String> allowed = new ArrayList<>(8); //to be reset at every loop
    private ArrayList<String> visited = new ArrayList<>(3);
    /*----------------------------------------*/

    private Traveller2_1() {
        totalDistance = 0;
        position = myMap();
    }

    private static final int ATTEMPTS = 1000;
    private static int successes = 0, failures = 0;
    private static int totalMoves = 0;

    public static void main(String[] args) throws InterruptedException {

//        MITMapDrawing();
        Thread.sleep(2000);

        Traveller2_1 dumbTraveller;

        for (int i = 0; i < ATTEMPTS; i++) {

            dumbTraveller = new Traveller2_1();

            while (true) {
                dumbTraveller.yourPosition();

                System.out.println("Computing possible directions...");
                for (String direction : DIRECTIONS)
                    if (dumbTraveller.isAllowed(direction))
                        dumbTraveller.allowed.add(direction);

                try {
                    dumbTraveller.allowedDirections();
                    int randomPath = (int) (Math.random() * dumbTraveller.allowed.size());
                    String randomDirection = dumbTraveller.allowed.get(randomPath);
                    dumbTraveller.moveTraveller(randomDirection);

                } catch (Exception e) {
                    dumbTraveller.gameOver();
                    totalMoves += dumbTraveller.number_of_movements;
                    break;
                }

                if (dumbTraveller.isArrived()) {
                    System.out.println("\nIn total I've travelled " + dumbTraveller.getTotalDistance() + " units," +
                            " moving " + dumbTraveller.number_of_movements + " times");
                    dumbTraveller.youWon();
                    totalMoves += dumbTraveller.number_of_movements;
                    break;
                }

                //reset the allowed directions
                dumbTraveller.allowed.clear();
            }
        }
        double successRatio = ((double)successes / (double)ATTEMPTS) * 100; //in percentage
        System.out.println("\n\nIn " + ATTEMPTS + " attempts: " + failures + " failures, " + successes + " successes");
        System.out.println("Success ratio is " + successRatio + "%");
        System.out.println("Total moves in " + ATTEMPTS+ " attempts: " + totalMoves);
        System.out.println("The average number of moves per match is: " + Math.round((double)totalMoves/(double) ATTEMPTS));
    }

    /*--------------------MAPS---------------*/
    private Node map() {
        // Nodes
        Node START = new Node("S");
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node GOAL = new Node("G", true);
        // Links
        START.linkTo(nodeA, E, 3);
        START.linkTo(nodeB, NE, 5);
        nodeA.linkTo(nodeB, N, 4);
        nodeB.linkTo(nodeC, N, 4);
        nodeC.linkTo(nodeE, E, 6);
        nodeA.linkTo(nodeD, E, 3);
        nodeD.linkTo(GOAL, NE, 5);

        return START;
    }
    private Node myMap(){
        Node START = new Node ("start");
        Node nodeA = new Node ("a");
        Node nodeB = new Node("b");
        Node nodeC = new Node("c");
        Node nodeD = new Node ("d");
        Node nodeE = new Node ("e");
        Node nodeF = new Node ("f");
        Node nodeG = new Node ("g");
        Node nodeH = new Node ("h");
        Node GOAL = new Node("Goal", true);

        /*
                 g-----h
                /|     |
              /  |   GOAL
             e---f
             |   |
             |   |
             b---c---d
           / |
          /  |
         s---a
         */


        //Links must be > 11
        START.linkTo(nodeA, E,1);
        START.linkTo(nodeB, NE,1);
        nodeA.linkTo(nodeB,N,1);
        nodeB.linkTo(nodeE, N, 1);
        nodeB.linkTo(nodeC, E, 1);
        nodeC.linkTo(nodeD, E, 1);
        nodeC.linkTo(nodeF, N, 1);

        nodeE.linkTo(nodeF, E ,1 );
        nodeE.linkTo(nodeG, NE, 1);

        nodeF.linkTo(nodeG, N, 1);

        nodeG.linkTo(nodeH, E, 1);
        nodeH.linkTo(GOAL, S, 1);

        return START;
    }

    /*-----------------GETTERS--------------*/
    int getTotalDistance() {
        return totalDistance;
    }

    int getAllowedDirections() {
        return allowed.size();
    }
    /*---------------------------------------*/


    /*---------------------PRINTING-------------------*/
    /**
     * First get an array (expandable) of the possible directions,
     * the try to render it in a human and grammatically correct way
     *
     * @since 1.0
     */
    private void allowedDirections() throws IndexOutOfBoundsException {

        ArrayList<String> allowed = new ArrayList<>(4); //initialize with  1 <= initial capacity <= 4

        for (String direction : DIRECTIONS)
            if (isAllowed(direction))
                allowed.add(direction);

        allowed.trimToSize();

        if (allowed.size() == 0 && !isArrived()) { //right now this can't happen
            throw new IndexOutOfBoundsException("No more options left");

        } else if (allowed.size() == 1)
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
    private void yourPosition() {
        System.out.println("\nI am in " + position.label);
    }
    private void gameOver() {
        failures++;
        System.out.println("You never learn.\nGAME OVER");
    }
    private void youWon() {
        successes++;
        System.out.println("Great job, now you're free");
    }
    /*------------------------------------------------*/


    /**
     * Checks if the label in which it wants to move is in the list of the visited nodes.
     * If not, move.
     * Increases the number of moves.
     * For previous implementation check Traveller1 class
     * Also checks
     *
     * @param destination where to go
     * @return distance travelled
     * @since 1.1
     */
    private double moveTraveller(String destination) {
        double distance = 0;

        if (destination.equals(N) && isAllowed(N)) {
            distance = position.links[north].length;
            visited.add(position.label);
            position = position.north;
        } else if (destination.equals(E) && isAllowed(E)) {
            distance = position.links[east].length;
            visited.add(position.label);
            position = position.east;
        } else if (destination.equals(S) && isAllowed(S)) {
            distance = position.links[south].length;
            visited.add(position.label);
            position = position.south;
        } else if (destination.equals(W) && isAllowed(W)) {
            distance = position.links[west].length;
            visited.add(position.label);
            position = position.west;
        } else if (destination.equals(NW) && isAllowed(NW)) {
            distance = position.links[northWest].length;
            visited.add(position.label);
            position = position.northWest;
        } else if (destination.equals(SW) && isAllowed(SW)) {
            distance = position.links[southWest].length;
            visited.add(position.label);
            position = position.southWest;
        } else if (destination.equals(NE) && isAllowed(NE)) {
            distance = position.links[northEast].length;
            visited.add(position.label);
            position = position.northEast;
        } else if (destination.equals(SE) && isAllowed(SE)) {
            distance = position.links[southEast].length;
            visited.add(position.label);
            position = position.southEast;
        }

        System.out.println("I've moved by " + distance + " to " + lengthen(destination.toLowerCase()));
        number_of_movements++;
        totalDistance += distance;
        return distance;
    }
    /**
     * @return if we are in a node whose final property is true
     * @since 1.0
     */
    private boolean isArrived() {
        return position.finalNode;
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
                return (position.north != null) && !visited.contains(position.north.label);
            case S:
                return (position.south != null) && !visited.contains(position.south.label);
            case E:
                return (position.east != null) && !visited.contains(position.east.label);
            case W:
                return (position.west != null) && !visited.contains(position.west.label);
            case NE:
                return (position.northEast != null) && !visited.contains(position.northEast.label);
            case SE:
                return (position.southEast != null) && !visited.contains(position.southEast.label);
            case NW:
                return (position.northWest != null) && !visited.contains(position.northWest.label);
            case SW:
                return (position.southWest != null) && !visited.contains(position.southWest.label);
        }
        return false;
    }


    /*-------------------------------INNER CLASSES-----------------------------*/

    /**
     * Each node will be connected at least to one other node.
     * Only one link is allowed in each direction,
     * a node can have links in each direction.
     * Each node has a link attribute. There are eight possible links, we initialize an array of
     * links. Each direction is encoded by a magic constant.
     */
    public class Node {
        String label;
        Link[] links = new Link[8];

        Node north, east, south, west, northWest, southWest, northEast, southEast;
        boolean finalNode;

        /* Constructors */
        private Node(String label, boolean finalNode) {
            this.label = label;
            this.finalNode = finalNode;
        }

        private Node(String label) {
            this(label, false);
        }
        /* End of constructors*/

        /**
         * Method that links two nodes.
         * Nodes cannot be null.
         * If the direction is already occupied to connect some node, print some error message
         *
         * @param otherNode is the node that is connected to the current one
         * @param direction is the direction in which we move to find the other node
         * @throws Exception when trying to overwrite an existing node
         *                   <p>
         *                   to update with new points
         * @since 1.0
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
    /*-------------------------------------------------------------------------*/

}


