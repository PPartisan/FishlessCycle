package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.adapter.DataAdapter;
import com.github.ppartisan.fishlesscycle.model.Data;

import java.util.ArrayList;
import java.util.List;

public final class DetailFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;

    public static DetailFragment newInstance() {

        Bundle args = new Bundle();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fd_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fd_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new DataAdapter(buildDummyData());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    private static List<Data> buildDummyData() {

        List<Data> dataList = new ArrayList<>();

        dataList.add(new Data(0, (long)(System.currentTimeMillis() - Math.random()*10000000), 2, 0, 0));
        dataList.add(new Data(1, (long)(System.currentTimeMillis() - Math.random()*10000000), 5, 0, 0));
        dataList.add(new Data(2, (long)(System.currentTimeMillis() - Math.random()*10000000), 6, 0, 0));
        dataList.add(new Data(3, (long)(System.currentTimeMillis() - Math.random()*10000000), 1, 2, 12));
        dataList.add(new Data(4, (long)(System.currentTimeMillis() - Math.random()*10000000), 0, 4, 22));
        dataList.add(new Data(5, (long)(System.currentTimeMillis() - Math.random()*10000000), 0, 10, 26));
        dataList.add(new Data(6, (long)(System.currentTimeMillis() - Math.random()*10000000), 3, 5, 40));

        return dataList;

    }

}
