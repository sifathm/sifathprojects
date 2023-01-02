package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Engine extends RoomWorld {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    private static final int MENU_WIDTH = 35;
    private static final int MENU_HEIGHT = 40;
    private static final int KEYBOARD = 0;
    private static final int STRING = 2;
    TERenderer ter = new TERenderer();
    private List<String> commandRecord;
    private int inputType;
    private boolean continueGame;
    private Avatar avatar;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (commandRecord == null) {
            commandRecord = new ArrayList<>();
        }
        inputType = KEYBOARD;
        continueGame = true;
        KeyboardInputSource input = new KeyboardInputSource(ter, world, true);
        drawMenu();
        char command = input.getNextKey();
        if (command == 'N') {
            StdDraw.clear(StdDraw.BLACK);
            ter.initialize(WIDTH, HEIGHT);
            long seed = getSeed(input);
            world = createWorld(seed, input);
            ter.renderFrame(world);
            input = new KeyboardInputSource(ter, world, false);
            runGame(input);
        } else if (command == 'L') {
            String pastCommands = loadGame();
            world = interactWithInputString(pastCommands);
            inputType = KEYBOARD;
            ter.initialize(WIDTH, HEIGHT);
            input = new KeyboardInputSource(ter, world, false);
            ter.renderFrame(world);
            runGame(input);
        } else if (command == 'Q') {
            System.exit(0);
        } else {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, "Please enter a valid command");
            for (int j = 0; j < 5000; j++) {
                StdDraw.show();
            }
            interactWithKeyboard();
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
     * In other words, both of these calls:
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

        inputType = STRING;
        if (commandRecord == null) {
            commandRecord = new ArrayList<>();
        }
        continueGame = true;
        InputSource inputString = new StringInputDevice(input);
        char command = inputString.getNextKey();
        if (command == 'N') {
            long seed = getSeed(inputString);
            world = createWorld(seed, inputString);
            runGame(inputString);
        } else if (command == 'L') {
            String pastCommands = loadGame();
            world = interactWithInputString(pastCommands);
            runGame(inputString);
        } else {
            inputString = new StringInputDevice(input);
            long seed = getSeed(inputString);
            world = createWorld(seed, inputString);
            runGame(inputString);
        }
        return world;

    }

    public TETile[][] createWorld(long seed, InputSource inp) {
        world = roomWorld(seed);
        avatar = new Avatar(world, seed, ter, inputType, inp);
        avatar.placePlayer(seed);
        return world;
    }

    private long getSeed(InputSource input) {
        String result = "";
        while (input.possibleNextInput()) {
            char next = input.getNextKey();
            commandRecord.add(String.valueOf(next));
            if (next == 'N') {
                continue;
            } else if (next == 'S') {
                break;
            } else {
                result += next;
            }
        }
        long seed = 0;
        for (int i = 0; i < result.length(); i++) {
            seed = seed * 10 + Character.getNumericValue(result.charAt(i));
        }
        return seed;
    }

    public void runGame(InputSource input) {
        while (input.possibleNextInput() && continueGame) {
            char next = input.getNextKey();
            if (next == ':') {
                if (input.possibleNextInput()) {
                    next = input.getNextKey();
                    if (next == 'Q') {
                        continueGame = false;
                        saveGame();
                    }
                }
            }
            commandRecord.add(String.valueOf(next));
            moveAvatar(next);
            if (inputType == KEYBOARD) {
                ter.renderFrame(world);
            }

        }
        saveGame();
    }

    private void saveGame() {
        try {
            FileWriter writeFile = new FileWriter("load.txt");
            String commands = "";
            for (int i = 0; i < commandRecord.size(); i++) {
                commands += commandRecord.get(i);
            }
            writeFile.write(commands);
            writeFile.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file.");
        }
        if (inputType == KEYBOARD) {
            System.exit(0);
        }
    }


    private String loadGame() {
        File loadData = new File("load.txt");
        return Utils.readContentsAsString(loadData);
    }


    private void moveAvatar(char input) {
        switch (input) {
            case 'W':
                avatar.moveUp();
                break;
            case 'A':
                avatar.moveLeft();
                break;
            case 'S':
                avatar.moveDown();
                break;
            case 'D':
                avatar.moveRight();
                break;
            default:
                avatar.doNothing();
                break;
        }

    }

    private void drawMenu() {
        StdDraw.setCanvasSize(MENU_WIDTH * 10, MENU_HEIGHT * 10);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, MENU_WIDTH);
        StdDraw.setYscale(0, MENU_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
        String title = "The Game";
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 * 3, title);
        font = new Font("Arial", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.orange);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, "Load Game (L)");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 - 4, "Quit (Q)");
        StdDraw.show();
    }

}



