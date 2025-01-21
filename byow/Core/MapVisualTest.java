package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.Random;

public class MapVisualTest {
    private static final int WIDTH = 55;
    private static final int HEIGHT = 55;
    private static final int SEED = 99;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Random random = new Random(SEED);
        MapGenerator mapGEN = new MapGenerator(WIDTH, HEIGHT, random);
        TETile[][] world = mapGEN.map();
        ter.renderFrame(world);
    }
}
