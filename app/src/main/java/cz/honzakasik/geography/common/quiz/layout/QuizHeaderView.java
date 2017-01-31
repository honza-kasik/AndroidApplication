package cz.honzakasik.geography.common.quiz.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cz.honzakasik.geography.R;

public class QuizHeaderView extends RelativeLayout {

    private TextView questionCurrentIndex;
    private TextView questionTotalCount;

    public QuizHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.questionCurrentIndex = (TextView) findViewById(R.id.quiz_header_numbering_question_current);
        this.questionTotalCount = (TextView) findViewById(R.id.quiz_header_numbering_question_total);
        this.updateCurrentQuestionIndex(1);
    }

    public void setTotalQuestionsCount(int count) {
        this.questionTotalCount.setText(String.valueOf(count));
    }

    public void updateCurrentQuestionIndex(int index) {
        this.questionCurrentIndex.setText(String.valueOf(index));
    }
}
