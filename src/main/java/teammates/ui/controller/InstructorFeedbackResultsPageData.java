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
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.template.InstructorFeedbackResultsFilterPanel;
import teammates.ui.template.InstructorFeedbackResultsSessionPanel;
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
    private boolean isPanelsCollapsed;
    
    private FieldValidator validator = new FieldValidator();


    // used for html table ajax loading
    public String courseId = null;
    public String feedbackSessionName = null;
    public String ajaxStatus = null;
    public String sessionResultsHtmlTableAsString = null;
    

    // TODO multiple page data classes for each view type
    
    // for question view
    List<InstructorResultsQuestionTable> questionPanels;
    // for giver > question > recipient, recipient > question > giver...
    LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels;
    
    enum ViewType {
        QUESTION, GIVER_QUESTION_RECIPIENT, RECIPIENT_QUESTION_GIVER;
        
        public String toString() {
            return name().toLowerCase().replaceAll("_", "-");
        }
        
        public boolean isFirstGroupedByGiver() {
            return this == GIVER_QUESTION_RECIPIENT;
        }
    }
    ViewType viewType;
    
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
        startIndex = -1;
    }
    
    public void initForViewByQuestion() {
        viewType = ViewType.QUESTION;
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorResultsQuestionTable>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : questionToResponseMap.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();
            
            questionPanels.add(buildQuestionTableAndResponseRows(question, responses, viewType, ""));
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
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            buildSectionPanelsForForAjaxLoading(sections);
            return;
        }
        
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > 500);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByGiverQuestionRecipient(true);
        
        buildResponsesPanelsForGiverQuestionRecipient(sortedResponses);
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
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            buildSectionPanelsForForAjaxLoading(sections);
            return;
        }
        
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > 500);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByRecipientQuestionGiver(true);
       
        buildResponsesPanelsForRecipientQuestionGiver(sortedResponses);
    }

    private void buildResponsesPanelsForGiverQuestionRecipient(
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {
        viewType = ViewType.GIVER_QUESTION_RECIPIENT;
        final String additionalInfoId = "giver-%s-question-%s";
        
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByGiverTeam();
        
        
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        // Maintain previous section and previous team while iterating through the loop
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        Set<String> sectionsWithResponses = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();                                
        
        int giverIndex = this.startIndex;
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver : 
                                                                    sortedResponses.entrySet()) {
            giverIndex += 1;
            String giverIdentifier = responsesFromGiver.getKey();
            
            String currentTeam = getCurrentTeam(bundle, giverIdentifier);
            String currentSection = getCurrentGiverSection(responsesFromGiver);
            
            // Change in team
            if (!prevTeam.equals(currentTeam)) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    buildMissingParticipantPanelsWithModerationButtonForPrevTeam(
                                                    sectionPanel, prevTeam, teamMembersWithResponses,
                                                    viewType.isFirstGroupedByGiver());
                    teamMembersWithResponses.clear(); 
                }
                
                prevTeam = currentTeam;
                teamsWithResponses.add(currentTeam);
            }
            
            // Change in section
            if (!prevSection.equals(currentSection)) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    // add sectionPanel to sectionPanels,
                    // and initialize next section panel
                    prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
                    buildTeamsStatisticsTableForSectionPanel(sectionPanel, responsesGroupedByTeam, 
                                                             teamsWithResponses);
                    sectionPanel.setSectionName(prevSection);
                    sectionPanel.setSectionNameForDisplay(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a section" 
                                                                                                    : prevSection);
                    sectionPanel.setDisplayingTeamStatistics(true);
                    sectionPanels.put(prevSection, sectionPanel);
                    buildMissingTeamAndParticipantPanelsWithModerationButtonForSection(sectionPanel, prevSection, 
                                                                                       teamsWithResponses, currentTeam);
                    sectionsWithResponses.add(prevSection);
                    
                    teamsWithResponses.clear();
                    teamsWithResponses.add(currentTeam);
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }                
            }
            
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel = buildGiverPanel(viewType,
                                            additionalInfoId, sectionPanel, giverIndex, responsesFromGiver,
                                            giverIdentifier, currentTeam);
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, giverPanel);
            teamMembersWithResponses.add(giverIdentifier);
            
            prevSection = currentSection;
        }
        
        // for the last section
        prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
        buildTeamsStatisticsTableForSectionPanel(sectionPanel, responsesGroupedByTeam, 
                                                 teamsWithResponses);      
        sectionPanel.setSectionName(prevSection);
        sectionPanel.setSectionNameForDisplay(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a section" 
                                                                                        : prevSection);
        sectionPanel.setDisplayingTeamStatistics(true);
        sectionPanels.put(prevSection, sectionPanel);
        teamsWithResponses.add(prevTeam);
        sectionsWithResponses.add(prevSection);
        
        buildTeamAndParticipantPanelsForLastParticipantSection(sectionPanel, prevSection, prevTeam,
                                                     sectionsWithResponses, teamsWithResponses,
                                                     teamMembersWithResponses);

        // display missing sections
        buildSectionPanelsForMissingSections(sectionsWithResponses, viewType.isFirstGroupedByGiver());
        
    }
    
    private void buildResponsesPanelsForRecipientQuestionGiver(
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {
        viewType = ViewType.RECIPIENT_QUESTION_GIVER;
        final String additionalInfoId = "recipient-%s-question-%s";
        final boolean isGiver = false;
        
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByRecipientTeam();
        
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        
        // Maintain previous section and previous team while iterating through the loop
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        Set<String> sectionsWithResponses = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();      
        
        int recipientIndex = this.startIndex;
  
        // Iterate through the recipients
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesToRecipient : 
                                                                    sortedResponses.entrySet()) {
            recipientIndex += 1;
            String recipientIdentifier = responsesToRecipient.getKey();
            
            String currentTeam = getCurrentTeam(bundle, recipientIdentifier);
            String currentSection = getCurrentRecipientSection(responsesToRecipient);
            
            // Change in team
            if (!prevTeam.equals(currentTeam)) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    buildMissingParticipantPanelsWithoutModerationButtonForteam(
                                                    sectionPanel, prevTeam, 
                                                    teamMembersWithResponses, isGiver);
                    teamMembersWithResponses.clear(); 
                }
                prevTeam = currentTeam;
                teamsWithResponses.add(currentTeam);
            }
            
            // Change in section
            if (!prevSection.equals(currentSection)) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    // add to sectionPanels,
                    // and initialize next section panel
                    
                    prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
                    buildTeamsStatisticsTableForSectionPanel(sectionPanel, 
                                                    responsesGroupedByTeam, 
                                                    teamsWithResponses);
                    sectionPanel.setSectionName(prevSection);
                    sectionPanel.setSectionNameForDisplay(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a section" 
                                                                                                    : prevSection);
                    sectionPanel.setDisplayingTeamStatistics(true);
                    sectionPanels.put(prevSection, sectionPanel);
                    
                    buildMissingTeamAndParticipantPanelsWithoutModerationButtonForSection(
                                                    sectionPanel, prevSection,  
                                                    teamsWithResponses, currentTeam, isGiver);
                    sectionsWithResponses.add(prevSection);
                    
                    teamsWithResponses.clear();
                    teamsWithResponses.add(currentTeam);
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }
                
            }
            
            InstructorFeedbackResultsGroupByQuestionPanel recipientPanel = buildRecipientPanel(viewType,
                                            additionalInfoId, sectionPanel, recipientIndex,
                                            responsesToRecipient, recipientIdentifier, currentTeam);
            
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, recipientPanel);
            teamMembersWithResponses.add(recipientIdentifier);
            
            prevSection = currentSection;
        }
        
        // for the last section
        prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
        buildTeamsStatisticsTableForSectionPanel(sectionPanel, 
                                        responsesGroupedByTeam, 
                                        teamsWithResponses);
        sectionPanel.setSectionName(prevSection);
        sectionPanel.setSectionNameForDisplay(prevSection.equals(Const.DEFAULT_SECTION) ? "Not in a section" 
                                                                                        : prevSection);
        sectionPanel.setDisplayingTeamStatistics(true);
        sectionPanels.put(prevSection, sectionPanel);
        teamsWithResponses.add(prevTeam);
        sectionsWithResponses.add(prevSection);
        buildTeamAndParticipantPanelsForLastParticipantSection(sectionPanel, prevSection, prevTeam,
                                                     sectionsWithResponses, teamsWithResponses,
                                                     teamMembersWithResponses);

        // display missing sections
        buildSectionPanelsForMissingSections(sectionsWithResponses, isGiver);
    }

    private InstructorFeedbackResultsGroupByQuestionPanel buildRecipientPanel(
                                    final ViewType viewType,
                                    final String additionalInfoId,
                                    InstructorFeedbackResultsSectionPanel sectionPanel,
                                    int recipientIndex,
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesToRecipient,
                                    String recipientIdentifier, String currentTeam) {
        return buildParticipantPanel(viewType, additionalInfoId, sectionPanel, recipientIndex, responsesToRecipient, recipientIdentifier, currentTeam);
    }
    
    private InstructorFeedbackResultsGroupByQuestionPanel buildGiverPanel(
                                    final ViewType viewType,
                                    final String additionalInfoId,
                                    InstructorFeedbackResultsSectionPanel sectionPanel,
                                    int giverIndex,
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver,
                                    String giverIdentifier, String currentTeam) {
        return buildParticipantPanel(viewType, additionalInfoId, sectionPanel, giverIndex, responsesFromGiver, giverIdentifier, currentTeam);
    }
    
    private InstructorFeedbackResultsGroupByQuestionPanel buildParticipantPanel(
                                    ViewType viewType, String additionalInfoId,
                                    InstructorFeedbackResultsSectionPanel sectionPanel,
                                    int participantIndex,
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesForParticipant,
                                    String participantIdentifier, String currentTeam) {
        List<InstructorResultsQuestionTable> questionTables = new ArrayList<InstructorResultsQuestionTable>();
        
        int questionIndex = 0;
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForParticipantForQuestion : 
                                                                                     responsesForParticipant.getValue().entrySet()) {
            if (responsesForParticipantForQuestion.getValue().isEmpty()) {
                // participant has no responses for the current question
                continue;
            }
            
            questionIndex += 1;
            
            FeedbackQuestionAttributes currentQuestion = responsesForParticipantForQuestion.getKey();
            List<FeedbackResponseAttributes> responsesForQuestion = responsesForParticipantForQuestion.getValue();
            
            sectionPanel.getIsTeamWithResponses().put(currentTeam, true);

            InstructorResultsQuestionTable questionTable 
                = buildQuestionTableAndResponseRows(currentQuestion, responsesForQuestion,
                                                    viewType, String.format(additionalInfoId, participantIndex, questionIndex), 
                                                    participantIdentifier, true);
            questionTable.setBoldQuestionNumber(false);
            questionTables.add(questionTable);
      
        }
        
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel;
        if (viewType.isFirstGroupedByGiver()) {
            // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
            InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                      null, participantIdentifier,
                                                                      "btn btn-primary btn-xs",
                                                                      "Moderate Responses");
            participantPanel = buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                         participantIdentifier, 
                                                         bundle.getNameForEmail(participantIdentifier),
                                                         questionTables, moderationButton, 
                                                         bundle.isParticipantIdentifierStudent(participantIdentifier));
        } else {
            participantPanel = buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                                            participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier),
                                            questionTables);
        }
        return participantPanel;
    }

    private void buildMissingTeamAndParticipantPanelsWithoutModerationButtonForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String section,
                                    Set<String> receivingTeams, String currentTeam, 
                                    boolean isGiver) {
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, section, receivingTeams, currentTeam, 
                                                       false);
    }
    
    private void buildMissingTeamAndParticipantPanelsWithModerationButtonForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String section,
                                    Set<String> receivingTeams, String currentTeam 
                                    ) {
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, section, receivingTeams, currentTeam, 
                                                       true);
    }
    
    private void buildMissingTeamAndParticipantPanelsForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String prevSection,
                                    Set<String> receivingTeams, String currentTeam, 
                                    boolean isWithModerationButton) {

        
        // update the teams for the previous section
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(prevSection);
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(receivingTeams);
        
        // create for every remaining team in the section, participantResultsPanels for every team member
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembersOfTeam = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            if (isWithModerationButton) {
                addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, teamWithoutResponses, teamMembersOfTeam);
            } else {
                addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, teamWithoutResponses, teamMembersOfTeam);
            }
        }
        
    }

    private void buildTeamAndParticipantPanelsForLastParticipantSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String prevSection,
                                    String prevTeam, Set<String> receivingSections, Set<String> receivingTeams, 
                                    Set<String> teamMembersWithResponses) {
        boolean isGiver = viewType.isFirstGroupedByGiver();
        
        Set<String> teamMembersWithoutResponses = new HashSet<String>(bundle.getTeamMembersFromRoster(prevTeam));
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
        
        // for printing the participants without responses in the last response participant's team 
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
        Collections.sort(sortedTeamMembersWithoutResponses);
        
        if (isGiver) {
            addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, prevTeam,
                                                        sortedTeamMembersWithoutResponses);
        } else {
            addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, prevTeam,
                                            sortedTeamMembersWithoutResponses);
        }
        
        receivingTeams.add(prevTeam);
        
        // for printing the teams without responses in last section having responses
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(prevSection);
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(receivingTeams);
        for (String teamWithoutResponses : teamsWithoutResponses) {
            
            List<String> teamMembersOfTeam = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            
            if (isGiver) {
                addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, teamWithoutResponses, 
                                                                                teamMembersOfTeam);
            } else {
                addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, teamWithoutResponses, 
                                                teamMembersOfTeam);
            }
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
    
    private String getCurrentGiverSection(
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver) {
        return getCurrentSection(responsesFromGiver, true);
    }
    
    private String getCurrentRecipientSection(
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromRecipient) {
        return getCurrentSection(responsesFromRecipient, false);
    }
    

    /**
     * Uses the first response to get the current section
     * @param responses
     */
    private String getCurrentSection(
                        Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responses,
                        boolean isGiver) {
        String currentSection = Const.DEFAULT_SECTION;
        // update current section
        // retrieve section from the first response of this user
        // TODO simplify by introducing more data structures into bundle
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromGiverForQuestion : 
                                                                                     responses.getValue().entrySet()) {
            if (responsesFromGiverForQuestion.getValue().isEmpty()) {
                continue;
            }
            FeedbackResponseAttributes firstResponse = responsesFromGiverForQuestion.getValue().get(0);
            currentSection = isGiver ? firstResponse.giverSection : firstResponse.recipientSection ;
            break;
        }
        
        return currentSection;
    }

    private void buildMissingParticipantPanelsWithModerationButtonForPrevTeam(
                                            InstructorFeedbackResultsSectionPanel sectionPanel, String prevTeam,
                                            Set<String> teamMembersWithResponses,
                                            boolean isFirstGroupedByGiver) {
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, 
                                                  teamMembersWithResponses, true, isFirstGroupedByGiver);
    }
    
    private void buildMissingParticipantPanelsWithoutModerationButtonForteam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String team,
                                    Set<String> teamMembersWithResponses,
                                    boolean isFirstGroupedByGiver) {
        buildMissingParticipantPanelsForTeam(sectionPanel, team, 
                                                  teamMembersWithResponses, false, isFirstGroupedByGiver);
    }    
    
    private void buildMissingParticipantPanelsForTeam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String team,
                                    Set<String> teamMembersWithResponses,
                                    boolean isDisplayingModerationButton, boolean isFirstGroupedByGiver) {
        
        Set<String> teamMembersEmail = new HashSet<String>();
        teamMembersEmail.addAll(bundle.getTeamMembersFromRoster(team));
        
        Set<String> teamMembersWithoutResponses = new HashSet<String>(teamMembersEmail);
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
        
        // Create missing participants panels for the previous team
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
        Collections.sort(sortedTeamMembersWithoutResponses);
        
        if (isDisplayingModerationButton) {
            addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, 
                                                        team, sortedTeamMembersWithoutResponses);
        } else {
            addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, 
                                                        team, sortedTeamMembersWithoutResponses);
        }
        
    }

    private void buildSectionPanelsForMissingSections(Set<String> receivingSections,
                                                       boolean isGiver) {
        InstructorFeedbackResultsSectionPanel sectionPanel;
        
        Set<String> sectionsInCourse = bundle.rosterSectionTeamNameTable.keySet();
        Set<String> sectionsWithNoResponseReceived = new HashSet<String>(sectionsInCourse);
        sectionsWithNoResponseReceived.removeAll(receivingSections);
        
        // TODO introduce enums for this, because this causes problems if there is a section named "All"
        if (selectedSection.equals("All")) {
            List<String> sectionsWithNoResponseReceivedList = new ArrayList<String>(sectionsWithNoResponseReceived);
            Collections.sort(sectionsWithNoResponseReceivedList);
            
            for (String sectionWithNoResponseReceived: sectionsWithNoResponseReceivedList) {
                sectionPanel = new InstructorFeedbackResultsSectionPanel();
                sectionPanel.setSectionName(sectionWithNoResponseReceived);
                sectionPanel.setSectionNameForDisplay(sectionWithNoResponseReceived.equals(Const.DEFAULT_SECTION) ? "Not in a Section" : sectionWithNoResponseReceived);
                sectionPanel.setDisplayingTeamStatistics(true);
                sectionPanels.put(sectionWithNoResponseReceived, sectionPanel);
                
                Set<String> teamsFromSection = bundle.getTeamsInSectionFromRoster(sectionWithNoResponseReceived);
                List<String> teamsFromSectionList = new ArrayList<String>(teamsFromSection);
                
                Collections.sort(teamsFromSectionList);
                
                for (String teamInMissingSection : teamsFromSectionList) {
                    List<String> teamMembers = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamInMissingSection));
                    Collections.sort(teamMembers);
                    
                    if (isGiver) {
                        addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, teamInMissingSection, 
                                                                                        teamMembers);
                    } else {
                        addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, teamInMissingSection, 
                                                        teamMembers);
                    }
                }
            }
        }
    }

    private void buildSectionPanelsForForAjaxLoading(List<String> sections) {
        setShouldCollapsed(true);
        
        // TODO 
        // Abstract out "All" sections into a boolean or enum instead. Otherwise this will cause problems in future
        // if there is ever a section named "All"
        if (selectedSection.equals("All")) {
            sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
            
            for (String section : sections) {
                InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
                sectionPanel.setSectionName(section);
                sectionPanel.setSectionNameForDisplay(section);
                sectionPanel.setLoadSectionResponsesByAjax(true);
                
                sectionPanels.put(section, sectionPanel);
            }
            
            InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
            sectionPanel.setSectionName(Const.DEFAULT_SECTION);
            sectionPanel.setSectionNameForDisplay("Not in a section");
            sectionPanel.setLoadSectionResponsesByAjax(true);
            
            sectionPanels.put(Const.DEFAULT_SECTION, sectionPanel);
            
        } else {
            sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
            
            InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
            sectionPanel.setSectionName(selectedSection);
            sectionPanel.setLoadSectionResponsesByAjax(true);
            
            sectionPanels.put(selectedSection, sectionPanel);
        }
    }

    private void addMissingParticipantsForTeamToSectionPanelWithModerationButton(
                                                             InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(null, teamMember, 
                                                                           "btn btn-default btn-xs",
                                                                           "Moderate Responses");
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
                    buildInstructorFeedbackResultsGroupByQuestionPanel(teamMember, bundle.getFullNameFromRoster(teamMember),
                                                                       new ArrayList<InstructorResultsQuestionTable>(), moderationButton, 
                                                                       true);

            giverPanel.setHasResponses(false);
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }
    
    private void addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, 
                                    String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
            buildInstructorFeedbackResultsGroupByQuestionPanel(teamMember, bundle.getFullNameFromRoster(teamMember),
                                                          new ArrayList<InstructorResultsQuestionTable>(), null, 
                                                          false);
            giverPanel.setHasResponses(false);
            
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }
    

    private void addParticipantPanelToSectionPanel(
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
    }

    /**
     * 
     * 
     * @param sectionPanel
     * @param questions
     * @param responsesGroupedByTeam
     * @param teamsInSection
     */
    private void buildTeamsStatisticsTableForSectionPanel(
                     InstructorFeedbackResultsSectionPanel sectionPanel, 
                     LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                     Collection<String> teamsInSection) {
        Map<String, List<InstructorResultsQuestionTable>> teamToStatisticsTables = new HashMap<String, List<InstructorResultsQuestionTable>>();
        for (String team : teamsInSection) {
            if (!responsesGroupedByTeam.containsKey(team)) {
                continue;
            }
            
            List<InstructorResultsQuestionTable> statisticsTablesForTeam = new ArrayList<InstructorResultsQuestionTable>();
            
            for (FeedbackQuestionAttributes question : bundle.questions.values()) {
                if (!responsesGroupedByTeam.get(team).containsKey(question)) {
                    continue;
                }
                
                List<FeedbackResponseAttributes> responsesGivenTeamAndQuestion = responsesGroupedByTeam.get(team).get(question);
        
                InstructorResultsQuestionTable statsTable = buildQuestionTableWithoutResponseRows(question, 
                                                                               responsesGivenTeamAndQuestion,
                                                                               viewType, "");
                statsTable.setCollapsible(false);
                
                if (!statsTable.getQuestionStatisticsTable().isEmpty()) {
                    statisticsTablesForTeam.add(statsTable);
                }
            }
        
            InstructorResultsQuestionTable.sortByQuestionNumber(statisticsTablesForTeam);
            teamToStatisticsTables.put(team, statisticsTablesForTeam);
        }
        sectionPanel.setTeamStatisticsTable(teamToStatisticsTables);
    }

    private void prepareHeadersForTeamPanelsInSectionPanel(
                                    InstructorFeedbackResultsSectionPanel sectionPanel) {
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
    }

    
    private InstructorResultsQuestionTable buildQuestionTableAndResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    ViewType statisticsViewType, String additionalInfoId) {
        return buildQuestionTableAndResponseRows(
                                        question, responses,
                                        statisticsViewType, additionalInfoId, 
                                        null, true);   
    }
    
    private InstructorResultsQuestionTable buildQuestionTableWithoutResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    ViewType statisticsViewType, String additionalInfoId) {
        return buildQuestionTableAndResponseRows(
                                        question, responses,
                                        statisticsViewType, additionalInfoId, 
                                        null, false);   
    }
                                    
    /**
     * Builds a question table for given question, and response rows for the given responses.
     *  
     * @param question
     * @param responses
     * @param viewType
     * @param additionalInfoId
     * @param participantIdentifier  for viewTypes * > Question > *, constructs missing response rows
     *                               only for the given participant
     * @param isShowingResponseRows  if false, hides the response rows 
     */
    private InstructorResultsQuestionTable buildQuestionTableAndResponseRows(
                                                              FeedbackQuestionAttributes question,
                                                              List<FeedbackResponseAttributes> responses,
                                                              ViewType viewType, String additionalInfoId, 
                                                              String participantIdentifier, boolean isShowingResponseRows) {
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        String statisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, this, 
                                                                                 bundle, viewType.toString());

        List<ElementTag> columnTags = new ArrayList<ElementTag>();
        Map<String, Boolean> isSortable = new HashMap<String, Boolean>();
        boolean isCollapsible = true;
        List<InstructorResultsResponseRow> responseRows = null;
        
        if (isShowingResponseRows) {
            switch (viewType) {
                case QUESTION:
                    buildTableColumnHeaderForQuestionView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestion(question, responses, viewType);
                    break;
                case GIVER_QUESTION_RECIPIENT:
                    buildTableColumnHeaderForGiverQuestionRecipientView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestionForSingleGiver(question, responses, 
                                                                              viewType, participantIdentifier);
                    isCollapsible = false;
                    break;
                case RECIPIENT_QUESTION_GIVER:
                    buildTableColumnHeaderForRecipientQuestionGiverView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestionForSingleRecipient(question, responses, 
                                                                                  viewType, participantIdentifier);
                    isCollapsible = false;
                    break;
                default:
                    Assumption.fail("Invalid view type");
            }
        }
        
        InstructorResultsQuestionTable questionTable = new InstructorResultsQuestionTable(this, 
                                                            responses, statisticsTable, 
                                                            responseRows, question, additionalInfoId, 
                                                            columnTags, isSortable);
        questionTable.setShowResponseRows(isShowingResponseRows);
        questionTable.setCollapsible(isCollapsible);
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
     * 
     * @see configureResponseRowForViewType
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
                responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
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
        
        responseRows.addAll(getRemainingMissingResponseRows(question, possibleGiversWithoutResponses, 
                                                            possibleReceiversWithoutResponsesForGiver, 
                                                            prevGiver, viewType));
        
        return responseRows;
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleGiver(FeedbackQuestionAttributes question,
                                                                                          List<FeedbackResponseAttributes> responses,
                                                                                          ViewType viewType, String giverIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, viewType, giverIdentifier, true);
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleRecipient(FeedbackQuestionAttributes question,
                                                                                              List<FeedbackResponseAttributes> responses,
                                                                                              ViewType viewType, String recipientIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, viewType, recipientIdentifier, false);
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleParticipant(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    ViewType viewType, String participantIdentifier, boolean isFirstGroupedByGiver) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        List<String> possibleParticipantsWithoutResponses = isFirstGroupedByGiver ? bundle.getPossibleRecipients(question, participantIdentifier)
                                                                    : bundle.getPossibleGivers(question, participantIdentifier);
        
        for (FeedbackResponseAttributes response : responses) {
            if (!bundle.isGiverVisible(response) || !bundle.isRecipientVisible(response)) {
                possibleParticipantsWithoutResponses.clear();
            }
            
            // keep track of possible participant who did not give/receive a response to/from the participantIdentifier 
            String participantWithResponse =          isFirstGroupedByGiver ? response.recipientEmail : response.giverEmail;
            FeedbackParticipantType participantType = isFirstGroupedByGiver ? question.recipientType  : question.giverType; 
            removeParticipantIdentifierFromList(participantType, possibleParticipantsWithoutResponses, 
                                                participantWithResponse);
            
            InstructorResultsModerationButton moderationButton = buildModerationButtonForExistingResponse(question, response);
            
            InstructorResultsResponseRow responseRow = new InstructorResultsResponseRow(
                                   bundle.getGiverNameForResponse(question, response), bundle.getTeamNameForEmail(response.giverEmail), 
                                   bundle.getRecipientNameForResponse(question, response), bundle.getTeamNameForEmail(response.recipientEmail), 
                                   bundle.getResponseAnswerHtml(response, question), 
                                   bundle.isGiverVisible(response), moderationButton);
            
            configureResponseRowForViewType(question, viewType, response.giverEmail, response.recipientEmail, responseRow);
                        
            responseRows.add(responseRow);
        }

        if (isFirstGroupedByGiver) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                            question, possibleParticipantsWithoutResponses, participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier), 
                                            bundle.getTeamNameForEmail(participantIdentifier),
                                            viewType));
        } else {
            responseRows.addAll(buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                            question, possibleParticipantsWithoutResponses, participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier),
                                            bundle.getTeamNameForEmail(participantIdentifier), 
                                            viewType));
        }
        
        
        return responseRows;
    }
    

    private void configureResponseRowForViewType(FeedbackQuestionAttributes question,
                                                 ViewType statisticsViewType, 
                                                 String giver, String recipient,
                                                 InstructorResultsResponseRow responseRow) {
        
        switch (statisticsViewType) {
            case QUESTION:
                responseRow.setGiverProfilePictureDisplayed(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty());
                responseRow.setGiverProfilePictureLink(new Url(getProfilePictureLink(giver)));
                
                responseRow.setRecipientProfilePictureDisplayed(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty());
                responseRow.setRecipientProfilePictureLink(new Url(getProfilePictureLink(recipient)));
                responseRow.setActionsDisplayed(true);
                break;
            case GIVER_QUESTION_RECIPIENT:
                responseRow.setGiverDisplayed(false);
                responseRow.setGiverProfilePictureDisplayed(false);
                
                responseRow.setRecipientProfilePictureAColumn(true);
                responseRow.setRecipientProfilePictureDisplayed(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty());
                responseRow.setRecipientProfilePictureLink(new Url(getProfilePictureLink(recipient)));
                responseRow.setActionsDisplayed(false);
                break;
            case RECIPIENT_QUESTION_GIVER:
                responseRow.setRecipientDisplayed(false);
                responseRow.setRecipientProfilePictureDisplayed(false);
                
                responseRow.setGiverProfilePictureAColumn(true);
                responseRow.setGiverProfilePictureDisplayed(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty());
                responseRow.setGiverProfilePictureLink(new Url(getProfilePictureLink(giver)));
                responseRow.setActionsDisplayed(true);
                break;
            default:
                Assumption.fail();            
        }
    }
    
    /**
     * Construct missing response rows between the giver identified by {@code giverIdentifier} and 
     * {@code possibleReceivers}. The response rows are configured using 
     * {@code configureResponseRowForViewType(viewType)}. 
     *  
     * @see configureResponseRowForViewType
     */
    private List<InstructorResultsResponseRow> buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
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
                InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                         question, giverIdentifier, 
                                                                         "btn btn-default btn-xs", "Moderate Response");
                InstructorResultsResponseRow missingResponse = new InstructorResultsResponseRow(giverName, giverTeam, 
                                                                                                possibleRecipientName, possibleRecipientTeam, 
                                                                                                textToDisplay, true, moderationButton, true);
                
                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRowForViewType(question, viewType, giverIdentifier, possibleRecipient, missingResponse);
                missingResponses.add(missingResponse);
            }
        }
        
        return missingResponses;
    }
    
    /**
     * Construct missing response rows between the recipient identified by {@code recipientIdentifier} and 
     * {@code possibleGivers}. The response rows are configured using 
     * {@code configureResponseRowForViewType(viewType)}. 
     *  
     * @see configureResponseRowForViewType
     */
    private List<InstructorResultsResponseRow> buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                    FeedbackQuestionAttributes question, 
                                    List<String> possibleGivers, String recipientIdentifier,
                                    String recipientName, String recipientTeam, ViewType viewType) {
        List<InstructorResultsResponseRow> missingResponses = new ArrayList<InstructorResultsResponseRow>();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        
        for (String possibleGiver : possibleGivers) {
            String possibleGiverName = bundle.getFullNameFromRoster(possibleGiver);
            String possibleGiverTeam = bundle.getTeamNameFromRoster(possibleGiver);
            
            String textToDisplay = questionDetails.getNoResponseTextInHtml(recipientIdentifier, possibleGiver, bundle, question);
            
            if (questionDetails.shouldShowNoResponseText(possibleGiver, recipientIdentifier, question)) {
                InstructorResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                         question, possibleGiver, "btn btn-default btn-xs", 
                                                                         "Moderate Response");
                InstructorResultsResponseRow missingResponse = new InstructorResultsResponseRow(possibleGiverName, possibleGiverTeam, 
                                                                                                recipientName, recipientTeam, 
                                                                                                textToDisplay, true, moderationButton, true);
                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRowForViewType(question, viewType, possibleGiver, recipientIdentifier, missingResponse);
                
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
    
    private List<InstructorResultsResponseRow> getRemainingMissingResponseRows(
                                                FeedbackQuestionAttributes question,
                                                List<String> remainingPossibleGivers, List<String> possibleRecipientsForGiver, 
                                                String prevGiver, ViewType viewType) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        if (possibleRecipientsForGiver != null) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(question, possibleRecipientsForGiver,
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
            
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
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
                                                     getFeedbackSessionName(), 
                                                     Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isDisabled = !isAllowedToModerate;
        
        String giverIdentifier = giverEmail;
        if (question != null) {
            giverIdentifier = question.giverType.isTeam() ? giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"") 
                                                          : giverIdentifier;
        }
        
        InstructorResultsModerationButton moderationButton = new InstructorResultsModerationButton(isAllowedToModerate, isDisabled,
                                                                 className, giverIdentifier, 
                                                                 getCourseId(), getFeedbackSessionName(), 
                                                                 question, buttonText);
        return moderationButton;
   }
    
    private InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                                    String participantIdentifier, String participantName, 
                                    List<InstructorResultsQuestionTable> questionTables) {
        return buildInstructorFeedbackResultsGroupByQuestionPanel(
                                        participantIdentifier,  participantName, 
                                        questionTables, null, false);
    }
    
   private InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                             String participantIdentifier, String participantName, 
                                                             List<InstructorResultsQuestionTable> questionTables,
                                                             InstructorResultsModerationButton moderationButton, 
                                                             boolean isModerationButtonDisplayed) {
       boolean isEmailValid = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
       Url profilePictureLink = new Url(getProfilePictureLink(participantIdentifier));
       
       InstructorResultsQuestionTable.sortByQuestionNumber(questionTables);
       InstructorFeedbackResultsGroupByQuestionPanel giverPanel = 
                                    InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                    questionTables, isEmailValid, profilePictureLink, 
                                                                    viewType.isFirstGroupedByGiver(), participantIdentifier, participantName,
                                                                    moderationButton, isModerationButtonDisplayed);
       
       return giverPanel;
   }

    /* 
     * getInstructorFeedbackSessionPublishAndUnpublishAction()
     * is not covered in action test, but covered in UI tests.
     */

    private FeedbackSessionPublishButton getInstructorFeedbackSessionPublishAndUnpublishAction() {
        boolean isHome = false;
        return new FeedbackSessionPublishButton(this,
                                                bundle.feedbackSession,
                                                isHome,
                                                instructor,
                                                "btn-primary btn-block");
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

    @Deprecated
    public String getGroupByTeam() {
        return groupByTeam != null? groupByTeam : "null";
    }
    
    // TODO: swap groupByTeam to a normal boolean
    public boolean isGroupedByTeam() {
        return "on".equals(groupByTeam); 
    }

    // TODO: swap showStats to a normal boolean
    private boolean isStatsShown() {
        return showStats != null;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getCourseId() {
        return sanitizeForHtml(bundle.feedbackSession.courseId);
    }

    public String getFeedbackSessionName() {
        return sanitizeForHtml(bundle.feedbackSession.feedbackSessionName);
    }

    public String getAjaxStatus() {
        return ajaxStatus;
    }

    public String getSessionResultsHtmlTableAsString() {
        return sessionResultsHtmlTableAsString;
    }
    
    public boolean isShouldCollapsed() {
        return isPanelsCollapsed;
    }

    public void setShouldCollapsed(boolean shouldCollapsed) {
        this.isPanelsCollapsed = shouldCollapsed;
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

    private String getInstructorFeedbackSessionEditLink() {
        return instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)
               ? getInstructorFeedbackSessionEditLink(bundle.feedbackSession.courseId, 
                                                      bundle.feedbackSession.feedbackSessionName)
               : null;
    }
    
    private String getInstructorFeedbackSessionResultsLink() {
        return getInstructorFeedbackSessionResultsLink(bundle.feedbackSession.courseId, bundle.feedbackSession.feedbackSessionName);
    }
    
    public boolean isAllSectionsSelected() {
        return "All".equals(selectedSection);
    }
    
    // TODO: place below getter methods for template objects in some init method common to all views
    public InstructorFeedbackResultsSessionPanel getSessionPanel() {
        return new InstructorFeedbackResultsSessionPanel(
                bundle.feedbackSession, getInstructorFeedbackSessionEditLink(),
                getInstructorFeedbackSessionPublishAndUnpublishAction(), selectedSection);
    }
    
    public InstructorFeedbackResultsFilterPanel getFilterPanel() {
        return new InstructorFeedbackResultsFilterPanel(
                isStatsShown(), isPanelsCollapsed, bundle.feedbackSession, isAllSectionsSelected(), selectedSection,
                isGroupedByTeam(), sortType, getInstructorFeedbackSessionResultsLink(), sections);
    }
}
