package com.cphillipsdorsett.teamsweeper.singlePlayer;

import com.cphillipsdorsett.teamsweeper.BundleManifest;
import com.cphillipsdorsett.teamsweeper.GameBoard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SinglePlayerController {

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/single-player/new-game")
    public ResponseEntity<GameBoard> newGame(
        @RequestParam(value = "difficulty", defaultValue = "e") String difficulty, SinglePlayerService singlePlayerService
    ) {
        GameBoard board = singlePlayerService.createBoard(difficulty);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

}
