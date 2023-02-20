package teammates.storage.sqlapi;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

import java.time.Instant;

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

    /**
     * Gets the number of users created within a specified time range.
     */
    public long getNumUsersByTimeRange(Instant startTime, Instant endTime) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<User> root = cr.from(User.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));;

        return session.createQuery(cr).getSingleResult();
    }
}
