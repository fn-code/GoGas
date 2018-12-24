package com.funcode.funcode.gasku.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.funcode.funcode.gasku.R;


/**
 * Created by funcode on 10/31/17.
 */

public class HistoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View history = inflater.inflate(R.layout.history_fragment, container, false);

        viewPager = (ViewPager) history.findViewById(R.id.viewpager);
/*        toolbar = (Toolbar) history.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);*/
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);

        tabLayout = (TabLayout) history.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return history;
    }


    private void setupViewPager(ViewPager viewPager) {
        Pager adapter = new Pager(getChildFragmentManager());
        adapter.addFragment(new HistoryPesananFragment(), "Pesanan");
        adapter.addFragment(new HistoryLaporanFragment(), "Laporan");
        viewPager.setAdapter(adapter);

    }

}