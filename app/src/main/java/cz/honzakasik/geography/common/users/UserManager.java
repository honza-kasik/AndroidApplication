package cz.honzakasik.geography.common.users;

import android.support.annotation.NonNull;

import java.util.List;

public interface UserManager {

    void addUser(@NonNull User user);

    void removeUser(@NonNull User user);

    void removeUser(@NonNull Integer userId) throws DatasourceAccessException;

    User getUser(@NonNull Integer userId) throws DatasourceAccessException;

    void updateUser(@NonNull User user) throws DatasourceAccessException;

    List<User> getAllUsers() throws DatasourceAccessException;
}