package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;

/**
 * Handles operations related to accounts.
 *
 * @see AccountAttributes
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final Logger log = Logger.getLogger();

    private static AccountsLogic instance = new AccountsLogic();

    private static final AccountsDb accountsDb = new AccountsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private AccountsLogic() {
        // prevent initialization
    }

    public static AccountsLogic inst() {
        return instance;
    }

    public void createAccount(AccountAttributes accountData)
                    throws InvalidParametersException {

        List<String> invalidityInfo = accountData.getInvalidityInfo();
        if (!invalidityInfo.isEmpty()) {
            throw new InvalidParametersException(invalidityInfo);
        }

        log.info("going to create account :\n" + accountData.toString());

        accountsDb.createAccount(accountData);
    }

    public AccountAttributes getAccount(String googleId) {
        return getAccount(googleId, false);
    }

    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        return accountsDb.getAccount(googleId, retrieveStudentProfile);
    }

    public boolean isAccountPresent(String googleId) {
        return accountsDb.getAccount(googleId) != null;
    }

    public boolean isAccountAnInstructor(String googleId) {
        AccountAttributes a = accountsDb.getAccount(googleId);
        return a != null && a.isInstructor;
    }

    public List<AccountAttributes> getInstructorAccounts() {
        return accountsDb.getInstructorAccounts();
    }

    public String getCourseInstitute(String courseId) {
        CourseAttributes cd = coursesLogic.getCourse(courseId);
        Assumption.assertNotNull("Trying to getCourseInstitute for inexistent course with id " + courseId, cd);
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(cd.getId());

        Assumption.assertTrue("Course has no instructors: " + cd.getId(), !instructorList.isEmpty());
        // Retrieve institute field from one of the instructors of the course
        String institute = "";
        for (int i = 0; i < instructorList.size(); i++) {
            String instructorGoogleId = instructorList.get(i).googleId;
            if (instructorGoogleId == null) {
                continue;
            }
            AccountAttributes instructorAcc = accountsDb.getAccount(instructorGoogleId);
            if (instructorAcc != null) {
                institute = instructorAcc.institute;
                break;
            }
        }
        Assumption.assertNotEmpty("No institute found for the course", institute);
        return institute;
    }

    public void updateAccount(AccountAttributes account)
            throws InvalidParametersException, EntityDoesNotExistException {
        accountsDb.updateAccount(account, false);
    }

    public void updateAccount(AccountAttributes account, boolean updateStudentProfile)
            throws InvalidParametersException, EntityDoesNotExistException {
        accountsDb.updateAccount(account, updateStudentProfile);
    }

    public void joinCourseForStudent(String registrationKey, String googleId)
            throws JoinCourseException, InvalidParametersException {

        verifyStudentJoinCourseRequest(registrationKey, googleId);

        StudentAttributes student = studentsLogic.getStudentForRegistrationKey(registrationKey);

        //register the student
        student.googleId = googleId;
        try {
            studentsLogic.updateStudentCascade(student.email, student);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Student disappered while trying to register " + TeammatesException.toStringWithStackTrace(e));
        }

        if (accountsDb.getAccount(googleId) == null) {
            createStudentAccount(student);
        }
    }

    /**
     * Joins the user as an instructor, and sets the institute too.
     */
    public void joinCourseForInstructor(String encryptedKey, String googleId, String institute)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {

        joinCourseForInstructorWithInstitute(encryptedKey, googleId, institute);

    }

    /**
     * Joins the user as an instructor.
     */
    public void joinCourseForInstructor(String encryptedKey, String googleId)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {

        joinCourseForInstructorWithInstitute(encryptedKey, googleId, null);

    }

    /**
     * Institute is set only if it is not null. If it is null, this instructor
     * is given the institute of an existing instructor of the same course.
     */
    private void joinCourseForInstructorWithInstitute(String encryptedKey, String googleId, String institute)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {

        confirmValidJoinCourseRequest(encryptedKey, googleId);

        InstructorAttributes instructor = instructorsLogic.getInstructorForRegistrationKey(encryptedKey);
        AccountAttributes account = accountsDb.getAccount(googleId);
        String instituteToSave = institute == null ? getCourseInstitute(instructor.courseId) : institute;

        if (account == null) {
            createAccount(AccountAttributes.builder()
                    .withGoogleId(googleId)
                    .withName(instructor.name)
                    .withEmail(instructor.email)
                    .withInstitute(instituteToSave)
                    .withIsInstructor(true)
                    .withDefaultStudentProfileAttributes(googleId)
                    .build());
        } else {
            makeAccountInstructor(googleId);
        }

        instructor.googleId = googleId;
        instructorsLogic.updateInstructorByEmail(instructor.email, instructor);

        //Update the goolgeId of the student entity for the instructor which was created from sampleData.
        StudentAttributes student = studentsLogic.getStudentForEmail(instructor.courseId, instructor.email);
        if (student != null) {
            student.googleId = googleId;
            studentsLogic.updateStudentCascade(instructor.email, student);
        }

    }

    private void confirmValidJoinCourseRequest(String encryptedKey, String googleId)
            throws JoinCourseException {

        //The order in which these confirmations are done is important. Reorder with care.
        confirmValidKey(encryptedKey);

        InstructorAttributes instructorForKey = instructorsLogic.getInstructorForRegistrationKey(encryptedKey);

        confirmNotAlreadyJoinedAsInstructor(instructorForKey, googleId);
        confirmUnusedKey(instructorForKey, googleId);
        confirmNotRejoiningUsingDifferentKey(instructorForKey, googleId);

    }

    private void confirmNotRejoiningUsingDifferentKey(
            InstructorAttributes instructorForKey, String googleId) throws JoinCourseException {

        if (instructorForKey.googleId != null) { //using a used key. this means no danger of rejoining using different key
            return;
        }

        //check if this Google ID has already joined this course
        InstructorAttributes existingInstructor =
                instructorsLogic.getInstructorForGoogleId(instructorForKey.courseId, googleId);

        if (existingInstructor != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                                  googleId));
        }

    }

    private void confirmNotAlreadyJoinedAsInstructor(InstructorAttributes instructorForKey, String googleId)
            throws JoinCourseException {
        if (instructorForKey.googleId == null || !instructorForKey.googleId.equals(googleId)) {
            return;
        }
        AccountAttributes existingAccount = accountsDb.getAccount(googleId);
        if (existingAccount != null && existingAccount.isInstructor) {
            throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED,
                                          googleId + " has already joined this course");
        }

    }

    private void confirmValidKey(String encryptedKey) throws JoinCourseException {
        InstructorAttributes instructorForKey = instructorsLogic.getInstructorForRegistrationKey(encryptedKey);

        if (instructorForKey == null) {
            String joinUrl = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN + "?key=" + encryptedKey;
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                                          "You have used an invalid join link: " + joinUrl);

        }
    }

    private void confirmUnusedKey(InstructorAttributes instructorForKey, String googleId) throws JoinCourseException {
        if (instructorForKey.googleId == null) {
            return;
        }

        //We assume we have already confirmed that the key was not used by this
        //  person already.
        if (!instructorForKey.googleId.equals(googleId)) {
            throw new JoinCourseException(Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                                          String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                                  StringHelper.obscure(instructorForKey.googleId)));
        }
    }

    private void verifyStudentJoinCourseRequest(String encryptedKey, String googleId)
            throws JoinCourseException {

        StudentAttributes studentRole = studentsLogic.getStudentForRegistrationKey(encryptedKey);

        if (studentRole == null) {
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                    "You have used an invalid join link: %s");
        } else if (studentRole.isRegistered()) {
            if (studentRole.googleId.equals(googleId)) {
                throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED,
                        "You (" + googleId + ") have already joined this course");
            }
            throw new JoinCourseException(
                    Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                    String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                  StringHelper.obscure(studentRole.googleId)));
        }

        StudentAttributes existingStudent =
                studentsLogic.getStudentForCourseIdAndGoogleId(studentRole.course, googleId);

        if (existingStudent != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                            googleId));
        }
    }

    public void downgradeInstructorToStudentCascade(String googleId) {
        instructorsLogic.deleteInstructorsForGoogleIdAndCascade(googleId);
        makeAccountNonInstructor(googleId);
    }

    public void makeAccountNonInstructor(String googleId) {
        AccountAttributes account = accountsDb.getAccount(googleId, true);
        if (account == null) {
            log.warning("Accounts logic trying to modify non-existent account a non-instructor :" + googleId);
        } else {
            account.isInstructor = false;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Invalid account data detected unexpectedly "
                                + "while removing instruction privileges from account :" + account.toString());
            }
        }
    }

    public void makeAccountInstructor(String googleId) {

        AccountAttributes account = accountsDb.getAccount(googleId, true);

        if (account == null) {
            log.warning("Accounts logic trying to modify non-existent account an instructor:" + googleId);
        } else {
            account.isInstructor = true;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Invalid account data detected unexpectedly "
                                + "while adding instruction privileges to account :" + account.toString());
            }
        }
    }

    public void deleteAccountCascade(String googleId) {
        instructorsLogic.deleteInstructorsForGoogleIdAndCascade(googleId);
        studentsLogic.deleteStudentsForGoogleIdAndCascade(googleId);
        accountsDb.deleteAccount(googleId);
        //TODO: deal with orphan courses, submissions etc.
    }

    private void createStudentAccount(StudentAttributes student)
            throws InvalidParametersException {

        AccountAttributes account = AccountAttributes.builder()
                .withGoogleId(student.googleId)
                .withEmail(student.email)
                .withName(student.name)
                .withIsInstructor(false)
                .withInstitute(getCourseInstitute(student.course))
                .withStudentProfileAttributes(StudentProfileAttributes.builder(student.googleId)
                        .withInstitute(getCourseInstitute(student.course))
                        .build())
                .build();

        accountsDb.createAccount(account);
    }

}
