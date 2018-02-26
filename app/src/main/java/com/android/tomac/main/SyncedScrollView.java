package com.android.tomac.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by TomaC on 12.09.2017.
 */

public class SyncedScrollView extends ScrollView implements IScrollNotifier {
    private IScrollListener scrollListener = null;

    public SyncedScrollView(Context context) {
        super(context);
    }

    public SyncedScrollView(Context context, AttributeSet attrs, IScrollListener scrollListener) {
        super(context, attrs);
        this.scrollListener = scrollListener;
    }

    public SyncedScrollView(Context context, AttributeSet attrs, int defStyleAttr, IScrollListener scrollListener) {
        super(context, attrs, defStyleAttr);
        this.scrollListener = scrollListener;
    }

    public SyncedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollListener != null)
            scrollListener.onScrollChanged(this, l, t, oldl, oldt);
    }
    @Override
    public void setScrollListener(IScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }
    @Override
    public IScrollListener getScrollListener() {
        return scrollListener;
    }

}
