package teammates.storage.sqlapi;

import java.time.Instant;

import org.hibernate.Session;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for users.
 *
 * @see User
 */
public final class UserDb extends EntitiesDb<User> {
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
    public <T extends User> T createUser(T user)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert user != null;

        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }

        if (getInstructor(user.getId()) == null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, user.toString()));
        }

        if (getStudent(user.getId()) == null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, user.toString()));
        }

        persist(user);
        return user;
    }

    /**
     * Gets an instructor by its {@code id}.
     */
    public Instructor getInstructor(Integer id) {
        assert id != null;

        return HibernateUtil.getSessionFactory().getCurrentSession().get(Instructor.class, id);
    }
    
    /**
     * Gets a student by its {@code id}.
     */
    public Student getStudent(Integer id) {
        assert id != null;

        return HibernateUtil.getSessionFactory().getCurrentSession().get(Student.class, id);
    }

    /**
     * Saves an updated {@code User} to the db.
     */
    public <T extends User> T updateUser(T user)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert user != null;

        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }

        if (getInstructor(user.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        if (getStudent(user.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(user);
    }

    /**
     * Deletes a user.
     */
    public <T extends User> void deleteUser(T user) {
        if (user != null) {
            delete(user);
        }
    }

    /**
     * Gets the number of instructors created within a specified time range.
     */
    public long getNumInstructorsByTimeRange(Instant startTime, Instant endTime) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return session.createQuery(cr).getSingleResult();
    }

     /**
     * Gets the number of students created within a specified time range.
     */
    public long getNumStudentsByTimeRange(Instant startTime, Instant endTime) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return session.createQuery(cr).getSingleResult();
    }
}
