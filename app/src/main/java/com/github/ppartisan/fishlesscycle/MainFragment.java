package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class MainFragment extends Fragment {

    private EmptyRecyclerView mRecyclerView;
    private TanksAdapter mAdapter;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.fm_recycler);
        mRecyclerView.setEmptyView(view.findViewById(R.id.fm_empty_view));

        mAdapter = new TanksAdapter(buildDummyTankData());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;

    }

    private static List<Tank> buildDummyTankData() {
        List<Tank> tanks = new ArrayList<>();
        tanks.add(new Tank("My First Tank", 0));
        tanks.add(new Tank("My Second Tank", 1));
        return tanks;
    }

}
