package com.pbw.sportsync.admin;

import java.util.List;
import com.pbw.sportsync.user.UserRepository;
import com.pbw.sportsync.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepository repo;

    @GetMapping("/listMembers")
    public String listMembers(@RequestParam(defaultValue="") String filter,
        @RequestParam(defaultValue="0") int page,
        Model model){
            List<User> user;
            if(page == 0){ //tanpa pagination
                user = this.repo.findByKeyword(filter);
            }else{ //dengan pagination
                int limit = 8;
                int offset = (page - 1) * limit;
                int rowCount = this.repo.rowCount();
                user = this.repo.pagination(limit, offset);
                int pageCount = (int) Math.ceil((double) rowCount / limit);
    
                model.addAttribute("currentPage", page);
                model.addAttribute("pageCount", pageCount);
            }
            model.addAttribute("users", user);
            model.addAttribute("filter", filter);

            return "admin/listMembers";
    }

    @GetMapping("/addRace")
    public String addRace(){
        return "admin/addRace";
    }
}
