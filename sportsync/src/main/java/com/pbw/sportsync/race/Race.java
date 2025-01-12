package com.pbw.sportsync.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import com.pbw.sportsync.activity.Activity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Race {

    private int id;

    @NotNull
    @Size(min = 4, max = 100)
    private String judul;

    @NotNull
    @Size(min = 10, max = 200)
    private String deskripsi;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglMulai;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglSelesai;
    
    @NotNull
    private int jarakTempuh;

    private transient List<Activity> leaderboard;
    
    
        public Race(int id, String judul, String deskripsi, LocalDate tglMulai, LocalDate tglSelesai, int jarakTempuh) {
            this.id = id;
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.tglMulai = tglMulai;
            this.tglSelesai = tglSelesai;
            this.jarakTempuh = jarakTempuh;
    }
}
