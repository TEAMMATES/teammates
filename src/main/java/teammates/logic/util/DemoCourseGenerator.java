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
     * Generates a course ID for a demo course from a given email or a previously generated course ID.
     *
     * <p>If the input contains "@", it is treated as an instructor email and the base course ID is generated.
     * If it is a course ID already ending in "-demo", the suffix "0" is appended.
     * Otherwise, the numeric suffix after "-demo" is incremented.
     *
     * <p>If the generated ID exceeds {@code maximumIdLength}, the head is truncated.
     *
     * @param instructorEmailOrProposedCourseId the instructor email or a proposed course ID
     * @param maximumIdLength the maximum allowed length for the resulting ID
     * @return the next candidate course ID
     */
    public static String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
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
