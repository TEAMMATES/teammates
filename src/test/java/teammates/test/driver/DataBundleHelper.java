package teammates.test.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Contains some helper functions for processing {@link DataBundle}.
 */
public class DataBundleHelper {

    private final DataBundle dataBundle;

    public DataBundleHelper(DataBundle dataBundle) {
        this.dataBundle = dataBundle;
    }

    /**
     * Gets all the courses for the given instructor id.
     */
    public List<CourseAttributes> getCoursesByInstructorGoogleId(String instructorGoogleId) {
        return dataBundle.instructors.values().stream()
                .filter(instructorAttributes -> {
                    String googleId = instructorAttributes.getGoogleId();
                    if (googleId != null && googleId.equals(instructorGoogleId)) {
                        return true;
                    }
                    return false;
                })
                .map(instructorAttributes -> getCourseById(instructorAttributes.getCourseId()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the course by the given id.
     */
    public CourseAttributes getCourseById(String courseId) {
        return dataBundle.courses.values()
                .stream()
                .filter(course -> course.getId().equals(courseId))
                .findFirst()
                .orElseGet(null);
    }

    /**
     * Gets all the students for the given course id.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        return dataBundle.students.values()
                .stream()
                .filter(student -> student.course.equals(courseId))
                .collect(Collectors.toList());
    }

    /**
     * Gets all the section names for the given course id.
     */
    public List<String> getSectionNamesForCourse(String courseId) {
        List<String> sections = new ArrayList<>();

        List<StudentAttributes> studentsOfCourse = getStudentsForCourse(courseId);
        studentsOfCourse.forEach(student -> {
            if (student.section != null && !sections.contains(student.section)) {
                sections.add(student.section);
            }
        });

        return sections;
    }

    /**
     * Gets all the team names for the given section name in the given course id.
     */
    public List<String> getTeamsForSection(String courseId, String sectionName) {
        List<String> teams = new ArrayList<>();

        List<StudentAttributes> studentsOfCourse = getStudentsForCourse(courseId);
        studentsOfCourse.forEach(student -> {
            if (student.section != null && student.section.equals(sectionName) && student.team != null
                    && !teams.contains(student.team)) {
                teams.add(student.team);
            }
        });

        return teams;
    }

    /**
     * Gets all the students in the given team name for the given section name in the given course id.
     */
    public List<StudentAttributes> getStudentsForTeam(String courseId, String sectionName, String teamName) {
        return dataBundle.students.values()
                .stream()
                .filter(student -> student.course.equals(courseId))
                .filter(student -> student.section.equals(sectionName))
                .filter(student -> student.team.equals(teamName))
                .collect(Collectors.toList());
    }

}
