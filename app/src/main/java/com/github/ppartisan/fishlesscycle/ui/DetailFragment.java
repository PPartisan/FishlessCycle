package com.github.ppartisan.fishlesscycle.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.adapter.DataRowCallbacks;
import com.github.ppartisan.fishlesscycle.adapter.ReadingsAdapter;
import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapter;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapterImpl;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.view.HiddenViewManager;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.github.ppartisan.fishlesscycle.view.DosagePopUpWindow;

import java.util.List;

import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

public final class DetailFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener,ViewTreeObserver.OnGlobalLayoutListener, HiddenViewManager.AnimationCallbacks, TextWatcher, DataRowCallbacks {
    private static final String TAG = DetailFragment.class.getSimpleName();
    public static final String KEY_IDENTIFIER = TAG + ".KEY_IDENTIFIER";
    public static final String KEY_NAME = TAG + ".KEY_NAME";
    public static final String KEY_DOSAGE = TAG + ".KEY_DOSAGE";
    private static final String FAB_ROTATION_TAG = TAG + ".FAB_ROTATION_TAG";

    private static final float RECYCLER_ELEVATION = 4f;

    private int green300, red300;

    private EditModel mEditModel = new EditModel(false, -1);

    private HiddenViewManager mHiddenViewManager;
    private TextView mHiddenTitle;
    private TextView mAmmoniaLabel, mNitriteLabel, mNitrateLabel;
    private EditText mAmmonia, mNitrite, mNitrate;
    private CoordinatorLayout mCoordinator;

    private FloatingActionButton mFab;

    private ReadingsAdapter mAdapter;

    private ChartAdapter mChartAdapter;

    private DosagePopUpWindow mDosagePopUp;

    public static DetailFragment newInstance(String name, long identifier, float dosage) {

        Bundle args = new Bundle();
        args.putLong(KEY_IDENTIFIER, identifier);
        args.putString(KEY_NAME, name);
        args.putFloat(KEY_DOSAGE, dosage);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_detail, container, false);

        final String projTransName = TanksAdapter.TRANSITION_NAME_BASE + getIdentifier();

        green300 = ContextCompat.getColor(getContext(), R.color.green_300);
        red300 = ContextCompat.getColor(getContext(), android.R.color.white);

        mCoordinator = (CoordinatorLayout) view.findViewById(R.id.fd_coordinator);

        final View hiddenView = view.findViewById(R.id.fd_hidden_card);
        hiddenView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mHiddenViewManager = new HiddenViewManager(hiddenView);
        mHiddenViewManager.setAnimationCallbacks(this);

        mHiddenTitle = (TextView) view.findViewById(R.id.fd_hidden_title);

        mAmmoniaLabel = (TextView) view.findViewById(R.id.fd_ammonia_label);
        mNitriteLabel = (TextView) view.findViewById(R.id.fd_nitrite_label);
        mNitrateLabel = (TextView) view.findViewById(R.id.fd_nitrate_label);

        mAmmonia = (EditText) view.findViewById(R.id.fd_ammonia_entry);
        mNitrite = (EditText) view.findViewById(R.id.fd_nitrite_entry);
        mNitrate = (EditText) view.findViewById(R.id.fd_nitrate_entry);

        final ImageButton ammoniaChart = (ImageButton) view.findViewById(R.id.fd_ammonia_chart);
        final ImageButton nitriteChart = (ImageButton) view.findViewById(R.id.fd_nitrite_chart);
        final ImageButton nitrateChart = (ImageButton) view.findViewById(R.id.fd_nitrate_chart);

