package teammates.storage.sqlapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

/**
 * Handles CRUD operations for users.
 *
 * @see User
 * @see Instructor
 * @see Student
 */
public class UserDb extends EntitiesDb<User> {
    private static final UserDb instance = new UserDb();

    private UserDb() {
        // prevent initialization
    }

    public static UserDb inst() {
        return instance;
    }

    /**
     * Creates a user.
     */
    public User createUser(User user) throws InvalidParametersException, EntityAlreadyExistsException {
        assert user != null;

        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }

        if (getUser(user.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, user.toString()));
        }

        persist(user);
        return user;
    }

    /**
     * Gets a user by its {@code id}.
     */
    public User getUser(Integer userId) {
        assert userId != null;

        return HibernateUtil.getSessionFactory().getCurrentSession().get(User.class, userId);
    }

    /**
     * Saves an updated {@code User} to the db.
     */
    public User updateUser(User user) throws InvalidParametersException, EntityDoesNotExistException {
        assert user != null;

        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }

        if (getUser(user.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(user);
    }

    /**
     * Deletes a user.
     */
    public void deleteUser(Integer userId) {
        assert userId != null;

        User user = getUser(userId);

        if (user != null) {
            delete(user);
        }
    }
}
