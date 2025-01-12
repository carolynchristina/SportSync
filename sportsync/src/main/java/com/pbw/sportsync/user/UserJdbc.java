package com.pbw.sportsync.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbc implements UserRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Override
    public Optional<User> findUser(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> result = jdbcTemplate.query(sql, this::mapRowToUser, email);
        if(result.size()==0) return Optional.empty(); //user tidak ditemukan
        else return Optional.of(result.get(0)); //user ditemukan
    }

    @Override
    public void addUser(User user){
        boolean stats = true;
        if(user.getStatus().equalsIgnoreCase("active")){
            stats = true;
        }
        else{
            stats = false;
        }
        String sql = "INSERT INTO users (username, email, password, roles, status) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles(),
                stats);
    }
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> result = jdbcTemplate.query(sql, this::mapRowToUser, username);
        if(result.size()==0) return Optional.empty(); //user tidak ditemukan
        else return Optional.of(result.get(0)); //user ditemukan
    }

    @Override
    public void saveUser(String oldUsername, User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, roles = ?, status = ? WHERE username = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRoles(), user.getStatus(), oldUsername);
    }

    @Override
    public void saveEncryptedPassword(User user){
        String sql ="UPDATE users SET password = ? WHERE email = ?";
        jdbcTemplate.update(sql, user.getPassword(),user.getEmail());
    }

    @Override
    public String lastActivityDate(String username){
        String sql = "SELECT last_activity FROM lastActDate WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, String.class, username);
    }
    @Override
    public int countActivity(String username){
        String sql = "SELECT COUNT(*) FROM activity WHERE username ILIKE ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }
}
