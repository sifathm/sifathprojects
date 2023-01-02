package byow.Core;

import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Avatar extends RoomWorld {
    private final TETile icon = Tileset.AVATAR;
    private int x;
    private int y;
    private final Random ran;
    private final TETile[][] world;
    private final TERenderer ter;
    private final int inputType;
    private final TETile trailTile;
    private final InputSource input;


    public Avatar(TETile[][] w, long s, TERenderer t, int i, InputSource inp) {
        world = w;
        ran = new Random(s);
        ter = t;
        inputType = i;
        trailTile = Tileset.AVATAR;
        input = inp;
    }

    public void moveLeft() {
        if (world[x - 1][y] == Tileset.WALL) {
            return;
        }
        world[x][y] = Tileset.FLOOR;
        this.x--;
        world[x][y] = Tileset.AVATAR;
    }

    public void moveRight() {
        if (world[x + 1][y] == Tileset.WALL) {
            return;
        }
        world[x][y] = Tileset.FLOOR;
        this.x++;
        world[x][y] = Tileset.AVATAR;
        return;
    }

    public void moveUp() {
        if (world[x][y + 1] == Tileset.WALL) {
            return;
        }
        world[x][y] = Tileset.FLOOR;
        this.y++;
        world[x][y] = Tileset.AVATAR;
        return;
    }

    public void moveDown() {
        if (world[x][y - 1] == Tileset.WALL) {
            return;
        }
        world[x][y] = Tileset.FLOOR;
        this.y--;
        world[x][y] = Tileset.AVATAR;
    }

    public String doNothing() {
        return null;
    }

    public void placePlayer(long seed) {
        boolean setPlayer = true;
        Random random = new Random(seed);
        while (setPlayer) {
            x = random.nextInt(WIDTH / 3, 2 * WIDTH / 3);
            y = random.nextInt(HEIGHT / 3, 2 * WIDTH / 3);
            if (world[x][y].equals(Tileset.FLOOR)) {
                setPlayer = false;
                world[x][y] = Tileset.AVATAR;
            }
        }
    }


}
