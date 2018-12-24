package com.funcode.funcode.gasku;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.funcode.funcode.gasku.Fragment.HistoryLaporanFragment;
import com.funcode.funcode.gasku.google.playservices.placepicker.cardstream.Card;
import com.funcode.funcode.gasku.logger.Log;
import com.funcode.funcode.gasku.model.DetailTransaksiModel;
import com.funcode.funcode.gasku.model.LaporanMasyarakat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;


public class KonfirmasiPesananActivity extends AppCompatActivity {
    TextView alamat,nama_barang,jumlah,harga_satuan,total, tarifAntar, palamat;
    FirebaseDatabase database;
    Query transaksi, admin;
    String idTransaksi, tokenAdmin, alamatUser;
    CardView konfirmasi,batal;
    private LinearLayoutManager mLayoutManager;

    FirebaseAuth mAuth;
    NumberFormat rupiahFormat;
    FirebaseRecyclerAdapter<DetailTransaksiModel, DetailTransaksiViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pesanan);
        alamat = (TextView) findViewById(R.id.alamat);
        palamat = (TextView) findViewById(R.id.palamat);
        total = (TextView) findViewById(R.id.totbayar);
        tarifAntar = (TextView) findViewById(R.id.tarifAntar);
        konfirmasi = (CardView) findViewById(R.id.konfirmasi);
        batal = (CardView) findViewById(R.id.batal);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        idTransaksi = getIntent().getExtras().getString("idTransaksi");


        database = FirebaseDatabase.getInstance();
        transaksi = database.getReference("transaksi").child(idTransaksi);


        admin = database.getReference("admin").orderByChild("Level").equalTo(2);
        admin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tokenAdmin = dataSnapshot.child("Token").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        transaksi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String al = dataSnapshot.child("Lokasi").getValue(String.class);
                Integer tarifAn = dataSnapshot.child("TarifAntar").getValue(Integer.class);
                Integer tot = dataSnapshot.child("TotalBayar").getValue(Integer.class);
                if(!al.equals("")){
                    alamat.setText(al);
                    palamat.setText("Lokasi Tujuan");
                }else {
                    palamat.setText("Pilih Lokasi Anda");
                    alamat.setText("-");
                    palamat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(KonfirmasiPesananActivity.this, PesananActivity.class);
                            i.putExtra("idTransaksi", idTransaksi);
                            startActivity(i);
                        }
                    });
                }
                alamatUser = al;
                total.setText(String.valueOf(tot+tarifAn));
                tarifAntar.setText(String.valueOf(tarifAn));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.daftarpesan);
        mLayoutManager = new LinearLayoutManager(KonfirmasiPesananActivity.this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Query mDatabaseReference = database.getReference("detail_transaksi").orderByChild("IDTransaksi").equalTo(idTransaksi);

        adapter = new FirebaseRecyclerAdapter<DetailTransaksiModel, DetailTransaksiViewHolder>(
                DetailTransaksiModel.class,
                R.layout.activiy_konfirmasi_pesanan_list,
                DetailTransaksiViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final DetailTransaksiViewHolder viewHolder, DetailTransaksiModel model, final int position) {
                viewHolder.nama.setText(String.valueOf(model.Gas+" - "+model.JumlahPesanan));
                if(model.Status == 1){
                    viewHolder.jenis.setText("Beli Baru");
                }else {
                    viewHolder.jenis.setText("Isi Ulang");
                }

                String totl;
                try {
                    NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                    totl = rupiahFormat.format(Double.parseDouble(String.valueOf(model.Total)));
                } catch (NumberFormatException e) {
                    totl = String.valueOf(0);
                }

                String Result = "Rp. " + totl;
                viewHolder.tot.setText(Result);


            }
        };

        mRecyclerView.setAdapter(adapter);

        konfirmasi.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if(alamatUser.equals("")){
                    Toast.makeText(KonfirmasiPesananActivity.this, "Masukan Lokasi Anda", Toast.LENGTH_SHORT).show();
                }else {

                    database.getReference("transaksi").child(idTransaksi).child("Status").setValue(2);
                    send();
                    Intent i = new Intent(KonfirmasiPesananActivity.this, HomeActivity.class);
                    startActivity(i);
                }
            }
        });
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("transaksi").child(idTransaksi).removeValue();
                Query dlt = database.getReference("detail_transaksi").orderByChild("IDTransaksi").equalTo(idTransaksi);
                dlt.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String key = dataSnapshot.getRef().getKey();
                        database.getReference("detail_transaksi").child(key).removeValue();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent i = new Intent(KonfirmasiPesananActivity.this, DaftarGasActivity.class);
                startActivity(i);
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void send(){

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        try {
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Authorization", "key=AAAAR0vgimU:APA91bEHyPFrliBghcPI3r6P67lDBjLtCFQ3qIP7sQXGqc5HPAlSbrN2XwV76FJ-HIy0cU_Ntc7CRDthp8MBzwwk1l5nVWSJYVBmaGqEKrv1y0kfMeMWDDHuIJ5FFZyS4CjRZCUi8wcR");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            JSONObject json = new JSONObject();
            json.put("to","fCvqggaZUog:APA91bFR5rVH6JNZZ5GLsNF5XpZmagb16MRYb3QZ8gZCcrqXyLRNBWZntmtbqQHe0SFQFaFocCIQwVcHznQDoBYinAC9HpbZOBMiB0rF12KR4hJGZP9P86cTGL0XAeRpMrA5loI5w1Cz");
            json.put("priority", "high");
            JSONObject info = new JSONObject();
            info.put("title", "Pesanan Masuk");   // Notification title
            info.put("body", "Permintaan Gas Konsumen");
            info.put("sound", notificationSound);
            info.put("click_action", "com.funcode.funcode.gaskuadmin_TARGET_PESANAN_MASUK");// Notification body
            json.put("notification", info);


            OutputStreamWriter os = new OutputStreamWriter(httpcon.getOutputStream());
            os.write(json.toString());
            os.flush();
            os.close();

            // Reading response
            java.io.InputStream input = httpcon.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                for (String line; (line = reader.readLine()) != null;) {
                    System.out.println("Response : "+line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //ViewHolder for our Firebase UI
    public static class DetailTransaksiViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, jenis,tot;
        View mView;


        public DetailTransaksiViewHolder(View v) {
            super(v);
            mView = v;
            nama = (TextView) v.findViewById(R.id.nama);
            jenis = (TextView) v.findViewById(R.id.jenis);
            tot = (TextView) v.findViewById(R.id.tot);

        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Untuk Kembali Tekan Tombol Batalkan Pesanan", Toast.LENGTH_SHORT).show();
    }
}
