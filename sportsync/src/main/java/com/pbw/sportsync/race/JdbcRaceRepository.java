package com.pbw.sportsync.race;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pbw.sportsync.activity.Activity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class JdbcRaceRepository implements RaceRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addRace(Race race) {
        String sql = "INSERT INTO race (judul, deskripsi, tglMulai, tglSelesai) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, race.getJudul(), race.getDeskripsi(), race.getTglMulai(), race.getTglSelesai());
    }

    @Override
    public List<Race> findOngoingRaces() {
        String sql = "SELECT * FROM race WHERE tglSelesai >= CURRENT_DATE";
        return jdbcTemplate.query(sql, this::mapRowToRace);
    }

    @Override
    public List<Race> findPastRaces() {
        String sql = "SELECT * FROM race WHERE tglSelesai < CURRENT_DATE";
        return jdbcTemplate.query(sql, this::mapRowToRace);
    }

    @Override
    public List<Activity> findLeaderboardByRaceId(int raceId) {
        String sql = """
            SELECT username, jarakTempuh, durasi 
            FROM activity 
            WHERE idRace = ? 
            ORDER BY jarakTempuh DESC, durasi ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Activity(
            rs.getString("username"),
            rs.getInt("jarakTempuh"),
            rs.getTime("durasi").toLocalTime()
        ), raceId);
    }


    private Race mapRowToRace(ResultSet rs, int rowNum) throws SQLException {
        return new Race(
            rs.getInt("id"),
            rs.getString("judul"),
            rs.getString("deskripsi"),
            rs.getDate("tglMulai").toLocalDate(),
            rs.getDate("tglSelesai").toLocalDate(),
            rs.getInt("jarakTempuh")
        );
    }

    @Override
    public List<Race> findValidJoinedRaces(String username, LocalDate dateNow) {
        String sql = """
            SELECT 
                * 
            FROM 
                raceParticipants 
                INNER JOIN race 
                ON race.id = raceParticipants.idRace 
            WHERE 
                raceParticipants.username = ?
                AND tglmulai <= ?
                AND tglselesai >= ?
                AND NOT EXISTS (
                    SELECT 1 
                    FROM activity 
                    WHERE activity.username = raceParticipants.username 
                    AND activity.idRace = race.id
                )
        """;
        return jdbcTemplate.query(sql, this::mapRowToRace, username, dateNow, dateNow);
    }

    @Override
    public boolean isUserInRace(int raceId, String username) {
        String sql = "SELECT COUNT(*) FROM raceParticipants WHERE idRace = ? AND username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, raceId, username);
        return count != null && count > 0;
    }

    @Override
    public void joinRace(int raceId, String username) {
        String sql = "INSERT INTO raceParticipants (idRace, username) VALUES (?, ?)";
        jdbcTemplate.update(sql, raceId, username);
    }
}