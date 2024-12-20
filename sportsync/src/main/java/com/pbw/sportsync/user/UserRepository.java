package com.pbw.sportsync.user;

import java.util.List;

public interface UserRepository {
    List<User> findAll();    
    List<User> findByKeyword(String keyword);
    List<User> pagination(int limit, int offset);
    int rowCount();
}
