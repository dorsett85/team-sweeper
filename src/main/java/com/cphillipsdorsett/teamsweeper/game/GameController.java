package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.Board;
import com.cphillipsdorsett.teamsweeper.BundleManifest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping("/game")
public class GameController {

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/new-game")
    public ResponseEntity<Board> newGame(
        @RequestParam(value = "difficulty", defaultValue = "e") String difficulty,
        GameService gameService
    ) {
        // Make sure the difficulty param is one we accept
        if (!Set.of("e", "m", "h").contains(difficulty)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // TODO check for existing game
        Board board = gameService.createBoard(difficulty);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

}
