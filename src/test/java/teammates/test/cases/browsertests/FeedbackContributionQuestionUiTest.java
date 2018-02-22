package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for contribution questions.
 */
public class FeedbackContributionQuestionUiTest extends FeedbackQuestionUiTest {
    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackContributionQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);

        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName);
    }

    @Test
    public void allTests() throws Exception {
        testEditPage();

        //TODO: move/create other Contribution question related UI tests here.
        //i.e. results page, submit page.
    }

    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testCopyOptions();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("CONTRIB: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONTRIB");
        assertTrue(feedbackEditPage.verifyNewContributionQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEqualsErrorTexts(
                Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);
    }

    @Override
    public void testCustomizeOptions() {

        //no question specific options to test

        ______TS("CONTRIB: verify only valid visibility options are visible");

        assertTrue(feedbackEditPage
                .isVisibilityDropdownOptionHiddenForNewQuestion("ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS"));
        assertTrue(feedbackEditPage
                .isVisibilityDropdownOptionHiddenForNewQuestion("ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS"));
        assertFalse(feedbackEditPage
                .isVisibilityDropdownOptionHiddenForNewQuestion("ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS"));
        assertFalse(feedbackEditPage.isVisibilityDropdownOptionHiddenForNewQuestion("VISIBLE_TO_INSTRUCTORS_ONLY"));
        assertTrue(feedbackEditPage.isVisibilityDropdownOptionHiddenForNewQuestion("VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS"));
        assertTrue(feedbackEditPage.isVisibilityDropdownSeparatorHiddenForNewQuestion());
        assertTrue(feedbackEditPage.isVisibilityDropdownOptionHiddenForNewQuestion("OTHER"));

        ______TS("CONTRIB: verify correct output params for selected visibility option");

        // default (first option)
        assertEquals("RECEIVER,OWN_TEAM_MEMBERS,RECEIVER_TEAM_MEMBERS,INSTRUCTORS",
                feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("RECEIVER,INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "VISIBLE_TO_INSTRUCTORS_ONLY");
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS");
        assertEquals("RECEIVER,OWN_TEAM_MEMBERS,RECEIVER_TEAM_MEMBERS,INSTRUCTORS",
                feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("RECEIVER,INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("CONTRIB: add question action success");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("contrib qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("CONTRIB: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);

        //Check invalid feedback paths are disabled.
        //Javascript should hide giver/recipient options that are not STUDENTS to OWN_TEAM_MEMBERS_INCLUDING_SELF
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEdit.html");

        feedbackEditPage.fillQuestionTextBox("edited contrib qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.toggleNotSureCheck(1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEditSuccess.html");
    }

    @Override
    public void testDeleteQuestionAction() {
        ______TS("CONTRIB: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONTRIB: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    /**
     * Tests the copying of options to/from contribution questions. Options should not be
     * copied in the case of a contribution question being added after a non-contribution
     * question. This is to prevent options that are invalid for contribution questions
     * from being copied over to a contribution question.
     */
    private void testCopyOptions() {
        ______TS("CONTRIB: skip copy visibility options non-contrib -> contrib");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("q1, essay qn");
        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "VISIBLE_TO_INSTRUCTORS_ONLY");
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONTRIB");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("q2, contribution qn");
        assertEquals("Shown anonymously to recipient and team members, visible to instructors",
                feedbackEditPage.getVisibilityDropdownLabelForNewQuestion());
        assertEquals("RECEIVER,OWN_TEAM_MEMBERS,RECEIVER_TEAM_MEMBERS,INSTRUCTORS",
                feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("RECEIVER,INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "VISIBLE_TO_INSTRUCTORS_ONLY");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("CONTRIB: copy visibility options contrib -> contrib");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONTRIB");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("q3, contribution qn");
        assertEquals("Visible to instructors only",
                feedbackEditPage.getVisibilityDropdownLabelForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("CONTRIB: copy visibility options contrib -> non-contrib");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");

        assertEquals("Shown anonymously to recipient and team members, visible to instructors",
                feedbackEditPage.getVisibilityDropdownLabelForNewQuestion());
        assertEquals("RECEIVER,OWN_TEAM_MEMBERS,INSTRUCTORS",
                feedbackEditPage.getVisibilityParamShowResponsesToForNewQuestion());
        assertEquals("INSTRUCTORS", feedbackEditPage.getVisibilityParamShowGiverToForNewQuestion());
        assertEquals("RECEIVER,INSTRUCTORS", feedbackEditPage.getVisibilityParamShowRecipientToForNewQuestion());

    }

}
