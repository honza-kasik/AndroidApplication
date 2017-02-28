package cz.honzakasik.geography;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import cz.honzakasik.geography.learning.flags.FlagOverviewActivity;
import cz.honzakasik.geography.learning.location.LocationActivity;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.CountryExtractor;
import cz.honzakasik.geography.common.location.country.ExtractionObserver;
import cz.honzakasik.geography.common.location.country.geojsonextractor.GeoJsonExtractor;
import cz.honzakasik.geography.common.tasks.LoadCountriesTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;
import cz.honzakasik.geography.common.utils.Publisher;
import cz.honzakasik.geography.games.location.GuessCountryByCapitalActivity;
import cz.honzakasik.geography.games.location.GuessCountryByFlagActivity;
import cz.honzakasik.geography.games.quiz.FlagQuizActivity;

public class MainActivity extends AppCompatActivity implements ExtractionObserver {

    private Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private LinearLayout content;
    private ProgressBar progressBar;

    private List<Country> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidGraphicFactory.createInstance(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.content = (LinearLayout) findViewById(R.id.main_activity_content);
        this.progressBar = (ProgressBar) findViewById(R.id.main_activity_progressbar);
        CountryExtractor countryExtractor = new GeoJsonExtractor(this);
        countryExtractor.registerObserver(this);
        new LoadCountriesTask(new PostExecuteTask<List<Country>>() {
            @Override
            public void run(List<Country> result) {
                countries = result;
                ((App)getApplicationContext()).setCountries(countries);
                content.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).execute(countryExtractor);
    }

    public void onStart() {
        super.onStart();
        Publisher publisher = new Publisher(this);
        try {
            publisher.publishFilesFromAssets();
        } catch (IOException e) {
            logger.error("Error publishing files!", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showFlagQuiz(View view) {
        Intent intent = new Intent(this, FlagQuizActivity.class);
        startActivity(intent);
    }

    public void showMap(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void showFlagOverview(View view) {
        Intent intent = new Intent(this, FlagOverviewActivity.class);
        startActivity(intent);
    }

    public void showGuessCountry(View view) {
        Intent intent = new Intent(this, GuessCountryByFlagActivity.class);
        startActivity(intent);
    }

    public void showGuessCountryByCapital(View view) {
        Intent intent = new Intent(this, GuessCountryByCapitalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onExtractionProgressUpdate(int percentDone) {
        this.progressBar.setProgress(percentDone);
    }
}
