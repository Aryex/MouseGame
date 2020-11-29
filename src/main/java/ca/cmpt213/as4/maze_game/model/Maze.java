package ca.cmpt213.as4.maze_game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static java.lang.Math.*;

/*
 * Maze class represent a maze using a 2D
 * boolean array.
 *
 * Maze class uses a Depth-First-Loop
 * to create a random maze in constructor.
 * Afterwards it will add cycles to the maze. */
public class Maze {
    //private final boolean[][] WALL_MAP;
    private boolean[][] WALL_MAP;
    private final int MAX_COL;
    private final int MAX_ROW;
    private final int LAST_COL;
    private final int LAST_ROW;

    private Stack<Point> movementStack = new Stack<>();
    private Stack<Point> visitedStack = new Stack<>();

    public Maze(int row, int cols) {
        this.MAX_COL = cols;
        this.MAX_ROW = row;
        this.LAST_COL = MAX_COL - 1;
        this.LAST_ROW = MAX_ROW - 1;
        WALL_MAP = new boolean[MAX_ROW][MAX_COL];

        buildBaseMap();
        createMazeOnMap();
        clearCorners();
        makeCycle(25, 100);

    }

    // breaking a length 3 wall will ensure that
    // no open square is created.
    private void makeCycle(int frequency, int tries) {
        int counter = 0;
        while (counter < frequency) {
            if (findAndBreakRandomLength3Wall(1)) {
                counter++;
            } else {
                //counter++;
            }
        }
        System.out.println("Coutner: " + counter);
        counter = 0;
        while (counter < frequency) {
            if (findAndBreakRandomLength3Wall(0)) {
                counter++;
            } else {
                // counter++;
            }
        }
        System.out.println("Coutner: " + counter);

    }

