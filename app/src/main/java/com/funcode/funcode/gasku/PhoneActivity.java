package com.funcode.funcode.gasku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        final EditText phone = (EditText) findViewById(R.id.phone);
        Button lanjut = (Button) findViewById(R.id.lanjut);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        id_user = getIntent().getExtras().getString("idUser");

        lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(phone.getText())){
                    mDatabase.child(id_user).child("NoTelp").setValue(phone.getText().toString());
                    Intent i = new Intent(PhoneActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PhoneActivity.this, "Masukan No Handphone Anda Dulu", Toast.LENGTH_SHORT).show();
        return;


    }
}
