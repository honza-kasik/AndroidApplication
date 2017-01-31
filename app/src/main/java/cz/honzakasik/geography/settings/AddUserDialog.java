package cz.honzakasik.geography.settings;

import android.content.Context;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;

/**
 * Dialog for adding a user
 */
public class AddUserDialog extends UserDialog {

    protected AddUserDialog(Context context, final UserManager userManager, final UserArrayAdapter arrayAdapter) {
        super(context, userManager, arrayAdapter);
        setTitle(R.string.add_new_user);
    }

    protected void handleUser(User user) {
        userManager.addUser(user);
        arrayAdapter.add(user);
        arrayAdapter.notifyDataSetChanged();
    }
}
