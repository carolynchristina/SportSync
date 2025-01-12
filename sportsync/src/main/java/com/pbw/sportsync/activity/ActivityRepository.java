package com.pbw.sportsync.activity;

import java.util.List;

public interface ActivityRepository {
    void saveActivity(Activity activity);
    List<Activity> findUserActivities(String username);
}
