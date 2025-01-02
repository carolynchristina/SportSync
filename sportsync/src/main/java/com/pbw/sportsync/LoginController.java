package com.pbw.sportsync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pbw.sportsync.user.UserService;
import com.pbw.sportsync.user.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/sportsync")
public class LoginController {
    
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String landingPage(Model model){
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
        model.addAttribute("email", "");
        return "LandingPage";
    }

    @GetMapping("/login")
    public String index(HttpSession session){
        if(session.getAttribute("username") != null){
            //redirect sesuai role
            if(session.getAttribute("role").equals("admin")){
                return "redirect:/sportsync/admin";
            }else{
                return "redirect:/sportsync/user";
            }
        }
        return "LoginPage";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
        HttpSession session, Model model){
            User user = userService.login(email, password);
            if(user != null){ //berhasil login, tambah di session
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRoles());
                session.setAttribute("email", user.getEmail());
                
                //redirect sesuai role
                if(user.getRoles().equals("admin")){
                    return "redirect:/sportsync/admin";
                }else{
                    return "redirect:/sportsync/user";
                }
            }else{ //gagal login
                model.addAttribute("error", "Invalid email/password");
                return "LoginPage";
            } 
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/sportsync";
    }
}
