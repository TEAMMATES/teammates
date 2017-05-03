package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.api.Logic;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackResponse;

/**
 * Adds sections to large courses without sections. For use after migrating evaluations
 * to feedback sessions. Handles updating sections in responses, but not comments.
 *
 */
public class AddSectionsToLargeCourses extends RemoteApiClient {

    private static final Logic logic = new Logic();

    // modify this to modify only a specific course or all courses
    private static final boolean isForAllCourses = false;

    // modify this to modify the max size of a course without a section
    // if numStudents in a course > maxCourseSizeWithoutSections,
    // then sections will be added to the course
    private static final int maxCourseSizeWithoutSections = 100;

    // if not modifying all courses, specify which course to modify here
    private static final String courseToAddSectionsTo = "demo-course";

    // when adding teams to a section, when this value is reached or exceeded,
    // change the section for the next team
    private static final int numOfStudentsInSection = 100;

    // modify for preview
    private static final boolean isPreview = true;

    /*
     * IMPORTANT: *******************************
     * This script does not update FeedbackResponseComments because it was created
     * originally to deal with feedback responses migrated from legacy data (those
     * responses did not have comments)
     * ******************************************
     */

    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();

        AddSectionsToLargeCourses migrator = new AddSectionsToLargeCourses();
        migrator.doOperationRemotely();

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }

    @Override
    protected void doOperation() {
        if (isPreview) {
            System.out.println("In Preview Mode");
        }

        Set<String> courses;
        if (isForAllCourses) {
            courses = getCourses();
        } else {
            courses = new HashSet<String>();
            courses.add(courseToAddSectionsTo);
        }

        courses = filterLargeCoursesWithoutSections(courses);

        for (String course : courses) {
            addSectionsToCourse(course);
        }
    }

    private Set<String> getCourses() {
        String q = "SELECT FROM " + Course.class.getName();
        @SuppressWarnings("unchecked")
        List<Course> courses = (List<Course>) PM.newQuery(q).execute();

        Set<String> allCourses = new HashSet<String>();

        for (Course course : courses) {
            allCourses.add(course.getUniqueId());
        }
        return allCourses;
    }

    private Set<String> filterLargeCoursesWithoutSections(Set<String> courses) {
        Set<String> largeCoursesWithoutSections = new HashSet<String>();

        CourseDetailsBundle courseDetails = null;
        for (String courseId : courses) {

            try {
                courseDetails = logic.getCourseDetails(courseId);
            } catch (EntityDoesNotExistException e) {
                System.out.println("Course not found" + courseId);
                e.printStackTrace();
                continue;
            }

            boolean hasSection = courseDetails.stats.sectionsTotal != 0;
            boolean isCourseSizeSmall = courseDetails.stats.studentsTotal <= maxCourseSizeWithoutSections;
            if (!hasSection && !isCourseSizeSmall) {
                largeCoursesWithoutSections.add(courseId);
            }
        }

        return largeCoursesWithoutSections;
    }

    private void addSectionsToCourse(String courseId) {
        System.out.println("Course: [" + courseId + "] ");

        List<TeamDetailsBundle> teams;
        try {
            teams = logic.getTeamsForCourse(courseId);
        } catch (EntityDoesNotExistException e) {
            System.out.println("ERROR Course not found" + courseId);
            e.printStackTrace();
            return;
        }

        int numSections = 1;
        String sectionPrefix = "Section ";
        String currentSection = sectionPrefix + numSections;

        int currentSectionSize = 0;

        for (TeamDetailsBundle team : teams) {
            String teamName = team.name;

            List<StudentAttributes> students = team.students;
            System.out.println("Students in team " + teamName + " : " + students.size());

            for (StudentAttributes student : students) {
                updateStudentSection(currentSection, student);

                currentSectionSize = currentSectionSize + 1;
            }

            if (currentSectionSize >= numOfStudentsInSection) {
                // increment section
                numSections++;
                currentSection = sectionPrefix + numSections;
                currentSectionSize = 0;
            }
        }

        System.out.println();
    }

    private void updateStudentSection(String currentSection, StudentAttributes student) {

        System.out.println("Update " + student.email + " to section " + currentSection);
        if (isPreview) {
            return;
        }

        CourseStudent studentEntity = getStudent(student.email, student.course);
        updateStudentToBeInSection(studentEntity, currentSection);

        List<FeedbackResponse> responsesForStudent = getResponsesForStudent(student);
        updateFeedbackResponsesToBeInSection(responsesForStudent, student, currentSection);

        PM.close();

    }

    private CourseStudent getStudent(String email, String courseId) {

        String q = "SELECT FROM " + CourseStudent.class.getName() + " "
                + "WHERE email == emailParam && courseID == courseIdParam" + " "
                + "PARAMETERS String emailParam, String courseIdParam";
        @SuppressWarnings("unchecked")
        List<CourseStudent> studentList = (List<CourseStudent>) PM.newQuery(q).execute(email, courseId);

        return studentList.get(0);
    }

    private void updateStudentToBeInSection(CourseStudent student, String sectionToChangeTo) {
        if (isPreview) {
            return;
        }

        student.setSectionName(sectionToChangeTo);
    }

    private void updateFeedbackResponsesToBeInSection(List<FeedbackResponse> responses,
                                                      StudentAttributes student, String sectionName) {
        if (isPreview) {
            return;
        }

        String studentEmail = student.email;
        String studentTeam = student.team;

        for (FeedbackResponse response : responses) {
            if (response.getRecipientEmail().equals(studentEmail)
                    || response.getRecipientEmail().equals(studentTeam)) {

                response.setRecipientSection(sectionName);
            }

            if (response.getGiverEmail().equals(studentEmail)) {
                response.setGiverSection(sectionName);
            }
        }

        // note that comments are not updated
    }

    private List<FeedbackResponse> getResponsesForStudent(StudentAttributes student) {
        String studentEmail = student.email;
        String studentTeam = student.team;
        String course = student.course;

        String q = "SELECT FROM " + FeedbackResponse.class.getName() + " "
                + "WHERE giverEmail == emailParam" + " "
                + "&& courseId == courseParam" + " "
                + "PARAMETERS String emailParam, String courseParam";

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> responsesAsGiver = (List<FeedbackResponse>) PM.newQuery(q).execute(studentEmail, course);

        q = "SELECT FROM " + FeedbackResponse.class.getName() + " "
                + "WHERE (receiver == emailParam" + " "
                + "|| receiver == teamParam)" + " "
                + "&& courseId == courseParam" + " "
                + "PARAMETERS String emailParam, String teamParam, String courseParam";

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> responsesAsReceiver =
                (List<FeedbackResponse>) PM.newQuery(q).execute(studentEmail, studentTeam, course);

        List<FeedbackResponse> responses = new ArrayList<FeedbackResponse>();
        responses.addAll(responsesAsGiver);
        responses.addAll(responsesAsReceiver);

        return responses;
    }

}
