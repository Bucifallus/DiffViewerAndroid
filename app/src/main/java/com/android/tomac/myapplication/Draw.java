package com.android.tomac.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by TomaC on 12.09.2017.
 */

public class Draw extends View {
    TextView relativeTextViewer = null;
    public void setRelativeTextViewer(TextView relativeTextViewer) {
        this.relativeTextViewer = relativeTextViewer;
    }

    /** Stores data about single circle */
    private static class RectArea {
        int startX;
        int startY;
        int stopX;
        int stopY;

        public RectArea(int startX, int startY, int stopX, int stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }
    }

    RectArea rect;

    Paint paint = new Paint();

    public Draw(Context context) {
        super(context);

        init(context);
    }

    public Draw(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Draw(Context context, @Nullable AttributeSet attrs, Paint paint) {
        super(context, attrs);
        this.paint = paint;

        init(context);
    }

    public Draw(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Paint paint) {
        super(context, attrs, defStyleAttr);
        this.paint = paint;

        init(context);
    }

    private void init(Context context) {

        rect = new RectArea(2, 2, 2, 2);

    }

/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Compute the height required to render the view
        // Assume Width will always be MATCH_PARENT.
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 3000 + 50; // Since 3000 is bottom of last Rect to be drawn added and 50 for padding.
        setMeasuredDimension(width, height);
    }
*/

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

       // CircleArea touchedCircle;
        int xTouch, left, top, width,height, visible, visibleLinesCount;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                //clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
               /* touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);*/


                if (relativeTextViewer != null) {

                    relativeTextViewer.scrollTo(0, getScaledY(yTouch, 0, getClientHeight() , 0, relativeTextViewer.getLayout().getHeight()));
                }
                //cTextView2.scrollTo(0, yTouch);

                left = 2;
                top = 2 + (int)(getFirstVisibleLineIndex() / getnumberOfLinesPerPixel());
                width = getClientWidth() + (8 - 5) * 2;

                visibleLinesCount = getLastLineIndex() - getFirstVisibleLineIndex();

                height =  3 * (int)Math.max((visibleLinesCount / getnumberOfLinesPerPixel()), 2);

                rect = new RectArea(0, yTouch, width, yTouch + height);

                invalidate();
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    if (relativeTextViewer != null) {

                        relativeTextViewer.scrollTo(0, getScaledY(yTouch, 0, getClientHeight() , 0, relativeTextViewer.getLayout().getHeight()));
                    }

                    left = 2;
                    top = 2 + (int)(getFirstVisibleLineIndex() / getnumberOfLinesPerPixel());
                    width = getClientWidth() + (8 - 5) * 2;

                    visibleLinesCount = getLastLineIndex() - getFirstVisibleLineIndex();

                    height = 3 * (int)Math.max((visibleLinesCount / getnumberOfLinesPerPixel()), 2);

                    rect = new RectArea(0, yTouch, width, yTouch + height);

                }
                invalidate();
                handled = true;
                break;
           /* case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;
*/
            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    int getScaledY(int value, int currX, int currY, int destX, int destY) {
        int scale = scale = (destY - destX) / (currY - currX);
        return (value - currX) * scale + destX;
    }

    final float[] getPointerCoords(TextView view, MotionEvent e)
    {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();

        view.getMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBorder(canvas);
        drawMovingRect(canvas);
     /*   paint.setColor(Color.GREEN);
        canvas.drawRect(30, 30, 90, 200, paint);
        paint.setColor(Color.BLUE);

        canvas.drawLine(100, 20, 100, 1900, paint);

        paint.setColor(Color.GREEN);
        canvas.drawRect(200, 2000, 400, 3000, paint);*/

    }

    public int getClientHeight()
    {
        if (relativeTextViewer == null)
            return this.getHeight();
        else
            return 3 * (Math.min(relativeTextViewer.getLineCount(), this.getHeight()));

    }

    public int getClientWidth()
    {
       return this.getWidth();
    }

    private void drawBorder(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        canvas.drawRect(2,2, getClientWidth(), getClientHeight(), paint);

        for (int i = 0; i < 10; i++) {
            paint.setColor(Color.RED);
            canvas.drawLine(getClientWidth() / 2, 30 * i, getClientWidth(), 30 * i, paint);

            paint.setColor(Color.GREEN);
            canvas.drawLine(2, 35 * i, getClientWidth() / 2, 35 * i, paint);

            paint.setColor(Color.BLUE);
            canvas.drawLine(2, 27 * i, getClientWidth(), 27 * i, paint);
        }
    }

    private void drawMovingRect(Canvas canvas) {

        int left = 2;
        int top = 2 + (int)(getFirstVisibleLineIndex() / getnumberOfLinesPerPixel());
        int width = getClientWidth() + (8 - 5) * 2;

        int visibleLinesCount = getLastLineIndex() - getFirstVisibleLineIndex();

        int height = (int)Math.max((visibleLinesCount / getnumberOfLinesPerPixel()), 2);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(rect.startX, rect.startY, rect.stopX, rect.stopY, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect.startX, rect.startY, rect.stopX, rect.stopY, paint);
    }

    public int getLastLineIndex() {
        int height = relativeTextViewer.getHeight();
        int scrollY = relativeTextViewer.getScrollY();
        Layout layout = relativeTextViewer.getLayout();
        if (layout != null) {
            return layout.getLineForVertical(scrollY + height);
        }
        return -1;
    }

    public int getFirstVisibleLineIndex()
    {
        int scrollY = relativeTextViewer.getScrollY();
        Layout layout = relativeTextViewer.getLayout();
        if (layout != null) {
            return layout.getLineForVertical(scrollY);
        }

        return -1;
       /* return relativeTextViewer != null ? RelativeTextViewer.ScintillaCtrl.Lines.FirstVisibleIndex : -1; }
        set
        {
            if (RelativeTextViewer != null && RelativeTextViewer.FirstVisibleLineIndex != value)
            {
                RelativeTextViewer.FirstVisibleLineIndex = value;
            }
        }*/
    }

    private double getnumberOfLinesPerPixel()
    {
        if (relativeTextViewer == null)
            return 1;
        else
            return Math.max((double)relativeTextViewer.getLineCount() / getClientHeight(), 1);
    }
}
