package com.pbw.sportsync.analysis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AnalysisJdbc implements AnalysisRepository{
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
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

    @Override
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

    @Override
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
