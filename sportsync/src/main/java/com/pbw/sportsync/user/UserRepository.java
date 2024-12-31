package com.pbw.sportsync.user;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository {
    void saveActivity(Activity activity);
    List<Race> findValidJoinedRaces(String username, LocalDate dateNow);
    List<Activity> findUserActivities(String username);
    List<User> findAll();    
    List<User> findByKeyword(String keyword);
    List<User> pagination(int limit, int offset);
    int rowCount();
    List<WeekChartData> getWeekChartData(String username);
    List<MonthChartData> getMonthChartData(String username);
    List<YearChartData> getYearChartData(String username);
}
