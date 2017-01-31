package cz.honzakasik.geography.common.quiz.question.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.LinearLayout;

import cz.honzakasik.geography.R;

public class QuestionView extends LinearLayout {

    private ViewStub questionLayoutContainer;

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.questionLayoutContainer = (ViewStub) findViewById(R.id.question_container);
    }

    public void setLayoutType(QuestionLayoutType type) {
        this.questionLayoutContainer.setLayoutResource(type.getLayoutId());
        this.questionLayoutContainer.inflate();
    }

}
