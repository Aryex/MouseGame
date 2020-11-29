package ca.cmpt213.as4.maze_game.controller;

import ca.cmpt213.as4.maze_game.model.Cat;
import ca.cmpt213.as4.maze_game.model.GameObject;
import ca.cmpt213.as4.maze_game.model.Maze;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * GameController class controls the game.
 * All inputs are done passed through run(input).
 * When run is called, player move first then cats.
 *
 * Player: player moves by movePlayer(input).
 * Game state is updated after each movement.
 * Map is revealed around players after each move.
 *
 * Cheese: location is generated randomly to be not
 *   on top of player.
 * Cheese respawn when eaten.
 *
 * Cats: cats move forward randomly.
 * If cat moves into player, game is over.
 * At a dead end, cat will "turn around".
 */
public class GameController {
    private Maze MAZE;
    private boolean[][] revealed; // fog.

    private final GameObject PLAYER;
    private final Point PLAYER_STARTING_POS = new Point(1, 1);
    private final Cat[] CATS;
    private final GameObject CHEESE;

    private ArrayList<Status> statusQueue = new ArrayList<>();
    private Status gameStatus = Status.Alive;

    private final int MAX_COL;
    private final int MAX_ROW;
    private int maxScore = 0;
    private int score = 0;
    private boolean catDisabled = false;

    public GameController(int mazeRows, int mazeCols, int catNumber, int cheeseCount) {
        this.MAX_ROW = mazeRows;
        this.MAX_COL = mazeCols;
        this.maxScore = cheeseCount;
        statusQueue.add(Status.Alive);

        MAZE = new Maze(mazeRows, mazeCols);
        revealed = new boolean[mazeRows][mazeCols];
        PLAYER = new GameObject(GameObject.TYPE.Player, PLAYER_STARTING_POS);
        removeFogAroundPlayer();

        CATS = new Cat[]{
                new Cat(GameObject.TYPE.Cat, MAX_COL - 2, MAX_ROW - 2),
                new Cat(GameObject.TYPE.Cat, MAX_COL - 2, 1),
                new Cat(GameObject.TYPE.Cat, 1, MAX_ROW - 2)
        };
        CHEESE = new GameObject(GameObject.TYPE.Cheese, getRandomPointNotWallNotPlayer());
    }

    public void run(String input) {
        statusQueue.clear();
        playerAction(input);
        if (catDisabled) {
            return;
        }
        //catsActions();
    }

    private boolean hasCatAt(Point target) {
        for (GameObject cat : CATS) {
            if (target.equals(cat.getPosition())) {
                return true;
            }
        }
        return false;
    }

    public Cell getCell(int rowNum, int colNum) {
        Point point = new Point(colNum, rowNum);
        if (isBorder(point)) {
            return Cell.WALL;
        }
        if (point.equals(PLAYER.getPosition())) {
            return Cell.PLAYER;
        }
        for (GameObject cat : CATS) {
            if (point.equals(cat.getPosition())
                    && point.equals(CHEESE.getPosition())) {
                return Cell.CHEESE_CAT;
            }
            if (point.equals(cat.getPosition())) {
                return Cell.CAT;
            }
        }
        if (point.equals(CHEESE.getPosition())) {
            return Cell.CHEESE;
        }
        if (!revealed[(int) point.getY()][(int) point.getX()]) {
            return Cell.FOG;
        }
        if (MAZE.isWallAt(colNum, rowNum)) {
            return Cell.WALL;
        } else {
            return Cell.PATH;
        }
    }

    private boolean isBorder(Point point) {
        return point.getX() == 0 ||
                point.getY() == 0 ||
                point.getY() == MAX_ROW - 1 ||
                point.getX() == MAX_COL - 1;
    }

    public void catsActions() {
        for (Cat cat : CATS) {
            Point currentCatPosition = cat.getPosition();
            Point WhereCatGoingToBe;

     /*    Cat's previous position will always be behind it.
           Cat previous position represent a direction it can't go into.*/
            if (catAtDeadEnd(cat)) {
//              At dead end, remembering current position first before looking for new direction.
                cat.rememberCurrentPosition();

                do {
                    WhereCatGoingToBe = getRandPointNotWallNear(currentCatPosition);
                } while (WhereCatGoingToBe.equals(cat.getLastTurnPosition()));

                cat.moveTo(WhereCatGoingToBe);
            } else {

                do {
                    WhereCatGoingToBe = getRandPointNotWallNear(currentCatPosition);
                } while (WhereCatGoingToBe.equals(cat.getLastTurnPosition()));

                cat.rememberCurrentPosition();
                cat.moveTo(WhereCatGoingToBe);
            }
            if (cat.getPosition().equals(PLAYER.getPosition())) {
                gameStatus = Status.GameOver;
            }
        }

    }

    private boolean catAtDeadEnd(Cat cat) {
        int wallCount = 0;
        Point catCurrentPosition = cat.getPosition();
        int colNum = (int) catCurrentPosition.getX();
        int rowNum = (int) catCurrentPosition.getY();
        if (MAZE.isWallAt(colNum + 1, rowNum)) {
            wallCount++;
        }
        if (MAZE.isWallAt(colNum - 1, rowNum)) {
            wallCount++;
        }
        if (MAZE.isWallAt(colNum, rowNum + 1)) {
            wallCount++;
        }
        if (MAZE.isWallAt(colNum, rowNum - 1)) {
            wallCount++;
        }
        return wallCount > 2;
    }

