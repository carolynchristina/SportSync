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

    @GetMapping("/Login")
    public String showLogin(Model model){
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
        model.addAttribute("email", "");
        return "LoginPage"; 
    }

    @PostMapping("/Login")
    public String login(@RequestParam ("email") String Email,@RequestParam ("password") String Password,Model model, HttpSession session){
        List<User> users = userRepository.findUser(Email, Password);
        if (users.size()==1) {
            User user = users.get(0);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("password", user.getPassword());
            session.setAttribute("role", user.getRole());
            return "redirect:/dashbord";
        }
        else{
            model.addAttribute("Error", "Email Atau Password Salah!");
            model.addAttribute("email", Email);
            return "LoginPage";
        }
    }
    @GetMapping("/Dashboard")
    public String showDashboard(@RequestParam (defaultValue = "") String username, HttpSession session, Model model){
        if(session.getAttribute("username") != null){
            String nama = (String)session.getAttribute("username");
            String role = (String)session.getAttribute("role");

            model.addAttribute("username", nama);
            model.addAttribute("role", role);

            if(model.getAttribute("role").equals("admin")){
                List<User> Allusers = userRepository.findUserByName(username);
                model.addAttribute("users",Allusers);
                return "redirect:/Dashboard/admin";
            }
            else{
                List<User> Allusers = userRepository.findUserByName(username);
                model.addAttribute("users",Allusers);
                return "redirect:/Dashboard/user";
            }
        }
        else{
            return "redirect:/user";
        }
    }
    @GetMapping("/addActivity")
    public String addActivity(HttpSession session, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String datetimeNow = LocalDateTime.now().format(formatter);
        model.addAttribute("datetimeNow", datetimeNow);

        String username = (String) session.getAttribute("username");
        LocalDate dateNow = LocalDate.now();
        List<Race> joinedRaces = userRepository.findOngoingJoinedRaces(username, dateNow);

        model.addAttribute("joinedRaces", joinedRaces);
        return "user/add_activity"; 
    }

    @PostMapping("/addActivity")
    public String saveActivity(
            @Valid @ModelAttribute ActivityDTO activityDTO, 
            @RequestParam(required = false) MultipartFile foto,
            @RequestParam(required = false, defaultValue="-1") int id, //id race
            Model model, 
            BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct the highlighted errors.");
            return "user/add_activity";
        }
        Activity activity = new Activity(
            activityDTO.getJudul(),
            activityDTO.getDeskripsi(),
            activityDTO.getTglWaktuMulai(),
            activityDTO.getJarakTempuh(),
            activityDTO.getDurasi(),
            activityDTO.getUsername()
        );

        //handle foto
        if (foto != null && !foto.isEmpty()){
             try {
                byte[] fotoBytes = foto.getBytes();
                String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
                activity.setFoto(fotoBase64);  
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //handle race
        if(id > -1){
            activity.setIdRace(id);
            userRepository.submitToRace(activity);
        } 
        // Save or process the `activity` object
        userRepository.saveActivity(activity); // Save to the database

        return "redirect:/member/activities";
        
    }

}