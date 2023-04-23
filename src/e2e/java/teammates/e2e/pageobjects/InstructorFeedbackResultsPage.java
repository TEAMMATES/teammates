package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.e2e.util.TestProperties;

/**
 * Represents the "Results" page for Instructors.
 */
public class InstructorFeedbackResultsPage extends AppPage {
    private static final String QUESTION_VIEW = "QUESTION";
    private static final String GQR_VIEW = "GQR";
    private static final String RQG_VIEW = "RQG";
    private static final String GRQ_VIEW = "GRQ";
    private static final String RGQ_VIEW = "RGQ";

    private static final String NO_RESPONSE_LABEL = "No Response";
    private static final String NO_TEAM_LABEL = "No Specific Team";
    private static final String NO_SECTION_LABEL = "No specific section";
    private static final String NO_USER_LABEL = "No Specific User";

    private static final String MCQ_OTHER = "Other";

    private String currentView = "";

    @FindBy(id = "course-id")
    private WebElement courseId;

    @FindBy(id = "session-name")
    private WebElement sessionName;

    @FindBy(id = "session-duration")
    private WebElement sessionDuration;

    @FindBy(id = "result-visible-date")
    private WebElement resultVisibleDate;

    @FindBy(id = "btn-publish")
    private WebElement publishButton;

    @FindBy(id = "btn-download")
    private WebElement downloadButton;

    @FindBy(id = "no-response-panel")
    private WebElement noResponsePanel;

    @FindBy(id = "btn-remind-all")
    private WebElement remindAllButton;

    @FindBy(id = "include-team-grouping")
    private WebElement groupByTeamCheckbox;

    @FindBy(id = "include-statistics")
    private WebElement statisticsCheckbox;

    @FindBy(id = "include-missing-responses")
    private WebElement missingResponsesCheckbox;

