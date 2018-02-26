package com.android.tomac.main;

/**
 * Created by TomaC on 12.09.2017.
 */

public interface IScrollNotifier {
    public void setScrollListener(IScrollListener scrollListener);

    public IScrollListener getScrollListener();
}
