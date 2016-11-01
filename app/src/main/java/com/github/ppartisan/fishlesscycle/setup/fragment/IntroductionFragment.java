package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.AdapterSupplier;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;

public final class IntroductionFragment extends BaseSetUpWizardPagerFragment implements View.OnClickListener {

    private AdapterSupplier mAdapterSupplier;

    public static IntroductionFragment newInstance() {

        Bundle args = new Bundle();

        IntroductionFragment fragment = new IntroductionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mAdapterSupplier = (AdapterSupplier) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    AdapterSupplier.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapterSupplier = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_suw_introduction, container, false);

        Button skip = (Button) v.findViewById(R.id.wus_intro_skip);
        skip.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        mAdapterSupplier.setPagerToLastPage();
    }

}
