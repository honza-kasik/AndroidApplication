package cz.honzakasik.geography.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.software.shell.fab.ActionButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.datasource.OrmLiteBaseAppCompatActivity;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.ORMLiteUserManager;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.UserManager;

public class UsersManagementActivity extends OrmLiteBaseAppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private static final float MOVE_DISTANCE_CONSTANT = 85.0f;

    private Logger logger = LoggerFactory.getLogger(UsersManagementActivity.class);

    private UserArrayAdapter arrayAdapter;
    private ListView listView;
    private UserManager userManager;

    private ActionButton addUserButton;
    private ActionButton removeUserButton;

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.userManager = new ORMLiteUserManager(getHelper().getDao(User.class));
        } catch (SQLException e) {
            logger.error("Unable to instantiate user manager!", e);
        }

        this.setContentView(R.layout.activity_setting_user);

        this.listView = (ListView) findViewById(R.id.setting_list_of_users);
        this.listView.setOnItemSelectedListener(this);

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                listView.clearFocus();
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.requestFocusFromTouch();
                        listView.setSelection(position);
                    }
                });
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listView.getItemAtPosition(position);
                openEditUserDialog(user);
            }
        });

        this.addUserButton = (ActionButton) findViewById(R.id.add_button);
        this.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddUserDialog();
            }
        });
        this.removeUserButton = (ActionButton) findViewById(R.id.remove_user_button);
        this.removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.removeUser(selectedUser);
                arrayAdapter.remove(selectedUser);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        List<User> list = null;
        try {
            list = userManager.getAllUsers();
        } catch (DatasourceAccessException e) {
            logger.error("Unable to obtain all user list!", e);
        }

        arrayAdapter = new UserArrayAdapter(this, list);

        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.settings_manage_users));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void showRemoveButton() {
        addUserButton.moveLeft(MOVE_DISTANCE_CONSTANT);
        removeUserButton.show();
    }

    private void hideRemoveButton() {
        addUserButton.moveRight(MOVE_DISTANCE_CONSTANT);
        removeUserButton.hide();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        logger.info("item selected!");
        selectedUser = (User) parent.getSelectedItem();
        showRemoveButton();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        hideRemoveButton();
    }

    private void openAddUserDialog() {
        new AddUserDialog(this, userManager, arrayAdapter).show();
    }

    private void openEditUserDialog(User user) {
        new EditUserDialog(this, userManager, arrayAdapter, user).show();
    }


}
