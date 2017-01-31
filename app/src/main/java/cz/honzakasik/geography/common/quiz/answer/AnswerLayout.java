package cz.honzakasik.geography.common.quiz.answer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cz.honzakasik.geography.R;

public class AnswerLayout extends RelativeLayout {

    private static final int[] STATE_ANSWERED_WRONG = {R.attr.state_answered_wrong};
    private static final int[] STATE_ANSWERED_RIGHT = {R.attr.state_answered_right};

    private boolean isAnsweredWrong = false;
    private boolean isAnsweredRight = false;

    private boolean disabled;
    private Answer answer;

    public AnswerLayout(Context context) {
        super(context);
    }

    public AnswerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnsweredRight(boolean isAnsweredRight) {
        this.isAnsweredRight = isAnsweredRight;
        this.isAnsweredWrong = !isAnsweredRight;
    }

    @Deprecated
    public void setAnsweredWrong(boolean isAnsweredWrong) {
        this.isAnsweredRight = !isAnsweredWrong;
        this.isAnsweredWrong = isAnsweredWrong;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (isAnsweredRight) {
            mergeDrawableStates(drawableState, STATE_ANSWERED_RIGHT);
        }
        if (isAnsweredWrong) {
            mergeDrawableStates(drawableState, STATE_ANSWERED_WRONG);
        }
        return drawableState;
    }

    public void setLetter(String letter) {
        TextView letterView = (TextView) this.findViewById(R.id.quiz_answer_letter);
        letterView.setText(String.valueOf(letter));
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
        TextView answerTextView = (TextView) this.findViewById(R.id.quiz_answer_text);
        answerTextView.setText(answer.getAnswerText());
    }

    public Answer getAnswer() {
        return answer;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
