package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.StudentCourseDetailsPageData;

/**
 * SUT: {@link StudentCourseDetailsPageData}.
 */
public class StudentCourseDetailsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void test() {
        ______TS("typical success case");

        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        StudentCourseDetailsPageData pageData = new StudentCourseDetailsPageData(account, dummySessionToken);

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");

        CourseDetailsBundle courseDetails = new CourseDetailsBundle(course);

        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(dataBundle.instructors.get("instructor1OfCourse1"));
        instructors.add(dataBundle.instructors.get("instructor2OfCourse1"));
        instructors.add(dataBundle.instructors.get("helperOfCourse1"));

        TeamDetailsBundle team = new TeamDetailsBundle();
        team.name = student.team;

        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }

        pageData.init(courseDetails, instructors, student, team);

        List<InstructorAttributes> courseInstructors = pageData.getStudentCourseDetailsPanel().getInstructors();
        List<StudentAttributes> teammates = pageData.getStudentCourseDetailsPanel().getTeammates();

        assertEquals(courseDetails.course.getName(), pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.getId(), pageData.getStudentCourseDetailsPanel().getCourseId());

        assertFalse(courseInstructors.isEmpty());
        assertFalse(teammates.isEmpty());

        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());

        assertEquals(instructors.size(), courseInstructors.size());
        assertEquals(instructors.get(0).name, courseInstructors.get(0).name);
        assertEquals(instructors.get(1).name, courseInstructors.get(1).name);
        assertEquals(instructors.get(2).name, courseInstructors.get(2).name);
        assertEquals(instructors.get(0).displayedName, courseInstructors.get(0).displayedName);
        assertEquals(instructors.get(1).displayedName, courseInstructors.get(1).displayedName);
        assertEquals(instructors.get(2).displayedName, courseInstructors.get(2).displayedName);

        assertEquals(team.students.size(), teammates.size());

        ______TS("student in unregistered course");

        student = dataBundle.students.get("student1InUnregisteredCourse");
        course = dataBundle.courses.get("unregisteredCourse");

        courseDetails = new CourseDetailsBundle(course);

        instructors = new ArrayList<>();
        instructors.add(dataBundle.instructors.get("instructor5"));

        team = new TeamDetailsBundle();
        team.name = student.team;

        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }

        pageData.init(courseDetails, instructors, student, team);

        courseInstructors = pageData.getStudentCourseDetailsPanel().getInstructors();
        teammates = pageData.getStudentCourseDetailsPanel().getTeammates();

        assertEquals(courseDetails.course.getName(), pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.getId(), pageData.getStudentCourseDetailsPanel().getCourseId());

        assertFalse(courseInstructors.isEmpty());
        assertFalse(teammates.isEmpty());

        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());

        assertEquals(instructors.size(), courseInstructors.size());
        assertEquals(instructors.get(0).name, courseInstructors.get(0).name);
        assertEquals(instructors.get(0).displayedName, courseInstructors.get(0).displayedName);

        assertEquals(team.students.size(), teammates.size());

        ______TS("student in archived course");

        student = dataBundle.students.get("student1InArchivedCourse");
        course = dataBundle.courses.get("archivedCourse");

        courseDetails = new CourseDetailsBundle(course);

        instructors = new ArrayList<>();
        instructors.add(dataBundle.instructors.get("instructorOfArchivedCourse"));

        team = new TeamDetailsBundle();
        team.name = student.team;

        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }

        pageData.init(courseDetails, instructors, student, team);

        courseInstructors = pageData.getStudentCourseDetailsPanel().getInstructors();
        teammates = pageData.getStudentCourseDetailsPanel().getTeammates();

        assertEquals(courseDetails.course.getName(), pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.getId(), pageData.getStudentCourseDetailsPanel().getCourseId());

        assertFalse(courseInstructors.isEmpty());
        assertFalse(teammates.isEmpty());

        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());

        assertEquals(instructors.size(), courseInstructors.size());
        assertEquals(instructors.get(0).name, courseInstructors.get(0).name);
        assertEquals(instructors.get(0).displayedName, courseInstructors.get(0).displayedName);

        assertEquals(team.students.size(), teammates.size());
    }
}
