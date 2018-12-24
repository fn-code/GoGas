package com.funcode.funcode.gasku.model;

/**
 * Created by Ain on 1/19/2018.
 */

public class DetailTransaksiModel {

    public String Gas;
    public String IDGas;
    public String IDTransaksi;
    public Integer Status;
    public Integer Total;
    public Integer JumlahPesanan;

    public DetailTransaksiModel(){

    }

    public DetailTransaksiModel(String gas, String IDGas, String IDTransaksi, Integer status, Integer total, Integer jumlahPesanan) {
        Gas = gas;
        this.IDGas = IDGas;
        this.IDTransaksi = IDTransaksi;
        Status = status;
        Total = total;
        JumlahPesanan = jumlahPesanan;
    }
}
