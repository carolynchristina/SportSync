package com.pbw.sportsync.race;

import java.util.List;

public interface RaceRepository {
    void addRace(Race race);
    List<Race> findOngoingRaces();
    List<Race> findPastRaces();
}
