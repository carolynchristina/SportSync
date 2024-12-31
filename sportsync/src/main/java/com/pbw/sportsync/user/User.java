package com.pbw.sportsync.user;

import lombok.Data;

@Data
public class User {
    private final String username;
    private final String email;
    private final String password;
    private final String roles;
    private final String status;
}
