package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class World implements Serializable {
    private static TETile[][] world; // current world
    TETile[][] savedWorld; // saved world
    private static Matrix worldMatrix; // represent the world by matrix form
    static int[] doorIndex;
    static int[] playerIndex;
    static int[] bombIndex;
    static int[] wormholeoneIndex;
    static int[] wormholetwoIndex;
    static final int WIDTH = Game.WIDTH;
    static final int HEIGHT = Game.HEIGHT;
    // an array deque stores the pointer to each room
    private static ArrayDeque<Room> rooms = new ArrayDeque();
    // an array deque stores the pointer to each hallway
    private static ArrayDeque<Hallway> hallways = new ArrayDeque();

    /** Constructor: generate a random world according to the random seed */
    public World() {
        // initialize tiles with NOTHING
        world = new TETile[WIDTH][HEIGHT];
        worldMatrix = new Matrix(WIDTH, HEIGHT);
        generateRandomly();
    }

    /** Get a copy of the world to print. */
    public TETile[][] forPrintWorld() {
        TETile[][] copy = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                copy[x][y] = world[x][y];
            }
        }

        return copy;
    }

    /** Return the origin world to modify. */
    public TETile[][] forModifyWorld() {
        return world;
    }

    /** Fill the world by Nothing. */
    public void clear() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Generate a random world. */
    private void generateRandomly() {
        clear(); //clear the world first
        worldMatrix = new Matrix(WIDTH, HEIGHT); //clear the worldMatrix
        rooms = new ArrayDeque();
        hallways = new ArrayDeque();

        Generate gen;
        // generate a world containing rooms(at least 1 rectangular room)
        // and hallway. No player or door.
        gen = new Generate();

        int[] index = {2, WIDTH - 3, 2, HEIGHT - 4}; //initialize index
        gen.generateWorld(forModifyWorld(), 2, index);

        // create hallways between each two rooms.
        gen.connectRooms(world);

        // add a door and a player
        doorIndex = gen.addDoor(world);
        playerIndex = gen.addPlayer(world);
        bombIndex = gen.addBomb(world);
        wormholeoneIndex = gen.addwormhole(world);
        world[wormholeoneIndex[0]][wormholeoneIndex[1]] = Tileset.FLOWER;
        wormholetwoIndex = gen.addwormhole(world);
        world[wormholetwoIndex[0]][wormholetwoIndex[1]] = Tileset.FLOWER;
    }

    /** Update savedWorld */
    public void updateSavedWorld() {
        TETile[][] copy = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                copy[x][y] = world[x][y];
            }
        }
        savedWorld = copy;
    }

    /** load savedWorld. */
    public void load(TETile[][] newWorld, int[][] indices) {
        world = newWorld;
        doorIndex = indices[0];
        playerIndex = indices[1];
        wormholeoneIndex = indices[2];
        wormholetwoIndex = indices[3];
        bombIndex = indices[4];
    }

    /** Get the width of world */
    public static int getWidth() {
        return WIDTH;
    }

    /** Get the height of world */
    public static int getHeight() {
        return HEIGHT;
    }

    /** Return the matrix representing the world.*/
    public static Matrix getMatrix() {
        return worldMatrix;
    }

    /** Produce a matrix that represents the world:
     * Wall > 0
     * Floor < 0
     * Nothing == 0
     */
    public void worldToMatrix() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j].equals(Tileset.WALL)) {
                    worldMatrix.givenvalue(i, j, 1);
                } else if (world[i][j].equals(Tileset.FLOOR)) {
                    worldMatrix.givenvalue(i, j, -999);
                } else {
                    worldMatrix.givenvalue(i, j, 0);
                }
            }
        }
    }

    /** modify the world according to the value of matrix */
    public static void matrixToWorld(Matrix newMatrix) {
        for (int i = 0; i < newMatrix.getWidth(); i++) {
            for (int j = 0; j < newMatrix.getHeigh(); j++) {
                if (newMatrix.getitem(i, j) > 0) {
                    world[i][j] = Tileset.WALL;
                } else if (newMatrix.getitem(i, j) < 0) {
                    world[i][j] = Tileset.FLOOR;
                } else if (newMatrix.getitem(i, j) == 0) {
                    world[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    /** Return wall deque stores the pointer of every wall and floor. */
    public static ArrayDeque<Room> getRoomsDeque() {
        return rooms;
    }

    /** Return floor deque list stores the pointer of every wall and floor. */
    public static ArrayDeque<Hallway> getHallwaysDeque() {
        return hallways;
    }
}
