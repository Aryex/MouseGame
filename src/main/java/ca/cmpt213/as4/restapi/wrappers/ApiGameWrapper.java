package ca.cmpt213.as4.restapi.wrappers;

import ca.cmpt213.as4.maze_game.controller.GameController;

public class ApiGameWrapper {
    public int gameNumber;
    public boolean isGameWon;
    public boolean isGameLost;
    public int numCheeseFound;
    public int numCheeseGoal;

    // INIT-IALIZATION
    public static ApiGameWrapper makeFromGame(GameController game, int id) {
        ApiGameWrapper wrapper = new ApiGameWrapper();
        wrapper.gameNumber = id;
        wrapper.numCheeseFound = game.getScore();
        wrapper.numCheeseGoal = game.getMaxScore();
        GameController.Status gameStatus = game.getGameStatus();
        updateGameStatus(wrapper, gameStatus);
        return wrapper;
    }

    private static void updateGameStatus(ApiGameWrapper wrapper, GameController.Status gameStatus) {
        switch (gameStatus) {
            case Win:
                wrapper.isGameWon = true;
                break;
            case GameOver:
                wrapper.isGameLost = true;
                break;
            default:
                wrapper.isGameLost = false;
                wrapper.isGameWon = false;
        }
    }

    public void update(GameController game) {
        this.numCheeseFound = game.getScore();
        this.numCheeseGoal = game.getMaxScore();
        GameController.Status gameStatus = game.getGameStatus();
        updateGameStatus(this, gameStatus);
    }
}