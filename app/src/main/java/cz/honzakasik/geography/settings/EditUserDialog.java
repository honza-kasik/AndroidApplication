package cz.honzakasik.geography.settings;

import android.content.Context;
import android.os.Bundle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;

/**
 * Dialog for editing a user
 */
public class EditUserDialog extends UserDialog {

    private Logger logger = LoggerFactory.getLogger(EditUserDialog.class);

    private User selectedUser;

    protected EditUserDialog(Context context, UserManager userManager,
                             UserArrayAdapter arrayAdapter, User selectedUser) {
        super(context, userManager, arrayAdapter);
        setTitle(R.string.edit_user);
        this.selectedUser = selectedUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name.setText(selectedUser.getNickName());
        switch(selectedUser.getDifficultyLevel()) {
            case EASY:
                difficultyRadioGroup.check(R.id.easy_difficulty);
                break;
            case MODERATE:
                difficultyRadioGroup.check(R.id.moderate_difficulty);
                break;
            case HARD:
                difficultyRadioGroup.check(R.id.hard_difficulty);
                break;
        }
    }

    @Override
    protected void handleUser(User user) {
        try {
            User updatedUser = arrayAdapter.getItem(arrayAdapter.getPosition(selectedUser));
            updatedUser.setNickName(user.getNickName());
            updatedUser.setDifficultyLevel(user.getDifficultyLevel());
            userManager.updateUser(updatedUser);
            arrayAdapter.notifyDataSetChanged();
        } catch (DatasourceAccessException e) {
            logger.error("Error updating user {}!", user.toString(), e);
        }
    }
}
