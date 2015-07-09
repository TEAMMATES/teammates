package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.InstructorResultsParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel;
import teammates.ui.template.InstructorFeedbackResultsSectionPanel;
import teammates.ui.template.FeedbackSessionPublishButton;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorResultsQuestionTable;
import teammates.ui.template.InstructorResultsResponseRow;
import teammates.ui.template.InstructorResultsModerationButton;

public class InstructorFeedbackResultsPageData extends PageData {
    public static final String EXCEEDING_RESPONSES_ERROR_MESSAGE = "Sorry, we could not retrieve results. "
                                                                 + "Please try again in a few minutes. If you continue to see this message, it could be because the report you are trying to display contains too much data to display in one page. e.g. more than 2,500 entries."
                                                                 + "<ul><li>If that is the case, you can still use the 'By question' report to view responses. You can also download the results as a spreadsheet. If you would like to see the responses in other formats (e.g. 'Group by - Giver'), you can try to divide the course into smaller sections so that we can display responses one section at a time.</li>"
                                                                 + "<li>If you believe the report you are trying to view is unlikely to have more than 2,500 entries, please contact us at <a href='mailto:teammates@comp.nus.edu.sg'>teammates@comp.nus.edu.sg</a> so that we can investigate.</li></ul>";

    
    public FeedbackSessionResultsBundle bundle = null;
    public InstructorAttributes instructor = null;
    public List<String> sections = null;
    public String selectedSection = null;
    public String sortType = null;
    public String groupByTeam = null;
    public String showStats = null;
    public int startIndex;
    private boolean shouldCollapsed;


    // used for html table ajax loading
    public String courseId = null;
    public String feedbackSessionName = null;
    public String ajaxStatus = null;
    public String sessionResultsHtmlTableAsString = null;

    // TODO multiple page data classes for each view type
    
    // for question view
    List<InstructorResultsQuestionTable> questionPanels;
    // for giver > question > recipient, and TODO more...
    LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels;
    
    enum ViewType {
        QUESTION, GIVER_QUESTION_RECIPIENT, RECIPIENT_QUESTION_GIVER;
        
