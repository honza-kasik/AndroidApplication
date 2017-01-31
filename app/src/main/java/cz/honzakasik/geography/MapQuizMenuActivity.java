package cz.honzakasik.geography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cz.honzakasik.geography.games.location.GuessCountryByCapitalActivity;
import cz.honzakasik.geography.games.location.GuessCountryByFlagActivity;
import cz.honzakasik.geography.games.location.GuessCountryByNameActivity;

public class MapQuizMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_map_quiz);
    }

    public void openFindCountryByCapital(View view) {
        Intent intent = new Intent(this, GuessCountryByCapitalActivity.class);
        startActivity(intent);
    }

    public void openFindCountryByFlag(View view) {
        Intent intent = new Intent(this, GuessCountryByFlagActivity.class);
        startActivity(intent);
    }

    public void openFindCountryByName(View view) {
        Intent intent = new Intent(this, GuessCountryByNameActivity.class);
        startActivity(intent);
    }
}
