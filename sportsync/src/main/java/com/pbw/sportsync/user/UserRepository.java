package com.pbw.sportsync.user;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository {
    void saveActivity(Activity activity);
    List<Race> findValidJoinedRaces(String username, LocalDate dateNow);
    List<Activity> findUserActivities(String username);
}
