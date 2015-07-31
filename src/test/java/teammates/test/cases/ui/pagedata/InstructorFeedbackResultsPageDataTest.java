package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.ui.controller.InstructorFeedbackResultsPageData.ViewType;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.InstructorFeedbackResultsPageData;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel;
import teammates.ui.template.InstructorFeedbackResultsModerationButton;
import teammates.ui.template.InstructorFeedbackResultsResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;
import teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody;
import teammates.ui.template.InstructorFeedbackResultsSectionPanel;
import teammates.ui.template.InstructorFeedbackResultsParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsQuestionTable;

public class InstructorFeedbackResultsPageDataTest extends BaseComponentTestCase {
    private static final String NOT_IN_A_SECTION = "Not in a section";

    private static DataBundle dataBundle = getTypicalDataBundle();
    
    // logic is used in this page data test, but not other page data tests, because
    // unfortunately, logic is required to construct the ResultsBundle
    Logic logic = new Logic();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testInitForViewByQuestion() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("typical case: view all sections, all questions, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "question");
        data.initForViewByQuestion(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, "question", "on");
        
        List<InstructorFeedbackResultsQuestionTable> questionPanels = data.getQuestionPanels();
        assertEquals(4, questionPanels.size());
        
        // Verify that the first question table is correct
        InstructorFeedbackResultsQuestionTable questionPanel = questionPanels.get(0);
        verifyQuestionTableForQuestionView(questionPanel, dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue());
        
        List<InstructorFeedbackResultsResponseRow> responseRows = questionPanel.getResponses();
        assertEquals(5, responseRows.size());
        // first response
        InstructorFeedbackResultsResponseRow responseRow = responseRows.get(0);
        verifyResponseRow(responseRow, dataBundle.feedbackResponses.get("response1ForQ1S1C1"), 
                                       dataBundle.students.get("student1InCourse1"), 
                                       dataBundle.students.get("student1InCourse1"));
        
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertFalse(responseRow.isRecipientProfilePictureAColumn());
        assertNotNull(responseRow.getModerationButton());
        
        InstructorFeedbackResultsModerationButton modButton = responseRow.getModerationButton();
  
        verifyModerateButton(modButton, "Moderate Response", 
                             dataBundle.students.get("student1InCourse1"), 
                             "First feedback session", 1);
        
        //missing response
        InstructorFeedbackResultsResponseRow missingResponseRow = responseRows.get(responseRows.size() - 1);
        verifyResponseRow(missingResponseRow, null, dataBundle.students.get("student5InCourse1"), 
                                             dataBundle.students.get("student5InCourse1"));
        
        assertNotNull(missingResponseRow.getModerationButton());
        modButton = missingResponseRow.getModerationButton();
        
        String moderateButtonText = "Moderate Response";
        StudentAttributes studentToModerate = dataBundle.students.get("student5InCourse1");
        String feedbackSessionName = "First feedback session";
        int questionNumber = 1;
        
        verifyModerateButton(modButton, moderateButtonText, studentToModerate, feedbackSessionName,
                                        questionNumber);
        
        // Verify the correctness of fourth question, which has giver type=instructor
        //TODO see #3367
        questionPanel = questionPanels.get(3);
        responseRows = questionPanel.getResponses();
        modButton = responseRows.get(0).getModerationButton();
        //assertFalse(responseRows.get(0).isModerationButtonDisplayed());
        //assertTrue(modButton.isAllowedToModerate());
        
        
        ______TS("view by question, section 1, all questions");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = "Section 1";
        data.bundle = logic.getFeedbackSessionResultsForInstructorInSection(data.feedbackSessionName, data.courseId, 
                                                                            instructor.email, data.selectedSection);
        data.initForViewByQuestion(instructor, "Section 1", null, "on");
        
        List<InstructorFeedbackResultsQuestionTable> sectionQuestionPanels = data.getQuestionPanels();
        assertEquals(4, sectionQuestionPanels.size());
        
        InstructorFeedbackResultsQuestionTable sectionQuestionPanel = sectionQuestionPanels.get(1);
        assertTrue(sectionQuestionPanel.getQuestionStatisticsTable().isEmpty());
        
        List<InstructorFeedbackResultsResponseRow> sectionResponseRows = sectionQuestionPanel.getResponses();
        assertEquals(3, sectionResponseRows.size());
        InstructorFeedbackResultsResponseRow sectionResponseRow = sectionResponseRows.get(2);

        assertEquals("Response from student 3 &quot;to&quot; student 2.\r\nMultiline test.", sectionResponseRow.getDisplayableResponse());
        
        
        ______TS("view by question, all sections, question 2");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions
                                             .get("session1InCourse1")
                                             .feedbackSessionName;
        
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        data.bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(data.feedbackSessionName, data.courseId, 
                                                                               data.instructor.email, 2);
        
        data.initForViewByQuestion(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, 
                                   "on", "on");
            
        List<InstructorFeedbackResultsQuestionTable> singleQuestionPanelList = data.getQuestionPanels();
        assertEquals(1, singleQuestionPanelList.size());
        
        InstructorFeedbackResultsQuestionTable singleQuestionPanel = singleQuestionPanelList.get(0);
        verifyQuestionTableForQuestionView(singleQuestionPanel, dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").questionMetaData.getValue());
        
        List<InstructorFeedbackResultsResponseRow> singleQuestionResponseRows = singleQuestionPanel.getResponses();
        assertEquals(3, singleQuestionResponseRows.size());
        // just verify any response
        InstructorFeedbackResultsResponseRow singleQuestionResponseRow = singleQuestionResponseRows.get(1);
        verifyResponseRow(singleQuestionResponseRow, dataBundle.feedbackResponses.get("response1ForQ2S1C1"),
                          dataBundle.students.get("student2InCourse1"), dataBundle.students.get("student1InCourse1"));
        
        assertFalse(singleQuestionResponseRow.isGiverProfilePictureAColumn());
        assertFalse(singleQuestionResponseRow.isRecipientProfilePictureAColumn());

        assertNotNull(singleQuestionResponseRow.getModerationButton());
        
        modButton = singleQuestionResponseRow.getModerationButton();
        assertEquals("Moderate Response", modButton.getButtonText());
        verifyHtmlClass(modButton.getClassName(), "btn", "btn-default", "btn-xs");
        
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals("student2InCourse1@gmail.tmt", modButton.getGiverIdentifier());
        assertEquals(2, modButton.getQuestionNumber());
        assertTrue(modButton.isAllowedToModerate());
        assertFalse(modButton.isDisabled());
        
        
        ______TS("view by question, all sections, require loading by ajax");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1, "question");
        data.initForViewByQuestion(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, "on", "on");
        assertFalse(data.bundle.isComplete());
        
        List<InstructorFeedbackResultsQuestionTable> ajaxQuestionPanels = data.getQuestionPanels();
        assertEquals(4, ajaxQuestionPanels.size());
        InstructorFeedbackResultsQuestionTable ajaxQuestionPanel = ajaxQuestionPanels.get(0);
        assertEquals("panel-default", ajaxQuestionPanel.getPanelClass());
        assertTrue(ajaxQuestionPanel.isCollapsible());
        
        verifyHtmlClass(ajaxQuestionPanel.getResponsesBodyClass(), "panel-collapse", "collapse");
    }

