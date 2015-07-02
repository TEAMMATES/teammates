package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.gson.Gson;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorFeedbackEditPageData;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageDataTest extends BaseTestCase{

    private static Gson gson = Utils.getTeammatesGson();
    private static DataBundle dataBundle = getTypicalDataBundle();


    @Test
    public void allTests() {
        
        ______TS("Typical case");
        // Setup
        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(dataBundle.accounts.get("instructor1OfCourse1"));
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        List<FeedbackQuestionAttributes> questions = new ArrayList<FeedbackQuestionAttributes>();
        questions.add(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        questions.add(dataBundle.feedbackQuestions.get("qn2InSession1InCourse1"));
        questions.add(dataBundle.feedbackQuestions.get("qn3InSession1InCourse1"));
        
        List<FeedbackQuestionAttributes> copiableQuestions = new ArrayList<FeedbackQuestionAttributes>();
        copiableQuestions.addAll(dataBundle.feedbackQuestions.values());
        
        Map<String, Boolean> questionHasResponses = new HashMap<String, Boolean>();
        questionHasResponses.put(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").getId(), true);
        
        List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
        studentList.add(dataBundle.students.get("student1InCourse1"));
        
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        data.init(fs, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
        
        // Test fs form
        FeedbackSessionsForm fsForm = data.getFsForm();
        assertEquals((new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE).withUserId(instructor.googleId)).toString(), fsForm.getCopyToLink().toString());
        assertEquals(fs.courseId, fsForm.getCourseIdForNewSession());
        assertEquals(null, fsForm.getCourses());
        assertEquals(null, fsForm.getCoursesSelectField());
        assertEquals(null, fsForm.getFeedbackSessionNameForSessionList());
        assertEquals(false, fsForm.isFeedbackSessionTypeEditable());
        assertEquals(true, fsForm.isEditFsButtonsVisible());
        assertEquals(null, fsForm.getFeedbackSessionTypeOptions());
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE).toString(), fsForm.getFormSubmitAction().toString());
        
        assertEquals(data.getInstructorFeedbackSessionDeleteLink(fs.courseId, fs.feedbackSessionName, ""), fsForm.getFsDeleteLink().toString());
        assertEquals(TimeHelper.formatDate(fs.endTime), fsForm.getFsEndDate());
        
        assertEquals(fs.feedbackSessionName, fsForm.getFsName());
        assertEquals(TimeHelper.formatDate(fs.startTime), fsForm.getFsStartDate());
        
        assertEquals(Sanitizer.sanitizeForHtml(fs.instructions.getValue()), fsForm.getInstructions());
        assertEquals("Save Changes", fsForm.getSubmitButtonText());
        
        assertEquals(false, fsForm.isCourseIdEditable());
        assertEquals(false, fsForm.isShowNoCoursesMessage());
        assertEquals(false, fsForm.isSubmitButtonVisible());
        assertEquals(false, fsForm.isSubmitButtonDisabled());
        
        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings = data.getFsForm().getAdditionalSettings();
        assertEquals(TimeHelper.formatDate(fs.resultsVisibleFromTime), additionalSettings.getResponseVisibleDateValue());
        assertEquals(TimeHelper.formatDate(fs.sessionVisibleFromTime), additionalSettings.getSessionVisibleDateValue());
        
        assertFalse(additionalSettings.isResponseVisiblePublishManuallyChecked());
        assertTrue(additionalSettings.isResponseVisibleDateChecked());
        assertFalse(additionalSettings.isResponseVisibleImmediatelyChecked());
        assertFalse(additionalSettings.isResponseVisibleNeverChecked());
        assertFalse(additionalSettings.isResponseVisibleDateDisabled());
       
        assertFalse(additionalSettings.isSessionVisibleAtOpenChecked());
        assertFalse(additionalSettings.isSessionVisibleDateDisabled());
        assertTrue(additionalSettings.isSessionVisibleDateButtonChecked());
        assertFalse(additionalSettings.isSessionVisiblePrivateChecked());
        assertTrue(additionalSettings.isSendClosingEmailChecked());
        assertTrue(additionalSettings.isSendOpeningEmailChecked());
        assertTrue(additionalSettings.isSendPublishedEmailChecked());
        
        // test question edit forms
        List<FeedbackQuestionEditForm> questionForms = data.getQnForms();
        assertEquals(3, questionForms.size());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT, questionForms.get(0).getAction().toString());
        assertEquals(fs.courseId, questionForms.get(0).getCourseId());
        assertEquals(fs.feedbackSessionName, questionForms.get(0).getFeedbackSessionName());

        assertEquals(3, questionForms.get(0).getNumOfQuestionsOnPage());
        assertEquals(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"), questionForms.get(0).getQuestion());
        assertEquals(3, questionForms.get(0).getQuestionNumberOptions().size());
        assertEquals("What is the best selling point of your product?", questionForms.get(0).getQuestionText());
        assertEquals(false, questionForms.get(0).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(1, questionForms.get(0).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(8, questionForms.get(0).getFeedbackPathSettings().getRecipientParticipantOptions().size());
        assertEquals(4, questionForms.get(0).getFeedbackPathSettings().getGiverParticipantOptions().size());
        assertEquals("-1", questionForms.get(0).getQuestionNumberSuffix());
        
        assertTrue(questionForms.get(0).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getResponseVisibleFor().containsKey(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getResponseVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(0).getVisibilitySettings().getGiverNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getGiverNameVisibleFor().containsKey(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getGiverNameVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(0).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getRecipientNameVisibleFor().containsKey(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(0).getVisibilitySettings().getRecipientNameVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertEquals("Rate 1 other student's product", questionForms.get(1).getQuestionText());
        assertEquals(false, questionForms.get(1).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(1, questionForms.get(1).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(8, questionForms.get(1).getFeedbackPathSettings().getRecipientParticipantOptions().size());
        assertEquals(4, questionForms.get(1).getFeedbackPathSettings().getGiverParticipantOptions().size());
        
        assertTrue(questionForms.get(1).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertTrue(questionForms.get(1).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(1).getVisibilitySettings().getResponseVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(1).getVisibilitySettings().getGiverNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertFalse(questionForms.get(1).getVisibilitySettings().getGiverNameVisibleFor().containsKey(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(1).getVisibilitySettings().getGiverNameVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(1).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertTrue(questionForms.get(1).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.RECEIVER.name()));
        assertFalse(questionForms.get(1).getVisibilitySettings().getRecipientNameVisibleFor().containsKey(FeedbackParticipantType.STUDENTS.name()));
        
        assertEquals("My comments on the class", questionForms.get(2).getQuestionText());
        
        assertEquals("-3", questionForms.get(2).getQuestionNumberSuffix());
        
        assertEquals(true, questionForms.get(2).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(1, questionForms.get(2).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(8, questionForms.get(2).getFeedbackPathSettings().getRecipientParticipantOptions().size());
        assertEquals(4, questionForms.get(2).getFeedbackPathSettings().getGiverParticipantOptions().size());
        
        assertTrue(questionForms.get(2).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.RECEIVER.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getResponseVisibleFor().get(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(2).getVisibilitySettings().getGiverNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getGiverNameVisibleFor().get(FeedbackParticipantType.RECEIVER.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getGiverNameVisibleFor().get(FeedbackParticipantType.STUDENTS.name()));
        
        assertTrue(questionForms.get(2).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.INSTRUCTORS.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.RECEIVER.name()));
        assertTrue(questionForms.get(2).getVisibilitySettings().getRecipientNameVisibleFor().get(FeedbackParticipantType.STUDENTS.name()));
        //TODO test specific question edit form html after it is no longer html in java
        
        // test question add form
        FeedbackQuestionEditForm newQuestionForm = data.getNewQnForm();
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.courseId)
                        .withSessionName(fs.feedbackSessionName).toString(), newQuestionForm.getDoneEditingLink().toString());
        assertEquals(false, newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals("", newQuestionForm.getQuestionNumberSuffix());
        
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                            .withUserId(instructor.googleId)
                            .withCourseId(fs.courseId)
                            .withSessionName(fs.feedbackSessionName).toString(),
                     newQuestionForm.getDoneEditingLink().toString());
        
        // preview form
        FeedbackSessionPreviewForm previewForm = data.getPreviewForm();
        assertEquals(1, previewForm.getStudentToPreviewAsOptions().size());
        assertEquals(1, previewForm.getInstructorToPreviewAsOptions().size());
        
        // copy question form
        FeedbackQuestionCopyTable copyForm = data.getCopyQnForm();
        assertEquals(dataBundle.feedbackQuestions.size(), copyForm.getQuestionRows().size());
        
        
        ______TS("empty feedback session");
        // setup
        data = new InstructorFeedbackEditPageData(dataBundle.accounts.get("instructor1OfCourse1"));
        
        fs = dataBundle.feedbackSessions.get("empty.session");
        fs.isPublishedEmailEnabled = false;
        fs.isClosingEmailEnabled = false;
        
        questions = new ArrayList<FeedbackQuestionAttributes>();
        
        copiableQuestions = new ArrayList<FeedbackQuestionAttributes>();
        
        questionHasResponses = new HashMap<String, Boolean>();
        
        studentList = new ArrayList<StudentAttributes>();
        
        instructorList = new ArrayList<InstructorAttributes>();
        
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        data.init(fs, questions, copiableQuestions, 
                  questionHasResponses, studentList, 
                  instructorList, instructor);
        fsForm = data.getFsForm();
        assertEquals((new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE).withUserId(instructor.googleId)).toString(), fsForm.getCopyToLink().toString());
        assertEquals(fs.courseId, fsForm.getCourseIdForNewSession());
        assertEquals(null, fsForm.getCourses());
        assertEquals(null, fsForm.getCoursesSelectField());
        assertEquals(null, fsForm.getFeedbackSessionNameForSessionList());
        assertEquals(false, fsForm.isFeedbackSessionTypeEditable());
        assertEquals(true, fsForm.isEditFsButtonsVisible());
        assertEquals(null, fsForm.getFeedbackSessionTypeOptions());
        
        additionalSettings = data.getFsForm().getAdditionalSettings();
        assertEquals(TimeHelper.formatDate(fs.resultsVisibleFromTime), additionalSettings.getResponseVisibleDateValue());
        assertEquals(TimeHelper.formatDate(fs.sessionVisibleFromTime), additionalSettings.getSessionVisibleDateValue());
        
        assertFalse(additionalSettings.isResponseVisiblePublishManuallyChecked());
        assertTrue(additionalSettings.isResponseVisibleDateChecked());
        assertFalse(additionalSettings.isResponseVisibleImmediatelyChecked());
        assertFalse(additionalSettings.isResponseVisibleNeverChecked());
        assertFalse(additionalSettings.isResponseVisibleDateDisabled());
       
        assertFalse(additionalSettings.isSessionVisibleAtOpenChecked());
        assertFalse(additionalSettings.isSessionVisibleDateDisabled());
        assertTrue(additionalSettings.isSessionVisibleDateButtonChecked());
        assertFalse(additionalSettings.isSessionVisiblePrivateChecked());
        assertFalse(additionalSettings.isSendClosingEmailChecked());
        assertTrue(additionalSettings.isSendOpeningEmailChecked());
        assertFalse(additionalSettings.isSendPublishedEmailChecked());
        
        assertEquals("You have not created any questions for this feedback session yet. Click the button below to add a feedback question.", data.getEmptyFsMsg());
        
        questionForms = data.getQnForms();
        assertEquals(0, questionForms.size());
        
        previewForm = data.getPreviewForm();
        assertEquals(0, previewForm.getStudentToPreviewAsOptions().size());
        assertEquals(0, previewForm.getInstructorToPreviewAsOptions().size());
        
        newQuestionForm = data.getNewQnForm();
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.courseId)
                        .withSessionName(fs.feedbackSessionName).toString(), newQuestionForm.getDoneEditingLink().toString());
        assertEquals(false, newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                         .withUserId(instructor.googleId)
                         .withCourseId(fs.courseId)
                         .withSessionName(fs.feedbackSessionName).toString(),
                     newQuestionForm.getDoneEditingLink().toString());
        
        
        copyForm = data.getCopyQnForm();
        assertEquals(0, copyForm.getQuestionRows().size());
        
        
        ______TS("instructor with insufficient permissions");
        // setup
        data = new InstructorFeedbackEditPageData(dataBundle.accounts.get("helperOfCourse1"));
        fs = dataBundle.feedbackSessions.get("session1InCourse1");
        fs.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
        fs.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        
        questions = new ArrayList<FeedbackQuestionAttributes>();
        questions.add(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        questions.add(dataBundle.feedbackQuestions.get("qn2InSession1InCourse1"));
        
        copiableQuestions = new ArrayList<FeedbackQuestionAttributes>();
        copiableQuestions.addAll(dataBundle.feedbackQuestions.values());
        
        questionHasResponses = new HashMap<String, Boolean>();
        questionHasResponses.put(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").getId(), true);
        
        studentList = new ArrayList<StudentAttributes>();
        studentList.add(dataBundle.students.get("student1InCourse1"));
        
        instructorList = new ArrayList<InstructorAttributes>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));
        
        instructor = dataBundle.instructors.get("helperOfCourse1");
        instructor.privileges = gson.fromJson(instructor.instructorPrivilegesAsText, InstructorPrivileges.class);
        
        data.init(fs, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
        assertEquals(0, data.getCopyQnForm().getQuestionRows().size());
    }



}