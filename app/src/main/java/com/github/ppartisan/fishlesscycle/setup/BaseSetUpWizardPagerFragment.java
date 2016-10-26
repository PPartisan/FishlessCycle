package com.github.ppartisan.fishlesscycle.setup;

import android.content.Context;
import android.support.v4.app.Fragment;


public class BaseSetUpWizardPagerFragment extends Fragment {

    protected TankModifier mTankModifier;
    protected ColorPackSupplier mColorPackSupplier;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTankModifier = (TankModifier) context;
            mColorPackSupplier = (ColorPackSupplier) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement "
                    + TankModifier.class.getCanonicalName() + " and "
                    + ColorPackSupplier.class.getCanonicalName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTankModifier = null;
        mColorPackSupplier = null;
    }
}
