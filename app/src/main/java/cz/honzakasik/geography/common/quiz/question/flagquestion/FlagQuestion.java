package cz.honzakasik.geography.common.quiz.question.flagquestion;

import java.util.List;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.quiz.answer.Answer;
import cz.honzakasik.geography.common.quiz.question.Question;

public class FlagQuestion extends Question {

    private Country guessedCountry;

    public FlagQuestion(List<Answer> answers, String question, Country guessedCountry) {
        super(answers, question);
        this.guessedCountry = guessedCountry;
    }

    public Country getGuessedCountry() {
        return guessedCountry;
    }
}
