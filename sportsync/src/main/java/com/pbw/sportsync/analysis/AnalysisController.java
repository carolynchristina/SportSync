package com.pbw.sportsync.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("sportsync/user")
public class AnalysisController {
    
    @Autowired
    private AnalysisRepository analysisRepository;

    @GetMapping("/analysis")
    public String analysis(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        
        List<WeekChartData> weekChartData = analysisRepository.getWeekChartData(username);
        List<MonthChartData> monthChartData = analysisRepository.getMonthChartData(username);
        List<YearChartData> yearChartData = analysisRepository.getYearChartData(username);
        
        //Sort weekChartData
        List<String> daysOfWeek = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
        Map<String, WeekChartData> weekDataMap = new HashMap<>();
        for (WeekChartData data : weekChartData) {
            weekDataMap.put(data.getHari(), data);
        }
        
        List<WeekChartData> sortedWeekChartData = new ArrayList<>();
        for (String day : daysOfWeek) {
            WeekChartData data = weekDataMap.get(day);
            if (data != null) {
                sortedWeekChartData.add(data);
            } else {
                sortedWeekChartData.add(new WeekChartData(day, 0));
    
            }
        }
        
        //Summary
        int weekTotalActivities = 0;
        int weekTotalDistance = 0;
        int weekAverageDistance = 0;
        
        int monthTotalActivities = 0;
        int monthTotalDistance = 0;
        int monthAverageDistance = 0;
        
        int yearTotalActivities = 0;
        int yearTotalDistance = 0;
        int yearAverageDistance = 0;
        
        for (WeekChartData data : weekChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                weekTotalActivities++;
                weekTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (weekTotalActivities > 0) {
            weekAverageDistance = weekTotalDistance / weekTotalActivities;
        }

        for (MonthChartData data : monthChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                monthTotalActivities++;
                monthTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (monthTotalActivities > 0) {
            monthAverageDistance = monthTotalDistance / monthTotalActivities;
        }
        
        for (YearChartData data : yearChartData) {
            if (data.getTotalJarakTempuh() > 0) {
                yearTotalActivities++;
                yearTotalDistance += data.getTotalJarakTempuh();
            }
        }
        if (yearTotalActivities > 0) {
            yearAverageDistance = yearTotalDistance / yearTotalActivities;
        }
        
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("weekChartData", sortedWeekChartData);
        model.addAttribute("monthChartData", monthChartData);
        model.addAttribute("yearChartData", yearChartData);

        model.addAttribute("weekTotalActivities", weekTotalActivities);
        model.addAttribute("weekTotalDistance", weekTotalDistance);
        model.addAttribute("weekAverageDistance", weekAverageDistance);

        model.addAttribute("monthTotalActivities", monthTotalActivities);
        model.addAttribute("monthTotalDistance", monthTotalDistance);
        model.addAttribute("monthAverageDistance", monthAverageDistance);

        model.addAttribute("yearTotalActivities", yearTotalActivities);
        model.addAttribute("yearTotalDistance", yearTotalDistance);
        model.addAttribute("yearAverageDistance", yearAverageDistance);

        return "user/Analysis";
    }
}
