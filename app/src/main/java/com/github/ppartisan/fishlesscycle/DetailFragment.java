package com.github.ppartisan.fishlesscycle;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.ppartisan.fishlesscycle.adapter.DataAdapter;
import com.github.ppartisan.fishlesscycle.adapter.DataRowCallbacks;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapter;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapterImpl;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.setup.view.HiddenViewManager;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.util.List;

import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

public final class DetailFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener,ViewTreeObserver.OnGlobalLayoutListener, HiddenViewManager.AnimationCallbacks, TextWatcher, DataRowCallbacks {

    private static final String TAG = DetailFragment.class.getCanonicalName();
    public static final String KEY_IDENTIFIER = TAG + ".KEY_IDENTIFIER";
    private static final String FAB_ROTATION_TAG = TAG + ".FAB_ROTATION_TAG";

    private int green500, red500;

    private EditModel mEditModel = new EditModel(false, -1);

    private HiddenViewManager mHiddenViewManager;
    private TextView mHiddenTitle;
    private TextView mAmmoniaLabel, mNitriteLabel, mNitrateLabel;
    private EditText mAmmonia, mNitrite, mNitrate;
    private CoordinatorLayout mCoordinator;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;

    private CombinedChart mChart;
    private ChartAdapter mChartAdapter;

    public static DetailFragment newInstance(long identifier) {

        Bundle args = new Bundle();
        args.putLong(KEY_IDENTIFIER, identifier);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        green500 = ContextCompat.getColor(getContext(), R.color.green_500);
        red500 = ContextCompat.getColor(getContext(), R.color.red_500);

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

        mToolbar = (Toolbar) view.findViewById(R.id.fd_toolbar);
        mToolbar.inflateMenu(R.menu.detail_menu);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.fd_fab);
        mFab.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fd_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new DataAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChart = (CombinedChart) view.findViewById(R.id.fd_chart);
        mChartAdapter = new ChartAdapterImpl(mChart);
        mChartAdapter.setData(null);

        if (savedInstanceState != null) {
            mEditModel = savedInstanceState.getParcelable(EditModel.KEY);
            mHiddenViewManager.onRestoreInstanceState(savedInstanceState);
            mChartAdapter.onRestoreInstanceState(savedInstanceState);
            ViewCompat.setRotation(mFab, savedInstanceState.getFloat(FAB_ROTATION_TAG, 0));
        }

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
        outState.putFloat(FAB_ROTATION_TAG, ViewCompat.getRotation(mFab));
        outState.putParcelable(EditModel.KEY, mEditModel);
    }

    @Override
    public void onClick(View view) {
        mHiddenViewManager.animate();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.md_action_switch_graph) {
            mChartAdapter.switchChartType();
            return true;
        }
        return false;
    }

    private long getIdentifier() {
        return getArguments().getLong(KEY_IDENTIFIER);
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
    public void onExpanded() {}

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
        final int color = (isActivated) ? green500 : red500;
        Log.d(getClass().getCanonicalName(), "Set Color Activated");
        if (view.getCurrentTextColor() != color) {
            Log.d(getClass().getCanonicalName(), "Setting Color to: " + color);
            view.setTextColor(color);
        }
    }

    @Override
    public void onNotesClicked(int position) {

    }

    @Override
    public void onEditClicked(int position) {

        mEditModel.isInEditMode = true;
        mEditModel.index = position;

        final Reading reading = mAdapter.getDataItem(position);

        mHiddenTitle.setText(getString(R.string.df_edit_entry));
        mHiddenViewManager.animateIn();
        mAmmonia.setText(String.valueOf(reading.ammonia));
        mNitrite.setText(String.valueOf(reading.nitrite));
        mNitrate.setText(String.valueOf(reading.nitrate));

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

}
