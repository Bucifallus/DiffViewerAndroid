package com.android.tomac.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by TomaC on 11.09.2017.
 */

public class CustomTextView extends AppCompatTextView implements IScrollNotifier{
    final float scale = getResources().getDisplayMetrics().density;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private IScrollListener scrollListener = null;

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

    @Override
    public void draw(Canvas canvas) {

        int baseline = getBaseline();

        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) this.getLayoutParams();
        int lineCount = getLayout().getLineCount();
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);

        int pixelMargin = (int) (lp.topMargin / scale + 0.5f);

        for (int i = 0; i < lineCount; i++) {
            baseline += getLineHeight();

            rect.top = (getLayout().getLineTop(i));
            rect.left = (int) getLayout().getLineLeft(i);
            rect.right = (int) getLayout().getWidth();
            rect.bottom = (int) (getLayout().getLineBottom(i) - ((i + 1 == lineCount) ? 0 : getLayout().getSpacingAdd()));

            canvas.drawLine(rect.left, ((rect.bottom - rect.top) / 2) + rect.top, rect.right, ((rect.bottom - rect.top) / 2) + rect.top , paint);
            canvas.drawText("" + (i+1), rect.left, baseline, paint);
        }

        super.draw(canvas);
    }
}
