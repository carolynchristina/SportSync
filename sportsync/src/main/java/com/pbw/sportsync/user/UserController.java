package com.pbw.sportsync.user;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/sportsync/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

            List<WeekChartData> weekChartData = userRepository.getWeekChartData(nama);
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
    @GetMapping("/activities")
    public String activities(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        
        List<Activity> activityList = userRepository.findUserActivities(username);
        
        model.addAttribute("activityList", activityList);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        
        return "user/Activities"; 
    }

    @GetMapping("/addActivity")
    public String addActivity(HttpSession session, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String datetimeNow = LocalDateTime.now().format(formatter);
        model.addAttribute("datetimeNow", datetimeNow);
        
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        LocalDate dateNow = LocalDate.now();
        List<Race> joinedRaces = userRepository.findValidJoinedRaces(username, dateNow);

        model.addAttribute("joinedRaces", joinedRaces);
        return "user/AddActivity"; 
    }

    @PostMapping("/saveActivity")
    public String saveActivity(
            @Valid @ModelAttribute ActivityDTO activityDTO, 
            @RequestParam(required = false) MultipartFile fotoUpload,
            @RequestParam(required = false, defaultValue="-1") int idRace, 
            Model model, 
            BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct the highlighted errors.");
            return "user/AddActivity";
        }
        Activity activity = new Activity(
            activityDTO.getJudul(),
            activityDTO.getDeskripsi(),
            activityDTO.getTglWaktuMulai(),
            activityDTO.getJarakTempuh(),
            activityDTO.getDurasi(),
            activityDTO.getUsername(),
            activityDTO.getIdRace()
        );

        //handle foto
        if (fotoUpload != null && !fotoUpload.isEmpty()){
             try {
                byte[] fotoBytes = fotoUpload.getBytes();
                String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                activity.setFoto(fotoBase64);  
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        userRepository.saveActivity(activity);

        return "redirect:/user/activities";
    }

    @GetMapping("/analysis")
    public String analysis(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        
        List<WeekChartData> weekChartData = userRepository.getWeekChartData(username);
        List<MonthChartData> monthChartData = userRepository.getMonthChartData(username);
        List<YearChartData> yearChartData = userRepository.getYearChartData(username);
        
        //Sort weekChartData
        List<String> daysOfWeek = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
        Map<String, WeekChartData> weekDataMap = new HashMap<>();
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
        
        //Summary
        int weekTotalActivities = 0;
        int weekTotalDistance = 0;
        int weekAverageDistance = 0;
        
        int monthTotalActivities = 0;
        int monthTotalDistance = 0;
        int monthAverageDistance = 0;
        
        int yearTotalActivities = 0;
        int yearTotalDistance = 0;
        int yearAverageDistance = 0;
        
        for (WeekChartData data : weekChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                weekTotalActivities++;
                weekTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (weekTotalActivities > 0) {
            weekAverageDistance = weekTotalDistance / weekTotalActivities;
        }

        for (MonthChartData data : monthChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                monthTotalActivities++;
                monthTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (monthTotalActivities > 0) {
            monthAverageDistance = monthTotalDistance / monthTotalActivities;
        }
        
        for (YearChartData data : yearChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                yearTotalActivities++;
                yearTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (yearTotalActivities > 0) {
            yearAverageDistance = yearTotalDistance / yearTotalActivities;
        }
        
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("weekChartData", sortedWeekChartData);
        model.addAttribute("monthChartData", monthChartData);
        model.addAttribute("yearChartData", yearChartData);

        model.addAttribute("weekTotalActivities", weekTotalActivities);
        model.addAttribute("weekTotalDistance", weekTotalDistance);
        model.addAttribute("weekAverageDistance", weekAverageDistance);

        model.addAttribute("monthTotalActivities", monthTotalActivities);
        model.addAttribute("monthTotalDistance", monthTotalDistance);
        model.addAttribute("monthAverageDistance", monthAverageDistance);

        model.addAttribute("yearTotalActivities", yearTotalActivities);
        model.addAttribute("yearTotalDistance", yearTotalDistance);
        model.addAttribute("yearAverageDistance", yearAverageDistance);

        return "user/Analysis";
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