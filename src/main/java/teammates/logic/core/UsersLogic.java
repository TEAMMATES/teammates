package teammates.logic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorQuery;
import teammates.common.datatransfer.StudentQuery;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UserUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.HibernateUtil;
import teammates.common.util.LinksUtil;
import teammates.common.util.SanitizationHelper;
import teammates.logic.email.CourseJoinEmailsLogic;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.CourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;
import teammates.storage.api.UsersDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InstructorUpdateRequest;

/**
 * Handles operations related to user (instructor & student).
 *
 * @see User
 * @see UsersDb
 */
public final class UsersLogic {

    private static final UsersLogic instance = new UsersLogic();

    private static final int MAX_KEY_REGENERATION_TRIES = 10;

    private UsersDb usersDb;

    private CoursesLogic coursesLogic;
    private CourseJoinEmailsLogic courseJoinEmailsLogic;
    private FeedbackSessionsLogic feedbackSessionsLogic;

    private FeedbackResponsesLogic feedbackResponsesLogic;
    private InstructorPermissionsLogic instructorPermissionsLogic;

    private UsersLogic() {
        // prevent initialization
    }

    public static UsersLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersDb usersDb, CoursesLogic coursesLogic,
                               CourseJoinEmailsLogic courseJoinEmailsLogic,
                               FeedbackSessionsLogic feedbackSessionsLogic,
                               FeedbackResponsesLogic feedbackResponsesLogic,
                               InstructorPermissionsLogic instructorPermissionsLogic) {
        this.usersDb = usersDb;
        this.coursesLogic = coursesLogic;
        this.courseJoinEmailsLogic = courseJoinEmailsLogic;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
        this.feedbackResponsesLogic = feedbackResponsesLogic;
        this.instructorPermissionsLogic = instructorPermissionsLogic;
    }

    /**
     * Gets user associated with {@code id}.
     */
    public User getUser(UUID id) {
        return usersDb.getUser(id);
    }

    /**
     * Get user by registration key.
     */
    public User getUserByRegistrationKey(String regKey) {
        Objects.requireNonNull(regKey);
        return usersDb.getUserByRegKey(regKey);
    }

    /**
     * Gets users for the specified course.
     */
    public List<User> getUsersForCourse(String courseId) {
        List<User> userReturnList = usersDb.getUsersForCourse(courseId);
        sortByName(userReturnList);

        return userReturnList;
    }

    /**
     * Creates an instructor with the given attributes.
     *
     * @param account optional account to associate with the instructor at creation time
     * @return the created instructor
     * @throws InvalidParametersException   if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public Instructor createInstructor(Course course, String name, String email,
            boolean isDisplayedToStudents, String displayedName, InstructorPermissionRole role,
            @Nullable Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Instructor instructor = new Instructor(course, name, email, isDisplayedToStudents, displayedName, role);
        if (account != null) {
            instructor.setAccount(account);
        }

        validateUser(instructor);

        if (getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()) != null) {
            throw new EntityAlreadyExistsException("Instructor already exists.");
        }

        return usersDb.persistInstructor(instructor);
    }

    /**
     * Creates an instructor from a create request.
     * Handles sanitization, entity construction, and custom privilege storage.
     *
     * @return the created instructor
     * @throws InvalidParametersException   if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists
     */
    public Instructor createInstructor(String courseId, InstructorCreateRequest request)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = coursesLogic.getCourse(courseId);

        String instrName = SanitizationHelper.sanitizeName(request.getName());
        String instrEmail = SanitizationHelper.sanitizeEmail(request.getEmail());
        InstructorPermissionRole role = InstructorPermissionRole.getEnum(request.getRoleName());

        String instrDisplayedName = request.getDisplayName();
        if (instrDisplayedName == null || instrDisplayedName.isEmpty()) {
            instrDisplayedName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
        } else {
            instrDisplayedName = SanitizationHelper.sanitizeName(instrDisplayedName);
        }

        Instructor createdInstructor = createInstructor(
                course, instrName, instrEmail, request.getIsDisplayedToStudent(), instrDisplayedName, role, null);

        InstructorPrivileges requestPrivileges = request.getPrivileges();
        if (requestPrivileges != null
                && Const.InstructorPermissionRoleNames.CUSTOM.equals(request.getRoleName())) {
            instructorPermissionsLogic.saveInstructorPrivileges(createdInstructor, requestPrivileges);
        }

        return createdInstructor;
    }

    /**
     * Updates an instructor.
     *
     * @return updated instructor
     * @throws InvalidParametersException if the instructor update request is invalid
     * @throws InstructorUpdateException if the update violates instructor validity
     * @throws EntityDoesNotExistException if the instructor does not exist in the database
     */
    public Instructor updateInstructorCascade(UUID id, InstructorUpdateRequest instructorRequest) throws
            InvalidParametersException, InstructorUpdateException, EntityDoesNotExistException {
        Instructor instructor = getInstructor(id);

        if (instructor == null) {
            throw new EntityDoesNotExistException("Trying to update an instructor that does not exist.");
        }

        verifyAtLeastOneInstructorIsDisplayed(
                instructor.getCourseId(), instructor.isDisplayedToStudents(), instructorRequest.getIsDisplayedToStudent());

        String newDisplayName = instructorRequest.getDisplayName();
        if (newDisplayName == null || newDisplayName.isEmpty()) {
            newDisplayName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
        }

        instructor.setName(SanitizationHelper.sanitizeName(instructorRequest.getName()));
        instructor.setEmail(SanitizationHelper.sanitizeEmail(instructorRequest.getEmail()));
        instructor.setRole(InstructorPermissionRole.getEnum(instructorRequest.getRoleName()));
        boolean isCustomRole = Const.InstructorPermissionRoleNames.CUSTOM.equals(instructorRequest.getRoleName());
        if (isCustomRole && instructorRequest.getPrivileges() != null) {
            instructorPermissionsLogic.saveInstructorPrivileges(instructor, instructorRequest.getPrivileges());
        }

        instructor.setDisplayName(SanitizationHelper.sanitizeName(newDisplayName));
        instructor.setDisplayedToStudents(instructorRequest.getIsDisplayedToStudent());

        validateUser(instructor);

        updateToEnsureValidityOfInstructorsForTheCourse(instructor);

        return instructor;
    }

    /**
     * Verifies that at least one instructor is displayed to studens.
     *
     * @throws InstructorUpdateException if there is no instructor displayed to students.
     */
    void verifyAtLeastOneInstructorIsDisplayed(String courseId, boolean isOriginalInstructorDisplayed,
                                               boolean isEditedInstructorDisplayed)
            throws InstructorUpdateException {
        List<Instructor> instructorsDisplayed = usersDb.getInstructorsDisplayedToStudents(courseId);
        boolean isEditedInstructorChangedToNonVisible = isOriginalInstructorDisplayed && !isEditedInstructorDisplayed;
        boolean isNoInstructorMadeVisible = instructorsDisplayed.isEmpty() && !isEditedInstructorDisplayed;

        if (isNoInstructorMadeVisible || instructorsDisplayed.size() == 1 && isEditedInstructorChangedToNonVisible) {
            throw new InstructorUpdateException("At least one instructor must be displayed to students");
        }
    }

    /**
     * Creates a student with the given parameters.
     */
    public Student createStudent(Course course, Team team, String name, String email, String comments)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Student student = new Student(course, name, email, comments);
        if (getStudentForEmail(course.getId(), email) != null) {
            throw new EntityAlreadyExistsException(String.format("Student with email %s already exists.", email));
        }

        team.addUser(student);
        validateUser(student);
        return usersDb.persistStudent(student);
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id Id of Instructor.
     * @return Returns Instructor if found else null.
     */
    public Instructor getInstructor(UUID id) {
        assert id != null;

        return usersDb.getInstructor(id);
    }

    /**
     * Gets instructor associated with {@code id} in the specified course.
     */
    public Instructor getInstructorOfCourse(String courseId, UUID id) {
        Objects.requireNonNull(courseId);
        Objects.requireNonNull(id);

        Instructor instructor = getInstructor(id);
        return instructor != null && courseId.equals(instructor.getCourseId()) ? instructor : null;
    }

    /**
     * Gets the instructor with the specified email.
     */
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        return usersDb.getInstructorForEmail(courseId, userEmail);
    }

    /**
     * Gets an instructor by associated {@code regkey}.
     */
    public Instructor getInstructorByRegistrationKey(String regKey) {
        User user = getUserByRegistrationKey(regKey);
        if (user instanceof Instructor instructor) {
            return instructor;
        }

        return null;
    }

    /**
     * Gets instructors matching the specified query.
     *
     * @return List of found instructors. Returns an empty list if no results are found.
     */
    public List<Instructor> getInstructors(InstructorQuery query) {
        return usersDb.getInstructors(query);
    }

    /**
     * Deletes an instructor or student.
     *
     * <p>Fails silently if the user doesn't exist.</p>
     */
    public <T extends User> void deleteUser(T user) {
        if (user == null) {
            return;
        }

        usersDb.removeUser(user);
    }

    /**
     * Deletes an instructor by user ID and cascades deletion to
     * associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the instructor does not exist.
     */
    public void deleteInstructorCascade(UUID userId) throws InvalidOperationException {
        Instructor instructor = getInstructor(userId);
        if (instructor == null) {
            return;
        }

        if (!hasAlternativeInstructor(instructor)) {
            throw new InvalidOperationException(
                    "The instructor you are trying to delete is the last instructor in the course. "
                            + "Deleting the last instructor from the course is not allowed.");
        }

        deleteUser(instructor);
    }

    /**
     * Returns true if there is at least one joined instructor (other than the instructor to delete)
     * with the privilege of modifying instructors and at least one instructor visible to the students.
     */
    private boolean hasAlternativeInstructor(Instructor instructorToDelete) {
        List<Instructor> instructors = getInstructorsForCourse(instructorToDelete.getCourseId());
        boolean hasAlternativeModifyInstructor = false;
        boolean hasAlternativeVisibleInstructor = false;

        for (Instructor instr : instructors) {
            hasAlternativeModifyInstructor = hasAlternativeModifyInstructor || instr.isRegistered()
                    && !instr.equals(instructorToDelete)
                    && instructorPermissionsLogic.hasPermissions(instr,
                            Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);

            hasAlternativeVisibleInstructor = hasAlternativeVisibleInstructor
                    || instr.isDisplayedToStudents()
                    && !instr.equals(instructorToDelete);

            if (hasAlternativeModifyInstructor && hasAlternativeVisibleInstructor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the list of instructors with co-owner role in a course.
     */
    public List<Instructor> getCoOwnersForCourse(String courseId) {
        List<Instructor> instructors = getInstructorsForCourse(courseId);
        List<Instructor> coOwners = new ArrayList<>();
        for (Instructor instructor : instructors) {
            if (!instructor.hasCoownerRole()) {
                continue;
            }
            coOwners.add(instructor);
        }
        return coOwners;
    }

    /**
     * Gets the co-owner contacts for the specified course.
     */
    public List<EmailContact> getCoOwnerContacts(String courseId) {
        return getCoOwnersForCourse(courseId).stream()
                .map(coOwner -> new EmailContact(coOwner.getName(), coOwner.getEmail()))
                .toList();
    }

    /**
     * Enqueues the post-join course registration confirmation email for the given user.
     */
    public void enqueueUserCourseRegisteredEmail(User user) {
        Course course = user.getCourse();
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                getCoOwnerContacts(course.getId()));
        UserCourseRegisteredEmailContext userContext = new UserCourseRegisteredEmailContext(
                user.getEmail(),
                user.getName(),
                user instanceof Instructor,
                user instanceof Instructor ? LinksUtil.getInstructorHomePageUrl() : LinksUtil.getStudentHomePageUrl());
        courseJoinEmailsLogic.enqueueUserCourseRegisteredEmail(courseContext, userContext);
    }

    /**
     * Enqueues the student course join invitation email for the given student.
     */
    public void enqueueStudentCourseJoinEmail(Student student) {
        Course course = student.getCourse();
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                getCoOwnerContacts(course.getId()));
        StudentCourseJoinEmailContext studentContext = new StudentCourseJoinEmailContext(
                student.getEmail(),
                student.getName(),
                LinksUtil.getStudentCourseJoinUrl(student.getRegKey()));
        courseJoinEmailsLogic.enqueueStudentCourseJoinEmail(courseContext, studentContext);
    }

    /**
     * Enqueues student course join invitation emails for all unregistered students in
     * the given course.
     */
    public void enqueueStudentCourseJoinEmailsForCourse(String courseId) {
        Course course = coursesLogic.getCourse(courseId);
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                getCoOwnerContacts(course.getId()));
        List<StudentCourseJoinEmailContext> studentContexts = getUnregisteredStudentsForCourse(courseId).stream()
                .map(student -> new StudentCourseJoinEmailContext(
                        student.getEmail(),
                        student.getName(),
                        LinksUtil.getStudentCourseJoinUrl(student.getRegKey())))
                .toList();
        courseJoinEmailsLogic.enqueueStudentCourseJoinEmails(courseContext, studentContexts);
    }

    /**
     * Enqueues the student course rejoin email after account unlink.
     */
    public void enqueueStudentCourseRejoinAfterUnlinkAccountEmail(Student student) {
        Course course = student.getCourse();
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                getCoOwnerContacts(course.getId()));
        CourseRejoinAfterUnlinkEmailContext studentContext = new CourseRejoinAfterUnlinkEmailContext(
                student.getEmail(),
                student.getName(),
                LinksUtil.getStudentCourseJoinUrl(student.getRegKey()));
        courseJoinEmailsLogic.enqueueStudentCourseRejoinAfterUnlinkAccountEmail(courseContext, studentContext);
    }

    /**
     * Enqueues the instructor course join invitation email.
     */
    public void enqueueInstructorCourseJoinEmail(Instructor inviter, Instructor instructor) {
        Course course = instructor.getCourse();
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                List.of());
        InstructorCourseJoinEmailContext instructorContext = new InstructorCourseJoinEmailContext(
                instructor.getEmail(),
                instructor.getName(),
                LinksUtil.getInstructorCourseJoinUrl(instructor.getRegKey()),
                inviter.getName(),
                inviter.getEmail());
        courseJoinEmailsLogic.enqueueInstructorCourseJoinEmail(courseContext, instructorContext);
    }

    /**
     * Enqueues the instructor course rejoin email after account unlink.
     */
    public void enqueueInstructorCourseRejoinAfterUnlinkAccountEmail(Instructor instructor) {
        Course course = instructor.getCourse();
        CourseEmailContext courseContext = new CourseEmailContext(
                course.getId(),
                course.getName(),
                List.of());
        CourseRejoinAfterUnlinkEmailContext instructorContext = new CourseRejoinAfterUnlinkEmailContext(
                instructor.getEmail(),
                instructor.getName(),
                LinksUtil.getInstructorCourseJoinUrl(instructor.getRegKey()));
        courseJoinEmailsLogic.enqueueInstructorCourseRejoinAfterUnlinkAccountEmail(courseContext, instructorContext);
    }

    /**
     * Sends the requested join reminder email for the given user and returns the
     * corresponding status message.
     */
    public String sendJoinReminderForUser(UUID userId, @Nullable Instructor inviter)
            throws EntityDoesNotExistException {
        User user = getJoinReminderUserOrThrow(userId);
        if (user instanceof Student student) {
            enqueueStudentCourseJoinEmail(student);
            return "An email has been sent to " + student.getEmail();
        }
        if (user instanceof Instructor instructor) {
            if (inviter == null) {
                throw new EntityDoesNotExistException("Inviter does not exist.");
            }
            enqueueInstructorCourseJoinEmail(inviter, instructor);
            return "An email has been sent to " + instructor.getEmail();
        }

        // This line should never be reached because the user should either be a student or an instructor.
        throw new AssertionError("Invalid user type for join reminder: " + user.getClass().getName());
    }

    /**
     * Sends join reminder emails to all unregistered students in the given course
     * and returns the corresponding status message.
     */
    public String sendJoinReminderForStudentsInCourse(String courseId) throws EntityDoesNotExistException {
        enqueueStudentCourseJoinEmailsForCourse(getJoinReminderCourseOrThrow(courseId).getId());
        return "Emails have been sent to unregistered students.";
    }

    /**
     * Gets a list of instructors for the specified course.
     */
    public List<Instructor> getInstructorsForCourse(String courseId) {
        List<Instructor> instructorReturnList = usersDb.getInstructorsForCourse(courseId);
        sortByName(instructorReturnList);

        return instructorReturnList;
    }

    /**
     * Gets the instructors that should be displayed to students for the specified course.
     */
    public List<Instructor> getDisplayedInstructorsForCourse(String courseId) {
        List<Instructor> instructorReturnList = usersDb.getInstructorsDisplayedToStudents(courseId);
        sortByName(instructorReturnList);

        return instructorReturnList;
    }

    /**
     * Regenerates the registration key for the user with {@code userId}.
     *
     * @return the user with the new registration key.
     * @throws UserUpdateException if system was unable to generate a new registration key.
     * @throws EntityDoesNotExistException if the user does not exist.
     */
    public User regenerateUserRegistrationKey(UUID userId)
            throws EntityDoesNotExistException, UserUpdateException {
        User user = usersDb.getUser(userId);
        if (user == null) {
            String errorMessage = String.format("The user with ID [%s] could not be found.", userId);
            throw new EntityDoesNotExistException(errorMessage);
        }

        String oldKey = user.getRegKey();
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            user.generateNewRegistrationKey();
            if (!user.getRegKey().equals(oldKey)) {
                return user;
            }
            numTries++;
        }

        throw new UserUpdateException("Could not regenerate a new course registration key for the user.");
    }

    /**
     * Regenerates the registration key for the user with {@code userId} and enqueues the
     * corresponding feedback session summary email.
     */
    public User regenerateUserRegKeyAndEnqueueSummaryEmail(UUID userId)
            throws EntityDoesNotExistException, UserUpdateException {
        User user = regenerateUserRegistrationKey(userId);
        feedbackSessionsLogic.enqueueFeedbackSessionSummaryEmail(
                user,
                user instanceof Student
                        ? EmailType.STUDENT_COURSE_LINKS_REGENERATED
                        : EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        return user;
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id Id of Student.
     * @return Returns Student if found else null.
     */
    public Student getStudent(UUID id) {
        assert id != null;

        return usersDb.getStudent(id);
    }

    /**
     * Gets student associated with {@code id} in the specified course.
     */
    public Student getStudentOfCourse(String courseId, UUID id) {
        Objects.requireNonNull(courseId);
        Objects.requireNonNull(id);

        Student student = getStudent(id);
        return student != null && courseId.equals(student.getCourseId()) ? student : null;
    }

    /**
     * Gets the student with the specified email.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        return usersDb.getStudentForEmail(courseId, userEmail);
    }

    /**
     * Gets a list of students with the specified email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        return usersDb.getAllStudentsForEmail(email);
    }

    /**
     * Gets a list of students for the specified course.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        List<Student> studentReturnList = usersDb.getStudentsForCourse(courseId);
        sortByName(studentReturnList);

        return studentReturnList;
    }

    /**
     * Gets the students for the supplied query.
     */
    public List<Student> getStudents(StudentQuery query) {
        return usersDb.getStudents(query);
    }

    /**
     * Gets the students visible to the given account for the supplied query.
     */
    public List<Student> getStudentsVisibleToAccount(StudentQuery query, Account account) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(account);

        // TODO: This does not take into account section level permissions.
        // This is a known issue and will be addressed in a future update after permissions have been simplified.
        List<Instructor> instructors = getInstructorsByAccountId(account.getId());
        String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT;
        List<String> visibleCourseIds = instructors.stream()
                .filter(i -> instructorPermissionsLogic.hasPermissions(i, privilegeName))
                .map(Instructor::getCourseId)
                .toList();

        if (visibleCourseIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> requestedCourseIds = query.courseIds();
        List<String> effectiveCourseIds = requestedCourseIds == null
                ? visibleCourseIds
                : requestedCourseIds.stream()
                        .filter(visibleCourseIds::contains)
                        .toList();
        if (effectiveCourseIds.isEmpty()) {
            return new ArrayList<>();
        }

        return usersDb.getStudents(new StudentQuery(effectiveCourseIds, query.searchKey(), query.limit()));
    }

    /**
     * Gets a list of unregistered students for the specified course.
     */
    public List<Student> getUnregisteredStudentsForCourse(String courseId) {
        List<Student> students = getStudentsForCourse(courseId);
        List<Student> unregisteredStudents = new ArrayList<>();

        for (Student s : students) {
            if (s.getAccount() == null) {
                unregisteredStudents.add(s);
            }
        }

        return unregisteredStudents;
    }

    /**
     * Gets all students of a section.
     */
    public List<Student> getStudentsForSection(String sectionName, String courseId) {
        return usersDb.getStudentsForSection(sectionName, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<Student> getStudentsForTeam(String teamName, String courseId) {
        List<Student> studentReturnList = usersDb.getStudentsForTeam(teamName, courseId);
        sortByName(studentReturnList);

        return studentReturnList;
    }

    /**
     * Gets all students of a team by team ID.
     */
    public List<Student> getStudentsForTeam(UUID teamId, String courseId) {
        List<Student> studentReturnList = usersDb.getStudentsForTeam(teamId, courseId);
        sortByName(studentReturnList);

        return studentReturnList;
    }

    /**
     * Gets a student by associated {@code regkey}.
     */
    public Student getStudentByRegistrationKey(String regKey) {
        assert regKey != null;
        User user = getUserByRegistrationKey(regKey);
        if (user instanceof Student student) {
            return student;
        }

        return null;
    }

    /**
     * Gets a student by associated {@code accountId} and {@code courseId}.
     */
    public Student getStudentByAccountId(UUID accountId, String courseId) {
        Objects.requireNonNull(courseId);
        Objects.requireNonNull(accountId);

        return usersDb.getStudentByAccountId(accountId, courseId);
    }

    /**
     * Gets all students by associated {@code accountId}.
     */
    public List<Student> getStudentsByAccountId(UUID accountId) {
        Objects.requireNonNull(accountId);

        return usersDb.getStudentsByAccountId(accountId);
    }

    /**
     * Gets an instructor by associated {@code accountId} and {@code courseId}.
     */
    public Instructor getInstructorByAccountId(UUID accountId, String courseId) {
        Objects.requireNonNull(courseId);
        Objects.requireNonNull(accountId);

        return usersDb.getInstructorByAccountId(accountId, courseId);
    }

    /**
     * Gets all instructors by associated {@code accountId}.
     */
    public List<Instructor> getInstructorsByAccountId(UUID accountId) {
        Objects.requireNonNull(accountId);

        return usersDb.getInstructorsByAccountId(accountId);
    }

    /**
     * Gets team by ID.
     */
    public Team getTeam(UUID teamId) {
        return usersDb.getTeam(teamId);
    }

    /**
     * Gets the section with the name in a particular course.
     */
    public Section getSection(String courseId, String sectionName) {
        return usersDb.getSection(courseId, sectionName);
    }

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param instructorToEdit Instructor that will be edited.
     *                         This may be modified within the method.
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(Instructor instructorToEdit) {
        String courseId = instructorToEdit.getCourseId();
        List<Instructor> instructors = getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        Instructor instrWithModifyInstructorPrivilege = null;
        for (Instructor instructor : instructors) {
            if (instructorPermissionsLogic.hasPermissions(instructor,
                    Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.getAccountId()
                .equals(instructorToEdit.getAccountId()));
        if (isLastRegInstructorWithPrivilege) {
            InstructorPrivileges privileges = instructorPermissionsLogic.getInstructorPrivileges(instructorToEdit);
            privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
            instructorPermissionsLogic.saveInstructorPrivileges(instructorToEdit, privileges);
        }
    }

    /**
     * Deletes a student by user ID along with its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteStudentCascade(UUID userId) {
        Student student = getStudent(userId);

        if (student == null) {
            return;
        }

        String courseId = student.getCourseId();
        deleteUser(student);
        HibernateUtil.flushSession();
        feedbackResponsesLogic.updateRankRecipientQuestionResponsesAfterDeletingStudent(courseId);
    }

    /**
     * Deletes students in the course.
     */
    public void deleteStudentsInCourse(String courseId) {
        usersDb.deleteStudentsInCourse(courseId);
    }

    /**
     * Updates a student by attributes to update. If an attribute is null, it will not be updated.
     */
    public Student updateStudentCascade(Student student, String newEmail, String newName, Team newTeam, String newComments)
            throws InvalidParametersException {
        if (newName != null) {
            student.setName(newName);
        }

        if (newEmail != null && !student.getEmail().equals(newEmail)) {
            student.setEmail(newEmail);
        }

        if (newTeam != null && !student.getTeam().equals(newTeam)) {
            student.setTeam(newTeam);
        }

        if (newComments != null) {
            student.setComments(newComments);
        }

        validateUser(student);

        return student;
    }

    /**
     * Unlinks the account associated with the user profile without deleting
     * either entity, allowing the profile to be linked to a different account.
     */
    public User unlinkAccount(UUID userId) throws EntityDoesNotExistException {
        User user = getUser(userId);

        if (user == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + "User [id=" + userId + "]");
        }

        user.setAccount(null);
        return user;
    }

    /**
     * Unlinks the account associated with the user profile and enqueues the
     * corresponding rejoin email.
     */
    public User unlinkAccountAndNotify(UUID userId) throws EntityDoesNotExistException {
        User user = unlinkAccount(userId);
        if (user instanceof Student student) {
            enqueueStudentCourseRejoinAfterUnlinkAccountEmail(student);
        } else if (user instanceof Instructor instructor) {
            enqueueInstructorCourseRejoinAfterUnlinkAccountEmail(instructor);
        }
        return user;
    }

    /**
     * Sorts the instructors list alphabetically by name.
     */
    public static <T extends User> void sortByName(List<T> users) {
        users.sort(Comparator.comparing(user -> user.getName().toLowerCase()));
    }

    private User getJoinReminderUserOrThrow(UUID userId) throws EntityDoesNotExistException {
        User user = getUser(userId);
        if (user == null) {
            throw new EntityDoesNotExistException("User with ID " + userId + " does not exist.");
        }
        return user;
    }

    private Course getJoinReminderCourseOrThrow(String courseId) throws EntityDoesNotExistException {
        Course course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException("Course with ID " + courseId + " does not exist.");
        }
        return course;
    }

    private void validateUser(User user) throws InvalidParametersException {
        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }
    }

    /**
     * Gets createdAt timestamps of students created within the given time range.
     */
    public List<Instant> getStudentCreatedAtTimestampsForTimeRange(Instant startTime, Instant endTime) {
        return usersDb.getStudentCreatedAtTimestampsForTimeRange(startTime, endTime);
    }

    /**
     * Gets createdAt timestamps of instructors created within the given time range.
     */
    public List<Instant> getInstructorCreatedAtTimestampsForTimeRange(Instant startTime, Instant endTime) {
        return usersDb.getInstructorCreatedAtTimestampsForTimeRange(startTime, endTime);
    }
}
