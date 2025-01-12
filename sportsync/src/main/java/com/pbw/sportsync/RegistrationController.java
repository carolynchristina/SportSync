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

    @GetMapping("/Register")
    public String showRegister(HttpSession session) {
        return "RegistrationPage";
    }

    @PostMapping("/Register")
    public String showRegistration(@RequestParam String fullname,
                                   @RequestParam String email,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String confpassword,
                                   Model model,
                                   HttpSession session) {

        int checkUsername = userRepository.usernameCheck(username);
        int checkEmail = userRepository.emailCheck(email);

        // Validasi input form
        if (checkUsername > 0) {
            model.addAttribute("error", "Username Already Used");
            return "RegistrationPage";
        }

        if (checkEmail > 0) {
            model.addAttribute("error", "Email Already Used");
            return "RegistrationPage";
        }

        if (password.length() > 8) {
            model.addAttribute("error", "Password is too long");
            return "RegistrationPage";
        }

        if (!password.equals(confpassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "RegistrationPage";
        }

        // Jika semua validasi berhasil
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);  // Pertimbangkan enkripsi password sebelum menyimpan
        user.setUsername(username);
        user.setRoles("pengguna");
        user.setStatus("Active");

        // Tambah user ke database
        userRepository.addUser(user);

        // Proses registrasi tambahan, misalnya enkripsi password
        userService.register(email, password);  // Pastikan ini menangani enkripsi password

        return "redirect:/sportsync/login";
    }
}
