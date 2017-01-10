package com.stydydemo.sunny.autoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class PagerView extends ViewGroup {

    private float startPos = 0;
    private VelocityTracker velocityTracker;
    private int currentScreen;
    private Scroller scroller;

    public PagerView(Context context, AttributeSet attrs) {
        super(context, attrs);  //attrs的意义
        scroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int count = getChildCount();
            int leftX = 0;
            for (int i = 0; i < count; i++) {
                View childView = getChildAt(i); //拿到每一个view，大小与父控件一样大
                int width = childView.getMeasuredWidth();
                childView.layout(leftX, 0, leftX + width, childView.getMeasuredHeight());
                leftX += width;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //父view要给子view的东西;不同尺寸，三个意义，不同意思
        int count = getChildCount(); //几个控件
//        int width = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i); //拿到每一个view，大小与父控件一样大
//            int xxx = MeasureSpec.makeMeasureSpec()
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(event);
                }
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                startPos = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                float deltaX = startPos - event.getX();
                startPos = event.getX();
                scrollBy((int) deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                float vX = velocityTracker.getXVelocity();
                velocityTracker.clear();
                velocityTracker = null;
                if (vX > 600) {
                    scrollToScreen(currentScreen - 1);
                } else if (vX < -600) {
                    scrollToScreen(currentScreen + 1);
                } else {
                    scrollToScreen(currentScreen);
                }
                break;
        }
        return true;
    }

    @Override //类似onDrow
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) { //scroller有新的数据
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }

    private void scrollToScreen(int screen) {
        if (screen >= 0 && screen < getChildCount()) {
            currentScreen = screen;
            int distance = screen * getWidth() - getScrollX();
            scroller.startScroll(getScrollX(), 0, distance, 0, Math.abs(distance));
            postInvalidate();
        } else if (screen < 0) {
            scrollToScreen(0);
        } else {
            scrollToScreen(getChildCount() - 1);
        }
    }
}
