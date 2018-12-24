package com.funcode.funcode.gasku.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.funcode.funcode.gasku.BatalkanActivity;
import com.funcode.funcode.gasku.R;
import com.funcode.funcode.gasku.UploadLaporanActivity;
import com.funcode.funcode.gasku.model.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Ain on 12/14/2017.
 */

public class HistoryPesananFragment extends Fragment {


    private LinearLayoutManager mLayoutManager;

    FirebaseAuth mAuth;
    NumberFormat rupiahFormat;
    FirebaseRecyclerAdapter<HistoryModel, HistoryPesananFragment.HistoryViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View historyPesananFragment = inflater.inflate(R.layout.history_pesanan_fragment, container, false);

        final RecyclerView mRecyclerView = (RecyclerView) historyPesananFragment.findViewById(R.id.history_list);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String id_user = mAuth.getCurrentUser().getUid();
        Query mDatabaseReference = database.getReference("transaksi").orderByChild("IDUser").equalTo(id_user);

        adapter = new FirebaseRecyclerAdapter<HistoryModel, HistoryPesananFragment.HistoryViewHolder>(
                HistoryModel.class,
                R.layout.history_pesanan_fragment_list,
                HistoryPesananFragment.HistoryViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final HistoryPesananFragment.HistoryViewHolder viewHolder, HistoryModel model, final int position) {
                rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

                String rupiah;
                String total;
                try {
                    NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                    rupiah = rupiahFormat.format(Double.parseDouble(String.valueOf(model.TarifAntar)));
                    total = rupiahFormat.format(Double.parseDouble(String.valueOf(model.TotalBayar)));
                } catch (NumberFormatException e) {
                    rupiah = String.valueOf(0);
                    total = String.valueOf(0);
                }

                String Result = "Rp. " + rupiah;
                String ResultTotal = "Rp. " + total;

                final String post_key = getRef(position).getKey();

                final Integer status = model.Status;
                if(status == 2){
                    viewHolder.btn.setVisibility(View.GONE);
                    viewHolder.status.setText("MENCARI DRIVER");
                }else if(status == 3){
                    final String jns = "1";
                    viewHolder.status.setText("SEDANG DI ANTAR");
                    viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), BatalkanActivity.class);
                            i.putExtra("idTransaksi", post_key);
                            i.putExtra("jenis", jns);
                            startActivity(i);
                        }
                    });

                }else if(status == 4){
                    viewHolder.btn.setVisibility(View.GONE);
                    viewHolder.status.setText("SELESAI");
                }
                else if(status == 5 || status == 6){
                    final String jns = "2";
                    viewHolder.btn.setVisibility(View.GONE);
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent k = new Intent(getActivity(), BatalkanActivity.class);
                            k.putExtra("idTransaksi", post_key);
                            k.putExtra("jenis", jns);
                            startActivity(k);
                        }
                    });
                    viewHolder.status.setText("DIBATALKAN");
                }

                viewHolder.tarif.setText(Result);
                viewHolder.alamat.setText(model.Lokasi);
                viewHolder.waktu.setText(model.Waktu);
                viewHolder.total.setText(ResultTotal);





            }
        };

        mRecyclerView.setAdapter(adapter);


        return historyPesananFragment;
    }

    //ViewHolder for our Firebase UI
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView ket,tarif,total,alamat,waktu, status;
        public Button btn;
        View mView;


        public HistoryViewHolder(View v) {
            super(v);
            mView = v;
            tarif = (TextView) v.findViewById(R.id.tarifAntar);
            status = (TextView) v.findViewById(R.id.status);
            total = (TextView) v.findViewById(R.id.totbayar);
            alamat = (TextView) v.findViewById(R.id.alamat);
            waktu = (TextView) v.findViewById(R.id.waktu);
            btn = (Button) v.findViewById(R.id.batalkan);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}

