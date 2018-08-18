package byog.Core;

import java.util.Random;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Generate {
    static boolean door = false; // check if door exists
    static boolean player = false; // check if player exists
    private Random rand = null; //random number generator
    Integer seed = null;

    /** Constructor. */
    public Generate() {
        door = false;
        player = false;
        // Generate a random variable with a seed according to the time.
        rand = Game.rand;
    }

    /** Generate a random world randomly using divide and conquer strategy.
     * The parameter "times" is the minimum split number.
     * int[] "index" stores four bound of the indices of the world: left, right, upper, lower.
     */
    public void generateWorld(TETile[][] world, int times, int[] index) {
        // check NullPointer
        if (world == null) {
            return;
        }
        // If it hasn't run out the minimum times, keep split,
        // otherwise, it can either split or return.
        int splitNum;
        if (times > 0) {
            splitNum = rand.nextInt(20); //split at least splitNum times
        } else {
            splitNum = rand.nextInt(30);
        }
        //split world into splitNum pieces, call split function.
        if (splitNum == 1) {
            //System.out.println("split into 2 parts vertically");
            split(world, times, updateIndexVertical(index));
            //split into 2 parts vertically
        } else if (splitNum == 0) {
            //System.out.println("split into 2 parts horizontally");
            split(world, times, updateIndexHorizontal(index));
            //split into 2 parts horizontally
        } else if (splitNum < 11) {
            //System.out.println("split into 2*2");
            split(world, times, updateIndex(index, 2)); //split into 2*2 = 4 parts
        } else if (splitNum < 20) {
            //System.out.println("split into 3*3");
            split(world, times, updateIndex(index, 3)); //split into 3*3 = 9 parts
        } else {
            // generate a small world but don't split anymore, just return (Base case)
            // generate rooms randomly using strategy1 or strategy2
            int choose = rand.nextInt(10);
            Room room = new Room();
            //System.out.println("Generate Room");
            if (choose < 4) {
                room.generateRoomStrategyOne(world, 0.7, index); //percentage = 0.7
            } else {
                room.generateRoomStrategyTwo(world, 5, index); // merge 4 rooms to get a big room
            }
        }
    }

    /** Split function and generateWorld alternate recursively call each other
     * to split the world into small pieces for at least minimal times(parameter "times").
     * Every piece's index are stored in an array called indices.
     */
    public void split(TETile[][] world, int times, int[][] indices) {
        // if the world is not available to split, generate a small world and return (Base case)
        if (indices == null) {
            return;
        }
        for (int i = 0; i < indices.length; i++) {
            // recursively call generateWorld on smaller pieces and decrease times by 1.
            generateWorld(world, times - 1, indices[i]);
        }

    }

    /** updateIndex takes two parameters, the index of original world and splitNum.
     * Then it returns the new indices (stored in an 2D array) for each piece after split.
     * If the world is not able to split as required, return null.
     */
    public int[][] updateIndex(int[] index, int splitNum) {
        //check if it is able to split
        int horizontaldelta = (index[1] - index[0] + 1) / splitNum; //the increasement in row
        int verticaldelta = (index[3] - index[2] + 1) / splitNum; //the increasement in coloumn
        if (horizontaldelta < 6 || verticaldelta < 3) {
            //System.out.println("Cannot split anymore");
            return null;
        }

        //calculate the index for each piece after split
        int[][] result = new int[splitNum * splitNum][]; //store the resulting indices
        int[] newIndex = {index[0], index[0] + horizontaldelta - 1,
                          index[2], index[2] + verticaldelta - 1};
        int count = 0;

        // split into splitNum * splitNum pieces
        // i starts from upper to lower
        for (int i = 1; i <= splitNum; i++) {
            // j starts from left to right each time
            for (int j = 0; j < splitNum; j++) {
                newIndex[0] = index[0] + j * horizontaldelta;
                newIndex[1] = index[0] + (j + 1) * horizontaldelta - 1;
                //copy into result[count]
                result[count] = new int[4];
                for (int k = 0; k < 4; k++) {
                    result[count][k] = newIndex[k];
                }
                //increase count
                count += 1;
            }
            //increase newIndex
            newIndex[2] = newIndex[2] + verticaldelta;
            newIndex[3] = newIndex[3] + verticaldelta;
        }

        /* for test purpose
        for(int i =0; i< result.length; i++) {
            for( int j = 0; j<result[0].length ; j++) {
                System.out.println(result[i][j]);
            }
        }
        */

        return result;
    }

    /** Return the indices of each pieces that split original world
     *  into 2 parts vertically.
     */
    public int[][] updateIndexVertical(int[] index) {
        //check if it is able to split
        int length = (index[1] - index[0]) / 2;
        if (length < 3) {
            //System.out.println("Cannot split anymore");
            return null;
        }
        //split into 2 parts vertically
        //calculate the index for each piece after split
        int[][] result = new int[2][]; //store the resulting indices
        result[0] = new int[4];
        result[1] = new int[4];
        result[0][0] = index[0];
        result[0][1] = index[0] + length;
        result[0][2] = index[2];
        result[0][3] = index[3];
        result[1][0] = index[0] + length + 1;
        result[1][1] = index[1];
        result[1][2] = index[2];
        result[1][3] = index[3];

        return result;
    }

    /** Return the indices of each pieces that split original world
     *  into 2 parts vertically.
     */
    public int[][] updateIndexHorizontal(int[] index) {
        //check if it is able to split
        int length = (index[3] - index[2]) / 2;
        if (length < 3) {
            //System.out.println("Cannot split anymore");
            return null;
        }
        //split into 2 parts vertically
        //calculate the index for each piece after split
        int[][] result = new int[2][]; //store the resulting indices
        result[0] = new int[4];
        result[1] = new int[4];
        result[0][0] = index[0];
        result[0][1] = index[1];
        result[0][2] = index[2];
        result[0][3] = index[2] + length;
        result[1][0] = index[0];
        result[1][1] = index[1];
        result[1][2] = index[2] + length + 1;
        result[1][3] = index[3];

        return result;
    }

    /**
     * Test only: fill the region with wall.
     */
    private void fill(TETile[][] world, int[] index) {
        System.out.println("Test Region: drawing walls");
        for (int i = 0; i < index.length; i++) {
            System.out.print(index[i] + " ");
        }
        for (int i = index[0]; i <= index[1]; i++) {
            for (int j = index[2]; j <= index[3]; j++) {
                world[i][j] = Tileset.FLOWER;
            }
        }
    }

    /** generate a world with two rooms connected by hallway */
    public void generateTwoRooms(TETile[][] world) {
        // check NullPointer
        if (world == null) {
            return;
        }
        Room room1 = new Room();
        Room room2 = new Room();
        int[] index1 = {Game.WIDTH / 2 + 1, Game.WIDTH - 1, 0, Game.HEIGHT / 2};
        int[] index2 = {0, Game.WIDTH / 2,  0, Game.HEIGHT - 1};
        room1.generateRoomStrategyOne(world, 0.3, index1); //percentage = 0.7
        room2.generateRoomStrategyOne(world, 0.3, index2); //percentage = 0.7
        Hallway hallway = new Hallway();
        hallway.connectTwoRooms(world, room1, room2);
    }

    public void connectRooms(TETile[][] world) {
        int count = 0;
        ArrayDeque<Room> rooms = World.getRoomsDeque();
        while (count < rooms.size() - 1) {
            Room r1 = rooms.get(count);
            Room r2 = rooms.get(count + 1);
            Hallway hallway = new Hallway();
            hallway.connectTwoRooms(world, r1, r2);
            count += 1;
        }
    }


    /** add a door */
    public int[] addDoor(TETile[][] world) {
        //return null if the player has already been set or the rooms deque is empty
        if (door) {
            return null;
        }
        Matrix worldMatrix = World.getMatrix();
        ArrayDeque<int[]> worldWalls = new ArrayDeque<>();
        ArrayDeque<int[]> worldFloors = new ArrayDeque<>();
        for (int i = 0; i < worldMatrix.getWidth(); i++) {
            for (int j = 0; j < worldMatrix.getHeigh(); j++) {
                int[] temp = {i, j};
                if (worldMatrix.getitem(i, j) > 0) {
                    worldWalls.addFirst(temp);
                } else if (worldMatrix.getitem(i, j) < 0) {
                    worldFloors.addFirst(temp);
                }
            }
        }


        //generate a random index for all rooms in rooms deque
        int[] result = null;
        while (!door) {
            //generate a random index for the door in roomWall deque
            int doorIndex = rand.nextInt(worldWalls.size());
            result = worldWalls.get(doorIndex);
            //find a proper temp: temp[0][1] cannot on the edge of window.
            int up = worldMatrix.getitem(result[0], (result[1] - 1));
            int down = worldMatrix.getitem(result[0], (result[1] + 1));
            int left = worldMatrix.getitem(result[0] - 1, result[1]);
            int right = worldMatrix.getitem(result[0] + 1, result[1]);
            //find a proper door
            if ((up * down > 0 && left < 0 && right == 0)
                 || (up * down > 0 && left == 0 && right < 0)
                 || (up < 0 && down == 0 && left * right > 0)
                 || (up == 0 && down < 0 && left * right > 0)) {
                //modify this TETile to be the door
                world[result[0]][result[1]] = Tileset.LOCKED_DOOR;
                door = true; //mark door has been set.
            }
        }
        return result; //return the index of the door
    }

    /** add a player */
    public int[] addPlayer(TETile[][] world) {
        //return null if the player has already been set or the rooms deque is empty
        if (player) {
            return null;
        }
        Matrix worldMatrix = World.getMatrix();
        ArrayDeque<int[]> worldWalls = new ArrayDeque<>();
        ArrayDeque<int[]> worldFloors = new ArrayDeque<>();
        int count = 0;
        for (int i = 0; i < worldMatrix.getWidth(); i++) {
            for (int j = 0; j < worldMatrix.getHeigh(); j++) {
                int[] temp = {i, j};
                if (worldMatrix.getitem(i, j) > 0) {
                    worldWalls.addFirst(temp);
                } else if (worldMatrix.getitem(i, j) < 0) {
                    worldFloors.addFirst(temp);
                    count += 1;
                }
            }
        }

        //generate a random index for player
        int[] result = null;
        while (!player) {
            //generate a random index for the door in roomWall deque
            int playerIndex = rand.nextInt(worldFloors.size());
            result = worldFloors.get(playerIndex);
            //find a proper door
            if (worldMatrix.getitem(result[0], (result[1])) < 0) {
                world[result[0]][result[1]] = Tileset.PLAYER; //modify this TETile to be the door
                player = true; //mark door has been set.
            }
        }
        return result; //return the index of the door
    }


    public int[] addBomb(TETile[][] world) {
        //return null if the player has already been set or the rooms deque is empty
        Matrix worldMatrix = World.getMatrix();
        ArrayDeque<int[]> worldWalls = new ArrayDeque<>();
        ArrayDeque<int[]> worldFloors = new ArrayDeque<>();
        int count = 0;
        for (int i = 0; i < worldMatrix.getWidth(); i++) {
            for (int j = 0; j < worldMatrix.getHeigh(); j++) {
                int[] temp = {i, j};
                if (worldMatrix.getitem(i, j) > 0) {
                    worldWalls.addFirst(temp);
                } else if (worldMatrix.getitem(i, j) < 0) {
                    worldFloors.addFirst(temp);
                    count += 1;
                }
            }
        }
        //generate a random index for player
        int[] result = null;
        while (true) {
            //generate a random index for the door in roomWall deque
            int bombIndex = rand.nextInt(worldFloors.size());
            result = worldFloors.get(bombIndex);
            //find a proper door
            if (worldMatrix.getitem(result[0], (result[1])) < 0) {
                break;
            }
        }

        return result;
    }

    public int[] addwormhole(TETile[][] world) {
        //return null if the player has already been set or the rooms deque is empty
        Matrix worldMatrix = World.getMatrix();
        ArrayDeque<int[]> worldWalls = new ArrayDeque<>();
        ArrayDeque<int[]> worldFloors = new ArrayDeque<>();
        int count = 0;
        for (int i = 0; i < worldMatrix.getWidth(); i++) {
            for (int j = 0; j < worldMatrix.getHeigh(); j++) {
                int[] temp = {i, j};
                if (worldMatrix.getitem(i, j) > 0) {
                    worldWalls.addFirst(temp);
                } else if (worldMatrix.getitem(i, j) < 0) {
                    worldFloors.addFirst(temp);
                    count += 1;
                }
            }
        }
        //generate a random index for wormhole
        int[] result = null;
        while (true) {
            int bombIndex = rand.nextInt(worldFloors.size());
            result = worldFloors.get(bombIndex);
            //find a proper door
            if (worldMatrix.getitem(result[0], (result[1])) < 0) {
                break;
            }
        }

        return result;
    }
}
