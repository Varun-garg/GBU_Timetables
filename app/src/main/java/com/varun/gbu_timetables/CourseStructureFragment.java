package com.varun.gbu_timetables;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CourseStructureFragment extends Fragment {

    public CourseStructureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_course_structure, container, false);
        WebView webView = RootView.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/ict_course_structure.htm");

        return RootView;
    }
}
