package teammates.test.scenariobuilder;

import java.util.UUID;
import java.util.function.Consumer;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;

public final class GivenData {
    private final String testName;
    private final DataBundle dataBundle = new DataBundle();

    public GivenData(String testName) {
        this.testName = testName;
    }

    public String course(String alias) {
        CourseData courseData = new CourseData(stringId(alias));
        Course course = courseData.build();
        dataBundle.courses.put(alias, course);
        return alias;
    }

    public String course(String alias, Consumer<CourseData> options) {
        CourseData courseData = new CourseData(stringId(alias));
        options.accept(courseData);
        Course course = courseData.build();
        dataBundle.courses.put(alias, course);
        return alias;
    }

    public Course getCourse(String alias) {
        Course course = dataBundle.courses.get(alias);
        return HibernateUtil.get(Course.class, course.getId());
    }

    public String getCourseId(String alias) {
        return dataBundle.courses.get(alias).getId();
    }

    public DataBundle getDataBundle() {
        return dataBundle;
    }

    private String stringId(String alias) {
        String prefix = alias.substring(0, Math.min(alias.length(), 27));
        UUID uuid = uuid(alias);
        return prefix + "-" + uuid.toString();
    }

    private UUID uuid(String alias) {
        return UUID.nameUUIDFromBytes((testName + ":" + alias).getBytes());
    }
}
