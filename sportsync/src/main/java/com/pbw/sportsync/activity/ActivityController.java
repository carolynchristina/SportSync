package com.pbw.sportsync.activity;
import com.pbw.sportsync.RequiredRole;
import com.pbw.sportsync.race.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/sportsync/user")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RaceRepository raceRepository;
    
    @GetMapping("/activities")
    @RequiredRole("pengguna")
    public String activities(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        
        List<Activity> activityList = activityRepository.findUserActivities(username);
        
        model.addAttribute("activityList", activityList);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        
        return "user/Activities"; 
    }

    @GetMapping("/addActivity")
    @RequiredRole("pengguna")
    public String addActivity(HttpSession session, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String datetimeNow = LocalDateTime.now().format(formatter);
        model.addAttribute("datetimeNow", datetimeNow);
        
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        LocalDate dateNow = LocalDate.now();
        List<Race> joinedRaces = raceRepository.findValidJoinedRaces(username, dateNow);

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

        activityRepository.saveActivity(activity);

        return "redirect:/user/activities";
    }
}