    private Point getRandPointNotWallNear(Point currentCatPosition) {
        Random random = new Random();
        Point point;

        do {
            int colNum = 0;
            int rowNum = 0;
            point = new Point((int) currentCatPosition.getX(), (int) currentCatPosition.getY());

            boolean moveVertical = random.nextBoolean();
            if (moveVertical) {
                boolean positive = random.nextBoolean();
                if (positive) {
                    colNum = 1;
                } else {
                    colNum = -1;
                }
            } else {
                boolean positive = random.nextBoolean();
                if (positive) {
                    rowNum = 1;
                } else {
                    rowNum = -1;
                }
            }
            point.translate(colNum, rowNum);
        }
        while (MAZE.isWallAt(point));

        return point;
    }

    public void playerAction(String input) {
        int rowNum = 0;
        int colNum = 0;

        switch (input.toLowerCase()) {
            case "w":
                rowNum = -1;
                movePlayer(colNum, rowNum);
                break;
            case "a":
                colNum = -1;
                movePlayer(colNum, rowNum);
                break;
            case "s":
                rowNum = 1;
                movePlayer(colNum, rowNum);
                break;
            case "d":
                colNum = 1;
                movePlayer(colNum, rowNum);
                break;
            default:
        }
    }

    private void movePlayer(int colNum, int rowNum) {
        Point target = PLAYER.getPosition();
        target.translate(colNum, rowNum);

        if (hasWallAt(target)) {
            gameStatus = Status.Blocked;
        } else {
            PLAYER.translate(colNum, rowNum);
            removeFogAroundPlayer();
            if (hitCheese(target)) {
                score++;
                CHEESE.setPosition(getRandomPointNotWallNotPlayer());
            }
            if (winConditionReached()) {
                gameStatus = Status.Win;
            } else if (hasCatAt(target)) {
                gameStatus = Status.GameOver;
            } else {
                gameStatus = Status.Alive;
            }
        }
    }

    private boolean hitCheese(Point point) {
        return point.equals(CHEESE.getPosition());
    }

    private Point getRandomPointNotWallNotPlayer() {
        Random random = new Random();
        int randomRow = random.nextInt(MAX_ROW - 2) + 1;
        int randomCol = random.nextInt(MAX_COL - 2) + 1;
        Point randPoint = new Point(randomCol, randomRow);
        randPoint = moveOutOfWall(randPoint);
        return randPoint;
    }

    private Point moveOutOfWall(Point point) {
        if (point.equals(PLAYER.getPosition()) || MAZE.isWallAt(point)) {
            int row = (int) point.getY();
            for (int col = 0; col < MAX_COL; col++) {
                Point newPoint = new Point(col, row);
                if (!point.equals(PLAYER.getPosition()) && !MAZE.isWallAt(newPoint)) {
                    return newPoint;
                }
            }
        }
        return point;
    }

    public void revealAll() {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                revealed[row][col] = true;
            }
        }
    }

    private void printFOG() {
        for (boolean[] row : revealed) {
            for (boolean cell : row) {
                if (cell) {
                    System.out.printf("T");
                } else {
                    System.out.printf("f");
                }

            }
            System.out.println("");
        }
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void debugMaxScore() {
        maxScore = 1;
    }

    public void resetCheese() {
        CHEESE.setPosition(getRandomPointNotWallNotPlayer());
    }

    public void disableCat() {
        catDisabled = !catDisabled;
    }

    private boolean winConditionReached() {
        return score == maxScore;
    }

    private boolean hasWallAt(Point target) {
        return MAZE.isWallAt(target);
    }

    private void removeFogAroundPlayer() {
        Point currentPlayerPosition = PLAYER.getPosition();
        int playerRowNum = (int) currentPlayerPosition.getY();
        int playerColNum = (int) currentPlayerPosition.getX();


        revealed[playerRowNum][playerColNum] = true;

        revealed[playerRowNum + 1][playerColNum] = true;
        revealed[playerRowNum + 1][playerColNum + 1] = true;
        revealed[playerRowNum + 1][playerColNum - 1] = true;

        revealed[playerRowNum - 1][playerColNum] = true;
        revealed[playerRowNum - 1][playerColNum + 1] = true;
        revealed[playerRowNum - 1][playerColNum - 1] = true;

        revealed[playerRowNum][playerColNum - 1] = true;
        revealed[playerRowNum][playerColNum + 1] = true;
    }

    public void genNewMaze() {
        MAZE = new Maze(MAX_ROW, MAX_COL);
    }

    public int getMAX_COL() {
        return MAX_COL;
    }

    public int getMAX_ROW() {
        return MAX_ROW;
    }

    public boolean[][] getWallMap() {
        return MAZE.getWALL_MAP();
    }

    public boolean[][] getFogMap() {
        return revealed;
    }

    public Point getPlayerPosition() {
        return PLAYER.getPosition();
    }

    public Point getCheesePosition() {
        return CHEESE.getPosition();
    }

    public Iterable<Point> getCatPositions() {
        ArrayList<Point> positions = new ArrayList<>();
        for(Cat cat : CATS){
            positions.add(cat.getPosition());
        }
        return positions;
    }

    public enum Cell {
        WALL, PATH, FOG, CAT, CHEESE, CHEESE_CAT, PLAYER;

    }


    public enum Status {
        Alive, Blocked, GameOver, Win;
    }




    /*private void moveCheese() {
        Random random = new Random();
        Point pointRandom = new Point(random.nextInt(mazeRows-2)+2, random.nextInt(mazeCols-2)+2);
        validateCheesePoint();
    }*/
}
