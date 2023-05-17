package teammates.sqllogic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
import teammates.ui.request.InstructorCreateRequest;

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

    private AccountsLogic accountsLogic;

    private FeedbackResponsesLogic feedbackResponsesLogic;

    private FeedbackResponseCommentsLogic feedbackResponseCommentsLogic;

    private DeadlineExtensionsLogic deadlineExtensionsLogic;

    private UsersLogic() {
        // prevent initialization
    }

    public static UsersLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersDb usersDb, AccountsLogic accountsLogic, FeedbackResponsesLogic feedbackResponsesLogic,
        FeedbackResponseCommentsLogic feedbackResponseCommentsLogic, DeadlineExtensionsLogic deadlineExtensionsLogic) {
        this.usersDb = usersDb;
        this.accountsLogic = accountsLogic;
        this.feedbackResponsesLogic = feedbackResponsesLogic;
        this.feedbackResponseCommentsLogic = feedbackResponseCommentsLogic;
        this.deadlineExtensionsLogic = deadlineExtensionsLogic;
    }

    /**
     * Create an instructor.
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
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

        verifyAtLeastOneInstructorIsDisplayed(courseId, instructor.isDisplayedToStudents(), instructorRequest.getIsDisplayedToStudent());

        String originalEmail = instructor.getEmail();

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
     * Creates a student.
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public Student createStudent(Student student) throws InvalidParametersException, EntityAlreadyExistsException {
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
     * Deletes an instructor or student.
     */
    public <T extends User> void deleteUser(T user) {
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
     * Gets all students of a section.
     */
    public List<Student> getStudentsForSection(String sectionName, String courseId) {
        return usersDb.getStudentsForSection(sectionName, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<Student> getStudentsForTeam(String teamName, String courseId) {
        return usersDb.getStudentsForTeam(teamName, courseId);
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
     * Returns true if the user associated with the googleId is a student in any course in the system.
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
                        student.getCourse().getId(), student.getTeamName());
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
            accountsLogic.deleteAccountCascade(googleId);
        }
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
            accountsLogic.deleteAccountCascade(googleId);
        }
    }

    /**
     * Sorts the instructors list alphabetically by name.
     */
    public static <T extends User> void sortByName(List<T> users) {
        users.sort(Comparator.comparing(user -> user.getName().toLowerCase()));
    }

    /**
     * Checks if an instructor with {@code googleId} can create a course with {@code institute}
     * (ie. has an existing course(s) with the same {@code institute}).
     */
    public boolean canInstructorCreateCourse(String googleId, String institute) {
        assert googleId != null;
        assert institute != null;

        List<Instructor> existingInstructors = getInstructorsForGoogleId(googleId);
        return existingInstructors
                .stream()
                .filter(Instructor::hasCoownerPrivileges)
                .map(instructor -> instructor.getCourse())
                .anyMatch(course -> institute.equals(course.getInstitute()));
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
}
