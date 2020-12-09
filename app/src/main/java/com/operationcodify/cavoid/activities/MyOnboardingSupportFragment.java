package com.operationcodify.cavoid.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.leanback.app.OnboardingSupportFragment;

public class MyOnboardingSupportFragment extends OnboardingSupportFragment {
    static final String preferences = "com.operationcodify.cavoid.activities_preferences";

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        if (pageIndex == 1) {
            return "WARNING!";
        }
        else if (pageIndex == 2) {
            return "Privacy";
        }
        else {
            return "Sources";
        }
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        if (pageIndex == 1) {
            return "This data doesn't imply an encounter with covid-19.(It doesn't provide contanct tracing.)";
        }
        else if (pageIndex == 2) {
            return "Location is stored in the background. This information is never shared, not even with us. It never leaves your phone.";
        }
        else {
            return "All data is sourced from the NYT Covid Tracking Project (see Settings -> App Info) for more information.";
        }
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    protected void onFinishFragment() {
        // User has seen OnboardingSupportFragment, so mark our SharedPreferences
        // flag as completed so that we don't show our OnboardingSupportFragment
        // the next time the user launches the app.
        super.onFinishFragment();
    }
}