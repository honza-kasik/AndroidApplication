package cz.honzakasik.geography.common.mapquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MessageDisplayHandler {

    private static final long ANIMATION_DURATION = 300;

    private Timer hideBoxTimer;
    private final TextView messageBox;
    private final Activity parentActivity;

    public MessageDisplayHandler(TextView messageBox, Activity parentActivity) {
        this.hideBoxTimer = new Timer();
        this.messageBox = messageBox;
        this.parentActivity = parentActivity;
    }

    private void showMessageBox() {
        this.messageBox.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        messageBox.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean isMessageBoxVisible() {
        return this.messageBox.getVisibility() == View.VISIBLE;
    }

    private void hideMessageBox() {
        this.messageBox.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        messageBox.setVisibility(View.GONE);

                    }
                });
    }

    private void resetTimer() {
        this.hideBoxTimer.cancel();
        this.hideBoxTimer.purge();
        this.hideBoxTimer = new Timer();
        this.hideBoxTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideMessageBox();
                    }
                });
            }
        }, 5000);
    }

    public void displayMessage(String message) {
        resetTimer();
        if (!isMessageBoxVisible()) {
            showMessageBox();
        }
        this.messageBox.setText(message);
    }
}
