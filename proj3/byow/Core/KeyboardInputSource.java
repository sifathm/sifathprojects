package byow.Core;

/**
 * Created by hug.
 */

import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputSource extends RoomWorld implements InputSource {
    String input;
    TERenderer ter;
    TETile[][] world;
    private boolean printKeys;


    public KeyboardInputSource(TERenderer t, TETile[][] w, boolean print) {
        input = "";
        ter = t;
        world = w;
        printKeys = print;
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (printKeys) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
