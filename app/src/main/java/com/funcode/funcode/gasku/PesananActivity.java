package com.funcode.funcode.gasku;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.funcode.funcode.gasku.google.playservices.placepicker.MainActivity;
import com.funcode.funcode.gasku.google.playservices.placepicker.cardstream.Card;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PesananActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Marker mCenterMarker;
    CardView pesan;
    TextView hargaGas,jenisGas, pilihLokasi,btnpilihLokasi, jmlPesanan, btnjmlPesanan, totBayar, tarifAntar;
    private static final int REQUEST_PLACE_PICKER = 1;

    Double lokasiLatitude;
    Double lokasiLongitude;
    Double tujuanLatitude;
    Double tujuanLongitude;
    Integer jumpesanan;
    String Tujuan;
    FirebaseAuth mAuthUser;
    LinearLayout detail;
    String idTransaksi, jenisgas, lokasi,idUsers;
    FirebaseDatabase database;
    DatabaseReference dbGas, transaksi;
    public int tarif, harga, totalKonsument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);

        pesan = (CardView) findViewById(R.id.pesan);
        pilihLokasi = (TextView) findViewById(R.id.pilih_lokasi);
        btnpilihLokasi  = (TextView) findViewById(R.id.btn_pilih_lokasi);
        detail = (LinearLayout) findViewById(R.id.detail);

        mAuthUser = FirebaseAuth.getInstance();
        idUsers = mAuthUser.getCurrentUser().getUid();

        idTransaksi = getIntent().getExtras().getString("idTransaksi");
        database = FirebaseDatabase.getInstance();


        detail.setVisibility(View.GONE);

        btnpilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(PesananActivity.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);

                    // Hide the pick option in the UI to prevent users from starting the picker
                    // multiple times.


                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), PesananActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(PesananActivity.this, "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                transaksi = database.getReference("transaksi").child(idTransaksi);
                transaksi.child("TujuanLatitude").setValue(tujuanLatitude);
                transaksi.child("TujuanLongitude").setValue(tujuanLongitude);
                transaksi.child("Lokasi").setValue(lokasi);
                Intent i = new Intent(PesananActivity.this, KonfirmasiPesananActivity.class);
                i.putExtra("idTransaksi", idTransaksi);
                startActivity(i);

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_poke);
        mapFragment.getMapAsync(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER) {
            // This result is from the PlacePicker dialog.

            if (resultCode == Activity.RESULT_OK) {

                final Place place = PlacePicker.getPlace(data, PesananActivity.this);

                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence address = place.getAddress();
                final LatLng latlng = place.getLatLng();
                String attribution = PlacePicker.getAttributions(data);
                if (attribution == null) {
                    attribution = "";
                }

                pilihLokasi.setText(address.toString());

                tujuanLatitude = latlng.latitude;
                tujuanLongitude = latlng.longitude;
                lokasi = address.toString();

                if(!lokasi.equals("")){
                    detail.setVisibility(View.VISIBLE);
                }

                mCenterMarker = mMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title("Lokasi Anda"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng PERTH = new LatLng(-31.90, 115.86);


        mMap.setPadding(10, 10, 10, 10);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Pilih Lokasi Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show();
    }

}
