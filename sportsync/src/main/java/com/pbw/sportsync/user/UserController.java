package com.pbw.sportsync.user;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/activities")
    public String activities(HttpSession session, Model model) {
        //TEST
        session.setAttribute("username", "bobby");

        String username = (String) session.getAttribute("username");

        List<Activity> activityList = userRepository.findUserActivities(username);
        model.addAttribute("activityList", activityList);

        return "user/Activities"; 
    }

    @GetMapping("/addActivity")
    public String addActivity(HttpSession session, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String datetimeNow = LocalDateTime.now().format(formatter);
        model.addAttribute("datetimeNow", datetimeNow);
        
        //TEST
        session.setAttribute("username", "bobby");
        
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

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

}