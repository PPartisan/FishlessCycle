package com.github.ppartisan.fishlesscycle.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class BaseSetUpWizardPagerFragment extends Fragment {

    protected TankModifier mTankModifier;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTankModifier = (TankModifier) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement "
                    + TankModifier.class.getCanonicalName());
        }
    }

}
