package cz.honzakasik.geography;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.CountryExtractor;
import cz.honzakasik.geography.common.location.country.ExtractionObserver;
import cz.honzakasik.geography.common.location.country.geojsonextractor.GeoJsonExtractor;
import cz.honzakasik.geography.common.tasks.LoadCountriesTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;
import cz.honzakasik.geography.common.utils.AnimUtils;
import cz.honzakasik.geography.common.utils.FileHelper;
import cz.honzakasik.geography.common.utils.Publisher;
import cz.honzakasik.geography.learning.location.CountryInfoActivity;
import cz.honzakasik.geography.learning.location.LocationActivity;
import cz.honzakasik.geography.settings.PreferenceHelper;
import cz.honzakasik.geography.settings.SettingActivity;
import pub.devrel.easypermissions.EasyPermissions;

public class MainMenuActivity extends Activity implements ExtractionObserver, EasyPermissions.PermissionCallbacks {

    private final Logger logger = LoggerFactory.getLogger(MainMenuActivity.class);

    private LinearLayout content;
    private ProgressBar progressBar;

    private MainMenuActivity instance;

    private static final int STORAGE_RW_REQUEST_ID = 42;

    private Locale currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceHelper.with(this).isOverridingLanguageSelected()) {
            setSystemLanguage(PreferenceHelper.with(this).getOverridingLanguage().getLanguage());
        }
        this.currentLanguage = getResources().getConfiguration().locale;

        setContentView(R.layout.activity_main_menu);
        this.instance = this;
        this.content = (LinearLayout) findViewById(R.id.main_menu_activity_content);
        this.progressBar = (ProgressBar) findViewById(R.id.main_menu_activity_progressbar);
        CountryExtractor countryExtractor = new GeoJsonExtractor(this);
        countryExtractor.registerObserver(this);
        if (((App)getApplicationContext()).getCountries().size() == 0) {
            new LoadCountriesTask(new PostExecuteTask<List<Country>>() {
                @Override
                public void run(List<Country> result) {
                    ((App)getApplicationContext()).setCountries(result);
                    AnimUtils.crossFadeViews(instance, 600, progressBar, content);
                    checkPermissions();
                    publishFiles();
                }
            }).execute(countryExtractor);
        } else {
            AnimUtils.crossFadeViews(instance, 600, progressBar, content);
            checkPermissions();
            publishFiles();
        }
    }

    private void setSystemLanguage(String languageTag) {
        Locale language = new Locale(languageTag);
        Locale.setDefault(language);
        Configuration config = getResources().getConfiguration();
        config.locale = language;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        logger.info("Language should be set to {}.", language.getDisplayLanguage());

    }

    public void openMapQuizzesMenu(View view) {
        Intent intent = new Intent(this, MapQuizMenuActivity.class);
        startActivity(intent);
    }

    public void openQuizzesMenu(View view) {
        Intent intent = new Intent(this, QuizMenuActivity.class);
        startActivity(intent);
    }

    public void openLearningMenu(View view) {
        /*Intent intent = new Intent(this, LearningMenuActivity.class);
        startActivity(intent);*/
        openMapBrowsing(view);
        //openCountryInfo(view);
    }

    public void openCountryInfo(View view) {
        Intent intent = new Intent(this, CountryInfoActivity.class);
        startActivity(intent);
    }

    public void openMapBrowsing(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_justification_text),
                    STORAGE_RW_REQUEST_ID,
                    permissions);
        }
    }

    private void publishFiles() {
        if (FileHelper.isExternalStorageWritable()) {
            try {
                logger.info("Publishing files!");
                new Publisher(this).publishFilesFromAssets();
            } catch (IOException e) {
                logger.error("Error publishing files!", e);
            }
        } else {
            showPublishingAbortedMessageAndFinish();
        }
    }

    private void showPublishingAbortedMessageAndFinish() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.external_storage_not_writable_title));
        alertDialog.setMessage(getString(R.string.external_storage_not_writable_text));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishAffinity();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onExtractionProgressUpdate(int percentDone) {
        this.progressBar.setProgress(percentDone);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        logger.info("Permission granted!");
        if (requestCode == STORAGE_RW_REQUEST_ID) {
            publishFiles();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.permission_not_granted_title));
        alertDialog.setMessage(getString(R.string.permission_not_granted_text));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishAffinity();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.currentLanguage != getResources().getConfiguration().locale) {
            restartActivity();
        }
    }

    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
