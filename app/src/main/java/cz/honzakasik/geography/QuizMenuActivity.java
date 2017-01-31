package cz.honzakasik.geography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cz.honzakasik.geography.games.quiz.FlagQuizActivity;
import cz.honzakasik.geography.games.quiz.QuizActivity;

public class QuizMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_quiz);
    }

    public void openCapitalQuiz(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void openFlagQuiz(View view) {
        Intent intent = new Intent(this, FlagQuizActivity.class);
        startActivity(intent);
    }

}
