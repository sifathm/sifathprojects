package byow.Core;

import byow.TileEngine.TETile;

public class Point {
    private final int x;
    private final int y;
    private TETile type;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, TETile tileType) {
        this.x = x;
        this.y = y;
        this.type = tileType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TETile getType() {
        return type;
    }

    public void drawPoint(TETile[][] tiles) {
        tiles[x][y] = type;
    }
}

