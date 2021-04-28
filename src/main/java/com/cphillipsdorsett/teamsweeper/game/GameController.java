package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.BundleManifest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/game")
public class GameController {

    GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/new-game")
    public ResponseEntity<Game> newGame(
        @RequestParam(value = "difficulty", defaultValue = "e") String difficulty,
        HttpSession session
    ) throws JsonProcessingException {
        // Make sure the difficulty param is one we accept
        if (!Set.of("e", "m", "h").contains(difficulty)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Game game = gameService.getGame(session.getId(), difficulty);

        return new ResponseEntity<>(game, HttpStatus.OK);
    }

}
