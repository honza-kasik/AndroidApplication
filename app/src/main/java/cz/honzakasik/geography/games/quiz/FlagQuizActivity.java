package cz.honzakasik.geography.games.quiz;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.quiz.question.layout.QuestionLayoutType;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.tasks.LoadFlagImageTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;

public class FlagQuizActivity extends QuizActivityAbstract {

    private Logger logger = LoggerFactory.getLogger(FlagQuizActivity.class);

    private ProgressBar progressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.questionView.setLayoutType(QuestionLayoutType.IMAGE);
        this.progressBar = (ProgressBar) questionView.findViewById(R.id.question_container_image_progress_bar);
        this.imageView = (ImageView) questionView.findViewById(R.id.question_container_image_image);
        super.loadNextQuestion();
    }

    @Override
    protected GameIdentification getGameIdentification() {
        return GameIdentification.QUIZ_FLAG;
    }

    @Override
    protected void loadQuestionToView(FlagQuestion question, View view) {
        final TextView textView = (TextView) findViewById(R.id.question_container_image_text);
        textView.setText(question.getQuestion());

        showProgressbarAndHideImage();
        new LoadFlagImageTask(this, new PostExecuteTask<Picture>() {
            @Override
            public void run(Picture result) {
                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                imageView.setImageDrawable(new PictureDrawable(result));
                hideProgressbarAndShowImage();
                logger.debug("Image loaded!");
            }
        }).execute(question.getGuessedCountry());
    }

    private void showProgressbarAndHideImage() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.imageView.setVisibility(View.INVISIBLE);
    }

    private void hideProgressbarAndShowImage() {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.imageView.setVisibility(View.VISIBLE);
    }

}
