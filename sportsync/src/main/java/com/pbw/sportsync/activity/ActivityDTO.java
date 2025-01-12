package com.pbw.sportsync.activity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ActivityDTO {
    private String judul;
    private String deskripsi;
    private LocalDateTime tglWaktuMulai;
    private int jarakTempuh;
    private int durasiJam;   
    private int durasiMenit;
    private int durasiDetik;
    private String username;
    private String foto;
    private int idRace;

    public LocalTime getDurasi() {
        return LocalTime.of(durasiJam, durasiMenit, durasiDetik);
    }
}

