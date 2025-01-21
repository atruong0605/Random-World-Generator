package byow.Core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import javax.sound.midi.SysexMessage;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MapGenerator {
    private int p_x;
    private int lights;
    private int p_y;
    private int a_x;
    private int a_y;
    private ArrayList<Integer> Cali_x;
    private ArrayList<Integer> Cali_y;


    private TETile[][] world;
    private int HEIGHT;
    private int WIDTH;
    private Random SEED;

    private ArrayList<Integer> Floor_x;
    private ArrayList<Integer> Floor_y;

    public MapGenerator(int width, int height, Random seed) {
        WIDTH = width - 4;
        HEIGHT = height - 4;
        SEED = seed;
        world = new TETile[width][height];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                world[w][h] = Tileset.NOTHING;
            }
        }
        makeRoomsRandomly();
        Walls();
    }

    private void Walls() {
        for (int i = 1; i < world.length -1; i++) {
            for (int j = 1; j < world[0].length -1; j++) {
                if (world[i][j] == Tileset.NOTHING) {
                    boolean flag = false;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (world[i + di][j + dj] == Tileset.FLOOR) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        world[i][j] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public TETile[][] map() {
        TETile[][] ans = new TETile[world.length][world[0].length];
        if (lights == 0) {
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world[0].length; j++) {
                    ans[i][j] = Tileset.NOTHING;
                }
            }
            for (int i = Math.max(0, p_x - 6); i < Math.min(p_x + 7, world.length); i++) {
                for (int j = Math.max(0, p_y - 6); j < Math.min(p_y + 7, world[0].length); j++) {
                    ans[i][j] = world[i][j];
                }
            }
            return ans;
        }
        return world.clone();
    }

    public void makeRoomsRandomly() {
        Floor_x = new ArrayList<>();
        Floor_y = new ArrayList<>();
        Floor_x.add(RandomUtils.uniform(SEED, 15, world.length - 15));
        Floor_y.add(RandomUtils.uniform(SEED, 15, world[0].length - 15));
        ArrayList<Integer> temp_x = new ArrayList<>();
        ArrayList<Integer> temp_y = new ArrayList<>();
        temp_x.add(Floor_x.get(0));
        temp_y.add(Floor_y.get(0));

        int numberofhalls = 25;
        int cali = RandomUtils.uniform(SEED, 0, 25);
        Cali_x = new ArrayList<>();
        Cali_y = new ArrayList<>();
        for (int k = 0; k < numberofhalls; k++) {
            int index = RandomUtils.uniform(SEED, 0, temp_x.size());
            int prev_x = temp_x.get(index);
            int prev_y = temp_y.get(index);
            int x = RandomUtils.uniform(SEED, 6, WIDTH - 6);
            int y = RandomUtils.uniform(SEED, 6, WIDTH - 6);
            temp_x.clear();
            temp_y.clear();
            int direction = RandomUtils.uniform(SEED, 0, 3);
            int w;
            int h;
            switch (direction) {
                case 0 : //vertical
                    if (x > world.length/2) {
                        w = RandomUtils.uniform(SEED, -15, -5);
                    } else {
                        w = RandomUtils.uniform(SEED, 5, 15);
                    }
                    h = RandomUtils.uniform(SEED, 1, 3);
                    break;
                case 1 : //horizontal
                    if (y > world[0].length/2) {
                        h = RandomUtils.uniform(SEED, -15, -5);
                    } else {
                        h = RandomUtils.uniform(SEED, 5, 15);
                    }
                    w = RandomUtils.uniform(SEED, 1, 3);
                    break;
                default : //room
                    if (y > world[0].length/2) {
                        h = RandomUtils.uniform(SEED, -9, -5);
                    } else {
                        h = RandomUtils.uniform(SEED, 5, 9);
                    }
                    if (x > world.length/2) {
                        w = RandomUtils.uniform(SEED, -9, -5);
                    } else {
                        w = RandomUtils.uniform(SEED, 5, 9);
                    }
            }
            boolean joined = true;
            for (int i = min(x, x + w); i < max(x, x + w); i++) {
                for (int j = min(y, y + h); j < max(y, y + h); j++) {
                    if (i >= 4 && i < world.length - 4 && j >= 4 && j < world[0].length - 4) {
                        if (world[i][j] != Tileset.FLOOR) {
                            world[i][j] = Tileset.FLOOR;
                            Floor_x.add(i);
                            Floor_y.add(j);
                            joined = false;
                        }
                        temp_x.add(i);
                        temp_y.add(j);
                    }
                    else {
                        break;
                    }
                }
            }
            if (!joined) {
                join(prev_x, prev_y, x, y);
            }
            if (k == cali) {
                Cali_y.addAll(temp_y);
                Cali_x.addAll(temp_x);
                for (int i = 0; i < Cali_y.size(); i++) {
                    Floor_x.remove(Floor_x.size() - 1 - i);
                    Floor_y.remove(Floor_x.size() - 1 - i);
                }
            }
        }
    }

    public void join(int prev_x, int prev_y, int x, int y) {
        int i = prev_x;
        for (int j = min(prev_y, y); j <= max(prev_y, y); j++) {
            if (world[i][j] != Tileset.FLOOR) {
                world[i][j] = Tileset.FLOOR;
                Floor_x.add(i);
                Floor_y.add(j);
            }
        }
        int j = y;
        for (i = min(prev_x, x); i <= max(prev_x, x); i++) {
            if (world[i][j] != Tileset.FLOOR) {
                world[i][j] = Tileset.FLOOR;
                Floor_x.add(i);
                Floor_y.add(j);
            }
        }
    }

    public String move(char k) {
        int x = p_x;
        int y = p_y;
        switch (k) {
            case 'w' -> y ++;
            case 'a' -> x --;
            case 's' -> y --;
            case 'd' -> x ++;
        }
        try {
            if (world[x][y] == Tileset.FLOOR) {
                world[p_x][p_y] = Tileset.FLOOR;
                p_x = x;
                p_y = y;
                world[x][y] = Tileset.AVATAR;
            } else if (world[x][y] == Tileset.TREE) {
                return "Omer";
            }
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
        int a = RandomUtils.uniform(SEED, 0, Floor_x.size());
        world[a_x][a_y] = Tileset.FLOOR;
        a_x = Floor_x.get(a);
        a_y = Floor_y.get(a);
        world[a_x][a_y] = Tileset.OPP;
        if (p_x == a_x && a_y == p_y) {
            return "Albert";
        }
        return "";
    }

    public void player(String seed) {
        for (int i = 0; i < Cali_y.size(); i++) {
            world[Cali_x.get(i)][Cali_y.get(i)] = Tileset.TREE;
        }
        int p = RandomUtils.uniform(SEED, 0, Floor_x.size());
        p_x = Floor_x.get(p);
        p_y = Floor_y.get(p);
        world[p_x][p_y] = Tileset.AVATAR;
        int a = RandomUtils.uniform(SEED, 0, Floor_x.size());
        a_x = Floor_x.get(a);
        a_y = Floor_y.get(a);
        world[a_x][a_y] = Tileset.OPP;
    }

    public void lights() {
        lights = 1 - lights;
    }
}
