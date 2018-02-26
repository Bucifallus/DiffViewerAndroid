package com.android.tomac.myapplication;

import android.view.View;

/**
 * Created by TomaC on 12.09.2017.
 */

public interface IScrollListener {
    void onScrollChanged(View syncedScrollView, int l, int t, int oldl,
                         int oldt);
}