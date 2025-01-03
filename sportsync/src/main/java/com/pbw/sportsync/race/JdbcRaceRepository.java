package com.pbw.sportsync.race;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcRaceRepository implements RaceRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcRaceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    private Race mapRowToRace(ResultSet rs, int rowNum) throws SQLException {
        return new Race(
            rs.getInt("id"),
            rs.getString("judul"),
            rs.getString("deskripsi"),
            rs.getDate("tglMulai").toLocalDate(),
            rs.getDate("tglSelesai").toLocalDate()
        );
    }
}