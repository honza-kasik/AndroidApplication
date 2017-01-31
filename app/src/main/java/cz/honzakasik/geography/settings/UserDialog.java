package cz.honzakasik.geography.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.quiz.question.DifficultyLevel;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;

public abstract class UserDialog extends AlertDialog {

    protected EditText name;
    protected RadioGroup difficultyRadioGroup;

    protected UserManager userManager;
    protected UserArrayAdapter arrayAdapter;

    final DialogInterface.OnClickListener negativeBurronOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            User user = new User.Builder()
                    .userNickname(name.getText().toString())
                    .difficultyLevel(getDifficultyLevel(difficultyRadioGroup))
                    .build();
            handleUser(user);
            dialog.dismiss();
        }
    };

    protected UserDialog(Context context, final UserManager userManager, final UserArrayAdapter arrayAdapter) {
        super(context);
        this.arrayAdapter = arrayAdapter;
        this.userManager = userManager;

        LinearLayout layout = (LinearLayout) ((LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_add_user, null);

        name = (EditText) layout.findViewById(R.id.user_nickname);
        difficultyRadioGroup = (RadioGroup) layout.findViewById(R.id.difficulty_radio_group);

        setTitle(R.string.add_new_user);
        setView(layout);

        setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.save_button_label),
                    negativeBurronOnClickListener);

        setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getString(R.string.cancel_button_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    /**
     * Method to handle data obtained from dialog input fields
     * @param user data from input fields - nickname and difficulty level
     */
    protected abstract void handleUser(User user);

    private DifficultyLevel getDifficultyLevel(RadioGroup radioGroup) {
        DifficultyLevel difficultyLevel = DifficultyLevel.EASY;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case (R.id.easy_difficulty):
                difficultyLevel = DifficultyLevel.EASY;
                break;
            case (R.id.moderate_difficulty):
                difficultyLevel = DifficultyLevel.MODERATE;
                break;
            case (R.id.hard_difficulty):
                difficultyLevel = DifficultyLevel.HARD;
                break;
        }
        return difficultyLevel;
    }
}
