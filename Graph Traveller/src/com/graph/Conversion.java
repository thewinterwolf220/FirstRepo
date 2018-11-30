
package com.graph;
/**
 * @author marco
 * @version 1.0
 * Created for graph 2.0
 * Contains some utility methods
 */
class Conversion {

    enum Directions {
        NORTH(0), EAST(1), SOUTH(2), WEST(3),
        NORTH_WEST(4), SOUTH_WEST(5), NORTH_EAST(6), SOUTH_EAST(7);

        private final int code;

        Directions(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }

    /*___________CONSTANTS___________*/
    static final int north = 0;
    static final int east = 1;
    static final int south = 2;
    static final int west = 3;
    static final int northWest = 4;
    static final int southWest = 5;
    static final int northEast = 6;
    static final int southEast = 7;
    static final String N = "north";
    static final String E = "east";
    static final String S = "south";
    static final String W = "west";
    static final String NE = "north-east";
    static final String SE = "south-east";
    static final String NW = "north-west";
    static final String SW = "south-west";

    static final String[] DIRECTIONS = {N, E, S, W, NE, SE, NW, SW};

    static String convert(String o) {
        o = o.toLowerCase();

        String option = null;

        if (o.length() > 2)
            return o;

        switch (o) {
            case "n":
                option = N;
                break;
            case "s":
                option = S;
                break;
            case "e":
                option = E;
                break;
            case "w":
                option = W;
                break;
            case "nw":
                option = NW;
                break;
            case "sw":
                option = SW;
                break;
            case "ne":
                option = NE;
                break;
            case "se":
                option = SE;
                break;
            default:
                System.out.println("Option not valid");
                System.exit(1);
        }
        return option;
    }

    /*---------------*/

    /**
     * @param destination to be inserted as the whole word "north", "south"...
     * @return the integer corresponding to the direction
     */
    static int toInt(String destination) {
        int magic = -1;

        switch (destination.toLowerCase()) {
            case N:
                magic = north;
                break;
            case E:
                magic = east;
                break;
            case S:
                magic = south;
                break;
            case W:
                magic = west;
                break;
            case NW:
                magic = northWest;
                break;
            case SW:
                magic = southWest;
                break;
            case NE:
                magic = northEast;
                break;
            case SE:
                magic = southEast;
                break;
            default:
                System.out.println("Error of sort");
                System.exit(2);
        }
        return magic;
    }

    /**
     * Lengthens the destination:
     * Ex: "n" -> "north"...
     *
     * @return the whole word
     */
    static String lengthen(String shortened) {
        String longOne = null;

        if (shortened.length() > 2) {
            return shortened;
        }

        switch (shortened.toLowerCase()) {
            case "n":
                longOne = N;
                break;
            case "e":
                longOne = E;
                break;
            case "s":
                longOne = S;
                break;
            case "w":
                longOne = W;
                break;
            case "nw":
                longOne = NW;
                break;
            case "sw":
                longOne = SW;
                break;
            case "ne":
                longOne = NE;
                break;
            case "se":
                longOne = SE;
                break;
            default:
                System.out.println("Option not valid");
                System.exit(2);
        }
        return longOne;
    }

    static void MITMapDrawing() {
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
}
