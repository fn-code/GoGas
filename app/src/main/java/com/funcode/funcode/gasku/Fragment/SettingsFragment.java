package com.funcode.funcode.gasku.Fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.funcode.funcode.gasku.LoginActivity;
import com.funcode.funcode.gasku.R;
import com.funcode.funcode.gasku.SplahActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by funcode on 10/31/17.
 */

public class SettingsFragment extends Fragment {
    EditText pass,hp;
    Button btnKeluar,btnUbahPass,btnUbahHp;
    DatabaseReference userDB;
    FirebaseAuth user;
    ImageView userPF;
    TextView nm_user;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View setting = inflater.inflate(R.layout.settings_fragment, container, false);

        hp = (EditText) setting.findViewById(R.id.hpBaru);
        btnKeluar = (Button) setting.findViewById(R.id.btnKeluar);
        btnUbahHp = (Button) setting.findViewById(R.id.btnUbahHp);
        userPF = (ImageView) setting.findViewById(R.id.users_profil);
        nm_user = (TextView) setting.findViewById(R.id.nm_user);

        user = FirebaseAuth.getInstance();
        final String idUser = user.getCurrentUser().getUid();

        userDB = FirebaseDatabase.getInstance().getReference("users");

        userDB.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Nama = dataSnapshot.child("Nama").getValue(String.class);
                String urlPhoto = dataSnapshot.child("Photo").getValue(String.class);

                Picasso.with(getActivity()).load(urlPhoto).placeholder(R.drawable.gas).error(R.drawable.gas).resize(100,100).centerCrop().into(userPF);
                nm_user.setText(Nama);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUbahHp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newHP = hp.getText().toString().trim();

                if(TextUtils.getTrimmedLength(newHP) >= 11  ) {
                    userDB.child(idUser).child("NoTelp").setValue(newHP);
                    Toast.makeText(getActivity(), "No HP Berhasil Diubah", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getActivity(), "Perikasa Kembali No HP Yang Anda Masukan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.signOut();
                Intent i = new Intent(getActivity().getApplicationContext(), SplahActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        return setting;
    }
}
