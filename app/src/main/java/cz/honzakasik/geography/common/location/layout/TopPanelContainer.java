package cz.honzakasik.geography.common.location.layout;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cz.honzakasik.geography.R;

public class TopPanelContainer extends LinearLayout {

    public TopPanelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void animateAnsweredWrong() {
        blinkTopContainer(ContextCompat.getColor(this.getContext(), R.color.wrong_red));
    }

    public void animateAnsweredRight() {
        blinkTopContainer(ContextCompat.getColor(this.getContext(), R.color.right_green));
    }

    private void blinkTopContainer(int endColor) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this.getContext(), R.color.card_view_background),
                endColor);

        final GradientDrawable background = (GradientDrawable) ((LayerDrawable) this.getBackground()).getDrawable(1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                background.setColor((Integer) animator.getAnimatedValue());
            }

        });
        valueAnimator.setDuration(300);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }
}
