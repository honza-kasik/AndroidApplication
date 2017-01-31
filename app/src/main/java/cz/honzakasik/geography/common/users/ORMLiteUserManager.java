package cz.honzakasik.geography.common.users;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class ORMLiteUserManager implements UserManager {

    private Logger logger = LoggerFactory.getLogger(ORMLiteUserManager.class);

    private Dao<User, Integer> userDao;

    public ORMLiteUserManager(Dao<User, ?> userDao) {
        this.userDao = (Dao<User, Integer>) userDao;
    }

    @Override
    public void addUser(@NonNull User user) {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            e.printStackTrace();//TODO
        }
    }

    @Override
    public void removeUser(@NonNull User user) {
        try {
            userDao.delete(user);
        } catch (SQLException e) {
            e.printStackTrace();//TODO
        }
    }

    @Override
    public void removeUser(@NonNull Integer userId) throws DatasourceAccessException {
        try {
            userDao.deleteById(userId);
        } catch (SQLException e) {
            throw new DatasourceAccessException(e);
        }
    }

    @Override
    public User getUser(@NonNull Integer userId) throws DatasourceAccessException {
        logger.info("Trying to obtain user with id '{}'.", userId);
        try {
            User user = userDao.queryForId(userId);
            if (user == null) {
                throw new IllegalStateException("Could not obtain user with id '" + userId + "'.");
            }
            return user;
        } catch (SQLException | IllegalStateException e) {
            logger.error("Could not obtain user with id '{}'.", userId);
            throw new DatasourceAccessException(e);
        }
    }

    @Override
    public void updateUser(@NonNull User user) throws DatasourceAccessException {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            throw new DatasourceAccessException(e);
        }
    }

    @Override
    public List<User> getAllUsers() throws DatasourceAccessException {
        try {
            return userDao.queryForAll();
        } catch (SQLException e) {
            throw new DatasourceAccessException(e);
        }
    }
}
