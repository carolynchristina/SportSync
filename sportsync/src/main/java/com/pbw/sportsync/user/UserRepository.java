package com.pbw.sportsync.user;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository {
    void saveActivity(Activity activity);
    List<Race> findValidJoinedRaces(String username, LocalDate dateNow);
    List<Activity> findUserActivities(String username);
    List<User> findAll();    
    List<User> findByKeyword(String keyword);
    List<User> pagination(int limit, int offset, String status, String keyword);
    int rowCount(String status, String keyword);
    boolean editStatus(String username, boolean status);
    boolean deleteUser(String username);
    List<User> findUser(String username, String password);
    List<User> findUserByName(String username);
}
