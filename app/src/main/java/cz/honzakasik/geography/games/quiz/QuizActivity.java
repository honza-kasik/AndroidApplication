package cz.honzakasik.geography.games.quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.quiz.question.layout.QuestionLayoutType;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.utils.ResHelper;

public class QuizActivity extends QuizActivityAbstract {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.questionView.setLayoutType(QuestionLayoutType.TEXT);
        super.loadNextQuestion();
    }

    @Override
    protected GameIdentification getGameIdentification() {
        return GameIdentification.QUIZ_CAPITAL;
    }

    @Override
    protected void loadQuestionToView(FlagQuestion question, View view) {
        final TextView textView = (TextView) view.findViewById(R.id.question_container_text_text);
        int capitalStringId = ResHelper.getResId(question.getGuessedCountry().getIso2().toLowerCase() + "_capital", R.string.class);
        String questionText = String.format(getString(R.string.quiz_capital_question), getString(capitalStringId));
        textView.setText(questionText);
    }
}