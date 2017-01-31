package cz.honzakasik.geography.common.quiz.question;

public interface QuestionFactory<T extends Question> {

    int DEFAULT_ANSWERS_PER_QUESTION = 4;

    T createQuestion();

    T createQuestion(int answersPerQuestion);

}
