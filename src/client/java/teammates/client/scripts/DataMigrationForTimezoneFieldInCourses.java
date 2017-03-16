package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.entity.Course;

public class DataMigrationForTimezoneFieldInCourses extends RemoteApiClient {

    private static final Map<String, String> timeZoneDoubleToIdMapping = new HashMap<String, String>();

    static {
        map("-12.0", "Etc/GMT+12");
        map("-11.0", "US/Samoa");
        map("-10.0", "US/Hawaii");
        map("-9.5", "Pacific/Marquesas");
        map("-9.0", "US/Alaska");
        map("-8.0", "America/Los_Angeles");
        map("-7.0", "America/Phoenix");
        map("-6.0", "America/Chicago");
        map("-5.0", "America/New_York");
        map("-4.5", "America/Caracas");
        map("-4.0", "America/Halifax");
        map("-3.5", "America/St_Johns");
        map("-3.0", "America/Sao_Paulo");
        map("-2.0", "America/Noronha");
        map("-1.0", "Atlantic/Cape_Verde");
        map("0.0", "UTC");
        map("1.0", "Europe/Paris");
        map("2.0", "Europe/Athens");
        map("3.0", "Africa/Nairobi");
        map("3.5", "Asia/Tehran");
        map("4.0", "Asia/Dubai");
        map("4.5", "Asia/Kabul");
        map("5.0", "Asia/Tashkent");
        map("5.5", "Asia/Colombo");
        map("5.75", "Asia/Kathmandu");
        map("6.0", "Asia/Almaty");
        map("6.5", "Asia/Rangoon");
        map("7.0", "Asia/Jakarta");
        map("8.0", "Asia/Singapore");
        map("8.75", "Australia/Eucla");
        map("9.0", "Asia/Tokyo");
        map("9.5", "Australia/Adelaide");
        map("10.0", "Australia/Canberra");
        map("10.5", "Australia/Lord_Howe");
        map("11.0", "Pacific/Noumea");
        map("12.0", "Pacific/Auckland");
        map("12.75", "Pacific/Chatham");
        map("13.0", "Pacific/Tongatapu");
        map("14.0", "Pacific/Kiritimati");
    }

    private static final Logic logic = new Logic();

    private boolean isPreview = true;

    private static void map(String doubleTimezone, String timezoneId) {
        timeZoneDoubleToIdMapping.put(doubleTimezone, timezoneId);
    }

    public static void main(String[] args) throws IOException {
        DataMigrationForTimezoneFieldInCourses migrator = new DataMigrationForTimezoneFieldInCourses();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<CourseAttributes> allCourses = getAllCoursesWithoutTimeZone();
        for (CourseAttributes course : allCourses) {
            updateTimezoneForCourse(course);
        }
    }

    private void updateTimezoneForCourse(CourseAttributes course) {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(course.getId());
        String timeZone = sessions.isEmpty() ? Const.DEFAULT_TIMEZONE
                                             : getTimeZoneId(sessions.get(0).getTimeZone());

        if (isPreview) {
            System.out.println("Course " + course.getId() + " timezone to be set to " + timeZone + ".");
            return;
        }

        try {
            course.setTimeZone(timeZone);
            logic.updateCourse(course);
            System.out.println("Course " + course.getId() + " timezone successfully set to " + timeZone + ".");
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            System.out.println("Failed to set timezone for course " + course.getId() + ".");
            e.printStackTrace();
        }
    }

    private String getTimeZoneId(double timeZoneDouble) {
        return timeZoneDoubleToIdMapping.get(Double.toString(timeZoneDouble));
    }

    private List<CourseAttributes> getAllCoursesWithoutTimeZone() {
        List<CourseAttributes> coursesWithoutTimeZone = new ArrayList<CourseAttributes>();
        List<Course> courseEntities = getAllCourseEntities();
        for (Course courseEntity : courseEntities) {
            if (courseEntity.getTimeZone() == null && !JDOHelper.isDeleted(courseEntity)) {
                coursesWithoutTimeZone.add(new CourseAttributes(courseEntity));
            }
        }
        return coursesWithoutTimeZone;
    }

    @SuppressWarnings("unchecked")
    private List<Course> getAllCourseEntities() {
        String query = "select from " + Course.class.getName();
        return (List<Course>) PM.newQuery(query).execute();
    }

}
