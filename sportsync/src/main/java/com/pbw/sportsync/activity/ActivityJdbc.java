package com.pbw.sportsync.activity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityJdbc implements ActivityRepository{

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
    public List<Activity> findUserActivities(String username) {
        String sql = """
                SELECT 
                    judul,
                    deskripsi,
                    tglWaktuMulai,
                    jarakTempuh,
                    durasi,
                    username,
                    foto,
                    idRace
                FROM
                    activity
                WHERE
                    username = ?
                ORDER BY
                    tglWaktuMulai DESC
                """;

        return jdbcTemplate.query(sql, this::mapRowToActivity, username);

    }

    public Activity mapRowToActivity(ResultSet resultSet, int rowNum) throws SQLException {
        byte[] fotoBytes = resultSet.getBytes("foto");
        
        String fotoBase64 = fotoBytes != null ? Base64.getEncoder().encodeToString(fotoBytes) : null;
        
        return new Activity(
            resultSet.getString("judul"),
            resultSet.getString("deskripsi"),
            resultSet.getTimestamp("tglWaktuMulai").toLocalDateTime(),
            resultSet.getInt("jarakTempuh"),
            resultSet.getTime("durasi").toLocalTime(),
            resultSet.getString("username"),
            fotoBase64,
            resultSet.getInt("idRace")
        );
    }
}
