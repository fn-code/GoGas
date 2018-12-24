package com.funcode.funcode.gasku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.funcode.funcode.gasku.google.playservices.placepicker.cardstream.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DaftarGasActivity extends AppCompatActivity {
    NumberPicker npBaru5kg,npBaru12kg,npIsi5kg,npIsi12kg;
    CardView btnLanjut;
    FirebaseDatabase db;
    String idUsers;
    FirebaseAuth mAuthUser;
    int baru5kg,baru12kg,isi5kg,isi12kg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_gas);

        npBaru5kg = (NumberPicker) findViewById(R.id.np_baru_5kg);
        npBaru12kg = (NumberPicker) findViewById(R.id.np_baru_12kg);
        npIsi5kg = (NumberPicker) findViewById(R.id.np_isi_5kg);
        npIsi12kg = (NumberPicker) findViewById(R.id.np_isi_12kg);
        btnLanjut = (CardView) findViewById(R.id.lanjutkan);
        npBaru5kg.setValue(0);
        npBaru5kg.setMax(3);
        npBaru12kg.setValue(0);
        npBaru12kg.setMax(3);
        npIsi5kg.setValue(0);
        npIsi5kg.setMax(3);
        npIsi12kg.setValue(0);
        npIsi12kg.setMax(3);
        mAuthUser = FirebaseAuth.getInstance();
        idUsers = mAuthUser.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        DatabaseReference dbGas5kg = db.getReference("Jenis").child("1");
        dbGas5kg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int Harga = dataSnapshot.child("Harga").getValue(Integer.class);
                int HargaIsi = dataSnapshot.child("HargaIsi").getValue(Integer.class);
                baru5kg = Harga;
                isi5kg = HargaIsi;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference dbGas12kg = db.getReference("Jenis").child("2");
        dbGas12kg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int Harga = dataSnapshot.child("Harga").getValue(Integer.class);
                int HargaIsi = dataSnapshot.child("HargaIsi").getValue(Integer.class);
                baru12kg = Harga;
                isi12kg = HargaIsi;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total = npBaru5kg.getValue() + npBaru12kg.getValue() + npIsi5kg.getValue() + npIsi12kg.getValue();
                if(total > 3){
                    Toast.makeText(DaftarGasActivity.this, "Maaf, anda sudah mencapai batas maksimum pemesanan.", Toast.LENGTH_LONG).show();
                }else if(total == 0) {
                    Toast.makeText(DaftarGasActivity.this, "Pesanan minimal 1 buah bright gas.", Toast.LENGTH_LONG).show();
                }else {
                    int tot = (isi5kg * npIsi5kg.getValue()) + (isi12kg * npIsi12kg.getValue()) + (baru5kg * npBaru5kg.getValue()) + (baru12kg * npBaru12kg.getValue());
                    DatabaseReference transaksi = db.getReference("transaksi").push();
                    String key = transaksi.getRef().getKey();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    transaksi.child("IDTransaksi").setValue(key);
                    transaksi.child("Lokasi").setValue("");
                    transaksi.child("TujuanLatitude").setValue(0);
                    transaksi.child("TujuanLongitude").setValue(0);
                    transaksi.child("Status").setValue(1);
                    transaksi.child("IDUser").setValue(idUsers);
                    transaksi.child("IDDriver").setValue("");
                    transaksi.child("TotalBayar").setValue(tot);
                    transaksi.child("TarifAntar").setValue(10000);
                    transaksi.child("Waktu").setValue(formattedDate);
                    transaksi.child("Ket").setValue("");
                    transaksi.child("KetAdmin").setValue("");

                    if(npIsi5kg.getValue() != 0){
                        gas5kg(2,key,isi5kg*npIsi5kg.getValue(), npIsi5kg.getValue());
                    }
                    if(npIsi12kg.getValue() != 0) {
                        gas12kg(2,key,isi12kg*npIsi12kg.getValue(), npIsi12kg.getValue());
                    }
                    if(npBaru5kg.getValue() != 0) {
                        gas5kg(1,key,baru5kg*npBaru5kg.getValue(), npBaru5kg.getValue());
                    }
                    if(npBaru12kg.getValue() != 0) {
                        gas12kg(1,key,baru12kg*npBaru12kg.getValue(), npBaru12kg.getValue());
                    }

                    Intent i = new Intent(DaftarGasActivity.this, KonfirmasiPesananActivity.class);
                    i.putExtra("idTransaksi",key);
                    startActivity(i);
                    finish();
                }
            }
        });


    }

    public void gas5kg(int jenis, String key, int harga, int jml){
            DatabaseReference transaksi5kg = db.getReference("detail_transaksi").push();
            String keyTrans5kg = transaksi5kg.getRef().getKey();
            transaksi5kg.child("Status").setValue(jenis);
            transaksi5kg.child("Gas").setValue("5kg");
            transaksi5kg.child("IDGas").setValue("1");
            transaksi5kg.child("Total").setValue(harga);
            transaksi5kg.child("JumlahPesanan").setValue(jml);
            transaksi5kg.child("IDTransaksi").setValue(key);

    }
    public void gas12kg(int jenis, String key, int harga, int jml){
        DatabaseReference transaksi12kg = db.getReference("detail_transaksi").push();
        String keyTrans12kg = transaksi12kg.getRef().getKey();
        transaksi12kg.child("Status").setValue(jenis);
        transaksi12kg.child("Gas").setValue("12kg");
        transaksi12kg.child("IDGas").setValue("2");
        transaksi12kg.child("Total").setValue(harga);
        transaksi12kg.child("JumlahPesanan").setValue(jml);
        transaksi12kg.child("IDTransaksi").setValue(key);

    }
}
