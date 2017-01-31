package cz.honzakasik.geography.games.location;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.tasks.LoadFlagImageTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;
import cz.honzakasik.geography.common.utils.AnimUtils;

public class GuessCountryByFlagActivity extends MapQuizAbstractActivity {

    private Logger logger = LoggerFactory.getLogger(GuessCountryByFlagActivity.class);

    private ImageView flagImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.flagImageView = (ImageView) this.findViewById(R.id.map_quiz_container_question_image);
        fillInQuestion(quizManager.next());
    }

    @Override
    protected GameIdentification getGameIdentification() {
        return GameIdentification.MAP_QUIZ_FLAG;
    }

    @Override
    protected void fillInQuestion(FlagQuestion question) {
        logger.info("Filling in question! {}", question);
        setProgressBarVisible();
        this.currentQuestion = question;
        this.questionTextView.setText(question.getQuestion());
        new LoadFlagImageTask(this, new PostExecuteTask<Picture>() {
            @Override
            public void run(Picture result) {
                flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                flagImageView.setImageDrawable(new PictureDrawable(result));
                setProgressBarInvisible();
            }
        }).execute(question.getGuessedCountry());
    }

    private void setProgressBarInvisible() {
        final int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        View questionImage = findViewById(R.id.map_quiz_container_question_image);
        View progressBar = findViewById(R.id.map_quiz_container_question_progress_bar);
        AnimUtils.crossFadeViews(this, shortAnimationDuration, progressBar, questionImage);
    }

    private void setProgressBarVisible() {
        final int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        View questionImage = findViewById(R.id.map_quiz_container_question_image);
        View progressBar = findViewById(R.id.map_quiz_container_question_progress_bar);
        AnimUtils.crossFadeViews(this, shortAnimationDuration, questionImage, progressBar);
    }

}
