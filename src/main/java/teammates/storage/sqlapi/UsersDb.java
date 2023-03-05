package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;

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
public final class UsersDb extends EntitiesDb<User> {
    private static final UsersDb instance = new UsersDb();

    private UsersDb() {
        // prevent initialization
    }

    public static UsersDb inst() {
        return instance;
    }

    /**
     * Creates an instructor.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert instructor != null;

        if (!instructor.isValid()) {
            throw new InvalidParametersException(instructor.getInvalidityInfo());
        }

        String courseId = instructor.getCourse().getId();
        String email = instructor.getEmail();

        if (hasExistingInstructor(courseId, email)) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, instructor.toString()));
        }

        persist(instructor);
        return instructor;
    }

    /**
     * Creates a student.
     */
    public Student createStudent(Student student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert student != null;

        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }

        String courseId = student.getCourse().getId();
        String email = student.getEmail();

        if (hasExistingStudent(courseId, email)) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, student.toString()));
        }

        persist(student);
        return student;
    }

    /**
     * Gets an instructor by its {@code id}.
     */
    public Instructor getInstructor(Integer id) {
        assert id != null;

        return HibernateUtil.getCurrentSession().get(Instructor.class, id);
    }

    /**
     * Gets a student by its {@code id}.
     */
    public Student getStudent(Integer id) {
        assert id != null;

        return HibernateUtil.getCurrentSession().get(Student.class, id);
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

        if (hasExistingUser(user.getId())) {
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
        Session session = HibernateUtil.getCurrentSession();
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
        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return session.createQuery(cr).getSingleResult();
    }
    
    public List<Instructor> getInstructorsForCourse(String courseId) {
        assert courseId != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return session.createQuery(cr).getResultList();
    }

    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return session.createQuery(cr).getResultList();
    }

    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot)
                .where(cb.and(
                    cb.equal(instructorRoot.get("courseId"), courseId),
                    cb.equal(instructorRoot.get("email"), userEmail)));

        return session.createQuery(cr).getSingleResultOrNull();
    }

    public Student getStudentForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(studentRoot.get("courseId"), courseId),
                    cb.equal(studentRoot.get("email"), userEmail)));

        return session.createQuery(cr).getSingleResultOrNull();
    }

    /**
     * Gets list of students by email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        assert email != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.equal(studentRoot.get("email"), email));

        return session.createQuery(cr).getResultList();
    }

    /**
     * Checks if an instructor exists by its {@code courseId} and {@code email}.
     */
    private <T extends User> boolean hasExistingInstructor(String courseId, String email) {
        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot.get("id"))
                .where(cb.and(
                    cb.equal(instructorRoot.get("courseId"), courseId),
                    cb.equal(instructorRoot.get("email"), email)));

        return session.createQuery(cr).getSingleResultOrNull() != null;
    }

    /**
     * Checks if a student exists by its {@code courseId} and {@code email}.
     */
    private <T extends User> boolean hasExistingStudent(String courseId, String email) {
        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot.get("id"))
                .where(cb.and(
                        cb.equal(studentRoot.get("courseId"), courseId),
                        cb.equal(studentRoot.get("email"), email)));

        return session.createQuery(cr).getSingleResultOrNull() != null;
    }

    /**
     * Checks if a user exists by its {@code id}.
     */
    private boolean hasExistingUser(Integer id) {
        assert id != null;

        return HibernateUtil.getCurrentSession().get(User.class, id) != null;
    }
}
