package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import teammates.ui.template.FeedbackQuestionFeedbackPathSettings;
import teammates.ui.template.FeedbackQuestionVisibilitySettings;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageDataTest extends BaseTestCase{

    private static final int DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO = 1;
    private static final int NUM_GIVER_OPTIONS = 4;
    private static final int NUM_RECIPIENT_OPTIONS = 8;
    
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
        
        InstructorAttributes instructor = getInstructorFromBundle("instructor1OfCourse1");
        
        data.init(fs, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
        
        // Test fs form
        FeedbackSessionsForm fsForm = data.getFsForm();
        assertEquals((new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE).withUserId(instructor.googleId)).toString(), fsForm.getCopyToLink());
        assertEquals(fs.courseId, fsForm.getCourseId());
        assertNull(fsForm.getCourses());
        assertNull(fsForm.getCoursesSelectField());
        assertFalse(fsForm.isFeedbackSessionTypeEditable());
        assertTrue(fsForm.isEditFsButtonsVisible());
        assertNull(fsForm.getFeedbackSessionTypeOptions());
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE).toString(), fsForm.getFormSubmitAction());
        
        assertEquals(data.getInstructorFeedbackDeleteLink(fs.courseId, fs.feedbackSessionName, ""), fsForm.getFsDeleteLink());
        assertEquals(TimeHelper.formatDate(fs.endTime), fsForm.getFsEndDate());
        
        assertEquals(fs.feedbackSessionName, fsForm.getFsName());
        assertEquals(TimeHelper.formatDate(fs.startTime), fsForm.getFsStartDate());
        
        assertEquals(Sanitizer.sanitizeForHtml(fs.instructions.getValue()), fsForm.getInstructions());
        assertEquals("Save Changes", fsForm.getSubmitButtonText());
        
        assertFalse(fsForm.isCourseIdEditable());
        assertFalse(fsForm.isShowNoCoursesMessage());
        assertFalse(fsForm.isSubmitButtonVisible());
        assertFalse(fsForm.isSubmitButtonDisabled());
        
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
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT, questionForms.get(0).getAction());
        assertEquals(fs.courseId, questionForms.get(0).getCourseId());
        assertEquals(fs.feedbackSessionName, questionForms.get(0).getFeedbackSessionName());

        assertEquals(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"), questionForms.get(0).getQuestion());
        assertEquals(3, questionForms.get(0).getQuestionNumberOptions().size());
        assertEquals("What is the best selling point of your product?", questionForms.get(0).getQuestionText());
        
        FeedbackQuestionFeedbackPathSettings feedbackPath = questionForms.get(0).getFeedbackPathSettings();
        assertEquals(true, feedbackPath.isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO, feedbackPath.getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(NUM_RECIPIENT_OPTIONS, feedbackPath.getRecipientParticipantOptions().size());
        assertEquals(NUM_GIVER_OPTIONS, feedbackPath.getGiverParticipantOptions().size());
        assertEquals("-1", questionForms.get(0).getQuestionNumberSuffix());
        
        // Test visibility settings for the zero'th question form
        FeedbackQuestionVisibilitySettings visibilitySettings = questionForms.get(0).getVisibilitySettings();
        verifyMapContains(visibilitySettings.getResponseVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(visibilitySettings.getGiverNameVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(visibilitySettings.getRecipientNameVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        
        assertEquals("Rate 1 other student's product", questionForms.get(1).getQuestionText());
        assertTrue(questionForms.get(1).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
       
        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO, questionForms.get(1).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(NUM_RECIPIENT_OPTIONS, questionForms.get(1).getFeedbackPathSettings().getRecipientParticipantOptions().size());
        assertEquals(NUM_GIVER_OPTIONS, questionForms.get(1).getFeedbackPathSettings().getGiverParticipantOptions().size());
        
        verifyMapContains(questionForms.get(1).getVisibilitySettings().getResponseVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER));
        verifyMapContains(questionForms.get(1).getVisibilitySettings().getGiverNameVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        verifyMapContains(questionForms.get(1).getVisibilitySettings().getRecipientNameVisibleFor(), Arrays.asList(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER));
        
        assertEquals("My comments on the class", questionForms.get(2).getQuestionText());
        
        assertEquals("-3", questionForms.get(2).getQuestionNumberSuffix());
        
        assertFalse(questionForms.get(2).getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertEquals(DEFAULT_NUM_ENTITIES_TO_GIVE_RESPONSES_TO, questionForms.get(2).getFeedbackPathSettings().getNumOfEntitiesToGiveFeedbackToValue());
        assertEquals(NUM_RECIPIENT_OPTIONS, questionForms.get(2).getFeedbackPathSettings().getRecipientParticipantOptions().size());
        assertEquals(NUM_GIVER_OPTIONS, questionForms.get(2).getFeedbackPathSettings().getGiverParticipantOptions().size());
                
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
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.courseId)
                        .withSessionName(fs.feedbackSessionName).toString(), newQuestionForm.getDoneEditingLink());
        assertFalse(newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        assertTrue(newQuestionForm.getQuestionNumberSuffix().isEmpty());
        
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                            .withUserId(instructor.googleId)
                            .withCourseId(fs.courseId)
                            .withSessionName(fs.feedbackSessionName).toString(),
                     newQuestionForm.getDoneEditingLink());
        
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
        instructor = getInstructorFromBundle("instructor1OfCourse1");
        
        data.init(fs, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
        fsForm = data.getFsForm();
        assertEquals((new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE)
                          .withUserId(instructor.googleId)).toString(), 
                      fsForm.getCopyToLink());
        assertEquals(fs.courseId, fsForm.getCourseId());
        assertNull(fsForm.getCourses());
        assertNull(fsForm.getCoursesSelectField());
        assertFalse(fsForm.isFeedbackSessionTypeEditable());
        assertTrue(fsForm.isEditFsButtonsVisible());
        assertNull(fsForm.getFeedbackSessionTypeOptions());
        
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
        
        
        questionForms = data.getQnForms();
        assertEquals(questions.size(), questionForms.size());
        
        previewForm = data.getPreviewForm();
        assertEquals(studentList.size(), previewForm.getStudentToPreviewAsOptions().size());
        assertEquals(instructorList.size(), previewForm.getInstructorToPreviewAsOptions().size());
        
        newQuestionForm = data.getNewQnForm();
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                        .withUserId(instructor.googleId)
                        .withCourseId(fs.courseId)
                        .withSessionName(fs.feedbackSessionName).toString(), newQuestionForm.getDoneEditingLink());
        assertFalse(newQuestionForm.getFeedbackPathSettings().isNumberOfEntitiesToGiveFeedbackToChecked());
        
        assertEquals(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                         .withUserId(instructor.googleId)
                         .withCourseId(fs.courseId)
                         .withSessionName(fs.feedbackSessionName).toString(),
                     newQuestionForm.getDoneEditingLink());
        
        copyForm = data.getCopyQnForm();
        assertEquals(copiableQuestions.size(), copyForm.getQuestionRows().size());
        
        
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
        
        instructor = getInstructorFromBundle("helperOfCourse1");
        
        data.init(fs, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
        assertEquals(0, data.getCopyQnForm().getQuestionRows().size());
    }
    
    public InstructorAttributes getInstructorFromBundle(String instructor) {
        InstructorAttributes instructorAttributes = dataBundle.instructors.get(instructor);
        instructorAttributes.privileges = gson.fromJson(instructorAttributes.instructorPrivilegesAsText, InstructorPrivileges.class);
        return instructorAttributes;
    }

    public void verifyMapContains(Map<String, Boolean> map, List<FeedbackParticipantType> list) {
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