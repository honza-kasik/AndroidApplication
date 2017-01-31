package cz.honzakasik.geography.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

public class AnimUtils {

    public static void crossFadeViews(final Activity activity, long duration, final View wasVisible, final View willBeVisible) {
        willBeVisible.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                willBeVisible.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

        wasVisible.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wasVisible.setVisibility(View.GONE);
                            }
                        });
                    }
                });
    }
}
