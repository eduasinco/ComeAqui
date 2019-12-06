package com.example.eduardorodriguez.comeaqui.behaviors;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;

public class SlideHideBehavior extends CoordinatorLayout.Behavior<View> {

    private int mInitialOffset;
    private int cardDragDistance;
    private ObjectAnimator mAnimator;

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = -1;
    private int mScrollingDirection;


    public SlideHideBehavior() {
    }
    public SlideHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull final View child, int layoutDirection) {
        mInitialOffset = child.getTop();

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mScrollingDirection == DIRECTION_DOWN) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (-cardDragDistance > v.getHeight() / 2) {
                                restartAnimator(v, v.getHeight());
                            } else {
                                restartAnimator(v, mInitialOffset);
                                cardDragDistance = 0;
                            }
                            break;
                    }
                }
                return false;
            }
        });

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0) {
            mScrollingDirection = DIRECTION_UP;
        } else if (dy < 0) {
            mScrollingDirection = DIRECTION_DOWN;
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        cardDragDistance += dyUnconsumed;
        if (mScrollingDirection == DIRECTION_DOWN && TYPE_NON_TOUCH != type){
            child.offsetTopAndBottom(-dyUnconsumed);
        }
    }


    private void restartAnimator(View target, float value) {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ObjectAnimator
                .ofFloat(target, View.Y, value)
                .setDuration(250);
        mAnimator.start();
    }
}