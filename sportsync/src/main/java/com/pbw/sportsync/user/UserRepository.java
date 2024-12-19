package com.pbw.sportsync.user;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository {
    void saveActivity(Activity activity);
    List<Race> findOngoingJoinedRaces(String username, LocalDate dateNow);
    void submitToRace(Activity activity);
}
