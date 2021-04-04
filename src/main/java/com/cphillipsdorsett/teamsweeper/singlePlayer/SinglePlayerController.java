package com.cphillipsdorsett.teamsweeper.singlePlayer;

import com.cphillipsdorsett.teamsweeper.BundleManifest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SinglePlayerController {
    private final SinglePlayerService singlePlayerService;

    SinglePlayerController(SinglePlayerService singlePlayerService) {
        this.singlePlayerService = singlePlayerService;
    }

    @GetMapping("/single-player")
    public String singlePlayer(BundleManifest bundleManifest, Model model) {
        singlePlayerService.createBoard("1234");
        model.addAttribute("bundleManifest", bundleManifest);
        return "single-player";
    }
}
