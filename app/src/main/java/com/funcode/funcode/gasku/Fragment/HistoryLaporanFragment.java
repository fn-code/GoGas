package com.funcode.funcode.gasku.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.funcode.funcode.gasku.R;
import com.funcode.funcode.gasku.model.HistoryModel;
import com.funcode.funcode.gasku.model.LaporanMasyarakat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Ain on 12/14/2017.
 */

public class HistoryLaporanFragment extends Fragment {

    private LinearLayoutManager mLayoutManager;

    FirebaseAuth mAuth;
    NumberFormat rupiahFormat;
    FirebaseRecyclerAdapter<LaporanMasyarakat, HistoryLaporanFragment.HistoryLaporanViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View laporan = inflater.inflate(R.layout.history_laporan_fragment, container, false);

        final RecyclerView mRecyclerView = (RecyclerView) laporan.findViewById(R.id.laporan_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String id_user = mAuth.getCurrentUser().getUid();
        Query mDatabaseReference = database.getReference("laporan_masyarakat");

        adapter = new FirebaseRecyclerAdapter<LaporanMasyarakat, HistoryLaporanViewHolder>(
                LaporanMasyarakat.class,
                R.layout.history_laporan_fragment_list,
                HistoryLaporanFragment.HistoryLaporanViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final HistoryLaporanFragment.HistoryLaporanViewHolder viewHolder, LaporanMasyarakat model, final int position) {

                Picasso.with(getActivity()).load(model.Photo).placeholder(R.drawable.gas).error(R.drawable.gas).into(viewHolder.foto);
                viewHolder.ket.setText(model.Keterangan);
                viewHolder.waktu.setText(model.Waktu);


            }
        };

        mRecyclerView.setAdapter(adapter);


        return laporan;
    }


    //ViewHolder for our Firebase UI
    public static class HistoryLaporanViewHolder extends RecyclerView.ViewHolder {

        public TextView ket,waktu;
        ImageView foto;
        View mView;


        public HistoryLaporanViewHolder(View v) {
            super(v);
            mView = v;
            foto = (ImageView)  v.findViewById(R.id.laporanFoto);
            ket = (TextView) v.findViewById(R.id.ket);
            waktu = (TextView) v.findViewById(R.id.waktu);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
