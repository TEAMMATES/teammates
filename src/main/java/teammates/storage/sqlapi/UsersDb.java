package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
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

        persist(student);
        return student;
    }

    /**
     * Gets an instructor by its {@code id}.
     */
    public Instructor getInstructor(UUID id) {
        assert id != null;

        return HibernateUtil.get(Instructor.class, id);
    }

    /**
     * Gets an instructor by {@code regKey}.
     */
    public Instructor getInstructorByRegKey(String regKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot).where(cb.equal(instructorRoot.get("regKey"), regKey));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets an instructor by {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account");

        cr.select(instructorRoot).where(cb.and(
                cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all instructors by {@code googleId}.
     */
    public List<Instructor> getInstructorsByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account");

        cr.select(instructorRoot).where(cb.and(
                cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a student by its {@code id}.
     */
    public Student getStudent(UUID id) {
        assert id != null;

        return HibernateUtil.get(Student.class, id);
    }

    /**
     * Gets a student by {@code regKey}.
     */
    public Student getStudentByRegKey(String regKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot).where(cb.equal(studentRoot.get("regKey"), regKey));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets a student by {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.and(
                cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all students by {@code googleId}.
     */
    public List<Student> getStudentsByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.and(
                cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultList();
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
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    /**
     * Gets the number of students created within a specified time range.
     */
    public long getNumStudentsByTimeRange(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    /**
     * Gets the list of instructors for the specified {@code courseId}.
     */
    public List<Instructor> getInstructorsForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the list of students for the specified {@code courseId}.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the instructor with the specified {@code userEmail}.
     */
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot)
                .where(cb.and(
                    cb.equal(instructorRoot.get("courseId"), courseId),
                    cb.equal(instructorRoot.get("email"), userEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets the student with the specified {@code userEmail}.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(studentRoot.get("courseId"), courseId),
                    cb.equal(studentRoot.get("email"), userEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets list of students by email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        assert email != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.equal(studentRoot.get("email"), email));

        return HibernateUtil.createQuery(cr).getResultList();
    }

}
