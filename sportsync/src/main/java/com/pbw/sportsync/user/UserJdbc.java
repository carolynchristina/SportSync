package com.pbw.sportsync.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbc implements UserRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveActivity(Activity activity) {
        String sql = """
            INSERT INTO activity (judul, deskripsi, tglWaktuMulai, jarakTempuh, durasi, foto, username, idRace)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        byte[] fotoBytes = null;
        if (activity.getFoto()!=null){
            fotoBytes = Base64.getDecoder().decode(activity.getFoto());
        }
        jdbcTemplate.update(
            sql,
            activity.getJudul(),
            activity.getDeskripsi(),
            activity.getTglWaktuMulai(),
            activity.getJarakTempuh(),
            activity.getDurasi(),
            fotoBytes,
            activity.getUsername(),
            activity.getIdRace() > -1 ? activity.getIdRace() : null
        );
    }


    @Override
    public List<Race> findValidJoinedRaces(String username, LocalDate dateNow) {
        String sql = """
            SELECT 
                race.id, 
                race.judul, 
                race.deskripsi, 
                race.tglMulai, 
                race.tglSelesai 
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

    public Race mapRowToRace(ResultSet resultSet, int rowNum) throws SQLException {
        return new Race(
            resultSet.getInt("id"),
            resultSet.getString("judul"),
            resultSet.getString("deskripsi"),
            resultSet.getDate("tglMulai").toLocalDate(),
            resultSet.getDate("tglSelesai").toLocalDate()
        );
    }


}
