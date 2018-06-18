package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCaseWithMinimalGaeEnvironment;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackQuestionFeedbackPathSettings;
import teammates.ui.template.FeedbackQuestionVisibilitySettings;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.FeedbackSessionsForm;

/**
 * SUT: {@link InstructorFeedbackEditPageData}.
 */
public class InstructorFeedbackEditPageDataTest extends BaseTestCaseWithMinimalGaeEnvironment {

    private static final int DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO = 1;
    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void allTests() {

        ______TS("Typical case");
        // Setup
        InstructorFeedbackEditPageData data =
                new InstructorFeedbackEditPageData(dataBundle.accounts.get("instructor1OfCourse1"), dummySessionToken);
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();
        questions.add(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        questions.add(dataBundle.feedbackQuestions.get("qn2InSession1InCourse1"));
        questions.add(dataBundle.feedbackQuestions.get("qn3InSession1InCourse1"));

        Map<String, Boolean> questionHasResponses = new HashMap<>();
        questionHasResponses.put(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").getId(), true);

        List<StudentAttributes> studentList = new ArrayList<>();
        studentList.add(dataBundle.students.get("student1InCourse1"));

        List<InstructorAttributes> instructorList = new ArrayList<>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));

        InstructorAttributes instructor = getInstructorFromBundle("instructor1OfCourse1");
        CourseDetailsBundle courseDetails = new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1"));

        data.init(fs, questions, questionHasResponses, studentList, instructorList, instructor,
                true, instructorList.size(), courseDetails);

        // Test fs form
        FeedbackSessionsForm fsForm = data.getFsForm();
        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE)
                .withUserId(instructor.googleId).toString(), fsForm.getCopyToLink());
        assertEquals(fs.getCourseId(), fsForm.getCourseId());
        assertNull(fsForm.getCourses());
        assertNull(fsForm.getCoursesSelectField());
        assertFalse(fsForm.isSessionTemplateTypeEditable());
        assertTrue(fsForm.isEditFsButtonsVisible());
        assertNull(fsForm.getSessionTemplateTypeOptions());
        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE).toString(),
                     fsForm.getFormSubmitAction());

        assertEquals(data.getInstructorFeedbackDeleteLink(fs.getCourseId(),
                                                          fs.getFeedbackSessionName(),
                                                          Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE),
                     fsForm.getFsDeleteLink());
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getEndTimeLocal()), fsForm.getFsEndDate());

        assertEquals(fs.getFeedbackSessionName(), fsForm.getFsName());
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getStartTimeLocal()), fsForm.getFsStartDate());

        assertEquals(SanitizationHelper.sanitizeForHtml(fs.getInstructions().getValue()), fsForm.getInstructions());
        assertEquals("Save Changes", fsForm.getSubmitButtonText());

        assertFalse(fsForm.isCourseIdEditable());
        assertFalse(fsForm.isShowNoCoursesMessage());
        assertFalse(fsForm.isSubmitButtonVisible());
        assertFalse(fsForm.isSubmitButtonDisabled());

        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings = data.getFsForm().getAdditionalSettings();
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getResultsVisibleFromTimeLocal()),
                                           additionalSettings.getResponseVisibleDateValue());
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getSessionVisibleFromTimeLocal()),
                                           additionalSettings.getSessionVisibleDateValue());

        assertFalse(additionalSettings.isResponseVisiblePublishManuallyChecked());
        assertTrue(additionalSettings.isResponseVisibleDateChecked());
        assertFalse(additionalSettings.isResponseVisibleImmediatelyChecked());
        assertFalse(additionalSettings.isResponseVisibleDateDisabled());

        assertFalse(additionalSettings.isSessionVisibleAtOpenChecked());
        assertFalse(additionalSettings.isSessionVisibleDateDisabled());
        assertTrue(additionalSettings.isSessionVisibleDateButtonChecked());
        assertTrue(additionalSettings.isSendClosingEmailChecked());
        assertTrue(additionalSettings.isSendOpeningEmailChecked());
        assertTrue(additionalSettings.isSendPublishedEmailChecked());

        // test question edit forms
        List<FeedbackQuestionEditForm> questionForms = data.getQnForms();
        assertEquals(3, questionForms.size());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT, questionForms.get(0).getAction());
        assertEquals(fs.getCourseId(), questionForms.get(0).getCourseId());
        assertEquals(fs.getFeedbackSessionName(), questionForms.get(0).getFeedbackSessionName());

        String questionTextOfFirstQuestion = dataBundle.feedbackQuestions
                                                       .get("qn1InSession1InCourse1")
                                                       .getQuestionDetails().getQuestionText();
        assertEquals(questionTextOfFirstQuestion,
                     questionForms.get(0).getQuestionText());
        assertEquals(3, questionForms.get(0).getQuestionNumberOptions().size());
        assertEquals("What is the best selling point of your product?", questionForms.get(0).getQuestionText());

        FeedbackQuestionFeedbackPathSettings feedbackPath = questionForms.get(0).getFeedbackPathSettings();
        assertTrue(feedbackPath.isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO, feedbackPath.getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(1, questionForms.get(0).getQuestionIndex());

        // Test visibility settings for the zero'th question form
        FeedbackQuestionVisibilitySettings visibilitySettings = questionForms.get(0).getVisibilitySettings();
        verifyMapContains(visibilitySettings.getResponseVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(visibilitySettings.getGiverNameVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(visibilitySettings.getRecipientNameVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS));

        assertEquals("Rate 1 other student's product", questionForms.get(1).getQuestionText());
        assertTrue(questionForms.get(1).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());

        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO,
                     questionForms.get(1).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());

        verifyMapContains(questionForms.get(1).getVisibilitySettings().getResponseVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER));
        verifyMapContains(questionForms.get(1).getVisibilitySettings().getGiverNameVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(questionForms.get(1).getVisibilitySettings().getRecipientNameVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER));

        assertEquals("My comments on the class", questionForms.get(2).getQuestionText());

        assertEquals(3, questionForms.get(2).getQuestionIndex());

        assertFalse(questionForms.get(2).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO,
                     questionForms.get(2).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());

        verifyMapContains(questionForms.get(2).getVisibilitySettings().getResponseVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                                        FeedbackParticipantType.RECEIVER,
                                        FeedbackParticipantType.STUDENTS,
                                        FeedbackParticipantType.OWN_TEAM_MEMBERS));
        verifyMapContains(questionForms.get(2).getVisibilitySettings().getGiverNameVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                                        FeedbackParticipantType.RECEIVER,
                                        FeedbackParticipantType.STUDENTS,
                                        FeedbackParticipantType.OWN_TEAM_MEMBERS));
        verifyMapContains(questionForms.get(2).getVisibilitySettings().getRecipientNameVisibleFor(),
                          Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                                        FeedbackParticipantType.RECEIVER,
                                        FeedbackParticipantType.STUDENTS,
                                        FeedbackParticipantType.OWN_TEAM_MEMBERS));

        //TODO test specific question edit form html after it is no longer html in java

        // test question add form
        FeedbackQuestionEditForm newQuestionForm = data.getNewQnForm();
        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.getCourseId())
                        .withSessionName(fs.getFeedbackSessionName()).toString(), newQuestionForm.getDoneEditingLink());
        assertFalse(newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(-1, newQuestionForm.getQuestionIndex());

        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                            .withUserId(instructor.googleId)
                            .withCourseId(fs.getCourseId())
                            .withSessionName(fs.getFeedbackSessionName()).toString(),
                     newQuestionForm.getDoneEditingLink());

        // preview form
        FeedbackSessionPreviewForm previewForm = data.getPreviewForm();
        assertEquals(1, previewForm.getStudentToPreviewAsOptions().size());
        assertEquals(1, previewForm.getInstructorToPreviewAsOptions().size());

        ______TS("empty feedback session");
        // setup
        data = new InstructorFeedbackEditPageData(dataBundle.accounts.get("instructor1OfCourse1"), dummySessionToken);

        fs = dataBundle.feedbackSessions.get("empty.session");
        fs.setPublishedEmailEnabled(false);
        fs.setClosingEmailEnabled(false);

        questions = new ArrayList<>();

        questionHasResponses = new HashMap<>();
        studentList = new ArrayList<>();
        instructorList = new ArrayList<>();
        instructor = getInstructorFromBundle("instructor1OfCourse1");
        courseDetails = new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1"));

        data.init(fs, questions, questionHasResponses, studentList, instructorList, instructor, true,
                instructorList.size(), courseDetails);

        fsForm = data.getFsForm();
        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE)
                           .withUserId(instructor.googleId).toString(),
                      fsForm.getCopyToLink());
        assertEquals(fs.getCourseId(), fsForm.getCourseId());
        assertNull(fsForm.getCourses());
        assertNull(fsForm.getCoursesSelectField());
        assertFalse(fsForm.isSessionTemplateTypeEditable());
        assertTrue(fsForm.isEditFsButtonsVisible());
        assertNull(fsForm.getSessionTemplateTypeOptions());

        additionalSettings = data.getFsForm().getAdditionalSettings();
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getResultsVisibleFromTimeLocal()),
                                           additionalSettings.getResponseVisibleDateValue());
        assertEquals(TimeHelper.formatDateForSessionsForm(fs.getSessionVisibleFromTimeLocal()),
                                           additionalSettings.getSessionVisibleDateValue());

        assertFalse(additionalSettings.isResponseVisiblePublishManuallyChecked());
        assertTrue(additionalSettings.isResponseVisibleDateChecked());
        assertFalse(additionalSettings.isResponseVisibleImmediatelyChecked());
        assertFalse(additionalSettings.isResponseVisibleDateDisabled());

        assertFalse(additionalSettings.isSessionVisibleAtOpenChecked());
        assertFalse(additionalSettings.isSessionVisibleDateDisabled());
        assertTrue(additionalSettings.isSessionVisibleDateButtonChecked());
        assertFalse(additionalSettings.isSendClosingEmailChecked());
        assertTrue(additionalSettings.isSendOpeningEmailChecked());
        assertFalse(additionalSettings.isSendPublishedEmailChecked());

        questionForms = data.getQnForms();
        assertEquals(questions.size(), questionForms.size());

        previewForm = data.getPreviewForm();
        assertEquals(studentList.size(), previewForm.getStudentToPreviewAsOptions().size());
        assertEquals(instructorList.size(), previewForm.getInstructorToPreviewAsOptions().size());

        newQuestionForm = data.getNewQnForm();
        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.getCourseId())
                        .withSessionName(fs.getFeedbackSessionName()).toString(), newQuestionForm.getDoneEditingLink());
        assertFalse(newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());

        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                         .withUserId(instructor.googleId)
                         .withCourseId(fs.getCourseId())
                         .withSessionName(fs.getFeedbackSessionName()).toString(),
                     newQuestionForm.getDoneEditingLink());

        ______TS("Resolved time fields map");
        data = new InstructorFeedbackEditPageData(dataBundle.accounts.get("instructor1OfCourse1"), dummySessionToken);

        assertNotNull("Should be empty map if unused", data.getResolvedTimeFields());
        assertTrue(data.getResolvedTimeFields().isEmpty());

        Map<String, String> expected = new HashMap<>();
        String startDate = "start date";
        String startTime = "start time";
        expected.put(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, startDate);
        expected.put(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTime);
        data.putResolvedTimeField(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, startDate);
        data.putResolvedTimeField(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTime);
        assertEquals(expected, data.getResolvedTimeFields());
    }

    private InstructorAttributes getInstructorFromBundle(String instructor) {
        return dataBundle.instructors.get(instructor);
    }

    private void verifyMapContains(Map<String, Boolean> map, List<FeedbackParticipantType> list) {
        for (FeedbackParticipantType participant : list) {
            assertTrue(map.get(participant.name()));
        }
        Set<FeedbackParticipantType> nonParticipants = new HashSet<>(Arrays.asList(FeedbackParticipantType.values()));
        nonParticipants.removeAll(list);

        for (FeedbackParticipantType nonParticipant : nonParticipants) {
            assertFalse(map.containsKey(nonParticipant.name()));
        }
    }

}
