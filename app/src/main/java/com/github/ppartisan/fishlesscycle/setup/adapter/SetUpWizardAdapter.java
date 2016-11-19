package com.github.ppartisan.fishlesscycle.setup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.setup.TankBuilderObserver;
import com.github.ppartisan.fishlesscycle.setup.fragment.AmmoniaDosageFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.ConfirmationFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.FirstReadingFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.InfoListFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.IntroductionFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.TankSetUpFragment;
import com.github.ppartisan.fishlesscycle.setup.fragment.TankVolumeCalculatorFragment;

import java.util.HashSet;
import java.util.Set;

public final class SetUpWizardAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_PAGES = 7;

    private final Set<TankBuilderObserver> mTankBuilderObservers = new HashSet<>(NUM_PAGES);

    public SetUpWizardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        BaseSetUpWizardPagerFragment f;

        switch (position) {
            case 0:
                f = IntroductionFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 1:
                f = InfoListFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 2:
                f = TankVolumeCalculatorFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 3:
                f = AmmoniaDosageFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 4:
                f = TankSetUpFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 5:
                f = FirstReadingFragment.newInstance();
                mTankBuilderObservers.add(f);
                return f;
            case 6:
                f = ConfirmationFragment.newInstance(false);
                mTankBuilderObservers.add(f);
                return f;
        }

        throw new IllegalStateException("Number of fragments must be no greater than " + NUM_PAGES);

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    public void notifyTankBuilderUpdated(Tank.Builder builder) {
        for (TankBuilderObserver observer : mTankBuilderObservers) {
            observer.onTankModified(builder);
        }
    }

}
