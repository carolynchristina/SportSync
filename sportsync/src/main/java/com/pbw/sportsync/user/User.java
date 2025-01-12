package com.pbw.sportsync.user;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String username;
    private String email;
    private String password;
    private String roles;
    private String status;
}
