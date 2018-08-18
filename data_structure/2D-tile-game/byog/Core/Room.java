package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class Room implements Building {
    private static ArrayDeque<Room> rooms = null;
    final ArrayDeque<int[]> roomWall = new ArrayDeque<>();
    final ArrayDeque<int[]> roomFloor = new ArrayDeque<>();
    private Random rand = null; //random number generator
    private int height; //height of the room
    private int width; //width of the room

    /** Constructor. */
    public Room() {
        Room.rooms = World.getRoomsDeque();
        // Generate a random variable with a seed according to the time.
        rand = Game.rand;
        height = 0;
        width = 0;
    }

    @Override
    /** Return the height of the room. */
    public int getheightofstructure() {
        return height;
    }

    @Override
    /** Return the width of the room. */
    public int getwidthofstructure() {
        return width;
    }

    /** Generate a large room which takes more than "percentage" space of the room */
    public void generateRoomStrategyOne(TETile[][] world, double percentage, int[] index) {
        int roomArea = 0;
        int pieceLength = index[1] - index[0] + 1;
        int pieceWidth = index[3] - index[2] + 1;
        int pieceArea = pieceLength * pieceWidth;
        int newLeft = 0;
        int newRight = 0;
        int newUpper = 0;
        int newLower = 0;
        // generate proper room index such that the area of the room is greater than percantage.
        while (!(roomArea > percentage * pieceArea) || !(height >= 3) || !(width >= 3)) {
            newLeft = rand.nextInt(pieceLength / 2) + index[0];
            newRight = index[1] - rand.nextInt(pieceLength / 2);
            newUpper = rand.nextInt(pieceWidth / 2) + index[2];
            newLower = index[3] - rand.nextInt(pieceWidth / 2);

            height = newLower - newUpper + 1;
            width = newRight - newLeft + 1;
            roomArea = height * width;
        }
        // get the original matrix of original world
        Matrix mapMatrix = World.getMatrix();
        // update the indices of the world matrix for new walls and floors.
        for (int i = newLeft; i <= newRight; i++) {
            for (int j = newUpper; j <= newLower; j++) {
                if (i == newLeft || j == newUpper || i == newRight || j == newLower) {
                    mapMatrix.givenvalue(i, j, 1); //set world[i][j] to be wall if it is on the edge
                    int[] wallIndex = {i, j};
                    roomWall.addFirst(wallIndex); //add index [i][j] in to roomWall deque
                } else {
                    //set world[i][j] to be floor if it is inside the room
                    mapMatrix.givenvalue(i, j, -999);
                    int[] floorIndex = {i, j};
                    roomFloor.addFirst(floorIndex); // add index [i][j] in to roomFloor deque
                }
            }
        }
        // convert the new matrix to new world (fill the room with floor and walls)
        World.matrixToWorld(mapMatrix);

        //add this room to rooms
        rooms.addFirst(this);
    }

    public void generateRoomStrategyTwo(TETile[][] world, int num, int[] index) {
        // get the original matrix of original world
        Matrix origin = World.getMatrix();

        // call helpMerge Num times to merge Num rooms
        Matrix newMatrix = null; //set the first matrix to be null
        newMatrix = helpMerge(num, index, newMatrix);

        // convert the new matrix to new world
        origin.matrixadding(newMatrix);
        World.matrixToWorld(origin);

        // save indices of walls and floors into roomWall and roomFloor Deque.
        for (int i = index[0]; i <= index[1]; i++) {
            for (int j = index[2]; j <= index[3]; j++) {
                if (world[i][j].equals(Tileset.WALL)) {
                    int[] wallIndex = {i, j};
                    roomWall.addFirst(wallIndex); //add index [i][j] in to roomWall deque
                } else if (world[i][j].equals(Tileset.FLOOR)) {
                    int[] floorIndex = {i, j};
                    roomFloor.addFirst(floorIndex); //add index [i][j] in to roomWall deque
                }
            }
        }

        //add this room to rooms
        rooms.addFirst(this);
    }

    /** write a helper function recursively merge a new room with the origin world. */
    private Matrix helpMerge(int times, int[] index, Matrix result) {
        //return result matrix if we run out of times.
        if (times == 0) {
            return result;
        }
        //create newMatrix contains a new room overlaps with previous rooms
        Matrix newMatrix = null;
        // find a proper newMatrix that has room overlap with rooms in result.
        boolean proper = false;
        // loop until find a new room overlap with previous room
        while (!proper) {
            newMatrix = new Matrix(Game.WIDTH, Game.HEIGHT);
            //generate a new room
            int roomArea = 0;
            int pieceLength = index[1] - index[0] + 1;
            int pieceWidth = index[3] - index[2] + 1;
            int pieceArea = pieceLength * pieceWidth;
            int newLeft = 0;
            int newRight = 0;
            int newUpper = 0;
            int newLower = 0;
            // generate proper room index such that the area of the room is greater than percantage.
            while (!(roomArea > 0.1 * pieceArea) || !(height >= 3) || !(width >= 3)) {
                int x1 = rand.nextInt(pieceLength) + index[0];
                int x2 = rand.nextInt(pieceLength) + index[0];
                newLeft = Math.min(x1, x2);
                newRight = Math.max(x1, x2);
                int y1 = rand.nextInt(pieceWidth) + index[2];
                int y2 = rand.nextInt(pieceWidth) + index[2];
                newUpper = Math.min(y1, y2);
                newLower = Math.max(y1, y2);

                height = newLower - newUpper + 1;
                width = newRight - newLeft + 1;
                roomArea = height * width;
            }

            //assign value to newMatrix
            for (int i = newLeft; i <= newRight; i++) {
                for (int j = newUpper; j <= newLower; j++) {
                    if (i == newLeft || j == newUpper
                        || i == newRight || j == newLower) {
                        newMatrix.givenvalue(i, j, 1); // set value to be 1 if it is a wall.
                    } else if (newLeft < i && i < newRight && newUpper < j && j < newLower) {
                        newMatrix.givenvalue(i, j, -999); // set value to be -999 if it is a floor.
                        // if the new room overlaps with previous rooms,
                        // there must be at least one floor overlap with floor.
                        if (result == null || result.getitem(i, j) < 0) {
                            proper = true;
                        }
                    } else {
                        newMatrix.givenvalue(i, j, 0); // set value to be 0 if it is nothing.
                    }
                }
            }
        }

        //check if this is the first room generated, set result to be newMatrix
        if (result == null) {
            result = newMatrix;
        }

        //add the new room matrix to result matrix
        result.matrixadding(newMatrix);

        return helpMerge(times - 1, index, result);
    }
}
