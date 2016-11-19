package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.setup.adapter.InfoRecyclerAdapter;
import com.github.ppartisan.fishlesscycle.setup.model.InfoItem;
import com.github.ppartisan.fishlesscycle.setup.model.InfoItem.ParentBuilder;

import java.util.ArrayList;
import java.util.List;

public class InfoListFragment extends BaseSetUpWizardPagerFragment implements InfoRecyclerAdapter.OnItemClickListener {

    private List<InfoItem> mItems;

    private InfoRecyclerAdapter mAdapter;

    public static InfoListFragment newInstance() {

        Bundle args = new Bundle();

        InfoListFragment fragment = new InfoListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mItems = buildInfoItems(getResources());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_suw_info_list, container, false);

        final RecyclerView recycler = (RecyclerView) v.findViewById(R.id.li_swuf_recycler);
        mAdapter = new InfoRecyclerAdapter(mItems, this);
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setNestedScrollingEnabled(false);

        return v;
    }

    private static List<InfoItem> buildInfoItems(Resources res) {

        final String[] parents = res.getStringArray(R.array.info_item_parents);
        final String[] parentOpt = res.getStringArray(R.array.info_item_parents_optional);
        final String[] children = res.getStringArray(R.array.info_item_children);
        final String[] childrenOpt = res.getStringArray(R.array.info_item_child_optional);

        final String[] headers = res.getStringArray(R.array.info_item_headers);

        List<InfoItem> items = new ArrayList<>();
        items.add(new InfoItem.Header(headers[0]));
        items.add(new ParentBuilder(parents[0]).build());
        items.add(new ParentBuilder(parents[1]).setChild(children[0]).build());
        items.add(new ParentBuilder(parents[2]).setChild(children[1]).build());
        items.add(new ParentBuilder(parents[3]).setChild(children[2]).build());
        items.add(new ParentBuilder(parents[4]).setChild(children[3]).build());
        items.add(new ParentBuilder(parents[5]).build());
        items.add(new ParentBuilder(parents[6]).setChild(children[4]).build());
        items.add(new ParentBuilder(parents[7]).build());
        items.add(new ParentBuilder(parents[8]).setChild(children[5]).build());
        items.add(new InfoItem.Header(headers[1]));
        items.add(new ParentBuilder(parentOpt[0]).setChild(childrenOpt[0]).build());
        items.add(new ParentBuilder(parentOpt[1]).setChild(childrenOpt[1]).build());
        items.add(new ParentBuilder(parentOpt[2]).build());
        items.add(new ParentBuilder(parentOpt[3]).setChild(childrenOpt[2]).build());

        return items;
    }

    @Override
    public void onItemClick(int adapterPosition) {
        final InfoItem.Child child = mAdapter.getInfoItem(adapterPosition).getChild();
        if (!child.isVisible()) {
            mAdapter.addItem(adapterPosition + 1, child);
            child.setVisible(true);
        } else {
            mAdapter.removeItem(adapterPosition + 1);
            child.setVisible(false);
        }
    }
}
