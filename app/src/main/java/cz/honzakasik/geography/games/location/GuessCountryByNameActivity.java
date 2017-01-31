package cz.honzakasik.geography.games.location;

import android.os.Bundle;
import android.view.View;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.utils.ResHelper;

public class GuessCountryByNameActivity extends MapQuizAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.map_quiz_container_question_image_container).setVisibility(View.GONE);
        fillInQuestion(this.quizManager.next());
    }

    @Override
    protected GameIdentification getGameIdentification() {
        return GameIdentification.MAP_QUIZ_NAME;
    }

    @Override
    protected void fillInQuestion(FlagQuestion question) {
        this.currentQuestion = question;
        Country country = question.getGuessedCountry();
        String questionText = String.format(getString(R.string.quiz_name_question_map),
                ResHelper.getLocalizedCountryName(country, this));
        this.questionTextView.setText(questionText);
    }
}
