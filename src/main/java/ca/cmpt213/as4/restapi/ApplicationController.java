package ca.cmpt213.as4.restapi;


import ca.cmpt213.as4.maze_game.controller.GameController;
import ca.cmpt213.as4.restapi.wrappers.ApiBoardWrapper;
import ca.cmpt213.as4.restapi.wrappers.ApiGameWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ApplicationController {
    private static int count = 0;
    private static List<ApiGameWrapper> apiGameWrappers = new ArrayList<>();
    private static List<GameController> games = new ArrayList<>();

    @GetMapping("api/about")
    public String getAbout() {
        return "Minh Le - 301 325 697";
    }

    @GetMapping("api/games")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<ApiGameWrapper> getAllGames() {
        System.out.println("DEBUG: \"api/games\" ");
        return apiGameWrappers;
    }

    @PostMapping("api/games")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiGameWrapper makeNewGame() {
        int id = count;
        count++;
        GameController game = new GameController(15, 20, 3, 5);
        games.add(game);
        ApiGameWrapper apiGameWrapper = ApiGameWrapper.makeFromGame(game, id);
        apiGameWrappers.add(apiGameWrapper);
        return apiGameWrapper;
    }

    @GetMapping("api/games/{id}")
    public ApiGameWrapper getGame(@PathVariable("id") long id) {
        if ((int) id >= count) {
            System.out.println("throw");
            throw new NotFound();
        } else {
            return apiGameWrappers.get((int) id);
        }
    }

    @GetMapping("api/games/{id}/board")
    public ApiBoardWrapper getGameBoard(@PathVariable("id") long id) {
        int gameID = (int) id;
        if (gameID >= count) {
            throw new NotFound();
        } else {
            return ApiBoardWrapper.makeFromGame(games.get(gameID));
        }

    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("api/games/{id}/moves")
    public void gameMove(@PathVariable("id") long id,
                         @RequestBody String input) {
        int gameID = (int) id;
        if (gameID >= count) {
            System.out.println("throw");
            throw new NotFound();
        } else {
            GameController game = games.get(gameID);
            switch (input.toLowerCase()) {
                case "move_up":
                    game.run("w");
                    break;
                case "move_down":
                    game.run("s");
                    break;
                case "move_left":
                    game.run("a");
                    break;
                case "move_right":
                    game.run("d");
                    break;
                case "move_cats":
                    game.catsActions();
                    break;
                default:
                    throw new BadRequest();
            }
            apiGameWrappers.get(gameID).update(game);
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("api/games/{id}/cheatstate")
    public void cheats(@PathVariable("id") long id,
                       @RequestBody String cheat) {
        int gameID = (int) id;
        if (gameID >= count) {
            System.out.println("throw");
            throw new NotFound();
        } else {
            GameController game = games.get(gameID);
            switch (cheat.toLowerCase()) {
                case "1_cheese":
                    game.debugMaxScore();
                    break;
                case "show_all":
                    game.revealAll();
                    break;
                default:
                    throw new BadRequest();
            }
            apiGameWrappers.get(gameID).update(game);
        }
    }

    //Exception Handlers
    @ResponseStatus(value = HttpStatus.NOT_FOUND,
            reason = "Game ID not found.")
    static class NotFound extends RuntimeException{
        NotFound(){}
        NotFound(String msg){
            super(msg);
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST,
            reason = "Invalid player move")
    static class BadRequest extends RuntimeException{
        BadRequest(){}
        BadRequest(String msg){
            super(msg);
        }
    }


}
