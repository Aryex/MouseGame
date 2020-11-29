package ca.cmpt213.as4.maze_game.model;

import java.awt.*;
/*GameObject class represent generic game objects
* in the game. All generic GameObjects have a position
* in the maze, represented by Java Point.*/
public class GameObject {

    private Point position;
    private TYPE type;

    public GameObject(TYPE type, Point position){
        this.type = type;
        this.position = (Point) position.clone();
    }
    public GameObject(TYPE type,int colNum, int rowNum){
        this.type = type;
        this.position = new Point(colNum, rowNum);
    }

    public void setPosition(Point position) {
        this.position = position;
    }
    public void setPosition(int colNum, int rowNum){
        this.position = new Point(colNum, rowNum);
    }

    public Point getPosition() {
        return (Point) position.clone();
    }

    public TYPE getType() {
        return type;
    }

    public void translate(int x, int y) {
        position.translate(x,y);
    }

    @Override
    public String toString() {
        return "[ " + type + " at " + position + " ]";
    }

    public enum TYPE {
        Player, Cat, Cheese
    }
}