        ammoniaChart.setOnClickListener(this);
        nitriteChart.setOnClickListener(this);
        nitrateChart.setOnClickListener(this);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fd_toolbar);
        toolbar.inflateMenu(R.menu.detail_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.df_acc_nav_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(getActivity());
            }
        });
        toolbar.setTitle(getName());
        ViewCompat.setTransitionName(ViewUtils.getToolbarTitleTextView(toolbar), projTransName);

        mFab = (FloatingActionButton) view.findViewById(R.id.fd_fab);
        mFab.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fd_recycler_view);
        mAdapter = new ReadingsAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);

        ViewCompat.setElevation(recyclerView, RECYCLER_ELEVATION);

        CombinedChart chart = (CombinedChart) view.findViewById(R.id.fd_chart);
        mChartAdapter = new ChartAdapterImpl(chart);
        mChartAdapter.setData(null);

        final View popupAnchor = view.findViewById(R.id.md_action_dosage);
        mDosagePopUp = new DosagePopUpWindow(popupAnchor, getName(), getDosage());

        if (savedInstanceState != null) {
            mEditModel = savedInstanceState.getParcelable(EditModel.KEY);
            mHiddenViewManager.onRestoreInstanceState(savedInstanceState);
            mChartAdapter.onRestoreInstanceState(savedInstanceState);
            mDosagePopUp.onRestoreInstanceState(savedInstanceState);
            ViewCompat.setRotation(mFab, savedInstanceState.getFloat(FAB_ROTATION_TAG, 0));
        }

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(getActivity());
                return true;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmmonia.addTextChangedListener(this);
        mNitrite.addTextChangedListener(this);
        mNitrate.addTextChangedListener(this);
        setGroupLabelTextColorsActivated();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChartAdapter.onSaveInstanceState(outState);
        mHiddenViewManager.onSaveInstanceState(outState);
        mDosagePopUp.onSaveInstanceState(outState);
        outState.putFloat(FAB_ROTATION_TAG, ViewCompat.getRotation(mFab));
        outState.putParcelable(EditModel.KEY, mEditModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDosagePopUp.dismiss();
    }

    @Override
    public void onClick(View view) {

        ApiColorChartDialogFragment f;

        switch (view.getId()) {
            case R.id.fd_fab:
                mHiddenViewManager.animate();
                break;
            case R.id.fd_ammonia_chart:
                showApiColorChartFragment(ApiColorChartEntry.AMMONIA);
                break;
            case R.id.fd_nitrite_chart:
                showApiColorChartFragment(ApiColorChartEntry.NITRITE);
                break;
            case R.id.fd_nitrate_chart:
                showApiColorChartFragment(ApiColorChartEntry.NITRATE);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.md_action_dosage:
                mDosagePopUp.show();
                return true;
            case R.id.md_action_switch_graph:
                mChartAdapter.switchChartType();
                return true;
        }

        return false;
    }

    private long getIdentifier() {
        return getArguments().getLong(KEY_IDENTIFIER);
    }

    private String getName() {
        return getArguments().getString(KEY_NAME);
    }

    private float getDosage() {
        return getArguments().getFloat(KEY_DOSAGE);
    }

    public void updateReadings(List<Reading> readings) {
        mAdapter.setDataItems(readings);
        mAdapter.notifyDataSetChanged();
        mChartAdapter.setData(readings);
    }

    //Without below call FAB renders near the middle of screen until clicked
    @Override
    public void onGlobalLayout() {
        final boolean isExpanded = mHiddenViewManager.isExpanded();
        final View v = mHiddenViewManager.getHiddenView();
        if (!isExpanded) {
            v.setY(mCoordinator.getHeight());
            mCoordinator.dispatchDependentViewsChanged(mFab);
        }
        v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onAnimationUpdate(float per) {
        ViewCompat.setRotation(mFab, (45*per)+45);
    }

    @Override
    public void onExpanded() {
        if(!mFab.isShown()) mFab.show();
    }

    @Override
    public void onCollapsed() {
        if(!isTextWidgetEmpty(mAmmonia) && !isTextWidgetEmpty(mNitrite) && !isTextWidgetEmpty(mNitrate)) {
            final int ammonia = (int)ViewUtils.getParsedFloatFromTextWidget(mAmmonia);
            final int nitrite = (int)ViewUtils.getParsedFloatFromTextWidget(mNitrite);
            final int nitrate = (int)ViewUtils.getParsedFloatFromTextWidget(mNitrate);
            if (!mEditModel.isInEditMode) {
                final long date = System.currentTimeMillis();
                Reading reading = new Reading(getIdentifier(), date, ammonia, nitrite, nitrate, false);
                getContext().getContentResolver().insert(
                        Contract.ReadingEntry.CONTENT_URI, ReadingUtils.toContentValues(reading)
                );
            } else {
                final Reading reading = mAdapter.getDataItem(mEditModel.index);
                final long date = reading.date;
                final String where = Contract.ReadingEntry.COLUMN_DATE+"=?";
                final String[] whereArgs = new String[] { String.valueOf(date) };
                final ContentValues cv = new ContentValues(3);
                cv.put(Contract.ReadingEntry.COLUMN_AMMONIA, ammonia);
                cv.put(Contract.ReadingEntry.COLUMN_NITRITE, nitrite);
                cv.put(Contract.ReadingEntry.COLUMN_NITRATE, nitrate);
                getContext().getContentResolver().update(
                        Contract.ReadingEntry.CONTENT_URI, cv, where, whereArgs
                );
                mEditModel.isInEditMode = false;
            }
            mAmmonia.getText().clear();
            mNitrite.getText().clear();
            mNitrate.getText().clear();
        }
        mHiddenTitle.setText(getString(R.string.df_add_new_entry));
        ViewUtils.collapseSoftInput(getActivity());
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setGroupLabelTextColorsActivated();
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    private void setGroupLabelTextColorsActivated() {
        setLabelTextColorActivated(mAmmoniaLabel, !isTextWidgetEmpty(mAmmonia));
        setLabelTextColorActivated(mNitriteLabel, !isTextWidgetEmpty(mNitrite));
        setLabelTextColorActivated(mNitrateLabel, !isTextWidgetEmpty(mNitrate));
    }

    private void setLabelTextColorActivated(TextView view, boolean isActivated) {
        final int color = (isActivated) ? green300 : red300;
        if (view.getCurrentTextColor() != color) {
            view.setTextColor(color);
        }
    }

    @Override
    public void onNotesClicked(int position) {
        buildNotesDialog(position).show();
    }

    @Override
    public void onEditClicked(int position) {

        mEditModel.isInEditMode = true;
        mEditModel.index = position;

        final Reading reading = mAdapter.getDataItem(position);

        mHiddenTitle.setText(getString(R.string.df_edit_entry));
        mAmmonia.setText(String.valueOf((int)reading.ammonia));
        mNitrite.setText(String.valueOf((int)reading.nitrite));
        mNitrate.setText(String.valueOf((int)reading.nitrate));
        mHiddenViewManager.animateIn();

    }

    @Override
    public void onDeleteClicked(int position) {

        final Reading reading= mAdapter.getDataItem(position);

        final String where = Contract.ReadingEntry.COLUMN_DATE+"=?";
        final String[] whereArgs = new String[] { String.valueOf(reading.date) };
        getContext().getContentResolver().delete(
                Contract.ReadingEntry.CONTENT_URI, where, whereArgs
        );

        buildReadingDeletedSnackBar(reading).show();

    }

    private Snackbar buildReadingDeletedSnackBar(final Reading reading) {
        @SuppressWarnings("ConstantConditions")
        Snackbar snackbar = Snackbar.make(
                getView(),
                getString(R.string.deleted_template, ReadingUtils.getReadableDateString(reading.date)),
                Snackbar.LENGTH_LONG
        );

        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().getContentResolver().insert(
                        Contract.ReadingEntry.CONTENT_URI, ReadingUtils.toContentValues(reading)
                );
            }
        });

        return snackbar;
    }

    private AlertDialog buildNotesDialog(final int index) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NoteDialogTheme);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_dialog_notes, null);
        builder.setView(view);

        final Reading reading = mAdapter.getDataItem(index);
        final EditText input = (EditText) view.findViewById(R.id.nda_input);
        input.setText(reading.getNote());

        builder.setTitle(R.string.df_notes_title);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String note = input.getText().toString();
                reading.setNote(note);

                final ContentValues cv = new ContentValues(1);
                cv.put(Contract.ReadingEntry.COLUMN_NOTES, note);
                final String where = Contract.ReadingEntry.COLUMN_DATE+"=?";
                final String[] whereArgs = new String[] { String.valueOf(reading.date) };

                getContext().getContentResolver().update(
                        Contract.ReadingEntry.CONTENT_URI, cv, where, whereArgs
                );
            }
        });
        builder.setNegativeButton(R.string.dismiss, null);

        return builder.create();

    }

    private static class EditModel implements Parcelable {

        static final String KEY = EditModel.class.getSimpleName() + ".KEY";

        boolean isInEditMode = true;
        int index = -1;

        private EditModel(boolean isInEditMode, int index) {
            this.isInEditMode = isInEditMode;
            this.index = index;
        }

        EditModel(Parcel in) {
            isInEditMode = in.readByte() != 0;
            index = in.readInt();
        }

        public static final Creator<EditModel> CREATOR = new Creator<EditModel>() {
            @Override
            public EditModel createFromParcel(Parcel in) {
                return new EditModel(in);
            }

            @Override
            public EditModel[] newArray(int size) {
                return new EditModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte((byte) (isInEditMode ? 1 : 0));
            parcel.writeInt(index);
        }
    }

    private void showApiColorChartFragment(@ApiColorChartEntry.Categories int category) {
        final ApiColorChartDialogFragment fragment =
                ApiColorChartDialogFragment.newInstance(category);
        fragment.show(getFragmentManager(), fragment.getClass().getSimpleName());
    }

}
