package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCourseEditPageData;
import teammates.ui.template.CourseEditInstructorPanel;
import teammates.ui.template.CourseEditSectionRow;

/**
 * SUT: {@link InstructorCourseEditPageData}.
 */
public class InstructorCourseEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void testAll() {
        ______TS("test typical case");

        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");

        List<InstructorAttributes> instructorList = new ArrayList<>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));
        instructorList.add(dataBundle.instructors.get("instructor2OfCourse1"));
        instructorList.add(dataBundle.instructors.get("helperOfCourse1"));
        instructorList.add(dataBundle.instructors.get("instructorNotYetJoinCourse1"));

        InstructorAttributes currentInstructor = dataBundle.instructors.get("instructor1OfCourse1");

        int offset = -1;

        List<String> sectionNames = new ArrayList<>();
        sectionNames.add("Section 1");
        sectionNames.add("Section 2");

        List<String> feedbackSessionNames = new ArrayList<>();
        feedbackSessionNames.add("First feedback session");
        feedbackSessionNames.add("Second feedback session");
        feedbackSessionNames.add("Grace Period Session");
        feedbackSessionNames.add("Closed Session");
        feedbackSessionNames.add("Empty session");
        feedbackSessionNames.add("non visible session");

        InstructorCourseEditPageData pageData = new InstructorCourseEditPageData(account, dummySessionToken, course,
                                                                                 instructorList,
                                                                                 currentInstructor,
                                                                                 offset, sectionNames,
                                                                                 feedbackSessionNames);

        assertEquals("idOfTypicalCourse1", pageData.getCourse().getId());
        assertEquals(-1, pageData.getInstructorToShowIndex());
        assertNotNull(pageData.getDeleteCourseButton());
        assertNotNull(pageData.getAddInstructorButton());

        assertNotNull(pageData.getInstructorPanelList());
        assertEquals(instructorList.size(), pageData.getInstructorPanelList().size());

        CourseEditInstructorPanel panel = pageData.getInstructorPanelList().get(0);
        assertEquals(4, panel.getPermissionInputGroup1().size());
        assertEquals(1, panel.getPermissionInputGroup2().size());
        assertEquals(3, panel.getPermissionInputGroup3().size());
        assertEquals("idOfInstructor1OfCourse1", panel.getInstructor().googleId);
        assertNotNull(panel.getDeleteButton());
        assertNotNull(panel.getEditButton());
        assertNull(panel.getResendInviteButton());
        assertEquals(sectionNames.size(), panel.getSectionRows().size());
        CourseEditSectionRow sectionRow = panel.getSectionRows().get(0);
        assertEquals(1, sectionRow.getPermissionInputGroup2().size());
        assertEquals(3, sectionRow.getPermissionInputGroup3().size());
        assertEquals(feedbackSessionNames.size(), sectionRow.getFeedbackSessions().size());
        assertFalse(sectionRow.isSectionSpecial());
        /*
         * Comment for below Assertion:
         * These sections are separated by a group of 3 so here is the formula to get the number
         * of groups.
         */
        assertEquals((sectionNames.size() - 1) / 3 + 1, sectionRow.getSpecialSections().size());

        assertNotNull(pageData.getAddInstructorPanel());
        CourseEditInstructorPanel addInstructorPanel = pageData.getAddInstructorPanel();
        assertEquals(4, addInstructorPanel.getPermissionInputGroup1().size());
        assertEquals(1, addInstructorPanel.getPermissionInputGroup2().size());
        assertEquals(3, addInstructorPanel.getPermissionInputGroup3().size());
        assertEquals(sectionNames.size(), addInstructorPanel.getSectionRows().size());
        sectionRow = addInstructorPanel.getSectionRows().get(0);
        assertEquals(feedbackSessionNames.size(), sectionRow.getFeedbackSessions().size());

        ______TS("test case when current instructor has no privilege");

        String[] privileges = {
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
        };

        for (String privilege : privileges) {
            currentInstructor.privileges.updatePrivilege(privilege, false);
        }

        pageData = new InstructorCourseEditPageData(account, dummySessionToken, course, instructorList, currentInstructor,
                                                    offset, sectionNames, feedbackSessionNames);
        assertNull(pageData.getDeleteCourseButton().getAttributes().get("disabled"));
        assertTrue(pageData.getDeleteCourseButton().getAttributes().containsKey("disabled"));
        assertNull(pageData.getAddInstructorButton().getAttributes().get("disabled"));
        assertTrue(pageData.getAddInstructorButton().getAttributes().containsKey("disabled"));

        ______TS("test showing only one instructor");
        offset = 1;
        pageData = new InstructorCourseEditPageData(account, dummySessionToken, course, instructorList, currentInstructor,
                                                    offset, sectionNames, feedbackSessionNames);
        assertNotNull(pageData.getAddInstructorPanel());
        assertTrue(pageData.getInstructorPanelList().get(0).isAccessControlDisplayed());

        ______TS("test specialSection");
        InstructorAttributes instructor = instructorList.get(0);
        instructor.privileges.addSessionWithDefaultPrivileges("Section 1", "First feedback session");

        pageData = new InstructorCourseEditPageData(account, dummySessionToken, course, instructorList, currentInstructor,
                                                    offset, sectionNames, feedbackSessionNames);
        assertTrue(pageData.getInstructorPanelList().get(0).getSectionRows().get(0).isSectionSpecial());

        ______TS("test empty sectionNames");
        sectionNames = new ArrayList<>();
        pageData = new InstructorCourseEditPageData(account, dummySessionToken, course, instructorList, currentInstructor,
                                                    offset, sectionNames, feedbackSessionNames);
        assertNotNull(pageData.getAddInstructorPanel());

    }
}
