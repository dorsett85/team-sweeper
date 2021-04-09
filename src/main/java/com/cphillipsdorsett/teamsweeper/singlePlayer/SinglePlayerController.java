package com.cphillipsdorsett.teamsweeper.singlePlayer;

import com.cphillipsdorsett.teamsweeper.BundleManifest;
import com.cphillipsdorsett.teamsweeper.GameBoard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SinglePlayerController {

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/single-player/new-game")
    public ResponseEntity<GameBoard> newGame(SinglePlayerService singlePlayerService) {
        GameBoard board = singlePlayerService.createBoard("e");
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

}
