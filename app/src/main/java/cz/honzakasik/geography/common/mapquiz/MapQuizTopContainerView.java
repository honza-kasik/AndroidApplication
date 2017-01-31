package cz.honzakasik.geography.common.mapquiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.FrameLayout;

import cz.honzakasik.geography.common.quiz.question.layout.QuestionLayoutType;

public class MapQuizTopContainerView extends FrameLayout {

    private QuestionLayoutType questionLayoutType;
    private ViewStub questionViewStub;

    public MapQuizTopContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setType(QuestionLayoutType type) {
        this.questionLayoutType = type;
        questionViewStub.setLayoutResource(type.getLayoutId());
        questionViewStub.inflate();
    }
}
