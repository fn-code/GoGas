package com.funcode.funcode.gasku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class BatalkanActivity extends AppCompatActivity {

    EditText ket;
    Button simpan;
    FirebaseDatabase db;
    LinearLayout lapor, selesai;
    TextView anda,admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batalkan);

        ket = (EditText) findViewById(R.id.ket);
        simpan = (Button) findViewById(R.id.simpan);
        lapor = (LinearLayout) findViewById(R.id.pelaporan);
        selesai = (LinearLayout) findViewById(R.id.selesai);
        anda = (TextView) findViewById(R.id.anda);
        admin = (TextView) findViewById(R.id.admin);

        final String idTransaksi = getIntent().getStringExtra("idTransaksi");
        final String jns = getIntent().getStringExtra("jenis");
//        Toast.makeText(BatalkanActivity.this, idTransaksi, Toast.LENGTH_SHORT).show();

        if(jns.equals("1")) {
            selesai.setVisibility(View.GONE);
        }else if(jns.equals("2")){
            lapor.setVisibility(View.GONE);

        }
        db = FirebaseDatabase.getInstance();

        Query sh = db.getReference("transaksi").child(idTransaksi);
        sh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ket = dataSnapshot.child("Ket").getValue(String.class);
                String ketAdmin = dataSnapshot.child("KetAdmin").getValue(String.class);
                anda.setText(ket);
                admin.setText(ketAdmin);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        db.getReference("transaksi").child(idTransaksi).child("Ket").setValue(ket.getText().toString());
                        db.getReference("transaksi").child(idTransaksi).child("Status").setValue(5);
                Intent i = new Intent(BatalkanActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
