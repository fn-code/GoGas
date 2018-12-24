package com.funcode.funcode.gasku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadLaporanActivity extends AppCompatActivity {

    EditText ket;
    ImageButton foto;
    Button btnlapor;
    private static final int GALLERY_REQUEST = 1;
    private StorageReference mStorage;
    DatabaseReference mDatabase;
    Uri mImageUri=null;
    FirebaseAuth mAuth;
    ProgressDialog mProgres;
    FirebaseDatabase database;
    String idTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_laporan);

        ket = (EditText) findViewById(R.id.ket);
        foto = (ImageButton) findViewById(R.id.foto);
        btnlapor = (Button) findViewById(R.id.simpan);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("foto_pelaporan");
        mProgres = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();


        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);
            }
        });
        btnlapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(!ket.getText().toString().equals("")){
                    uploadLaporan();
                    mProgres.setMessage("Laporan Sedang Diupload ...");
                    mProgres.show();
                    mProgres.setCancelable(false);
            }else {
                Toast.makeText(UploadLaporanActivity.this, "Keterangan Laporan Harus Di Isi", Toast.LENGTH_SHORT).show();
            }


            }
        });
    }

    private void uploadLaporan() {

        if(mImageUri != null) {

            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadURL = taskSnapshot.getDownloadUrl().toString();
                    String id_user = mAuth.getCurrentUser().getUid();
                    String nama = mAuth.getCurrentUser().getDisplayName();

                    mDatabase = FirebaseDatabase.getInstance().getReference("laporan_masyarakat").push();
                    String idLapor = mDatabase.getRef().getKey();
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    String keterangan = ket.getText().toString();

                        mDatabase.child("Photo").setValue(downloadURL);
                        mDatabase.child("IDLaporan").setValue(idLapor);
                        mDatabase.child("Keterangan").setValue(keterangan);
                        mDatabase.child("IDUser").setValue(id_user);
                        mDatabase.child("Waktu").setValue(formattedDate);
                        mDatabase.child("Pelapor").setValue(nama);

                        mProgres.dismiss();
                        Toast.makeText(UploadLaporanActivity.this, "Upload Laporan Selesai", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UploadLaporanActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK ){
            Uri imageUri = data.getData();
            //foto.setImageURI(imageUri);
            //foto.setImageUriAsync(imageUri);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Picasso.with(UploadLaporanActivity.this).load(mImageUri).into(foto);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
