package com.pbw.sportsync.race;
import com.pbw.sportsync.RequiredRole;
import com.pbw.sportsync.activity.Activity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @RequiredRole("pengguna")
    public String getLeaderboard(@PathVariable int id, Model model) {
        List<Activity> leaderboard = raceRepository.findLeaderboardByRaceId(id);
        model.addAttribute("leaderboard", leaderboard);
        return "user/LeaderboardPage";
    }

    @GetMapping("/sportsync/user/races")
    @RequiredRole("pengguna")
    public String getUserRaces(Model model) {
        List<Race> ongoingRaces = raceRepository.findOngoingRaces();
        List<Race> pastRaces = raceRepository.findPastRaces();

        model.addAttribute("ongoingRaces", ongoingRaces);
        model.addAttribute("pastRaces", pastRaces);

        return "user/UserRacesPage";
    }

    @PostMapping("/sportsync/user/joinRace/{id}")
    @RequiredRole("pengguna")
    public String joinRace(@PathVariable int id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            redirectAttributes.addFlashAttribute("error", "User is not authenticated.");
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");
        boolean alreadyJoined = raceRepository.isUserInRace(id, username);

        if (alreadyJoined) {
            redirectAttributes.addFlashAttribute("error", "You are already registered for this race!");
        } else {
            raceRepository.joinRace(id, username);
            redirectAttributes.addFlashAttribute("success", "You have successfully joined the race!");
        }

        return "redirect:/sportsync/user/races";
    }

    @GetMapping("/sportsync/user/pastRace/{id}/leaderboard")
    @RequiredRole("pengguna")
    public String getPastRaceLeaderboard(@PathVariable int id, Model model) {
        List<Activity> leaderboard = raceRepository.findLeaderboardByRaceId(id);
        model.addAttribute("leaderboard", leaderboard);
        return "user/PastRaceLeaderboardPage";
    }
}