package com.pbw.sportsync.admin;

import java.util.List;
import java.util.ArrayList;
import com.pbw.sportsync.user.UserRepository;
import com.pbw.sportsync.user.User;
import com.pbw.sportsync.race.Race;
import com.pbw.sportsync.race.RaceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RaceRepository raceRepo;

    @GetMapping("/")
    public String listMembers(@RequestParam(defaultValue="1") int page,
        @RequestParam(defaultValue="") String keyword,
        @RequestParam(defaultValue="") String status,
        Model model){
            List<User> user;
            if(page == 0){ //tanpa pagination
                user = this.userRepo.findByKeyword(keyword);
                if(!status.equals("")){ //filter berdasarkan status member
                    List<User> filteredUsers = new ArrayList<>();
                    for(User u : user){
                        if(u.getStatus().equalsIgnoreCase(status)){
                            filteredUsers.add(u);
                        }
                    }
                    user = filteredUsers;
                }
            }else{ //dengan pagination
                int limit = 8;
                int offset = (page - 1) * limit;
                int rowCount = this.userRepo.rowCount(status, keyword);
                user = this.userRepo.pagination(limit, offset, status, keyword);
                int pageCount = (int) Math.ceil((double) rowCount / limit);
                
                model.addAttribute("currentPage", page);
                model.addAttribute("pageCount", pageCount);
                
            }
            model.addAttribute("status", status);
            model.addAttribute("users", user);
            model.addAttribute("keyword", keyword);

            return "admin/listMembers";
    }

    @GetMapping("/addRace")
    public String addRace(Model model){
        model.addAttribute("race", new Race());
        return "admin/addRace";
    }

    @PostMapping("/addRace/save")
    public String save(@Valid @ModelAttribute("race") Race race, BindingResult bindingResult, Model model){
        //cek validasi error
        if(bindingResult.hasErrors()){
            model.addAttribute("race", race);
            return "admin/addRace";
        }
        
        //cek validasi tglMulai, tglSelesai
        if(race.getTglMulai().isAfter(race.getTglSelesai())){
            model.addAttribute("race", race);
            model.addAttribute("dateError", "Start Date should be before End Date");
            return "admin/addRace";
        }
        
        this.raceRepo.addRace(race);
        model.addAttribute("race", race);
        model.addAttribute("success", "Race is registered");
        return "admin/addRace";
        
    }

    @PostMapping("/addRace/reset")
    public String reset(Model model){
        model.addAttribute("race", new Race());
        return "admin/addRace";
    }
}
