package cz.honzakasik.geography.common.quiz.question.flagquestion;

import android.content.Context;
import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.quiz.answer.Answer;
import cz.honzakasik.geography.common.quiz.question.QuestionFactory;
import cz.honzakasik.geography.common.utils.ResHelper;

public class FlagQuestionFactory implements QuestionFactory<FlagQuestion> {

    private final Queue<Country> questionedCountries;
    private final List<Country> countries;
    private final Context context;
    private final Random random;
    private final int answersPerQuestion;
    private final String questionText;

    private FlagQuestionFactory(Builder builder) {
        this.random = new Random();
        this.countries = builder.countries;
        this.context = builder.context;
        this.answersPerQuestion = builder.answersPerQuestion;
        this.questionText = builder.questionText;
        LinkedList<Country> shuffledCountries = new LinkedList<>(builder.countries);
        Collections.shuffle(shuffledCountries, this.random);
        this.questionedCountries = new ArrayDeque<>(shuffledCountries);
    }

    @Override
    public FlagQuestion createQuestion() {
        return createQuestion(answersPerQuestion);
    }

    @Override
    public FlagQuestion createQuestion(int answersPerQuestion) {
        Country country = this.questionedCountries.poll();
        FlagQuestion question = new FlagQuestion(createAnswers(country, answersPerQuestion),
                this.questionText, country);
        this.questionedCountries.offer(country);
        return question;
    }

    private List<Answer> createAnswers(Country rightCountry, int answerCount) {
        final int rightAnswerIndex = random.nextInt(answerCount);
        final List<Answer> answers = new LinkedList<>();
        for (int i = 0; i < answerCount; i++) {//TODO multiple right answers???
            Country country = countries.get(random.nextInt(countries.size()));
            while (country == rightCountry) {
                country = countries.get(random.nextInt(countries.size()));
            }
            if (i == rightAnswerIndex) {
                country = rightCountry;
            }
            answers.add(new Answer(ResHelper.getLocalizedCountryName(country, this.context),
                    (i == rightAnswerIndex)));
        }
        return answers;
    }

    public static class Builder {

        private Logger logger = LoggerFactory.getLogger(Builder.class);

        private List<Country> countries;
        private Context context;
        private int answersPerQuestion;
        private String questionText;

        public Builder() {
        }

        public Builder country(@NonNull Country country) {
            if (this.countries == null) {
                this.countries = new LinkedList<>();
            }
            this.countries.add(country);
            return this;
        }

        public Builder countries(@NonNull List<Country> countries) {
            this.countries = countries;
            return this;
        }

        public Builder context(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder answersPerQuestion(int answersPerQuestion) {
            this.answersPerQuestion = answersPerQuestion;
            return this;
        }

        public Builder questionText(@NonNull String questionText) {
            this.questionText = questionText;
            return this;
        }

        private Builder validate() {
            if (this.context == null) {
                throw new IllegalArgumentException("Context has to be set!");
            }
            if (this.countries == null) {
                this.countries = ((App) this.context.getApplicationContext()).getCountries();
            }
            if (this.questionText == null) {
                this.logger.warn("Setting answer text to its default value!");
                this.questionText = this.context.getString(R.string.quiz_question_flag_quiz_question);
            }
            if (this.answersPerQuestion <= 0) {
                this.logger.warn("Count of answers per question is set to invalid value: '{}'! Setting to default!", this.answersPerQuestion);
                this.answersPerQuestion = DEFAULT_ANSWERS_PER_QUESTION;
            }
            return this;
        }

        public FlagQuestionFactory build() {
            return new FlagQuestionFactory(validate());
        }
    }
}
