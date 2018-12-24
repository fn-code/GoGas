package com.funcode.funcode.gasku.model;

/**
 * Created by Ain on 11/29/2017.
 */

public class HistoryModel {


    public String Lokasi;
    public Integer Jumlah;
    public Integer TotalBayar;
    public Integer TarifAntar;
    public String JenisGas;
    public String Keterangan;
    public String Waktu;
    public Integer Harga;
    public Integer Status;

    public HistoryModel(){

    }

    public HistoryModel(String lokasi, Integer jumlah, Integer totalBayar, Integer tarifAntar, String jenisGas, String keterangan, String waktu, Integer harga, Integer status) {
        Lokasi = lokasi;
        Jumlah = jumlah;
        TotalBayar = totalBayar;
        TarifAntar = tarifAntar;
        JenisGas = jenisGas;
        Keterangan = keterangan;
        Waktu = waktu;
        Harga = harga;
        Status = status;
    }
}
