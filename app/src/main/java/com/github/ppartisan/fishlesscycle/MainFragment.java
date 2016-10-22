package com.github.ppartisan.fishlesscycle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class MainFragment extends Fragment implements View.OnClickListener {

    private Callbacks mCallbacks;

    private EmptyRecyclerView mRecyclerView;
    private TanksAdapter mAdapter;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement "
                    + Callbacks.class.getCanonicalName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fm_toolbar);
        //mCallbacks.setCustomActionBar(mToolbar);
        //mCallbacks.getCustomActionBar().setTitle(getString(R.string.app_name));

        mFab = (FloatingActionButton) view.findViewById(R.id.fm_fab);
        mFab.setOnClickListener(this);

        mRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.fm_recycler);
        mRecyclerView.setEmptyView(view.findViewById(R.id.fm_empty_view));

        mAdapter = new TanksAdapter(buildDummyTankData());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private static List<Tank> buildDummyTankData() {
        List<Tank> tanks = new ArrayList<>();
        tanks.add(new Tank("My First Tank", 0));
        tanks.add(new Tank("My Second Tank", 1));
        return tanks;
    }

    @Override
    public void onClick(View view) {
        //ToDo: Add New "Tank" To RecyclerView
        Log.d(getClass().getSimpleName(), "FAB clicked");
    }

    public interface Callbacks {
        void setCustomActionBar(Toolbar toolbar);
        ActionBar getCustomActionBar();
    }

}
