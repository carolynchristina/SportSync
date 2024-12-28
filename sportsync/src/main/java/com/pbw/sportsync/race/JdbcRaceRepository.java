package com.pbw.sportsync.race;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRaceRepository implements RaceRepository{
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addRace(Race race){
        String sql = "INSERT INTO race (judul, deskripsi, tglMulai, tglSelesai) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, race.getJudul(), race.getDeskripsi(), race.getTglMulai(), race.getTglSelesai());
    }
}
