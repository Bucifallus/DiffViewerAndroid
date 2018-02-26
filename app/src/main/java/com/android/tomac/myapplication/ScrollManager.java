package com.android.tomac.myapplication;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by TomaC on 12.09.2017.
 */

public class ScrollManager implements IScrollListener {
    private static final int SCROLL_HORIZONTAL = 1;
    private static final int SCROLL_VERTICAL = 2;

    private ArrayList<IScrollNotifier> clients = new ArrayList<IScrollNotifier>(4);

    private volatile boolean isSyncing = false;
    private int scrollType = SCROLL_HORIZONTAL;

    public void addScrollClient(IScrollNotifier client) {
        clients.add(client);
        client.setScrollListener(this);
    }

    // TODO fix dependency on all views being of equal horizontal/ vertical
    // dimensions
    @Override
    public void onScrollChanged(View sender, int l, int t, int oldl, int oldt) {
        // avoid notifications while scroll bars are being synchronized
        if (isSyncing) {
            return;
        }

        isSyncing = true;

        // remember scroll type
        if (l != oldl) {
            scrollType = SCROLL_HORIZONTAL;
        } else if (t != oldt) {
            scrollType = SCROLL_VERTICAL;
        } else {
            // not sure why this should happen
            isSyncing = false;
            return;
        }

        // update clients
        for (IScrollNotifier client : clients) {
            View view = (View) client;
            // don't update sender
            if (view == sender) {
                continue;
            }

            // scroll relevant views only
            // TODO Add support for horizontal ListViews - currently weird things happen when ListView is being scrolled horizontally
/*            if ((scrollType == SCROLL_HORIZONTAL && view instanceof TextView)) {
                view.scrollTo(l, t);
            }*/
            view.scrollTo(l, t);
        }

        isSyncing = false;
    }
}
