package cz.honzakasik.geography.games.quiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.quiz.QuizManager;
import cz.honzakasik.geography.common.quiz.answer.Answer;
import cz.honzakasik.geography.common.quiz.answer.AnswerLayout;
import cz.honzakasik.geography.common.quiz.layout.QuizFooterView;
import cz.honzakasik.geography.common.quiz.layout.QuizHeaderView;
import cz.honzakasik.geography.common.quiz.question.Question;
import cz.honzakasik.geography.common.quiz.question.QuestionFactory;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestionFactory;
import cz.honzakasik.geography.common.quiz.question.layout.QuestionView;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.games.AbstractGameActivity;
import cz.honzakasik.geography.games.GamesConstants;
import cz.honzakasik.geography.games.ResultsActivity;
import cz.honzakasik.geography.settings.PreferenceHelper;

public abstract class QuizActivityAbstract extends AbstractGameActivity {

    private Logger logger = LoggerFactory.getLogger(FlagQuizActivity.class);
    private Activity parentActivity;

    private ViewFlipper viewFlipper;
    private LinearLayout answerWrapper;
    protected QuestionView questionView;
    private QuizFooterView quizFooterView;
    private QuizHeaderView quizHeaderView;

    private QuizManager<FlagQuestion> quizManager;

    /**
     * This method has to overridden in child because it does not inflate view stub in question view
     * by default!
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parentActivity = this;
        this.setContentView(R.layout.quiz_flipper_view);
        final LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewFlipper = (ViewFlipper) this.findViewById(R.id.quiz_flipper);
        this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        this.quizFooterView = (QuizFooterView) this.findViewById(R.id.quiz_footer_view);
        this.quizHeaderView = (QuizHeaderView) this.findViewById(R.id.quiz_header_include_point);
        this.questionView = (QuestionView) inflater.inflate(R.layout.quiz_question_view, null);
        this.viewFlipper.addView(questionView);

        try {
            this.quizManager = buildQuizManager(
                    new FlagQuestionFactory.Builder()
                            .context(this)
                            .build());
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }
        this.quizHeaderView.setTotalQuestionsCount(quizManager.getQuestionCount());
        try {
            showUserChoiceDialogIfNeeded();
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }
        updatePointCounter();
    }

    protected abstract void loadQuestionToView(FlagQuestion question, View currentQuizFlipperView);

    private void loadAnswersToWrapper(LinearLayout answerWrapper, List<Answer> answers) {
        answerWrapper.removeAllViews(); //clean wrapper

        final LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        char letter = 'A';
        for (Answer answer : answers) {
            AnswerLayout answerView = (AnswerLayout) inflater.inflate(R.layout.quiz_answer_view, null);
            answerView.setAnswer(answer);
            answerView.setLetter(Character.toString(letter));
            answerView.setOnClickListener(new AnswerButtonTouchListener());
            answerWrapper.addView(answerView);
            if (letter != 'A') {
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)answerView.getLayoutParams();
                lParams.setMargins(0, (int) getResources().getDimension(R.dimen.quiz_answer_margin_top), 0, 0);
                answerView.setLayoutParams(lParams);
            }
            letter++;
        }
    }

    private class AnswerButtonTouchListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            logger.debug("Clicked!");
            final AnswerLayout answerLayout = (AnswerLayout) view;
            if (answerLayout.isDisabled()) {
                return;
            } else {
                answerLayout.setDisabled(true);
            }

            boolean isAnswerRight = answerLayout.getAnswer().isAnswerRight();
            quizManager.answeredQuestion(isAnswerRight);
            answerLayout.setAnsweredRight(isAnswerRight);

            updatePointCounter();
            if (isAnswerRight) {
                disableRestOfAnswers();
                startResultActivityOrMoveToNextAnswerIfPresent(2500);
            }
            else if (!quizManager.hasTryLeft()) {
                disableRestOfAnswers();
                markRightAnswer();
                startResultActivityOrMoveToNextAnswerIfPresent(2500);
            }
        }

        private void disableRestOfAnswers() {
            for (int i = 0; i < answerWrapper.getChildCount(); i++) {
                AnswerLayout currentAnswerLayout = ((AnswerLayout)answerWrapper.getChildAt(i));
                currentAnswerLayout.setDisabled(true);
            }
        }

    }

    private void markRightAnswer() {
        for (int i = 0; i < answerWrapper.getChildCount(); i++) {
            AnswerLayout currentAnswerLayout = ((AnswerLayout)answerWrapper.getChildAt(i));
            if (currentAnswerLayout.getAnswer().isAnswerRight()) {
                currentAnswerLayout.setAnsweredRight(true);
                currentAnswerLayout.refreshDrawableState();
            }
        }
    }

    private void startResultActivityOrMoveToNextAnswerIfPresent() {
        if (quizManager.hasNext()) {
            loadNextQuestion();
            logger.info("Moved to next question");
        } else {
            startResultActivity();
        }
    }

    private void startResultActivityOrMoveToNextAnswerIfPresent(long delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startResultActivityOrMoveToNextAnswerIfPresent();
                    }
                });
            }
        }, delay);
    }

    private void startResultActivity() {
        this.finish();
        EventBus.getDefault().postSticky(quizManager);
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    protected void loadNextQuestion() {
        FlagQuestion question = quizManager.next();
        View currentView = viewFlipper.getCurrentView();
        loadQuestionToView(question, currentView);
        answerWrapper = (LinearLayout) currentView.findViewById(R.id.question_answer_wrapper);
        loadAnswersToWrapper(answerWrapper, question.getAnswers());
        quizHeaderView.updateCurrentQuestionIndex(quizManager.getCurrentQuestionIndexFromOne());
        updatePointCounter();
        viewFlipper.showNext();
    }

    private void updatePointCounter() {
        quizFooterView.updatePenalizationCount(quizManager.getTriesLeft());
        quizFooterView.updatePointCount(quizManager.getTotalPointCount());
    }

    private <T extends Question> QuizManager<T> buildQuizManager(QuestionFactory<T> questionFactory)
            throws DatasourceAccessException {
        User user = null;

        if (PreferenceHelper.with(this).isDefaultUserSelected()) {
            Integer defaultUserId = PreferenceHelper.with(this).getDefaultUserId();
            user = userManager.getUser(defaultUserId);
        }

        return new QuizManager.Builder<T>()
                .user(user)
                .difficultyLevel(getCurrentDifficultyLevelAccordingToUser())
                .gameIdentification(getGameIdentification())
                .questionCount(GamesConstants.QUESTION_COUNT_IN_QUIZ)
                .questionFactory(questionFactory)
                .build();
    }
}