    private void verifyModerateButton(InstructorFeedbackResultsModerationButton modButton, String moderateButtonText,
                                    StudentAttributes studentToModerate, String feedbackSessionName,
                                    int questionNumber) {
        assertEquals(moderateButtonText, modButton.getButtonText());
        assertEquals(studentToModerate.course, modButton.getCourseId());
        assertEquals(feedbackSessionName, modButton.getFeedbackSessionName());
        assertEquals(studentToModerate.email, modButton.getGiverIdentifier());
        assertEquals(questionNumber, modButton.getQuestionNumber());
        assertTrue(modButton.isAllowedToModerate());
        assertFalse(modButton.isDisabled());
        
        verifyHtmlClass(modButton.getClassName(), "btn", "btn-default", "btn-xs");
    }

    private void verifyQuestionTableForQuestionView(InstructorFeedbackResultsQuestionTable questionPanel, String questionText) {
        assertEquals("panel-info", questionPanel.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(questionText), 
                                        questionPanel.getQuestionText());
        
        verifyHtmlClass(questionPanel.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        assertEquals(6, questionPanel.getColumns().size());
        
        verifyColumns(questionPanel.getColumns(), "Giver", "Team", "Recipient", "Team", "Feedback", "Actions");
        
        verifyKeysOfMap(questionPanel.getIsColumnSortable(),
                        Arrays.asList("Giver", "Team", "Recipient", "Feedback", "Actions"));
        verifyTrueValuesInMap(questionPanel.getIsColumnSortable(), 
                              Arrays.asList("Giver", "Team", "Recipient", "Feedback"));
        verifyFalseValuesInMap(questionPanel.getIsColumnSortable(), 
                               Arrays.asList("Actions"));
    }
    
    @Test
    public void testInitForViewByGQR() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("GQR typical case: view all sections, all sections, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "giver-question-recipient";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "giver-question-recipient");
        //data.initForViewByGiverQuestionRecipient(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, "giver-question-recipient", "on");
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, 
                                      "on", "on", ViewType.GIVER_QUESTION_RECIPIENT);
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels = data.getSectionPanels();
        verifyKeysOfMap(sectionPanels, Arrays.asList("Section 1", "Section 2", "None"));
        
        ______TS("GQR typical case - verify 'None' Section");
        InstructorFeedbackResultsSectionPanel sectionPanel = sectionPanels.get("None");
        assertEquals(NOT_IN_A_SECTION, sectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", sectionPanel.getDetailedResponsesHeaderText());
        verifyKeysOfMap(sectionPanel.getIsTeamWithResponses(), Arrays.asList("Instructors"));
        assertEquals("panel-success", sectionPanel.getPanelClass());
        assertEquals("Statistics for Given Responses", sectionPanel.getStatisticsHeaderText());
        
        
        // Incorrect values now due to a bug. TODO see #2857 
        //Map<String, List<InstructorFeedbackResultsQuestionTable>> statsTables = sectionPanel.getTeamStatisticsTable();
        //assertEquals(1, statsTables.size());
        
        //List<InstructorFeedbackResultsQuestionTable> statsTablesForInstructors = statsTables.get("Instructors");
        //assertEquals(0, statsTablesForInstructors.size());
        
        assertEquals(1, sectionPanel.getParticipantPanels().size());
        
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)sectionPanel.getParticipantPanels().get("Instructors").get(0);
        assertEquals("instructor1@course1.tmt", participantPanel.getParticipantIdentifier());
        assertEquals("Instructor1 Course1", participantPanel.getName());
        
        assertTrue(participantPanel.isGiver());
        assertNull(participantPanel.getModerationButton());
        assertTrue(participantPanel.isHasResponses());
        
        ______TS("GQR typical case - verify question panel");
        List<InstructorFeedbackResultsQuestionTable> questionPanels = participantPanel.getQuestionTables();
        assertEquals(1, questionPanels.size());
        
        InstructorFeedbackResultsQuestionTable firstQuestionPanel = questionPanels.get(0);
        
        assertEquals(1, firstQuestionPanel.getResponses().size());
        assertEquals("", firstQuestionPanel.getAdditionalInfoText());
        verifyQuestionTableForGQR(firstQuestionPanel, "qn3InSession1InCourse1");
        
        InstructorFeedbackResultsResponseRow responseRow = firstQuestionPanel.getResponses().get(0);
        
        assertEquals("Good work, keep it up!", responseRow.getDisplayableResponse());
        assertEquals(instructor.name, responseRow.getGiverDisplayableIdentifier());
        assertEquals("-", responseRow.getRecipientDisplayableIdentifier());
        assertEquals("Instructors", responseRow.getGiverTeam());
        assertEquals("-", responseRow.getRecipientTeam());
        assertFalse(responseRow.isGiverDisplayed());
        assertTrue(responseRow.isRecipientDisplayed());
        assertTrue(responseRow.isRecipientProfilePictureAColumn());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertEquals(null, responseRow.getRowAttributes());
        
        ______TS("GQR typical case - verify Section 1");
        sectionPanel = sectionPanels.get("Section 1");
        assertTrue(sectionPanel.isDisplayingMissingParticipants());
        assertTrue(sectionPanel.isDisplayingTeamStatistics());
        assertFalse(sectionPanel.isLoadSectionResponsesByAjax());
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> sectionOneParticipantPanels = sectionPanel.getParticipantPanels();
        
        verifyKeysOfMap(sectionOneParticipantPanels, Arrays.asList("Team 1.1"));
        List<InstructorFeedbackResultsParticipantPanel> teamParticipantPanel = sectionOneParticipantPanels.get("Team 1.1");
        
        assertEquals("student1InCourse1@gmail.tmt", teamParticipantPanel.get(0).getParticipantIdentifier());
        assertTrue(teamParticipantPanel.get(0).isGiver());
        assertNotNull(teamParticipantPanel.get(0).getModerationButton());
        
        
        List<InstructorFeedbackResultsQuestionTable> singleSectionQuestionTables = ((InstructorFeedbackResultsGroupByQuestionPanel)teamParticipantPanel.get(0)).getQuestionTables();
        assertEquals(2, singleSectionQuestionTables.size());
        
        InstructorFeedbackResultsQuestionTable singleSectionQuestionTable = singleSectionQuestionTables.get(1);
        assertEquals(Sanitizer.sanitizeForHtml("Rate 1 other student's product"), 
                     singleSectionQuestionTable.getQuestionText());
        verifyHtmlClass(singleSectionQuestionTable.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        assertEquals(1, singleSectionQuestionTable.getResponses().size());
        
        InstructorFeedbackResultsResponseRow secondQuestionResponseRow = 
                                        singleSectionQuestionTable.getResponses().get(0);

        assertTrue(secondQuestionResponseRow.isRecipientProfilePictureAColumn());
        assertFalse(secondQuestionResponseRow.isGiverProfilePictureAColumn());
        assertNotNull(secondQuestionResponseRow.getModerationButton());
        verifyResponseRow(secondQuestionResponseRow, dataBundle.feedbackResponses.get("response2ForQ2S1C1"), 
                                       dataBundle.students.get("student1InCourse1"), 
                                       dataBundle.students.get("student2InCourse1"));

        
        ______TS("GQR section 1 case: view section 1, all questions, no stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = "Section 1";
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorFromSectionWithinRange(data.feedbackSessionName, data.courseId,
                                                                                  data.instructor.email, data.selectedSection, 1000);
        
        data.initForSectionPanelViews(instructor, "Section 1", null, "on", ViewType.GIVER_QUESTION_RECIPIENT);
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionOnePanels = data.getSectionPanels();
        verifyKeysOfMap(sectionOnePanels, Arrays.asList("Section 1"));
        
        ______TS("GQR section 1 case - verify section panel");
        InstructorFeedbackResultsSectionPanel singleSectionPanel = sectionOnePanels.get("Section 1");
        assertEquals("Section 1", singleSectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", singleSectionPanel.getDetailedResponsesHeaderText());

        verifyKeysOfMap(singleSectionPanel.getIsTeamWithResponses(), Arrays.asList("Team 1.1"));
        assertEquals("panel-success", singleSectionPanel.getPanelClass());
        assertEquals("Statistics for Given Responses", singleSectionPanel.getStatisticsHeaderText());
        
        assertTrue(singleSectionPanel.isDisplayingTeamStatistics()); // note that this is still true because
                                                               // the logic for displaying stats is done in javascript
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> studentPanels = singleSectionPanel.getParticipantPanels();
        InstructorFeedbackResultsGroupByQuestionPanel studentPanel = (InstructorFeedbackResultsGroupByQuestionPanel) studentPanels.get("Team 1.1").get(0);
        questionPanels = studentPanel.getQuestionTables();
        assertEquals(2, questionPanels.size());
        
        ______TS("GQR section 1 case - verify question panel");
        InstructorFeedbackResultsQuestionTable questionPanel = questionPanels.get(0);
        
        assertEquals(1, questionPanel.getResponses().size());
        assertEquals("", questionPanel.getAdditionalInfoText());
        assertEquals("panel-info", questionPanel.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue()), 
                                        questionPanel.getQuestionText());
        verifyHtmlClass(questionPanel.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        verifyColumns(questionPanel.getColumns(), "Photo", "Recipient", "Team", "Feedback");
        verifyKeysOfMap(questionPanel.getIsColumnSortable(),
                        Arrays.asList("Photo", "Recipient", "Team", "Feedback"));
        verifyTrueValuesInMap(questionPanel.getIsColumnSortable(), 
                              Arrays.asList("Recipient", "Team", "Feedback"));
        verifyFalseValuesInMap(questionPanel.getIsColumnSortable(), 
                               Arrays.asList("Photo"));
        
        responseRow = questionPanel.getResponses().get(0);
        verifyResponseRow(responseRow, dataBundle.feedbackResponses.get("response1ForQ1S1C1"), 
                                       dataBundle.students.get("student1InCourse1"), 
                                       dataBundle.students.get("student1InCourse1"));
        assertTrue(responseRow.isRecipientProfilePictureAColumn());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        
        InstructorFeedbackResultsModerationButton moderationButton = responseRow.getModerationButton();
        verifyModerateButton(moderationButton, "Moderate Response", dataBundle.students.get("student1InCourse1"), 
                             "First feedback session", 1);
        
        ______TS("GQR not grouped by team case: all sections, not grouping by team");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions
                                             .get("session1InCourse1")
                                             .feedbackSessionName;
        
        data.showStats = "on";
        data.groupByTeam = null;
        data.sortType = "giver-question-recipient";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "giver-question-recipient");
        
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                      "on", null, ViewType.GIVER_QUESTION_RECIPIENT);
        
        Map<String, InstructorFeedbackResultsSectionPanel> notGroupByTeamPanels = data.getSectionPanels();
        verifyKeysOfMap(notGroupByTeamPanels, Arrays.asList("Section 1", "Section 2", "None"));
        
        sectionPanel = notGroupByTeamPanels.get("Section 1");
        
        List<InstructorFeedbackResultsParticipantPanel> notGroupByTeamParticipantPanels = sectionPanel.getParticipantPanelsInSortedOrder();
        assertEquals(4, notGroupByTeamParticipantPanels.size());
        
        List<String> nameList = Arrays.asList("student1 In Course1", "student2 In Course1", 
                                              "student3 In Course1", "student4 In Course1");
        
        for (int i = 0; i < nameList.size(); i++) {
            String nameOfStudent = nameList.get(i);
            InstructorFeedbackResultsGroupByQuestionPanel notGroupByTeamParticipantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(i);
            String sectionPanelStudentName = notGroupByTeamParticipantPanel.getName();
            
            assertEquals(nameOfStudent, sectionPanelStudentName);
            
            // Test moderate button on student panel
            InstructorFeedbackResultsModerationButton modButton = notGroupByTeamParticipantPanel.getModerationButton();
            assertEquals("Moderate Responses", modButton.getButtonText());
            
            if (notGroupByTeamParticipantPanel.isHasResponses()) {
                verifyHtmlClass(modButton.getClassName(), "btn", "btn-xs", "btn-primary");
            } else {
                verifyHtmlClass(modButton.getClassName(), "btn", "btn-xs", "btn-default");
            }
            
            assertEquals("idOfTypicalCourse1", modButton.getCourseId());
            assertEquals("First feedback session", modButton.getFeedbackSessionName());
            assertEquals(-1, modButton.getQuestionNumber());
        }
        
        InstructorFeedbackResultsGroupByQuestionPanel notGroupByTeamStudentPanel = 
                                        (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(1);
        
        List<InstructorFeedbackResultsQuestionTable> notGroupByTeamquestionTables = notGroupByTeamStudentPanel.getQuestionTables();
        assertEquals(2, notGroupByTeamquestionTables.size());
        
        ______TS("GQR not grouped by team case - verify question table");
        
        InstructorFeedbackResultsQuestionTable notGroupByTeamQuestionTable = notGroupByTeamquestionTables.get(0);
        
        assertEquals(1, notGroupByTeamQuestionTable.getResponses().size());
        assertEquals("", notGroupByTeamQuestionTable.getAdditionalInfoText());
        assertEquals("panel-info", notGroupByTeamQuestionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue()), 
                                        notGroupByTeamQuestionTable.getQuestionText());
        verifyHtmlClass(notGroupByTeamQuestionTable.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        verifyColumns(notGroupByTeamQuestionTable.getColumns(), "Photo", "Recipient", "Team", "Feedback");
        verifyKeysOfMap(notGroupByTeamQuestionTable.getIsColumnSortable(),
                        Arrays.asList("Photo", "Recipient", "Team", "Feedback"));
        verifyTrueValuesInMap(notGroupByTeamQuestionTable.getIsColumnSortable(), 
                              Arrays.asList("Recipient", "Team", "Feedback"));
        verifyFalseValuesInMap(notGroupByTeamQuestionTable.getIsColumnSortable(), 
                               Arrays.asList("Photo"));
        
        assertEquals(1, notGroupByTeamQuestionTable.getResponses().size());
        InstructorFeedbackResultsResponseRow notGroupedByTeamResponseRow = notGroupByTeamQuestionTable.getResponses().get(0);
        verifyResponseRow(notGroupedByTeamResponseRow, dataBundle.feedbackResponses.get("response2ForQ1S1C1"), 
                                       dataBundle.students.get("student2InCourse1"), 
                                       dataBundle.students.get("student2InCourse1"));
        assertTrue(notGroupedByTeamResponseRow.isRecipientProfilePictureAColumn());
        assertFalse(notGroupedByTeamResponseRow.isGiverProfilePictureAColumn());
        
        InstructorFeedbackResultsModerationButton notGroupedByTeamModerationButton = notGroupedByTeamResponseRow.getModerationButton();
        verifyModerateButton(notGroupedByTeamModerationButton, "Moderate Response", dataBundle.students.get("student2InCourse1"), 
                             "First feedback session", 1);
        
        InstructorFeedbackResultsQuestionTable notGroupByTeamQuestionTable2 = notGroupByTeamquestionTables.get(1);
        assertEquals(1, notGroupByTeamQuestionTable2.getResponses().size());
        assertEquals("", notGroupByTeamQuestionTable2.getAdditionalInfoText());
        assertEquals("panel-info", notGroupByTeamQuestionTable2.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").questionMetaData.getValue()), 
                                        notGroupByTeamQuestionTable2.getQuestionText());
        assertEquals(1, notGroupByTeamQuestionTable2.getResponses().size());
        InstructorFeedbackResultsResponseRow notGroupedByTeamResponseRow2 = notGroupByTeamQuestionTable2.getResponses().get(0);
        verifyResponseRow(notGroupedByTeamResponseRow2, dataBundle.feedbackResponses.get("response1ForQ2S1C1"), 
                                                        dataBundle.students.get("student2InCourse1"), 
                                                        dataBundle.students.get("student1InCourse1"));
        
        ______TS("GQR ajax case: all sections, require loading by ajax");
        
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1, "giver-question-recipient");
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                      "on", "on", ViewType.GIVER_QUESTION_RECIPIENT);
        assertFalse(data.bundle.isComplete());
        
        Map<String, InstructorFeedbackResultsSectionPanel> ajaxQuestionPanels = data.getSectionPanels();
        assertEquals(3, ajaxQuestionPanels.size());
        
        verifyKeysOfMap(ajaxQuestionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        for (InstructorFeedbackResultsSectionPanel ajaxSectionPanel : ajaxQuestionPanels.values()) {
            verifyHtmlClass(ajaxSectionPanel.getPanelClass(), "panel-success");
            assertTrue(ajaxSectionPanel.isLoadSectionResponsesByAjax());
            assertEquals(0, ajaxSectionPanel.getParticipantPanels().size());
        }
    }

    private void verifyQuestionTableForGQR(InstructorFeedbackResultsQuestionTable firstQuestionTable, String dataQuestionId) {
        assertEquals("panel-info", firstQuestionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get(dataQuestionId).questionMetaData.getValue()), 
                                        firstQuestionTable.getQuestionText());
        verifyHtmlClass(firstQuestionTable.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        verifyColumns(firstQuestionTable.getColumns(), "Photo", "Recipient", "Team", "Feedback");
        verifyKeysOfMap(firstQuestionTable.getIsColumnSortable(),
                        Arrays.asList("Photo", "Recipient", "Team", "Feedback"));
        verifyTrueValuesInMap(firstQuestionTable.getIsColumnSortable(), 
                              Arrays.asList("Recipient", "Team", "Feedback"));
        verifyFalseValuesInMap(firstQuestionTable.getIsColumnSortable(), 
                               Arrays.asList("Photo"));
    }
    
    @Test
    public void testInitForViewByRQG() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("RQG typical case: view all sections, all sections, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "recipient-question-giver");
        
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                      "on", "on", ViewType.RECIPIENT_QUESTION_GIVER);
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels = data.getSectionPanels();
        verifyKeysOfMap(sectionPanels, Arrays.asList("Section 1", "Section 2", "None"));
        
        ______TS("RQG typical case - verify section 1");
        InstructorFeedbackResultsSectionPanel sectionPanel = sectionPanels.get("Section 1");
        assertTrue(sectionPanel.isDisplayingMissingParticipants());
        assertTrue(sectionPanel.isDisplayingTeamStatistics());
        assertFalse(sectionPanel.isLoadSectionResponsesByAjax());
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> sectionOneParticipantPanels = sectionPanel.getParticipantPanels();
        
        verifyKeysOfMap(sectionOneParticipantPanels, Arrays.asList("Team 1.1"));
        List<InstructorFeedbackResultsParticipantPanel> teamParticipantPanel = sectionOneParticipantPanels.get("Team 1.1");
        
        assertEquals("student1InCourse1@gmail.tmt", teamParticipantPanel.get(0).getParticipantIdentifier());
        assertFalse(teamParticipantPanel.get(0).isGiver());
        assertNull(teamParticipantPanel.get(0).getModerationButton());
        
        
        List<InstructorFeedbackResultsQuestionTable> questionPanels = ((InstructorFeedbackResultsGroupByQuestionPanel)teamParticipantPanel.get(0)).getQuestionTables();
        ______TS("RQG typical case - verify question panel 1");
        InstructorFeedbackResultsQuestionTable questionPanel = questionPanels.get(0);
        
        assertEquals("What is the best selling point of your product?", questionPanel.getQuestionText());
        assertEquals("panel-info", questionPanel.getPanelClass());

        verifyColumns(questionPanel.getColumns(), "Photo", "Giver", "Team", "Feedback", "Actions");
        verifyKeysOfMap(questionPanel.getIsColumnSortable(), Arrays.asList("Photo", "Giver", "Team", "Feedback", "Actions"));
        verifyTrueValuesInMap(questionPanel.getIsColumnSortable(), 
                              Arrays.asList("Giver", "Team", "Feedback"));
        verifyFalseValuesInMap(questionPanel.getIsColumnSortable(), 
                               Arrays.asList("Photo", "Actions"));

        verifyHtmlClass(questionPanel.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        List<InstructorFeedbackResultsResponseRow> responseRows =  questionPanel.getResponses();
        assertEquals(1, responseRows.size());
        InstructorFeedbackResultsResponseRow firstResponseRow = responseRows.get(0);
        verifyResponseRow(firstResponseRow, dataBundle.feedbackResponses.get("response1ForQ1S1C1"), 
                          dataBundle.students.get("student1InCourse1"), dataBundle.students.get("student1InCourse1"));
        
        ______TS("RQG typical case - verify question panel 2");
        questionPanel = questionPanels.get(1);
        assertEquals(Sanitizer.sanitizeForHtml("Rate 1 other student's product"),
                     questionPanel.getQuestionText());
        assertEquals("panel-info", questionPanel.getPanelClass());

        responseRows = questionPanel.getResponses();
        assertEquals(1, responseRows.size());
        firstResponseRow = responseRows.get(0);
        verifyResponseRow(firstResponseRow, dataBundle.feedbackResponses.get("response1ForQ2S1C1"), 
                          dataBundle.students.get("student2InCourse1"), dataBundle.students.get("student1InCourse1"));
        
        
        ______TS("RQG single section case : view section 1, all questions, no stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = "Section 1";
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorToSectionWithinRange(data.feedbackSessionName, data.courseId,
                                                                                  data.instructor.email, data.selectedSection, 1000);
        data.initForSectionPanelViews(instructor, "Section 1",
                                      null, "on", ViewType.RECIPIENT_QUESTION_GIVER);
        
        Map<String, InstructorFeedbackResultsSectionPanel> singleSectionSectionPanels = data.getSectionPanels();
        verifyKeysOfMap(singleSectionSectionPanels, Arrays.asList("Section 1"));
        
        ______TS("RQG single section case - verify section panel");
        InstructorFeedbackResultsSectionPanel singleSectionSectionPanel = singleSectionSectionPanels.get("Section 1");
        assertEquals("Section 1", singleSectionSectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", singleSectionSectionPanel.getDetailedResponsesHeaderText());

        verifyKeysOfMap(singleSectionSectionPanel.getIsTeamWithResponses(), Arrays.asList("Team 1.1"));
        assertEquals("panel-success", singleSectionSectionPanel.getPanelClass());
        assertEquals("Received Responses Statistics", singleSectionSectionPanel.getStatisticsHeaderText());
        
        assertTrue(singleSectionSectionPanel.isDisplayingTeamStatistics()); // note that this is still true because
                                                               // displaying stats are done in javascript
      
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> singleSectionStudentPanel = singleSectionSectionPanel.getParticipantPanels();
        InstructorFeedbackResultsGroupByQuestionPanel studentPanel = (InstructorFeedbackResultsGroupByQuestionPanel) singleSectionStudentPanel.get("Team 1.1").get(0);
        List<InstructorFeedbackResultsQuestionTable> singleSectionQuestionTables = studentPanel.getQuestionTables();
        assertEquals(2, singleSectionQuestionTables.size());
        
        ______TS("RQG single section case - verify question table");
        InstructorFeedbackResultsQuestionTable singleSectionQuestionTable = singleSectionQuestionTables.get(0);
        
        assertEquals(1, singleSectionQuestionTable.getResponses().size());
        assertEquals("", singleSectionQuestionTable.getAdditionalInfoText());
        assertEquals("panel-info", singleSectionQuestionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue()), 
                                        singleSectionQuestionTable.getQuestionText());
        verifyHtmlClass(singleSectionQuestionTable.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        verifyColumns(singleSectionQuestionTable.getColumns(), "Photo", "Giver", "Team", "Feedback", "Actions");
        verifyKeysOfMap(singleSectionQuestionTable.getIsColumnSortable(),
                        Arrays.asList("Photo", "Giver", "Team", "Feedback", "Actions"));
        verifyTrueValuesInMap(singleSectionQuestionTable.getIsColumnSortable(), 
                               Arrays.asList("Giver", "Team", "Feedback"));
        verifyFalseValuesInMap(singleSectionQuestionTable.getIsColumnSortable(), 
                               Arrays.asList("Photo", "Actions"));
        
        assertEquals(1, singleSectionQuestionTable.getResponses().size());
        InstructorFeedbackResultsResponseRow singleSectionResponseRow = singleSectionQuestionTable.getResponses().get(0);
        verifyResponseRow(singleSectionResponseRow, dataBundle.feedbackResponses.get("response1ForQ1S1C1"), 
                                                    dataBundle.students.get("student1InCourse1"), 
                                                    dataBundle.students.get("student1InCourse1"));
        assertFalse(singleSectionResponseRow.isRecipientProfilePictureAColumn());
        assertTrue(singleSectionResponseRow.isGiverProfilePictureAColumn());
        
        ______TS("RQG not grouped by team case: all sections, not grouping by team");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions
                                             .get("session1InCourse1")
                                             .feedbackSessionName;
        
        data.showStats = "on";
        data.groupByTeam = null;
        data.sortType = "recipient-question-giver";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "recipient-question-giver");
        
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                      "on", null, ViewType.RECIPIENT_QUESTION_GIVER);
        
        Map<String, InstructorFeedbackResultsSectionPanel> notGroupByTeamPanels = data.getSectionPanels();
        verifyKeysOfMap(notGroupByTeamPanels, Arrays.asList("Section 1", "Section 2", "None"));
        
        ______TS("RQG not grouped by team case - verify section panel");
        InstructorFeedbackResultsSectionPanel notGroupedByTeamSectionPanel = notGroupByTeamPanels.get("Section 1");
        
        List<InstructorFeedbackResultsParticipantPanel> notGroupByTeamParticipantPanels = notGroupedByTeamSectionPanel.getParticipantPanelsInSortedOrder();
        assertEquals(4, notGroupByTeamParticipantPanels.size());
        
        List<String> nameList = Arrays.asList("student1 In Course1", "student2 In Course1", 
                                              "student3 In Course1", "student4 In Course1");
        
        for (int i = 0; i < nameList.size(); i++) {
            String nameOfStudent = nameList.get(i);
            InstructorFeedbackResultsGroupByQuestionPanel notGroupByTeamParticipantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(i);
            String sectionPanelStudentName = notGroupByTeamParticipantPanel.getName();
            
            assertEquals(nameOfStudent, sectionPanelStudentName);
        }
        
        InstructorFeedbackResultsGroupByQuestionPanel notGroupedByTeamStudentPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(0);
        assertNull(notGroupedByTeamStudentPanel.getModerationButton());
                
        List<InstructorFeedbackResultsQuestionTable> notGroupedByTeamQuestionTables = notGroupedByTeamStudentPanel.getQuestionTables();
        assertEquals(2, notGroupedByTeamQuestionTables.size());
        
        InstructorFeedbackResultsQuestionTable notGroupedByTeamQuestionTable = notGroupedByTeamQuestionTables.get(0);
        
        assertEquals(1, notGroupedByTeamQuestionTable.getResponses().size());
        assertEquals("", notGroupedByTeamQuestionTable.getAdditionalInfoText());
        
        verifyQuestionTableForRQG(notGroupedByTeamQuestionTable, "qn1InSession1InCourse1");
      
        
        InstructorFeedbackResultsResponseRow notGroupedByTeamResponseRow = notGroupedByTeamQuestionTable.getResponses().get(0);
        verifyResponseRow(notGroupedByTeamResponseRow, dataBundle.feedbackResponses.get("response1ForQ1S1C1"), 
                          dataBundle.students.get("student1InCourse1"), dataBundle.students.get("student1InCourse1"));
        assertFalse(notGroupedByTeamResponseRow.isRecipientProfilePictureAColumn());
        assertTrue(notGroupedByTeamResponseRow.isGiverProfilePictureAColumn());

        
        ______TS("RQG ajax case: all sections, require loading by ajax");
        
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        // force load by ajax by setting the responses range to 1
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1, "recipient-question-giver");
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                        "on", "on", ViewType.RECIPIENT_QUESTION_GIVER);
        assertFalse(data.bundle.isComplete());
        
