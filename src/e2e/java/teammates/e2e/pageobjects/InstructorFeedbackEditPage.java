package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.test.ThreadHelper;

/**
 * Represents the instructor feedback edit page of the website.
 */
public class InstructorFeedbackEditPage extends AppPage {
    private static final String CUSTOM_FEEDBACK_PATH_OPTION = "Custom feedback path";
    private static final String FEEDBACK_PATH_SEPARATOR = " will give feedback on ";
    private static final String CUSTOM_VISIBILITY_OPTION = "Custom visibility options";

    @FindBy(id = "btn-fs-edit")
    private WebElement fsEditButton;

    @FindBy(id = "btn-fs-save")
    private WebElement fsSaveButton;

    @FindBy(id = "btn-fs-copy")
    private WebElement fsCopyButton;

    @FindBy(id = "edit-course-id")
    private WebElement courseIdTextBox;

    @FindBy(id = "time-zone")
    private WebElement timezoneDropDown;

    @FindBy(id = "course-name")
    private WebElement courseNameTextBox;

    @FindBy(id = "edit-session-name")
    private WebElement sessionNameTextBox;

    @FindBy(id = "instructions")
    private WebElement instructionsEditor;

    @FindBy(id = "submission-start-date")
    private WebElement startDateBox;

    @FindBy(id = "submission-start-time")
    private WebElement startTimeDropdown;

    @FindBy(id = "submission-end-date")
    private WebElement endDateBox;

    @FindBy(id = "submission-end-time")
    private WebElement endTimeDropdown;

    @FindBy(id = "grace-period")
    private WebElement gracePeriodDropdown;

    @FindBy(id = "submission-status")
    private WebElement submissionStatusTextBox;

    @FindBy(id = "published-status")
    private WebElement publishStatusTextBox;

    @FindBy(id = "btn-change-visibility")
    private WebElement changeVisibilityButton;

    @FindBy(id = "session-visibility-custom")
    private WebElement customSessionVisibleTimeButton;

    @FindBy(id = "session-visibility-date")
    private WebElement sessionVisibilityDateBox;

    @FindBy(id = "session-visibility-time")
    private WebElement sessionVisibilityTimeDropdown;

    @FindBy(id = "session-visibility-at-open")
    private WebElement openSessionVisibleTimeButton;

    @FindBy(id = "response-visibility-custom")
    private WebElement customResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-date")
    private WebElement responseVisibilityDateBox;

    @FindBy(id = "response-visibility-time")
    private WebElement responseVisibilityTimeDropdown;

    @FindBy(id = "response-visibility-immediately")
    private WebElement immediateResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-manually")
    private WebElement manualResponseVisibleTimeButton;

    @FindBy(id = "btn-change-email")
    private WebElement changeEmailButton;

    @FindBy(id = "email-opening")
    private WebElement openingSessionEmailCheckbox;

    @FindBy(id = "email-closing")
    private WebElement closingSessionEmailCheckbox;

    @FindBy(id = "email-published")
    private WebElement publishedSessionEmailCheckbox;

    @FindBy(id = "btn-new-question")
    private WebElement addNewQuestionButton;

    @FindBy(id = "btn-copy-question")
    private WebElement copyQuestionButton;

    @FindBy(id = "preview-student")
    private WebElement previewAsStudentDropdown;

    @FindBy(id = "btn-preview-student")
    private WebElement previewAsStudentButton;

    @FindBy(id = "preview-instructor")
    private WebElement previewAsInstructorDropdown;

    @FindBy(id = "btn-preview-instructor")
    private WebElement previewAsInstructorButton;

