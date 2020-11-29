package ca.cmpt213.as4.restapi.wrappers;

import ca.cmpt213.as4.maze_game.controller.GameController;

import java.util.Arrays;
import java.util.List;

public class ApiBoardWrapper {
    public int boardWidth;
    public int boardHeight;
    public ApiLocationWrapper mouseLocation;
    public ApiLocationWrapper cheeseLocation;
    public List<ApiLocationWrapper> catLocations;
    public boolean[][] hasWalls;
    public boolean[][] isVisible;

    // MAY NEED TO CHANGE PARAMETERS HERE TO SUITE YOUR PROJECT
    public static ApiBoardWrapper makeFromGame(GameController game) {
        ApiBoardWrapper wrapper = new ApiBoardWrapper();

        wrapper.boardWidth = game.getMAX_COL();
        wrapper.boardHeight = game.getMAX_ROW();

        wrapper.mouseLocation = ApiLocationWrapper.makeFromCellLocation(game.getPlayerPosition());
        wrapper.cheeseLocation = ApiLocationWrapper.makeFromCellLocation(game.getCheesePosition());
        wrapper.catLocations = ApiLocationWrapper.makeFromCellLocations(game.getCatPositions());

        wrapper.hasWalls = game.getWallMap();
        wrapper.isVisible = game.getFogMap();

        System.out.println("DEBUG: " + wrapper);
        return wrapper;
    }

    @Override
    public String toString() {
        return "ApiBoardWrapper{" +
                "boardWidth=" + boardWidth +
                ", boardHeight=" + boardHeight +
                ", mouseLocation=" + mouseLocation +
                ", cheeseLocation=" + cheeseLocation +
                ", catLocations=" + catLocations +
                '}';
    }
}