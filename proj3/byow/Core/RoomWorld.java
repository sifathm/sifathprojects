package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class RoomWorld {

    static final int WIDTH = 50;
    static final int HEIGHT = 50;
    static final int TIMES = 50;
    ArrayList<Room> rooms = new ArrayList<>();
    TETile[][] world = new TETile[WIDTH][HEIGHT];
    TETile[][] roomLocation = new TETile[WIDTH][HEIGHT];


    public void emptyFill(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void fillRoom(Random random) {
        for (int i = 0; i < TIMES; i++) {
            int x = random.nextInt(1, WIDTH - 2);
            int y = random.nextInt(1, HEIGHT - 2);
            int width = random.nextInt(WIDTH / 10) + 4;
            int height = random.nextInt(HEIGHT / 5) + 4;
            if (y + height + 1 >= HEIGHT || x + width + 1 >= WIDTH) {
                continue;
            }
            if (isOverlap(x, y, width, height)) {
                continue;
            }
            buildRoom(x, y, width, height);
            rooms.add(new Room(x, y, width, height));
        }

    }

    public boolean isOverlap(int x, int y, int width, int height) {
        for (int i = x; i <= x + width + 1; i++) {
            for (int j = y; j <= y + height + 1; j++) {
                if (roomLocation[i][j] == Tileset.WALL || roomLocation[i][j] == Tileset.GRASS) {
                    return true;
                }
            }
        }
        return false;
    }

    public void buildRoom(int x, int y, int width, int height) {
        for (int i = x; i <= x + width + 1; i++) {
            for (int j = y; j <= y + height + 1; j++) {
                if (i == x || i == x + width + 1 || j == y || j == y + height + 1) {
                    world[i][j] = Tileset.WALL;
                    roomLocation[i][j] = Tileset.WALL;
                    continue;
                }
                world[i][j] = Tileset.FLOOR;
                roomLocation[i][j] = Tileset.FLOOR;
            }
        }
    }

    public ArrayList<Point> createEntrance(Room curRoom) {
        ArrayList<Point> edgeList = new ArrayList<>();
        for (int a = curRoom.x; a <= curRoom.x + curRoom.width + 1; a++) {
            for (int b = curRoom.y; b <= curRoom.y + curRoom.height + 1; b++) {
                if (a == curRoom.x || a == curRoom.x + curRoom.width + 1
                        || b == curRoom.y || b == curRoom.y + curRoom.height + 1) {
                    if (a == curRoom.x && b == curRoom.y) {
                        continue;
                    }
                    if (a == curRoom.x + curRoom.width + 1 && b == curRoom.y) {
                        continue;
                    }
                    if (a == curRoom.x && b == curRoom.y + curRoom.height + 1) {
                        continue;
                    }
                    if (a == curRoom.x + curRoom.width + 1 && b == curRoom.y + curRoom.height + 1) {
                        continue;
                    }
                    edgeList.add(new Point(a, b, Tileset.FLOOR));
                }

            }
        }
        return edgeList;

    }

    public void turnDown(Room curRoom, Point entrance, Random random) {
        if (entrance.getY() == curRoom.y) {
            int end = random.nextInt(entrance.getY() + 1);
            for (int c = 1; c <= end; c++) {
                if (Tileset.WALL.equals(world[entrance.getX()][entrance.getY() - c])) {
                    break;
                }
                Point newPoint = new Point(entrance.getX(), entrance.getY() - c, Tileset.FLOOR);
                newPoint.drawPoint(world);
                int newStart = entrance.getY() - end;
                if (entrance.getX() <= WIDTH / 2) {
                    for (int d = 1; d <= WIDTH - entrance.getX() - 1; d++) {
                        if (Tileset.WALL.equals(world[entrance.getX() + d][newStart])) {
                            break;
                        }
                        Point downLeft = new Point(entrance.getX() + d, newStart, Tileset.FLOOR);
                        downLeft.drawPoint(world);
                    }
                } else {
                    for (int d = 1; d <= WIDTH - entrance.getX() - 1; d++) {
                        if (Tileset.WALL.equals(world[entrance.getX() - d][newStart])) {
                            break;
                        }
                        Point downRight = new Point(entrance.getX() - d, newStart, Tileset.FLOOR);
                        downRight.drawPoint(world);
                    }
                }
            }
        }
    }

    public void turnUp(Room curRoom, Point entrance, Random random) {
        if (entrance.getY() == curRoom.y + curRoom.height + 1) {
            int end = random.nextInt(HEIGHT - entrance.getY() + 1);
            for (int c = 1; c <= end; c++) {
                if (Tileset.WALL.equals(world[entrance.getX()][entrance.getY() + c - 1])) {
                    break;
                }
                Point newPoint =
                        new Point(entrance.getX(), entrance.getY() + c - 1, Tileset.FLOOR);
                newPoint.drawPoint(world);
                int newStart = entrance.getY() + end - 1;
                if (entrance.getX() <= WIDTH / 2) {
                    for (int d = 1; d <= WIDTH - entrance.getX() - 1; d++) {
                        if (Tileset.WALL.equals(world[entrance.getX() + d][newStart - 1])) {
                            break;
                        }
                        Point downLeft =
                                new Point(entrance.getX() + d, newStart - 1, Tileset.FLOOR);
                        downLeft.drawPoint(world);
                    }
                } else {
                    for (int d = 1; d <= WIDTH - entrance.getX() - 1; d++) {
                        if (Tileset.WALL.equals(world[entrance.getX() - d][newStart])) {
                            break;
                        }
                        Point downRight = new Point(entrance.getX() - d, newStart, Tileset.FLOOR);
                        downRight.drawPoint(world);
                    }
                }
            }
        }
    }

    public void turnLeft(Room curRoom, Point entrance, Random random) {
        if (entrance.getX() == curRoom.x) {
            int end = random.nextInt(entrance.getX() + 1);
            for (int e = 1; e <= end; e++) {
                if (Tileset.WALL.equals(world[entrance.getX() - e][entrance.getY()])) {
                    break;
                }
                Point newPoint = new Point(entrance.getX() - e, entrance.getY(), Tileset.FLOOR);
                newPoint.drawPoint(world);
            }
            int newStart = entrance.getX() - end;
            if (entrance.getY() <= HEIGHT / 2) {
                for (int c = 1; c <= HEIGHT - entrance.getY() - 1; c++) {
                    if (Tileset.WALL.equals(world[newStart][entrance.getY() + c])) {
                        break;
                    }
                    Point leftUp = new Point(newStart, entrance.getY() + c, Tileset.FLOOR);
                    leftUp.drawPoint(world);
                }
            } else {
                for (int c = 1; c <= HEIGHT - entrance.getY() - 1; c++) {
                    if (Tileset.WALL.equals(world[newStart][entrance.getY() - c])) {
                        break;
                    }
                    Point leftDown = new Point(newStart, entrance.getY() - c, Tileset.FLOOR);
                    leftDown.drawPoint(world);
                }
            }
        }


    }

    public void connectRoomWithHallWay(Random random) {
        for (int i = 0; i < rooms.size(); i++) {
            Room curRoom = rooms.get(i);
            ArrayList<Point> edgeList = createEntrance(curRoom);
            int start = random.nextInt(1, (curRoom.width + curRoom.height - 4) * 2 - 1);
            Point entrance = edgeList.get(start);
            edgeList.remove(start);
            entrance.drawPoint(world);

            turnDown(curRoom, entrance, random);
            turnUp(curRoom, entrance, random);
            turnLeft(curRoom, entrance, random);

            int start2 = random.nextInt(1, (curRoom.width + curRoom.height - 4) * 2 - 2);
            Point entrance2 = edgeList.get(start2);
            edgeList.remove(start2);
            entrance2.drawPoint(world);

            turnDown(curRoom, entrance2, random);
            turnUp(curRoom, entrance2, random);
            turnLeft(curRoom, entrance2, random);

        }

    }

    public void constructWall() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.FLOOR) && world[i - 1][j].equals(Tileset.NOTHING)
                        && world[i + 1][j].equals(Tileset.NOTHING)) {
                    Point neoLeft = new Point(i - 1, j, Tileset.WALL);
                    Point neoRight = new Point(i + 1, j, Tileset.WALL);
                    neoLeft.drawPoint(world);
                    neoRight.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i][j + 1].equals(Tileset.NOTHING)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    Point neoUp = new Point(i, j + 1, Tileset.WALL);
                    Point neoDown = new Point(i, j - 1, Tileset.WALL);
                    neoUp.drawPoint(world);
                    neoDown.drawPoint(world);
                }
            }
        }
    }

    public void repairHallway() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {

                if (world[i][j].equals(Tileset.FLOOR) && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    Point neoDown = new Point(i, j - 1, Tileset.WALL);
                    neoDown.drawPoint(world);
                }

                if (world[i][j].equals(Tileset.FLOOR) && world[i - 1][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i + 1, j, Tileset.WALL);
                    neo.drawPoint(world);
                }


                if (world[i][j].equals(Tileset.FLOOR) && world[i - 1][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i, j, Tileset.WALL);
                    neo.drawPoint(world);
                }

                if (world[i][j].equals(Tileset.FLOOR) && world[i][j + 1].equals(Tileset.NOTHING)
                        && world[i + 1][j].equals(Tileset.NOTHING)) {
                    Point neoUp = new Point(i, j + 1, Tileset.WALL);
                    Point neoRight = new Point(i + 1, j, Tileset.WALL);
                    neoUp.drawPoint(world);
                    neoRight.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i + 1][j].equals(Tileset.WALL)
                        && world[i][j + 1].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i + 1, j + 1, Tileset.WALL);
                    neo.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i + 1][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i - 1, j, Tileset.WALL);
                    neo.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i - 1][j].equals(Tileset.NOTHING)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    Point neoLeft = new Point(i - 1, j, Tileset.WALL);
                    Point neoDown = new Point(i, j - 1, Tileset.WALL);
                    Point neoCorner = new Point(i - 1, j - 1, Tileset.WALL);
                    neoLeft.drawPoint(world);
                    neoDown.drawPoint(world);
                    neoCorner.drawPoint(world);
                }

                if (world[i][j].equals(Tileset.FLOOR) && world[i - 1][j].equals(Tileset.NOTHING)
                        && world[i + 1][j].equals(Tileset.FLOOR)) {
                    Point neo = new Point(i, j, Tileset.WALL);
                    neo.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i][j - 1].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i, j - 1, Tileset.WALL);
                    neo.drawPoint(world);
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i][j + 1].equals(Tileset.NOTHING)) {
                    Point neo = new Point(i, j, Tileset.WALL);
                    neo.drawPoint(world);
                }

            }

        }
    }

    public void contructEdge() {
        for (int i = 0; i < WIDTH - 1; i++) {
            for (int j = 0; j < HEIGHT - 1; j++) {
                if (i == 0 | j == 0 | i == WIDTH - 1 || j == HEIGHT - 1) {
                    if (world[i][j].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i + 1][j].equals(Tileset.NOTHING)) {
                        world[i][j] = Tileset.NOTHING;
                    }
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i][j + 1].equals(Tileset.NOTHING)) {
                        world[i][j] = Tileset.NOTHING;
                    }
                }
            }
        }
    }

    public void breakWall() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.NOTHING)) {
                    world[i + 1][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i - 1][j].equals(Tileset.NOTHING)) {
                    world[i - 1][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    world[i][j - 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i - 1][j - 1].equals(Tileset.WALL)
                        && world[i - 1][j + 1].equals(Tileset.WALL)) {
                    world[i][j] = Tileset.FLOOR;
                    world[i][j + 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i - 1][j + 1].equals(Tileset.WALL)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.NOTHING)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.WALL)
                        && world[i + 1][j - 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i - 1][j].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.WALL)
                        && world[i - 1][j - 1].equals(Tileset.NOTHING)) {
                    world[i][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i - 1][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.NOTHING)
                        && world[i - 1][j - 1].equals(Tileset.WALL)) {
                    world[i][j - 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.NOTHING)
                        && world[i - 1][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public void breakTwoWall() {
        for (int i = 2; i < WIDTH - 1; i++) {
            for (int j = 2; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.WALL) && world[i - 1][j].equals(Tileset.WALL)
                        && world[i - 2][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                    world[i - 1][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.WALL) && world[i - 1][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i][j + 1].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.WALL)
                        && world[i - 1][j - 1].equals(Tileset.FLOOR)
                        && world[i + 1][j + 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                    world[i - 1][j] = Tileset.FLOOR;
                    world[i][j + 1] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.FLOOR)
                        && world[i - 1][j].equals(Tileset.WALL)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.WALL) && world[i - 1][j].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i - 1][j + 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                }

                if (world[i][j].equals(Tileset.WALL) && world[i][j - 1].equals(Tileset.WALL)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i][j - 2].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.FLOOR;
                    world[i][j - 1] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.FLOOR) && world[i][j + 1].equals(Tileset.WALL)
                        && world[i][j - 1].equals(Tileset.NOTHING)
                        && world[i + 1][j - 1].equals(Tileset.NOTHING)) {
                    world[i][j - 1] = Tileset.FLOOR;
                    world[i + 1][j - 1] = Tileset.FLOOR;
                    world[i + 1][j] = Tileset.WALL;
                }

            }
        }
    }

    public void contructEdge2() {
        for (int i = 0; i <= WIDTH - 1; i++) {
            for (int j = 0; j <= HEIGHT - 1; j++) {
                if (j == HEIGHT - 1) {
                    if (world[i][j].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                }
                if (i == 0) {
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i + 1][j].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i + 1][j].equals(Tileset.NOTHING)
                            && world[i][j - 1].equals(Tileset.NOTHING)) {
                        world[i][j] = Tileset.NOTHING;
                    }
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i + 1][j].equals(Tileset.WALL)) {
                        world[i][j] = Tileset.WALL;
                        world[i + 1][j] = Tileset.FLOOR;
                    }
                }
                if (j == 0) {
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i][j + 1].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public void constructEdge3() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (j == HEIGHT - 1) {
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i - 1][j].equals(Tileset.NOTHING)
                            && world[i + 1][j].equals(Tileset.NOTHING)
                            && world[i][j - 1].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.NOTHING;
                        world[i][j - 1] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i - 1][j].equals(Tileset.WALL)
                            && world[i + 1][j].equals(Tileset.NOTHING)
                            && world[i][j - 1].equals(Tileset.WALL)) {
                        world[i][j] = Tileset.WALL;
                    }
                }
                if (i == WIDTH - 1) {
                    if (world[i][j].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i - 1][j].equals(Tileset.WALL)
                            && world[i][j - 1].equals(Tileset.WALL)) {
                        world[i][j] = Tileset.WALL;
                    }
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.NOTHING)
                        && world[i + 1][j - 1].equals(Tileset.NOTHING)) {
                    world[i + 1][j - 1] = Tileset.WALL;
                    world[i][j - 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i][j - 1].equals(Tileset.NOTHING)) {
                    world[i][j - 1] = Tileset.WALL;
                }
            }
        }
    }

    public void constructEdge4() {
        for (int i = 1; i <= WIDTH - 3; i++) {
            for (int j = 1; j <= HEIGHT - 3; j++) {
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.NOTHING)) {
                    world[i][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i + 2][j].equals(Tileset.FLOOR)) {
                    world[i + 1][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.NOTHING)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.WALL)
                        && world[i + 1][j + 2].equals(Tileset.FLOOR)
                        && world[i + 2][j].equals(Tileset.WALL)
                        && world[i + 2][j - 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.WALL;
                    world[i + 1][j] = Tileset.FLOOR;
                    world[i + 1][j + 1] = Tileset.FLOOR;
                    world[i + 1][j - 1] = Tileset.FLOOR;
                    world[i][j - 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.WALL)
                        && world[i + 2][j + 1].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.WALL)) {
                    world[i + 1][j] = Tileset.FLOOR;
                    world[i + 1][j + 1] = Tileset.FLOOR;
                    world[i + 2][j - 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.WALL)
                        && world[i + 1][j + 1].equals(Tileset.WALL)
                        && world[i + 1][j + 2].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.WALL)) {
                    world[i + 1][j] = Tileset.FLOOR;
                    world[i + 1][j + 1] = Tileset.FLOOR;
                    world[i + 2][j] = Tileset.WALL;
                }
            }
        }
    }

    public void constructEdge5() {
        for (int i = 1; i <= WIDTH - 1; i++) {
            for (int j = 1; j <= HEIGHT - 1; j++) {
                if (j == HEIGHT - 1) {
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i][j - 1].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i][j - 1].equals(Tileset.NOTHING)) {
                        world[i][j] = Tileset.NOTHING;
                    }
                }
                if (i == HEIGHT - 1) {
                    if (world[i][j].equals(Tileset.FLOOR)
                            && world[i][j - 1].equals(Tileset.NOTHING)) {
                        world[i][j] = Tileset.WALL;
                        world[i][j - 1] = Tileset.WALL;
                    }
                    if (world[i][j].equals(Tileset.NOTHING)
                            && world[i][j - 1].equals(Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                        world[i][j - 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public void constructEdge6() {
        for (int i = 0; i < WIDTH - 1; i++) {
            for (int j = 0; j < HEIGHT - 1; j++) {
                if (i == 0) {
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i + 1][j].equals(Tileset.WALL)
                            && world[i + 1][j + 1].equals(Tileset.FLOOR)
                            && world[i + 1][j - 1].equals(Tileset.FLOOR)) {
                        world[i + 1][j] = Tileset.FLOOR;
                    }
                    if (world[i][j].equals(Tileset.WALL)
                            && world[i + 1][j].equals(Tileset.FLOOR)
                            && world[i + 1][j + 1].equals(Tileset.NOTHING)) {
                        world[i + 1][j + 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public void repairWall2() {
        for (int i = 0; i < WIDTH - 1; i++) {
            for (int j = 0; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.NOTHING)) {
                    world[i][j + 1] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.FLOOR)
                        && world[i + 1][j].equals(Tileset.NOTHING)) {
                    world[i + 1][j] = Tileset.WALL;
                }
                if (world[i][j].equals(Tileset.WALL)
                        && world[i + 1][j].equals(Tileset.FLOOR)
                        && world[i][j + 1].equals(Tileset.FLOOR)
                        && world[i + 1][j + 1].equals(Tileset.WALL)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j].equals(Tileset.NOTHING)
                        && world[i][j + 1].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public TETile[][] roomWorld(long seed) {
        emptyFill(world);
        Random random = new Random(seed);
        fillRoom(random);
        connectRoomWithHallWay(random);
        constructWall();
        repairHallway();
        breakWall();
        contructEdge();
        contructEdge2();
        constructEdge3();
        breakTwoWall();
        constructEdge4();
        constructEdge5();
        constructEdge6();
        repairWall2();
        return world;
    }
}






