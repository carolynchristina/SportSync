package com.pbw.sportsync.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Race {
    private int id;
    private String judul;
    private String deskripsi;
    private LocalDate tglMulai;
    private LocalDate tglSelesai;
}
