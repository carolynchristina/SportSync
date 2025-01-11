package com.pbw.sportsync.analysis;

import java.util.List;

public interface AnalysisRepository {
    List<WeekChartData> getWeekChartData(String username);
    List<MonthChartData> getMonthChartData(String username);
    List<YearChartData> getYearChartData(String username);
}