    public InstructorFeedbackEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Edit Feedback Session");
    }

    public void verifySessionDetails(CourseAttributes course, FeedbackSessionAttributes feedbackSession) {
        waitForElementPresence(By.id("instructions"));
        assertEquals(getCourseId(), course.getId());
        assertEquals(getCourseName(), course.getName());
        assertEquals(getTimeZone(), feedbackSession.getTimeZone());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertEquals(getInstructions(), feedbackSession.getInstructions());
        assertEquals(getStartDate(), getDateString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
        assertEquals(getStartTime(), getTimeString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
        assertEquals(getEndDate(), getDateString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
        assertEquals(getEndTime(), getTimeString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
        assertEquals(getGracePeriod(), feedbackSession.getGracePeriodMinutes() + " min");
        verifySubmissionStatus(feedbackSession);
        verifyPublishedStatus(feedbackSession);
        verifyVisibilitySettings(feedbackSession);
        verifyEmailSettings(feedbackSession);
    }

    private void verifySubmissionStatus(FeedbackSessionAttributes feedbackSession) {
        String submissionStatus = getSubmissionStatus();
        if (feedbackSession.isClosed()) {
            assertEquals(submissionStatus, "Closed");
        } else if (feedbackSession.isVisible() && (feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
            assertEquals(submissionStatus, "Open");
        } else {
            assertEquals(submissionStatus, "Awaiting");
        }
    }

    private void verifyPublishedStatus(FeedbackSessionAttributes feedbackSession) {
        String publishedStatus = getPublishedStatus();
        if (feedbackSession.isPublished()) {
            assertEquals(publishedStatus, "Published");
        } else {
            assertEquals(publishedStatus, "Not Published");
        }
    }

    private void verifyVisibilitySettings(FeedbackSessionAttributes feedbackSession) {
        Instant sessionVisibleTime = feedbackSession.getSessionVisibleFromTime();
        Instant responseVisibleTime = feedbackSession.getResultsVisibleFromTime();

        // Default settings, assert setting section not expanded
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                && responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertTrue(isElementPresent("btn-change-visibility"));
            return;
        }
        verifySessionVisibilitySettings(sessionVisibleTime, feedbackSession);
        verifyResponseVisibilitySettings(responseVisibleTime, feedbackSession);
    }

    private void verifySessionVisibilitySettings(Instant sessionVisibleTime,
                                                 FeedbackSessionAttributes feedbackSession) {
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            assertTrue(openSessionVisibleTimeButton.isSelected());
        } else {
            assertTrue(customSessionVisibleTimeButton.isSelected());
            assertEquals(getSessionVisibilityDate(), getDateString(feedbackSession.getSessionVisibleFromTime(),
                    feedbackSession.getTimeZone()));
            assertEquals(getSessionVisibilityTime(), getTimeString(feedbackSession.getSessionVisibleFromTime(),
                    feedbackSession.getTimeZone()));
        }
    }

    private void verifyResponseVisibilitySettings(Instant responseVisibleTime,
                                                  FeedbackSessionAttributes feedbackSession) {
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            assertTrue(immediateResponseVisibleTimeButton.isSelected());
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertTrue(manualResponseVisibleTimeButton.isSelected());
        } else {
            assertTrue(customSessionVisibleTimeButton.isSelected());
            assertEquals(getResponseVisibilityDate(), getDateString(feedbackSession.getResultsVisibleFromTime(),
                    feedbackSession.getTimeZone()));
            assertEquals(getResponseVisibilityTime(), getTimeString(feedbackSession.getResultsVisibleFromTime(),
                    feedbackSession.getTimeZone()));
        }
    }

    private void verifyEmailSettings(FeedbackSessionAttributes feedbackSession) {
        boolean isOpeningEmailEnabled = feedbackSession.isOpeningEmailEnabled();
        boolean isClosingEmailEnabled = feedbackSession.isClosingEmailEnabled();
        boolean isPublishedEmailEnabled = feedbackSession.isPublishedEmailEnabled();

        // Default settings, assert setting section not expanded
        if (isOpeningEmailEnabled && isClosingEmailEnabled && isPublishedEmailEnabled) {
            assertTrue(isElementPresent("btn-change-email"));
            return;
        }
        if (isOpeningEmailEnabled) {
            assertTrue(openingSessionEmailCheckbox.isSelected());
        }
        if (isClosingEmailEnabled) {
            assertTrue(closingSessionEmailCheckbox.isSelected());
        }
        if (isPublishedEmailEnabled) {
            assertTrue(publishedSessionEmailCheckbox.isSelected());
        }
    }

    public void editSessionDetails(FeedbackSessionAttributes newFeedbackSessionDetails) {
        click(fsEditButton);
        setInstructions(newFeedbackSessionDetails.getInstructions());
        setSessionStartDateTime(newFeedbackSessionDetails.getStartTime(), newFeedbackSessionDetails.getTimeZone());
        setSessionEndDateTime(newFeedbackSessionDetails.getEndTime(), newFeedbackSessionDetails.getTimeZone());
        selectGracePeriod(newFeedbackSessionDetails.getGracePeriodMinutes());
        setVisibilitySettings(newFeedbackSessionDetails);
        setEmailSettings(newFeedbackSessionDetails);
        click(fsSaveButton);
    }

    public void copySessionToOtherCourse(CourseAttributes otherCourse, String sessionName) {
        click(fsCopyButton);
        WebElement copyFsModal = waitForElementPresence(By.id("copy-course-modal"));

        fillTextBox(copyFsModal.findElement(By.id("copy-session-name")), sessionName);
        List<WebElement> options = copyFsModal.findElements(By.className("form-check"));
        for (WebElement option : options) {
            String courseId = option.findElement(By.cssSelector("label span")).getText();
            if (courseId.equals(otherCourse.getId())) {
                click(option.findElement(By.tagName("input")));
                break;
            }
        }
        click(browser.driver.findElement(By.id("btn-confirm-copy-course")));
    }

    public void deleteSession() {
        clickAndConfirm(waitForElementPresence(By.id("btn-fs-delete")));
    }

    public FeedbackSubmitPage previewAsStudent(StudentAttributes student) {
        selectDropdownOptionByText(previewAsStudentDropdown, String.format("[%s] %s", student.getTeam(), student.getName()));
        click(previewAsStudentButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public FeedbackSubmitPage previewAsInstructor(InstructorAttributes instructor) {
        selectDropdownOptionByText(previewAsInstructorDropdown, instructor.getName());
        click(previewAsInstructorButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(getNumQuestions(), expected);
    }

    public void verifyQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        scrollElementToCenter(getQuestionForm(questionNum));
        assertEquals(feedbackQuestion.getQuestionType(), getQuestionType(questionNum));
        assertEquals(feedbackQuestion.getQuestionNumber(), getQuestionNumber(questionNum));
        assertEquals(feedbackQuestion.getQuestionDetailsCopy().getQuestionText(), getQuestionBrief(questionNum));
        assertEquals(getQuestionDescription(questionNum), feedbackQuestion.getQuestionDescription());
        verifyFeedbackPathSettings(questionNum, feedbackQuestion);
        verifyQuestionVisibilitySettings(questionNum, feedbackQuestion);
    }

    private void verifyFeedbackPathSettings(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        assertEquals(getDisplayGiverName(feedbackQuestion.getGiverType()), getFeedbackGiver(questionNum));
        String feedbackReceiver = getFeedbackReceiver(questionNum);
        assertEquals(getDisplayRecipientName(feedbackQuestion.getRecipientType()), feedbackReceiver);

        if (feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.INSTRUCTORS))
                || feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF))
                || feedbackReceiver.equals(getDisplayRecipientName(FeedbackParticipantType.TEAMS_EXCLUDING_SELF))) {
            verifyNumberOfEntitiesToGiveFeedbackTo(questionNum, feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        }
    }

    private void verifyNumberOfEntitiesToGiveFeedbackTo(int questionNum, int numberOfEntitiesToGiveFeedbackTo) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement feedbackPathPanel = questionForm.findElement(By.tagName("tm-feedback-path-panel"));
        if (numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
            assertTrue(feedbackPathPanel.findElement(By.id("unlimited-recipients")).isSelected());
        } else {
            assertTrue(feedbackPathPanel.findElement(By.id("custom-recipients")).isSelected());
            assertEquals(feedbackPathPanel.findElement(By.id("custom-recipients-number")).getAttribute("value"),
                    Integer.toString(numberOfEntitiesToGiveFeedbackTo));
        }
    }

    private void verifyQuestionVisibilitySettings(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
        String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        List<FeedbackParticipantType> showResponsesTo = feedbackQuestion.getShowResponsesTo();
        List<FeedbackParticipantType> showGiverNameTo = feedbackQuestion.getShowGiverNameTo();
        List<FeedbackParticipantType> showRecipientNameTo = feedbackQuestion.getShowRecipientNameTo();

        switch (visibility) {
        case "Shown anonymously to recipient and giver's team members, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
            assertEquals(showResponsesTo.size(), 3);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Visible to instructors only":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showResponsesTo.size(), 1);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showRecipientNameTo.size(), 1);
            break;

        case "Shown anonymously to recipient and instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertEquals(showGiverNameTo.size(), 0);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Shown anonymously to recipient, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Shown anonymously to recipient and giver/recipient's team members, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
            assertEquals(showResponsesTo.size(), 4);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Visible to recipient and instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showGiverNameTo.size(), 2);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        default:
            verifyCustomQuestionVisibility(questionNum, feedbackQuestion);
            break;
        }
    }

    private void verifyCustomQuestionVisibility(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
        String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        assertEquals(visibility, CUSTOM_VISIBILITY_OPTION);

        FeedbackParticipantType giver = feedbackQuestion.getGiverType();
        FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
        WebElement customVisibilityTable = visibilityPanel.findElement(By.id("custom-visibility-table"));
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
    }

    private void assertVisibilityBoxesSelected(WebElement table, FeedbackParticipantType giver,
                                               FeedbackParticipantType receiver, List<FeedbackParticipantType> participants,
                                               int colNum) {
        List<FeedbackParticipantType> possibleTypes = new ArrayList<>(Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
        if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        }
        if (!receiver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }
        if (receiver.equals(FeedbackParticipantType.NONE)
                || receiver.equals(FeedbackParticipantType.SELF)
                || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER);
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }

        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = colNum - 1;
        for (FeedbackParticipantType participant : participants) {
            assertTrue(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index)
                    .isSelected());
        }
    }

    public void addTemplateQuestion(int optionNum) {
        addNewQuestion(1);
        WebElement templateQuestionModal = waitForElementPresence(By.id("template-question-modal"));

        click(templateQuestionModal.findElements(By.tagName("input")).get(optionNum - 1));
        clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-template")));
    }

    public void copyQuestion(String courseId, String questionText) {
        click(copyQuestionButton);
        WebElement copyQuestionModal = waitForElementPresence(By.id("copy-question-modal"));

        List<WebElement> cards = copyQuestionModal.findElements(By.className("card"));
        for (WebElement card : cards) {
            WebElement cardHeader = card.findElement(By.className("card-header"));
            if (cardHeader.getText().startsWith("[" + courseId + "]")) {
                click(cardHeader);
                WebElement cardBody = waitForElementPresence(By.className("card-body"));
                // Reload questions
                WebElement reloadBtn = cardBody.findElement(By.tagName("button"));
                click(reloadBtn);
                WebElement table = waitForElementPresence(By.id("copy-question-table"));
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.get(2).getText().equals(questionText)) {
                        markOptionAsSelected(cells.get(0).findElement(By.tagName("input")));
                    }
                }
            }
        }
        clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-copy-question")));
    }

    public void editQuestionNumber(int questionNum, int newQuestionNumber) {
        clickEditQuestionButton(questionNum);
        selectDropdownOptionByText(getQuestionForm(questionNum).findElement(By.id("question-number-dropdown")),
                Integer.toString(newQuestionNumber));
        clickSaveQuestionButton(questionNum);
    }

    public void editQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        clickEditQuestionButton(questionNum);
        inputQuestionDetails(questionNum, feedbackQuestion);
        clickSaveQuestionButton(questionNum);
    }

    private void inputQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        setQuestionBrief(questionNum, feedbackQuestion.getQuestionDetailsCopy().getQuestionText());
        setQuestionDescription(questionNum, feedbackQuestion.getQuestionDescription());
        FeedbackQuestionType questionType = feedbackQuestion.getQuestionType();
        if (!questionType.equals(FeedbackQuestionType.CONTRIB)) {
            setFeedbackPath(questionNum, feedbackQuestion);
            setQuestionVisibility(questionNum, feedbackQuestion);
        }
    }

    public void duplicateQuestion(int questionNum) {
        clickAndWaitForNewQuestion(getQuestionForm(questionNum).findElement(By.id("btn-duplicate-question")));
    }

    public void deleteQuestion(int questionNum) {
        clickAndConfirm(getQuestionForm(questionNum).findElement(By.id("btn-delete-question")));
    }

    public void verifyTextQuestionDetails(int questionNum, FeedbackTextQuestionDetails questionDetails) {
        String recommendLength = getRecommendedTextLengthField(questionNum).getAttribute("value");
        assertEquals(recommendLength, questionDetails.getRecommendedLength().toString());
    }

    public void addTextQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(2);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackTextQuestionDetails questionDetails =
                (FeedbackTextQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        fillTextBox(getRecommendedTextLengthField(questionNum), questionDetails.getRecommendedLength().toString());
        clickSaveNewQuestionButton();
    }

    public void editTextQuestion(int questionNum, FeedbackTextQuestionDetails textQuestionDetails) {
        clickEditQuestionButton(questionNum);
        WebElement recommendedTextLengthField = getRecommendedTextLengthField(questionNum);
        waitForElementToBeClickable(recommendedTextLengthField);
        fillTextBox(recommendedTextLengthField, textQuestionDetails.getRecommendedLength().toString());
        clickSaveQuestionButton(questionNum);
    }

    public void verifyMcqQuestionDetails(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
        if (verifyGeneratedOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
            return;
        }
        verifyOptions(questionNum, questionDetails.getMcqChoices());
        verifyOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMcqWeights());
        verifyOtherOption(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMcqOtherWeight());
    }

    public void addMcqQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(3);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputMcqDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editMcqQuestion(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
        clickEditQuestionButton(questionNum);
        inputMcqDetails(questionNum, questionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyMsqQuestionDetails(int questionNum, FeedbackMsqQuestionDetails questionDetails) {
        verifyMaxOptions(questionNum, questionDetails.getMaxSelectableChoices());
        verifyMinOptions(questionNum, questionDetails.getMinSelectableChoices());
        if (verifyGeneratedOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
            return;
        }
        verifyOptions(questionNum, questionDetails.getMsqChoices());
        verifyOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMsqWeights());
        verifyOtherOption(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMsqOtherWeight());
    }

    public void addMsqQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(4);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputMsqDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editMsqQuestion(int questionNum, FeedbackMsqQuestionDetails msqQuestionDetails) {
        clickEditQuestionButton(questionNum);
        inputMsqDetails(questionNum, msqQuestionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyNumScaleQuestionDetails(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
        assertEquals(getMinNumscaleInput(questionNum).getAttribute("value"),
                Integer.toString(questionDetails.getMinScale()));
        assertEquals(getNumScaleIncrementInput(questionNum).getAttribute("value"),
                getDoubleString(questionDetails.getStep()));
        assertEquals(getMaxNumscaleInput(questionNum).getAttribute("value"),
                Integer.toString(questionDetails.getMaxScale()));
    }

    public void addNumScaleQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(5);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackNumericalScaleQuestionDetails questionDetails =
                (FeedbackNumericalScaleQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputNumScaleDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editNumScaleQuestion(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
        clickEditQuestionButton(questionNum);
        inputNumScaleDetails(questionNum, questionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyConstSumQuestionDetails(int questionNum, FeedbackConstantSumQuestionDetails questionDetails) {
        if (!questionDetails.isDistributeToRecipients()) {
            verifyOptions(questionNum, questionDetails.getConstSumOptions());
        }

        if (questionDetails.isPointsPerOption()) {
            assertTrue(getConstSumPerOptionPointsRadioBtn(questionNum).isSelected());
            assertEquals(getConstSumPerOptionPointsInput(questionNum).getAttribute("value"),
                    Integer.toString(questionDetails.getPoints()));
            assertFalse(getConstSumTotalPointsRadioBtn(questionNum).isSelected());
        } else {
            assertTrue(getConstSumTotalPointsRadioBtn(questionNum).isSelected());
            assertEquals(getConstSumTotalPointsInput(questionNum).getAttribute("value"),
                    Integer.toString(questionDetails.getPoints()));
            assertFalse(getConstSumPerOptionPointsRadioBtn(questionNum).isSelected());
        }

        if (questionDetails.isForceUnevenDistribution()) {
            String distributeFor = questionDetails.getDistributePointsFor();
            assertTrue(getConstSumUnevenDistributionCheckbox(questionNum).isSelected());
            assertEquals(getSelectedDropdownOptionText(getConstSumUnevenDistributionDropdown(questionNum)).trim(),
                    "All options".equals(distributeFor) ? "Every option" : distributeFor);
        } else {
            assertFalse(getConstSumUnevenDistributionCheckbox(questionNum).isSelected());
        }
    }

    public void addConstSumOptionQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(6);
        addConstSumQuestion(feedbackQuestion);
    }

    public void addConstSumRecipientQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(7);
        addConstSumQuestion(feedbackQuestion);
    }

    public void addConstSumQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackConstantSumQuestionDetails questionDetails =
                (FeedbackConstantSumQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputConstSumDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editConstSumQuestion(int questionNum, FeedbackConstantSumQuestionDetails csQuestionDetails) {
        clickEditQuestionButton(questionNum);
        inputConstSumDetails(questionNum, csQuestionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyContributionQuestionDetails(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
        assertEquals(questionDetails.isZeroSum(), getZeroSumCheckbox(questionNum).isSelected());
        assertEquals(questionDetails.isNotSureAllowed(), getAllowNotSureContributionCheckbox(questionNum).isSelected());
        if (questionDetails.isZeroSum()) {
            assertFalse(questionDetails.isNotSureAllowed());
        }
    }

    public void addContributionQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(8);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackContributionQuestionDetails questionDetails =
                (FeedbackContributionQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputContributionDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editContributionQuestion(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
        clickEditQuestionButton(questionNum);
        inputContributionDetails(questionNum, questionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyRubricQuestionDetails(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
        int numChoices = questionDetails.getNumOfRubricChoices();
        List<String> choices = questionDetails.getRubricChoices();
        for (int i = 0; i < numChoices; i++) {
            assertEquals(choices.get(i), getRubricChoiceInputs(questionNum).get(i).getAttribute("value"));
        }

        int numSubQn = questionDetails.getNumOfRubricSubQuestions();
        List<String> subQuestions = questionDetails.getRubricSubQuestions();
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();
        for (int i = 0; i < numSubQn; i++) {
            List<WebElement> textAreas = getRubricTextareas(questionNum, i + 2);
            assertEquals(subQuestions.get(i), textAreas.get(0).getAttribute("value"));
            for (int j = 0; j < numChoices; j++) {
                assertEquals(descriptions.get(i).get(j), textAreas.get(j + 1).getAttribute("value"));
            }
        }

        if (questionDetails.isHasAssignedWeights()) {
            assertTrue(getWeightCheckbox(questionNum).isSelected());
            List<List<Double>> weights = questionDetails.getRubricWeights();
            for (int i = 0; i < numSubQn; i++) {
                List<WebElement> rubricWeights = getRubricWeights(questionNum, i + 2);
                for (int j = 0; j < numChoices; j++) {
                    assertEquals(rubricWeights.get(j).getAttribute("value"),
                            getDoubleString(weights.get(i).get(j)));
                }
            }
        } else {
            assertFalse(getWeightCheckbox(questionNum).isSelected());
        }
    }

    public void addRubricQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(9);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackRubricQuestionDetails questionDetails =
                (FeedbackRubricQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputRubricDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editRubricQuestion(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
        clickEditQuestionButton(questionNum);
        inputRubricDetails(questionNum, questionDetails);
        clickSaveQuestionButton(questionNum);
    }

    public void verifyRankQuestionDetails(int questionNum, FeedbackRankQuestionDetails questionDetails) {
        if (questionDetails instanceof FeedbackRankOptionsQuestionDetails) {
            FeedbackRankOptionsQuestionDetails optionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
            verifyOptions(questionNum, optionDetails.getOptions());
        }
        assertEquals(getAllowDuplicateRankCheckbox(questionNum).isSelected(), questionDetails.isAreDuplicatesAllowed());
        verifyMaxOptions(questionNum, questionDetails.getMaxOptionsToBeRanked());
        verifyMinOptions(questionNum, questionDetails.getMinOptionsToBeRanked());
    }

    public void addRankOptionsQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(10);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackRankOptionsQuestionDetails questionDetails =
                (FeedbackRankOptionsQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputRankDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void addRankRecipientsQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(11);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackRankQuestionDetails questionDetails =
                (FeedbackRankQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
        inputRankDetails(questionNum, questionDetails);
        clickSaveNewQuestionButton();
    }

    public void editRankQuestion(int questionNum, FeedbackRankQuestionDetails questionDetails) {
        clickEditQuestionButton(questionNum);
        inputRankDetails(questionNum, questionDetails);
        clickSaveQuestionButton(questionNum);
    }

    private String getCourseId() {
        return courseIdTextBox.getText();
    }

    private String getCourseName() {
        return courseNameTextBox.getText();
    }

    private String getTimeZone() {
        return timezoneDropDown.getText();
    }

    private String getFeedbackSessionName() {
        return sessionNameTextBox.getText();
    }

    private String getInstructions() {
        return getEditorRichText(instructionsEditor.findElement(By.tagName("editor")));
    }

    private String getStartDate() {
        return startDateBox.findElement(By.tagName("input")).getAttribute("value");
    }

    private String getStartTime() {
        return getSelectedDropdownOptionText(startTimeDropdown.findElement(By.tagName("select")));
    }

    private String getEndDate() {
        return endDateBox.findElement(By.tagName("input")).getAttribute("value");
    }

    private String getEndTime() {
        return getSelectedDropdownOptionText(endTimeDropdown.findElement(By.tagName("select")));
    }

    private String getSessionVisibilityDate() {
        return sessionVisibilityDateBox.findElement(By.tagName("input")).getAttribute("value");
    }

    private String getSessionVisibilityTime() {
        return getSelectedDropdownOptionText(sessionVisibilityTimeDropdown.findElement(By.tagName("select")));
    }

    private String getResponseVisibilityDate() {
        return responseVisibilityDateBox.findElement(By.tagName("input"))
                .getAttribute("value");
    }

    private String getResponseVisibilityTime() {
        return getSelectedDropdownOptionText(responseVisibilityTimeDropdown.findElement(By.tagName("select")));
    }

    private String getGracePeriod() {
        return getSelectedDropdownOptionText(gracePeriodDropdown);
    }

    private String getSubmissionStatus() {
        return submissionStatusTextBox.getText();
    }

    private String getPublishedStatus() {
        return publishStatusTextBox.getText();
    }

    private String getDateString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy");
    }

    private String getTimeString(Instant instant, String timeZone) {
        ZonedDateTime dateTime = instant.atZone(ZoneId.of(timeZone));
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return "23:59H";
        }
        return getDisplayedDateTime(instant, timeZone, "HH:00") + "H";
    }

    private void setInstructions(String newInstructions) {
        writeToRichTextEditor(instructionsEditor.findElement(By.tagName("editor")), newInstructions);
    }

    private void setSessionStartDateTime(Instant startInstant, String timeZone) {
        setDateTime(startDateBox, startTimeDropdown, startInstant, timeZone);
    }

    private void setSessionEndDateTime(Instant endInstant, String timeZone) {
        setDateTime(endDateBox, endTimeDropdown, endInstant, timeZone);
    }

    private void setVisibilityDateTime(Instant startInstant, String timeZone) {
        setDateTime(sessionVisibilityDateBox, sessionVisibilityTimeDropdown, startInstant, timeZone);
    }

    private void setResponseDateTime(Instant endInstant, String timeZone) {
        setDateTime(responseVisibilityDateBox, responseVisibilityTimeDropdown, endInstant, timeZone);
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant, String timeZone) {
        fillDatePicker(dateBox, startInstant, timeZone);

        selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getTimeString(startInstant, timeZone));
    }

    private void selectGracePeriod(long gracePeriodMinutes) {
        selectDropdownOptionByText(gracePeriodDropdown, gracePeriodMinutes + " min");
    }

    private void setVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        showVisibilitySettings();

        setSessionVisibilitySettings(newFeedbackSession);
        setResponseVisibilitySettings(newFeedbackSession);
    }

    private void setSessionVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant sessionDateTime = newFeedbackSession.getSessionVisibleFromTime();
        if (sessionDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            click(openSessionVisibleTimeButton);
        } else {
            click(customSessionVisibleTimeButton);
            setVisibilityDateTime(sessionDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setResponseVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant responseDateTime = newFeedbackSession.getResultsVisibleFromTime();
        if (responseDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            click(immediateResponseVisibleTimeButton);
        } else if (responseDateTime.equals(Const.TIME_REPRESENTS_LATER)) {
            click(manualResponseVisibleTimeButton);
        } else {
            click(customResponseVisibleTimeButton);
            setResponseDateTime(responseDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setEmailSettings(FeedbackSessionAttributes newFeedbackSessionDetails) {
        showEmailSettings();
        if (newFeedbackSessionDetails.isOpeningEmailEnabled() != openingSessionEmailCheckbox.isSelected()) {
            click(openingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isClosingEmailEnabled() != closingSessionEmailCheckbox.isSelected()) {
            click(closingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isPublishedEmailEnabled() != publishedSessionEmailCheckbox.isSelected()) {
            click(publishedSessionEmailCheckbox);
        }
    }

    private void showVisibilitySettings() {
        if (isElementPresent(By.id("btn-change-visibility"))) {
            click(changeVisibilityButton);
        }
    }

    private void showEmailSettings() {
        if (isElementPresent(By.id("btn-change-email"))) {
            click(changeEmailButton);
        }
    }

    private int getNumQuestions() {
        return browser.driver.findElements(By.tagName("tm-question-edit-form")).size();
    }

    private WebElement getQuestionForm(int questionNum) {
        return browser.driver.findElements(By.tagName("tm-question-edit-form")).get(questionNum - 1);
    }

    private FeedbackQuestionType getQuestionType(int questionNum) {
        String questionType = getQuestionForm(questionNum).findElement(By.id("question-type")).getText().trim();

        switch (questionType) {
        case "Essay question":
            return FeedbackQuestionType.TEXT;
        case "Multiple-Choice (single answer) question":
            return FeedbackQuestionType.MCQ;
        case "Multiple-choice (multiple answers) question":
            return FeedbackQuestionType.MSQ;
        case "Numerical Scale Question":
            return FeedbackQuestionType.NUMSCALE;
        case "Distribute points (among options) question":
            return FeedbackQuestionType.CONSTSUM_OPTIONS;
        case "Distribute points (among recipients) question":
            return FeedbackQuestionType.CONSTSUM_RECIPIENTS;
        case "Team contribution question":
            return FeedbackQuestionType.CONTRIB;
        case "Rubric question":
            return FeedbackQuestionType.RUBRIC;
        case "Rank (options) question":
            return FeedbackQuestionType.RANK_OPTIONS;
        case "Rank (recipients) question":
            return FeedbackQuestionType.RANK_RECIPIENTS;
        default:
            throw new IllegalArgumentException("Unknown FeedbackQuestionType");
        }
    }

    private int getQuestionNumber(int questionNum) {
        return Integer.parseInt(getQuestionForm(questionNum).findElement(By.id("question-number")).getText());
    }

    private String getQuestionBrief(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("question-brief")).getAttribute("value");
    }

    private String getQuestionDescription(int questionNum) {
        WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
        return getEditorRichText(editor);
    }

    private String getFeedbackGiver(int questionNum) {
        String feedbackPath = getFeedbackPath(questionNum);
        if (CUSTOM_FEEDBACK_PATH_OPTION.equals(feedbackPath)) {
            return getSelectedDropdownOptionText(getQuestionForm(questionNum)
                    .findElement(By.tagName("tm-feedback-path-panel"))
                    .findElement(By.id("giver-type")));
        }
        return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[0];
    }

    private String getFeedbackReceiver(int questionNum) {
        String feedbackPath = getFeedbackPath(questionNum);
        if (CUSTOM_FEEDBACK_PATH_OPTION.equals(feedbackPath)) {
            return getSelectedDropdownOptionText(getQuestionForm(questionNum)
                    .findElement(By.tagName("tm-feedback-path-panel"))
                    .findElement(By.id("receiver-type")));
        }
        return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[1];
    }

    private String getFeedbackPath(int questionNum) {
        WebElement feedbackPathPanel = getQuestionForm(questionNum).findElement(By.tagName("tm-feedback-path-panel"));
        return feedbackPathPanel.findElement(By.cssSelector("#btn-feedback-path span")).getText();
    }

    private void setQuestionBrief(int questionNum, String newBrief) {
        fillTextBox(getQuestionForm(questionNum).findElement(By.id("question-brief")), newBrief);
    }

    private void setQuestionDescription(int questionNum, String newDescription) {
        WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
        writeToRichTextEditor(editor, newDescription);
    }

    private void setFeedbackPath(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        FeedbackParticipantType newGiver = feedbackQuestion.getGiverType();
        FeedbackParticipantType newRecipient = feedbackQuestion.getRecipientType();
        String feedbackPath = getFeedbackPath(questionNum);
        WebElement questionForm = getQuestionForm(questionNum).findElement(By.tagName("tm-feedback-path-panel"));
        if (!CUSTOM_FEEDBACK_PATH_OPTION.equals(feedbackPath)) {
            selectFeedbackPathDropdownOption(questionNum, CUSTOM_FEEDBACK_PATH_OPTION + "...");
        }
        // Set to type STUDENT first to adjust NumberOfEntitiesToGiveFeedbackTo
        selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")),
                getDisplayGiverName(FeedbackParticipantType.STUDENTS));
        selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
                getDisplayRecipientName(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF));
        if (feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
            click(questionForm.findElement(By.id("unlimited-recipients")));
        } else {
            click(questionForm.findElement(By.id("custom-recipients")));
            fillTextBox(questionForm.findElement(By.id("custom-recipients-number")),
                    Integer.toString(feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo()));
        }

        selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")), getDisplayGiverName(newGiver));
        selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
                getDisplayRecipientName(newRecipient));
    }

    private void selectFeedbackPathDropdownOption(int questionNum, String text) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement feedbackPathPanel = questionForm.findElement(By.tagName("tm-feedback-path-panel"));
        click(feedbackPathPanel.findElement(By.id("btn-feedback-path")));
        WebElement dropdown = feedbackPathPanel.findElement(By.id("feedback-path-dropdown"));
        List<WebElement> options = dropdown.findElements(By.className("dropdown-button"));
        for (WebElement option : options) {
            if (option.getText().equals(text)) {
                click(option);
                return;
            }
        }
    }

    private void clickEditQuestionButton(int questionNum) {
        click(getQuestionForm(questionNum).findElement(By.id("btn-edit-question")));
    }

    private void clickSaveQuestionButton(int questionNum) {
        WebElement saveButton = getQuestionForm(questionNum).findElement(By.id("btn-save-question"));
        click(saveButton);
        ThreadHelper.waitFor(1000);
    }

    private void setQuestionVisibility(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
        String visibility = visibilityPanel.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        if (!CUSTOM_VISIBILITY_OPTION.equals(visibility)) {
            selectVisibilityDropdownOption(questionNum, CUSTOM_VISIBILITY_OPTION + "...");
        }

        FeedbackParticipantType giver = feedbackQuestion.getGiverType();
        FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
        WebElement customVisibilityTable = visibilityPanel.findElement(By.id("custom-visibility-table"));
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
    }

    private void selectVisibilityBoxes(WebElement table, FeedbackParticipantType giver,
                                       FeedbackParticipantType receiver, List<FeedbackParticipantType> participants,
                                       int colNum) {
        List<FeedbackParticipantType> possibleTypes = new ArrayList<>(Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
        if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        }
        if (!receiver.equals(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }
        if (receiver.equals(FeedbackParticipantType.NONE)
                || receiver.equals(FeedbackParticipantType.SELF)
                || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER);
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }

        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = colNum - 1;
        for (FeedbackParticipantType participant : participants) {
            markOptionAsSelected(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index));
        }
    }

    private void selectVisibilityDropdownOption(int questionNum, String text) {
        WebElement questionForm = getQuestionForm(questionNum);
        WebElement visibilityPanel = questionForm.findElement(By.tagName("tm-visibility-panel"));
        click(visibilityPanel.findElement(By.id("btn-question-visibility")));
        WebElement dropdown = visibilityPanel.findElement(By.id("question-visibility-dropdown"));
        List<WebElement> options = dropdown.findElements(By.className("dropdown-button"));
        for (WebElement option : options) {
            if (option.getText().equals(text)) {
                click(option);
                return;
            }
        }
    }

    private void clickAndWaitForNewQuestion(WebElement button) {
        int newQuestionNum = getNumQuestions() + 1;
        click(button);
        waitForElementPresence(By.id("question-form-" + newQuestionNum));
    }

    private void addNewQuestion(int optionNumber) {
        click(addNewQuestionButton);
        WebElement newQuestionDropdown = waitForElementPresence(By.id("new-question-dropdown"));
        WebElement optionButton = newQuestionDropdown.findElements(By.tagName("button")).get(optionNumber - 1);
        if (optionNumber == 1) {
            click(optionButton);
        } else {
            clickAndWaitForNewQuestion(optionButton);
        }
    }

    private void clickSaveNewQuestionButton() {
        WebElement saveButton = browser.driver.findElement(By.id("btn-save-new"));
        click(saveButton);
        waitForElementStaleness(saveButton);
    }

    private WebElement getRecommendedTextLengthField(int questionNum) {
        return getQuestionForm(questionNum)
                .findElement(By.tagName("tm-text-question-edit-details-form"))
                .findElement(By.id("recommended-length"));
    }

    private WebElement getGenerateOptionsCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("generate-checkbox"));
    }

    private WebElement getGenerateOptionsDropdown(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("generate-dropdown"));
    }

    private WebElement getWeightCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("weights-checkbox"));
    }

    private WebElement getOtherOptionCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("other-checkbox"));
    }

    private WebElement getQuestionDropdownCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("make-question-dropdown"));
    }

    private String getGeneratedOptionString(FeedbackParticipantType type) {
        switch (type) {
        case STUDENTS:
        case STUDENTS_IN_SAME_SECTION:
            return "students";
        case STUDENTS_EXCLUDING_SELF:
            return "students (excluding self)";
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
            return "teams";
        case TEAMS_EXCLUDING_SELF:
            return "teams (excluding own team)";
        case INSTRUCTORS:
            return "instructors";
        default:
            return "unknown";
        }
    }

    private String getDoubleString(Double value) {
        return value % 1 == 0 ? Integer.toString(value.intValue()) : Double.toString(value);
    }

    private WebElement getOptionsSection(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("options-section"));
    }

    private List<WebElement> getOptionInputs(int questionNum) {
        WebElement optionsSection = getOptionsSection(questionNum);
        return optionsSection.findElements(By.cssSelector("input[type='text']"));
    }

    private List<WebElement> getOptionWeightInputs(int questionNum) {
        WebElement optionsSection = getOptionsSection(questionNum);
        return optionsSection.findElements(By.cssSelector("tm-weight-field input"));
    }

    private WebElement getOtherWeightInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("other-weight"));
    }

    private boolean verifyGeneratedOptions(int questionNum, FeedbackParticipantType participantType) {
        if (!participantType.equals(FeedbackParticipantType.NONE)) {
            assertTrue(getGenerateOptionsCheckbox(questionNum).isSelected());
            assertEquals(getSelectedDropdownOptionText(getGenerateOptionsDropdown(questionNum)),
                    getGeneratedOptionString(participantType));
            return true;
        }
        assertFalse(getGenerateOptionsCheckbox(questionNum).isSelected());
        return false;
    }

    private void verifyOptions(int questionNum, List<String> options) {
        List<WebElement> inputs = getOptionInputs(questionNum);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), inputs.get(i).getAttribute("value"));
        }
    }

    private void verifyOptionWeights(int questionNum, boolean hasWeights, List<Double> weights) {
        if (hasWeights) {
            assertTrue(getWeightCheckbox(questionNum).isSelected());
            List<WebElement> weightInputs = getOptionWeightInputs(questionNum);
            for (int i = 0; i < weights.size(); i++) {
                assertEquals(getDoubleString(weights.get(i)), weightInputs.get(i).getAttribute("value"));
            }
        } else {
            assertFalse(getWeightCheckbox(questionNum).isSelected());
        }
    }

    private void verifyOtherOption(int questionNum, boolean hasOther, Double weight) {
        if (hasOther) {
            assertTrue(getOtherOptionCheckbox(questionNum).isSelected());
            if (weight > 0) {
                String otherWeight = getOtherWeightInput(questionNum).getAttribute("value");
                assertEquals(getDoubleString(weight), otherWeight);
            }
        } else {
            assertFalse(getOtherOptionCheckbox(questionNum).isSelected());
        }
    }

    private void inputMcqDetails(int questionNum, FeedbackMcqQuestionDetails questionDetails) {
        if (inputGenerateOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
            return;
        }

        inputOptions(questionNum, questionDetails.getMcqChoices());
        inputOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMcqWeights());
        inputOtherChoice(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMcqOtherWeight());
        inputDropdownEnabledChoice(questionNum, questionDetails.isQuestionDropdownEnabled());
    }

    private boolean inputGenerateOptions(int questionNum, FeedbackParticipantType participantType) {
        if (!participantType.equals(FeedbackParticipantType.NONE)) {
            markOptionAsSelected(getGenerateOptionsCheckbox(questionNum));
            selectDropdownOptionByText(getGenerateOptionsDropdown(questionNum),
                    getGeneratedOptionString(participantType));
            clickSaveQuestionButton(questionNum);
            return true;
        }
        markOptionAsUnselected(getGenerateOptionsCheckbox(questionNum));
        return false;
    }

    private void inputOptions(int questionNum, List<String> options) {
        List<WebElement> inputs = getOptionInputs(questionNum);
        int numInputsNeeded = options.size() - inputs.size();
        if (numInputsNeeded > 0) {
            for (int i = 0; i < numInputsNeeded; i++) {
                click(getQuestionForm(questionNum).findElement(By.id("btn-add-option")));
            }
            inputs = getOptionInputs(questionNum);
        }
        if (numInputsNeeded < 0) {
            for (int i = 0; i < -numInputsNeeded; i++) {
                click(getOptionsSection(questionNum).findElement(By.tagName("button")));
            }
            inputs = getOptionInputs(questionNum);
        }

        for (int i = 0; i < options.size(); i++) {
            fillTextBox(inputs.get(i), options.get(i));
        }
    }

    private void inputOptionWeights(int questionNum, boolean hasWeights, List<Double> weights) {
        if (hasWeights) {
            markOptionAsSelected(getWeightCheckbox(questionNum));
            List<WebElement> weightInputs = getOptionWeightInputs(questionNum);
            for (int i = 0; i < weights.size(); i++) {
                fillTextBox(weightInputs.get(i), getDoubleString(weights.get(i)));
            }
        } else {
            markOptionAsUnselected(getWeightCheckbox(questionNum));
        }
    }

    private void inputOtherChoice(int questionNum, boolean hasOther, Double otherWeight) {
        if (hasOther) {
            markOptionAsSelected(getOtherOptionCheckbox(questionNum));
            if (otherWeight > 0) {
                fillTextBox(getOtherWeightInput(questionNum), getDoubleString(otherWeight));
            }
        } else {
            markOptionAsUnselected(getOtherOptionCheckbox(questionNum));
        }
    }

    private void inputDropdownEnabledChoice(int questionNum, boolean hasOther) {
        if (hasOther) {
            markOptionAsSelected(getQuestionDropdownCheckbox(questionNum));
        } else {
            markOptionAsUnselected(getQuestionDropdownCheckbox(questionNum));
        }
    }

    private WebElement getMaxOptionsCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("max-options-checkbox"));
    }

    private WebElement getMaxOptionsInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("max-options"));
    }

    private WebElement getMinOptionsCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("min-options-checkbox"));
    }

    private WebElement getMinOptionsInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("min-options"));
    }

    private void verifyMaxOptions(int questionNum, int maxOptions) {
        if (maxOptions == Const.POINTS_NO_VALUE) {
            assertFalse(getMaxOptionsCheckbox(questionNum).isSelected());
        } else {
            assertTrue(getMaxOptionsCheckbox(questionNum).isSelected());
            assertEquals(getMaxOptionsInput(questionNum).getAttribute("value"),
                    Integer.toString(maxOptions));
        }
    }

    private void verifyMinOptions(int questionNum, int minOptions) {
        if (minOptions == Const.POINTS_NO_VALUE) {
            assertFalse(getMinOptionsCheckbox(questionNum).isSelected());
        } else {
            assertTrue(getMinOptionsCheckbox(questionNum).isSelected());
            assertEquals(getMinOptionsInput(questionNum).getAttribute("value"),
                    Integer.toString(minOptions));
        }
    }

    private void inputMsqDetails(int questionNum, FeedbackMsqQuestionDetails questionDetails) {
        if (inputGenerateOptions(questionNum, questionDetails.getGenerateOptionsFor())) {
            return;
        }

        inputOptions(questionNum, questionDetails.getMsqChoices());
        inputOptionWeights(questionNum, questionDetails.isHasAssignedWeights(), questionDetails.getMsqWeights());
        inputOtherChoice(questionNum, questionDetails.isOtherEnabled(), questionDetails.getMsqOtherWeight());
        inputMaxOptions(questionNum, questionDetails.getMaxSelectableChoices());
        inputMinOptions(questionNum, questionDetails.getMinSelectableChoices());
    }

    private void inputMaxOptions(int questionNum, int maxOptions) {
        if (maxOptions == Const.POINTS_NO_VALUE) {
            markOptionAsUnselected(getMaxOptionsCheckbox(questionNum));
        } else {
            markOptionAsSelected(getMaxOptionsCheckbox(questionNum));
            fillTextBox(getMaxOptionsInput(questionNum), Integer.toString(maxOptions));
        }
    }

    private void inputMinOptions(int questionNum, int minOptions) {
        if (minOptions == Const.POINTS_NO_VALUE) {
            markOptionAsUnselected(getMinOptionsCheckbox(questionNum));
        } else {
            markOptionAsSelected(getMinOptionsCheckbox(questionNum));
            fillTextBox(getMinOptionsInput(questionNum), Integer.toString(minOptions));
        }
    }

    private WebElement getMinNumscaleInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("min-value"));
    }

    private WebElement getMaxNumscaleInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("max-value"));
    }

    private WebElement getNumScaleIncrementInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("increment-value"));
    }

    private void inputNumScaleDetails(int questionNum, FeedbackNumericalScaleQuestionDetails questionDetails) {
        inputNumScaleValue(getMinNumscaleInput(questionNum), Integer.toString(questionDetails.getMinScale()));
        inputNumScaleValue(getNumScaleIncrementInput(questionNum), getDoubleString(questionDetails.getStep()));
        inputNumScaleValue(getMaxNumscaleInput(questionNum), Integer.toString(questionDetails.getMaxScale()));
    }

    private void inputNumScaleValue(WebElement input, String value) {
        input.clear();
        input.sendKeys(value);
    }

    private WebElement getConstSumTotalPointsRadioBtn(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("total-points-radio"));
    }

    private WebElement getConstSumTotalPointsInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("total-points"));
    }

    private WebElement getConstSumPerOptionPointsRadioBtn(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("per-option-points-radio"));
    }

    private WebElement getConstSumPerOptionPointsInput(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("per-option-points"));
    }

    private WebElement getConstSumUnevenDistributionCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("uneven-distribution-checkbox"));
    }

    private WebElement getConstSumUnevenDistributionDropdown(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("uneven-distribution-dropdown"));
    }

    private void inputConstSumDetails(int questionNum, FeedbackConstantSumQuestionDetails questionDetails) {
        if (!questionDetails.isDistributeToRecipients()) {
            inputOptions(questionNum, questionDetails.getConstSumOptions());
        }
        if (questionDetails.isPointsPerOption()) {
            click(getConstSumPerOptionPointsRadioBtn(questionNum));
            fillTextBox(getConstSumPerOptionPointsInput(questionNum), Integer.toString(questionDetails.getPoints()));
        } else {
            click(getConstSumTotalPointsRadioBtn(questionNum));
            fillTextBox(getConstSumTotalPointsInput(questionNum), Integer.toString(questionDetails.getPoints()));
        }
        String distributeFor = questionDetails.getDistributePointsFor();
        if (questionDetails.isForceUnevenDistribution()) {
            markOptionAsSelected(getConstSumUnevenDistributionCheckbox(questionNum));
            selectDropdownOptionByText(getConstSumUnevenDistributionDropdown(questionNum),
                    "All options".equals(distributeFor) ? "Every option" : distributeFor);
        } else {
            markOptionAsUnselected(getConstSumUnevenDistributionCheckbox(questionNum));
        }
    }

    private WebElement getZeroSumCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("zero-sum-checkbox"));
    }

    private WebElement getAllowNotSureContributionCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("not-sure-checkbox"));
    }

    private void inputContributionDetails(int questionNum, FeedbackContributionQuestionDetails questionDetails) {
        if (questionDetails.isZeroSum()) {
            markOptionAsSelected(getZeroSumCheckbox(questionNum));
        } else {
            markOptionAsUnselected(getZeroSumCheckbox(questionNum));
        }
        if (questionDetails.isNotSureAllowed()) {
            markOptionAsSelected(getAllowNotSureContributionCheckbox(questionNum));
        } else {
            markOptionAsUnselected(getAllowNotSureContributionCheckbox(questionNum));
        }
    }

    private WebElement getRubricRow(int questionNum, int rowNumber) {
        return getQuestionForm(questionNum).findElements(By.cssSelector("tm-rubric-question-edit-details-form tr"))
                .get(rowNumber - 1);
    }

    private List<WebElement> getRubricChoiceInputs(int questionNum) {
        return getRubricRow(questionNum, 1).findElements(By.tagName("input"));
    }

    private List<WebElement> getRubricTextareas(int questionNum, int rowNum) {
        return getRubricRow(questionNum, rowNum).findElements(By.tagName("textarea"));
    }

    private List<WebElement> getRubricWeights(int questionNum, int rowNum) {
        return getRubricRow(questionNum, rowNum).findElements(By.tagName("input"));
    }

    private WebElement getRubricDeleteSubQnBtn(int questionNum, int rowNum) {
        return getRubricRow(questionNum, rowNum).findElement(By.id("btn-delete-subquestion"));
    }

    private WebElement getRubricDeleteChoiceBtn(int questionNum, int colNum) {
        return getRubricRow(questionNum, getNumRubricRows(questionNum)).findElements(By.id("btn-delete-choice")).get(colNum);
    }

    private int getNumRubricRows(int questionNum) {
        return getQuestionForm(questionNum).findElements(By.cssSelector("#rubric-table tr")).size();
    }

    private int getNumRubricCols(int questionNum) {
        WebElement row = getRubricRow(questionNum, 1);
        return row.findElements(By.tagName("td")).size() + row.findElements(By.tagName("th")).size();
    }

    private void inputRubricDetails(int questionNum, FeedbackRubricQuestionDetails questionDetails) {
        int numSubQn = questionDetails.getNumOfRubricSubQuestions();
        int numChoices = questionDetails.getNumOfRubricChoices();
        adjustNumRubricFields(questionNum, numSubQn, numChoices);

        List<String> choices = questionDetails.getRubricChoices();
        for (int i = 0; i < numChoices; i++) {
            fillTextBox(getRubricChoiceInputs(questionNum).get(i), choices.get(i));
        }

        List<String> subQuestions = questionDetails.getRubricSubQuestions();
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();
        for (int i = 0; i < numSubQn; i++) {
            List<WebElement> textAreas = getRubricTextareas(questionNum, i + 2);
            fillTextBox(textAreas.get(0), subQuestions.get(i));
            for (int j = 0; j < numChoices; j++) {
                fillTextBox(textAreas.get(j + 1), descriptions.get(i).get(j));
                if (descriptions.get(i).get(j).isEmpty()) {
                    // using clear does not send the required event
                    // as a workaround, after clearing without event, enter a random character and delete it
                    textAreas.get(j + 1).sendKeys("a");
                    textAreas.get(j + 1).sendKeys(Keys.BACK_SPACE);
                }
            }
        }

        if (questionDetails.isHasAssignedWeights()) {
            markOptionAsSelected(getWeightCheckbox(questionNum));
            List<List<Double>> weights = questionDetails.getRubricWeights();
            for (int i = 0; i < numSubQn; i++) {
                for (int j = 0; j < numChoices; j++) {
                    fillTextBox(getRubricWeights(questionNum, i + 2).get(j), getDoubleString(weights.get(i).get(j)));
                }
            }
        } else {
            markOptionAsUnselected(getWeightCheckbox(questionNum));
        }
    }

    private void adjustNumRubricFields(int questionNum, int numSubQn, int numChoices) {
        int numSubQnsNeeded = numSubQn - (getNumRubricRows(questionNum) - 2);
        int numChoicesNeeded = numChoices - (getNumRubricCols(questionNum) - 1);
        if (numSubQnsNeeded > 0) {
            for (int i = 0; i < numSubQnsNeeded; i++) {
                click(getQuestionForm(questionNum).findElement(By.id("btn-add-row")));
            }
        }
        if (numChoicesNeeded > 0) {
            for (int i = 0; i < numChoicesNeeded; i++) {
                click(getQuestionForm(questionNum).findElement(By.id("btn-add-col")));
            }
        }
        if (numSubQnsNeeded < 0) {
            for (int i = 0; i < -numSubQnsNeeded; i++) {
                click(getRubricDeleteSubQnBtn(questionNum, 2));
            }
        }
        if (numChoicesNeeded < 0) {
            for (int i = 0; i < -numChoicesNeeded; i++) {
                clickAndConfirm(getRubricDeleteChoiceBtn(questionNum, 2));
            }
        }
    }

    private WebElement getAllowDuplicateRankCheckbox(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("duplicate-rank-checkbox"));
    }

    private void inputRankDetails(int questionNum, FeedbackRankQuestionDetails questionDetails) {
        if (questionDetails instanceof FeedbackRankOptionsQuestionDetails) {
            FeedbackRankOptionsQuestionDetails optionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
            inputOptions(questionNum, optionDetails.getOptions());
        }
        if (questionDetails.isAreDuplicatesAllowed()) {
            markOptionAsSelected(getAllowDuplicateRankCheckbox(questionNum));
        } else {
            markOptionAsUnselected(getAllowDuplicateRankCheckbox(questionNum));
        }
        inputMaxOptions(questionNum, questionDetails.getMaxOptionsToBeRanked());
        inputMinOptions(questionNum, questionDetails.getMinOptionsToBeRanked());
    }
}
