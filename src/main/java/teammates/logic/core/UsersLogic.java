package teammates.logic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import teammates.common.datatransfer.EnrollResults;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.UsersDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Handles operations related to user (instructor & student).
 *
 * @see User
 * @see UsersDb
 */
public final class UsersLogic {

    static final String ERROR_INVALID_TEAM_NAME_INSTRUCTION =
            "Please use different team names in different sections.";
    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT =
            "You are trying enroll more than %s students in section \"%s\".";
    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION =
            "To avoid performance problems, please do not enroll more than %s students in a single section.";

    private static final UsersLogic instance = new UsersLogic();

    private static final int MAX_KEY_REGENERATION_TRIES = 10;

    private UsersDb usersDb;

    private AccountsLogic accountsLogic;

    private CoursesLogic coursesLogic;

    private FeedbackResponsesLogic feedbackResponsesLogic;

    private FeedbackResponseCommentsLogic feedbackResponseCommentsLogic;

    private DeadlineExtensionsLogic deadlineExtensionsLogic;

    private UsersLogic() {
        // prevent initialization
    }

    public static UsersLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersDb usersDb, AccountsLogic accountsLogic, CoursesLogic coursesLogic,
                               FeedbackResponsesLogic feedbackResponsesLogic,
                               FeedbackResponseCommentsLogic feedbackResponseCommentsLogic,
                               DeadlineExtensionsLogic deadlineExtensionsLogic) {
        this.usersDb = usersDb;
        this.accountsLogic = accountsLogic;
        this.coursesLogic = coursesLogic;
        this.feedbackResponsesLogic = feedbackResponsesLogic;
        this.feedbackResponseCommentsLogic = feedbackResponseCommentsLogic;
        this.deadlineExtensionsLogic = deadlineExtensionsLogic;
    }

    /**
     * Gets user associated with {@code id}.
     */
    public User getUser(UUID id) {
        return usersDb.getUser(id);
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
     * Create an instructor.
     *
     * @return the created instructor
     * @throws InvalidParametersException   if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the
     *                                      database.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        validateUser(instructor);

        if (getInstructorForEmail(instructor.getCourseId(), instructor.getEmail()) != null) {
            throw new EntityAlreadyExistsException("Instructor already exists.");
        }

        return usersDb.createInstructor(instructor);
    }

    /**
     * Updates an instructor and cascades to responses and comments if needed.
     *
     * @return updated instructor
     * @throws InvalidParametersException if the instructor update request is invalid
     * @throws InstructorUpdateException if the update violates instructor validity
     * @throws EntityDoesNotExistException if the instructor does not exist in the database
     */
    public Instructor updateInstructorCascade(String courseId, InstructorCreateRequest instructorRequest) throws
            InvalidParametersException, InstructorUpdateException, EntityDoesNotExistException {
        Instructor instructor;
        String instructorId = instructorRequest.getId();
        if (instructorId == null) {
            instructor = getInstructorForEmail(courseId, instructorRequest.getEmail());
        } else {
            instructor = getInstructorByGoogleId(courseId, instructorId);
        }

        if (instructor == null) {
            throw new EntityDoesNotExistException("Trying to update an instructor that does not exist.");
        }

        verifyAtLeastOneInstructorIsDisplayed(
                courseId, instructor.isDisplayedToStudents(), instructorRequest.getIsDisplayedToStudent());

        String originalEmail = instructor.getEmail();
        boolean needsCascade = false;

        String newDisplayName = instructorRequest.getDisplayName();
        if (newDisplayName == null || newDisplayName.isEmpty()) {
            newDisplayName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
        }

        instructor.setName(SanitizationHelper.sanitizeName(instructorRequest.getName()));
        instructor.setEmail(SanitizationHelper.sanitizeEmail(instructorRequest.getEmail()));
        instructor.setRole(InstructorPermissionRole.getEnum(instructorRequest.getRoleName()));
        instructor.setPrivileges(new InstructorPrivileges(instructorRequest.getRoleName()));
        instructor.setDisplayName(SanitizationHelper.sanitizeName(newDisplayName));
        instructor.setDisplayedToStudents(instructorRequest.getIsDisplayedToStudent());

        String newEmail = instructor.getEmail();

        if (!originalEmail.equals(newEmail)) {
            needsCascade = true;
        }

        validateUser(instructor);

        if (needsCascade) {
            // cascade responses
            List<FeedbackResponse> responsesFromUser =
                    feedbackResponsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, originalEmail);
            for (FeedbackResponse responseFromUser : responsesFromUser) {
                FeedbackQuestion question = responseFromUser.getFeedbackQuestion();
                if (question.getGiverType() == FeedbackParticipantType.INSTRUCTORS
                        || question.getGiverType() == FeedbackParticipantType.SELF) {
                    responseFromUser.setGiver(newEmail);
                }
            }
            List<FeedbackResponse> responsesToUser =
                    feedbackResponsesLogic.getFeedbackResponsesForRecipientForCourse(courseId, originalEmail);
            for (FeedbackResponse responseToUser : responsesToUser) {
                FeedbackQuestion question = responseToUser.getFeedbackQuestion();
                if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS
                        || question.getGiverType() == FeedbackParticipantType.INSTRUCTORS
                        && question.getRecipientType() == FeedbackParticipantType.SELF) {
                    responseToUser.setRecipient(newEmail);
                }
            }
            // cascade comments
            feedbackResponseCommentsLogic.updateFeedbackResponseCommentsEmails(courseId, originalEmail, newEmail);
        }

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
        return usersDb.createStudent(student);
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
     * Gets the instructor with the specified email.
     */
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        return usersDb.getInstructorForEmail(courseId, userEmail);
    }

    /**
     * Gets instructors matching any of the specified emails.
     */
    public List<Instructor> getInstructorsForEmails(String courseId, List<String> userEmails) {
        return usersDb.getInstructorsForEmails(courseId, userEmails);
    }

    /**
     * Gets an instructor by associated {@code regkey}.
     */
    public Instructor getInstructorByRegistrationKey(String regKey) {
        assert regKey != null;

        return usersDb.getInstructorByRegKey(regKey);
    }

    /**
     * Gets an instructor by associated {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return usersDb.getInstructorByGoogleId(courseId, googleId);
    }

    /**
     * Searches instructors in the whole system. Used by admin only.
     *
     * @return List of found instructors in the whole system. Returns an empty list if no results are found.
     */
    public List<Instructor> searchInstructorsInWholeSystem(String queryString) {
        return usersDb.searchInstructorsInWholeSystem(queryString);
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

        usersDb.deleteUser(user);
    }

    /**
     * Deletes an instructor and cascades deletion to
     * associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the instructor does not exist.
     */
    public void deleteInstructorCascade(String courseId, String email) {
        Instructor instructor = getInstructorForEmail(courseId, email);
        if (instructor == null) {
            return;
        }

        feedbackResponsesLogic.deleteFeedbackResponsesForCourseCascade(courseId, email);
        deadlineExtensionsLogic.deleteDeadlineExtensionsForUser(instructor);
        deleteUser(instructor);
    }

    /**
     * Gets the list of instructors with co-owner privileges in a course.
     */
    public List<Instructor> getCoOwnersForCourse(String courseId) {
        List<Instructor> instructors = getInstructorsForCourse(courseId);
        List<Instructor> instructorsWithCoOwnerPrivileges = new ArrayList<>();
        for (Instructor instructor : instructors) {
            if (!instructor.hasCoownerPrivileges()) {
                continue;
            }
            instructorsWithCoOwnerPrivileges.add(instructor);
        }
        return instructorsWithCoOwnerPrivileges;
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
     * Check if the instructors with the provided emails exist in the course.
     */
    public boolean verifyInstructorsExistInCourse(String courseId, List<String> emails) {
        List<Instructor> instructors = usersDb.getInstructorsForEmails(courseId, emails);
        Map<String, User> emailInstructorMap = convertUserListToEmailUserMap(instructors);

        for (String email : emails) {
            if (!emailInstructorMap.containsKey(email)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets all instructors associated with a googleId.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        assert googleId != null;
        return usersDb.getInstructorsForGoogleId(googleId);
    }

    /**
     * Gets a non-soft-deleted instructor with the specified email and institute.
     */
    public Instructor getInstructorForEmailAndInstitute(String email, String institute) {
        assert email != null;
        assert institute != null;

        return usersDb.getInstructorByEmailAndInstitute(email, institute);
    }

    /**
     * Make the instructor join the course, i.e. associate an account to the instructor with the given googleId.
     * Creates an account for the instructor if no existing account is found.
     * Preconditions:
     * Parameters regkey and googleId are non-null.
     * @throws EntityAlreadyExistsException if the instructor already exists in the database.
     * @throws InvalidParametersException if the instructor parameters are not valid
     */
    public Instructor joinCourseForInstructor(String googleId, Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        if (googleId == null) {
            throw new InvalidParametersException("Instructor's googleId cannot be null");
        }
        if (instructor == null) {
            throw new InvalidParametersException("Instructor cannot be null");
        }

        // setting account for instructor sets it as registered
        if (instructor.getAccount() == null) {
            Account dbAccount = accountsLogic.getAccountForGoogleId(googleId);
            if (dbAccount != null) {
                instructor.setAccount(dbAccount);
            } else {
                Account account = new Account(googleId, instructor.getName(), instructor.getEmail());
                instructor.setAccount(account);
                accountsLogic.createAccount(account);
            }
        } else {
            instructor.setGoogleId(googleId);
        }
        validateUser(instructor);

        // Update the googleId of the student entity for the instructor which was created from sample data.
        Student student = getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            if (student.getAccount() == null) {
                Account account = new Account(googleId, student.getName(), student.getEmail());
                student.setAccount(account);
            } else {
                student.getAccount().setGoogleId(googleId);
            }
            validateUser(student);
        }

        return instructor;
    }

    /**
     * Regenerates the registration key for the instructor with email address {@code email} in course {@code courseId}.
     *
     * @return the instructor with the new registration key.
     * @throws InstructorUpdateException if system was unable to generate a new registration key.
     * @throws EntityDoesNotExistException if the instructor does not exist.
     */
    public Instructor regenerateInstructorRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, InstructorUpdateException {
        Instructor instructor = getInstructorForEmail(courseId, email);
        if (instructor == null) {
            String errorMessage = String.format(
                    "The instructor with the email %s could not be found for the course with ID [%s].", email, courseId);
            throw new EntityDoesNotExistException(errorMessage);
        }

        String oldKey = instructor.getRegKey();
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            instructor.generateNewRegistrationKey();
            if (!instructor.getRegKey().equals(oldKey)) {
                return instructor;
            }
            numTries++;
        }

        throw new InstructorUpdateException("Could not regenerate a new course registration key for the instructor.");
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student with the new registration key.
     * @throws StudentUpdateException if system was unable to generate a new registration key.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public Student regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, StudentUpdateException {
        Student student = getStudentForEmail(courseId, email);
        if (student == null) {
            String errorMessage = String.format(
                    "The student with the email %s could not be found for the course with ID [%s].", email, courseId);
            throw new EntityDoesNotExistException(errorMessage);
        }

        String oldKey = student.getRegKey();
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            student.generateNewRegistrationKey();
            if (!student.getRegKey().equals(oldKey)) {
                return student;
            }
            numTries++;
        }

        throw new StudentUpdateException("Could not regenerate a new course registration key for the student.");
    }

    /**
     * Returns true if the user associated with the googleId is an instructor in any course in the system.
     */
    public boolean isInstructorInAnyCourse(String googleId) {
        return !usersDb.getAllInstructorsByGoogleId(googleId).isEmpty();
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
     * Gets the student with the specified email.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        return usersDb.getStudentForEmail(courseId, userEmail);
    }

    /**
     * Check if the students with the provided emails exist in the course.
     */
    public boolean verifyStudentsExistInCourse(String courseId, List<String> emails) {
        List<Student> students = usersDb.getStudentsForEmails(courseId, emails);
        Map<String, User> emailStudentMap = convertUserListToEmailUserMap(students);

        for (String email : emails) {
            if (!emailStudentMap.containsKey(email)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a list of students with the specified email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        return usersDb.getAllStudentsForEmail(email);
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<Student> getAllStudentsByGoogleId(String googleId) {
        return usersDb.getAllStudentsByGoogleId(googleId);
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
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<Student> searchStudents(String queryString, List<Instructor> instructors) {
        return usersDb.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return an empty list if no result is found
     */
    public List<Student> searchStudentsInWholeSystem(String queryString) {
        return usersDb.searchStudentsInWholeSystem(queryString);
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
     * Gets a student by associated {@code regkey}.
     */
    public Student getStudentByRegistrationKey(String regKey) {
        assert regKey != null;

        return usersDb.getStudentByRegKey(regKey);
    }

    /**
     * Gets a student by associated {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return usersDb.getStudentByGoogleId(courseId, googleId);
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<Student> getStudentsByGoogleId(String googleId) {
        assert googleId != null;

        return usersDb.getStudentsByGoogleId(googleId);
    }

    /**
     * Returns true if the user associated with the googleId is a student in any
     * course in the system.
     */
    public boolean isStudentInAnyCourse(String googleId) {
        return !usersDb.getAllStudentsByGoogleId(googleId).isEmpty();
    }

    /**
     * Gets all instructors and students by {@code googleId}.
     */
    public List<User> getAllUsersByGoogleId(String googleId) {
        assert googleId != null;

        return usersDb.getAllUsersByGoogleId(googleId);
    }

    /**
     * Gets the section with the name in a particular course.
     */
    public Section getSection(String courseId, String sectionName) {
        return usersDb.getSection(courseId, sectionName);
    }

    /**
     * Gets the section with the name in a particular course, otherwise creates a new section.
     */
    public Section getSectionOrCreate(String courseId, String sectionName) {
        // TODO: temporarily added so actions that use this do not break.
        // This should not be used and will be removed once all actions are refactored to not use this method.
        // Sections should not be created implicitly unless creating/updating a student.
        // Currently this is used in actions such as SubmitFeedbackResponsesAction,
        //  which should not be creating sections at all.
        Course course = coursesLogic.getCourse(courseId);
        if (course == null) {
            return null;
        }
        Section section = getSection(courseId, sectionName);
        if (section == null) {
            try {
                section = coursesLogic.createSection(course, sectionName);
            } catch (EntityAlreadyExistsException e) {
                assert false : "Section with name " + sectionName + " should not exist for course " + courseId;
            } catch (InvalidParametersException e) {
                assert false : "Section name " + sectionName + " should be valid.";
            }
        }

        return section;
    }

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                         This may be modified within the method.
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, Instructor instructorToEdit) {
        List<Instructor> instructors = getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        Instructor instrWithModifyInstructorPrivilege = null;
        for (Instructor instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.getGoogleId()
                .equals(instructorToEdit.getGoogleId()));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        }
    }

    /**
     * Deletes a student along with its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        Student student = getStudentForEmail(courseId, studentEmail);

        if (student == null) {
            return;
        }

        feedbackResponsesLogic
                .deleteFeedbackResponsesForCourseCascade(courseId, studentEmail);

        if (usersDb.getStudentCountForTeam(student.getTeamName(), student.getCourseId()) == 1) {
            // the student is the only student in the team, delete responses related to the team
            feedbackResponsesLogic
                    .deleteFeedbackResponsesForCourseCascade(
                            student.getCourseId(), student.getTeamName());
        }

        deadlineExtensionsLogic.deleteDeadlineExtensionsForUser(student);
        deleteUser(student);
        feedbackResponsesLogic.updateRankRecipientQuestionResponsesAfterDeletingStudent(courseId);
    }

    /**
     * Deletes students in the course cascade their associated responses, deadline extensions, and comments.
     */
    public void deleteStudentsInCourseCascade(String courseId) {
        List<Student> studentsInCourse = getStudentsForCourse(courseId);

        for (Student student : studentsInCourse) {
            RequestTracer.checkRemainingTime();
            deleteStudentCascade(courseId, student.getEmail());
        }
    }

    private boolean isEmailChanged(String originalEmail, String newEmail) {
        return newEmail != null && !originalEmail.equals(newEmail);
    }

    private boolean isSectionChanged(Section originalSection, Section newSection) {
        return newSection != null && originalSection != null
                && !originalSection.equals(newSection);
    }

    /**
     * Updates a student by attributes to update. If an attribute is null, it will not be updated.
     */
    public Student updateStudentCascade(Student student, String newEmail, String newName, Team newTeam, String newComments)
            throws InvalidParametersException {
        String courseId = student.getCourseId();

        if (newName != null) {
            student.setName(newName);
        }

        if (newEmail != null && !student.getEmail().equals(newEmail)) {
            feedbackResponsesLogic
                    .updateFeedbackResponsesForChangingEmail(courseId, student.getEmail(), newEmail);
            feedbackResponseCommentsLogic
                    .updateFeedbackResponseCommentsEmails(courseId, student.getEmail(), newEmail);
            student.setEmail(newEmail);
        }

        if (newTeam != null && !student.getTeam().equals(newTeam)) {
            feedbackResponsesLogic
                    .updateFeedbackResponsesForChangingTeam(student.getCourse(), student.getEmail(), student.getTeam());
            if (isSectionChanged(student.getSection(), newTeam.getSection())) {
                feedbackResponsesLogic.updateFeedbackResponsesForChangingSection(
                        student.getCourse(), student.getEmail(), newTeam.getSection());
            }
            student.setTeam(newTeam);
        }

        if (newComments != null) {
            student.setComments(newComments);
        }

        validateUser(student);

        return student;
    }

    /**
     * Resets the googleId associated with the instructor.
     */
    public void resetInstructorGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        assert email != null;
        assert courseId != null;
        assert googleId != null;

        Instructor instructor = getInstructorForEmail(courseId, email);

        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT
                    + "Instructor [courseId=" + courseId + ", email=" + email + "]");
        }

        instructor.setAccount(null);

        if (usersDb.getAllUsersByGoogleId(googleId).isEmpty()) {
            accountsLogic.deleteAccount(googleId);
        }
    }

    /**
     * Updates a student by student id and update request, and cascades to responses and comments if needed.
     */
    public Student updateStudent(UUID studentId, StudentUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException, EnrollException {

        Student student = getStudent(studentId);
        if (student == null) {
            throw new EntityDoesNotExistException(String.format("Student with id %s not found", studentId));
        }

        boolean isEmailChanged = isEmailChanged(student.getEmail(), updateRequest.getEmail());
        if (isEmailChanged) {
            Instructor instructor = getInstructorForEmail(student.getCourseId(), updateRequest.getEmail());
            if (instructor != null) {
                String errorMessage = String.format(
                        "Cannot update student email to %s as this email is already used by an instructor in course %s",
                        updateRequest.getEmail(), student.getCourseId());
                throw new EntityAlreadyExistsException(errorMessage);
            }

            Student existingStudent = getStudentForEmail(student.getCourseId(), updateRequest.getEmail());
            if (existingStudent != null) {
                String errorMessage = String.format(
                        "Cannot update student email to %s as this email is already used by another student in course %s",
                        updateRequest.getEmail(), student.getCourseId());
                throw new EntityAlreadyExistsException(errorMessage);
            }
        }

        // section name -> section
        Map<String, Section> sections = student.getCourse().getSections()
                .stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));

        // section -> team name -> team
        Map<String, Map<String, Team>> teams = coursesLogic.getTeamsForCourse(student.getCourseId())
                .stream()
                .collect(Collectors.groupingBy(team -> team.getSection().getName(),
                        Collectors.toMap(Team::getName, Function.identity())));

        Section section = sections.get(updateRequest.getSection());
        if (section == null) {
            section = coursesLogic.createSection(student.getCourse(), updateRequest.getSection());
            sections.put(section.getName(), section);
        }

        Team team = teams.getOrDefault(section.getName(), Collections.emptyMap()).get(updateRequest.getTeam());
        if (team == null) {
            team = coursesLogic.createTeam(section, updateRequest.getTeam());
            teams.computeIfAbsent(section.getName(), k -> new HashMap<>()).put(team.getName(), team);
        }

        updateStudentCascade(student, updateRequest.getEmail(), updateRequest.getName(), team, updateRequest.getComments());

        // Validate section limit and team name violations.
        // Precondition: this is executed within a transaction; throwing an exception
        // here will roll back all changes.
        List<Student> studentsInCourse = getStudentsForCourse(student.getCourseId());
        String errorMessage = getSectionInvalidityInfo(studentsInCourse)
                + getTeamInvalidityInfo(studentsInCourse);
        if (!errorMessage.isEmpty()) {
            throw new EnrollException(errorMessage);
        }

        return student;
    }

    /**
     * Enrolls students in a course according to the enroll requests, creating the section and team if needed.
     */
    public EnrollResults enrollStudents(Course course,
            List<StudentEnrollRequest> enrollRequests) throws EnrollException {
        Set<String> instructorEmails = getInstructorsForCourse(course.getId())
                .stream()
                .map(Instructor::getEmail)
                .collect(Collectors.toSet());

        // student email -> student
        Map<String, Student> studentsInCourse = getStudentsForCourse(course.getId())
                .stream()
                .collect(Collectors.toMap(Student::getEmail, Function.identity()));

        // section name -> section
        Map<String, Section> sections = course.getSections()
                .stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));

        // section -> team name -> team
        Map<String, Map<String, Team>> teams = coursesLogic.getTeamsForCourse(course.getId())
                .stream()
                .collect(Collectors.groupingBy(team -> team.getSection().getName(),
                        Collectors.toMap(Team::getName, Function.identity())));

        EnrollResults enrollResults = new EnrollResults();

        // Process individual enroll requests
        for (StudentEnrollRequest enrollRequest : enrollRequests) {
            String email = enrollRequest.getEmail();
            if (instructorEmails.contains(email)) {
                String errorMsg = String.format(
                        "Cannot enroll student with email %s as this email is already used by an instructor in course %s",
                        email, course.getId());
                enrollResults.addUnsuccessfulEnroll(email, errorMsg);
                continue;
            }

            try {
                Student student = processEnrollRequest(course, studentsInCourse, sections, teams, enrollRequest);
                enrollResults.addEnrolledStudent(student);
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                enrollResults.addUnsuccessfulEnroll(email, e.getMessage());
            }
        }

        // Validate section limit and team name violations.
        // Precondition: this is executed within a transaction; throwing an exception here will roll back all changes.
        String errorMessage = getSectionInvalidityInfo(studentsInCourse.values())
                + getTeamInvalidityInfo(studentsInCourse.values());
        if (!errorMessage.isEmpty()) {
            throw new EnrollException(errorMessage);
        }

        return enrollResults;
    }

    /**
     * Process an individual enroll request, creating the section, team and student if needed.
     */
    private Student processEnrollRequest(
            Course course,
            Map<String, Student> studentsInCourse,
            Map<String, Section> sections,
            Map<String, Map<String, Team>> teams,
            StudentEnrollRequest enrollRequest) throws InvalidParametersException, EntityAlreadyExistsException {
        String email = enrollRequest.getEmail();

        Section section = sections.get(enrollRequest.getSection());
        if (section == null) {
            section = coursesLogic.createSection(course, enrollRequest.getSection());
            sections.put(section.getName(), section);
        }

        Team team = teams.getOrDefault(section.getName(), Collections.emptyMap()).get(enrollRequest.getTeam());
        if (team == null) {
            team = coursesLogic.createTeam(section, enrollRequest.getTeam());
            teams.computeIfAbsent(section.getName(), k -> new HashMap<>()).put(team.getName(), team);
        }

        Student student = studentsInCourse.get(email);
        if (student != null) {
            updateStudentCascade(student, null, enrollRequest.getName(), team, enrollRequest.getComments());
        } else {
            student = createStudent(course, team, enrollRequest.getName(), email, enrollRequest.getComments());
            studentsInCourse.put(email, student);
        }

        return student;
    }

    private String getSectionInvalidityInfo(Collection<Student> studentList) {
        Map<String, Integer> sectionCountMap = new HashMap<>();
        for (Student student : studentList) {
            String sectionName = student.getSectionName();
            assert sectionName != null : "Section name should not be null";
            sectionCountMap.put(sectionName, sectionCountMap.getOrDefault(sectionName, 0) + 1);
        }

        StringJoiner errorMessage = new StringJoiner(" ");
        sectionCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (entry.getValue() > Const.SECTION_SIZE_LIMIT) {
                        errorMessage.add(String.format(
                                ERROR_ENROLL_EXCEED_SECTION_LIMIT,
                                Const.SECTION_SIZE_LIMIT, entry.getKey()));
                    }
                });

        if (errorMessage.length() > 0) {
            errorMessage.add(String.format(
                    ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION,
                    Const.SECTION_SIZE_LIMIT));
        }

        return errorMessage.toString();
    }

    private String getTeamInvalidityInfo(Collection<Student> studentList) {
        StringJoiner errorMessage = new StringJoiner(" ");
        Map<String, Set<String>> teamToSectionsMap = new HashMap<>();
        for (Student student : studentList) {
            String teamName = student.getTeamName();
            assert teamName != null : "Team name should not be null";
            String sectionName = student.getSectionName();
            assert sectionName != null : "Section name should not be null";
            teamToSectionsMap.computeIfAbsent(teamName, k -> new HashSet<>()).add(sectionName);
        }

        teamToSectionsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (entry.getValue().size() > 1) {
                        List<String> sectionStrings = entry.getValue().stream()
                                .sorted()
                                .map(section -> String.format("\"%s\"", section))
                                .toList();
                        errorMessage.add(String.format(
                                "Team \"%s\" is detected in Sections %s.",
                                entry.getKey(),
                                String.join(", ", sectionStrings)));
                    }
                });

        if (errorMessage.length() > 0) {
            errorMessage.add(ERROR_INVALID_TEAM_NAME_INSTRUCTION);
        }

        return errorMessage.toString();
    }

    /**
     * Resets the googleId associated with the student.
     */
    public void resetStudentGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        assert email != null;
        assert courseId != null;
        assert googleId != null;

        Student student = getStudentForEmail(courseId, email);

        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT
                    + "Student [courseId=" + courseId + ", email=" + email + "]");
        }

        student.setAccount(null);

        if (usersDb.getAllUsersByGoogleId(googleId).isEmpty()) {
            accountsLogic.deleteAccount(googleId);
        }
    }

    /**
     * Sorts the instructors list alphabetically by name.
     */
    public static <T extends User> void sortByName(List<T> users) {
        users.sort(Comparator.comparing(user -> user.getName().toLowerCase()));
    }

    /**
     * Utility function to convert user list to email-user map for faster email lookup.
     *
     * @param users users list which contains users with unique email addresses
     * @return email-user map for faster email lookup
     */
    private Map<String, User> convertUserListToEmailUserMap(List<? extends User> users) {
        Map<String, User> emailUserMap = new HashMap<>();
        users.forEach(u -> emailUserMap.put(u.getEmail(), u));

        return emailUserMap;
    }

    private void validateUser(User user) throws InvalidParametersException {
        if (!user.isValid()) {
            throw new InvalidParametersException(user.getInvalidityInfo());
        }
    }
}
