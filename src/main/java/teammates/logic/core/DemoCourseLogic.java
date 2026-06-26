package teammates.logic.core;

import java.time.Instant;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.util.DemoCourseGenerator;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * Handles the creation of demo courses for new instructors.
 */
public final class DemoCourseLogic {
    private static final int MAX_DEMO_COURSE_ID_ATTEMPTS = 1000;

    private static final DemoCourseLogic instance = new DemoCourseLogic();

    private AccountVerificationsLogic accountVerificationsLogic;
    private AccountsLogic accountsLogic;
    private CoursesLogic coursesLogic;
    private DataBundleLogic dataBundleLogic;

    private DemoCourseLogic() {
        // prevent initialization
    }

    public static DemoCourseLogic inst() {
        return instance;
    }

    /**
     * Initialises dependencies for {@link DemoCourseLogic}.
     */
    void initLogicDependencies(AccountVerificationsLogic accountVerificationsLogic,
            AccountsLogic accountsLogic, CoursesLogic coursesLogic, DataBundleLogic dataBundleLogic) {
        this.accountVerificationsLogic = accountVerificationsLogic;
        this.accountsLogic = accountsLogic;
        this.coursesLogic = coursesLogic;
        this.dataBundleLogic = dataBundleLogic;
    }

    /**
     * Creates a demo course for the given account verification request.
     *
     * <p>The demo course is populated with sample data and the instructor/student
     * registration keys are joined to the given account.
     *
     * @param id the ID of the account verification request
     * @param timezone the timezone for the demo course; if null or invalid, defaults to UTC
     * @param account the account to join as instructor and student
     * @throws EntityDoesNotExistException if no account verification request with the given ID exists
     * @throws EntityAlreadyExistsException if a demo course has already been created for the request
     * @throws InvalidParametersException if a unique course ID cannot be found within
     *         {@value #MAX_DEMO_COURSE_ID_ATTEMPTS} attempts, or if an unexpected parameter error
     *         occurs during data bundle persistence
     */
    public void createDemoCourse(UUID id, String timezone, Account account)
            throws EntityDoesNotExistException, EntityAlreadyExistsException, InvalidParametersException {
        String sanitizedTimezone = timezone;
        if (timezone == null || !FieldValidator.getInvalidityInfoForTimeZone(timezone).isEmpty()) {
            sanitizedTimezone = Const.DEFAULT_TIME_ZONE;
        }

        AccountVerificationRequest avr = accountVerificationsLogic.getAccountVerificationRequest(id);

        if (avr == null) {
            throw new EntityDoesNotExistException("Account verification request with id " + id + " could not be found");
        }

        if (avr.getCreatedDemoCourseAt() != null) {
            throw new EntityAlreadyExistsException(
                    "Account verification request with id " + id + " has already created a demo course.");
        }

        String instructorEmail = avr.getEmail();
        String instructorName = avr.getName();

        String courseId = generateUniqueCourseId(instructorEmail);
        Instant now = Instant.now();

        String dataBundleString = DemoCourseGenerator.buildDataBundleString(
                courseId, instructorEmail, instructorName, sanitizedTimezone, now);

        var dataBundle = DataBundleLogic.deserializeDataBundle(dataBundleString);

        for (Course course : dataBundle.courses.values()) {
            avr.getInstitute().addCourse(course);
        }

        dataBundle = dataBundleLogic.persistDataBundle(dataBundle);

        Instructor createdInstructor = dataBundle.instructors.get("demoInstructor");
        Student createdStudent = dataBundle.students.get("demoInstructorStudent");

        assert createdInstructor != null : "Demo instructor should have been created in data bundle";
        assert createdStudent != null : "Demo instructor student should have been created in data bundle";

        accountsLogic.joinCourse(createdInstructor.getId(), account);
        accountsLogic.joinCourse(createdStudent.getId(), account);

        avr.setCreatedDemoCourseAt(now);
    }

    private String generateUniqueCourseId(String instructorEmail) throws InvalidParametersException {
        for (int attempt = 0; attempt < MAX_DEMO_COURSE_ID_ATTEMPTS; attempt++) {
            String proposedCourseId = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix(
                    instructorEmail, FieldValidator.COURSE_ID_MAX_LENGTH);
            if (coursesLogic.getCourse(proposedCourseId) == null) {
                return proposedCourseId;
            }
        }
        throw new InvalidParametersException(
                "Could not generate a unique demo course ID for " + instructorEmail
                + " after " + MAX_DEMO_COURSE_ID_ATTEMPTS + " attempts.");
    }
}
