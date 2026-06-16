package teammates.logic.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;

/**
 * Utility class for generating demo course data.
 */
public final class DemoCourseGenerator {

    private DemoCourseGenerator() {
        // Utility class
    }

    /**
     * Builds the data bundle JSON string for the demo course using the given parameters.
     *
     * @param courseId the course ID for the demo course
     * @param instructorEmail the email of the instructor
     * @param instructorName the name of the instructor
     * @param timezone the timezone string for the demo course
     * @param now the current time used to compute relative dates in the bundle
     * @return the populated JSON string ready for deserialization
     */
    public static String buildDataBundleString(String courseId, String instructorEmail,
            String instructorName, String timezone, Instant now) {
        String dateString1 = getDateString(now.minus(7, ChronoUnit.DAYS));
        String dateString2 = getDateString(now.minus(3, ChronoUnit.DAYS));
        String dateString3 = getDateString(now.minus(2, ChronoUnit.DAYS));
        String dateString4 = getDateString(now.plus(3, ChronoUnit.DAYS));
        String dateString5 = getDateString(now);

        String instructorEmailAsStudent = getInstructorAsStudentEmail(instructorEmail);
        String dataBundleString = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                "teammates.demo.instructor.student@demo.course", instructorEmailAsStudent,
                "teammates.demo.instructor@demo.course", instructorEmail,
                "Demo_Instructor", instructorName,
                "demo.course", courseId,
                "demo.timezone", timezone,
                "demo.date1", dateString1,
                "demo.date2", dateString2,
                "demo.date3", dateString3,
                "demo.date4", dateString4,
                "demo.date5", dateString5);

        if (!Const.DEFAULT_TIME_ZONE.equals(timezone)) {
            dataBundleString = replaceAdjustedTimeAndTimezone(dataBundleString, timezone);
        }

        return dataBundleString;
    }

    /**
     * Generates the nth candidate course ID for a demo course derived from the given instructor email.
     *
     * <p>Attempt 0 produces the base ID (e.g. {@code john.exa-demo}).
     * Attempt 1 produces the base ID with suffix {@code 0} (e.g. {@code john.exa-demo0}).
     * Subsequent attempts increment the suffix ({@code 1}, {@code 2}, …).
     *
     * <p>If the candidate exceeds {@code maximumIdLength}, its head is truncated so that it fits.
     *
     * @param instructorEmail the instructor's email address
     * @param attempt zero-based attempt number
     * @param maximumIdLength the maximum allowed length for the resulting ID
     * @return the candidate course ID for the given attempt
     */
    public static String generateDemoCourseIdCandidate(String instructorEmail, int attempt, int maximumIdLength) {
        String root = getDemoCourseIdRoot(instructorEmail);
        String suffix = attempt == 0 ? "" : String.valueOf(attempt - 1);
        return StringHelper.truncateHead(root + suffix, maximumIdLength);
    }

    /**
     * Generates an email for instructor-as-student by replacing "@" with "+student@".
     */
    public static String getInstructorAsStudentEmail(String instructorEmail) {
        return instructorEmail.replace("@", "+student@");
    }

    static String getDemoCourseIdRoot(String instructorEmail) {
        String[] emailSplit = instructorEmail.split("@");

        String username = emailSplit[0];
        String host = emailSplit[1];

        String head = StringHelper.replaceIllegalChars(username, FieldValidator.REGEX_COURSE_ID, '_');
        String hostAbbreviation = host.substring(0, Math.min(host.length(), 3));

        return head + "." + hostAbbreviation + "-demo";
    }

    static String getDateString(Instant instant) {
        return TimeHelper.formatInstant(instant, Const.DEFAULT_TIME_ZONE, "yyyy-MM-dd");
    }

    static String replaceAdjustedTimeAndTimezone(String template, String timezoneString) {
        assert ZoneId.getAvailableZoneIds().contains(timezoneString);

        String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
        ZoneId timezone = ZoneId.of(timezoneString);

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
