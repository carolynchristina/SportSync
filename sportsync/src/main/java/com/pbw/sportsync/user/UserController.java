package com.pbw.sportsync.user;
import com.pbw.sportsync.analysis.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/sportsync/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @GetMapping("")
    public String showDashboard(@RequestParam (defaultValue = "") String username,
                                HttpSession session,
                                Model model){
        if(session.getAttribute("username") != null){
            return "redirect:/sportsync/user/dashboard";
        }
        else{
            return "redirect:/sportsync/login";
        }
    }
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){
            
            String nama = (String)session.getAttribute("username");
            String role = (String)session.getAttribute("role");
            String email = (String) session.getAttribute("email");

            List<WeekChartData> weekChartData = analysisRepository.getWeekChartData(nama);
            Map<String, WeekChartData> weekDataMap = new HashMap<>();
            List<String> daysOfWeek = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
            for (WeekChartData data : weekChartData) {
                weekDataMap.put(data.getHari(), data);
            }
            List<WeekChartData> sortedWeekChartData = new ArrayList<>();
            for (String day : daysOfWeek) {
                WeekChartData data = weekDataMap.get(day);
                if (data != null) {
                    sortedWeekChartData.add(data);
                } else {
                    sortedWeekChartData.add(new WeekChartData(day, 0));
        
                }
            }

            int weekTotalDistance = 0;

            for (WeekChartData data : weekChartData) {
                if (data.getTotalJarakTempuh() > 0) {
                    weekTotalDistance += data.getTotalJarakTempuh();
                }
            }

            model.addAttribute("username", nama);
            model.addAttribute("role", role);
            model.addAttribute("email", email);

            model.addAttribute("weekChartData", sortedWeekChartData);
            model.addAttribute("weekTotalDistance", weekTotalDistance);
            return "user/Dashboard";
    }

    @PostMapping("logout")
    public String logout (HttpSession session) {
        session.invalidate();
        return "redirect:/sportsync";
    }

    @GetMapping("/editProfile")
    public String editProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        return "user/EditProfile";
    }

    @PostMapping("/editProfile")
    public String saveEditProfile(HttpSession session, 
                                  @RequestParam("username") String username,
                                  @RequestParam("email") String email,
                                  @RequestParam("currPassword") String currPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confPassword") String confPassword,
                                  Model model) {

        String oldUsername = (String) session.getAttribute("username");
        String oldEmail = (String) session.getAttribute("email");

        model.addAttribute("username", oldUsername);
        model.addAttribute("email", oldEmail);

        if (userRepository.findByUsername(oldUsername).isEmpty()) {
            model.addAttribute("error", "User not found");
            return "user/EditProfile";
        }

        User user = userRepository.findByUsername(oldUsername).get();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user == null || !passwordEncoder.matches(currPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "user/EditProfile";
        } 

        if (!username.equals(oldUsername) && userRepository.findByUsername(username).isPresent()){
            model.addAttribute("error", "New username is already taken");
            return "user/EditProfile";
        }

        if (!email.equals(oldEmail) && userRepository.findUser(email).isPresent()){
            model.addAttribute("error", "New email is already taken");
            return "user/EditProfile";
        }

        user.setUsername(username);
        user.setEmail(email);

        if (!newPassword.isEmpty()) {
            if (!newPassword.equals(confPassword)) {
                model.addAttribute("error", "New password and confirmation password do not match");
                return "user/EditProfile";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.saveUser(oldUsername, user);

        session.setAttribute("username", username);
        session.setAttribute("email", email);

        return "redirect:/sportsync/user";
    }
}