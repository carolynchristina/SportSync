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

    @Override
    public List<User> findAll(){
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public List<User> findByKeyword(String keyword){
        String sql = "SELECT * FROM users WHERE username ILIKE ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, "%"+keyword+"%");
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException{
        boolean isActive = resultSet.getBoolean("status");
        String status = isActive? "Active" : "Inactive";
        return new User(
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            resultSet.getString("roles"),
            status
        );
    }

    @Override
    public List<User> pagination(int limit, int offset, String status, String keyword){
        String sql = "";
        if(status.equals("")){ //all status
            sql = "SELECT * FROM users WHERE username ILIKE ? LIMIT ? OFFSET ?";
        }else if(status.equalsIgnoreCase("active")){ //active status
            sql = "SELECT * FROM users WHERE status=true AND username ILIKE ? LIMIT ? OFFSET ?";
        }else{ //inactive status
            sql = "SELECT * FROM users WHERE status=false AND username ILIKE ? LIMIT ? OFFSET ?";
        }
        return jdbcTemplate.query(sql, this::mapRowToUser, "%"+keyword+"%", limit, offset);
    }

    @Override
    public int rowCount(String status, String keyword){
        String sql = "SELECT COUNT(*) FROM users WHERE username ILIKE ?";
        if(status.equalsIgnoreCase("active")){ //active status
            sql += " AND status=true";
        }else if(status.equalsIgnoreCase("inactive")){ //inactive status
            sql += " AND status=false";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class, "%"+keyword+"%");
    }
    
    @Override
    public boolean editStatus(String username, boolean status){
        int rowsAffected = 0;
        String sql = "UPDATE users SET status = ? WHERE username = ?";
        rowsAffected = jdbcTemplate.update(sql, status, username);
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteUser(String username){
        String sql = "DELETE FROM users WHERE username = ?";
        try{
            jdbcTemplate.update(sql, username);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
