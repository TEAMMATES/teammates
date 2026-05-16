package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
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
     * Creates an instructor.
     */
    public Instructor createInstructor(Instructor instructor) {
        assert instructor != null;

        HibernateUtil.persist(instructor);
        return instructor;
    }

    /**
     * Creates a student.
     */
    public Student createStudent(Student student) {
        assert student != null;

        HibernateUtil.persist(student);
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
    public List<Student> getStudentsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of students by {@code teamName} and {@code courseId}.
     */
    public List<Student> getStudentsByTeamName(String teamName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        studentRoot.alias("student");

        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(studentRoot).where(cb.and(
                cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all instructors and students by {@code googleId}.
     */
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
     */
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
     */
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
                    .filter(i -> i.getPrivileges().getCourseLevelPrivileges().isCanViewStudentInSections())
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
     * Deletes a user.
     */
    public <T extends User> void deleteUser(T user) {
        HibernateUtil.remove(user);
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
        assert courseId != null && !courseId.isEmpty();

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
     */
    public List<Instructor> getInstructorsForEmails(String courseId, List<String> userEmails) {
        assert courseId != null;
        assert userEmails != null;

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
     * Gets a non-soft-deleted instructor with the specified {@code email} and {@code institute}.
     */
    public Instructor getInstructorByEmailAndInstitute(String email, String institute) {
        assert email != null;
        assert institute != null;
        String normalizedEmail = normalizeEmail(email);

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Course> courseJoin = instructorRoot.join("course");

        cr.select(instructorRoot)
                .where(cb.and(
                cb.equal(cb.lower(instructorRoot.get("email")), normalizedEmail),
                        cb.equal(courseJoin.get("institute"), institute),
                        cb.isNull(courseJoin.get("deletedAt"))));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets the student with the specified {@code userEmail}.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;
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
     */
    public List<Student> getStudentsForEmails(String courseId, List<String> userEmails) {
        assert courseId != null;
        assert userEmails != null;

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
        assert email != null;
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
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        assert googleId != null;

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
        assert sectionName != null;
        assert courseId != null;

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
        assert teamName != null;
        assert courseId != null;

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
     * Gets count of students of a team of a course.
     */
    public long getStudentCountForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(cb.count(studentRoot.get("id")))
                .where(cb.and(
                        cb.equal(courseJoin.get("id"), courseId),
                        cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    /**
     * Gets the section with the specified {@code sectionName} and {@code courseId}.
     */
    public Section getSection(String courseId, String sectionName) {
        assert sectionName != null;

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

    /**
     * Gets a team by its {@code section} and {@code teamName}.
     */
    public Team getTeam(Section section, String teamName) {
        assert teamName != null;
        assert section != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Team> cr = cb.createQuery(Team.class);
        Root<Team> teamRoot = cr.from(Team.class);
        Join<Team, Section> sectionJoin = teamRoot.join("section");

        cr.select(teamRoot)
                .where(cb.and(
                        cb.equal(sectionJoin.get("id"), section.getId()),
                        cb.equal(teamRoot.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

}
