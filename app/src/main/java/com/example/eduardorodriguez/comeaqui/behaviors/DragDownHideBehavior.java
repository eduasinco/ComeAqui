package com.example.eduardorodriguez.comeaqui.behaviors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class DragDownHideBehavior extends CoordinatorLayout.Behavior<View> {

    private int mInitialOffset;
    private int cardDragDistance;
    private ObjectAnimator mAnimator;
    
    float initialY, dY;

    public DragDownHideBehavior() {
    }
    public DragDownHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull final View child, int layoutDirection) {

        child.post(() -> {
            mInitialOffset = (int) child.getY();
        });

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dY = child.getY() - ev.getRawY();
                initialY = child.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                if(ev.getRawY() + dY > initialY)
                    child.animate()
                            .y(ev.getRawY() + dY)
                            .setDuration(0).start();
                break;
            case MotionEvent.ACTION_UP:
                if (child.getY() - initialY > child.getHeight() / 3){
                    child.animate()
                            .y(child.getBottom())
                            .setDuration(100).withEndAction((
                    ) -> {
                        child.setVisibility(View.GONE);
                    }).start();
                } else {
                    child.animate()
                            .y(initialY)
                            .setDuration(100)
                            .start();
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dY = child.getY() - ev.getRawY();
                initialY = child.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                if(ev.getRawY() + dY > initialY)
                    child.animate()
                            .y(ev.getRawY() + dY)
                            .setDuration(0)
                            .start();
                break;
            case MotionEvent.ACTION_UP:
                if (child.getY() - initialY > child.getHeight() / 3){
                    child.animate()
                            .y(child.getBottom())
                            .setDuration(100).withEndAction((
                    ) -> {
                        child.setVisibility(View.GONE);
                    }).start();
                } else {
                    child.animate()
                            .y(initialY)
                            .setDuration(100)
                            .start();
                }
                break;
            default:
                return false;
        }
        return false;
    }


    private void restartAnimator(final View target, final float value, boolean close, CoordinatorLayout parent) {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ObjectAnimator
                .ofFloat(target, View.Y, value)
                .setDuration(250);
        mAnimator.start();

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cardDragDistance = 0;
                if (close){
                    parent.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}