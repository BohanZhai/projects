package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

public class Display {
    private TERenderer ter; //TERender
    private TETile[][] world; //TETiles
    private final int WIDTH = Game.WIDTH;
    private final int HEIGHT = Game.HEIGHT;


    /** Constructor: Initialize the tile rendering engine with WIDTH * HEIGHT
     * and fill the world with NOTHING.
     */
    public Display(TERenderer t, TETile[][] w) {
        ter = t;
        world = w;
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
    }

    /** Assign the new tiles to world */
    public void update(TETile[][] w) {
        world = w;
    }

    /** Take a parameter w and display all the stuff in w.*/
    public void print() {
        // draws the world to the screen
        ter.renderFrame(world , "Good Luck!", "No time limit", "Play with input string");
    }
}
