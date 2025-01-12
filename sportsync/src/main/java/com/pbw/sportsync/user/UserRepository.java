package com.pbw.sportsync.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();    
    List<User> findByKeyword(String keyword);
    List<User> pagination(int limit, int offset, String status, String keyword);
    int rowCount(String status, String keyword);
    boolean editStatus(String username, boolean status);
    boolean deleteUser(String username);
    Optional<User> findUser(String email);
    Optional<User> findByUsername(String username);
    void saveUser(String oldUsername, User user);
    void addUser(User user);
    void saveEncryptedPassword(User user);
    String lastActivityDate(String username);
    int countActivity(String username);
}