        public String toString() {
            return name().toLowerCase().replaceAll("_", "-");
        }
    }
    
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
        startIndex = -1;
    }
    
    public void initForViewByQuestion() {
        ViewType viewType = ViewType.QUESTION;
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorResultsQuestionTable>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : questionToResponseMap.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();
            
            questionPanels.add(buildQuestionTable(question, responses, viewType, "", true));
        }
        
    }
    
    /**
     * Creates {@code InstructorFeedbackResultsSectionPanel}s for sectionPanels.
     * 
     * Iterates through the responses and creates panels and questions for them. Keeps track 
     * of missing sections, teams and participants who do not have responses 
     * and create panels for these missing sections, teams and participants.
     * 
     * TODO: simplify the logic in this method
     */
    public void initForViewByGiverQuestionRecipient() {
        
        ViewType viewType = ViewType.GIVER_QUESTION_RECIPIENT;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            buildSectionPanelsForGiverQuestionRecipientForAjaxLoading(sections);
            return;
        }
        
        if (bundle.responses.size() == 0) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > 500);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByGiverQuestionRecipient(groupByTeam == null 
                                                                      || groupByTeam.equals("on"));
        
        buildResponsesPanelsForGiverQuestionRecipient(bundle, viewType, sortedResponses);
        
    }
    
    /**
     * Creates {@code InstructorFeedbackResultsSectionPanel}s for sectionPanels.
     * 
     * Iterates through the responses and creates panels and questions for them. Keeps track 
     * of missing sections, teams and participants who do not have responses 
     * and create panels for these missing sections, teams and participants.
     * 
     * TODO: simplify the logic in this method
     */
    public void initForViewByRecipientQuestionGiver() {
        
        ViewType viewType = ViewType.RECIPIENT_QUESTION_GIVER;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            buildSectionPanelsForGiverQuestionRecipientForAjaxLoading(sections);
            return;
        }
        
        if (bundle.responses.size() == 0) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > 500);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByRecipientQuestionGiver(groupByTeam == null 
                                                                      || groupByTeam.equals("on"));
       
        buildResponsesPanelsForRecipientQuestionGiver(bundle, viewType, sortedResponses);
        

    }

    private LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> buildResponsesPanelsForGiverQuestionRecipient(
                                    FeedbackSessionResultsBundle bundle,
                                    ViewType viewType, 
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {
        FieldValidator validator = new FieldValidator();
        
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByGiverTeam();
        Map<String, FeedbackQuestionAttributes> questions = bundle.questions;
        
        // Initialize section Panels. TODO abstract into method
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        Set<String> sectionsInCourse = bundle.rosterSectionTeamNameTable.keySet();
        Set<String> sectionsWithResponses = new HashSet<String>();
        
        // Maintain previous section and previous team while iterating through the loop
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        Set<String> teamsInSection = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        
        String prevTeam = "";
        Set<String> teamMembersInTeam = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();                                
        
        int giverIndex = this.startIndex;
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver : 
                                                                    sortedResponses.entrySet()) {
            giverIndex += 1;
            String giverIdentifier = responsesFromGiver.getKey();
            
            String currentTeam = getCurrentTeam(bundle, giverIdentifier);
            String currentSection = getCurrentSection(responsesFromGiver);
            
            // Change in team
            if (!prevTeam.equals(currentTeam)) {
                boolean isFirstTeam = prevTeam.equals("");
                if (!isFirstTeam) {
                    createMissingParticipantPanelsForPrevTeamAndResetVariables(bundle, validator, sectionPanel,
                                                    prevTeam, teamsWithResponses, teamMembersWithResponses,
                                                    teamMembersInTeam, currentTeam);
                }
                
                prevTeam = currentTeam;
                teamsWithResponses.add(currentTeam);
            }
            
            // Change in section
            if (!prevSection.equals(currentSection)) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                
                if (!isFirstSection) {
                    buildTeamStatisticsTableForSectionPanel(sectionPanel, prevSection, viewType, 
                                                    questions, responsesGroupedByTeam, 
                                                    teamsWithResponses);
                    createMissingTeamAndParticipantPanelsForPrevSectionAndResetVariables(bundle,
                                                    validator, sectionPanel, prevSection, sectionsWithResponses,
                                                    teamsWithResponses, teamsInSection, currentTeam);
                    
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                    
                }
                
                teamsInSection = bundle.getTeamsInSectionFromRoster(currentSection);                
            }
            
            // questionForGiver is used to keep track of any question, 
            // this is used to determine the giver type later
            // TODO #2857
            FeedbackQuestionAttributes questionForGiver = null;
            List<InstructorResultsQuestionTable> questionTables = new ArrayList<InstructorResultsQuestionTable>();
            
            int questionIndex = 0;
            for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromGiverForQuestion : 
                                                                                         responsesFromGiver.getValue().entrySet()) {
                if (responsesFromGiverForQuestion.getValue().isEmpty()) {
                    // participant has no responses for the current question
                    continue;
                }
                
                questionIndex += 1;
                
                // keep track of current question
                FeedbackQuestionAttributes currentQuestion = responsesFromGiverForQuestion.getKey();
                List<FeedbackResponseAttributes> responsesForQuestion = responsesFromGiverForQuestion.getValue();
                
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);

                InstructorResultsQuestionTable questionTable 
                    = buildQuestionTable(currentQuestion, responsesForQuestion,
                                         viewType, "giver-" + giverIndex + "-question-" + questionIndex,
                                         false);
                questionTable.setBoldQuestionNumber(false);
                questionTables.add(questionTable);
                
                questionForGiver = currentQuestion;
            }
            
            // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
            InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                      null, giverIdentifier,
                                                                      "btn btn-primary btn-xs",
                                                                      "Moderate Responses");
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
                                                                 buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                     validator, giverIdentifier, 
                                                                     bundle.getNameForEmail(giverIdentifier),
                                                                     questionForGiver, questionTables, moderationButton, 
                                                                     bundle.isParticipantIdentifierStudent(giverIdentifier),
                                                                     true);
            
            giverPanel.setModerationButtonDisplayed(bundle.isParticipantIdentifierStudent(giverIdentifier));
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, giverPanel);
            teamMembersWithResponses.add(giverIdentifier);
            
            prevSection = currentSection;
        }
        
        buildTeamStatisticsTableForSectionPanel(sectionPanel, prevSection, viewType, 
                                        questions, responsesGroupedByTeam, 
                                        teamsWithResponses);
        
        // for the last section
        createTeamAndParticipantPanelsForLastParticipantSection(bundle, validator, sectionPanel, prevSection, prevTeam,
                                                     sectionsWithResponses, teamsWithResponses, teamsInSection,
                                                     teamMembersWithResponses);

        // display missing sections
        createSectionPanelsForMissingSections(bundle, validator, sectionsInCourse, sectionsWithResponses);
        
        return responsesGroupedByTeam;
    }
    
    private LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> buildResponsesPanelsForRecipientQuestionGiver(
                                    FeedbackSessionResultsBundle bundle,
                                    ViewType viewType, 
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {
        FieldValidator validator = new FieldValidator();
        
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByGiverTeam();
        Map<String, FeedbackQuestionAttributes> questions = bundle.questions;
        
        
        // Initialize section Panels. TODO abstract into method
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        Set<String> sectionsInCourse = bundle.rosterSectionTeamNameTable.keySet();
        Set<String> sectionsWithResponses = new HashSet<String>();
        // Maintain previous section and previous team while iterating through the loop
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        Set<String> teamsInSection = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        
        String prevTeam = "";
        Set<String> teamMembersInTeam = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();      
        
        int recipientIndex = this.startIndex;
  
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesToRecipient : 
                                                                    sortedResponses.entrySet()) {
            recipientIndex += 1;
            String recipientIdentifier = responsesToRecipient.getKey();
            
            String currentTeam = getCurrentTeam(bundle, recipientIdentifier);
            String currentSection = getCurrentSection(responsesToRecipient);
            
            // Change in team
            if (!prevTeam.equals(currentTeam)) {
                boolean isFirstTeam = prevTeam.equals("");
                if (!isFirstTeam) {
                    createMissingParticipantPanelsForPrevTeamAndResetVariables(bundle, validator, sectionPanel,
                                                    prevTeam, teamsWithResponses, teamMembersWithResponses,
                                                    teamMembersInTeam, currentTeam);
                }
                prevTeam = currentTeam;
                teamsWithResponses.add(currentTeam);
            }
            
            // Change in section
            if (!prevSection.equals(currentSection)) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    buildTeamStatisticsTableForSectionPanel(sectionPanel, prevSection, viewType, 
                                                    questions, responsesGroupedByTeam, 
                                                    teamsWithResponses);
                    createMissingTeamAndParticipantPanelsForPrevSectionAndResetVariables(bundle,
                                                    validator, sectionPanel, prevSection, sectionsWithResponses,
                                                    teamsWithResponses, teamsInSection, currentTeam);
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }
                
                
                teamsInSection = bundle.getTeamsInSectionFromRoster(currentSection);                
            }
            
            // questionForGiver is used to keep track of any question, 
            // this is used to determine the giver type later
            // TODO #2857
            FeedbackQuestionAttributes questionForRecipient = null;
            List<InstructorResultsQuestionTable> questionTables = new ArrayList<InstructorResultsQuestionTable>();
            
            int questionIndex = 0;
            for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesToRecipientForQuestion : 
                                                                                         responsesToRecipient.getValue().entrySet()) {
                if (responsesToRecipientForQuestion.getValue().isEmpty()) {
                    // participant has no responses for the current question
                    continue;
                }
                
                questionIndex += 1;
                
                // keep track of current question
                FeedbackQuestionAttributes currentQuestion = responsesToRecipientForQuestion.getKey();
                List<FeedbackResponseAttributes> responsesForQuestion = responsesToRecipientForQuestion.getValue();
                
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);

                InstructorResultsQuestionTable questionTable 
                    = buildQuestionTable(currentQuestion, responsesForQuestion,
                                         viewType, "recipient-" + recipientIndex + "-question-" + questionIndex,
                                         false);
                questionTable.setBoldQuestionNumber(false);
                questionTables.add(questionTable);
                
                questionForRecipient = currentQuestion;
            }
            
            // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
            InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                      null, recipientIdentifier,
                                                                      "btn btn-primary btn-xs",
                                                                      "Moderate Responses");
            InstructorFeedbackResultsGroupByQuestionPanel recipientPanel = 
                                                             buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                 validator, recipientIdentifier, 
                                                                 bundle.getNameForEmail(recipientIdentifier),
                                                                 questionForRecipient, questionTables, moderationButton, 
                                                                 bundle.isParticipantIdentifierStudent(recipientIdentifier),
                                                                 false);
            
            recipientPanel.setModerationButtonDisplayed(bundle.isParticipantIdentifierStudent(recipientIdentifier));
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, recipientPanel);
            teamMembersWithResponses.add(recipientIdentifier);
            
            prevSection = currentSection;
        }
        
        buildTeamStatisticsTableForSectionPanel(sectionPanel, prevSection, viewType, 
                                        questions, responsesGroupedByTeam, 
                                        teamsWithResponses);
        
        // for the last section
        createTeamAndParticipantPanelsForLastParticipantSection(bundle, validator, sectionPanel, prevSection, prevTeam,
                                                     sectionsWithResponses, teamsWithResponses, teamsInSection,
                                                     teamMembersWithResponses);

        // display missing sections
        createSectionPanelsForMissingSections(bundle, validator, sectionsInCourse, sectionsWithResponses);
        return responsesGroupedByTeam;
    }

    private void createMissingTeamAndParticipantPanelsForPrevSectionAndResetVariables(
                                    FeedbackSessionResultsBundle bundle, FieldValidator validator,
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String prevSection,
                                    Set<String> receivingSections, Set<String> receivingTeams,
                                    Set<String> teamsInSection, String currentTeam) {
        sectionPanel.setSectionName(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a section" : prevSection);
        sectionPanel.setDisplayingTeamStatistics(true);
        sectionPanels.put(prevSection, sectionPanel);
        
        receivingSections.add(prevSection);
            
        // update the teams for the previous section
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(receivingTeams);
        receivingTeams.clear();
        receivingTeams.add(currentTeam);
        
        // create for every remaining team in the section, participantResultsPanels for every team member
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembersOfTeam = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            addMissingParticipantsForTeamToSectionPanel(validator, sectionPanel, teamWithoutResponses, teamMembersOfTeam);
        }
        
    }

    private void createTeamAndParticipantPanelsForLastParticipantSection(FeedbackSessionResultsBundle bundle,
                                    FieldValidator validator,
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String prevSection,
                                    String prevTeam, Set<String> receivingSections,
                                    Set<String> receivingTeams, Set<String> teamsInSection,
                                    Set<String> teamMembersWithResponses) {
        receivingTeams.add(prevTeam);
        
        sectionPanel.setSectionName(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a Section" : prevSection);
        sectionPanel.setDisplayingTeamStatistics(true);
        sectionPanels.put(prevSection, sectionPanel);
        receivingSections.add(prevSection);
        
        Set<String> teamMembersWithoutResponses = new HashSet<String>(bundle.getTeamMembersFromRoster(prevTeam));
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
        
        // for printing the participants without responses in the last response participant's team 
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
        Collections.sort(sortedTeamMembersWithoutResponses);
        
        addMissingParticipantsForTeamToSectionPanel(validator, sectionPanel, prevTeam,
                                                    sortedTeamMembersWithoutResponses);
        
        receivingTeams.add(prevTeam);
        // for printing the teams without responses in last section having responses
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(receivingTeams);
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembersOfTeam = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            addMissingParticipantsForTeamToSectionPanel(validator, sectionPanel, teamWithoutResponses, teamMembersOfTeam);
        }
    }

    private String getCurrentTeam(FeedbackSessionResultsBundle bundle, String giverIdentifier) {
        String currentTeam;
        if (bundle.isParticipantIdentifierInstructor(giverIdentifier)) {
            currentTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            currentTeam = bundle.getTeamNameForEmail(giverIdentifier);
            if (currentTeam.equals("")) {
                currentTeam = bundle.getNameForEmail(giverIdentifier);
            }
        }
        return currentTeam;
    }

    /**
     * Uses the first response to set the current section
     * @param responsesFromGiver
     * @return
     */
    private String getCurrentSection(
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver) {
        String currentSection = Const.DEFAULT_SECTION;
        // update current section
        // retrieve section from the first response of this user
        // TODO simplify by introducing more data structures into bundle
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromGiverForQuestion : 
                                                                                     responsesFromGiver.getValue().entrySet()) {
            if (responsesFromGiverForQuestion.getValue().isEmpty()) {
                continue;
            }
            FeedbackResponseAttributes firstResponse = responsesFromGiverForQuestion.getValue().get(0);
            currentSection = firstResponse.giverSection;
            break;
        }
        
        return currentSection;
    }

    private void createMissingParticipantPanelsForPrevTeamAndResetVariables(FeedbackSessionResultsBundle bundle,
                                            FieldValidator validator,
                                            InstructorFeedbackResultsSectionPanel sectionPanel, String prevTeam,
                                            Set<String> receivingTeams, Set<String> teamMembersWithResponses,
                                            Set<String> teamMembersEmail, String currentTeam) {
        boolean isFirstTeam = prevTeam.isEmpty();
        if (!isFirstTeam) {
            teamMembersEmail.clear();
            teamMembersEmail.addAll(bundle.getTeamMembersFromRoster(prevTeam));
            
            Set<String> teamMembersWithoutResponses = new HashSet<String>(teamMembersEmail);
            teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
            
            // Create missing participants panels for the previous team
            List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
            Collections.sort(sortedTeamMembersWithoutResponses);
            addMissingParticipantsForTeamToSectionPanel(validator, sectionPanel, 
                                                        prevTeam, sortedTeamMembersWithoutResponses);
        }

        teamMembersWithResponses.clear();
        
        
    }

    private void createSectionPanelsForMissingSections(FeedbackSessionResultsBundle bundle,
                                    FieldValidator validator, Set<String> sectionsInCourse,
                                    Set<String> receivingSections) {
        InstructorFeedbackResultsSectionPanel sectionPanel;
        Set<String> sectionsWithNoResponseReceived = new HashSet<String>(sectionsInCourse);
        sectionsWithNoResponseReceived.removeAll(receivingSections);
        
        // TODO introduce enums for this, because this causes problems if there is a section named "All"
        if (selectedSection.equals("All")) {
            List<String> sectionsWithNoResponseReceivedList = new ArrayList<String>(sectionsWithNoResponseReceived);
            Collections.sort(sectionsWithNoResponseReceivedList);
            
            for (String sectionWithNoResponseReceived: sectionsWithNoResponseReceivedList) {
                sectionPanel = new InstructorFeedbackResultsSectionPanel();
                sectionPanel.setSectionName(sectionWithNoResponseReceived.equals(Const.DEFAULT_SECTION) ? "Not in a Section" : sectionWithNoResponseReceived);
                sectionPanel.setDisplayingTeamStatistics(true);
                sectionPanels.put(sectionWithNoResponseReceived, sectionPanel);
                
                Set<String> teamsFromSection = bundle.getTeamsInSectionFromRoster(sectionWithNoResponseReceived);
                List<String> teamsFromSectionList = new ArrayList<String>(teamsFromSection);
                Collections.sort(teamsFromSectionList);
                
                for (String teamInMissingSection : teamsFromSectionList) {
                    List<String> teamMembers = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamInMissingSection));
                    Collections.sort(teamMembers);
                    
                    addMissingParticipantsForTeamToSectionPanel(validator, sectionPanel, teamInMissingSection, teamMembers);                    
                }
            }
        }
    }

    private void buildSectionPanelsForGiverQuestionRecipientForAjaxLoading(List<String> sections) {
        setShouldCollapsed(true);
        
        // TODO 
        // Abstract out "All" sections into a boolean or enum instead. Otherwise this will cause problems in future
        // if there is ever a section named "All"
        if (selectedSection.equals("All")) {
            sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
            
            for (String section : sections) {
                InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
                sectionPanel.setSectionName(section);
                sectionPanel.setLoadSectionResponsesByAjax(true);
                
                sectionPanels.put(section, sectionPanel);
            }
            
        } else {
            sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
            
            InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
            sectionPanel.setSectionName(selectedSection);
            sectionPanel.setLoadSectionResponsesByAjax(true);
            
            sectionPanels.put(selectedSection, sectionPanel);
        }
    }

    private void addMissingParticipantsForTeamToSectionPanel(FieldValidator validator,
                                                             InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(null, teamMember, 
                                                                                               "btn btn-default btn-xs",
                                                                                               "Moderate Responses");
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
                    buildInstructorFeedbackResultsGroupByQuestionPanel(validator, teamMember, bundle.getFullNameFromRoster(teamMember),
                                                                       null, new ArrayList<InstructorResultsQuestionTable>(), moderationButton, 
                                                                       true, true);

            
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }

    private List<InstructorResultsParticipantPanel> addParticipantPanelToSectionPanel(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String currentTeam,
                                    InstructorFeedbackResultsGroupByQuestionPanel giverPanel) {
        List<InstructorResultsParticipantPanel> teamsMembersPanels;
        if (sectionPanel.getParticipantPanels().containsKey(currentTeam)) {
            teamsMembersPanels = sectionPanel.getParticipantPanels().get(currentTeam);
        } else {
            teamsMembersPanels = new ArrayList<InstructorResultsParticipantPanel>();
            sectionPanel.getParticipantPanels().put(currentTeam, teamsMembersPanels);
        }
        teamsMembersPanels.add(giverPanel);
        return teamsMembersPanels;
    }

    /*private void buildTeamStatisticsTables(
                        FeedbackSessionResultsBundle bundle,
                        List<String> sections, ViewType viewType, Map<String, FeedbackQuestionAttributes> questions,
                        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam) {
        for (String section : sections) {
            if (sectionPanels.containsKey(section)) {
                buildTeamStatisticsTableForSectionPanel(sectionPanels.get(section), section, viewType, 
                                                        questions, responsesGroupedByTeam, 
                                                        bundle.getTeamsInSectionFromRoster(section));
            }
        }
        
        // handle "None" ("Not in a section") separately since it should not be considered a section of the course
        // TODO use enum 
        if (sectionPanels.containsKey(Const.DEFAULT_SECTION)) {
            buildTeamStatisticsTableForSectionPanel(sectionPanels.get(Const.DEFAULT_SECTION), Const.DEFAULT_SECTION,
                                                    viewType, questions, responsesGroupedByTeam,
                                                    Arrays.asList(Const.USER_TEAM_FOR_INSTRUCTOR));
        }
    }*/
    
    private void buildTeamStatisticsTableForSectionPanel(InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                         String section, ViewType viewType,
                                                         Map<String, FeedbackQuestionAttributes> questions,
                                                         LinkedHashMap<String, Map<FeedbackQuestionAttributes,
                                                         List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                                                         Collection<String> teamsInSection) {
        System.out.println(teamsInSection);
        sectionPanel.setArrowClass("glyphicon-chevron-up");
        sectionPanel.setPanelClass("panel-success");
        switch (viewType) {
            case GIVER_QUESTION_RECIPIENT:
                sectionPanel.setStatisticsHeaderText("Statistics for Given Responses");
                sectionPanel.setDetailedResponsesHeaderText("Detailed Responses");
                
                break;
            case RECIPIENT_QUESTION_GIVER:
                sectionPanel.setStatisticsHeaderText("Received Responses Statistics");
                sectionPanel.setDetailedResponsesHeaderText("Detailed Responses");
                
                break;
            default:
                Assumption.fail("Team statistics table should not be used for this view");
        }
        
        constructStatisticsTablesForTeams(viewType, questions, responsesGroupedByTeam, sectionPanel, teamsInSection);
    }

    private void constructStatisticsTablesForTeams(
                                    ViewType viewType,
                                    Map<String, FeedbackQuestionAttributes> questions,
                                    LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                                    InstructorFeedbackResultsSectionPanel panel,
                                    Collection<String> teamsInSection) {
        Map<String, List<InstructorResultsQuestionTable>> teamToStatisticsTables = new HashMap<String, List<InstructorResultsQuestionTable>>();
        for (String team : teamsInSection) {
            if (!responsesGroupedByTeam.containsKey(team)) {
                continue;
            }
            
            List<InstructorResultsQuestionTable> statisticsTablesForTeam = new ArrayList<InstructorResultsQuestionTable>();
            
            for (FeedbackQuestionAttributes question : questions.values()) {
                if (!responsesGroupedByTeam.get(team).containsKey(question)) {
                    continue;
                }
                
                List<FeedbackResponseAttributes> responsesGivenTeamAndQuestion = responsesGroupedByTeam.get(team).get(question);

                InstructorResultsQuestionTable statsTable = buildQuestionTable(question, responsesGivenTeamAndQuestion,
                                                                               viewType, "", false);
                statsTable.setShowResponseRows(false); 
                statsTable.setCollapsible(false);
                
                if (!statsTable.getQuestionStatisticsHtml().isEmpty()) {
                    statisticsTablesForTeam.add(statsTable);
                }
            }

            InstructorResultsQuestionTable.sortByQuestionNumber(statisticsTablesForTeam);
            teamToStatisticsTables.put(team, statisticsTablesForTeam);
        }
        panel.setTeamStatisticsTable(teamToStatisticsTables);
    }
    
    private InstructorResultsQuestionTable buildQuestionTable(FeedbackQuestionAttributes question,
                                                              List<FeedbackResponseAttributes> responses,
                                                              ViewType statisticsViewType, String additionalInfoId,
                                                              boolean isIncludeMissingResponses) {
        
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        String statisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, 
                                                                                 this, bundle, statisticsViewType.name());
        List<InstructorResultsResponseRow> responseRows = isIncludeMissingResponses ? 
                                                          buildResponseRowsForQuestion(question, responses, statisticsViewType) :
                                                          buildResponseRowsForQuestionWithoutMissingResponses(
                                                                                          question, responses, statisticsViewType);
        
        boolean isCollapsible = true;
        List<ElementTag> columnTags = new ArrayList<ElementTag>();
        Map<String, Boolean> isSortable = new HashMap<String, Boolean>();
        
        switch(statisticsViewType) {
            case QUESTION:
                buildTableColumnHeaderForQuestionView(columnTags, isSortable);
                break;
            case GIVER_QUESTION_RECIPIENT:
                buildTableColumnHeaderForGiverQuestionRecipientView(columnTags, isSortable);
                isCollapsible = false;
                break;
            case RECIPIENT_QUESTION_GIVER:
                buildTableColumnHeaderForRecipientQuestionGiverView(columnTags, isSortable);
                isCollapsible = false;
                break;
            default:
                Assumption.fail("Invalid view type");
        }
        
        InstructorResultsQuestionTable questionTable = new InstructorResultsQuestionTable(this, 
                                                                   responses, statisticsTable, 
                                                                   responseRows, question, additionalInfoId, 
                                                                   columnTags, isSortable);
        questionTable.setCollapsible(isCollapsible);
        questionTable.setShowResponseRows(true);
        
        questionTable.setColumns(columnTags);
        
        return questionTable;
    }

    private void buildTableColumnHeaderForQuestionView(List<ElementTag> columnTags, 
                                                       Map<String, Boolean> isSortable) {
        ElementTag giverElement = new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none", "onclick", "toggleSort(this,1)", "style", "width: 15%;");
        ElementTag giverTeamElement = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-none", "onclick", "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag recipientElement = new ElementTag("Recipient", "id", "button_sortToName", "class", "button-sort-none", "onclick", "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag recipientTeamElement = new ElementTag("Team", "id", "button_sortToTeam", "class", "button-sort-ascending", "onclick", "toggleSort(this,4)", "style", "width: 15%;");
        ElementTag responseElement = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", "toggleSort(this,5)");
        ElementTag actionElement = new ElementTag("Actions");
        
        columnTags.add(giverElement);
        columnTags.add(giverTeamElement);
        columnTags.add(recipientElement);
        columnTags.add(recipientTeamElement);
        columnTags.add(responseElement);
        columnTags.add(actionElement);
        
        isSortable.put(giverElement.getContent(), true);
        isSortable.put(giverTeamElement.getContent(), true);
        isSortable.put(recipientElement.getContent(), true);
        isSortable.put(responseElement.getContent(), true);
        isSortable.put(actionElement.getContent(), false);
    }
    
    private void buildTableColumnHeaderForGiverQuestionRecipientView(List<ElementTag> columnTags,
                                                                     Map<String, Boolean> isSortable) {
        ElementTag photoElement = new ElementTag("Photo");
        ElementTag recipientElement = new ElementTag("Recipient", "id", "button_sortTo", "class", "button-sort-none", "onclick", "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag recipientTeamElement = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending", "onclick", "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag responseElement = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", "toggleSort(this,4)");

        columnTags.add(photoElement);
        columnTags.add(recipientElement);
        columnTags.add(recipientTeamElement);
        columnTags.add(responseElement);
        
        isSortable.put(photoElement.getContent(), false);
        isSortable.put(recipientTeamElement.getContent(), true);
        isSortable.put(recipientElement.getContent(), true);
        isSortable.put(responseElement.getContent(), true);

    }
    
    private void buildTableColumnHeaderForRecipientQuestionGiverView(List<ElementTag> columnTags,
                                    Map<String, Boolean> isSortable) {
        ElementTag photoElement = new ElementTag("Photo");
        ElementTag giverElement = new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none", "onclick", "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag giverTeamElement = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending", "onclick", "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag responseElement = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", "toggleSort(this,4)");
        ElementTag actionElement = new ElementTag("Actions");
        
        columnTags.add(photoElement);
        columnTags.add(giverElement);
        columnTags.add(giverTeamElement);
        columnTags.add(responseElement);
        columnTags.add(actionElement);
        
        isSortable.put(photoElement.getContent(), false);
        isSortable.put(giverTeamElement.getContent(), true);
        isSortable.put(giverElement.getContent(), true);
        isSortable.put(responseElement.getContent(), true);
        isSortable.put(actionElement.getContent(), false);
        
    }
    
    /**
     * Builds response rows for a given question. This not only builds response rows for existing responses, but includes 
     * the missing responses between pairs of givers and recipients.
     * @param question
     * @param responses  existing responses for the question
     */
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestion(FeedbackQuestionAttributes question,
                                                                            List<FeedbackResponseAttributes> responses,
                                                                            ViewType viewType) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        List<String> possibleGiversWithoutResponses = bundle.getPossibleGivers(question);
        List<String> possibleReceiversWithoutResponsesForGiver = new ArrayList<String>();

        String prevGiver = "";
        
        for (FeedbackResponseAttributes response : responses) {
            if (!bundle.isGiverVisible(response) || !bundle.isRecipientVisible(response)) {
                possibleGiversWithoutResponses.clear();
                possibleReceiversWithoutResponsesForGiver.clear();
            }
            
            // keep track of possible givers who did not give a response
            removeParticipantIdentifierFromList(question.giverType, possibleGiversWithoutResponses, 
                                                response.giverEmail);
            
            boolean isNewGiver = !prevGiver.equals(response.giverEmail); 
            if (isNewGiver) {
                responseRows.addAll(buildResponseRowsBetweenGiverAndPossibleRecipients(
                                    question, possibleReceiversWithoutResponsesForGiver, prevGiver, 
                                    bundle.getNameForEmail(prevGiver), bundle.getTeamNameForEmail(prevGiver), 
                                    viewType));
                
                String giverIdentifier = (question.giverType == FeedbackParticipantType.TEAMS) ? 
                                         bundle.getFullNameFromRoster(response.giverEmail) :
                                         response.giverEmail;
                            
                possibleReceiversWithoutResponsesForGiver = bundle.getPossibleRecipients(question, giverIdentifier);
            }
            
            // keep track of possible recipients without a response from the current giver
            removeParticipantIdentifierFromList(question.recipientType, possibleReceiversWithoutResponsesForGiver, response.recipientEmail);
            prevGiver = response.giverEmail;
            
            InstructorResultsModerationButton moderationButton = buildModerationButtonForExistingResponse(question, response);
            
            InstructorResultsResponseRow responseRow = new InstructorResultsResponseRow(
                                                               bundle.getGiverNameForResponse(question, response), bundle.getTeamNameForEmail(response.giverEmail), 
                                                               bundle.getRecipientNameForResponse(question, response), bundle.getTeamNameForEmail(response.recipientEmail), 
                                                               bundle.getResponseAnswerHtml(response, question), 
                                                               bundle.isGiverVisible(response), moderationButton);
            configureResponseRowForViewType(question, viewType, prevGiver, response.recipientEmail, responseRow);
            
            
            responseRows.add(responseRow);
        }
        
        responseRows.addAll(getRemainingResponseRows(question, 
                                                     possibleGiversWithoutResponses, 
                                                     possibleReceiversWithoutResponsesForGiver, 
                                                     prevGiver, viewType));
        
        return responseRows;
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionWithoutMissingResponses(FeedbackQuestionAttributes question,
                                                    List<FeedbackResponseAttributes> responses,
                                                    ViewType viewType) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        String prevGiver = "";
        
        for (FeedbackResponseAttributes response : responses) {
            InstructorResultsModerationButton moderationButton = buildModerationButtonForExistingResponse(question, response);
            
            InstructorResultsResponseRow responseRow = new InstructorResultsResponseRow(
                                   bundle.getGiverNameForResponse(question, response), bundle.getTeamNameForEmail(response.giverEmail), 
                                   bundle.getRecipientNameForResponse(question, response), bundle.getTeamNameForEmail(response.recipientEmail), 
                                   bundle.getResponseAnswerHtml(response, question), 
                                   bundle.isGiverVisible(response), moderationButton);
            configureResponseRowForViewType(question, viewType, prevGiver, response.recipientEmail, responseRow);
            
            
            responseRows.add(responseRow);
        }
        

        
        return responseRows;
    }

    private void configureResponseRowForViewType(FeedbackQuestionAttributes question,
                                                 ViewType statisticsViewType, 
                                                 String giver, String recipient,
                                                 InstructorResultsResponseRow responseRow) {
        switch (statisticsViewType) {
            case QUESTION:
                responseRow.setGiverProfilePictureDisplayed(question.isGiverAStudent());
                responseRow.setGiverProfilePictureLink(new Url(getProfilePictureLink(giver)));
                
                responseRow.setRecipientProfilePictureDisplayed(question.isRecipientAStudent());
                responseRow.setRecipientProfilePictureLink(new Url(getProfilePictureLink(recipient)));
                responseRow.setActionsDisplayed(true);
                break;
            case GIVER_QUESTION_RECIPIENT:
                responseRow.setGiverDisplayed(false);
                responseRow.setGiverProfilePictureDisplayed(false);
                
                responseRow.setRecipientProfilePictureAColumn(true);
                responseRow.setRecipientProfilePictureDisplayed(question.isRecipientAStudent());
                responseRow.setRecipientProfilePictureLink(new Url(getProfilePictureLink(recipient)));
                responseRow.setActionsDisplayed(false);
                break;
            case RECIPIENT_QUESTION_GIVER:
                responseRow.setRecipientDisplayed(false);
                responseRow.setRecipientProfilePictureDisplayed(false);
                
                responseRow.setGiverProfilePictureAColumn(true);
                responseRow.setGiverProfilePictureDisplayed(question.isGiverAStudent());
                responseRow.setGiverProfilePictureLink(new Url(getProfilePictureLink(giver)));
                responseRow.setActionsDisplayed(true);
                break;
            default:
                Assumption.fail();            
        }
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsBetweenGiverAndPossibleRecipients(
                                                                    FeedbackQuestionAttributes question, 
                                                                    List<String> possibleReceivers, 
                                                                    String giverIdentifier,
                                                                    String giverName, String giverTeam, ViewType viewType) {
        List<InstructorResultsResponseRow> missingResponses = new ArrayList<InstructorResultsResponseRow>();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        
        for (String possibleRecipient : possibleReceivers) {
            
            String possibleRecipientName = bundle.getFullNameFromRoster(possibleRecipient);
            String possibleRecipientTeam = bundle.getTeamNameFromRoster(possibleRecipient);
            
            String textToDisplay = questionDetails.getNoResponseTextInHtml(giverIdentifier, possibleRecipient, bundle, question);
            
            if (questionDetails.shouldShowNoResponseText(giverIdentifier, possibleRecipient, question)) {
                InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(question, giverIdentifier, "btn btn-default btn-xs", "Moderate Response");
                InstructorResultsResponseRow missingResponse = new InstructorResultsResponseRow(giverName, giverTeam, possibleRecipientName, possibleRecipientTeam, 
                                                                                                textToDisplay, true, moderationButton, true);
                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                
                configureResponseRowForViewType(question, viewType, giverIdentifier, possibleRecipient, missingResponse);
                missingResponses.add(missingResponse);
            }
        }
        
        return missingResponses;
    }

    /**
     * Given a participantIdentifier, remove it from participantIdentifierList. 
     * 
     * Before removal, FeedbackSessionResultsBundle.getNameFromRoster is used to 
     * convert the identifier into a canonical form if the participantIdentifierType is TEAMS. 
     *  
     * @param participantIdentifierType
     * @param participantIdentifierList
     * @param participantIdentifier
     */
    private void removeParticipantIdentifierFromList(
            FeedbackParticipantType participantIdentifierType,
            List<String> participantIdentifierList, String participantIdentifier) {
        if (participantIdentifierType == FeedbackParticipantType.TEAMS) {
            participantIdentifierList.remove(bundle.getFullNameFromRoster(participantIdentifier)); 
        } else {
            participantIdentifierList.remove(participantIdentifier);
        }
    }
    
    private List<InstructorResultsResponseRow> getRemainingResponseRows(
                                                FeedbackQuestionAttributes question,
                                                List<String> remainingPossibleGivers,
                                                List<String> possibleRecipientsForGiver, String prevGiver, ViewType viewType) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        if (possibleRecipientsForGiver != null) {
            responseRows.addAll(buildResponseRowsBetweenGiverAndPossibleRecipients(question, 
                                                                                   possibleRecipientsForGiver,
                                                                                   prevGiver, 
                                                                                   bundle.getNameForEmail(prevGiver), 
                                                                                   bundle.getTeamNameForEmail(prevGiver),
                                                                                   viewType));
            
        }
        
        removeParticipantIdentifierFromList(question.giverType, remainingPossibleGivers, prevGiver);
            
        for (String possibleGiverWithNoResponses : remainingPossibleGivers) {
            if (!selectedSection.equals("All") && !bundle.getSectionFromRoster(possibleGiverWithNoResponses).equals(selectedSection)) {
                continue;
            }
            possibleRecipientsForGiver = bundle.getPossibleRecipients(question, possibleGiverWithNoResponses);
            
            responseRows.addAll(buildResponseRowsBetweenGiverAndPossibleRecipients(
                                    question, possibleRecipientsForGiver, possibleGiverWithNoResponses, 
                                    bundle.getFullNameFromRoster(possibleGiverWithNoResponses),
                                    bundle.getTeamNameFromRoster(possibleGiverWithNoResponses), 
                                    viewType));
        }
        
        return responseRows;
    }
    

    private InstructorResultsModerationButton buildModerationButtonForExistingResponse(FeedbackQuestionAttributes question,
                                                                      FeedbackResponseAttributes response) {
        return buildModerationButtonForGiver(question, response.giverEmail, "btn btn-default btn-xs", "Moderate Response");
    }
    
    private InstructorResultsModerationButton buildModerationButtonForGiver(FeedbackQuestionAttributes question,
                                                                            String giverEmail, String className,
                                                                            String buttonText) {
        boolean isAllowedToModerate = instructor.isAllowedForPrivilege(bundle.getSectionFromRoster(giverEmail), 
                                                     feedbackSessionName, 
                                                     Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isDisabled = !isAllowedToModerate;
        
        String giverIdentifier = giverEmail;
        if (question != null) {
            giverIdentifier = question.giverType.isTeam() ? 
                              giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"") : 
                              giverIdentifier;
        }
        
        InstructorResultsModerationButton moderationButton = new InstructorResultsModerationButton(isAllowedToModerate, isDisabled,
                                                                 className, giverIdentifier, 
                                                                 courseId, feedbackSessionName, 
                                                                 question, buttonText);
        return moderationButton;
   }
    
   private InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanel(FieldValidator validator, 
                                                                                                            String participantIdentifier, String participantName, 
                                                                                                            FeedbackQuestionAttributes question, 
                                                                                                            List<InstructorResultsQuestionTable> questionTables,
                                                                                                            InstructorResultsModerationButton moderationButton, 
                                                                                                            boolean isModerationButtonDisplayed,
                                                                                                            boolean isGiver) {
       boolean isEmailValid = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
    
       Url profilePictureLink = new Url(getProfilePictureLink(participantIdentifier));
    
       String mailtoStyle = "";
       if (question != null) {
           mailtoStyle = (question.giverType == FeedbackParticipantType.NONE 
                       || question.giverType == FeedbackParticipantType.TEAMS 
                       || participantIdentifier.contains("@@")) ? "style=\"display:none;\""
                                                                   : "";
       }
       
       InstructorResultsQuestionTable.sortByQuestionNumber(questionTables);
       InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
                                    InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                    bundle,
                                                                    questionTables, isEmailValid, profilePictureLink, 
                                                                    mailtoStyle, isGiver, participantIdentifier, participantName,
                                                                    moderationButton, isModerationButtonDisplayed);
    
       return giverPanel;
   }

    /* 
     * The next three methods are not covered in action test, but covered in UI tests.
     */

    /*
    public FeedbackSessionPublishButton getInstructorFeedbackSessionPublishAndUnpublishAction(
                                                                        FeedbackSessionAttributes session,
                                                                        boolean isHome,
                                                                        InstructorAttributes instructor) {
        return new FeedbackSessionPublishButton(this, session, isHome, instructor, "btn-primary btn-block");
    }
    */

    /**
     * TODO: re-use {@link FeedbackSessionPublishButton} when migrating this to JSTL.<br>
     * As a shortcut, un-comment the above method, making necessary changes, and remove this one.
     */
    public String getInstructorFeedbackSessionPublishAndUnpublishAction(FeedbackSessionAttributes session,
                                                                        boolean isHome,
                                                                        InstructorAttributes instructor) {
        boolean hasPublish = !session.isWaitingToOpen() && !session.isPublished();
        boolean hasUnpublish = !session.isWaitingToOpen() && session.isPublished();
        String disabledStr = "disabled=\"disabled\"";
        String disableUnpublishSessionStr = 
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" 
                                                                                                         : disabledStr;
        String disablePublishSessionStr = 
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" 
                                                                                                         : disabledStr;
        String result = "";
        if (hasUnpublish) {
            result =
                "<a class=\"btn btn-primary btn-block btn-tm-actions session-unpublish-for-test\""
                    + "href=\"" + getInstructorFeedbackSessionUnpublishLink(session.courseId, 
                                                                            session.feedbackSessionName, 
                                                                            isHome) + "\" " 
                    + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH + "\" data-toggle=\"tooltip\" "
                    + "data-placement=\"top\" onclick=\"return toggleUnpublishEvaluation('" 
                    + session.feedbackSessionName + "');\" " + disableUnpublishSessionStr + ">Unpublish Results</a> ";
        } else {
            result = "<a class=\"btn btn-primary btn-block btn-tm-actions session-publish-for-test" 
                   + (hasPublish ? "\"" : DISABLED) + "href=\""
                   + getInstructorFeedbackSessionPublishLink(session.courseId, session.feedbackSessionName,
                                                             isHome) 
                   + "\" " + "title=\""
                   + (hasPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH 
                                 : Const.Tooltips.FEEDBACK_SESSION_AWAITING)
                   + "\" " + "data-toggle=\"tooltip\" data-placement=\"top\""
                   + (hasPublish ? "onclick=\"return togglePublishEvaluation('" + session.feedbackSessionName + "', " 
                                                                                + session.isPublishedEmailEnabled + ");\" " 
                                              : " ") 
                   + disablePublishSessionStr + ">Publish Results</a> ";
        }
        return result;
    }

    public String getResultsVisibleFromText() {
        if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            if (bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
                return TimeHelper.formatTime(bundle.feedbackSession.startTime);
            } else if (bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
                return "Never";
            } else {
                return TimeHelper.formatTime(bundle.feedbackSession.sessionVisibleFromTime);
            }
        } else if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return "I want to manually publish the results.";
        } else if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return "Never";
        } else {
            return TimeHelper.formatTime(bundle.feedbackSession.resultsVisibleFromTime);
        }
    }

    public String getProfilePictureLink(String studentEmail) {
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE
                + "?" + Const.ParamsNames.STUDENT_EMAIL + "="
                + StringHelper.encrypt(studentEmail)
                + "&" + Const.ParamsNames.COURSE_ID + "="
                + StringHelper.encrypt(instructor.courseId)
                + "&" + Const.ParamsNames.USER_ID + "=" + account.googleId;
    }

    public static String getExceedingResponsesErrorMessage() {
        return EXCEEDING_RESPONSES_ERROR_MESSAGE;
    }

    public FeedbackSessionResultsBundle getBundle() {
        return bundle;
    }

    public InstructorAttributes getInstructor() {
        return instructor;
    }

    public List<String> getSections() {
        return sections;
    }

    public String getSelectedSection() {
        return selectedSection;
    }

    public String getSortType() {
        return sortType;
    }

    public String getGroupByTeam() {
        return groupByTeam != null? groupByTeam : "null";
    }

    public String getShowStats() {
        return showStats;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getAjaxStatus() {
        return ajaxStatus;
    }

    public String getSessionResultsHtmlTableAsString() {
        return sessionResultsHtmlTableAsString;
    }
    
    public boolean isShouldCollapsed() {
        return shouldCollapsed;
    }

    public void setShouldCollapsed(boolean shouldCollapsed) {
        this.shouldCollapsed = shouldCollapsed;
    }

    public List<InstructorResultsQuestionTable> getQuestionPanels() {
        return questionPanels;
    }

    public Map<String, InstructorFeedbackResultsSectionPanel> getSectionPanels() {
        return sectionPanels;
    }

    public void setSectionPanels(LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels) {
        this.sectionPanels = sectionPanels;
    }

    
    
    
    
}
