package com.funcode.funcode.gasku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.funcode.funcode.gasku.Fragment.HistoryFragment;
import com.funcode.funcode.gasku.Fragment.HomeFragment;
import com.funcode.funcode.gasku.Fragment.SettingsFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity  {

    private TextView mTextMessage;
    private Button keluar;
    FirebaseAuth mAuthDriver;
    FirebaseDatabase mUser;
    String idUsers, nama;
    int level;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference dbUser;
    Toolbar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Fragment homeFrag = new HomeFragment();
                    FragmentTransaction transac = getSupportFragmentManager().beginTransaction();
                    transac.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    transac.replace(R.id.content, homeFrag);
                    transac.commit();

                    return true;
                case R.id.navigation_dashboard:

                    Fragment historyFrag = new HistoryFragment();
                    FragmentTransaction transacHistory = getSupportFragmentManager().beginTransaction();
                    transacHistory.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    transacHistory.replace(R.id.content, historyFrag);
                    transacHistory.commit();
                    return true;
                case R.id.navigation_notifications:
                    Fragment settingFrag = new SettingsFragment();
                    FragmentTransaction transacSet = getSupportFragmentManager().beginTransaction();
                    transacSet.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    transacSet.replace(R.id.content, settingFrag);
                    transacSet.commit();

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*mTextMessage = (TextView) findViewById(R.id.nama);
        keluar = (Button) findViewById(R.id.keluar);*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Drawable icon = getResources().getDrawable(R.drawable.top_logo);
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 360, 100, true));
        getSupportActionBar().setTitle("");

        getSupportActionBar().setIcon(newdrawable);
        mAuthDriver = FirebaseAuth.getInstance();
        mUser = FirebaseDatabase.getInstance();


        Fragment homeFrag = new HomeFragment();
        FragmentTransaction transac = getSupportFragmentManager().beginTransaction();
        transac.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transac.replace(R.id.content, homeFrag);
        transac.commit();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    //Toast.makeText(HomeActivity.this, "awal checked", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };



        mAuth = FirebaseAuth.getInstance();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }


}
