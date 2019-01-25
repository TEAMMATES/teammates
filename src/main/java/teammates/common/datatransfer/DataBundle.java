package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.storage.entity.BaseEntity;

/**
 * Holds a bundle of *Attributes data transfer objects.
 * This class is mainly used for serializing JSON strings.
 */
public class DataBundle {
    public Map<String, AccountAttributes> accounts = new LinkedHashMap<>();
    public Map<String, CourseAttributes> courses = new LinkedHashMap<>();
    public Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();
    public Map<String, StudentAttributes> students = new LinkedHashMap<>();
    public Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();
    public Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
    public Map<String, FeedbackResponseAttributes> feedbackResponses = new LinkedHashMap<>();
    public Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new LinkedHashMap<>();
    public Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();

    /**
     * Sanitize each attribute in the dataBundle for saving.
     */
    public void sanitizeForSaving() {
        sanitizeMapForSaving(accounts);
        sanitizeMapForSaving(courses);
        sanitizeMapForSaving(instructors);
        sanitizeMapForSaving(students);
        sanitizeMapForSaving(feedbackSessions);
        sanitizeMapForSaving(feedbackQuestions);
        sanitizeMapForSaving(feedbackResponses);
        sanitizeMapForSaving(feedbackResponseComments);
        sanitizeMapForSaving(profiles);
    }

    public List<InstructorAttributes> getInstructorsByGoogleId(String googleId) {
        return this.instructors.values()
                .stream()
                .filter(instructor -> instructor.googleId.equals(googleId))
                .collect(Collectors.toList());
    }

    public CourseAttributes getCourseById(String courseId) {
        return this.courses.values()
                .stream()
                .filter(course -> course.getId().equals(courseId))
                .findFirst()
                .orElseGet(null);
    }

    public List<CourseAttributes> getCoursesForInstructor(String googleId) {
        List<CourseAttributes> courses = new ArrayList<>();

        List<InstructorAttributes> instructorsWithGoogleId = getInstructorsByGoogleId(googleId);
        instructorsWithGoogleId.forEach(instructor -> {
            String courseId = instructor.getCourseId();
            CourseAttributes course = getCourseById(courseId);
            if (course != null) {
                courses.add(course);
            }
        });

        return courses;
    }

    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        return this.students.values()
                .stream()
                .filter(student -> student.course.equals(courseId))
                .collect(Collectors.toList());
    }

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

    public List<StudentAttributes> getStudentsForTeam(String courseId, String sectionName, String teamName) {
        return this.students.values()
                .stream()
                .filter(student -> student.course.equals(courseId))
                .filter(student -> student.section.equals(sectionName))
                .filter(student -> student.team.equals(teamName))
                .collect(Collectors.toList());
    }

    /**
     * Sanitize each attribute in the {@code map} for saving.
     */
    private <T extends EntityAttributes<? extends BaseEntity>> void sanitizeMapForSaving(Map<String, T> map) {
        for (T attribute : map.values()) {
            attribute.sanitizeForSaving();
        }
    }
}
