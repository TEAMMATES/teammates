package teammates.test.cases.webapi;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetInstructorPrivilegeAction;
import teammates.ui.webapi.output.InstructorPrivilegeData;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionTest extends BaseActionTest<GetInstructorPrivilegeAction> {

    private DataBundle dataBundle = getTypicalDataBundle();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String section1 = dataBundle.students.get("student1InCourse1").getSection();
        String session1 = dataBundle.feedbackSessions.get("session1InCourse1").getFeedbackSessionName();
        InstructorPrivileges privileges = instructor1ofCourse1.privileges;
        // update section privilege for testing purpose.

        // section level privilege
        privileges.updatePrivilege(section1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);

        // session level privilege
        privileges.updatePrivilege(section1, session1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        instructor1ofCourse1.privileges = privileges;

        dataBundle.instructors.put("instructor1OfCourse1", instructor1ofCourse1);
        removeAndRestoreDataBundle(dataBundle);
    }

    private void verifyIsSamePrivilegeSet(InstructorPrivilegesBundle bundle, InstructorPrivileges privileges) {
        assertTrue(bundle.getCourseLevelPrivileges().equals(privileges.getCourseLevelPrivileges()));
        assertTrue(bundle.getSectionLevelPrivileges().equals(privileges.getSectionLevelPrivileges()));
        assertTrue(bundle.getSessionLevelPrivileges().equals(privileges.getSessionLevelPrivileges()));
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1ofCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        InstructorPrivileges coOwnerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPrivileges managerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorPrivileges observerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        InstructorPrivileges tutorPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        InstructorPrivileges customPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);

        ______TS("Request to retrieve complete instructor privileges for the instructor");

        String[] courseIdParam = {
                Const.ParamsNames.INSTRUCTOR_PRIVILEGES_IS_ALL_NEEDED, "true",
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        GetInstructorPrivilegeAction a = getAction(courseIdParam);
        InstructorPrivilegeData output = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        InstructorPrivilegesBundle privileges = output.getPrivileges();

        Map<String, Boolean> courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        Map<String, Map<String, Boolean>> sectionLevelPrivileges = privileges.getSectionLevelPrivileges();
        Map<String, Map<String, Map<String, Boolean>>> sessionLevelPrivileges = privileges.getSessionLevelPrivileges();

        assertEquals(instructor1ofCourse1.privileges.getCourseLevelPrivileges().size(), courseLevelPrivileges.size());
        instructor1ofCourse1.privileges.getCourseLevelPrivileges()
                .forEach((k, v) -> assertEquals(v, courseLevelPrivileges.get(k)));

        assertEquals(instructor1ofCourse1.privileges.getSectionLevelPrivileges().size(), sectionLevelPrivileges.size());
        instructor1ofCourse1.privileges.getSectionLevelPrivileges()
                .forEach((section, sectionP) -> {
                    assertEquals(sectionP.size(), sectionLevelPrivileges.get(section).size());
                    sectionP.forEach((k, v) -> assertEquals(v, sectionLevelPrivileges.get(section).get(k))); });

        assertEquals(instructor1ofCourse1.privileges.getSessionLevelPrivileges().size(), sessionLevelPrivileges.size());
        instructor1ofCourse1.privileges.getSessionLevelPrivileges()
                .forEach((session, sessionP) -> {
                    assertEquals(sessionP.size(), sessionLevelPrivileges.get(session).size());
                    sessionP.forEach((section, sectionP) -> {
                        assertEquals(sectionP.size(), sessionLevelPrivileges.get(session).get(section).size());
                        sectionP.forEach((k, v) ->
                                assertEquals(v, sessionLevelPrivileges.get(session).get(section).get(k)));
                    });
                });

        assertNull(output.getInstructorPrivilegesMap());

        ______TS("Request to retrieve instructor roles privileges map.");

        String[] mapParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_IS_PRIVILEGE_MAP_NEEDED, "true",
        };

        a = getAction(mapParams);
        output = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        privileges = output.getPrivileges();
        assertEquals(0, privileges.getSessionLevelPrivileges().size());
        assertEquals(0, privileges.getSectionLevelPrivileges().size());
        assertEquals(0, privileges.getCourseLevelPrivileges().size());

        Map<String, InstructorPrivilegesBundle> privilegesMap = output.getInstructorPrivilegesMap();
        verifyIsSamePrivilegeSet(privilegesMap.get("coowner"), coOwnerPrivileges);
        verifyIsSamePrivilegeSet(privilegesMap.get("manager"), managerPrivileges);
        verifyIsSamePrivilegeSet(privilegesMap.get("observer"), observerPrivileges);
        verifyIsSamePrivilegeSet(privilegesMap.get("tutor"), tutorPrivileges);
        verifyIsSamePrivilegeSet(privilegesMap.get("custom"), customPrivileges);

        ______TS("Request with course id and section name, selected privileges of "
                + "the instructor for the section will be returned.");

        String[] sectionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
        };

        a = getAction(sectionParams);
        output = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        privileges = output.getPrivileges();
        assertTrue(privileges.isAllowedInSectionLevel(student1InCourse1.getSection(),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));

        assertEquals(1, privileges.getCourseLevelPrivileges().size());
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertEquals(1, privileges.getSectionLevelPrivileges().get(student1InCourse1.getSection()).size());
        assertEquals(0, privileges.getSessionLevelPrivileges().size());

        ______TS("Request with course id, selected privileges will be returned");

        String[] invalidSectionNameParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1ofCourse1.getFeedbackSessionName(),
        };

        a = getAction(invalidSectionNameParams);
        output = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        privileges = output.getPrivileges();

        assertEquals(4, privileges.getCourseLevelPrivileges().size());
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertEquals(1, privileges.getSectionLevelPrivileges()
                .get(session1ofCourse1.getFeedbackSessionName()).size());
        assertEquals(0, privileges.getSessionLevelPrivileges().size());

        assertTrue(privileges.isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(privileges.isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(privileges.isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(privileges.isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(privileges.isAllowedInSectionLevel(session1ofCourse1.getFeedbackSessionName(),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);

    }

}
