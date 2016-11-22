package com.github.ppartisan.fishlesscycle.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.BuildConfig;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.util.Api;

public final class AboutDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final Uri GOOGLE_PLUS_URI =
            Uri.parse("https://plus.google.com/communities/110791270173758398263");
    private static final Uri GITHUB_URI =
            Uri.parse("https://github.com/PPartisan/Udacity-CapstoneStageII");

    public static AboutDialogFragment newInstance() {

        Bundle args = new Bundle();

        AboutDialogFragment fragment = new AboutDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_about, container, false);

        final TextView version = (TextView) v.findViewById(R.id.af_version);
        version.setText(getVersionString());

        final ImageButton gPlus = (ImageButton) v.findViewById(R.id.af_google_plus);
        final ImageButton email = (ImageButton) v.findViewById(R.id.af_email);
        final ImageButton github = (ImageButton) v.findViewById(R.id.af_github);
        final ImageButton dismiss = (ImageButton) v.findViewById(R.id.af_dismiss);

        gPlus.setOnClickListener(this);
        email.setOnClickListener(this);
        github.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        return v;

    }

    private String getVersionString() {
        return getString(R.string.fa_version_template, BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(View view) {

        Intent i = null;

        switch (view.getId()) {
            case R.id.af_google_plus:
                i = new Intent(Intent.ACTION_VIEW, GOOGLE_PLUS_URI);
                startActivity(i);
                break;
            case R.id.af_email:
                i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:" + Api.EMAIL));
                i.putExtra(Intent.EXTRA_SUBJECT, "Re: " + getString(R.string.app_name));
                startActivity(i);
                break;
            case R.id.af_github:
                i = new Intent(Intent.ACTION_VIEW, GITHUB_URI);
                startActivity(i);
                break;
            case R.id.af_dismiss:
                dismiss();
                break;
        }

    }

}
