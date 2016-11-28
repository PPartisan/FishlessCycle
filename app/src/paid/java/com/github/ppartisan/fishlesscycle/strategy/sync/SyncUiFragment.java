package com.github.ppartisan.fishlesscycle.strategy.sync;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.ui.MainActivity;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public final class SyncUiFragment extends AppCompatDialogFragment implements SyncCallbacks, View.OnClickListener {

    private static final String FREE_PACKAGE_NAME =
            "package:com.github.ppartisan.fishlesscycle.free";

    public static final String TAG = SyncUiFragment.class.getSimpleName();
    private static final String SYNC_COMPLETE_TAG = TAG + ".SYNC_COMPLETE";
    private static final int REQUEST_UNINSTALL_CODE = 12;

    private SyncReceiver mReceiver;
    private boolean mIsSyncComplete, mIsUninstallComplete;

    private ViewGroup mSyncingLayout, mConfirmLayout;
    private CheckBox mUninstallCheckBox;

    public static SyncUiFragment newInstance() {

        Bundle args = new Bundle();

        SyncUiFragment fragment = new SyncUiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        After uninstalling, the Activity and Fragment are relaunched, so check to make sure
        the free version is installed. If it isn't, and we're seeing this Fragment, it means the
        uninstall is complete, so we can skip rendering a UI or launching the Service and go
        straight to onActivityResult() to launch MainActivity.
         */
        mIsUninstallComplete = !SyncUtil.isFreeVersionInstalled(getActivity().getPackageManager());
        if(mIsUninstallComplete) return;
        mReceiver = new SyncReceiver(this);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SyncDialog);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mIsUninstallComplete) return null;

        final View v = inflater.inflate(R.layout.fragment_dialog_sync, container, false);

        if(savedInstanceState != null) {
            mIsSyncComplete = savedInstanceState.getBoolean(SYNC_COMPLETE_TAG);
        }

        final TextView content = (TextView) v.findViewById(R.id.sdf_content);
        content.setText(getString(R.string.sync_content, getString(R.string.app_name)));
        final TextView label = (TextView) v.findViewById(R.id.sdf_label);
        label.setText(getString(R.string.sync_label, getString(R.string.app_name)));

        mSyncingLayout = (ViewGroup) v.findViewById(R.id.sdf_progress_layout);
        mConfirmLayout = (ViewGroup) v.findViewById(R.id.sdf_confirm_layout);

        mSyncingLayout.setVisibility((mIsSyncComplete) ? GONE : VISIBLE);
        mConfirmLayout.setVisibility((mIsSyncComplete) ? VISIBLE : GONE);

        final Button next = (Button) v.findViewById(R.id.sdf_continue);
        next.setOnClickListener(this);

        mUninstallCheckBox = (CheckBox) v.findViewById(R.id.sdf_uninstall_check);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SYNC_COMPLETE_TAG, mIsSyncComplete);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsUninstallComplete) return;
        final IntentFilter filter = new IntentFilter(SyncService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
        getActivity().startService(new Intent(getContext(), SyncService.class));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsUninstallComplete) return;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdf_continue:
                SyncUtil.setSyncNoticeAlreadyDisplayed(getContext(), true);
                final boolean isUninstallChecked = mUninstallCheckBox.isChecked();
                if(isUninstallChecked) {
                    requestUninstall();
                } else {
                    startMainActivity(null);
                }
                break;
        }
    }

    @Override
    public void onSyncComplete(List<SyncManager.DisplayData> data) {
        mIsSyncComplete = true;
        mSyncingLayout.setVisibility(GONE);
        mConfirmLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UNINSTALL_CODE) {
            String message = null;
            switch (resultCode) {
                case RESULT_OK:
                    message = getString(R.string.uninstall_success);
                    break;
                case RESULT_CANCELED:
                    message = getString(R.string.uninstall_cancelled);
                    break;
                case RESULT_FIRST_USER:
                    message = getString(R.string.uninstall_fail);
                    break;
            }
            startMainActivity(message);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().finish();
    }

    private void requestUninstall() {
        final Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse(FREE_PACKAGE_NAME));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, REQUEST_UNINSTALL_CODE);
    }

    private void startMainActivity(String message) {
        final Intent i = new Intent(getContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(message!=null) i.putExtra(MainActivity.UNINSTALL_MESSAGE_EXTRA, message);
        startActivity(i);
        getActivity().finish();
    }

}
