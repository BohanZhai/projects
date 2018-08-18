package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class UI {
    public UI() {
        StdDraw.setCanvasSize(Game.WIDTH * 16, Game.HEIGHT * 16);
    }

    protected static void Show_UI(){
        StdDraw.clear(Color.BLACK);
        StdDraw.setXscale(0, Game.WIDTH);
        StdDraw.setYscale(0, Game.HEIGHT);
        Font font_UI = new Font("Monaco", Font.BOLD, 30);
        StdDraw.picture(Game.WIDTH/2, Game.HEIGHT/2, "byog/Core/images/6bf2bf12732bef8d8ac72ee8978bbeec.jpeg");
        StdDraw.setPenRadius(0.001);
        StdDraw.line(0,Game.HEIGHT-5, Game.WIDTH, Game.HEIGHT-5);
        StdDraw.text(3, Game.HEIGHT -2, "AT&T");
        StdDraw.setPenRadius(1);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setFont(font_UI);
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5-5, "Welcome to Overload");
        Font font2 = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setPenRadius(0.1);
        StdDraw.setFont(font2);
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5 - 10, "New Game (N)");
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5 - 15, "Load Game(L)");
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5 - 20, "Quit(Q)");
        StdDraw.show();

    }

    protected static void ID_UI(String id){
        StdDraw.clear();
        StdDraw.setXscale(0, Game.WIDTH);
        StdDraw.setYscale(0, Game.HEIGHT);
        Font font_UI = new Font("Monaco", Font.BOLD, 30);
        Font font2 = new Font("Monaco", Font.BOLD, 15);
        StdDraw.picture(Game.WIDTH/2, Game.HEIGHT/2, "byog/Core/images/6bf2bf12732bef8d8ac72ee8978bbeec.jpeg");
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.setPenRadius(10);
        StdDraw.setFont(font_UI);
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5-7.5, "Please Enter the Seed: ");
        StdDraw.setFont(font2);
        StdDraw.text(Game.WIDTH/2, Game.HEIGHT*4/5-15, id);
        StdDraw.show();
    }

    public static void HUD(String encourage_word,
                           String left_time, String description_mouse){


        StdDraw.setXscale(0, Game.WIDTH);
        StdDraw.setYscale(0, Game.HEIGHT);
        Font font1 = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setPenRadius(0.001);
        StdDraw.line(0,Game.HEIGHT - 4, Game.WIDTH, Game.HEIGHT-4);
        StdDraw.setFont(font1);
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.text(15, Game.HEIGHT - 2,  description_mouse);
        StdDraw.text(Game.WIDTH / 2,Game.HEIGHT- 2, "time left: " + left_time);
        StdDraw.text(Game.WIDTH / 2 + 25, Game.HEIGHT - 2, encourage_word);
    }

}




