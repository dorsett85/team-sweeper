package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.Board;
import com.cphillipsdorsett.teamsweeper.BundleManifest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    @GetMapping("/game/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/game/new-game")
    public ResponseEntity<Board> newGame(
        @RequestParam(value = "difficulty", defaultValue = "e") String difficulty,
        GameService gameService
    ) {
        // Make sure the difficulty param is one we accept
        // TODO simplify logic check for the difficulty param
        if (!difficulty.equals("e") && !difficulty.equals("m") && !difficulty.equals("h")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // TODO check for existing game
        Board board = gameService.createBoard(difficulty);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

}
