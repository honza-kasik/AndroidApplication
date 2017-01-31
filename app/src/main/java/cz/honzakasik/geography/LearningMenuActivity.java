package cz.honzakasik.geography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cz.honzakasik.geography.education.flags.FlagOverviewActivity;
import cz.honzakasik.geography.education.location.LocationActivity;

public class LearningMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_learning);
    }

    public void openFlagBrowsing(View view) {
        Intent intent = new Intent(this, FlagOverviewActivity.class);
        startActivity(intent);
    }

    public void openMapBrowsing(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }
}
