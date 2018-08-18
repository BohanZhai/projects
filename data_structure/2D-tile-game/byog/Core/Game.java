package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.awt.Font;
import java.awt.Color;

import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 30;
    private boolean checkGame = false;
    private boolean bombLock = false;
    private boolean quitLock = false;
    protected static Random rand = new Random(System.currentTimeMillis());
    protected Integer seed = new Integer(-1); //seed to set random
    private World map = null; //stores all data of our map
    UI ui = new UI();

    /** Return world. */
    public TETile[][] getWorld() {
        return map.forModifyWorld();
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        // display information and logo of this game
        preDisplay();
        // get player's input and form the seed
        Boolean seedLock = false;
        StdDraw.enableDoubleBuffering();
        String inputSeed = new String("");
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'N' || input == 'n') {
                    //To start a new game, unlock the seedLock
                    seedLock = true;
                    ui.ID_UI("Seed: 0");
                } else if (seedLock && (input >= '0' && input <= '9')) {
                    //add the number into the user's inputSeed
                    inputSeed += input;
                    ui.ID_UI("Seed: " + inputSeed.toString());
                    System.out.println(inputSeed);
                } else if (input == 'L' || input == 'l') {
                    map = new World();
                    map.clear();
                    //load a previous game
                    load();
                    break;
                } else if (input == 'Q' || input == 'q') {
                    System.exit(0);
                } else if (!inputSeed.equals("") && ((input == 'S') || (input == 's'))) {
                    break;
                }
            }
        }

        //create the world
        if (!inputSeed.equals("")) {
            playWithInputString("N" + inputSeed + "S");
        }

        //prompt
        String encourage_word = "Good Luck!";
        String leftTime = "25";
        String description_mouse = "Move your mouse";

        //initialize ter
        //ter.initialize(WIDTH, HEIGHT);
        long timestart = System.currentTimeMillis();
        long timesfinished = timestart;
        System.out.println("bomb location: " + map.bombIndex[0] + "  " + map.bombIndex[1]);
        System.out.println("wormhole location: " + map.wormholeoneIndex[0] + "  " + map.wormholeoneIndex[1] +
                           "\n" + map.wormholetwoIndex[0]  + "  " + map.wormholetwoIndex[1]);

        while ((timesfinished - timestart < 60000) && (!checkGame)) {
            //display the world
            ter.renderFrame(this.getWorld() , encourage_word, leftTime + " s", description_mouse);

            //play
            if (StdDraw.hasNextKeyTyped()) {
                char move = StdDraw.nextKeyTyped();
                movement(move);
            }

            //bomb
            if (bombLock) {
                long start = System.currentTimeMillis();
                long finished = timestart;

                while (finished - start < 3000) {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "BOOM!! You step on a BOMB!! \nbe careful next time");
                    StdDraw.show();
                    finished = System.currentTimeMillis();
                }
                break;
            }

            //time
            double time = (60000 - timesfinished + timestart) / 1000;
            leftTime = String.valueOf((int)time);

            // mouse
            map.worldToMatrix();
            description_mouse = getMouse();

            timesfinished = System.currentTimeMillis();
        }

        //check if the game is over
        if (checkGame) {
            // Win Prompt.
            System.out.println("Well done. You win!!");
            winPrompt();
        } else {
            System.out.println("You lose!!");
            losePrompt();
        }

    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        seed = 0;
        map = new World();
        map.clear();
        boolean seedLock = false;
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == 'L' || input.charAt(i) == 'l') {
                load();
                break;
            } else if (input.charAt(i) == 'N' || input.charAt(i) == 'n') {
                seedLock = true;
            } else if (input.charAt(i) == 'S' || input.charAt(i) == 's') {
                rand = new MyRandom(seed).getRand();
                map = new World();
                i++;
                break;
            } else if (seedLock) {
                seed = seed * 10 + (input.charAt(i) - '0');
            }
            i++;
        }
        while (i < input.length()) {
            if (input.charAt(i) == 'w' || input.charAt(i) == 'W'
                    || input.charAt(i) == 's' || input.charAt(i) == 'S'
                    || input.charAt(i) == 'a' || input.charAt(i) == 'A'
                    || input.charAt(i) == 'd' || input.charAt(i) == 'D') {
                movement(input.charAt(i));
            } else if (input.charAt(i) == 'q' || input.charAt(i) == 'Q') {
                save();
            }
            i++;
        }
        return this.getWorld();
    }

    /* Display information and logo of this game */
    public void preDisplay() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
        long timestart = System.currentTimeMillis();
        long timesfinished = timestart;

        while (timesfinished - timestart < 300) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "ERROR: 404 Not Found");
            StdDraw.show();
            timesfinished = System.currentTimeMillis();
        }
        timestart = System.currentTimeMillis();
        timesfinished = timestart;
        while (timesfinished - timestart < 200) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "HaHa, Just a joke");
            StdDraw.show();
            timesfinished = System.currentTimeMillis();
        }
        timestart = System.currentTimeMillis();
        timesfinished = timestart;
        while (timesfinished - timestart < 2000) {
            ui.Show_UI();
            timesfinished = System.currentTimeMillis();
        }
    }

    /* Display information and logo of this game */
    public void winPrompt() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
        long timestart = System.currentTimeMillis();
        long timesfinished = timestart;

        while (timesfinished - timestart < 3000) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "You WIN!!! Well Done!");
            StdDraw.show();
            timesfinished = System.currentTimeMillis();
        }
    }

    /* Display information and logo of this game */
    public void losePrompt() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
        long timestart = System.currentTimeMillis();
        long timesfinished = timestart;

        while (timesfinished - timestart < 3000) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "You LOSE!!! Try again!");
            StdDraw.show();
            timesfinished = System.currentTimeMillis();
        }
    }

    private void movement(char movement) {
        TETile[][] updateWorld = this.getWorld();
        // nullPointerError check
        if (updateWorld == null) {
            throw new RuntimeException("Error: There is no map, cannot move!!");
        }
        if (movement == ':') {
            quitLock = true;
        } else if ((quitLock) && (movement == 'q' || movement == 'Q')) {
            save();
            System.exit(0);
        } else if (movement == 'w' || movement == 'W') {
            int x = map.playerIndex[0];
            int y = map.playerIndex[1] + 1;
            if (updateWorld[x][y].equals(Tileset.FLOOR)
                    || updateWorld[x][y].equals(Tileset.LOCKED_DOOR)
                    || updateWorld[x][y].equals(Tileset.FLOWER)) {
                if (((map.playerIndex[0] == map.wormholetwoIndex[0])
                        && (map.playerIndex[1] == map.wormholetwoIndex[1]))
                        || ((map.playerIndex[0] == map.wormholeoneIndex[0])
                        && (map.playerIndex[1] == map.wormholeoneIndex[1]))) {
                    updateWorld[x][y - 1] = Tileset.FLOWER;
                } else {
                    updateWorld[x][y - 1] = Tileset.FLOOR;
                }
                updateWorld[x][y] = Tileset.PLAYER;

                map.playerIndex[1] += 1;
            }
        } else if (movement == 's' || movement == 'S') {
            int x = map.playerIndex[0];
            int y = map.playerIndex[1] - 1;
            if (updateWorld[x][y].equals(Tileset.FLOOR)
                    || updateWorld[x][y].equals(Tileset.LOCKED_DOOR)
                    || updateWorld[x][y].equals(Tileset.FLOWER)) {
                if (((map.playerIndex[0] == map.wormholetwoIndex[0])
                        && (map.playerIndex[1] == map.wormholetwoIndex[1]))
                        || ((map.playerIndex[0] == map.wormholeoneIndex[0])
                        && (map.playerIndex[1] == map.wormholeoneIndex[1]))) {
                    updateWorld[x][y + 1] = Tileset.FLOWER;
                } else {
                    updateWorld[x][y + 1] = Tileset.FLOOR;
                }
                updateWorld[x][y] = Tileset.PLAYER;

                map.playerIndex[1] -= 1;
            }
        } else if (movement == 'd' || movement == 'D') {
            int x = map.playerIndex[0] + 1;
            int y = map.playerIndex[1];
            if (updateWorld[x][y].equals(Tileset.FLOOR)
                    || updateWorld[x][y].equals(Tileset.LOCKED_DOOR)
                    || updateWorld[x][y].equals(Tileset.FLOWER)) {
                if (((map.playerIndex[0] == map.wormholetwoIndex[0])
                        && (map.playerIndex[1] == map.wormholetwoIndex[1]))
                        || ((map.playerIndex[0] == map.wormholeoneIndex[0])
                        && (map.playerIndex[1] == map.wormholeoneIndex[1]))) {
                    updateWorld[x - 1][y] = Tileset.FLOWER;
                } else {
                    updateWorld[x - 1][y] = Tileset.FLOOR;
                }
                updateWorld[x][y] = Tileset.PLAYER;
                map.playerIndex[0] += 1;
            }
        } else if (movement == 'a' || movement == 'A') {
            int x = map.playerIndex[0] - 1;
            int y = map.playerIndex[1];
            if (updateWorld[x][y].equals(Tileset.FLOOR)
                    || updateWorld[x][y].equals(Tileset.LOCKED_DOOR)
                    || updateWorld[x][y].equals(Tileset.FLOWER)) {
                if (((map.playerIndex[0] == map.wormholetwoIndex[0])
                        && (map.playerIndex[1] == map.wormholetwoIndex[1]))
                     || ((map.playerIndex[0] == map.wormholeoneIndex[0])
                        && (map.playerIndex[1] == map.wormholeoneIndex[1]))) {
                    updateWorld[x + 1][y] = Tileset.FLOWER;
                } else {
                    updateWorld[x + 1][y] = Tileset.FLOOR;
                }
                updateWorld[x][y] = Tileset.PLAYER;

                map.playerIndex[0] -= 1;
            }
        }
        //check if the game is over
        if ((map.playerIndex[0] == map.doorIndex[0] )
                && (map.playerIndex[1] == map.doorIndex[1])) {
            int x = map.playerIndex[0];
            int y = map.playerIndex[1];
            this.getWorld()[x][y] = Tileset.UNLOCKED_DOOR;
            checkGame = true;
        } else if ((map.playerIndex[0] == map.bombIndex[0] )
                && (map.playerIndex[1] == map.bombIndex[1])) {
            bombLock = true;
        } else if ((map.playerIndex[0] == map.wormholeoneIndex[0])
                && (map.playerIndex[1] == map.wormholeoneIndex[1])) {
            updateWorld[map.playerIndex[0]][map.playerIndex[1]] = Tileset.FLOWER;
            updateWorld[map.wormholetwoIndex[0]][map.wormholetwoIndex[1]] = Tileset.PLAYER;
            map.playerIndex[0] = map.wormholetwoIndex[0];
            map.playerIndex[1] = map.wormholetwoIndex[1];
        } else if ((map.playerIndex[0] == map.wormholetwoIndex[0])
                && (map.playerIndex[1] == map.wormholetwoIndex[1])) {
            updateWorld[map.playerIndex[0]][map.playerIndex[1]] = Tileset.FLOWER;
            updateWorld[map.wormholeoneIndex[0]][map.wormholeoneIndex[1]] = Tileset.PLAYER;
            map.playerIndex[0] = map.wormholeoneIndex[0];
            map.playerIndex[1] = map.wormholeoneIndex[1];
        }
    }

    private void load() {
        //load/
        ObjectInputStream inObj = null;
        ObjectInputStream inObjIndex = null;
        try {
            FileInputStream inStr = new FileInputStream("obj.txt");
            FileInputStream inStrIndex = new FileInputStream("Indices.txt");
            inObj = new ObjectInputStream(inStr);
            inObjIndex = new ObjectInputStream(inStrIndex);
            int[][] indices = (int[][]) inObjIndex.readObject();
            TETile[][] temp = (TETile[][]) inObj.readObject();
            map.load(temp, indices);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inObj != null) {
                    inObj.close();
                }
                if (inObjIndex != null) {
                    inObjIndex.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**Get mouse position */
    private String getMouse() {
        String description = "";
        int x = (int)StdDraw.mouseX();
        int y = (int)StdDraw.mouseY();
        if (! ((0 < x && x < 70) && (0 < y && y < 45))) {
            return "out of range";
        }
        Matrix coordinate = map.getMatrix();
        if (coordinate.getitem(x, y) > 0) {
            description = "This is a wall";
        } else if (coordinate.getitem(x, y) < 0) {
            description = "It seems like a floor";
        } else if (coordinate.getitem(x, y) == 0) {
            description = "Um...nothing here";
        }
        if (x == map.doorIndex[0] && y == map.doorIndex[1]) {
            description = "This is hope of your life";
        } else if (x == map.playerIndex[0] && y == map.playerIndex[1]) {
            description = "You looks handsome";
        } else if (x == map.bombIndex[0] && y == map.bombIndex[1]) {
            description = "CAREFUL!!! you find a bomb here!";
        } else if ((x == map.wormholeoneIndex[0] && y == map.wormholeoneIndex[1])
                || (x == map.wormholetwoIndex[0] && y == map.wormholetwoIndex[1])) {
            description = "WOW!!! you wanna travel to another place?";
        }

        return description + ": " + x + " " + y;
    }

    private void save () {
        // save
        ObjectOutputStream outObj = null;
        try {
            FileOutputStream outStr = new FileOutputStream("obj.txt");
            outObj = new ObjectOutputStream(outStr);
            map.updateSavedWorld();
            outObj.writeObject(map.savedWorld);
            outObj.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outObj != null) {
                    outObj.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ObjectOutputStream outObjIndex = null;
        try {
            FileOutputStream outStrIndex = new FileOutputStream("Indices.txt");
            outObjIndex = new ObjectOutputStream(outStrIndex);
            map.updateSavedWorld();
            int[][] indices = {map.doorIndex, map.playerIndex, map.wormholeoneIndex,
                    map.wormholetwoIndex, map.bombIndex};
            outObjIndex.writeObject(indices);
            outObjIndex.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outObjIndex != null) {
                    outObjIndex.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
