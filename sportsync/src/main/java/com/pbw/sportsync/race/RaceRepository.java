package com.pbw.sportsync.race;

import java.util.List;

import com.pbw.sportsync.user.Activity;

public interface RaceRepository {
    void addRace(Race race);
    List<Race> findOngoingRaces();
    List<Race> findPastRaces();
    List<Activity> findLeaderboardByRaceId(int raceId);
}
