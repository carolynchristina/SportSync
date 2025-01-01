package com.pbw.sportsync.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    public User login(String email, String password){
        Optional<User> user = userRepository.findUser(email);
        
        if(user.isPresent()){ //user ditemukan
            if(passwordEncoder.matches(password, user.get().getPassword())){ //cek password
                return user.get(); //berhasil login
            }
        }
        return null; //gagal login
    }
}
