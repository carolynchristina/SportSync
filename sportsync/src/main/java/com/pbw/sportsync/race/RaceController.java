package com.pbw.sportsync.race;
import com.pbw.sportsync.activity.Activity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
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

        return "RacePage";
    }

    @GetMapping("/sportsync/races/{id}/leaderboard")
    public String getLeaderboard(@PathVariable int id, Model model) {
        List<Activity> leaderboard = raceRepository.findLeaderboardByRaceId(id);
        model.addAttribute("leaderboard", leaderboard);
        return "user/LeaderboardPage";
    }

    @GetMapping("/sportsync/user/races")
    public String getUserRaces(Model model) {
        List<Race> ongoingRaces = raceRepository.findOngoingRaces();
        List<Race> pastRaces = raceRepository.findPastRaces();

        model.addAttribute("ongoingRaces", ongoingRaces);
        model.addAttribute("pastRaces", pastRaces);

        return "user/UserRacesPage";
    }

    @GetMapping("/sportsync/user/joinRace/{id}")
    public String joinRace(@PathVariable int id, Principal principal) {
        String username = principal.getName();
        raceRepository.joinRace(id, username);
        return "redirect:/sportsync/user/races";
    }

    @GetMapping("/sportsync/user/pastRace/{id}/leaderboard")
    public String getPastRaceLeaderboard(@PathVariable int id, Model model) {
        List<Activity> leaderboard = raceRepository.findLeaderboardByRaceId(id);
        model.addAttribute("leaderboard", leaderboard);
        return "user/PastRaceLeaderboardPage";
    }
}