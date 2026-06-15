package teammates.ui.webapi;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;
import teammates.logic.core.DataBundleLogic;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.exception.UnexpectedServerException;

/**
 * Creates a new demo course with a demo instructor and student.
 */
public class CreateDemoCourseAction extends LoggedInAction {

    private static final Logger log = Logger.getLogger();

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);
        gateKeeper.verifyCanViewAccountVerificationRequest(requestContext, id);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);
        String timezone = getRequestParamValue(Const.ParamsNames.TIMEZONE);

        if (timezone == null || !FieldValidator.getInvalidityInfoForTimeZone(timezone).isEmpty()) {
            // Use default timezone instead
            timezone = Const.DEFAULT_TIME_ZONE;
        }

        AccountVerificationRequest accountVerificationRequest = logic.getAccountVerificationRequest(id);

        if (accountVerificationRequest == null) {
            throw new EntityNotFoundException("Account verification request with id " + id + " could not be found");
        }

        if (accountVerificationRequest.getCreatedDemoCourseAt() != null) {
            throw new InvalidOperationException(
                    "Account verification request with id " + id + " has already created a demo course.");
        }

        String instructorEmail = accountVerificationRequest.getEmail();
        String instructorName = accountVerificationRequest.getName();
        Institute institute = accountVerificationRequest.getInstitute();
        DataBundle dataBundle;

        try {
            dataBundle = importDemoData(instructorEmail, instructorName, institute, timezone);
        } catch (InvalidParametersException e) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", e);
            throw new UnexpectedServerException(e);
        }

        Instructor createdInstructor = dataBundle.instructors.get("demoInstructor");
        Student createdStudent = dataBundle.students.get("demoInstructorStudent");

        assert createdInstructor != null : "Demo instructor should have been created in data bundle";
        assert createdStudent != null : "Demo instructor student should have been created in data bundle";

        try {
            logic.joinCourse(createdInstructor.getRegKey(), getCurrentAccount());
            logic.joinCourse(createdStudent.getRegKey(), getCurrentAccount());
        } catch (EntityDoesNotExistException | EntityAlreadyExistsException e) {
            // EntityDoesNotExistException should not be thrown as all entities should exist in demo course.
            // EntityAlreadyExistsException should not be thrown as updated entities should not have
            // conflict with generated entities in new demo course.
            // InvalidParametersException should not be thrown as as there should not be any invalid parameters.
            log.severe("Unexpected error", e);
            throw new UnexpectedServerException(e);
        }

        try {
            markDemoCourseCreated(accountVerificationRequest);
        } catch (InvalidParametersException e) {
            // InvalidParametersException should not be thrown as there should not be any invalid parameters.
            log.severe("Unexpected error", e);
            throw new UnexpectedServerException(e);
        }

        return new JsonResult("Demo course successfully created", HttpStatus.SC_OK);
    }

    private AccountVerificationRequest markDemoCourseCreated(AccountVerificationRequest accountVerificationRequest)
            throws InvalidParametersException {
        accountVerificationRequest.setCreatedDemoCourseAt(Instant.now());
        logic.updateAccountVerificationRequest(accountVerificationRequest);
        return accountVerificationRequest;
    }

    private static String getDateString(Instant instant) {
        return TimeHelper.formatInstant(instant, Const.DEFAULT_TIME_ZONE, "yyyy-MM-dd");
    }

    /**
     * Imports demo course for the new instructor.
     *
     * @return the DataBundle with the demo course
     */
    private DataBundle importDemoData(String instructorEmail, String instructorName,
            Institute institute, String timezone) throws InvalidParametersException {

        String courseId = generateDemoCourseId(instructorEmail);
        Instant now = Instant.now();

        // Used for start time + visible time for all sessions
        String dateString1 = getDateString(now.minus(7, ChronoUnit.DAYS));
        // Used for end time for sessions already past
        String dateString2 = getDateString(now.minus(3, ChronoUnit.DAYS));
        // Used for result visible time for sessions already past
        String dateString3 = getDateString(now.minus(2, ChronoUnit.DAYS));
        // Used for end time for session still ongoing
        String dateString4 = getDateString(now.plus(3, ChronoUnit.DAYS));
        // Used for timestamp of comments
        String dateString5 = getDateString(now);

        String instructorEmailAsStudent = getInstructorAsStudentEmail(instructorEmail);
        String dataBundleString = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                // replace instructor-as-student email
                "teammates.demo.instructor.student@demo.course", instructorEmailAsStudent,
                // replace instructor email
                "teammates.demo.instructor@demo.course", instructorEmail,
                // replace name
                "Demo_Instructor", instructorName,
                // replace course
                "demo.course", courseId,
                // replace timezone
                "demo.timezone", timezone,
                // replace dates
                "demo.date1", dateString1,
                "demo.date2", dateString2,
                "demo.date3", dateString3,
                "demo.date4", dateString4,
                "demo.date5", dateString5);

        if (!Const.DEFAULT_TIME_ZONE.equals(timezone)) {
            dataBundleString = replaceAdjustedTimeAndTimezone(dataBundleString, timezone);
        }

        DataBundle dataBundle = DataBundleLogic.deserializeDataBundle(dataBundleString);

        // The demo course is created under the institute associated with the account verification request.
        for (Course course : dataBundle.courses.values()) {
            institute.addCourse(course);
        }

        return logic.persistDataBundle(dataBundle);
    }

    // Strategy to Generate New Demo Course Id:
    // a. keep the part of email before "@"
    //    replace "@" with "."
    //    replace email host with their first 3 chars. eg, gmail.com -> gma
    //    append "-demo"
    //    to sum up: lebron@gmail.com -> lebron.gma-demo
    //
    // b. if the generated courseId already exists, create another one by appending a integer to the previous courseId.
    //    if the newly generate id still exists, increment the id, until we find a feasible one
    //    eg.
    //    lebron@gmail.com -> lebron.gma-demo  // already exists!
    //    lebron@gmail.com -> lebron.gma-demo0 // already exists!
    //    lebron@gmail.com -> lebron.gma-demo1 // already exists!
    //    ...
    //    lebron@gmail.com -> lebron.gma-demo99 // already exists!
    //    lebron@gmail.com -> lebron.gma-demo100 // found! a feasible id
    //
    // c. in any cases(a or b), if generated Id is longer than FieldValidator.COURSE_ID_MAX_LENGTH, shorten the part
    //    before "@" of the initial input email, by continuously removing its last character

    /**
     * Generate a course ID for demo course, and if the generated id already exists, try another one.
     *
     * @param instructorEmail is the instructor email.
     * @return generated course id
     */
    private String generateDemoCourseId(String instructorEmail) {
        String proposedCourseId = generateNextDemoCourseId(instructorEmail, FieldValidator.COURSE_ID_MAX_LENGTH);
        while (logic.getCourse(proposedCourseId) != null) {
            proposedCourseId = generateNextDemoCourseId(proposedCourseId, FieldValidator.COURSE_ID_MAX_LENGTH);
        }
        return proposedCourseId;
    }

    /**
     * Generate a course ID for demo course from a given email.
     *
     * @param instructorEmail is the instructor email.
     * @return the first proposed course id. eg.lebron@gmail.com -> lebron.gma-demo
     */
    private String getDemoCourseIdRoot(String instructorEmail) {
        String[] emailSplit = instructorEmail.split("@");

        String username = emailSplit[0];
        String host = emailSplit[1];

        String head = StringHelper.replaceIllegalChars(username, FieldValidator.REGEX_COURSE_ID, '_');
        String hostAbbreviation = host.substring(0, Math.min(host.length(), 3));

        return head + "." + hostAbbreviation + "-demo";
    }

    /**
     * Generate a course ID for demo course from a given email or a generated course Id.
     *
     * <p>Here we check the input string is an email or course Id and handle them accordingly;
     * check the resulting course id, and if bigger than maximumIdLength, cut it so that it equals maximumIdLength.
     *
     * @param instructorEmailOrProposedCourseId is the instructor email or a proposed course id that already exists.
     * @param maximumIdLength is the maximum resulting id length allowed, above which we will cut the part before "@"
     * @return the proposed course id, e.g.:
     *         <ul>
     *         <li>lebron@gmail.com -> lebron.gma-demo</li>
     *         <li>lebron.gma-demo -> lebron.gma-demo0</li>
     *         <li>lebron.gma-demo0 -> lebron.gma-demo1</li>
     *         <li>012345678901234567890123456789.gma-demo9 -> 01234567890123456789012345678.gma-demo10 (being cut)</li>
     *         </ul>
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

    /**
     * Generate an email for instructor-as-student in demo course, by replacing "@" in instructor email with "+student@".
     *
     * <p>This is to make sure the generated email for student does not conflict with the instructor email.
     */
    private String getInstructorAsStudentEmail(String instructorEmail) {
        return instructorEmail.replace("@", "+student@");
    }

    /**
     * Replace time and timezone based on users timezone.
     * Strings representing instant are adjusted so that they represent the same date and time but in the users timezone.
     * Timezone is changed to users timezone.
     */
    private String replaceAdjustedTimeAndTimezone(String template, String timezoneString) {
        // timezoneString should have been validated in #execute() method already
        assert ZoneId.getAvailableZoneIds().contains(timezoneString);

        String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z"; // regex for instant
        ZoneId timezone = ZoneId.of(timezoneString);

        // replace instant with instant adjusted for user's timezone
        return Pattern.compile(pattern).matcher(template).replaceAll(timestampMatch -> {
            String timestamp = timestampMatch.group();
            Instant instant = Instant.parse(timestamp);

            if (TimeHelper.isSpecialTime(instant)) {
                return timestamp;
            }

            return ZonedDateTime.ofInstant(instant, ZoneId.of(Const.DEFAULT_TIME_ZONE))
                    .withZoneSameLocal(timezone).toInstant().toString();
        });
    }
}
