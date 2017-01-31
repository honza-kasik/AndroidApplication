package cz.honzakasik.geography.games.location;

import android.os.Bundle;
import android.view.View;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.utils.ResHelper;

public class GuessCountryByCapitalActivity extends MapQuizAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.map_quiz_container_question_image_container).setVisibility(View.GONE);
        fillInQuestion(this.quizManager.next());
    }

    @Override
    protected GameIdentification getGameIdentification() {
        return GameIdentification.MAP_QUIZ_CAPITAL;
    }

    @Override
    protected void fillInQuestion(FlagQuestion question) {
        this.currentQuestion = question;
        Country country = question.getGuessedCountry();
        int capitalStringId = ResHelper.getResId(country.getIso2().toLowerCase() + "_capital", R.string.class);
        String questionText = String.format(getString(R.string.quiz_capital_question_map),
                getString(capitalStringId));
        this.questionTextView.setText(questionText);
    }
}
