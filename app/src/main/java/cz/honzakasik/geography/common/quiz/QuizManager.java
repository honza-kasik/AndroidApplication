package cz.honzakasik.geography.common.quiz;

import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cz.honzakasik.geography.common.quiz.question.DifficultyLevel;
import cz.honzakasik.geography.common.quiz.question.Question;
import cz.honzakasik.geography.common.quiz.question.QuestionFactory;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.users.User;

import static cz.honzakasik.geography.common.quiz.QuizConstants.MAX_TRIES_PER_ANSWER;
import static cz.honzakasik.geography.common.quiz.QuizConstants.MIN_TRIES_PER_ANSWER;

/**
 * Class which should be used to manage quiz. Total points, managing question ordering etc...
 */
public class QuizManager<T extends Question> implements Iterator<T> {

    private final Logger logger = LoggerFactory.getLogger(QuizManager.class);

    private final List<T> questions;
    private final int maxTriesPerQuestion;
    private final DifficultyLevel difficultyLevel;
    private final User user;
    private final GameIdentification gameIdentification;

    private int questionIndex = -1;
    private int totalPointCount;
    private int currentTryCount;

    public QuizManager(Builder<T> builder) {
        LinkedList<T> questions = new LinkedList<>();
        for (int i = 0; i < builder.questionCount; i++) {
            questions.add(builder.questionFactory.createQuestion());
        }
        this.questions = questions;
        this.difficultyLevel = builder.difficultyLevel;
        this.maxTriesPerQuestion = getMaxTriesPerQuestion(difficultyLevel);
        this.currentTryCount = getMaxTriesPerQuestion(difficultyLevel);
        this.user = builder.user;
        this.gameIdentification = builder.gameIdentification;
    }

    private int getMaxTriesPerQuestion(DifficultyLevel difficultyLevel){
        switch (difficultyLevel) {
            case EASY:
                return MAX_TRIES_PER_ANSWER;
            case MODERATE:
                return (MAX_TRIES_PER_ANSWER + MIN_TRIES_PER_ANSWER) / 2;
            case HARD:
                return MIN_TRIES_PER_ANSWER;
            default:
                throw new IllegalStateException("Unexpected difficulty level value!");
        }
    }

    private void renewTryCount() {
        this.currentTryCount = getMaxTriesPerQuestion(this.difficultyLevel);
    }

    public int getCurrentQuestionIndexFromOne() {
        return questionIndex + 1;
    }

    public void answeredQuestion(boolean isAnswerRight) {
        if (isAnswerRight) {
            totalPointCount += currentTryCount;
            logger.debug("{} points was added to total point count, which is now {}", currentTryCount, totalPointCount);
        } else {
            logger.debug("There are {} tries left.", --currentTryCount);
        }
    }

    @Deprecated
    public void answeredRight() {
        totalPointCount += currentTryCount;
        logger.info("{} points was added to total point count, which is now {}", currentTryCount, totalPointCount);
    }

    @Deprecated
    public void answeredWrong() {
        logger.info("There are {} tries left.", --currentTryCount);
    }

    public boolean hasTryLeft() {
        return currentTryCount > 0;
    }

    public int getTotalPointCount() {
        return totalPointCount;
    }

    public int getCurrentTryCount() {
        return currentTryCount;
    }

    public int getTriesLeft() {
        return currentTryCount;
    }

    @Override
    public boolean hasNext() {
        return questionIndex < questions.size() - 1;
    }

    @Override
    public T next() {
        renewTryCount();
        logger.info("Current question index is {}", ++questionIndex);
        return questions.get(questionIndex);
    }

    @Override
    public void remove() { //Question in QuizManager are immutable
    }

    public int getMaxTriesPerQuestion() {
        return maxTriesPerQuestion;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public int getMaxTotalPointCount() {
        return getQuestionCount() * getMaxTriesPerQuestion();
    }

    /**
     * Obtain assigned game type
     */
    public GameIdentification getGameIdentification() {
        return gameIdentification;
    }

    /**
     * Obtain currently playing user
     */
    public User getUser() {
        return user;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public static class Builder<T extends Question> {

        private DifficultyLevel difficultyLevel;
        private User user;
        private QuestionFactory<T> questionFactory;
        private Integer questionCount;
        private GameIdentification gameIdentification;

        public Builder() {}

        public Builder<T> difficultyLevel(@NonNull DifficultyLevel difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
            return this;
        }

        public Builder<T> user(@NonNull User user) {
            this.user = user;
            return this;
        }

        public Builder<T> gameIdentification(@NonNull GameIdentification gameIdentification) {
            this.gameIdentification = gameIdentification;
            return this;
        }

        public Builder<T> questionFactory(@NonNull QuestionFactory<T> questionFactory) {
            this.questionFactory = questionFactory;
            return this;
        }

        public Builder<T> questionCount(@NonNull Integer questionCount) {
            this.questionCount = questionCount;
            return this;
        }

        private void checkNotNull(Object toCheck, String message) {
            if (toCheck == null) {
                throw new IllegalStateException(message);
            }
        }

        private void validate() {
            //checkNotNull(user, "User must be set!");
            checkNotNull(questionFactory, "QuestionFactory must be set!");
            checkNotNull(gameIdentification, "GameIdentification must be set!");
            checkNotNull(questionCount, "QuestionCount must be set!");
        }

        public QuizManager<T> build() {
            validate();
            return new QuizManager<>(this);
        }

    }

    //TODO add builder which uses game identification, show top 5 results in ResultActivity
}
