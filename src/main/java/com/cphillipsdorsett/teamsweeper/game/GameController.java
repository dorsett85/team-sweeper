package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.BundleManifest;

import com.cphillipsdorsett.teamsweeper.game.dao.GameDifficulty;
import com.cphillipsdorsett.teamsweeper.game.dto.GameStartResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

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
    public ResponseEntity<GameStartResponseDto> newGame(
        @RequestParam(value = "difficulty", defaultValue = "e") String difficulty,
        HttpSession session
    ) throws JsonProcessingException {
        // Make sure the difficulty param is one we accept
        GameDifficulty gameDifficulty = GameDifficulty
            .getNameByValue(difficulty)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "difficulty param must be (e|m|h)"));

        GameStartResponseDto gameStartDto = gameService.newGame(session.getId(), gameDifficulty);

        return ResponseEntity.ok(gameStartDto);
    }

    @GetMapping("/session-stats")
    public ResponseEntity<Object> getSessionGameStats(HttpSession session) {
        Object sessionGameStats = gameService.findSessionGameStats(session.getId());
        return ResponseEntity.ok(sessionGameStats);
    }

}
