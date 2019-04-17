package teammates.e2e.cases.e2e;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorHomePage;


/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageE2ETest extends BaseE2ETestCase {
    static final String COMMON_INSTRUCTOR = "CHomeUiT.instructor.tmms";
    static final String UNLOADED_COURSE = "CHomeUiT.Unloaded";
    static final String COURSE_WITH_SESSIONS = "CHomeUiT.CS2104";
    static final String COURSE_WITH_NO_PRIVILEGES = "CHomeUiT.CS1101";

    private InstructorHomePage homePage;

    private FeedbackSessionAttributes feedbackSessionAwaiting;
    private FeedbackSessionAttributes feedbackSessionOpen;
    private FeedbackSessionAttributes feedbackSessionClosed;
    private FeedbackSessionAttributes feedbackSessionPublished;

    private List<FeedbackSessionAttributes> feedbackSessions;
    private List<String> courseIds;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        feedbackSessionAwaiting = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSessionOpen = testData.feedbackSessions.get("First Feedback Session");
        feedbackSessionClosed = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSessionPublished = testData.feedbackSessions.get("Fourth Feedback Session");
    }

    @Test
    public void allTests() {
        loginAsCommonInstructor();
        testContent();
        testSortAction();
        testResponseLink();
        testRemindActions();
        testPublishUnpublishResendActions();
        testArchiveCourseAction();
        testCopyToFsAction();
        testDeleteAction();
        logout();
    }

    private void testContent() {

        ______TS("Test multiple courses content");

        courseIds = getAllVisibleCourseIds();
        // default courses are sort by "Creation Date"
        verifyVisibleCourseNames(courseIds);

        int courseIdx = courseIds.indexOf(UNLOADED_COURSE);
        assertFalse(homePage.isCoursePanelExpanded(courseIdx));
        homePage.loadInstructorCoursePanel(courseIdx);
        assertTrue(homePage.isCoursePanelExpanded(courseIdx));

        ______TS("Test case: download action");
        // TODO: to be added after implementation of download result feature

    }

    private void testSortAction() {

        ______TS("Test case: sort courses");
        homePage.clickSortByNameButton();
        List<String> courseNames = getAllVisibleCourseNames(courseIds);
        Collections.sort(courseNames);
        verifyVisibleCourseNames(courseNames);

        homePage.clickSortByIdButton();
        verifyVisibleCourseNames(courseIds);

        // sort by creation date is ignored as it is already checked initially when courses are populated

        ______TS("Test case: sort feedback sessions");
        feedbackSessions = getAllFeedbackSessions(COURSE_WITH_SESSIONS);

        int courseIdx = courseIds.indexOf(COURSE_WITH_SESSIONS);
        homePage.sortTablesByStartDate();
        feedbackSessions.sort(Comparator.comparing(FeedbackSessionAttributes::getStartTime));
        verifyVisibleSessionNames(feedbackSessions, courseIdx);

        homePage.sortTablesByEndDate();
        feedbackSessions.sort(Comparator.comparing(FeedbackSessionAttributes::getEndTime));
        verifyVisibleSessionNames(feedbackSessions, courseIdx);

        homePage.sortTablesByName();
        feedbackSessions.sort(Comparator.comparing(FeedbackSessionAttributes::getFeedbackSessionName));
        verifyVisibleSessionNames(feedbackSessions, courseIdx);

    }

    private void testResponseLink() {

        ______TS("Test case: load feedback session link");

        int fsIdx = feedbackSessions.indexOf(feedbackSessionOpen);
        homePage.clickFsShowLink(fsIdx);
        assertEquals(homePage.getFsViewResponseText(fsIdx), "0 / 4");

    }

    private void testRemindActions() {

        ______TS("Test case: remind action for AWAITING feedback session");

        int fsIdx = feedbackSessions.indexOf(feedbackSessionAwaiting);
        homePage.verifyUnclickable(homePage.getFsRemindStudentsBtn(fsIdx));

        ______TS("Test case: remind action for PUBLISHED feedback session");

        fsIdx = feedbackSessions.indexOf(feedbackSessionPublished);
        homePage.verifyUnclickable(homePage.getFsRemindStudentsBtn(fsIdx));

        ______TS("Test case: remind action for CLOSED feedback session");
        fsIdx = feedbackSessions.indexOf(feedbackSessionClosed);
        homePage.verifyUnclickable(homePage.getFsRemindStudentsBtn(fsIdx));

        ______TS("Test case: remind action for OPEN feedback session");

        fsIdx = feedbackSessions.indexOf(feedbackSessionOpen);
        homePage.clickAndCancel(homePage.getFsRemindStudentsBtn(fsIdx));
        homePage.clickAndConfirm(homePage.getFsRemindStudentsBtn(fsIdx));
        homePage.verifyErrorStatusMessage("List of users to remind cannot be empty");

        homePage.clickAndConfirmRemindStudentsWithUsers(fsIdx);
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

    }

    private void testPublishUnpublishResendActions() {

        ______TS("Test case: publish action for AWAITING feedback session");

        int fsIdx = feedbackSessions.indexOf(feedbackSessionAwaiting);
        homePage.verifyUnclickable(homePage.getFsPublishBtn(fsIdx));
        homePage.verifyResendPublishedEmailButtonDoesNotExist(fsIdx);

        ______TS("Test case: publish action for OPEN feedback session");

        fsIdx = feedbackSessions.indexOf(feedbackSessionOpen);
        homePage.clickAndCancel(homePage.getFsPublishBtn(fsIdx));
        homePage.clickAndConfirm(homePage.getFsPublishBtn(fsIdx));
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);

        homePage.clickAndCancel(homePage.getFsResendPublishedEmail(fsIdx));
        homePage.clickAndConfirm(homePage.getFsResendPublishedEmail(fsIdx));
        homePage.verifyErrorStatusMessage("List of users to remind cannot be empty");

        homePage.clickAndConfirmResendPublishedWithUsers(fsIdx);
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_SENT);

        homePage.clickAndCancel(homePage.getFsUnpublishBtn(fsIdx));
        homePage.clickAndConfirm(homePage.getFsUnpublishBtn(fsIdx));
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);

        ______TS("Test case: publish action for CLOSED feedback session");

        fsIdx = feedbackSessions.indexOf(feedbackSessionClosed);
        homePage.clickAndCancel(homePage.getFsPublishBtn(fsIdx));

        ______TS("Test case: publish action for PUBLISHED feedback session");

        fsIdx = feedbackSessions.indexOf(feedbackSessionPublished);
        homePage.verifyResendPublishedEmailButtonExists(fsIdx);

        homePage.clickAndCancel(homePage.getFsUnpublishBtn(fsIdx));
        homePage.clickAndConfirm(homePage.getFsUnpublishBtn(fsIdx));
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);

    }

    private void testCopyToFsAction() {

        int fsIdx = feedbackSessions.indexOf(feedbackSessionPublished);

        homePage.clickAndCancel(homePage.getFsCopyBtn(fsIdx));
        homePage.clickFsCopyButton(fsIdx);
        homePage.verifyUnclickable(homePage.getModalSubmitBtn());

        homePage.fillFormWithLastCourseSelected("New session copied for testing");
        homePage.clickModalSubmitBtn();
        homePage.verifySuccessStatusMessage("The feedback session has been copied."
                + " Please modify settings/questions as necessary.");
        homePage.clickHomeBtn();

        homePage.clickFsCopyButton(fsIdx);
        homePage.fillFormWithLastCourseSelected("New session copied for testing");
        homePage.clickModalSubmitBtn();
        homePage.verifyErrorStatusMessageContains("Trying to create an entity that exists:");

    }

    private void testArchiveCourseAction() {

        ______TS("Test case: archive course action");

        int courseIdx = courseIds.indexOf(COURSE_WITH_NO_PRIVILEGES);
        homePage.clickAndConfirm(homePage.getCourseArchiveBtn(courseIdx));
        homePage.verifyErrorStatusMessage("You are not authorized to access this resource.");

        // TODO: Archive the course after issue #9675 is resolved
        // courseIdx = courseIds.indexOf(UNLOADED_COURSE);
        // homePage.clickAndCancel(homePage.getCourseArchiveBtn(courseIdx));
        // homePage.clickAndConfirm(homePage.getCourseArchiveBtn(courseIdx));
        // homePage.verifyErrorStatusMessage("The request is not valid.");

    }

    private void testDeleteAction() {

        ______TS("Test case: delete course");

        int courseIdx = courseIds.indexOf(UNLOADED_COURSE);
        homePage.clickAndCancel(homePage.getCourseDeleteBtn(courseIdx));
        homePage.clickAndConfirm(homePage.getCourseDeleteBtn(courseIdx));
        homePage.verifySuccessStatusMessage("The course " + UNLOADED_COURSE + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");

        ______TS("Test case: delete feedback session");

        int indexIdx = feedbackSessions.indexOf(feedbackSessionClosed);
        homePage.clickAndCancel(homePage.getFsDeleteBtn(indexIdx));
        homePage.clickAndConfirm(homePage.getFsDeleteBtn(indexIdx));
        homePage.verifySuccessStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_MOVED_TO_RECYCLE_BIN_FROM_HOMEPAGE);

    }

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        for (InstructorAttributes instructor : testData.instructors.values()) {
            if (COMMON_INSTRUCTOR.equals(instructor.googleId)) {
                courseIds.add(instructor.getCourseId());
            }
        }
        return courseIds;
    }

    private List<String> getAllVisibleCourseNames(List<String> courseIds) {
        List<String> courseNames = new ArrayList<>();

        for (CourseAttributes course : testData.courses.values()) {
            if (courseIds.contains(course.getId())) {
                courseNames.add(course.getName());
            }
        }

        return courseNames;
    }

    private List<FeedbackSessionAttributes> getAllFeedbackSessions(String courseId) {
        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<>();

        for (FeedbackSessionAttributes feedbackSession : testData.feedbackSessions.values()) {
            if (courseId.equals(feedbackSession.getCourseId())) {
                feedbackSessions.add(feedbackSession);
            }
        }
        return feedbackSessions;
    }

    private void verifyVisibleCourseNames(List<String> panelInfo) {
        for (int i = 0; i < panelInfo.size(); i++) {
            assertTrue(verifyVisibleCourseName(panelInfo.get(i), "panel-head-" + i));
        }
    }

    private boolean verifyVisibleCourseName(String courseName, String id) {
        return browser.driver.findElement(By.id(id)).getText().contains(courseName);
    }

    private void verifyVisibleSessionNames(List<FeedbackSessionAttributes> feedbackSessions, int courseIndex) {
        for (int i = 0; i < feedbackSessions.size(); i++) {
            assertTrue(verifyVisibleSessionName(
                    feedbackSessions.get(i).getFeedbackSessionName(), "course-" + courseIndex, i));
        }
    }

    private boolean verifyVisibleSessionName(String fsName, String courseId, int index) {
        return browser.driver.findElement(By.id(courseId))
                .findElement(By.className("session-" + index)).getText().contains(fsName);
    }

    private void loginAsCommonInstructor() {
        AppUrl editUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE).withUserId(COMMON_INSTRUCTOR);

        homePage = loginAdminToPage(editUrl, InstructorHomePage.class);
    }
}
