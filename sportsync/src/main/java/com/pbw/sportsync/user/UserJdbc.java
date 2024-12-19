package com.pbw.sportsync.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveActivity'");
    }

    @Override
    public List<Race> findOngoingJoinedRaces(String username, LocalDate dateNow) {
        String sql =  """
                SELECT 
                    race.id, 
                    race.judul, 
                    deskripsi, 
                    tglMulai, 
                    tglSelesai 
                FROM 
                    raceParticipants 
                    INNER JOIN race 
                    ON race.id = raceParticipants.idRace 
                WHERE 
                    username = ?
                    AND tglmulai <= ?
                    AND tglselesai >= ?
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
    public void submitToRace(Activity activity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submitToRace'");
    }
}
