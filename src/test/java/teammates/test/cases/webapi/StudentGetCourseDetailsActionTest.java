package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.StudentGetCourseDetailsAction;
import teammates.ui.webapi.action.StudentGetCourseDetailsAction.InstructorDetails;
import teammates.ui.webapi.action.StudentGetCourseDetailsAction.StudentGetCourseDetailsResult;

/**
 * SUT: {@link StudentGetCourseDetailsAction}.
 */
public class StudentGetCourseDetailsActionTest extends BaseActionTest<StudentGetCourseDetailsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {

        ______TS("using a registered student in a registered course");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String studentId = student1InCourse1.googleId;
        loginAsStudent(studentId);

        // null courseid
        verifyHttpParameterFailure();

        // accessible course
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
        };

        StudentGetCourseDetailsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        StudentGetCourseDetailsResult output = (StudentGetCourseDetailsResult) r.getOutput();

        StudentAttributes expectedStudent = output.getStudent();
        expectedStudent.lastName = null;
        expectedStudent.comments = null;
        expectedStudent.key = null;
        assertEquals(student1InCourse1.toString(), expectedStudent.toString());

        assertEquals(student1InCourse1.course, output.getCourse().getId());

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor3OfCourse1 = typicalBundle.instructors.get("instructor3OfCourse1");
        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        InstructorAttributes instructorNotYetJoinCourse1 = typicalBundle.instructors.get("instructorNotYetJoinCourse1");

        List<InstructorDetails> expectedInstructorDetailsList = new LinkedList<>();
        List<InstructorAttributes> instructors = new ArrayList<>();

        instructors.add(instructor1OfCourse1);
        instructors.add(instructor2OfCourse1);
        instructors.add(instructor3OfCourse1);
        instructors.add(helperOfCourse1);
        instructors.add(instructorNotYetJoinCourse1);

        instructors.forEach(
                instructor -> {
                    if (instructor.isDisplayedToStudents()) {
                        expectedInstructorDetailsList.add(
                                new InstructorDetails(instructor.getDisplayedName(), instructor.getEmail()));
                    }
                });

        AssertHelper.assertSameContentIgnoreOrder(expectedInstructorDetailsList, output.getInstructorDetails());

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student3InCourse1 = typicalBundle.students.get("student3InCourse1");
        StudentAttributes student4InCourse1 = typicalBundle.students.get("student4InCourse1");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");

        List<StudentAttributes> expectedTeammatesList = new LinkedList<>();
        expectedTeammatesList.add(student2InCourse1);
        expectedTeammatesList.add(student3InCourse1);
        expectedTeammatesList.add(student4InCourse1);
        expectedTeammatesList.add(student5InCourse1);

        List<StudentProfileAttributes> expectedTeammateProfiles = new LinkedList<>();
        expectedTeammatesList.forEach(teammate -> {
            StudentProfileAttributes teammateProfile = logic.getStudentProfile(teammate.googleId);
            if (teammateProfile != null) {
                teammateProfile.googleId = null;
                teammateProfile.modifiedDate = null;

                expectedTeammateProfiles.add(teammateProfile);
            }
        });

        AssertHelper.assertSameContentIgnoreOrder(expectedTeammateProfiles, output.getTeammateProfiles());

    }

    @Override
    @Test
    public void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);

        verifyCanMasquerade("student1InCourse1", submissionParams);
    }
}
