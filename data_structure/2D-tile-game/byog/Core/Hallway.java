package byog.Core;

import byog.TileEngine.TETile;

import java.util.Random;


public class Hallway implements Building {
    final ArrayDeque<int[]> hallwayWall = new ArrayDeque<>();
    private static final ArrayDeque<Hallway> HALLWAYS = World.getHallwaysDeque();
    final ArrayDeque<int[]> hallwayFloor = new ArrayDeque<>();
    private int height; //height of the room
    private int width; //width of the room
    private Random rand = null; //random number generator

    /** Constructor. */
    public Hallway() {
        // Generate a random variable with a seed according to the time.
        rand = Game.rand; //initialize the random generator
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

    /** Generate a vertical hallway with length L */
    public Matrix generateVerticalHallway(int length, int positionx, int positiony, boolean up) {
        /**
         * the position is the center of the beginning of the Hallway
         */
        if (!up) {
            width = 3;
            height = length;
            Matrix hallWaymatrix = new Matrix(World.WIDTH, World.HEIGHT);
            for (int i = 0; i < World.WIDTH; i++) {
                for (int j = 0; j < World.HEIGHT; j++) {
                    if (positiony  >= j && (positiony - length + 1) <= j) {
                        if (i == positionx - 1 || i == positionx + 1) {
                            hallWaymatrix.givenvalue(i, j, 1);
                        } else if (i == positionx) {
                            hallWaymatrix.givenvalue(i, j, -99);
                        } else {
                            hallWaymatrix.givenvalue(i, j, 0);
                        }
                    } else {
                        hallWaymatrix.givenvalue(i, j, 0);
                    }
                }
            }
            hallWaymatrix.givenvalue((positionx - 1), (positiony + 1), 1);
            hallWaymatrix.givenvalue((positionx + 1), (positiony + 1), 1);
            hallWaymatrix.givenvalue((positionx - 1), (positiony - length), 1);
            hallWaymatrix.givenvalue((positionx + 1), (positiony - length), 1);

            return hallWaymatrix;
        }
        width = 3;
        height = length;
        Matrix hallWaymatrix = new Matrix(World.WIDTH, World.HEIGHT);
        for (int i = 0; i < World.WIDTH; i++) {
            for (int j = 0; j < World.HEIGHT; j++) {
                if (positiony  <= j && (positiony + length - 1) >= j) {
                    if (i == positionx - 1 || i == positionx + 1) {
                        hallWaymatrix.givenvalue(i, j, 1);
                    } else if (i == positionx) {
                        hallWaymatrix.givenvalue(i, j, -99);
                    } else {
                        hallWaymatrix.givenvalue(i, j, 0);
                    }
                } else {
                    hallWaymatrix.givenvalue(i, j, 0);
                }
            }
        }
        hallWaymatrix.givenvalue((positionx - 1), (positiony - 1), 1);
        hallWaymatrix.givenvalue((positionx + 1), (positiony - 1), 1);
        hallWaymatrix.givenvalue((positionx - 1), (positiony + length), 1);
        hallWaymatrix.givenvalue((positionx + 1), (positiony + length), 1);

        return hallWaymatrix;
    }

    public Matrix generateHorizontalHallway(int length, int positionx, int positiony) {
        /**
         * the position is the center of the beginning of the Hallway
         */
        width = length;
        height = 3;
        Matrix hallWaymatrix = new Matrix(World.WIDTH, World.HEIGHT);
        for (int i = 0; i < World.WIDTH; i++) {
            for (int j = 0; j < World.HEIGHT; j++) {
                if (positionx <= i && (positionx + length - 1) >= i) {
                    if (j == positiony - 1 || j == positiony + 1) {
                        hallWaymatrix.givenvalue(i, j, 1);
                    } else if (j == positiony) {
                        hallWaymatrix.givenvalue(i, j, -99);
                    } else {
                        hallWaymatrix.givenvalue(i, j, 0);
                    }
                } else {
                    hallWaymatrix.givenvalue(i, j, 0);
                }
            }
        }

        if (!(positiony == 0)) {
            hallWaymatrix.givenvalue((positionx - 1), (positiony - 1), 1);
            hallWaymatrix.givenvalue((positionx - 1), (positiony + 1), 1);
            hallWaymatrix.givenvalue((positionx + length), (positiony - 1),  1);
            hallWaymatrix.givenvalue((positionx + length), (positiony + 1),  1);
        } else if (positiony == 0) {
            hallWaymatrix.givenvalue(positionx, positiony, 1);
        }

        return hallWaymatrix;
    }

    public Matrix generateHallway(int[] begin, int[] end) {
        if (begin == null || end == null) {
            System.out.println("null points");
            return null;
        }

        if ((begin[0] - end[0]) * (begin[1] - end[1]) < 0) {
            int beginx = Math.min(begin[0], end[0]);
            int beginy = Math.max(begin[1], end[1]);
            int endx = Math.max(begin[0], end[0]);
            int endy = Math.min(begin[1], end[1]);
            int rangex =  Math.abs(beginx - endx) + 1;
            int rangey = Math.abs(beginy - endy) + 1;
            Matrix hallwaysmatrix = new Matrix(World.WIDTH, World.HEIGHT);
            while (beginx < endx || beginy > endy) {
                // Boolean
                if (rand.nextBoolean()) {
                    if (beginx < endx) {
                        int firstlength = rand.nextInt(rangex) + 1;
                        hallwaysmatrix.matrixadding(
                                generateHorizontalHallway(firstlength, beginx, beginy));
                        beginx += firstlength;
                        rangex -= firstlength;
                    }
                } else {
                    if (beginy > endy) {
                        int secondlength = rand.nextInt(rangey) + 1;
                        hallwaysmatrix.matrixadding(
                                generateVerticalHallway(secondlength, beginx, beginy, false));
                        beginy -= secondlength;
                        rangey -= secondlength;

                    }
                }
            }
            return hallwaysmatrix;
        }

        int beginx = Math.min(begin[0], end[0]);
        int beginy = Math.min(begin[1], end[1]);
        int endx = Math.max(begin[0], end[0]);
        int endy = Math.max(begin[1], end[1]);
        int rangex =  Math.abs(beginx - endx) + 1;
        int rangey = Math.abs(beginy - endy) + 1;
        Matrix hallwaysmatrix = new Matrix(World.WIDTH, World.HEIGHT);
        while (beginx < endx || beginy < endy) {
            // Boolean
            if (rand.nextBoolean()) {
                if (beginx < endx) {
                    int firstlength = rand.nextInt(rangex) + 1;
                    hallwaysmatrix.matrixadding(
                            generateHorizontalHallway(firstlength, beginx, beginy));
                    beginx += firstlength;
                    rangex -= firstlength;
                }
            } else {
                if (beginy < endy) {
                    int secondlength = rand.nextInt(rangey);
                    hallwaysmatrix.matrixadding(
                            generateVerticalHallway(secondlength, beginx, beginy, true));
                    beginy += secondlength;
                    rangey -= secondlength;
                }
            }
        }

        return hallwaysmatrix;
    }

    /** Randomly connect two rooms by hallways
     *
     */
    public void connectTwoRooms(TETile[][] world, Room r1, Room r2) {
        //generate a random index for the player in roomFloor deque
        ArrayDeque<int[]> floor1 = r1.roomFloor;
        //generate a random index for the player in roomFloor deque
        ArrayDeque<int[]> floor2 = r2.roomFloor;

        int i1 = rand.nextInt(floor1.size());
        int i2 = rand.nextInt(floor2.size());
        int[] indexr1 = floor1.get(i1);
        int[] indexr2 = floor2.get(i2);

        System.out.println(indexr2[0]+" "+ indexr2[1]);
        System.out.println(indexr1[0]+" "+ indexr1[1]);

        Matrix hallwayr1r2 = generateHallway(indexr1, indexr2);
        Matrix result = World.getMatrix();

        //add hallway into hallway Deque
        for (int i = 0; i < Game.WIDTH; i++) {
            for (int j = 0; j < Game.HEIGHT; j++) {
                if (hallwayr1r2.getitem(i, j) == 1) {
                    int[] hallwayIndex = {i, j};
                    hallwayWall.addFirst(hallwayIndex); //add index [i][j] in to roomWall deque
                } else if (hallwayr1r2.getitem(i, j) < 0) {
                    int[] floorIndex = {i, j};
                    hallwayFloor.addFirst(floorIndex); //add index [i][j] in to roomWall deque
                }
            }
        }

        result.matrixadding(hallwayr1r2);
        World.matrixToWorld(result);

        HALLWAYS.addFirst(this);
    }

}
