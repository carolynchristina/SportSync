package com.pbw.sportsync.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    Optional<User> findUser(String email);
    List<WeekChartData> getWeekChartData(String username);
    List<MonthChartData> getMonthChartData(String username);
    List<YearChartData> getYearChartData(String username);
    Optional<User> findByUsername(String username);
    void saveUser(String oldUsername, User user);
    void addUser(User user);
    void saveEncryptedPassword(User user);
}