    public InstructorFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Feedback Session Results");
    }

    public void verifySessionDetails(FeedbackSessionAttributes feedbackSession) {
        assertEquals(feedbackSession.getCourseId(), courseId.getText());
        assertEquals(feedbackSession.getFeedbackSessionName(), sessionName.getText());
        assertEquals(getSessionDurationString(feedbackSession), sessionDuration.getText());
        assertEquals(getDateString(feedbackSession.getResultsVisibleFromTime(), feedbackSession.getTimeZone()),
                resultVisibleDate.getText());
    }

    public void unpublishSessionResults() {
        if (publishButton.getText().contains("Unpublish")) {
            clickAndConfirm(publishButton);
        }
    }

    public void publishSessionResults() {
        if (publishButton.getText().contains("Publish")) {
            clickAndConfirm(publishButton);
        }
    }

    public void downloadResults() {
        click(downloadButton);
    }

    public void sortNoResponseByName() {
        click(getNoResponseTable().findElement(By.id("sort-by-name")));
        waitUntilAnimationFinish();
    }

    public void verifyNoResponsePanelDetails(List<StudentAttributes> noResponseStudents) {
        verifyTableBodyValues(getNoResponseTable(), getExpectedNoResponseDetails(noResponseStudents));
    }

    public void remindAllNonResponders() {
        click(remindAllButton);
        click(waitForElementPresence(By.id("btn-confirm-send-reminder")));
    }

    public void includeGroupingByTeam(boolean isIncluded) {
        includeOption(isIncluded, groupByTeamCheckbox);
    }

    public void includeMissingResponses(boolean isIncluded) {
        includeOption(isIncluded, missingResponsesCheckbox);
    }

    public void includeStatistics(boolean isIncluded) {
        includeOption(isIncluded, statisticsCheckbox);
    }

    public void expandAllPanels() {
        WebElement expandAllButton = browser.driver.findElement(By.id("btn-expand-all"));
        if (expandAllButton.getText().contains("Expand")) {
            click(expandAllButton);
            waitUntilAnimationFinish();
        }
    }

    public void filterBySectionEither(String sectionName) {
        selectSectionDropdown(sectionName);
        selectSectionTypeDropdown("0: EITHER");
    }

    public void unfilterResponses() {
        selectSectionDropdown("All");
    }

    public void verifyQnViewResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
                                      Collection<InstructorAttributes> instructors, Collection<StudentAttributes> students) {
        selectViewType(QUESTION_VIEW);

        WebElement questionPanel = getQuestionPanel(question.getQuestionNumber());
        verifyQuestionText(questionPanel, question);

        List<FeedbackResponseAttributes> responsesWithoutMissing = filterMissingResponses(responses);
        if (responsesWithoutMissing.isEmpty()) {
            verifyNoResponsesMessage(questionPanel, true, true);
        } else {
            String[][] expectedDetails = getExpectedQnViewDetails(question, responses, instructors, students);
            verifyTableBodyValues(getResponseTable(questionPanel), expectedDetails);
        }
    }

    public void verifyGrqViewResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
                                       boolean isGroupedByTeam, Collection<InstructorAttributes> instructors,
                                       Collection<StudentAttributes> students) {
        selectViewType(GRQ_VIEW);

        // all responses should be from the same giver
        String giver = responses.get(0).getGiver();
        FeedbackParticipantType giverType = question.getGiverType();
        WebElement giverPanel = getUserPanel(giverType, giver, instructors, students, isGroupedByTeam, true);

        List<String> recipients = getRecipients(responses);
        for (String recipient : recipients) {
            FeedbackResponseAttributes responseForRecipient = getResponseForRecipient(responses, recipient);
            FeedbackParticipantType recipientType = question.getRecipientType();
            String recipientTeam = getTeam(recipientType, recipient, students);
            String recipientName = getName(recipientType, recipient, instructors, students);

            verifyGroupedResponses(question, giverPanel, recipientName, recipientTeam, responseForRecipient, true);
        }
    }

    public void verifyRgqViewResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
                                       boolean isGroupedByTeam, Collection<InstructorAttributes> instructors,
                                       Collection<StudentAttributes> students) {
        selectViewType(RGQ_VIEW);

        // all responses should have the same recipient
        String recipient = responses.get(0).getRecipient();
        FeedbackParticipantType recipientType = question.getRecipientType();
        WebElement recipientPanel = getUserPanel(recipientType, recipient, instructors, students, isGroupedByTeam, false);

        List<String> givers = getGivers(responses);
        for (String giver : givers) {
            FeedbackResponseAttributes responseFromGiver = getResponseFromGiver(responses, giver);
            FeedbackParticipantType giverType = question.getGiverType();
            String giverTeam = getTeam(giverType, giver, students);
            String giverName = getName(giverType, giver, instructors, students);

            verifyGroupedResponses(question, recipientPanel, giverName, giverTeam, responseFromGiver, false);
        }
    }

    public void verifyGqrViewResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
                                       boolean isGroupedByTeam, Collection<InstructorAttributes> instructors,
                                       Collection<StudentAttributes> students) {
        selectViewType(GQR_VIEW);

        // all responses should be from the same giver
        String giver = responses.get(0).getGiver();
        FeedbackParticipantType giverType = question.getGiverType();
        WebElement giverPanel = getUserPanel(giverType, giver, instructors, students, isGroupedByTeam, true);

        WebElement questionPanel = getQuestionPanel(giverPanel, question.getQuestionNumber());
        verifyQuestionText(questionPanel, question);

        List<FeedbackResponseAttributes> responsesWithoutMissing = filterMissingResponses(responses);

        if (responsesWithoutMissing.isEmpty()) {
            verifyNoResponsesMessage(questionPanel, true, true);
        } else {
            for (FeedbackResponseAttributes response : responses) {
                String[] expectedResponses = getExpectedGqrDetails(question, response, instructors, students);
                String recipientTeam = getTeam(question.getRecipientType(), response.getRecipient(), students);
                String recipientNameAndEmail = getNameAndEmail(question.getRecipientType(), response.getRecipient(),
                        instructors, students);
                verifyTableRowValues(getResponseRow(questionPanel, recipientTeam, recipientNameAndEmail),
                        expectedResponses);
            }
        }
    }

    public void verifyRqgViewResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
                                       boolean isGroupedByTeam, Collection<InstructorAttributes> instructors,
                                       Collection<StudentAttributes> students) {
        selectViewType(RQG_VIEW);

        // all responses should be from the same recipient
        String recipient = responses.get(0).getRecipient();
        FeedbackParticipantType recipientType = question.getRecipientType();
        WebElement recipientPanel = getUserPanel(recipientType, recipient, instructors, students, isGroupedByTeam, false);

        WebElement questionPanel = getQuestionPanel(recipientPanel, question.getQuestionNumber());
        verifyQuestionText(questionPanel, question);

        List<FeedbackResponseAttributes> responsesWithoutMissing = filterMissingResponses(responses);

        if (responsesWithoutMissing.isEmpty()) {
            verifyNoResponsesMessage(questionPanel, true, true);
        } else {
            for (FeedbackResponseAttributes response : responses) {
                String[] expectedResponses = getExpectedRqgDetails(question, response, instructors, students);
                String giverTeam = getTeam(question.getGiverType(), response.getGiver(), students);
                String giverNameAndEmail = getNameAndEmail(question.getGiverType(), response.getGiver(), instructors,
                        students);
                verifyTableRowValues(getResponseRow(questionPanel, giverTeam, giverNameAndEmail), expectedResponses);
            }
        }
    }

    private void verifyQuestionText(WebElement questionPanel, FeedbackQuestionAttributes question) {
        assertEquals(question.getQuestionDetailsCopy().getQuestionText(), getQuestionText(questionPanel));
    }

    private void verifyGroupedResponses(FeedbackQuestionAttributes question, WebElement userPanel, String userName,
                                        String userTeam, FeedbackResponseAttributes response, boolean isGrq) {
        WebElement groupedResponses;
        try {
            groupedResponses = getGroupedResponses(userPanel, userName, userTeam, isGrq);
        } catch (NoSuchElementException e) {
            // No response message shown instead of grouped responses
            // if all responses in panel are missing responses
            assertTrue(isMissingResponse(response));
            verifyNoResponsesMessage(userPanel, false, isGrq);
            return;
        }

        if (groupedResponses == null) {
            // Empty grouped response if this user only has missing responses
            assertTrue(isMissingResponse(response));
            return;
        }

        WebElement questionPanel = getQuestionPanel(groupedResponses, question.getQuestionNumber());
        verifyQuestionText(questionPanel, question);
        WebElement singleResponse = questionPanel.findElement(By.id("response"));
        if (isMissingResponse(response)) {
            // Missing response will only be shown if this user has some real responses
            assertEquals(NO_RESPONSE_LABEL, singleResponse.getText());
        } else {
            assertEquals(getAnswerString(question, response.getResponseDetailsCopy()), singleResponse.getText());
        }
    }

    public void verifyQnViewStats(FeedbackQuestionAttributes question,
                                  List<FeedbackResponseAttributes> responses,
                                  Collection<InstructorAttributes> instructors,
                                  Collection<StudentAttributes> students) {
        selectViewType(QUESTION_VIEW);
        WebElement questionPanel = getQuestionPanel(question.getQuestionNumber());
        // re-expand question panel to reset sorting order
        hideQuestionPanel(questionPanel);
        expandQuestionPanel(questionPanel);
        verifyStatistics(questionPanel, question, responses, instructors, students);
    }

    public void verifyGqrViewStats(FeedbackQuestionAttributes question,
                                   List<FeedbackResponseAttributes> responses,
                                   boolean isGroupedByTeam,
                                   Collection<InstructorAttributes> instructors,
                                   Collection<StudentAttributes> students) {
        selectViewType(GQR_VIEW);

        String giver = responses.get(0).getGiver();
        FeedbackParticipantType giverType = question.getGiverType();
        verifyUserViewStats(giverType, giver, question, responses, instructors, students, isGroupedByTeam, true);
    }

    public void verifyRqgViewStats(FeedbackQuestionAttributes question,
                                   List<FeedbackResponseAttributes> responses,
                                   boolean isGroupedByTeam,
                                   Collection<InstructorAttributes> instructors,
                                   Collection<StudentAttributes> students) {
        selectViewType(RQG_VIEW);

        String recipient = responses.get(0).getRecipient();
        FeedbackParticipantType recipientType = question.getRecipientType();
        verifyUserViewStats(recipientType, recipient, question, responses, instructors, students, isGroupedByTeam, false);
    }

    private void verifyUserViewStats(FeedbackParticipantType type, String user,
                                     FeedbackQuestionAttributes question,
                                     List<FeedbackResponseAttributes> responses,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students,
                                     boolean isGroupedByTeam,
                                     boolean isGiver) {
        WebElement panelWithStats = getPanelWithStats(type, user, question, instructors, students,
                isGroupedByTeam, isGiver);
        verifyStatistics(panelWithStats, question, responses, instructors, students);
    }

    private void verifyStatistics(WebElement questionPanel, FeedbackQuestionAttributes question,
                                  List<FeedbackResponseAttributes> responses,
                                  Collection<InstructorAttributes> instructors,
                                  Collection<StudentAttributes> students) {
        switch (question.getQuestionType()) {
        case MCQ:
            verifyMcqStatistics(questionPanel, question, responses, instructors, students);
            break;
        case TEXT:
        case NUMSCALE:
        case RANK_RECIPIENTS:
        case MSQ:
        case RUBRIC:
        case RANK_OPTIONS:
        case CONSTSUM:
        case CONTRIB:
            return; // TODO: Find way to test different statistics efficiently.
        default:
            throw new RuntimeException("Unknown question type: " + question.getQuestionType());
        }
    }

    private void verifyMcqStatistics(WebElement questionPanel, FeedbackQuestionAttributes question,
                                     List<FeedbackResponseAttributes> responses,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students) {
        List<FeedbackResponseAttributes> responsesToUse = filterMissingResponses(responses);
        List<WebElement> statisticsTables = questionPanel.findElements(By.cssSelector("#mcq-statistics table"));
        verifyTableBodyValues(statisticsTables.get(0), getMcqResponseSummary(question));
        // sort per recipient statistics
        click(statisticsTables.get(1).findElements(By.tagName("th")).get(1));
        verifyTableBodyValues(statisticsTables.get(1), getMcqPerRecipientStatistics(question, responsesToUse, students,
                instructors));
    }

    public void verifyQnViewStatsHidden(FeedbackQuestionAttributes question) {
        selectViewType(QUESTION_VIEW);

        WebElement questionPanel = getQuestionPanel(question.getQuestionNumber());
        verifyStatsHidden(questionPanel);
    }

    public void verifyGqrViewStatsHidden(FeedbackQuestionAttributes question,
                                         String giver,
                                         Collection<InstructorAttributes> instructors,
                                         Collection<StudentAttributes> students,
                                         boolean isGroupedByTeam) {
        selectViewType(GQR_VIEW);

        FeedbackParticipantType giverType = question.getGiverType();
        WebElement panelWithStats = getPanelWithStats(giverType, giver, question, instructors, students,
                isGroupedByTeam, true);
        verifyStatsHidden(panelWithStats);
    }

    public void verifyRqgViewStatsHidden(FeedbackQuestionAttributes question,
                                         String recipient,
                                         Collection<InstructorAttributes> instructors,
                                         Collection<StudentAttributes> students,
                                         boolean isGroupedByTeam) {
        selectViewType(RQG_VIEW);

        FeedbackParticipantType recipientType = question.getRecipientType();
        WebElement panelWithStats = getPanelWithStats(recipientType, recipient, question, instructors, students,
                isGroupedByTeam, false);
        verifyStatsHidden(panelWithStats);
    }

    private WebElement getPanelWithStats(FeedbackParticipantType type, String user,
                                         FeedbackQuestionAttributes question,
                                         Collection<InstructorAttributes> instructors,
                                         Collection<StudentAttributes> students,
                                         boolean isGroupedByTeam,
                                         boolean isGiver) {
        String section = getSection(type, user, students);
        String team = getTeam(type, user, students);
        String name = getName(type, user, instructors, students);
        String header = getUserHeader(isGiver, name);
        int qnNum = question.getQuestionNumber();
        if (isGroupedByTeam) {
            WebElement teamPanel = getUserParentPanel(section, team, true);
            return getTeamStats(teamPanel, qnNum);
        } else {
            WebElement userPanel = getUserPanel(section, team, header, false);
            return getQuestionPanel(userPanel, qnNum);
        }
    }

    private void verifyStatsHidden(WebElement panelWithStats) {
        assertTrue(panelWithStats.findElements(By.tagName("tm-single-statistics")).isEmpty());
    }

    public void verifyQnViewComment(FeedbackQuestionAttributes question, FeedbackResponseCommentAttributes comment,
                                    FeedbackResponseAttributes response, Collection<InstructorAttributes> instructors,
                                    Collection<StudentAttributes> students) {
        selectViewType(QUESTION_VIEW);
        WebElement questionPanel = getQuestionPanel(question.getQuestionNumber());

        String giverTeam = getTeam(question.getGiverType(), response.getGiver(), students);
        String giverName = getNameAndEmail(question.getGiverType(), response.getGiver(), instructors, students);
        String recipientTeam = getTeam(question.getRecipientType(), response.getRecipient(), students);
        String recipientName = getNameAndEmail(question.getRecipientType(), response.getRecipient(), instructors,
                students);
        WebElement responseRow = getResponseRow(questionPanel, giverTeam, giverName, recipientTeam, recipientName);

        verifyResponseRowComment(responseRow, comment, instructors, students);
    }

    public void verifyGqrViewComment(FeedbackQuestionAttributes question, FeedbackResponseCommentAttributes comment,
                                     FeedbackResponseAttributes response,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students, boolean isGroupedByTeam) {
        selectViewType(GQR_VIEW);

        FeedbackParticipantType giverType = question.getGiverType();
        WebElement giverPanel = getUserPanel(giverType, response.getGiver(), instructors, students, isGroupedByTeam, true);

        WebElement questionPanel = getQuestionPanel(giverPanel, question.getQuestionNumber());
        String recipientTeam = getTeam(question.getRecipientType(), response.getRecipient(), students);
        String recipientName = getNameAndEmail(question.getRecipientType(), response.getRecipient(), instructors,
                students);
        WebElement responseRow = getResponseRow(questionPanel, recipientTeam, recipientName);

        verifyResponseRowComment(responseRow, comment, instructors, students);
    }

    public void verifyRqgViewComment(FeedbackQuestionAttributes question, FeedbackResponseCommentAttributes comment,
                                     FeedbackResponseAttributes response,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students, boolean isGroupedByTeam) {
        selectViewType(RQG_VIEW);

        FeedbackParticipantType recipientType = question.getRecipientType();
        WebElement recipientPanel = getUserPanel(recipientType, response.getRecipient(), instructors, students,
                isGroupedByTeam, false);

        WebElement questionPanel = getQuestionPanel(recipientPanel, question.getQuestionNumber());
        String giverTeam = getTeam(question.getGiverType(), response.getGiver(), students);
        String giverNameAndEmail = getNameAndEmail(question.getGiverType(), response.getGiver(), instructors, students);
        WebElement responseRow = getResponseRow(questionPanel, giverTeam, giverNameAndEmail);

        verifyResponseRowComment(responseRow, comment, instructors, students);
    }

    public void verifyGrqViewComment(FeedbackQuestionAttributes question, FeedbackResponseCommentAttributes comment,
                                     FeedbackResponseAttributes response,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students, boolean isGroupedByTeam) {

        selectViewType(GRQ_VIEW);

        FeedbackParticipantType giverType = question.getGiverType();
        WebElement userPanel = getUserPanel(giverType, response.getGiver(), instructors, students, isGroupedByTeam, true);

        FeedbackParticipantType recipientType = question.getRecipientType();
        String recipientTeam = getTeam(recipientType, response.getRecipient(), students);
        String recipientName = getName(recipientType, response.getRecipient(), instructors, students);

        WebElement groupedResponses = getGroupedResponses(userPanel, recipientName, recipientTeam, true);
        verifyGroupedResponseComment(groupedResponses, question.getQuestionNumber(), comment, instructors, students);
    }

    public void verifyRgqViewComment(FeedbackQuestionAttributes question, FeedbackResponseCommentAttributes comment,
                                     FeedbackResponseAttributes response,
                                     Collection<InstructorAttributes> instructors,
                                     Collection<StudentAttributes> students, boolean isGroupedByTeam) {
        selectViewType(RGQ_VIEW);

        FeedbackParticipantType recipientType = question.getRecipientType();
        WebElement userPanel = getUserPanel(recipientType, response.getRecipient(), instructors, students,
                isGroupedByTeam, false);

        FeedbackParticipantType giverType = question.getGiverType();
        String giverTeam = getTeam(giverType, response.getGiver(), students);
        String giverName = getName(giverType, response.getGiver(), instructors, students);

        WebElement groupedResponses = getGroupedResponses(userPanel, giverName, giverTeam, false);
        verifyGroupedResponseComment(groupedResponses, question.getQuestionNumber(), comment, instructors, students);
    }

    private void verifyResponseRowComment(WebElement responseRow, FeedbackResponseCommentAttributes comment,
                                          Collection<InstructorAttributes> instructors,
                                          Collection<StudentAttributes> students) {
        click(responseRow.findElement(By.id("btn-add-comment")));
        WebElement commentModal = waitForElementPresence(By.className("modal-body"));

        String editor = getName(comment.getCommentGiverType(),
                comment.getLastEditorEmail(), instructors, students);
        String commentGiver = getName(comment.getCommentGiverType(), comment.getCommentGiver(),
                instructors, students);
        verifyCommentDetails(commentModal, commentGiver, editor, comment.getCommentText(), true);
    }

    private void verifyGroupedResponseComment(WebElement groupedResponses, int qnNum,
                                              FeedbackResponseCommentAttributes comment,
                                              Collection<InstructorAttributes> instructors,
                                              Collection<StudentAttributes> students) {
        WebElement questionPanel = getQuestionPanel(groupedResponses, qnNum);

        String editor = getName(comment.getCommentGiverType(), comment.getLastEditorEmail(),
                instructors, students);
        String commentGiver = getName(comment.getCommentGiverType(), comment.getCommentGiver(),
                instructors, students);
        verifyCommentDetails(questionPanel, commentGiver, editor, comment.getCommentText(), false);
    }

    public void verifyCommentDetails(WebElement commentSection, String commentGiver, String commentEditor,
                                     String commentString, boolean isClosable) {
        WebElement commentField = getCommentField(commentSection, commentString);
        assertEquals(commentGiver, getCommentGiver(commentField));
        if (!commentEditor.isEmpty()) {
            assertEquals(commentEditor, getCommentEditor(commentField));
        }
        if (isClosable) {
            click(waitForElementPresence(By.id("btn-close-comments")));
            waitForPageToLoad();
        }
    }

    private void verifyNoResponsesMessage(WebElement panel, boolean isQuestion, boolean isGiver) {
        WebElement noResponsesMessage = panel.findElement(By.id("no-responses"));
        if (isQuestion) {
            assertEquals("There are no responses for this question or you may not have the permission to"
                    + " see the response.", noResponsesMessage.getText());
        } else {
            assertEquals("There are no responses " + (isGiver ? "given" : "received")
                    + " by this user or you may not have the permission to see the responses.",
                    noResponsesMessage.getText());
        }
    }

    // Methods for formatting expected results
    private String[][] getExpectedNoResponseDetails(List<StudentAttributes> noResponseStudents) {
        String[][] expectedDetails = new String[noResponseStudents.size()][2];
        for (int i = 0; i < noResponseStudents.size(); i++) {
            expectedDetails[i][0] = noResponseStudents.get(i).getTeam();
            expectedDetails[i][1] = String.format("%s (%s)",
                    noResponseStudents.get(i).getName(),
                    noResponseStudents.get(i).getEmail()
            );
        }
        return expectedDetails;
    }

    private String[][] getExpectedQnViewDetails(FeedbackQuestionAttributes question,
                                                List<FeedbackResponseAttributes> responses,
                                                Collection<InstructorAttributes> instructors,
                                                Collection<StudentAttributes> students) {
        String[][] expected = new String[responses.size()][5];
        FeedbackParticipantType giverType = question.getGiverType();
        FeedbackParticipantType recipientType = question.getRecipientType();

        for (int i = 0; i < responses.size(); i++) {
            FeedbackResponseAttributes response = responses.get(i);
            expected[i][0] = getTeam(giverType, response.getGiver(), students);
            expected[i][1] = getNameAndEmail(giverType, response.getGiver(), instructors, students);
            if (recipientType.equals(FeedbackParticipantType.NONE)) {
                expected[i][2] = NO_TEAM_LABEL;
                expected[i][3] = NO_USER_LABEL;
            } else {
                expected[i][2] = getTeam(recipientType, response.getRecipient(), students);
                expected[i][3] = getNameAndEmail(recipientType, response.getRecipient(), instructors, students);
            }
            if (isMissingResponse(response)) {
                expected[i][4] = NO_RESPONSE_LABEL;
            } else {
                expected[i][4] = getAnswerString(question, response.getResponseDetailsCopy());
            }
        }
        return expected;
    }

    private String[] getExpectedGqrDetails(FeedbackQuestionAttributes question,
                                           FeedbackResponseAttributes response,
                                           Collection<InstructorAttributes> instructors,
                                           Collection<StudentAttributes> students) {
        String[] expected = new String[3];
        FeedbackParticipantType recipientType = question.getRecipientType();
        if (recipientType.equals(FeedbackParticipantType.NONE)) {
            expected[0] = NO_TEAM_LABEL;
            expected[1] = NO_USER_LABEL;
        } else {
            expected[0] = getTeam(recipientType, response.getRecipient(), students);
            expected[1] = getNameAndEmail(recipientType, response.getRecipient(), instructors, students);
        }
        if (response.getFeedbackSessionName() == null) {
            expected[2] = NO_RESPONSE_LABEL;
        } else {
            expected[2] = getAnswerString(question, response.getResponseDetailsCopy());
        }
        return expected;
    }

    private String[] getExpectedRqgDetails(FeedbackQuestionAttributes question,
                                           FeedbackResponseAttributes response,
                                           Collection<InstructorAttributes> instructors,
                                           Collection<StudentAttributes> students) {
        String[] expected = new String[3];
        FeedbackParticipantType giverType = question.getGiverType();
        if (giverType.equals(FeedbackParticipantType.NONE)) {
            expected[0] = NO_TEAM_LABEL;
            expected[1] = NO_USER_LABEL;
        } else {
            expected[0] = getTeam(giverType, response.getGiver(), students);
            expected[1] = getNameAndEmail(giverType, response.getGiver(), instructors, students);
        }
        if (response.getFeedbackSessionName() == null) {
            expected[2] = NO_RESPONSE_LABEL;
        } else {
            expected[2] = getAnswerString(question, response.getResponseDetailsCopy());
        }
        return expected;
    }

    private String[][] getMcqResponseSummary(FeedbackQuestionAttributes question) {
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) question.getQuestionDetailsCopy();
        List<String> choices = questionDetails.getMcqChoices();
        List<Double> weights = questionDetails.getMcqWeights();
        Double otherWeight = questionDetails.getMcqOtherWeight();
        boolean isOtherEnabled = questionDetails.isOtherEnabled();
        boolean hasAssignedWeights = questionDetails.isHasAssignedWeights();

        int numRows = isOtherEnabled ? choices.size() + 1 : choices.size();
        String[][] expectedStatistics = new String[numRows][2];
        for (int i = 0; i < choices.size(); i++) {
            expectedStatistics[i][0] = choices.get(i);
            expectedStatistics[i][1] = hasAssignedWeights ? getDoubleString(weights.get(i)) : "-";
        }
        if (isOtherEnabled) {
            int index = choices.size();
            expectedStatistics[index][0] = MCQ_OTHER;
            expectedStatistics[index][1] = hasAssignedWeights ? getDoubleString(otherWeight) : "-";
        }
        return expectedStatistics;
    }

    private String[][] getMcqPerRecipientStatistics(FeedbackQuestionAttributes question,
                                                    List<FeedbackResponseAttributes> responses,
                                                    Collection<StudentAttributes> students,
                                                    Collection<InstructorAttributes> instructors) {
        List<String> recipients = getRecipients(responses);
        recipients.sort(Comparator.naturalOrder());

        String[][] expectedStatistics = new String[recipients.size()][2];

        for (int i = 0; i < recipients.size(); i++) {
            String recipient = recipients.get(i);
            expectedStatistics[i][0] = getTeam(question.getRecipientType(), recipient, students);
            expectedStatistics[i][1] = getNameAndEmail(question.getRecipientType(), recipient, instructors, students);
        }

        return expectedStatistics;
    }

    private String getAnswerString(FeedbackQuestionAttributes question, FeedbackResponseDetails response) {
        switch (response.getQuestionType()) {
        case TEXT:
        case NUMSCALE:
        case RANK_RECIPIENTS:
            return response.getAnswerString();
        case MCQ:
        case MSQ:
            return response.getAnswerString().replace(", ", TestProperties.LINE_SEPARATOR);
        case RUBRIC:
            return getRubricAnsString((FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy(),
                    (FeedbackRubricResponseDetails) response);
        case RANK_OPTIONS:
            return getRankOptionsAnsString((FeedbackRankOptionsQuestionDetails) question.getQuestionDetailsCopy(),
                    (FeedbackRankOptionsResponseDetails) response);
        case CONSTSUM:
            return getConstSumOptionsAnsString((FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy(),
                    (FeedbackConstantSumResponseDetails) response);
        case CONTRIB:
            return getContribAnsString((FeedbackContributionResponseDetails) response);
        default:
            throw new RuntimeException("Unknown question type: " + response.getQuestionType());
        }
    }

    private String getRubricAnsString(FeedbackRubricQuestionDetails question,
                                      FeedbackRubricResponseDetails responseDetails) {
        List<String> choices = question.getRubricChoices();
        List<Integer> answers = responseDetails.getAnswer();
        List<String> answerStrings = new ArrayList<>();
        for (int answer : answers) {
            answerStrings.add(choices.get(answer) + " (Choice " + (answer + 1) + ")");
        }
        return String.join(TestProperties.LINE_SEPARATOR, answerStrings);
    }

    private String getRankOptionsAnsString(FeedbackRankOptionsQuestionDetails question,
                                           FeedbackRankOptionsResponseDetails responseDetails) {
        List<String> options = question.getOptions();
        List<Integer> answers = responseDetails.getAnswers();
        List<String> answerStrings = new ArrayList<>();
        for (int i = 1; i <= options.size(); i++) {
            answerStrings.add(i + ": " + options.get(answers.indexOf(i)));
        }
        return String.join(TestProperties.LINE_SEPARATOR, answerStrings);
    }

    private String getConstSumOptionsAnsString(FeedbackConstantSumQuestionDetails question,
                                               FeedbackConstantSumResponseDetails responseDetails) {
        if (question.isDistributeToRecipients()) {
            return responseDetails.getAnswerString();
        }
        List<String> options = question.getConstSumOptions();
        List<Integer> answers = responseDetails.getAnswers();
        List<String> answerStrings = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            answerStrings.add(options.get(i) + ": " + answers.get(i));
        }
        answerStrings.sort(Comparator.naturalOrder());
        return String.join(TestProperties.LINE_SEPARATOR, answerStrings);
    }

    private String getContribAnsString(FeedbackContributionResponseDetails responseDetails) {
        int answer = responseDetails.getAnswer() - 100;
        StringBuilder answerSb = new StringBuilder("Equal Share");
        if (answer < 0) {
            answerSb.append(" -").append(answer);
        } else if (answer > 0) {
            answerSb.append(' ').append(answer);
        }
        return answerSb.toString();
    }

    private String getUserHeader(boolean isGiver, String name) {
        return (isGiver ? "From: " : "To: ") + name;
    }

    private String getSessionDurationString(FeedbackSessionAttributes feedbackSession) {
        return getDateString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()) + "   to\n"
                + getDateString(feedbackSession.getEndTime(), feedbackSession.getTimeZone());
    }

    private String getDateString(Instant date, String timeZone) {
        return getDisplayedDateTime(date, timeZone, "EEE, dd MMM, yyyy, hh:mm a X");
    }

    private String getDoubleString(double value) {
        int numDecimalPlaces = 0;
        if (value % 1 != 0) {
            numDecimalPlaces = Double.toString(value).split("\\.")[1].length();
        }
        if (numDecimalPlaces > 2) {
            numDecimalPlaces = 2;
        }
        return String.format("%." + numDecimalPlaces + "f", value);
    }

    // Methods for interacting with WebElements

    private WebElement getNoResponseTable() {
        WebElement noResponseHeader = noResponsePanel.findElement(By.className("card-header"));
        By tableId = By.id("no-response-table");
        if (!isElementPresent(tableId)) {
            click(noResponseHeader);
        }
        return waitForElementPresence(tableId);
    }

    private void includeOption(boolean isIncluded, WebElement option) {
        if (isIncluded) {
            markOptionAsSelected(option);
        } else {
            markOptionAsUnselected(option);
        }
        expandAllPanels();
    }

    private void selectViewType(String viewValue) {
        if (currentView.equals(viewValue)) {
            return;
        }

        selectDropdownOptionByValue(waitForElementPresence(By.id("view-type-dropdown")), viewValue);
        currentView = viewValue;
        expandAllPanels();
        waitUntilAnimationFinish();
    }

    private void selectSectionDropdown(String sectionName) {
        WebElement sectionDropdown = browser.driver.findElement(By.id("section-dropdown"));
        selectDropdownOptionByText(sectionDropdown, sectionName);
        waitUntilAnimationFinish();
    }

    private void selectSectionTypeDropdown(String sectionTypeValue) {
        WebElement sectionTypeDropdown = browser.driver.findElement(By.id("section-type-dropdown"));
        selectDropdownOptionByValue(sectionTypeDropdown, sectionTypeValue);
        waitUntilAnimationFinish();
    }

    private WebElement getQuestionPanel(int qnNum) {
        return getQuestionPanel(null, qnNum);
    }

    private WebElement getQuestionPanel(WebElement parentPanel, int qnNum) {
        List<WebElement> questionPanels;
        if (parentPanel == null) {
            questionPanels = browser.driver.findElements(By.cssSelector("[id^='question-panel-']"));
        } else {
            questionPanels = parentPanel.findElements(By.cssSelector("[id^='question-panel-']"));
        }

        for (WebElement questionPanel : questionPanels) {
            if (questionPanel.getText().contains("Question " + qnNum)) {
                return questionPanel;
            }
        }
        throw new RuntimeException("Question " + qnNum + " not found.");
    }

    private WebElement getSectionPanel(String sectionName) {
        List<WebElement> sectionPanels = browser.driver.findElements(By.id("section-panel"));
        for (WebElement sectionPanel : sectionPanels) {
            if (sectionPanel.getText().startsWith(sectionName)) {
                return sectionPanel;
            }
        }
        throw new RuntimeException("Section \"" + sectionName + "\" not found.");
    }

    private WebElement getTeamPanel(WebElement sectionPanel, String teamName) {
        List<WebElement> teamPanels = sectionPanel.findElements(By.id("team-panel"));
        for (WebElement teamPanel : teamPanels) {
            if (teamPanel.getText().startsWith(teamName)) {
                return teamPanel;
            }
        }
        throw new RuntimeException("Team \"" + teamName + "\" not found");
    }

    private WebElement getUserPanel(WebElement parentPanel, String header) {
        List<WebElement> userPanels = parentPanel.findElements(By.id("user-panel"));
        for (WebElement userPanel : userPanels) {
            if (userPanel.getText().startsWith(header)) {
                return userPanel;
            }
        }
        throw new RuntimeException("User \"" + header + "\" not found.");
    }

    private WebElement getUserPanel(FeedbackParticipantType type, String user,
                                    Collection<InstructorAttributes> instructors,
                                    Collection<StudentAttributes> students,
                                    boolean isGroupedByTeam,
                                    boolean isGiver) {
        String section = getSection(type, user, students);
        String team = getTeam(type, user, students);
        String name = getName(type, user, instructors, students);
        String userPanelHeader = getUserHeader(isGiver, name);

        return getUserPanel(section, team, userPanelHeader, isGroupedByTeam);
    }

    private WebElement getUserPanel(String section, String team, String userPanelHeader, boolean isGroupedByTeam) {
        WebElement parentPanel = getUserParentPanel(section, team, isGroupedByTeam);
        return getUserPanel(parentPanel, userPanelHeader);
    }

    private WebElement getUserParentPanel(String section, String team, boolean isGroupedByTeam) {
        WebElement sectionPanel = getSectionPanel(section);
        WebElement parentPanel = sectionPanel;
        if (isGroupedByTeam) {
            parentPanel = getTeamPanel(sectionPanel, team);
        }
        return parentPanel;
    }

    private void expandQuestionPanel(WebElement questionPanel) {
        if (!isQuestionPanelExpanded(questionPanel)) {
            click(questionPanel.findElement(By.className("card-header")));
            waitUntilAnimationFinish();
        }
    }

    private void hideQuestionPanel(WebElement questionPanel) {
        if (isQuestionPanelExpanded(questionPanel)) {
            click(questionPanel.findElement(By.className("card-header")));
            waitUntilAnimationFinish();
        }
    }

    private boolean isQuestionPanelExpanded(WebElement questionPanel) {
        return questionPanel.findElements(By.id("response-table")).size()
                + questionPanel.findElements(By.id("no-responses")).size() > 0;
    }

    private String getQuestionText(WebElement questionPanel) {
        return questionPanel.findElement(By.className("question-text")).getText().trim();
    }

    private WebElement getResponseTable(WebElement questionPanel) {
        return questionPanel.findElement(By.id("response-table"));
    }

    // For question view
    private WebElement getResponseRow(WebElement questionPanel, String giverTeam, String giverName, String recipientTeam,
                                      String recipientName) {
        List<WebElement> responseRows = getResponseTable(questionPanel).findElements(By.cssSelector("tbody tr"));
        for (WebElement responseRow : responseRows) {
            List<WebElement> cells = responseRow.findElements(By.tagName("td"));
            if (cells.get(0).getText().equals(giverTeam) && cells.get(1).getText().equals(giverName)
                    && cells.get(2).getText().equals(recipientTeam) && cells.get(3).getText().equals(recipientName)) {
                return responseRow;
            }
        }
        throw new RuntimeException("Response not found " + giverName);
    }

    // For other views
    private WebElement getResponseRow(WebElement questionPanel, String userTeam, String userNameAndEmail) {
        List<WebElement> responseRows = getResponseTable(questionPanel).findElements(By.cssSelector("tbody tr"));
        for (WebElement responseRow : responseRows) {
            List<WebElement> cells = responseRow.findElements(By.tagName("td"));
            if (cells.get(0).getText().equals(userTeam) && cells.get(1).getText().equals(userNameAndEmail)) {
                return responseRow;
            }
        }
        throw new RuntimeException("Response not found for " + userNameAndEmail);
    }

    private List<WebElement> getAllGroupedResponses(WebElement userPanel) {
        return userPanel.findElements(By.id("grouped-responses"));
    }

    private WebElement getGroupedResponses(WebElement userPanel, String userName, String userTeam, boolean isGrq) {
        List<WebElement> groupedResponses = getAllGroupedResponses(userPanel);
        String expectedStarting = getUserHeader(!isGrq, userName) + " (" + userTeam + ")";

        boolean hasEmptyGroupedResponses = false;
        for (WebElement groupedResponse : groupedResponses) {
            if (groupedResponse.getText().isEmpty()) {
                hasEmptyGroupedResponses = true;
                continue;
            }
            String usersDetails = groupedResponse.findElement(By.id("users-details")).getText();
            if (usersDetails.startsWith(expectedStarting)) {
                return groupedResponse;
            }
        }
        if (hasEmptyGroupedResponses) {
            return null;
        }
        throw new NoSuchElementException("Grouped responses not found for " + userName);
    }

    private WebElement getTeamStats(WebElement parentPanel, int qnNum) {
        List<WebElement> teamStats = parentPanel.findElements(By.id("team-statistics"));

        for (WebElement teamStat : teamStats) {
            if (teamStat.getText().contains("Question " + qnNum)) {
                return teamStat;
            }
        }
        throw new RuntimeException("Team statistics not found for question " + qnNum);
    }

    private String getCommentGiver(WebElement commentField) {
        String commentGiverDescription = commentField.findElement(By.id("comment-giver-name")).getText();
        return commentGiverDescription.split(" commented")[0];
    }

    private String getCommentEditor(WebElement commentField) {
        String editDescription = commentField.findElement(By.id("last-editor-name")).getText();
        return editDescription.split("edited by ")[1];
    }

    private WebElement getCommentField(WebElement commentSection, String commentString) {
        List<WebElement> commentFields = getCommentFields(commentSection);
        for (WebElement comment : commentFields) {
            if (comment.findElement(By.className("comment-text")).getText().equals(commentString)) {
                return comment;
            }
        }
        throw new RuntimeException("Comment field not found");
    }

    private List<WebElement> getCommentFields(WebElement commentSection) {
        waitForElementPresence(By.tagName("tm-comment-row"));
        return commentSection.findElements(By.tagName("tm-comment-row"));
    }

    // Methods for manipulating responses information

    private boolean isMissingResponse(FeedbackResponseAttributes response) {
        return response.getFeedbackSessionName() == null;
    }

    private List<FeedbackResponseAttributes> filterMissingResponses(List<FeedbackResponseAttributes> responses) {
        return responses.stream()
                .filter(r -> !isMissingResponse(r))
                .collect(Collectors.toList());
    }

    private List<String> getGivers(List<FeedbackResponseAttributes> responses) {
        return responses.stream().map(FeedbackResponseAttributes::getGiver).collect(Collectors.toList());
    }

    private List<String> getRecipients(List<FeedbackResponseAttributes> responses) {
        return responses.stream().map(FeedbackResponseAttributes::getRecipient).collect(Collectors.toList());
    }

    private FeedbackResponseAttributes getResponseFromGiver(List<FeedbackResponseAttributes> responses, String giver) {
        return responses.stream()
                .filter(response -> response.getGiver().equals(giver))
                .findFirst()
                .orElse(null);
    }

    private FeedbackResponseAttributes getResponseForRecipient(List<FeedbackResponseAttributes> responses,
                                                               String recipient) {
        return responses.stream()
                .filter(response -> response.getRecipient().equals(recipient))
                .findFirst()
                .orElse(null);
    }

    private String getSection(FeedbackParticipantType type, String participant, Collection<StudentAttributes> students) {
        String sectionName;
        if (type.equals(FeedbackParticipantType.TEAMS)) {
            sectionName = students.stream()
                    .filter(student -> student.getTeam().equals(participant))
                    .findFirst()
                    .map(StudentAttributes::getSection)
                    .orElse(null);
        } else if (type.equals(FeedbackParticipantType.INSTRUCTORS) || type.equals(FeedbackParticipantType.NONE)) {
            sectionName = "None";
        } else {
            sectionName = students.stream()
                    .filter(student -> student.getEmail().equals(participant))
                    .findFirst()
                    .map(StudentAttributes::getSection)
                    .orElse(null);
        }
        if (sectionName == null) {
            throw new RuntimeException("cannot find section name for " + participant);
        }
        if ("None".equals(sectionName)) {
            sectionName = NO_SECTION_LABEL;
        }
        return sectionName;
    }

    private String getTeam(FeedbackParticipantType type, String participant, Collection<StudentAttributes> students) {
        if (type.equals(FeedbackParticipantType.NONE)) {
            return NO_TEAM_LABEL;
        } else if (type.equals(FeedbackParticipantType.TEAMS)) {
            return participant;
        } else if (type.equals(FeedbackParticipantType.INSTRUCTORS)) {
            return "Instructors";
        }
        String teamName = students.stream()
                .filter(student -> student.getEmail().equals(participant))
                .findFirst()
                .map(StudentAttributes::getTeam)
                .orElse(null);

        if (teamName == null) {
            throw new RuntimeException("cannot find section name for " + participant);
        }

        return teamName;
    }

    private String getName(FeedbackParticipantType type, String participant,
                                   Collection<InstructorAttributes> instructors,
                                   Collection<StudentAttributes> students) {
        String name;
        if (type.equals(FeedbackParticipantType.NONE)) {
            name = NO_USER_LABEL;
        } else if (type.equals(FeedbackParticipantType.TEAMS)) {
            name = participant;
        } else if (type.equals(FeedbackParticipantType.INSTRUCTORS)) {
            name = instructors.stream()
                    .filter(instructor -> instructor.getEmail().equals(participant))
                    .findFirst()
                    .map(InstructorAttributes::getName)
                    .orElse(null);
        } else {
            name = students.stream()
                    .filter(student -> student.getEmail().equals(participant))
                    .findFirst()
                    .map(StudentAttributes::getName)
                    .orElse(null);
        }

        if (name == null) {
            throw new RuntimeException("Could not find name for : " + participant);
        }

        return name;
    }

    private String getNameAndEmail(FeedbackParticipantType type, String participant,
                           Collection<InstructorAttributes> instructors,
                           Collection<StudentAttributes> students) {
        String name;
        if (type.equals(FeedbackParticipantType.NONE)) {
            name = NO_USER_LABEL;
        } else if (type.equals(FeedbackParticipantType.TEAMS)) {
            name = participant;
        } else if (type.equals(FeedbackParticipantType.INSTRUCTORS)) {
            name = instructors.stream()
                    .filter(instructor -> instructor.getEmail().equals(participant))
                    .findFirst()
                    .map(instructor -> String.format("%s (%s)", instructor.getName(), instructor.getEmail()))
                    .orElse(null);
        } else {
            name = students.stream()
                    .filter(student -> student.getEmail().equals(participant))
                    .findFirst()
                    .map(student -> String.format("%s (%s)", student.getName(), student.getEmail()))
                    .orElse(null);
        }

        if (name == null) {
            throw new RuntimeException("Could not find name for : " + participant);
        }

        return name;
    }
}
