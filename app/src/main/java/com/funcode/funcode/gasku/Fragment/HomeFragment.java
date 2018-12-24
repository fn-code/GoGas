package com.funcode.funcode.gasku.Fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.funcode.funcode.gasku.DaftarGasActivity;
import com.funcode.funcode.gasku.HomeActivity;
import com.funcode.funcode.gasku.LoginActivity;
import com.funcode.funcode.gasku.PesananActivity;
import com.funcode.funcode.gasku.R;
import com.funcode.funcode.gasku.SplahActivity;
import com.funcode.funcode.gasku.UploadLaporanActivity;
import com.funcode.funcode.gasku.google.playservices.placepicker.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by funcode on 10/31/17.
 */

public class HomeFragment extends Fragment {

    CardView btnPesan, btnPesan12kg, btnLapor;
    String idUsers, urlPhoto;
    FirebaseAuth mAuthDriver;
    FirebaseDatabase mUser;
    DatabaseReference dbUser;
    TextView nmUser;
    ImageView imgView;
    private FirebaseAuth mAuth;
    private SliderLayout mSlider;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.home_fragment, container, false);

        btnPesan = (CardView) home.findViewById(R.id.pesan);
        //btnPesan12kg = (CardView) home.findViewById(R.id.pesan_12kg);
        btnLapor = (CardView) home.findViewById(R.id.lapor);


        mUser = FirebaseDatabase.getInstance();
        nmUser = (TextView) home.findViewById(R.id.nm_user);
        mSlider = (SliderLayout) home.findViewById(R.id.slider);


        mAuthDriver = FirebaseAuth.getInstance();

        idUsers = mAuthDriver.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    //Toast.makeText(HomeActivity.this, "awal checked", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), SplahActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }else {

                    btnLapor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent k = new Intent(getActivity(), UploadLaporanActivity.class);
                            startActivity(k);
                        }
                    });

                    btnPesan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), DaftarGasActivity.class);
                            startActivity(i);
                        }
                    });

//                    btnPesan12kg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent i = new Intent(getActivity(), PesananActivity.class);
//                            i.putExtra("idGas", "2");
//                            startActivity(i);
//                        }
//                    });
                }
            }
        };


        mAuth = FirebaseAuth.getInstance();

//        HashMap<String,String> url_maps = new HashMap<String, String>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Percobban 1",R.drawable.header1);
        file_maps.put("Percobaan 2",R.drawable.header2);
        file_maps.put("ah masa",R.drawable.header3);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Toast.makeText(getActivity(), "Selamat Datang", Toast.LENGTH_SHORT).show();
                        }
                    });

            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra",name);

            mSlider.addSlider(textSliderView);
        }

        mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(10000);

        return home;

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onPause() {
        mSlider.stopAutoCycle();
        super.onPause();
    }
}
