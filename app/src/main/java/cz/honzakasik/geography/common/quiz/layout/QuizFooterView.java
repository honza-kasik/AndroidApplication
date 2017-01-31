package cz.honzakasik.geography.common.quiz.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cz.honzakasik.geography.R;

public class QuizFooterView extends RelativeLayout {

    private TextView pointCounter;
    private TextView penalizationCounter;

    public QuizFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.pointCounter = (TextView) this.findViewById(R.id.quiz_footer_points_amount);
        this.penalizationCounter = (TextView) this.findViewById(R.id.quiz_footer_try_count);
    }

    public void updatePointCount(int count) {
        pointCounter.setText(String.valueOf(count));
    }

    public void updatePenalizationCount(int count) {
        penalizationCounter.setText(String.valueOf(count));
    }

}