        Map<String, InstructorFeedbackResultsSectionPanel> ajaxQuestionPanels = data.getSectionPanels();
        assertEquals(3, ajaxQuestionPanels.size());
        
        verifyKeysOfMap(ajaxQuestionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        
        for (InstructorFeedbackResultsSectionPanel ajaxSectionPanel : ajaxQuestionPanels.values()) {
            verifyHtmlClass(ajaxSectionPanel.getPanelClass(), "panel-success");
            assertTrue(ajaxSectionPanel.isLoadSectionResponsesByAjax());
            assertEquals(0, ajaxSectionPanel.getParticipantPanels().size());
        }
        
    }
    
    private void verifyQuestionTableForRQG(InstructorFeedbackResultsQuestionTable questionPanel, String dataQuestionKey) {
        assertEquals("panel-info", questionPanel.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get(dataQuestionKey).questionMetaData.getValue()), 
                                        questionPanel.getQuestionText());
        verifyHtmlClass(questionPanel.getResponsesBodyClass(), "panel-collapse", "collapse", "in");
        
        verifyColumns(questionPanel.getColumns(), "Photo", "Giver", "Team", "Feedback", "Actions");
        verifyKeysOfMap(questionPanel.getIsColumnSortable(),
                        Arrays.asList("Photo", "Giver", "Team", "Feedback", "Actions"));
        verifyTrueValuesInMap(questionPanel.getIsColumnSortable(), 
                              Arrays.asList("Giver", "Team", "Feedback"));
        verifyFalseValuesInMap(questionPanel.getIsColumnSortable(), 
                               Arrays.asList("Photo", "Actions"));
    }
    
    private void verifyResponseRow(InstructorFeedbackResultsResponseRow responseRow, FeedbackResponseAttributes response, 
                                  StudentAttributes studentGiver, StudentAttributes studentRecipient) {
        
        assertEquals(studentGiver.name, responseRow.getGiverDisplayableIdentifier());
        assertEquals(studentRecipient.name, responseRow.getRecipientDisplayableIdentifier());
        assertEquals(studentGiver.team, responseRow.getGiverTeam());
        assertEquals(studentRecipient.team, responseRow.getRecipientTeam());
        
        if (response != null) {
            assertEquals(Sanitizer.sanitizeForHtml(response.getResponseDetails().getAnswerString()), responseRow.getDisplayableResponse());
            assertEquals(null, responseRow.getRowAttributes());
        } else {
            assertEquals("<i>No Response</i>", responseRow.getDisplayableResponse());
            assertEquals(" class=\"pending_response_row\"", responseRow.getRowAttributes().getAttributesToString());
        }
        
        if (responseRow.isGiverDisplayed()) {
            assertTrue(responseRow.getGiverProfilePictureLink().toString().contains(StringHelper.encrypt(studentGiver.email)));
            assertTrue(responseRow.getGiverProfilePictureLink().toString().contains(StringHelper.encrypt(studentGiver.course)));
        }
        
        if (responseRow.isRecipientDisplayed()) {
            assertTrue(responseRow.getRecipientProfilePictureLink().toString().contains(StringHelper.encrypt(studentRecipient.email)));
            assertTrue(responseRow.getRecipientProfilePictureLink().toString().contains(StringHelper.encrypt(studentRecipient.course)));
        }
        
    }
    
    
    @Test
    public void testInitForViewByRGQ() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("RGQ typical case: view all sections");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-giver-question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "recipient-giver-question");
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                        "on", "on", ViewType.RECIPIENT_GIVER_QUESTION);
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels = data.getSectionPanels();
        
        verifyKeysOfMap(sectionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        
        ______TS("RGQ typical case - verify that section panel for 'Not in a section' is correct");
        InstructorFeedbackResultsSectionPanel noneSectionPanel = sectionPanels.get("None");
        
        assertEquals("panel-success", noneSectionPanel.getPanelClass());
        assertEquals(NOT_IN_A_SECTION, noneSectionPanel.getSectionNameForDisplay());
        assertEquals(Const.DEFAULT_SECTION, noneSectionPanel.getSectionName());
        
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> noneSectionprimaryParticipantPanelsMap = noneSectionPanel.getParticipantPanels();
        assertEquals(1, noneSectionprimaryParticipantPanelsMap.size());
        verifyKeysOfMap(noneSectionprimaryParticipantPanelsMap, Arrays.asList("-"));
        List<InstructorFeedbackResultsParticipantPanel> noneSectionPrimaryParticipantPanels = noneSectionprimaryParticipantPanelsMap.get("-");
        
        InstructorFeedbackResultsGroupByParticipantPanel noneSectionPrimaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)noneSectionPrimaryParticipantPanels.get(0);
        
        assertEquals("-", noneSectionPrimaryParticipantPanel.getName());
        assertEquals(Const.GENERAL_QUESTION, noneSectionPrimaryParticipantPanel.getParticipantIdentifier());
        assertFalse(noneSectionPrimaryParticipantPanel.isEmailValid());
        
        
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> noneSectionSecondaryParticipantPanels = noneSectionPrimaryParticipantPanel.getSecondaryParticipantPanels();
        InstructorFeedbackResultsSecondaryParticipantPanelBody noneSectionSecondaryParticipantPanel = noneSectionSecondaryParticipantPanels.get(0);
        
        assertEquals("instructor1@course1.tmt", noneSectionSecondaryParticipantPanel.getSecondaryParticipantIdentifier());
        assertEquals("Instructor1 Course1 (Instructors)", noneSectionSecondaryParticipantPanel.getSecondaryParticipantDisplayableName());
        
        // test response panels
        List<InstructorFeedbackResultsResponsePanel> noneSectionResponsePanels = noneSectionSecondaryParticipantPanel.getResponsePanels();
        
        List<String> expectedResponsesId = Arrays.asList("response1ForQ3S1C1");
        List<String> expectedQuestionsId = Arrays.asList("qn3InSession1InCourse1");
        verifyResponsePanels(noneSectionResponsePanels, expectedResponsesId, expectedQuestionsId);
        
        
        ______TS("RGQ typical case - verify that that section 1 is correct");
        InstructorFeedbackResultsSectionPanel firstSectionPanel = sectionPanels.get("Section 1");
        
        assertEquals("panel-success", firstSectionPanel.getPanelClass());
        assertEquals("Section 1", firstSectionPanel.getSectionNameForDisplay());
        assertEquals("Section 1", firstSectionPanel.getSectionName());
        
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> primaryParticipantPanelsMap = firstSectionPanel.getParticipantPanels();
        assertEquals(1, primaryParticipantPanelsMap.size());
        verifyKeysOfMap(primaryParticipantPanelsMap, Arrays.asList("Team 1.1"));
        
        List<InstructorFeedbackResultsParticipantPanel> primaryParticipantPanels = primaryParticipantPanelsMap.get("Team 1.1");
        
        InstructorFeedbackResultsGroupByParticipantPanel primaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)primaryParticipantPanels.get(0);
        
        assertEquals("student1 In Course1 (Team 1.1)", primaryParticipantPanel.getName());
        assertEquals("student1InCourse1@gmail.tmt", primaryParticipantPanel.getParticipantIdentifier());
        assertEquals(data.getProfilePictureLink("student1InCourse1@gmail.tmt"), primaryParticipantPanel.getProfilePictureLink());
        
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels = primaryParticipantPanel.getSecondaryParticipantPanels();
        InstructorFeedbackResultsSecondaryParticipantPanelBody secondaryParticipantPanel = secondaryParticipantPanels.get(0);
        
        assertEquals("student1InCourse1@gmail.tmt", secondaryParticipantPanel.getSecondaryParticipantIdentifier());
        assertEquals("student1 In Course1 (Team 1.1)", secondaryParticipantPanel.getSecondaryParticipantDisplayableName());
        
        // test response panels
        List<InstructorFeedbackResultsResponsePanel> responsePanels = secondaryParticipantPanel.getResponsePanels();
        
        expectedResponsesId = Arrays.asList("response1ForQ1S1C1", "response1ForQ2S1C1");
        expectedQuestionsId = Arrays.asList("qn1InSession1InCourse1", "qn2InSession1InCourse1");
        verifyResponsePanels(responsePanels, expectedResponsesId, expectedQuestionsId);
       
        ______TS("RGQ typical case - verify that section 2 is correct");
        
        InstructorFeedbackResultsSectionPanel secondSectionPanel = sectionPanels.get("Section 2");
        
        assertEquals("panel-success", secondSectionPanel.getPanelClass());
        assertEquals("Section 2", secondSectionPanel.getSectionNameForDisplay());
        assertEquals("Section 2", secondSectionPanel.getSectionName());
        
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> secondprimaryParticipantPanelsMap = secondSectionPanel.getParticipantPanels();
        assertEquals(1, secondprimaryParticipantPanelsMap.size());
        verifyKeysOfMap(secondprimaryParticipantPanelsMap, Arrays.asList("Team 1.2"));
        
        List<InstructorFeedbackResultsParticipantPanel> secondPrimaryParticipantPanels = secondprimaryParticipantPanelsMap.get("Team 1.2");
        
        InstructorFeedbackResultsGroupByParticipantPanel secondPrimaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)secondPrimaryParticipantPanels.get(0);
        
        assertEquals("student5 In Course1 (Team 1.2)", secondPrimaryParticipantPanel.getName());
        assertEquals("student5InCourse1@gmail.tmt", secondPrimaryParticipantPanel.getParticipantIdentifier());
        assertEquals(data.getProfilePictureLink("student5InCourse1@gmail.tmt"), secondPrimaryParticipantPanel.getProfilePictureLink());
        
        assertTrue(secondPrimaryParticipantPanel.getSecondaryParticipantPanels().isEmpty());
        
        
        ______TS("RGQ single section case: view section 1, all questions");
        data = new InstructorFeedbackResultsPageData(account);
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "recipient-giver-question";
        data.selectedSection = "Section 1";
        
        data.setBundle(logic.getFeedbackSessionResultsForInstructorToSectionWithinRange(
                                        data.feedbackSessionName, data.courseId,
                                        data.instructor.email, data.selectedSection, 1000));
        data.initForSectionPanelViews(instructor, "Section 1",
                                      null, "on", ViewType.RECIPIENT_GIVER_QUESTION);
        
        Map<String, InstructorFeedbackResultsSectionPanel> singleSectionSectionPanels = data.getSectionPanels();
        
        verifyKeysOfMap(singleSectionSectionPanels, Arrays.asList("Section 1"));
        InstructorFeedbackResultsSectionPanel singleSectionSectionPanel = singleSectionSectionPanels.get("Section 1");
        
        assertEquals("panel-success", singleSectionSectionPanel.getPanelClass());
        assertEquals("Section 1", singleSectionSectionPanel.getSectionNameForDisplay());
        assertEquals("Section 1", singleSectionSectionPanel.getSectionName());
                
        
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> singleSectionPrimaryParticipantPanelsMap 
                                                 = singleSectionSectionPanel.getParticipantPanels();
        assertEquals(1, singleSectionPrimaryParticipantPanelsMap.size());
        verifyKeysOfMap(singleSectionPrimaryParticipantPanelsMap, Arrays.asList("Team 1.1"));
        
        List<InstructorFeedbackResultsParticipantPanel> singleSectionPrimaryParticipantPanels = singleSectionPrimaryParticipantPanelsMap.get("Team 1.1");
        
        InstructorFeedbackResultsGroupByParticipantPanel singleSectionPrimaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)singleSectionPrimaryParticipantPanels.get(0);
        
        assertEquals("student1 In Course1 (Team 1.1)", singleSectionPrimaryParticipantPanel.getName());
        assertEquals("student1InCourse1@gmail.tmt", singleSectionPrimaryParticipantPanel.getParticipantIdentifier());
        assertEquals(data.getProfilePictureLink("student1InCourse1@gmail.tmt"), singleSectionPrimaryParticipantPanel.getProfilePictureLink());
        
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> singleSectionSecondaryParticipantPanels = singleSectionPrimaryParticipantPanel.getSecondaryParticipantPanels();
        InstructorFeedbackResultsSecondaryParticipantPanelBody singleSectionSecondaryParticipantPanel = singleSectionSecondaryParticipantPanels.get(0);
        
        assertEquals("student1InCourse1@gmail.tmt", singleSectionSecondaryParticipantPanel.getSecondaryParticipantIdentifier());
        assertEquals("student1 In Course1 (Team 1.1)", singleSectionSecondaryParticipantPanel.getSecondaryParticipantDisplayableName());
        
        // test response panels
        List<InstructorFeedbackResultsResponsePanel> singleSectionResponsePanels = singleSectionSecondaryParticipantPanel.getResponsePanels();
        
        expectedResponsesId = Arrays.asList("response1ForQ1S1C1", "response1ForQ2S1C1");
        expectedQuestionsId = Arrays.asList("qn1InSession1InCourse1", "qn2InSession1InCourse1");
        verifyResponsePanels(singleSectionResponsePanels, expectedResponsesId, expectedQuestionsId);
        
       
        ______TS("RGQ case : all sections, not grouping by team");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions
                                             .get("session1InCourse1")
                                             .feedbackSessionName;
        
        data.showStats = "on";
        data.groupByTeam = null;
        data.sortType = "recipient-giver-question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "recipient-giver-question");
        
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                      "on", null, ViewType.RECIPIENT_GIVER_QUESTION);
        
        Map<String, InstructorFeedbackResultsSectionPanel> notGroupedByTeamSectionPanels = data.getSectionPanels();
        
        verifyKeysOfMap(notGroupedByTeamSectionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        
        
        ______TS("RGQ all section not grouped by team case - verify that section panel for 'Not in a section' is correct");
        InstructorFeedbackResultsSectionPanel notGroupedByTeamNoneSectionPanel = sectionPanels.get("None");
        
        assertEquals("panel-success", notGroupedByTeamNoneSectionPanel.getPanelClass());
        assertEquals(NOT_IN_A_SECTION, notGroupedByTeamNoneSectionPanel.getSectionNameForDisplay());
        assertEquals("None", notGroupedByTeamNoneSectionPanel.getSectionName());
        
        Map<String, List<InstructorFeedbackResultsParticipantPanel>> notGroupedByTeamNoneSectionPrimaryParticipantPanelsMap = notGroupedByTeamNoneSectionPanel.getParticipantPanels();
        assertEquals(1, notGroupedByTeamNoneSectionPrimaryParticipantPanelsMap.size());
        verifyKeysOfMap(notGroupedByTeamNoneSectionPrimaryParticipantPanelsMap, Arrays.asList("-"));
        List<InstructorFeedbackResultsParticipantPanel> notGroupedByTeamNoneSectionPrimaryParticipantPanels = notGroupedByTeamNoneSectionPrimaryParticipantPanelsMap.get("-");
        
        InstructorFeedbackResultsGroupByParticipantPanel notGroupedByTeamNoneSectionPrimaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)notGroupedByTeamNoneSectionPrimaryParticipantPanels.get(0);
        
        assertEquals("-", notGroupedByTeamNoneSectionPrimaryParticipantPanel.getName());
        assertEquals(Const.GENERAL_QUESTION, notGroupedByTeamNoneSectionPrimaryParticipantPanel.getParticipantIdentifier());
        assertFalse(notGroupedByTeamNoneSectionPrimaryParticipantPanel.isEmailValid());
        
        
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> notGroupedByTeamNoneSectionSecondaryParticipantPanels = notGroupedByTeamNoneSectionPrimaryParticipantPanel.getSecondaryParticipantPanels();
        InstructorFeedbackResultsSecondaryParticipantPanelBody notGroupedByTeamNoneSectionSecondaryParticipantPanel = notGroupedByTeamNoneSectionSecondaryParticipantPanels.get(0);
        
        assertEquals("instructor1@course1.tmt", notGroupedByTeamNoneSectionSecondaryParticipantPanel.getSecondaryParticipantIdentifier());
        assertEquals("Instructor1 Course1 (Instructors)", notGroupedByTeamNoneSectionSecondaryParticipantPanel.getSecondaryParticipantDisplayableName());
        
        // test response panels
        List<InstructorFeedbackResultsResponsePanel> notGroupedByTeamNoneSectionResponsePanels = notGroupedByTeamNoneSectionSecondaryParticipantPanel.getResponsePanels();
        
        expectedResponsesId = Arrays.asList("response1ForQ3S1C1");
        expectedQuestionsId = Arrays.asList("qn3InSession1InCourse1");
        verifyResponsePanels(notGroupedByTeamNoneSectionResponsePanels, expectedResponsesId, expectedQuestionsId);
        
        
        ______TS("RGQ all section not grouped by team case - verify that that section 1 is correct");
        InstructorFeedbackResultsSectionPanel notGroupedByTeamFirstSectionPanel = notGroupedByTeamSectionPanels.get("Section 1");
        
        assertEquals("panel-success", notGroupedByTeamFirstSectionPanel.getPanelClass());
        assertEquals("Section 1", notGroupedByTeamFirstSectionPanel.getSectionNameForDisplay());
        assertEquals("Section 1", notGroupedByTeamFirstSectionPanel.getSectionName());
        
        List<InstructorFeedbackResultsParticipantPanel> notGroupedByTeamPrimaryParticipantPanels = notGroupedByTeamFirstSectionPanel.getParticipantPanelsInSortedOrder();
        
        InstructorFeedbackResultsGroupByParticipantPanel notGroupedByTeamPrimaryParticipantPanel 
            = (InstructorFeedbackResultsGroupByParticipantPanel)notGroupedByTeamPrimaryParticipantPanels.get(0);
        
        assertEquals("student1 In Course1 (Team 1.1)", notGroupedByTeamPrimaryParticipantPanel.getName());
        assertEquals("student1InCourse1@gmail.tmt", notGroupedByTeamPrimaryParticipantPanel.getParticipantIdentifier());
        assertEquals(data.getProfilePictureLink("student1InCourse1@gmail.tmt"), notGroupedByTeamPrimaryParticipantPanel.getProfilePictureLink());
        
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> notGroupedByTeamSecondaryParticipantPanels = notGroupedByTeamPrimaryParticipantPanel.getSecondaryParticipantPanels();
        InstructorFeedbackResultsSecondaryParticipantPanelBody notGroupedByTeamSecondaryParticipantPanel = notGroupedByTeamSecondaryParticipantPanels.get(0);
        
        assertEquals("student1InCourse1@gmail.tmt", notGroupedByTeamSecondaryParticipantPanel.getSecondaryParticipantIdentifier());
        assertEquals("student1 In Course1 (Team 1.1)", notGroupedByTeamSecondaryParticipantPanel.getSecondaryParticipantDisplayableName());
        
        // test response panels
        List<InstructorFeedbackResultsResponsePanel> notGroupedByTeamResponsePanels = notGroupedByTeamSecondaryParticipantPanel.getResponsePanels();
        
        expectedResponsesId = Arrays.asList("response1ForQ1S1C1", "response1ForQ2S1C1");
        expectedQuestionsId = Arrays.asList("qn1InSession1InCourse1", "qn2InSession1InCourse1");
        verifyResponsePanels(notGroupedByTeamResponsePanels, expectedResponsesId, expectedQuestionsId);
        
        
        ______TS("RGQ case : all sections, require loading by ajax");
        data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-giver-question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1, "recipient-giver-question");
        
        data.initForSectionPanelViews(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION,
                                        "on", "on", ViewType.RECIPIENT_GIVER_QUESTION);
        assertFalse(data.bundle.isComplete());
        
        Map<String, InstructorFeedbackResultsSectionPanel> ajaxPanels = data.getSectionPanels();
        assertEquals(3, ajaxPanels.size());
        
        verifyKeysOfMap(ajaxPanels, Arrays.asList("None", "Section 1", "Section 2"));
        
        for (InstructorFeedbackResultsSectionPanel ajaxSectionPanel : ajaxPanels.values()) {
            verifyHtmlClass(ajaxSectionPanel.getPanelClass(), "panel-success");
            assertTrue(ajaxSectionPanel.isLoadSectionResponsesByAjax());
            assertEquals(0, ajaxSectionPanel.getParticipantPanels().size());
        } 
    }

    private void verifyResponsePanels(List<InstructorFeedbackResultsResponsePanel> responsePanels,
                                    List<String> expectedResponsesId, List<String> expectedQuestionsId) {
        for (int i = 0; i < responsePanels.size(); i++) {
            FeedbackResponseAttributes expectedResponse = dataBundle.feedbackResponses.get(expectedResponsesId.get(i));
            FeedbackQuestionAttributes expectedQuestion = dataBundle.feedbackQuestions.get(expectedQuestionsId.get(i));
            
            InstructorFeedbackResultsResponsePanel responsePanel = responsePanels.get(i);
            assertEquals(expectedResponse.responseMetaData.getValue() , responsePanel.getDisplayableResponse());
            assertEquals(expectedQuestion.getQuestionDetails().getQuestionText(), responsePanel.getQuestionText());
            
            assertNull(responsePanel.getRowAttributes());
        }
    }
    
    private <K, V> void verifyKeysOfMap(Map<K, V> map, List<K> keys) {
        Set<K> keysOfMap = map.keySet();
        Set<K> keysSet = new HashSet<K>(keys);
        assertEquals(keysSet, keysOfMap);
    }
    
    private <K> void verifyTrueValuesInMap(Map<K, Boolean> map, List<K> keys) {
        Set<K> trueValuesInMap = new HashSet<>();
        for (Map.Entry<K, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                trueValuesInMap.add(entry.getKey());
            }
        }
        
        Set<K> expectedValues = new HashSet<>(keys);
        assertEquals(expectedValues, trueValuesInMap);
    }
    
    private <K> void verifyFalseValuesInMap(Map<K, Boolean> map, List<K> keys) {
        Set<K> falseValuesInMap = new HashSet<>();
        for (Map.Entry<K, Boolean> entry : map.entrySet()) {
            if (!entry.getValue()) {
                falseValuesInMap.add(entry.getKey());
            }
        }
        
        Set<K> expectedValues = new HashSet<>(keys);
        assertEquals(expectedValues, falseValuesInMap);
    }
    
    private void verifyColumns(List<ElementTag> columns, String... expectedColumnNames) {
        assertEquals(expectedColumnNames.length, columns.size());
        for (int i = 0; i < columns.size(); i++) {
            assertEquals(expectedColumnNames[i], columns.get(i).getContent());
        }
    }
    
    private void verifyHtmlClass(String actualHtmlClasses, String... expectedHtmlClasses) {
        for (String expectedHtmlClass : expectedHtmlClasses) {
            assertTrue(actualHtmlClasses.matches(".*\\b" + expectedHtmlClass + "\\b.*"));
        }
    }
}
