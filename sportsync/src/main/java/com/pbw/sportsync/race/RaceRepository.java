package com.pbw.sportsync.race;

import java.time.LocalDate;
import java.util.List;

import com.pbw.sportsync.activity.Activity;

public interface RaceRepository {
    void addRace(Race race);
    List<Race> findAllRace();
    List<Race> findByKeyword(String keyword);
    List<Race> findOngoingRaces();
    List<Race> findPastRaces();
    List<Activity> findLeaderboardByRaceId(int raceId);
    void joinRace(int raceId, String username);
    List<Race> findValidJoinedRaces(String username, LocalDate dateNow);
    boolean isUserInRace(int raceId, String username);
    List<Race> pagination(int limit, int offset, String status, String keyword);
    int rowCount(String status, String keyword);
    List<Race> findById(Integer id);
    int totalParticipants(Integer id);
}
