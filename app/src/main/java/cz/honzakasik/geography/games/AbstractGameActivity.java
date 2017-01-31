package cz.honzakasik.geography.games;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.datasource.OrmLiteBaseAppCompatActivity;
import cz.honzakasik.geography.common.quiz.question.DifficultyLevel;
import cz.honzakasik.geography.common.results.GameIdentification;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.ORMLiteUserManager;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;
import cz.honzakasik.geography.settings.PreferenceHelper;
import cz.honzakasik.geography.settings.UserArrayAdapter;

public abstract class AbstractGameActivity extends OrmLiteBaseAppCompatActivity {

    private Logger logger = LoggerFactory.getLogger(AbstractGameActivity.class);

    protected UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            userManager = new ORMLiteUserManager(getHelper().getDao(User.class));
        } catch (SQLException e) {
            logger.error("Error during user manager initialization!", e);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    protected void showUserChoiceDialogIfNeeded() throws DatasourceAccessException {
        final UserArrayAdapter arrayAdapter = new UserArrayAdapter(this, userManager.getAllUsers());
        final Context context = this;
        if (shouldShowChooseUserDialog(userManager)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_user_in_game_dialog_title)
                    .setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreferenceHelper.with(context).setDefaultUser(arrayAdapter.getItem(which));
                            String msg = String.format(getString(R.string.user_chosen_in_game_message),
                                    arrayAdapter.getItem(which).getNickName());
                            Toast.makeText(App.getContext(), msg, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    })
                    .show();
            logger.info("Showing choose default user dialog!");
        } else if (PreferenceHelper.with(this).isUserManagementEnabled() &&
                PreferenceHelper.with(this).isDefaultUserSelected()) {
            Integer defaultUserId = PreferenceHelper.with(this).getDefaultUserId();
            User defaultUser = userManager.getUser(defaultUserId);
            String nickname = defaultUser.getNickName();
            String msg = String.format(getString(R.string.user_chosen_already_message),
                    nickname);
            Toast.makeText(App.getContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    protected boolean shouldShowChooseUserDialog(UserManager userManager) throws DatasourceAccessException {
        return PreferenceHelper.with(this).isUserManagementEnabled() &&
                !PreferenceHelper.with(this).isDefaultUserSelected() &&
                userManager.getAllUsers().size() > 0;
    }

    protected DifficultyLevel getCurrentDifficultyLevelAccordingToUser() {
        DifficultyLevel difficultyLevel = DifficultyLevel.EASY;
        if (PreferenceHelper.with(this).isDefaultUserSelected()) {
            Integer defaultUserId = PreferenceHelper.with(this).getDefaultUserId();
            try {
                difficultyLevel = userManager.getUser(defaultUserId).getDifficultyLevel();
            } catch (DatasourceAccessException e) {
                logger.error("Error during obtaining default user!", e);
            }
        } else {
            logger.info("No default user set! Setting '{}' difficulty level!", difficultyLevel);
        }
        return difficultyLevel;
    }

    protected abstract GameIdentification getGameIdentification();
}
