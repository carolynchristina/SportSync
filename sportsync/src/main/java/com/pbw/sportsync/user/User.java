package com.pbw.sportsync.user;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class User {
    private String username;
    private String email;
    private String password;
    private String role;
}
