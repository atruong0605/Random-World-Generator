package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private String winner;
    public static final int WIDTH = 55;
    public static final int HEIGHT = WIDTH;
    private ArrayList<Character> player;
    private File save_file;
    private String str;
    private String state;
    private MapGenerator map;
    private boolean flag;
    private String seed;


    public Engine() {
        player = new ArrayList<>(Arrays.asList('w', 'a', 's', 'd'));
        save_file = new File("./games.txt");
        str = "";
        state = "start";
    }

    private void menus(String state) {
        if (state == "start") {
            StdDraw.setCanvasSize(WIDTH * 25, HEIGHT * 16);
            StdDraw.setXscale(2, HEIGHT - 3);
            StdDraw.setYscale(2, HEIGHT - 3);
            StdDraw.setPenColor(Color.YELLOW);
            StdDraw.clear(Color.BLACK);
            StdDraw.enableDoubleBuffering();

            Font controls = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(controls);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.75, "New Game: N");
            StdDraw.text(WIDTH / 2, HEIGHT * 0.7, "Load Save: L");
            StdDraw.text(WIDTH / 2, HEIGHT * 0.65, "Quit: Q");
            StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "In-game input T to toggle the lights in the map.");

            StdDraw.setPenColor(Color.WHITE);
            Font style = new Font("Monaco", Font.BOLD, 50);
            StdDraw.setFont(style);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.4, "Karachi 2 Cali");

            Font sub = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(controls);
            StdDraw.setPenColor(Color.GREEN);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.34, "Proton the Moton (O):");

            StdDraw.setFont(sub);
            StdDraw.setPenColor(Color.GRAY);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.31, "Use WASD keys to move your character. Get from Karachi (green dots) to Cali (trees).");

            StdDraw.setFont(controls);
            StdDraw.setPenColor(Color.RED);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.27, "Albert the Tweaker (A):");

            StdDraw.setFont(sub);
            StdDraw.setPenColor(Color.GRAY);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.24, "Spawns randomly in Karachi every time step. If he tweaks to a square next to you, you die.");
            StdDraw.show();
            StdDraw.pause(1);
        } else if (state == "random init") {
            Font sub = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(sub);
            StdDraw.clear(Color.DARK_GRAY);

            StdDraw.text(WIDTH / 2, HEIGHT * 0.8, "Input some numbers...");
            StdDraw.text(WIDTH / 2, HEIGHT * 0.7, seed);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "Press 's' to start the game.");
            StdDraw.show();
            StdDraw.pause(1);
        } else if (state == "done") {
            StdDraw.setCanvasSize(WIDTH * 20, HEIGHT * 16);
            StdDraw.setXscale(2, HEIGHT - 3);
            StdDraw.setYscale(2, HEIGHT - 3);

            StdDraw.clear(Color.BLACK);
            StdDraw.enableDoubleBuffering();
            if (winner.equals("Omer")) {
                StdDraw.setPenColor(Color.GREEN);
                winner = "Proton the Moton";
            } else {
                winner = "Albert the Tweaker";
                StdDraw.setPenColor(Color.RED);
            }
            Font style = new Font("Monaco", Font.BOLD, 50);
            StdDraw.setFont(style);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.4, winner + " wins");

            StdDraw.show();
            StdDraw.pause(1);
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

        menus(state);

        KeyboardInputSource in = new KeyboardInputSource();
        while (state != "done") {
            if (!in.possibleNextInput()) {
                continue;
            } else {
                char i = Character.toLowerCase(in.getNextKey());
                str += i;
                process(i);
                if (state == "in-progress") {
                    ter.renderFrame(map.map());
                    hud();
                }
            }
        }
        menus(state);
    }

    private void save_game() {
        try {
            save_file.createNewFile();
            FileWriter w = new FileWriter(save_file);
            w.write(str);
            w.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load_save() {
        try {
            FileReader r = new FileReader(save_file);
            String inp = "";
            for (int c = 0; c >= 0; c = r.read()) {
                inp += (char) c;
            }
            r.close();
            interactWithInputString(inp.substring(0,inp.length()-2));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        for (char i: input.toCharArray()) {
            process(i);
        }
        TETile[][] finalWorldFrame = map.map();
        return finalWorldFrame;
    }

    private void process(char k) {
        switch (state) {
            case "start":
                switch (k) {
                    case 'n':
                        flag = false;
                        state = "random init";
                        seed = "";
                        menus(state);
                        break;
                    case 'l':
                        flag = false;
                        str = "";
                        load_save();
                        state = "in-progress";
                        break;
                    case 'q':
                        if (flag) {
                            state = "done";
                            save_game();
                        }
                }
                break;
            case "random init":
                if (k == 's') {
                    state = "in-progress";
                    map = new MapGenerator(WIDTH, HEIGHT, new Random(Integer.parseInt(seed)));
                    ter.initialize(WIDTH, HEIGHT);
                    map.player(seed);
                } else {
                    seed += String.valueOf(k);
                    menus(state);
                }
                break;
            case "in-progress":
                if (k == ':') {
                    flag = true;
                }
                if (k == 'q' && flag) {
                    state = "done";
                    save_game();
                }
                if (k == 't') {
                    map.lights();
                }
                if (player.contains(k)) {
                    winner = map.move(k);
                    if (winner.length() > 0) {
                        state = "done";
                    }
                }
                break;
        }
    }
    private void hud() {
        TETile[][] w = map.map();
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.clear(Color.BLACK);
            ter.renderFrame(w);
            int x = (int) Math.floor(StdDraw.mouseX());
            int y = (int) Math.floor(StdDraw.mouseY());
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(1, HEIGHT - 1, w[min(max(x, 0), w.length - 1)][min(max(y, 0), w[0].length - 1)].description());
            StdDraw.textLeft(1, HEIGHT - 2, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            StdDraw.show();
        }
    }
}
