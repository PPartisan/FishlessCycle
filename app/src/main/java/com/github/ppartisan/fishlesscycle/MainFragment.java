package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.view.EmptyRecyclerView;

public final class MainFragment extends Fragment {

    private EmptyRecyclerView mRecyclerView;

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

        return view;

    }

}
