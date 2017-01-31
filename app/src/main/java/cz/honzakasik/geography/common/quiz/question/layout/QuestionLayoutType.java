package cz.honzakasik.geography.common.quiz.question.layout;

import cz.honzakasik.geography.R;

public enum QuestionLayoutType {

    IMAGE(R.layout.quiz_question_flag_view),
    TEXT(R.layout.quiz_question_text_view);

    private int layoutId;

    QuestionLayoutType(int layoutId) {
        this.layoutId = layoutId;
    }

    public int getLayoutId() {
        return layoutId;
    }
}
