package com.github.ppartisan.fishlesscycle.setup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.ppartisan.fishlesscycle.setup.fragment.IntroductionFragment;

public final class SetUpWizardAdapter extends FragmentStatePagerAdapter {

    public SetUpWizardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroductionFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 10;
    }

}
