package cz.honzakasik.geography.common.quiz.question;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import cz.honzakasik.geography.common.quiz.answer.Answer;

public class Question {

    private final List<Answer> answers;

    private final String question;

    public Question(List<Answer> answers, String question) {
        this.answers = answers;
        if (getRightAnswer() == null) {
            throw new IllegalStateException("No right answer defined!");
        }
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public Answer getRightAnswer() {
        for (Answer answer : this.answers) {
            if (answer.isAnswerRight()) {
                return answer;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Question question1 = (Question) o;

        return new EqualsBuilder()
                .append(answers, question1.answers)
                .append(question, question1.question)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(answers)
                .append(question)
                .toHashCode();
    }
}
