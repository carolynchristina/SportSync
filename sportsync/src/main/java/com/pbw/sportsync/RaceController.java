package com.pbw.sportsync;

import com.pbw.sportsync.race.RaceRepository;
import com.pbw.sportsync.race.Race;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class RaceController {
    private final RaceRepository raceRepository;

    public RaceController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping("/sportsync/races")
    public String getRaces(Model model) {
        List<Race> ongoingRaces = raceRepository.findOngoingRaces();
        List<Race> pastRaces = raceRepository.findPastRaces();

        model.addAttribute("ongoingRaces", ongoingRaces);
        model.addAttribute("pastRaces", pastRaces);

        return "RacesPage";
    }

    @GetMapping("/sportsync/user/races")
    public String getUserRaces(Model model) {
        List<Race> ongoingRaces = raceRepository.findOngoingRaces();
        List<Race> pastRaces = raceRepository.findPastRaces();

        model.addAttribute("ongoingRaces", ongoingRaces);
        model.addAttribute("pastRaces", pastRaces);
        model.addAttribute("userLoggedIn", true);

        return "UserRacesPage";
    }
}