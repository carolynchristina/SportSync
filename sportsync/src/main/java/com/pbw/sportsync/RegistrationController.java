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
import com.pbw.sportsync.user.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/sportsync")
public class RegistrationController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/Register")
    public String showRegistration (@RequestParam String fullname,
                                    @RequestParam String email,
                                    @RequestParam String username,
                                    @RequestParam String password,
                                    Model model,
                                    HttpSession session){
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        user.setRoles("pengguna");
        user.setStatus("true");

        userRepository.addUser(user);

        return "redirect:/sportsync/login";
    }
}