    private boolean findAndBreakRandomLength3Wall(int type) {
        Random rand = new Random();
        int randRow = rand.nextInt(LAST_ROW - 3) + 2;
        int randCol = rand.nextInt(LAST_COL - 3) + 2;
        Point randPoint = new Point(randCol, randRow);

        switch (type) {
            case 0:
                if (connectHorizontalWallLength3(randPoint)) {
                    WALL_MAP[randRow][randCol] = false;
                    return true;
                }
                break;
            case 1:
                if (connectVerticalWallLength3(randPoint)) {
                    WALL_MAP[randRow][randCol] = false;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private boolean connectVerticalWallLength3(Point point) {
        int col = (int) point.getX();
        int row = (int) point.getY();

        if (!isWallAt(point) && !isInsideMaze(point)) {
            return false;
        }

        int rowNumLeft = row - 1;
        int rowNumRight = row + 1;
        int colNumAbove = col - 1;
        int colNumBellow = col + 1;

        if (WALL_MAP[row][colNumAbove] && WALL_MAP[row][colNumBellow]
                && WALL_MAP[rowNumLeft][col] && WALL_MAP[rowNumRight][col]) {
            return false;
        }
        return WALL_MAP[row][colNumAbove] && WALL_MAP[row][colNumBellow];


    }

    private boolean connectHorizontalWallLength3(Point point) {
        int col = (int) point.getX();
        int row = (int) point.getY();

        int rowNumLeft = row - 1;
        int rowNumRight = row + 1;
        int colNumAbove = col - 1;
        int colNumBellow = col + 1;

        if (!isWallAt(point) && !isInsideMaze(point)) {
            return false;
        }

        if (WALL_MAP[row][colNumAbove] && WALL_MAP[row][colNumBellow]
                && WALL_MAP[rowNumLeft][col] && WALL_MAP[rowNumRight][col]) {
            return false;
        }
        return WALL_MAP[rowNumLeft][col] && WALL_MAP[rowNumRight][col];
    }

    private void createMazeOnMap() {

        Random rand = new Random();
        Point position;

        do {
            int row = rand.nextInt(LAST_ROW - 1);
            int col = rand.nextInt(LAST_COL - 1);
            position = new Point(col, row);
        } while (isWallAt(position));
        movementStack.push(position);

        Point nextPosition;
        do {

            if (haveNotVisited(position)) {
                movementStack.push(position);
                visitedStack.push(position);
            }

            ArrayList<Point> availableDirections = getAvailableDirectionsFrom(position);

            if (!availableDirections.isEmpty()) {
                nextPosition = pickARandPointFrom(availableDirections);
                breakWall(nextPosition);
                position = clonePoint(nextPosition);
            } else {
                Point topStack = movementStack.pop();
                position = clonePoint(topStack);
            }

        } while (!movementStack.empty());
    }

    private void breakWall(Point whereImGoingToBe) {
        if (isWallAt(whereImGoingToBe)) {
            WALL_MAP[(int) whereImGoingToBe.getY()][(int) whereImGoingToBe.getX()] = false;
        }
    }

    private Point pickARandPointFrom(ArrayList<Point> points) {
        Random rand = new Random();
        return points.get(rand.nextInt(points.size()));
    }


    private ArrayList<Point> getAvailableDirectionsFrom(Point myPosition) {

        ArrayList<Point> nsew = getNSEWPointsFrom(myPosition);
        ArrayList<Point> nsewInMaze = removePointsNotInMaze(nsew);

        return availableDirectionsFrom(myPosition, nsewInMaze);
    }

    private ArrayList<Point> availableDirectionsFrom(Point myPosition, ArrayList<Point> directions) {
        ArrayList<Point> availableDirections = new ArrayList<>();

        for (Point direction : directions) {
            if (isWallAt(direction)) {
                if (isWallBreakable(myPosition, direction)) {
                    availableDirections.add(clonePoint(direction));
                }
            } else {
                if (haveNotVisited(direction)) {
                    availableDirections.add(clonePoint(direction));
                }
            }
        }
        return availableDirections;
    }

    private boolean isWallBreakable(Point myPosition, Point wall) {
        Point otherSideOfWall = getOtherSideOfWall(myPosition, wall);

        if (isInsideMaze(otherSideOfWall)) {

            return isInsideMaze(wall) &&
                    !isWallAt(otherSideOfWall) &&
                    haveNotVisited(otherSideOfWall);

        } else {

            return (isWallBeside(wall, myPosition));

        }
    }

    private boolean isWallBeside(Point wall, Point myPosition) {
        int xDiff = (int) (wall.getX() - myPosition.getX());
        int yDiff = (int) (wall.getY() - myPosition.getY());
        Point besideWall1 = clonePoint(wall);
        Point besideWall2 = clonePoint(wall);

        if (yDiff != 0 && xDiff == 0) {
            besideWall1.translate(1, 0);
            besideWall2.translate(-1, 0);
            return WALL_MAP[(int) besideWall1.getY()][(int) besideWall1.getX()]
                    && WALL_MAP[(int) besideWall2.getY()][(int) besideWall2.getX()];
        }

        if (xDiff != 0 && yDiff == 0) {
            besideWall1.translate(0, 1);
            besideWall2.translate(0, -1);
            return WALL_MAP[(int) besideWall1.getY()][(int) besideWall1.getX()]
                    && WALL_MAP[(int) besideWall2.getY()][(int) besideWall2.getX()];
        }

        return false;
    }


    private boolean isInsideMaze(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();

        return (x >= 1 && x < LAST_COL) && (y >= 1 && y < LAST_ROW);
    }

    private Point getOtherSideOfWall(Point myPosition, Point wall) {
        int xDiff = (int) (wall.getX() - myPosition.getX());
        int yDiff = (int) (wall.getY() - myPosition.getY());

        if (yDiff == 0) {
            if (xDiff < 0) {
                xDiff--;
            } else {
                xDiff++;
            }
        }
        if (xDiff == 0) {
            if (yDiff < 0) {
                yDiff--;
            } else {
                yDiff++;
            }
        }

        Point otherSideOfWall = new Point((int) myPosition.getX(), (int) myPosition.getY());
        otherSideOfWall.translate(xDiff, yDiff);
        return otherSideOfWall;
    }

    public ArrayList<Point> removePointsNotInMaze(ArrayList<Point> points) {
        ArrayList<Point> pointsInMaze = new ArrayList<>();
        for (Point point : points) {
            if (isInsideMaze(point)) {
                pointsInMaze.add(point);
            }
        }
        return pointsInMaze;
    }

    public ArrayList<Point> getNSEWPointsFrom(Point myPosition) {
        ArrayList<Point> cardinalDirection = new ArrayList<>();
        cardinalDirection.add(clonePoint(myPosition));
        cardinalDirection.add(clonePoint(myPosition));
        cardinalDirection.add(clonePoint(myPosition));
        cardinalDirection.add(clonePoint(myPosition));
        cardinalDirection.get(0).translate(0, 1);
        cardinalDirection.get(1).translate(0, -1);
        cardinalDirection.get(2).translate(1, 0);
        cardinalDirection.get(3).translate(-1, 0);
        return cardinalDirection;
    }

    private Point clonePoint(Point myPosition) {
        return new Point((int) myPosition.getX(), (int) myPosition.getY());
    }

    private boolean haveNotVisited(Point myPosition) {
        return visitedStack.search(myPosition) == -1;
    }

    private boolean isEven(int num) {
        return num % 2 == 0;
    }

    private void buildBaseMap() {
        int firstRow = 0;
        int rowNum = 0;

        for (boolean[] row : WALL_MAP) {
            if (rowNum == firstRow) {
                buildHorizontalWallAt(firstRow);
                rowNum++;
            } else if (rowNum == LAST_ROW) {
                buildHorizontalWallAt(LAST_ROW);
                rowNum++;
            } else {
                WALL_MAP[rowNum][0] = true;
                WALL_MAP[rowNum][LAST_COL] = true;
                int startingCol = 0;
                if (!isEven(rowNum)) {
                    startingCol = 2;
                    buildWallAtEveryOtherCellAt(rowNum, startingCol);
                } else {
                    buildHorizontalWallAt(rowNum);
                }
                rowNum++;
            }
        }
    }

    private void buildWallAtEveryOtherCellAt(int rowNum, int startingCol) {
        int lastCol = MAX_COL - 1;
        for (int colNum = startingCol; colNum < lastCol; colNum += 2) {
            WALL_MAP[rowNum][colNum] = true;
        }
    }

    private void buildHorizontalWallAt(int rowNum) {
        int colNum = 0;
        for (boolean col : WALL_MAP[rowNum]) {
            WALL_MAP[rowNum][colNum] = true;
            colNum++;
        }
    }

    public void debugPrintMaze() {
        int rowNum = 0;
        for (boolean[] row : WALL_MAP) {
            int colNum = 0;
            for (boolean cell : row) {
                if (row[colNum]) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
                colNum++;
            }
            System.out.println("");
            rowNum++;
        }
    }

    private void clearCorners() {
        WALL_MAP[1][1] = false;
        WALL_MAP[1][2] = false;
        WALL_MAP[2][1] = false;

        WALL_MAP[MAX_ROW - 2][MAX_COL - 2] = false;

        WALL_MAP[1][MAX_COL - 2] = false;

        WALL_MAP[MAX_ROW - 2][1] = false;

    }

    public boolean isWallAt(Point point) {
        int rowNum = (int) point.getY();
        int colNum = (int) point.getX();
        return WALL_MAP[rowNum][colNum];
    }

    public boolean isWallAt(int colNum, int rowNum) {
        return WALL_MAP[rowNum][colNum];
    }

    public int getMAX_COL() {
        return MAX_COL;
    }

    public int getMAX_ROW() {
        return MAX_ROW;
    }

    public boolean[][] getWALL_MAP() {
        return WALL_MAP;
    }
}