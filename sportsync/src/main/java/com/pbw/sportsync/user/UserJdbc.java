package com.pbw.sportsync.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return new User(
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            resultSet.getString("roles")
        );
    }

    @Override
    public List<User> pagination(int limit, int offset){
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, limit, offset);
    }

    @Override
    public int rowCount(){
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<WeekChartData> getWeekChartData(String username) {
        LocalDate today = LocalDate.now();
            
        String sql = """
                SELECT tglWaktuMulai, SUM(jarakTempuh) AS totalJarakTempuh
                FROM activity
                WHERE username = ? AND tglWaktuMulai BETWEEN ? AND ?
                GROUP BY tglWaktuMulai
                ORDER BY tglWaktuMulai
                """;
    
        List<WeekChartData> weekChartData = jdbcTemplate.query(sql, this::mapRowToWeekChartData, username, today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY));
    
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.name().substring(0,3);
            boolean exists = false;
            for (WeekChartData data : weekChartData) {
                if (data.getHari().equals(dayName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                weekChartData.add(new WeekChartData(dayName.substring(0,3), 0));
            }
        }
    
        return weekChartData;
    }

    private WeekChartData mapRowToWeekChartData(ResultSet rs, int rowNum) throws SQLException{
        LocalDate activityDate = rs.getDate("tglWaktuMulai").toLocalDate();
        int totalDistance = rs.getInt("totalJarakTempuh");
        String dayOfWeek = activityDate.getDayOfWeek().name().substring(0, 3);
        return new WeekChartData(dayOfWeek, totalDistance);
    }

    public List<MonthChartData> getMonthChartData(String username) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        String sql = """
                SELECT tglWaktuMulai, SUM(jarakTempuh) AS totalJarakTempuh
                FROM activity
                WHERE username = ? AND tglWaktuMulai BETWEEN ? AND ?
                GROUP BY tglWaktuMulai
                ORDER BY tglWaktuMulai
                """;

        List<MonthChartData> monthChartData = jdbcTemplate.query(sql, this::mapRowToMonthChartData, username, firstDayOfMonth, lastDayOfMonth);

        Map<String, Integer> weekMap = new HashMap<>();
        for (MonthChartData data : monthChartData) {
            weekMap.put(data.getMinggu(), weekMap.getOrDefault(data.getMinggu(), 0) + data.getTotalJarakTempuh());
        }

        List<MonthChartData> finalMonthChartData = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            String weekLabel = "Week " + week;
            int totalDistance = weekMap.getOrDefault(weekLabel, 0);
            finalMonthChartData.add(new MonthChartData(weekLabel, totalDistance));
        }

        return finalMonthChartData;
    }

    private MonthChartData mapRowToMonthChartData(ResultSet rs, int rowNum) throws SQLException {
        LocalDate activityDate = rs.getDate("tglWaktuMulai").toLocalDate();
        int totalDistance = rs.getInt("totalJarakTempuh");

        int minggu = activityDate.get(WeekFields.ISO.weekOfMonth());
        String weekLabel = "Week " + minggu;

        return new MonthChartData(weekLabel, totalDistance);
    }

    public List<YearChartData> getYearChartData(String username) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
    
        String sql = """
                SELECT EXTRACT(MONTH FROM tglWaktuMulai) AS bulan, SUM(jarakTempuh) AS totalJarakTempuh
                FROM activity
                WHERE username = ? AND EXTRACT(YEAR FROM tglWaktuMulai) = ?
                GROUP BY EXTRACT(MONTH FROM tglWaktuMulai)
                ORDER BY bulan
                """;
    
        List<YearChartData> yearChartData = jdbcTemplate.query(sql, this::mapRowToYearChartData, username, currentYear);
    
        Map<Integer, Integer> monthMap = new HashMap<>();
        for (YearChartData data : yearChartData) {
            int monthIndex = Month.valueOf(data.getBulan()).getValue();
            monthMap.put(monthIndex, data.getTotalJarakTempuh());
        }
    
        List<YearChartData> finalYearChartData = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String monthName = Month.of(m).name().substring(0, 3); 
            int totalDistance = monthMap.getOrDefault(m, 0);
            finalYearChartData.add(new YearChartData(monthName, totalDistance));
        }
    
        return finalYearChartData;
    }
    
    private YearChartData mapRowToYearChartData(ResultSet rs, int rowNum) throws SQLException {
        int totalDistance = rs.getInt("totalJarakTempuh");
        String bulan = Month.of(rs.getInt("bulan")).name();
        return new YearChartData(bulan, totalDistance);
    }
    
    
}
