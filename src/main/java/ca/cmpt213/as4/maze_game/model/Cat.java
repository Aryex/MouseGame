package ca.cmpt213.as4.maze_game.model;

import java.awt.*;

/*Cat class represent cats in the maze.
* Cats are GameObjects, and can remember
* its previous position. */

public class Cat extends GameObject {
    private Point previousPosition;
    private int prevColNum;
    private int prevRowNum;

    public Cat(TYPE type, int colNum, int rowNum) {
        super(type, new Point(colNum,rowNum));
        previousPosition = super.getPosition();
    }

    public Point getLastTurnPosition() {
        return new Point((int) previousPosition.getX(), (int) previousPosition.getY());
    }

    public void moveTo(Point newPoint) {
        super.setPosition(newPoint);
    }
    public void rememberCurrentPosition(){
        this.previousPosition = super.getPosition();
    }
}
