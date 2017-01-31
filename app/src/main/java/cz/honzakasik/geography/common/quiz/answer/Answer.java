package cz.honzakasik.geography.common.quiz.answer;

public class Answer {

    private final String answerText;
    private final boolean isAnswerRight;

    public Answer(String answerText, boolean isAnswerRight) {
        this.answerText = answerText;
        this.isAnswerRight = isAnswerRight;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isAnswerRight() {
        return isAnswerRight;
    }
}
