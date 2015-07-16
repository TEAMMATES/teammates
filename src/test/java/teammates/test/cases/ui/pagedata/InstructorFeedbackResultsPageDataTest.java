package teammates.test.cases.ui.pagedata;

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

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Sanitizer;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.InstructorFeedbackResultsPageData;
import teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel;
import teammates.ui.template.InstructorFeedbackResultsSectionPanel;
import teammates.ui.template.InstructorResultsModerationButton;
import teammates.ui.template.InstructorResultsParticipantPanel;
import teammates.ui.template.InstructorResultsQuestionTable;
import teammates.ui.template.InstructorResultsResponseRow;

public class InstructorFeedbackResultsPageDataTest extends BaseComponentTestCase {
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
        
        List<InstructorResultsQuestionTable> questionPanels = data.getQuestionPanels();
        assertEquals(4, questionPanels.size());
        
        // Verify that the first question table is correct
        InstructorResultsQuestionTable questionPanel = questionPanels.get(0);
        assertEquals("", questionPanel.getAdditionalInfoText());
        assertEquals("panel-info", questionPanel.getPanelClass());
        assertEquals(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue(), 
                                        questionPanel.getQuestionText());
        assertTrue(questionPanel.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(questionPanel.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        assertTrue(questionPanel.getResponsesBodyClass().matches(".*\\bin\\b.*"));
        assertEquals(6, questionPanel.getColumns().size());
        assertTrue(isKeysOfMap(questionPanel.getIsColumnSortable(),
                               Arrays.asList("Giver", "Team", "Recipient", "Feedback", "Actions")));
        assertTrue(isValuesInMapTrue(questionPanel.getIsColumnSortable(), 
                                     Arrays.asList("Giver", "Team", "Recipient", "Feedback")));
        assertTrue(isValuesInMapFalse(questionPanel.getIsColumnSortable(), 
                                        Arrays.asList("Actions")));
        
        List<InstructorResultsResponseRow> responseRows = questionPanel.getResponses();
        assertEquals(5, responseRows.size());
        // first response
        InstructorResultsResponseRow responseRow = responseRows.get(0);
        assertEquals("Student 1 self feedback.", responseRow.getDisplayableResponse());
        
        assertEquals("Team 1.1", responseRow.getGiverTeam());
        assertEquals("student1 In Course1", responseRow.getGiverDisplayableIdentifier());
        assertEquals("student1 In Course1", responseRow.getRecipientDisplayableIdentifier());
        assertEquals("Team 1.1", responseRow.getRecipientTeam());
        
        assertEquals(null, responseRow.getRowAttributes());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertFalse(responseRow.isRecipientProfilePictureAColumn());
        assertTrue(responseRow.isGiverDisplayed());
        assertTrue(responseRow.isRecipientDisplayed());
        assertTrue(responseRow.isModerationsButtonDisplayed());
        
        InstructorResultsModerationButton modButton = responseRow.getModerationButton();
        assertEquals("Moderate Response", modButton.getButtonText());
        assertTrue(modButton.getClassName().matches(".*\\bbtn\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-default\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-xs\\b.*"));
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals("student1InCourse1@gmail.tmt", modButton.getGiverIdentifier());
        assertEquals(1, modButton.getQuestionNumber());
        assertTrue(modButton.isAllowedToModerate());
        assertFalse(modButton.isDisabled());
        
        //missing response
        responseRow = responseRows.get(responseRows.size() - 1);
        assertEquals("<i>No Response</i>", responseRow.getDisplayableResponse());
        
        assertEquals("Team 1.2", responseRow.getGiverTeam());
        assertEquals("student5 In Course1", responseRow.getGiverDisplayableIdentifier());
        assertEquals("student5 In Course1", responseRow.getRecipientDisplayableIdentifier());
        assertEquals("Team 1.2",responseRow.getRecipientTeam());
        
        assertEquals("pending_response_row", responseRow.getRowAttributes().getAttributes().get("class"));
        
        modButton = responseRow.getModerationButton();
        assertEquals("Moderate Response", modButton.getButtonText());
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals("student5InCourse1@gmail.tmt", modButton.getGiverIdentifier());
        assertEquals(1, modButton.getQuestionNumber());
        assertTrue(modButton.isAllowedToModerate());
        assertFalse(modButton.isDisabled());
        
        // Verify the fourth question, which has giver type=instructor, cannot be moderated
        //TODO bug see #
        questionPanel = questionPanels.get(3);
        responseRows = questionPanel.getResponses();
        modButton = responseRows.get(0).getModerationButton();
        //assertFalse(responseRows.get(0).isModerationButtonDisplayed());
        //assertTrue(modButton.isAllowedToModerate());
        
        
        ______TS("view section 1, all questions");
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
        
        List<InstructorResultsQuestionTable> sectionQuestionPanels = data.getQuestionPanels();
        assertEquals(4, sectionQuestionPanels.size());
        
        InstructorResultsQuestionTable sectionQuestionPanel = sectionQuestionPanels.get(1);
        assertTrue(sectionQuestionPanel.getQuestionStatisticsTable().isEmpty());
        
        List<InstructorResultsResponseRow> sectionResponseRows = sectionQuestionPanel.getResponses();
        assertEquals(3, sectionResponseRows.size());
        InstructorResultsResponseRow sectionResponseRow = sectionResponseRows.get(2);
        // TODO sanitization issue
        assertEquals("Response from student 3 &quot;to&quot; student 2.\r\nMultiline test.", sectionResponseRow.getDisplayableResponse());
        
        
        ______TS("all sections, question 2");
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
            
        List<InstructorResultsQuestionTable> singleQuestionPanelList = data.getQuestionPanels();
        assertEquals(1, singleQuestionPanelList.size());
        
        InstructorResultsQuestionTable singleQuestionPanel = singleQuestionPanelList.get(0);
        assertEquals("", singleQuestionPanel.getAdditionalInfoText());
        assertEquals("panel-info", singleQuestionPanel.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").questionMetaData.getValue()), 
                                        singleQuestionPanel.getQuestionText());
        assertTrue(singleQuestionPanel.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(singleQuestionPanel.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        assertTrue(singleQuestionPanel.getResponsesBodyClass().matches(".*\\bin\\b.*"));
        assertEquals(6, singleQuestionPanel.getColumns().size());
        assertTrue(isKeysOfMap(singleQuestionPanel.getIsColumnSortable(),
                               Arrays.asList("Giver", "Team", "Recipient", "Feedback", "Actions")));
        assertTrue(isValuesInMapTrue(singleQuestionPanel.getIsColumnSortable(), 
                                     Arrays.asList("Giver", "Team", "Recipient", "Feedback")));
        assertTrue(isValuesInMapFalse(singleQuestionPanel.getIsColumnSortable(), 
                                      Arrays.asList("Actions")));
        
        List<InstructorResultsResponseRow> singleQuestionResponseRows = singleQuestionPanel.getResponses();
        assertEquals(3, singleQuestionResponseRows.size());
        // just verify any response
        InstructorResultsResponseRow singleQuestionResponseRow = singleQuestionResponseRows.get(1);
        assertEquals("Response from student 2 to student 1.", singleQuestionResponseRow.getDisplayableResponse());
        
        assertEquals("student2 In Course1", singleQuestionResponseRow.getGiverDisplayableIdentifier());
        assertEquals("Team 1.1", singleQuestionResponseRow.getGiverTeam());
        assertEquals("student1 In Course1", singleQuestionResponseRow.getRecipientDisplayableIdentifier());
        assertEquals("Team 1.1", singleQuestionResponseRow.getRecipientTeam());
        
        assertEquals(null, singleQuestionResponseRow.getRowAttributes());
        assertFalse(singleQuestionResponseRow.isGiverProfilePictureAColumn());
        assertFalse(singleQuestionResponseRow.isRecipientProfilePictureAColumn());
        assertTrue(singleQuestionResponseRow.isGiverDisplayed());
        assertTrue(singleQuestionResponseRow.isRecipientDisplayed());
        assertTrue(singleQuestionResponseRow.isModerationsButtonDisplayed());
        
        modButton = singleQuestionResponseRow.getModerationButton();
        assertEquals("Moderate Response", modButton.getButtonText());
        assertTrue(modButton.getClassName().matches(".*\\bbtn\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-default\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-xs\\b.*"));
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals("student2InCourse1@gmail.tmt", modButton.getGiverIdentifier());
        assertEquals(2, modButton.getQuestionNumber());
        assertTrue(modButton.isAllowedToModerate());
        assertFalse(modButton.isDisabled());
        
        
        ______TS("all sections, require loading by ajax");
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
        
        List<InstructorResultsQuestionTable> ajaxQuestionPanels = data.getQuestionPanels();
        assertEquals(4, ajaxQuestionPanels.size());
        InstructorResultsQuestionTable ajaxQuestionPanel = ajaxQuestionPanels.get(0);
        assertEquals("panel-default", ajaxQuestionPanel.getPanelClass());
        assertTrue(ajaxQuestionPanel.isCollapsible());
        assertTrue(ajaxQuestionPanel.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(ajaxQuestionPanel.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        
    }
    
    @Test
    public void testInitForViewByGQR() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("typical case: view all sections, all sections, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "giver-question-recipient";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "question");
        data.initForViewByRecipientQuestionGiver(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, "giver-question-recipient", "on");
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(sectionPanels, Arrays.asList("Section 1", "Section 2", "None")));
        
        InstructorFeedbackResultsSectionPanel sectionPanel = sectionPanels.get("None");
        assertEquals("Not in a section", sectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", sectionPanel.getDetailedResponsesHeaderText());
        assertTrue(isKeysOfMap(sectionPanel.getIsTeamWithResponses(), Arrays.asList("-")));
        assertEquals("panel-success", sectionPanel.getPanelClass());
        assertEquals("Received Responses Statistics", sectionPanel.getStatisticsHeaderText());
        
        
        // This is a bug. TODO see #2857 
        //Map<String, List<InstructorResultsQuestionTable>> statsTables = sectionPanel.getTeamStatisticsTable();
        //assertEquals(1, statsTables.size());
        
        //List<InstructorResultsQuestionTable> statsTablesForInstructors = statsTables.get("Instructors");
        //assertEquals(0, statsTablesForInstructors.size());
        
        assertEquals(1, sectionPanel.getParticipantPanels().size());
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)sectionPanel.getParticipantPanels().get("-").get(0);
        assertEquals("instructor1@course1.tmt", participantPanel.getParticipantIdentifier());
        assertEquals("Instructor1 Course1", participantPanel.getName());
        
        assertTrue(participantPanel.isGiver());
        assertFalse(participantPanel.isModerationButtonDisplayed());
        assertTrue(participantPanel.isHasResponses());
        
        List<InstructorResultsQuestionTable> questionTables = participantPanel.getQuestionTables();
        assertEquals(1, questionTables.size());
        
        InstructorResultsQuestionTable firstQuestionTable = questionTables.get(0);
        assertEquals(1, firstQuestionTable.getResponses().size());
        assertEquals("", firstQuestionTable.getAdditionalInfoText());
        assertEquals("panel-info", firstQuestionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn3InSession1InCourse1").questionMetaData.getValue()), 
                                        firstQuestionTable.getQuestionText());
        assertTrue(firstQuestionTable.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(firstQuestionTable.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        assertTrue(firstQuestionTable.getResponsesBodyClass().matches(".*\\bin\\b.*"));
        assertEquals(4, firstQuestionTable.getColumns().size());
        assertTrue(isKeysOfMap(firstQuestionTable.getIsColumnSortable(),
                               Arrays.asList("Photo", "Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapTrue(firstQuestionTable.getIsColumnSortable(), 
                                     Arrays.asList("Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapFalse(firstQuestionTable.getIsColumnSortable(), 
                                        Arrays.asList("Photo")));
        
        InstructorResultsResponseRow responseRow = firstQuestionTable.getResponses().get(0);
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
        
        
        sectionPanel = sectionPanels.get("Section 1");
        assertTrue(sectionPanel.isDisplayingMissingParticipants());
        assertTrue(sectionPanel.isDisplayingTeamStatistics());
        assertFalse(sectionPanel.isLoadSectionResponsesByAjax());
        Map<String, List<InstructorResultsParticipantPanel>> sectionOneParticipantPanels = sectionPanel.getParticipantPanels();
        
        assertTrue(isKeysOfMap(sectionOneParticipantPanels, Arrays.asList("Team 1.1")));
        List<InstructorResultsParticipantPanel> teamParticipantPanel = sectionOneParticipantPanels.get("Team 1.1");
        assertEquals(null, teamParticipantPanel.get(0).getClassName());
        assertEquals("student1InCourse1@gmail.tmt", teamParticipantPanel.get(0).getParticipantIdentifier());
        assertTrue(teamParticipantPanel.get(0).isGiver());
        assertTrue(teamParticipantPanel.get(0).isModerationButtonDisplayed());
        

        responseRow = 
             ((InstructorFeedbackResultsGroupByQuestionPanel)teamParticipantPanel.get(0)).getQuestionTables()
                                                                                         .get(1).getResponses().get(0);
        assertEquals("Response from student 1 to student 2.", responseRow.getDisplayableResponse());
        assertEquals("Student 1 in course 1", responseRow.getGiverDisplayableIdentifier());
        assertEquals("Student 2 in course 1", responseRow.getRecipientDisplayableIdentifier());
        assertEquals("Team 1.1", responseRow.getGiverTeam());
        assertEquals("Team 1.1", responseRow.getRecipientTeam());
        assertFalse(responseRow.isGiverDisplayed());
        assertTrue(responseRow.isRecipientDisplayed());
        assertTrue(responseRow.isRecipientProfilePictureAColumn());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertEquals(null, responseRow.getRowAttributes());

        
        ______TS("view section 1, all questions, no stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = "Section 1";
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorFromSectionWithinRange(data.feedbackSessionName, data.courseId,
                                                                                  data.instructor.email, data.selectedSection, 1000);
        data.initForViewByGiverQuestionRecipient(instructor, "Section 1", null, "on");
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionOnePanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(sectionOnePanels, Arrays.asList("Section 1")));
        
        sectionPanel = sectionOnePanels.get("Section 1");
        assertEquals("Section 1", sectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", sectionPanel.getDetailedResponsesHeaderText());

        assertTrue(isKeysOfMap(sectionPanel.getIsTeamWithResponses(), Arrays.asList("Team 1.1")));
        assertEquals("panel-success", sectionPanel.getPanelClass());
        assertEquals("Statistics for Given Responses", sectionPanel.getStatisticsHeaderText());
        
        assertTrue(sectionPanel.isDisplayingTeamStatistics()); // note that this is still true because
                                                               // displaying stats are done in javascript
      
        
        ______TS("all sections, not grouping by team");
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
        
        data.initForViewByGiverQuestionRecipient(instructor, data.selectedSection, "on", null);
        
        Map<String, InstructorFeedbackResultsSectionPanel> notGroupByTeamPanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(notGroupByTeamPanels, Arrays.asList("Section 1", "Section 2", "None")));
        
        sectionPanel = notGroupByTeamPanels.get("Section 1");
        
        List<InstructorResultsParticipantPanel> notGroupByTeamParticipantPanels = sectionPanel.getParticipantPanelsInSortedOrder();
        assertEquals(4, notGroupByTeamParticipantPanels.size());
        
        List<String> nameList = Arrays.asList("student1 In Course1", "student2 In Course1", 
                                              "student3 In Course1", "student4 In Course1");
        
        for (int i = 0; i < nameList.size(); i++) {
            String nameOfStudent = nameList.get(i);
            InstructorFeedbackResultsGroupByQuestionPanel notGroupByTeamParticipantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(i);
            String sectionPanelStudentName = notGroupByTeamParticipantPanel.getName();
            
            assertEquals(nameOfStudent, sectionPanelStudentName);
        }
        
        InstructorFeedbackResultsGroupByQuestionPanel studentPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(0);
        InstructorResultsModerationButton modButton = studentPanel.getModerationButton();
        assertEquals("Moderate Responses", modButton.getButtonText());
        
        assertTrue(modButton.getClassName().matches(".*\\bbtn\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-primary\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-xs\\b.*"));
        
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals(-1, modButton.getQuestionNumber());
        
        questionTables = studentPanel.getQuestionTables();
        assertEquals(2, questionTables.size());
        
        InstructorResultsQuestionTable questionTable = questionTables.get(0);
        
        assertEquals(1, questionTable.getResponses().size());
        assertEquals("", questionTable.getAdditionalInfoText());
        assertEquals("panel-info", questionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue()), 
                                        questionTable.getQuestionText());
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bin\\b.*"));
        assertEquals(4, questionTable.getColumns().size());
        assertTrue(isKeysOfMap(questionTable.getIsColumnSortable(),
                               Arrays.asList("Photo", "Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapTrue(questionTable.getIsColumnSortable(), 
                                     Arrays.asList("Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapFalse(questionTable.getIsColumnSortable(), 
                                        Arrays.asList("Photo")));
        
        responseRow = questionTable.getResponses().get(0);
        assertEquals("Student 1 self feedback.", responseRow.getDisplayableResponse());
        assertEquals("student1 In Course1", responseRow.getGiverDisplayableIdentifier());
        assertEquals(responseRow.getGiverDisplayableIdentifier(), responseRow.getRecipientDisplayableIdentifier());
        assertEquals(responseRow.getRecipientTeam(), responseRow.getGiverTeam());
        assertEquals("Team 1.1", responseRow.getRecipientTeam());
        assertFalse(responseRow.isGiverDisplayed());
        assertTrue(responseRow.isRecipientDisplayed());
        assertTrue(responseRow.isRecipientProfilePictureAColumn());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertEquals(null, responseRow.getRowAttributes());
        
        
        ______TS("all sections, require loading by ajax");
        
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
        data.initForViewByGiverQuestionRecipient(instructor, data.selectedSection, "on", null);
        assertFalse(data.bundle.isComplete());
        
        Map<String, InstructorFeedbackResultsSectionPanel> ajaxQuestionPanels = data.getSectionPanels();
        assertEquals(3, ajaxQuestionPanels.size());
        
        isKeysOfMap(ajaxQuestionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        InstructorFeedbackResultsSectionPanel ajaxSectionPanel = ajaxQuestionPanels.get("Section 1");
        
        assertTrue(ajaxSectionPanel.getPanelClass().matches(".*\\bpanel-success\\b.*"));
        assertTrue(ajaxSectionPanel.isLoadSectionResponsesByAjax());
        assertEquals(0, ajaxSectionPanel.getParticipantPanels().size());
    }
    
    @Test
    public void testInitForViewByRQG() throws UnauthorizedAccessException, EntityDoesNotExistException {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        data.sections = Arrays.asList("Section 1", "Section 2", "None");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("typical case: view all sections, all sections, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1000, "question");
        data.initForViewByRecipientQuestionGiver(instructor, InstructorFeedbackResultsPageData.ALL_SECTION_OPTION, "giver-question-recipient", "on");
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(sectionPanels, Arrays.asList("Section 1", "Section 2", "None")));
        
        InstructorFeedbackResultsSectionPanel sectionPanel = sectionPanels.get("Section 1");
        assertTrue(sectionPanel.isDisplayingMissingParticipants());
        assertTrue(sectionPanel.isDisplayingTeamStatistics());
        assertFalse(sectionPanel.isLoadSectionResponsesByAjax());
        Map<String, List<InstructorResultsParticipantPanel>> sectionOneParticipantPanels = sectionPanel.getParticipantPanels();
        
        assertTrue(isKeysOfMap(sectionOneParticipantPanels, Arrays.asList("Team 1.1")));
        List<InstructorResultsParticipantPanel> teamParticipantPanel = sectionOneParticipantPanels.get("Team 1.1");
        assertEquals(null, teamParticipantPanel.get(0).getClassName());
        assertEquals("student1InCourse1@gmail.tmt", teamParticipantPanel.get(0).getParticipantIdentifier());
        assertTrue(teamParticipantPanel.get(0).isGiver());
        assertTrue(teamParticipantPanel.get(0).isModerationButtonDisplayed());
        
       //!!! add checks for response row here

        
        ______TS("view section 1, all questions, no stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = null;
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = "Section 1";
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorFromSectionWithinRange(data.feedbackSessionName, data.courseId,
                                                                                  data.instructor.email, data.selectedSection, 1000);
        data.initForViewByRecipientQuestionGiver(instructor, "Section 1", null, "on");
        
        Map<String, InstructorFeedbackResultsSectionPanel> sectionOnePanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(sectionOnePanels, Arrays.asList("Section 1")));
        
        sectionPanel = sectionOnePanels.get("Section 1");
        assertEquals("Section 1", sectionPanel.getSectionNameForDisplay());
        assertEquals("Detailed Responses", sectionPanel.getDetailedResponsesHeaderText());

        assertTrue(isKeysOfMap(sectionPanel.getIsTeamWithResponses(), Arrays.asList("Team 1.1")));
        assertEquals("panel-success", sectionPanel.getPanelClass());
        assertEquals("Statistics for Given Responses", sectionPanel.getStatisticsHeaderText());
        
        assertTrue(sectionPanel.isDisplayingTeamStatistics()); // note that this is still true because
                                                               // displaying stats are done in javascript
      
        
        ______TS("all sections, not grouping by team");
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
                                        1000, "giver-question-recipient");
        
        data.initForViewByRecipientQuestionGiver(instructor, data.selectedSection, "on", null);
        
        Map<String, InstructorFeedbackResultsSectionPanel> notGroupByTeamPanels = data.getSectionPanels();
        assertTrue(isKeysOfMap(notGroupByTeamPanels, Arrays.asList("Section 1", "Section 2", "None")));
        
        sectionPanel = notGroupByTeamPanels.get("Section 1");
        
        List<InstructorResultsParticipantPanel> notGroupByTeamParticipantPanels = sectionPanel.getParticipantPanelsInSortedOrder();
        assertEquals(4, notGroupByTeamParticipantPanels.size());
        
        List<String> nameList = Arrays.asList("student1 In Course1", "student2 In Course1", 
                                              "student3 In Course1", "student4 In Course1");
        
        for (int i = 0; i < nameList.size(); i++) {
            String nameOfStudent = nameList.get(i);
            InstructorFeedbackResultsGroupByQuestionPanel notGroupByTeamParticipantPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(i);
            String sectionPanelStudentName = notGroupByTeamParticipantPanel.getName();
            
            assertEquals(nameOfStudent, sectionPanelStudentName);
        }
        
        InstructorFeedbackResultsGroupByQuestionPanel studentPanel = (InstructorFeedbackResultsGroupByQuestionPanel)notGroupByTeamParticipantPanels.get(0);
        InstructorResultsModerationButton modButton = studentPanel.getModerationButton();
        assertEquals("Moderate Responses", modButton.getButtonText());
        
        assertTrue(modButton.getClassName().matches(".*\\bbtn\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-primary\\b.*"));
        assertTrue(modButton.getClassName().matches(".*\\bbtn-xs\\b.*"));
        
        assertEquals("idOfTypicalCourse1", modButton.getCourseId());
        assertEquals("First feedback session", modButton.getFeedbackSessionName());
        assertEquals(-1, modButton.getQuestionNumber());
        
        List<InstructorResultsQuestionTable> questionTables = studentPanel.getQuestionTables();
        assertEquals(2, questionTables.size());
        
        InstructorResultsQuestionTable questionTable = questionTables.get(0);
        
        assertEquals(1, questionTable.getResponses().size());
        assertEquals("", questionTable.getAdditionalInfoText());
        assertEquals("panel-info", questionTable.getPanelClass());
        assertEquals(Sanitizer.sanitizeForHtml(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").questionMetaData.getValue()), 
                                        questionTable.getQuestionText());
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bpanel-collapse\\b.*"));
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bcollapse\\b.*"));
        assertTrue(questionTable.getResponsesBodyClass().matches(".*\\bin\\b.*"));
        assertEquals(4, questionTable.getColumns().size());
        assertTrue(isKeysOfMap(questionTable.getIsColumnSortable(),
                               Arrays.asList("Photo", "Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapTrue(questionTable.getIsColumnSortable(), 
                                     Arrays.asList("Recipient", "Team", "Feedback")));
        assertTrue(isValuesInMapFalse(questionTable.getIsColumnSortable(), 
                                        Arrays.asList("Photo")));
        
        InstructorResultsResponseRow responseRow = questionTable.getResponses().get(0);
        assertEquals("Student 1 self feedback.", responseRow.getDisplayableResponse());
        assertEquals("student1 In Course1", responseRow.getGiverDisplayableIdentifier());
        assertEquals(responseRow.getGiverDisplayableIdentifier(), responseRow.getRecipientDisplayableIdentifier());
        assertEquals(responseRow.getRecipientTeam(), responseRow.getGiverTeam());
        assertEquals("Team 1.1", responseRow.getRecipientTeam());
        assertFalse(responseRow.isGiverDisplayed());
        assertTrue(responseRow.isRecipientDisplayed());
        assertTrue(responseRow.isRecipientProfilePictureAColumn());
        assertFalse(responseRow.isGiverProfilePictureAColumn());
        assertEquals(null, responseRow.getRowAttributes());
        
        
        ______TS("all sections, require loading by ajax");
        
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "recipient-question-giver";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                        data.feedbackSessionName, data.courseId, instructor.email, 
                                        1, "giver-question-recipient");
        data.initForViewByRecipientQuestionGiver(instructor, data.selectedSection, "on", null);
        assertFalse(data.bundle.isComplete());
        
        Map<String, InstructorFeedbackResultsSectionPanel> ajaxQuestionPanels = data.getSectionPanels();
        assertEquals(3, ajaxQuestionPanels.size());
        
        isKeysOfMap(ajaxQuestionPanels, Arrays.asList("None", "Section 1", "Section 2"));
        InstructorFeedbackResultsSectionPanel ajaxSectionPanel = ajaxQuestionPanels.get("Section 1");
        
        assertTrue(ajaxSectionPanel.getPanelClass().matches(".*\\bpanel-success\\b.*"));
        assertTrue(ajaxSectionPanel.isLoadSectionResponsesByAjax());
        assertEquals(0, ajaxSectionPanel.getParticipantPanels().size());
    }
    
    
    public <K, V> boolean isKeysOfMap(Map<K, V> map, List<K> keys) {
        Set<K> keysOfMap = map.keySet();
        
        Set<K> keysSet = new HashSet<K>(keys);
        
        return keysOfMap.equals(keysSet);
    }
    
    public <K> boolean isValuesInMapTrue(Map<K, Boolean> map, List<K> keys) {
        boolean result = true;
        for (K key : keys) {
            if (!map.get(key)) {
                result = false;
            }
        }
        return result;
    }
    public <K> boolean isValuesInMapFalse(Map<K, Boolean> map, List<K> keys) {
        boolean result = true;
        for (K key : keys) {
            if (map.get(key)) {
                result = false;
            }
        }
        return result;
    }
    
}
