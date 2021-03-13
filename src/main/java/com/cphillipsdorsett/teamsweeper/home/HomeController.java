package com.cphillipsdorsett.teamsweeper.home;

import com.cphillipsdorsett.teamsweeper.BundleManifest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(BundleManifest bundleManifest, Model model) {
        model.addAttribute("bundleManifest", bundleManifest);
        return "index";
    }

}
