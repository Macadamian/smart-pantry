package com.macadamian.smartpantry.utility;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AnimationUtility {

    public static final int ANIMATION_DURATION_LONG = 2000;
    public static final int ANIMATION_DURATION_MEDIUM = 1000;
    public static final int ANIMATION_DURATION_SHORT = 500;
    public static final int ANIMATION_DURATION_NONE = 0;

    public static void expand(final View drawer) {
        drawer.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = drawer.getMeasuredHeight();

        //make the height of the draw 0px
        drawer.getLayoutParams().height = 0;
        //make it visible (no blink will occur since view has no height)
        drawer.setVisibility(View.VISIBLE);
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //1 means animation has completed
                drawer.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        //else height is a fraction of time remaining
                        : (int) (targetHeight * interpolatedTime);
                //force layout invalidation
                drawer.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                //return true since it will change bounds of view being animated
                return true;
            }
        };

        anim.setDuration(ANIMATION_DURATION_SHORT);
        drawer.startAnimation(anim);
    }

    public static void collapse(final View drawer) {
        Animation anim = collapseAnimation(drawer);

        anim.setDuration(ANIMATION_DURATION_SHORT);
        drawer.startAnimation(anim);
    }

    public static void collapse(View drawer, int duration) {
        Animation anim = collapseAnimation(drawer);

        anim.setDuration(duration);
        drawer.startAnimation(anim);
    }

    private static Animation collapseAnimation(final View drawer) {
        final int initialHeight = drawer.getMeasuredHeight();

        return new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    drawer.setVisibility(View.GONE);
                } else {
                    drawer.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    drawer.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
    }

    public static void moveByAmount(View viewToMove, int amount) {
        Integer viewHeight = viewToMove.getHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewToMove.getLayoutParams();
        if (params.topMargin + amount < viewHeight * -1) {
            params.topMargin = viewHeight * -1;
        } else if (params.topMargin + amount > 0) {
            params.topMargin = 0;
        } else {
            params.topMargin = params.topMargin + amount;
        }
        viewToMove.setLayoutParams(params);
        viewToMove.requestLayout();
    }

    public static void resetMoveToZero(View viewToMove) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewToMove.getLayoutParams();
        params.topMargin = 0;
        viewToMove.setLayoutParams(params);
        viewToMove.requestLayout();
    }

    public static void updateAnimation(final View v) {
        final ScaleAnimation scaleInAnimation = new ScaleAnimation(v.getX(), v.getX() + 1, v.getY(), v.getY() + 1, v.getPivotX(), v.getPivotY());
        scaleInAnimation.setDuration(200);
        scaleInAnimation.setFillAfter(false);
        v.startAnimation(scaleInAnimation);
    }
}
