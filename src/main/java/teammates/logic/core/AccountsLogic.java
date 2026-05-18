package teammates.logic.core;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private AccountsDb accountsDb;

    private UsersLogic usersLogic;

    private CoursesLogic coursesLogic;

    private AccountRequestsLogic accountRequestsLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb,
            UsersLogic usersLogic, CoursesLogic coursesLogic, AccountRequestsLogic accountRequestsLogic) {
        this.accountsDb = accountsDb;
        this.usersLogic = usersLogic;
        this.coursesLogic = coursesLogic;
        this.accountRequestsLogic = accountRequestsLogic;
    }

    public static AccountsLogic inst() {
        return instance;
    }

    /**
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        assert id != null;
        return accountsDb.getAccount(id);
    }

    /**
     * Gets an account by googleId.
     */
    public Account getAccountForGoogleId(String googleId) {
        assert googleId != null;

        return accountsDb.getAccountByGoogleId(googleId);
    }

    /**
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmail(String email) {
        assert email != null;

        return accountsDb.getAccountsByEmail(email);
    }

    /**
     * Creates and returns an account for the given email if it does not exist,
     * otherwise just return the existing account.
     *
     * @param email the email of the account
     * @return the created or existing account
     */
    public Account createOrGetAccountForEmail(String email) {
        assert email != null;

        Account account = getAccountForGoogleId(email);
        if (account != null) {
            return account;
        }

        try {
            return createAccountForEmail(email);
        } catch (EntityAlreadyExistsException e) {
            // This should not happen.
            throw new IllegalStateException("Failed to create existing account for email: " + email, e);
        } catch (InvalidParametersException e) {
            throw new IllegalStateException("Failed to create account with invalid parameters: " + email, e);
        }
    }

    private Account createAccountForEmail(String email)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert email != null;

        // TODO: Account googleId will be replaced by OIDC subject in the future,
        // for now we can just use email as googleId.
        // Account name will also be removed, use a generic "User" for now.
        Account account = new Account(email, "User", email);
        return createAccount(account);
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        validateAccount(account);

        if (getAccountForGoogleId(account.getGoogleId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, account.toString()));
        }

        return accountsDb.createAccount(account);
    }

    /**
     * Deletes account associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(String googleId) {
        Account account = getAccountForGoogleId(googleId);
        if (account == null) {
            return;
        }

        accountsDb.deleteAccount(account);
    }

    /**
     * Deletes account and all users associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccountCascade(String googleId) {
        Account account = getAccountForGoogleId(googleId);
        if (account == null) {
            return;
        }

        List<User> usersToDelete = usersLogic.getAllUsersByGoogleId(googleId);

        for (User user : usersToDelete) {
            usersLogic.deleteUser(user);
        }

        accountsDb.deleteAccount(account);
    }

    /**
     * Joins the user as a student.
     */
    public Student joinCourseForStudent(String registrationKey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        // TODO: Fetch corresponding student's account from db with accountId, no need to create account here.
        // Account creation should have happened before joining course.
        Student student = validateStudentJoinRequest(registrationKey, googleId);

        Account account = accountsDb.getAccountByGoogleId(googleId);
        // Create an account if it doesn't exist
        if (account == null) {
            account = new Account(googleId, student.getName(), student.getEmail());
            createAccount(account);
        }

        if (student.getAccount() == null) {
            student.setAccount(account);
        }

        return student;
    }

    /**
     * Joins the user as an instructor.
     */
    public Instructor joinCourseForInstructor(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        // TODO: Fetch corresponding instructor's account from db with accountId, no need to create account here.
        // Account creation should have happened before joining course.
        Instructor instructor = validateInstructorJoinRequest(key, googleId);

        Account account = accountsDb.getAccountByGoogleId(googleId);
        if (account == null) {
            try {
                account = new Account(googleId, instructor.getName(), instructor.getEmail());
                createAccount(account);
            } catch (EntityAlreadyExistsException e) {
                assert false : "Account already exists.";
            }
        }

        instructor.setAccount(account);

        // Update the googleId of the student entity for the instructor which was created from sample data.
        // TODO: Sample data joining should use joinCourseForStudent instead, email used here may also be incorrect.
        Student student = usersLogic.getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            student.setAccount(account);
        }

        return instructor;
    }

    private Instructor validateInstructorJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructorForKey = usersLogic.getInstructorByRegistrationKey(registrationKey);

        if (instructorForKey == null) {
            throw new EntityDoesNotExistException("No instructor with given registration key: " + registrationKey);
        }

        Course course = coursesLogic.getCourse(instructorForKey.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException("Course with id " + instructorForKey.getCourseId() + " does not exist");
        }

        if (course.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        if (instructorForKey.isRegistered()) {
            if (instructorForKey.getGoogleId().equals(googleId)) {
                Account existingAccount = accountsDb.getAccountByGoogleId(googleId);
                if (existingAccount != null) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            // Check if this Google ID has already joined this course
            Instructor existingInstructor =
                    usersLogic.getInstructorByGoogleId(instructorForKey.getCourseId(), googleId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private Student validateStudentJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        Student studentRole = usersLogic.getStudentByRegistrationKey(registrationKey);

        if (studentRole == null) {
            throw new EntityDoesNotExistException("No student with given registration key: " + registrationKey);
        }

        Course course = coursesLogic.getCourse(studentRole.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException("Course with id " + studentRole.getCourseId() + " does not exist");
        }

        if (course.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        if (studentRole.isRegistered()) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        // Check if this Google ID has already joined this course
        Student existingStudent =
                usersLogic.getStudentByGoogleId(studentRole.getCourseId(), googleId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }

    private void validateAccount(Account account) throws InvalidParametersException {
        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }
    }

    /**
     * Creates a new instructor account, imports demo course data, and marks the account request as registered.
     *
     * @param googleId the Google ID of the instructor who is registering.
     * @param accountRequestId the UUID of the account request being processed.
     * @param timezone the timezone string to use when generating demo course sessions.
     * @throws InvalidParametersException if the demo data import fails due to invalid parameters.
     * @throws EntityDoesNotExistException if the account request or related entities do not exist.
     * @throws EntityAlreadyExistsException if the instructor has already joined the course.
     */
    public void createAccountWithDemoCourse(String googleId, UUID accountRequestId, String timezone)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        teammates.storage.entity.AccountRequest accountRequest =
                accountRequestsLogic.getAccountRequest(accountRequestId);
        assert accountRequest != null;

        String courseId = importDemoData(
                accountRequest.getEmail(), accountRequest.getName(), accountRequest.getInstitute(), timezone);

        List<Instructor> instructorList = usersLogic.getInstructorsForCourse(courseId);
        assert !instructorList.isEmpty();

        joinCourseForInstructor(instructorList.get(0).getRegKey(), googleId);
        accountRequestsLogic.markAccountRequestAsRegistered(accountRequestId);
    }

    /**
     * Imports demo course data for the new instructor.
     *
     * @return the ID of the created demo course
     */
    private String importDemoData(String instructorEmail, String instructorName, String instructorInstitute,
            String timezone) throws InvalidParametersException {
        String courseId = generateDemoCourseId(instructorEmail);
        Instant now = Instant.now();

        String dateString1 = getDateString(now.minus(7, ChronoUnit.DAYS));
        String dateString2 = getDateString(now.minus(3, ChronoUnit.DAYS));
        String dateString3 = getDateString(now.minus(2, ChronoUnit.DAYS));
        String dateString4 = getDateString(now.plus(3, ChronoUnit.DAYS));
        String dateString5 = getDateString(now);

        String instructorEmailAsStudent = instructorEmail.replace("@", "+student@");
        String dataBundleString = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                "teammates.demo.instructor.student@demo.course", instructorEmailAsStudent,
                "teammates.demo.instructor@demo.course", instructorEmail,
                "Demo_Instructor", instructorName,
                "demo.course", courseId,
                "demo.institute", instructorInstitute,
                "demo.timezone", timezone,
                "demo.date1", dateString1,
                "demo.date2", dateString2,
                "demo.date3", dateString3,
                "demo.date4", dateString4,
                "demo.date5", dateString5);

        if (!teammates.common.util.Const.DEFAULT_TIME_ZONE.equals(timezone)) {
            dataBundleString = replaceAdjustedTimeAndTimezone(dataBundleString, timezone);
        }

        DataBundle dataBundle = DataBundleLogic.deserializeDataBundle(dataBundleString);
        DataBundleLogic.inst().persistDataBundle(dataBundle);

        return courseId;
    }

    private String generateDemoCourseId(String instructorEmail) {
        String proposedCourseId = generateNextDemoCourseId(instructorEmail, FieldValidator.COURSE_ID_MAX_LENGTH);
        while (coursesLogic.getCourse(proposedCourseId) != null) {
            proposedCourseId = generateNextDemoCourseId(proposedCourseId, FieldValidator.COURSE_ID_MAX_LENGTH);
        }
        return proposedCourseId;
    }

    private String getDemoCourseIdRoot(String instructorEmail) {
        String[] emailSplit = instructorEmail.split("@");

        String username = emailSplit[0];
        String host = emailSplit[1];

        String head = StringHelper.replaceIllegalChars(username, FieldValidator.REGEX_COURSE_ID, '_');
        String hostAbbreviation = host.substring(0, Math.min(host.length(), 3));

        return head + "." + hostAbbreviation + "-demo";
    }

    /**
     * Generates the next candidate demo course ID from an email or previously generated ID.
     *
     * @param instructorEmailOrProposedCourseId the instructor email or a course ID that already exists.
     * @param maximumIdLength the maximum allowed course ID length.
     * @return a new proposed course ID.
     */
    public String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        boolean isFirstCourseId = instructorEmailOrProposedCourseId.contains("@");
        if (isFirstCourseId) {
            return StringHelper.truncateHead(getDemoCourseIdRoot(instructorEmailOrProposedCourseId), maximumIdLength);
        }

        boolean isFirstTimeDuplicate = instructorEmailOrProposedCourseId.endsWith("-demo");
        if (isFirstTimeDuplicate) {
            return StringHelper.truncateHead(instructorEmailOrProposedCourseId + "0", maximumIdLength);
        }

        int lastIndexOfDemo = instructorEmailOrProposedCourseId.lastIndexOf("-demo");
        String root = instructorEmailOrProposedCourseId.substring(0, lastIndexOfDemo);
        int previousDedupSuffix = Integer.parseInt(instructorEmailOrProposedCourseId.substring(lastIndexOfDemo + 5));

        return StringHelper.truncateHead(root + "-demo" + (previousDedupSuffix + 1), maximumIdLength);
    }

    private String replaceAdjustedTimeAndTimezone(String template, String timezoneString) {
        assert ZoneId.getAvailableZoneIds().contains(timezoneString);

        String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
        ZoneId timezone = ZoneId.of(timezoneString);

        return Pattern.compile(pattern).matcher(template).replaceAll(timestampMatch -> {
            String timestamp = timestampMatch.group();
            Instant instant = Instant.parse(timestamp);

            if (TimeHelper.isSpecialTime(instant)) {
                return timestamp;
            }

            return ZonedDateTime.ofInstant(instant, ZoneId.of(teammates.common.util.Const.DEFAULT_TIME_ZONE))
                    .withZoneSameLocal(timezone).toInstant().toString();
        });
    }

    private static String getDateString(Instant instant) {
        return TimeHelper.formatInstant(instant, teammates.common.util.Const.DEFAULT_TIME_ZONE, "yyyy-MM-dd");
    }
}
