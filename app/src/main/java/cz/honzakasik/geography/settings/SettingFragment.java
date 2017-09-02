package cz.honzakasik.geography.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cz.honzakasik.geography.BuildConfig;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.datasource.DatabaseHelper;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.ORMLiteUserManager;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;
import cz.honzakasik.geography.common.users.PreferencesConstants;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage.MediaMetadata;
import cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage.GalleryImageMetadataParser;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String DEFAULT_USER_KEY =  PreferencesConstants.DEFAULT_USER_KEY;

    private Logger logger = LoggerFactory.getLogger(SettingFragment.class);

    private DatabaseHelper databaseHelper;

    private UserManager userManager;

    private Context context;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        onBind();
        addPreferencesFromResource(R.xml.preferences);

        bindAboutApplicationDialog();
        try {
            bindAboutAuthorDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            populateSelectUserList();
            populateSelectLanguageList();
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }
    }

    private void bindAboutApplicationDialog() {
        Preference dialogPreference = getPreferenceScreen().findPreference(PreferencesConstants.ABOUT_APPLICATION);
        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //TODO replace by string resource
                StringBuilder stringBuilder = new StringBuilder()
                        .append("Author: Jan Kašík <kasik.honza@gmail.com>").append("\n")
                        .append("Version: ").append(BuildConfig.VERSION_NAME).append("\n")
                        .append("This application is my bachelor thesis finished in summer of 2016.");

                new AlertDialog.Builder(context)
                        .setMessage(stringBuilder.toString())
                        .setCancelable(true)
                        .setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void bindAboutAuthorDialog() throws IOException {
        Preference dialogPreference = getPreferenceScreen().findPreference(PreferencesConstants.ABOUT_MEDIA);
        final List<MediaMetadata> data = loadPhotosMetadata();
        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ListView listView = (ListView) ((LayoutInflater)context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.dialog_media_about, null);
                ListAdapter listAdapter =  new AuthorArrayAdapter(getActivity(), data);
                listView.setAdapter(listAdapter);
                new AlertDialog.Builder(context)
                        .setView(listView)
                        .setCancelable(true)
                        .setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    /**
     * Loads author and license metadata for all used images in country photos
     * @return list of media metadata
     */
    private List<MediaMetadata> loadPhotosMetadata() throws IOException {
        final AssetManager assetManager = getActivity().getAssets();
        final String rootPath = PropUtils.get("resources.country.photo.path");
        final String[] countryFolders = assetManager.list(rootPath);
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<MediaMetadata> data = new LinkedList<>();

        for (String countryFolder : countryFolders) {
            final String countryFolderPath = rootPath + File.separator + countryFolder;
            final String[] countryFiles = assetManager.list(countryFolderPath);
            for (String file : countryFiles) {
                if (FilenameUtils.isExtension(file, "json")) {
                    final MediaMetadata metadata = new GalleryImageMetadataParser.Builder(getActivity())
                            .objectMapper(objectMapper)
                            .inputStream(assetManager.open(countryFolderPath + File.separator + file))
                            .build()
                            .getMetadata();

                    data.add(metadata);
                }
            }
        }
        return data;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        onBind();
        try {
            populateSelectUserList();
            populateSelectLanguageList();
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        onUnbind();
    }

    private void populateSelectUserList() throws DatasourceAccessException {
        List<User> users = userManager.getAllUsers();

        //Preference item with list selection - find with id specified in preferences.xml
        ListPreference lp = (ListPreference)findPreference(DEFAULT_USER_KEY);

        //Prepare data for ListPreference - user's nickname will be shown and id will be used as an identifier
        CharSequence[] entries = new CharSequence[users.size()];
        CharSequence[] identifiers = new CharSequence[users.size()];
        for (int i = 0; i < users.size(); i++) {
            entries[i] = users.get(i).getNickName();
            identifiers[i] = users.get(i).getUserId().toString();
        }

        if (users.size() == 0) {
            //if there are no added users yet
            lp.setEnabled(false);
        } else {
            lp.setEnabled(true);
            lp.setEntries(entries);
            lp.setEntryValues(identifiers);
        }
    }

    private void populateSelectLanguageList() {
        //Preference item with list selection - find with id specified in preferences.xml
        ListPreference lp = (ListPreference)findPreference(PreferencesConstants.OVERRIDING_LANGUAGE_KEY);

        //Prepare data for ListPreference - user's nickname will be shown and id will be used as an identifier
        int availableTranslationsCount = SettingsConstants.AVAILABLE_TRANSLATIONS.length;
        CharSequence[] entries = new CharSequence[availableTranslationsCount];
        CharSequence[] identifiers = new CharSequence[availableTranslationsCount];
        for (int i = 0; i < availableTranslationsCount; i++) {
            entries[i] = SettingsConstants.AVAILABLE_TRANSLATIONS[i].getDisplayLanguage();
            identifiers[i] = SettingsConstants.AVAILABLE_TRANSLATIONS[i].getLanguage();
        }

        lp.setEntries(entries);
        lp.setEntryValues(identifiers);

        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.getKey().equals(PreferencesConstants.OVERRIDING_LANGUAGE_KEY)) {
                    logger.info("Selected new language '{}'.", newValue.toString());
                    setSystemLanguage(newValue.toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void setSystemLanguage(String languageTag) {
        Locale language = new Locale(languageTag);
        Locale.setDefault(language);
        Configuration config = getResources().getConfiguration();
        config.locale = language;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        restartActivity();
        logger.info("Language should be set to {}.", language.getDisplayLanguage());
    }

    private void restartActivity(){
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void onBind() {
        try {
            userManager = new ORMLiteUserManager(getHelper().getDao(User.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //necessary method since PreferenceFragment don't have onDestroy method visible
    private void onUnbind() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        logger.info("Value of preference '{}' changed to '{}'.", key, String.valueOf(sharedPreferences.getAll().get(key)));
    }
}
