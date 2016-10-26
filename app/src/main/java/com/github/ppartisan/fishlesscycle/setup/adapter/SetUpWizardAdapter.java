package com.github.ppartisan.fishlesscycle.setup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.ppartisan.fishlesscycle.setup.fragment.InfoListFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.IntroductionFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.TankVolumeCalculatorFragment;

public final class SetUpWizardAdapter extends FragmentStatePagerAdapter {

    public SetUpWizardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroductionFragment.newInstance();
            case 1:
                return InfoListFragment.newInstance();
            case 2:
                return TankVolumeCalculatorFragment.newInstance();
            default:
                return IntroductionFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 10;
    }

}
