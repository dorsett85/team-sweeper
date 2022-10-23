package com.cphillipsdorsett.teamsweeper.game;

import com.cphillipsdorsett.teamsweeper.BundleManifest;

import com.cphillipsdorsett.teamsweeper.game.dto.SessionGameStatsResponseDto;
import com.cphillipsdorsett.teamsweeper.game.websocket.GameSocketSessionStore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/game")
public class GameController {
    GameService gameService;
    GameSocketSessionStore gameSocketSessionStore;

    public GameController(GameService gameService, GameSocketSessionStore gameSocketSessionStore) {
        this.gameService = gameService;
        this.gameSocketSessionStore = gameSocketSessionStore;
    }

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }

    @GetMapping("/session-stats")
    public ResponseEntity<SessionGameStatsResponseDto> getSessionGameStats(HttpSession session) {
        SessionGameStatsResponseDto sessionGameStats = gameService.findSessionGameStats(session.getId());
        return ResponseEntity.ok(sessionGameStats);
    }

}
