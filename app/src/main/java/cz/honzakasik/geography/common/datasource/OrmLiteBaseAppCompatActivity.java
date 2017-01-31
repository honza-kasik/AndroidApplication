package cz.honzakasik.geography.common.datasource;

import android.support.v7.app.AppCompatActivity;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Base class for classes using access to OrmLite database
 */
public class OrmLiteBaseAppCompatActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
