package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils.VolumeUnit;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.TankUtils.getTankVolumeInLitresAsUserUnitPreference;
import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

public final class FirstReadingFragment extends BaseSetUpWizardPagerFragment {

    private @VolumeUnit int volumeUnit;
    private boolean isDosageMetric = false;

    private final DecimalFormat mFormat = new DecimalFormat("##.#");

    private TextView mContent, mControlHeader, mFirstReadingHeader;
    private EditText mControlAmmonia, mControlNitrite, mControlNitrate;
    private EditText mReadingAmmonia, mReadingNitrite, mReadingNitrate;

    public static FirstReadingFragment newInstance() {

        Bundle args = new Bundle();

        FirstReadingFragment fragment = new FirstReadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
        isDosageMetric = PreferenceUtils.isDosageMetric(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_suw_first_reading, container, false);

        mContent = (TextView) v.findViewById(R.id.rf_suwf_content);
        mControlHeader = (TextView) v.findViewById(R.id.rf_suwf_control_header);
        mFirstReadingHeader = (TextView) v.findViewById(R.id.rf_suwf_first_reading_header);

        mControlAmmonia = (EditText) v.findViewById(R.id.rf_suwf_control_ammonia);
        mControlNitrite = (EditText) v.findViewById(R.id.rf_suwf_control_nitrite);
        mControlNitrate = (EditText) v.findViewById(R.id.rf_suwf_control_nitrate);
        mReadingAmmonia = (EditText) v.findViewById(R.id.rf_suwf_reading_ammonia);
        mReadingNitrite = (EditText) v.findViewById(R.id.rf_suwf_reading_nitrite);
        mReadingNitrate = (EditText) v.findViewById(R.id.rf_suwf_reading_nitrate);

        mContent.setText(getContentString());
        mControlHeader.setText(getHeaderText(R.string.wus_frf_control_header));
        mFirstReadingHeader.setText(getHeaderText(R.string.wus_frf_first_reading_header));

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        final ControlTextWatcher controlWatcher = new ControlTextWatcher();
        mControlAmmonia.addTextChangedListener(controlWatcher);
        mControlNitrite.addTextChangedListener(controlWatcher);
        mControlNitrate.addTextChangedListener(controlWatcher);
        final FirstReadingTextWatcher firstReadingWatcher = new FirstReadingTextWatcher();
        mReadingAmmonia.addTextChangedListener(firstReadingWatcher);
        mReadingNitrite.addTextChangedListener(firstReadingWatcher);
        mReadingNitrate.addTextChangedListener(firstReadingWatcher);
    }

    private String getContentString() {

        if (getTankBuilderSupplier() == null) return null;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
        final AmmoniaDosage ammoniaDose = builder.getAmmoniaDosage();

        final int templateResId = R.string.wus_frf_content;

        final int volume = (int) Math.ceil(
                getTankVolumeInLitresAsUserUnitPreference(builder.getVolumeInLitres(), volumeUnit)
        );
        final String volumeString = getVolumeFormattedUnitString(volume);

        final String andHeater = (builder.isHeated())
                ? " " + getString(R.string.wus_frf_content_and_heater)
                : "";

        final float dosage = ammoniaDose.dosage;

        return getString(templateResId, volume, volumeString, andHeater, mFormat.format(dosage));

    }

    private String getHeaderText(int templateResId) {
        final String[] doseUnitOptions = getResources().getStringArray(R.array.dose_unit_options);
        final int doseUnitIndex = (isDosageMetric) ? 0 : 1;
        return getString(templateResId, doseUnitOptions[doseUnitIndex]);
    }

    private String getVolumeFormattedUnitString(int volume) {

        int resId;
        String quantityString = null;

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                resId = R.plurals.litres;
                quantityString = getResources().getQuantityString(resId, volume).toLowerCase();
                break;
            case PreferenceUtils.IMPERIAL:
                resId = R.plurals.imperial_gallons;
                quantityString = getResources().getQuantityString(resId, volume).toLowerCase();
                break;
            case PreferenceUtils.US:
                resId = R.plurals.us_gallons;
                quantityString = getResources().getQuantityString(resId, volume);
                break;
        }

        return quantityString;

    }

    @Override
    public void onTankModified(Tank.Builder builder) {
        if (mContent != null) mContent.setText(getContentString());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String volumeUnitPrefsKey = getString(R.string.pref_volume_unit_type_key);
        final String doseUnitPrefsKey = getString(R.string.pref_dosage_unit_type_key);

        if(volumeUnitPrefsKey.equals(s)) {
            mContent.setText(getContentString());
        }

        if(doseUnitPrefsKey.equals(s)) {
            mControlHeader.setText(getHeaderText(R.string.wus_frf_control_header));
            mFirstReadingHeader.setText(getHeaderText(R.string.wus_frf_first_reading_header));
        }

    }

    private final class ControlTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (getTankBuilderSupplier() == null) return;

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

            if(!isTextWidgetInGroupEmpty(mControlAmmonia, mControlNitrite, mControlNitrate)) {
                final long time = System.currentTimeMillis();
                final int ammonia = (int) ViewUtils.getParsedFloatFromTextWidget(mControlAmmonia);
                final int nitrite = (int) ViewUtils.getParsedFloatFromTextWidget(mControlNitrite);
                final int nitrate = (int) ViewUtils.getParsedFloatFromTextWidget(mControlNitrate);
                final Reading controlReading =
                        new Reading(builder.getIdentifier(), time, ammonia, nitrite, nitrate, true);
                builder.setControlReading(controlReading);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

    private final class FirstReadingTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (getTankBuilderSupplier() == null) return;

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

            if(!isTextWidgetInGroupEmpty(mReadingAmmonia, mReadingNitrite, mReadingNitrate)) {
                final long time = System.currentTimeMillis();
                final int ammonia = (int) ViewUtils.getParsedFloatFromTextWidget(mReadingAmmonia);
                final int nitrite = (int) ViewUtils.getParsedFloatFromTextWidget(mReadingNitrite);
                final int nitrate = (int) ViewUtils.getParsedFloatFromTextWidget(mReadingNitrate);
                final Reading controlReading =
                        new Reading(builder.getIdentifier(), time, ammonia, nitrite, nitrate, false);
                builder.setLastReading(controlReading);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

    private boolean isTextWidgetInGroupEmpty(TextView... views) {
        int counter = 0;

        while (counter < views.length) {
            if (isTextWidgetEmpty(views[0])) return true;
            counter++;
        }

        return false;

    }

}
