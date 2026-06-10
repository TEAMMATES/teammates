package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

/**
 * Handles CRUD operations for users.
 *
 * @see User
 */
public final class UsersDb {

    private static final UsersDb instance = new UsersDb();

    private UsersDb() {
        // prevent initialization
    }

    public static UsersDb inst() {
        return instance;
    }

    /**
     * Gets a user by its {@code id}.
     */
    public User getUser(UUID id) {
        return HibernateUtil.get(User.class, id);
    }

    /**
     * Gets a user by {@code regKey}.
     */
    public User getUserByRegKey(String regKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> userRoot = cr.from(User.class);

        cr.select(userRoot).where(cb.equal(userRoot.get("regKey"), regKey));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets users for the specified course.
     */
    public List<User> getUsersForCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> userRoot = cr.from(User.class);
        cr.select(userRoot).where(cb.equal(userRoot.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Persists an instructor.
     */
    public Instructor persistInstructor(Instructor instructor) {
        HibernateUtil.persist(instructor);
        return instructor;
    }

    /**
     * Persists a student.
     */
    public Student persistStudent(Student student) {
        HibernateUtil.persist(student);
        return student;
    }

    /**
     * Gets an instructor by its {@code id}.
     */
    public Instructor getInstructor(UUID id) {
        return HibernateUtil.get(Instructor.class, id);
    }

    /**
     * Gets an instructor by {@code googleId}.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
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
     * Gets all instructors that will be displayed to students of a course.
     */
    public List<Instructor> getInstructorsDisplayedToStudents(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot).where(cb.and(
                cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(instructorRoot.get("isDisplayedToStudents"), true)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a student by its {@code id}.
     */
    public Student getStudent(UUID id) {
        return HibernateUtil.get(Student.class, id);
    }

    /**
     * Gets an instructor by {@code accountId} and {@code courseId}.
     */
    public Instructor getInstructorByAccountId(UUID accountId, String courseId) {
        String jpql = "SELECT i FROM Instructor i WHERE i.accountId = :accountId AND i.courseId = :courseId";

        TypedQuery<Instructor> query = HibernateUtil.createQuery(jpql, Instructor.class);
        query.setParameter("accountId", accountId);
        query.setParameter("courseId", courseId);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all instructors by {@code accountId}.
     */
    public List<Instructor> getInstructorsByAccountId(UUID accountId) {
        String jpql = "SELECT i FROM Instructor i WHERE i.accountId = :accountId";

        TypedQuery<Instructor> query = HibernateUtil.createQuery(jpql, Instructor.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    /**
     * Gets a student by {@code accountId} and {@code courseId}.
     */
    public Student getStudentByAccountId(UUID accountId, String courseId) {
        String jpql = "SELECT s FROM Student s WHERE s.accountId = :accountId AND s.courseId = :courseId";

        TypedQuery<Student> query = HibernateUtil.createQuery(jpql, Student.class);
        query.setParameter("accountId", accountId);
        query.setParameter("courseId", courseId);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all students by {@code accountId}.
     */
    public List<Student> getStudentsByAccountId(UUID accountId) {
        String jpql = "SELECT s FROM Student s WHERE s.accountId = :accountId";

        TypedQuery<Student> query = HibernateUtil.createQuery(jpql, Student.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    /**
     * Gets a student by {@code googleId}.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
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
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Student> getStudentsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all instructors and students by {@code googleId}.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<User> getAllUsersByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<User> usersCr = cb.createQuery(User.class);
        Root<User> usersRoot = usersCr.from(User.class);
        Join<User, Account> accountsJoin = usersRoot.join("account");

        usersCr.select(usersRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(usersCr).getResultList();
    }

    /**
     * Gets all instructors by {@code googleId}.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Instructor> getAllInstructorsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> instructorsCr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorsRoot = instructorsCr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorsRoot.join("account");

        instructorsCr.select(instructorsRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(instructorsCr).getResultList();
    }

    /**
     * Gets all students by {@code googleId}.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Student> getAllStudentsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> studentsCr = cb.createQuery(Student.class);
        Root<Student> studentsRoot = studentsCr.from(Student.class);
        Join<Student, Account> accountsJoin = studentsRoot.join("account");

        studentsCr.select(studentsRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(studentsCr).getResultList();
    }

    /**
     * Escapes LIKE pattern metacharacters so user input is treated literally.
     */
    private static String escapeLikePattern(String pattern, char escapeChar) {
        String esc = String.valueOf(escapeChar);
        return pattern
                .replace(esc, esc + esc)
                .replace("%", esc + "%")
                .replace("_", esc + "_");
    }

    /**
     * Searches all instructors in the system.
     *
     * <p>This method should be used by admin only since the searching does not
     * restrict the visibility according to the logged-in user's google ID. This
     * is used by admin to search instructors in the whole system.
     */
    public List<Instructor> searchInstructorsInWholeSystem(String queryString) {
        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        char escapeChar = '\\';
        String escapedQuery = escapeLikePattern(queryString.toLowerCase(), escapeChar);
        String wildcardQuery = "%" + escapedQuery + "%";

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Course> coursesJoin = instructorRoot.join("course");
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account", JoinType.LEFT);

        Predicate searchPredicate = cb.or(
                cb.like(cb.lower(instructorRoot.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(instructorRoot.get("email")), wildcardQuery, escapeChar),
                cb.like(cb.lower(instructorRoot.get("courseId")), wildcardQuery, escapeChar),
                cb.like(cb.lower(coursesJoin.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(accountsJoin.get("googleId"), "")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(instructorRoot.get("displayName"), "")), wildcardQuery, escapeChar),
                cb.like(cb.lower(instructorRoot.get("role").as(String.class)), wildcardQuery, escapeChar));

        cr.select(instructorRoot)
                .where(searchPredicate)
                .orderBy(
                        cb.asc(instructorRoot.get("courseId")),
                        cb.asc(instructorRoot.get("role")),
                        cb.asc(instructorRoot.get("name")),
                        cb.asc(instructorRoot.get("email")));

        TypedQuery<Instructor> query = HibernateUtil.createQuery(cr);
        query.setMaxResults(Const.SEARCH_QUERY_SIZE_LIMIT);
        return query.getResultList();
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<Student> searchStudents(String queryString, List<Instructor> instructors) {
        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> courseIdsWithViewStudentPrivilege = null;
        if (instructors != null) {
            courseIdsWithViewStudentPrivilege = instructors.stream()
                    .map(Instructor::getCourseId)
                    .collect(Collectors.toList());

            if (courseIdsWithViewStudentPrivilege.isEmpty()) {
                return new ArrayList<>();
            }
        }

        char escapeChar = '\\';
        String escapedQuery = escapeLikePattern(queryString.toLowerCase(), escapeChar);
        String wildcardQuery = "%" + escapedQuery + "%";

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> coursesJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");
        Join<Team, Section> sectionsJoin = teamsJoin.join("section");

        Predicate searchPredicate = cb.or(
                cb.like(cb.lower(studentRoot.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(studentRoot.get("email")), wildcardQuery, escapeChar),
                cb.like(cb.lower(studentRoot.get("courseId")), wildcardQuery, escapeChar),
                cb.like(cb.lower(coursesJoin.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(teamsJoin.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(sectionsJoin.get("name")), wildcardQuery, escapeChar));

        if (courseIdsWithViewStudentPrivilege == null) {
            cr.select(studentRoot)
                    .where(searchPredicate);
        } else {
            cr.select(studentRoot)
                    .where(cb.and(searchPredicate,
                            studentRoot.get("courseId").in(courseIdsWithViewStudentPrivilege)));
        }

        cr.orderBy(
                cb.asc(studentRoot.get("courseId")),
                cb.asc(sectionsJoin.get("name")),
                cb.asc(teamsJoin.get("name")),
                cb.asc(studentRoot.get("name")),
                cb.asc(studentRoot.get("email")));

        TypedQuery<Student> query = HibernateUtil.createQuery(cr);
        query.setMaxResults(Const.SEARCH_QUERY_SIZE_LIMIT);
        return query.getResultList();
    }

    /**
     * Searches all students in the system.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     */
    public List<Student> searchStudentsInWholeSystem(String queryString) {
        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return searchStudents(queryString, null);
    }

    /**
     * Removes a user.
     */
    public <T extends User> void removeUser(T user) {
        HibernateUtil.remove(user);
    }

    /**
     * Deletes all students in the specified {@code courseId}.
     */
    public void deleteStudentsInCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<Student> cd = cb.createCriteriaDelete(Student.class);
        Root<Student> root = cd.from(Student.class);

        cd.where(cb.equal(root.get("courseId"), courseId));

        HibernateUtil.executeDelete(cd);
    }

    /**
     * Gets the list of instructors for the specified {@code courseId}.
     */
    public List<Instructor> getInstructorsForCourse(String courseId) {
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
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the instructor with the specified {@code userEmail}.
     *
     * @deprecated unused in production code, moving away from email based retrieval
     */
    @Deprecated(forRemoval = false)
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        String normalizedUserEmail = normalizeEmail(userEmail);

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot)
                .where(cb.and(
                        cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(cb.lower(instructorRoot.get("email")), normalizedUserEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets instructors with the specified {@code userEmail}.
     *
     * @deprecated unused in production code, moving away from email based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Instructor> getInstructorsForEmails(String courseId, List<String> userEmails) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        List<Predicate> predicates = new ArrayList<>();
        for (String userEmail : userEmails) {
            predicates.add(cb.equal(cb.lower(instructorRoot.get("email")), normalizeEmail(userEmail)));
        }

        cr.select(instructorRoot)
                .where(cb.and(
                        cb.equal(instructorRoot.get("courseId"), courseId),
                        cb.or(predicates.toArray(new Predicate[0]))));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the student with the specified {@code userEmail}.
     *
     * @deprecated unused in production code, moving away from email based retrieval
     */
    @Deprecated(forRemoval = false)
    public Student getStudentForEmail(String courseId, String userEmail) {
        String normalizedUserEmail = normalizeEmail(userEmail);

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.and(
                        cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(cb.lower(studentRoot.get("email")), normalizedUserEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets students with the specified {@code userEmail}.
     *
     * @deprecated unused in production code, moving away from email based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Student> getStudentsForEmails(String courseId, List<String> userEmails) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        List<Predicate> predicates = new ArrayList<>();
        for (String userEmail : userEmails) {
            predicates.add(cb.equal(cb.lower(studentRoot.get("email")), normalizeEmail(userEmail)));
        }

        cr.select(studentRoot)
                .where(cb.and(
                        cb.equal(studentRoot.get("courseId"), courseId),
                        cb.or(predicates.toArray(new Predicate[0]))));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets list of students by email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        String normalizedEmail = normalizeEmail(email);

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.equal(cb.lower(studentRoot.get("email")), normalizedEmail));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    private static String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    /**
     * Gets all instructors associated with a googleId.
     *
     * @deprecated moving away from googleId based retrieval
     */
    @Deprecated(forRemoval = false)
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account");

        cr.select(instructorRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all students of a section of a course.
     */
    public List<Student> getStudentsForSection(String sectionName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");
        Join<Team, Section> sectionJoin = teamsJoin.join("section");

        cr.select(studentRoot)
                .where(cb.and(
                        cb.equal(courseJoin.get("id"), courseId),
                        cb.equal(sectionJoin.get("name"), sectionName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all students of a team of a course.
     */
    public List<Student> getStudentsForTeam(String teamName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(studentRoot)
                .where(cb.and(
                        cb.equal(courseJoin.get("id"), courseId),
                        cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the section with the specified {@code sectionName} and {@code courseId}.
     *
     * @deprecated unused in production code
     */
    @Deprecated(forRemoval = false)
    public Section getSection(String courseId, String sectionName) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        Join<Section, Course> courseJoin = sectionRoot.join("course");

        cr.select(sectionRoot)
                .where(cb.and(
                        cb.equal(courseJoin.get("id"), courseId),
                        cb.equal(sectionRoot.get("name"), sectionName)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets a team by its {@code teamId}.
     */
    public Team getTeam(UUID teamId) {
        return HibernateUtil.get(Team.class, teamId);
    }

}
