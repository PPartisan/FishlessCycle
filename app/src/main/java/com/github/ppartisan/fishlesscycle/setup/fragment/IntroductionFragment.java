package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;

public final class IntroductionFragment extends BaseSetUpWizardPagerFragment {

    public static IntroductionFragment newInstance() {

        Bundle args = new Bundle();

        IntroductionFragment fragment = new IntroductionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_suw_introduction, container, false);
//        v.setBackgroundColor(getBackgroundColor());
//
//        ImageView imageView = (ImageView) v.findViewById(R.id.introduction_wusf_image);
//        imageView.setColorFilter(getDarkBackgroundColor(), PorterDuff.Mode.MULTIPLY);
//
//        Picasso.with(getContext()).load(R.drawable.tank_white).into(imageView);

        return v;
    }

}
