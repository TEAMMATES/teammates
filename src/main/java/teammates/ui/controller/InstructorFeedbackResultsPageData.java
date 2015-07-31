package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.InstructorFeedbackResultsFilterPanel;
import teammates.ui.template.InstructorFeedbackResultsNoResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody;
import teammates.ui.template.InstructorFeedbackResultsSessionPanel;
import teammates.ui.template.InstructorFeedbackResultsParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel;
import teammates.ui.template.InstructorFeedbackResultsSectionPanel;
import teammates.ui.template.FeedbackSessionPublishButton;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackResultsQuestionTable;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;
import teammates.ui.template.InstructorFeedbackResultsModerationButton;


public class InstructorFeedbackResultsPageData extends PageData {
    private static final String DISPLAY_NAME_FOR_DEFAULT_SECTION = "Not in a section";

    // TODO find out why it's 500
    private static final int RESPONSE_LIMIT_FOR_COLLAPSING_PANEL = 500;

    private FeedbackSessionResultsBundle bundle = null;
    private InstructorAttributes instructor = null;
    private List<String> sections = null;
    private String selectedSection = null;
    private String sortType = null;
    private String groupByTeam = null;
    private String showStats = null;
    private int startIndex;
    private boolean isPanelsCollapsed;
    
    private FieldValidator validator = new FieldValidator();
    private String feedbackSessionName = null;
    
    // used for html table ajax loading
    private String ajaxStatus = null;
    private String sessionResultsHtmlTableAsString = null;
    

    // for question view
    private List<InstructorFeedbackResultsQuestionTable> questionPanels;
    // for giver > question > recipient, recipient > question > giver,
    // giver > recipient > question, recipient > giver > question
    private LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels;
    
    // TODO multiple page data classes inheriting this for each view type, 
    // rather than an enum determining behavior in many methods
    private ViewType viewType;
    enum ViewType {
        QUESTION, GIVER_QUESTION_RECIPIENT, RECIPIENT_QUESTION_GIVER, RECIPIENT_GIVER_QUESTION, GIVER_RECIPIENT_QUESTION;
        
        public String toString() {
            // replace _ to - to keep it consistent with old behavior
            return name().toLowerCase().replaceAll("_", "-");
        }
        
        public boolean isPrimaryGroupingOfGiverType() {
            return this == GIVER_QUESTION_RECIPIENT || this == GIVER_RECIPIENT_QUESTION ;
        }
        
        public boolean isSecondaryGroupingOfParticipantType() {
            return this == RECIPIENT_GIVER_QUESTION || this == GIVER_RECIPIENT_QUESTION ;
        }
        
        public String additionalInfoId() {
            switch (this) {
                case GIVER_QUESTION_RECIPIENT:
                    return "giver-%s-question-%s";
                case GIVER_RECIPIENT_QUESTION:
                    return "giver-%s-recipient-%s";
                case RECIPIENT_GIVER_QUESTION:
                    return "giver-%s-recipient-%s";
                case RECIPIENT_QUESTION_GIVER:
                    return "recipient-%s-question-%s";
                default:
                    return "";
            }
        }
    }
    
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
        setStartIndex(-1);
    }
    
    /**
     * Prepares question tables for viewing
     *  
     * {@code bundle} should be set before this method
     */
    public void initForViewByQuestion(InstructorAttributes instructor, 
                                      String selectedSection, String showStats, 
                                      String groupByTeam) {
        Assumption.assertNotNull(bundle);
        this.viewType = ViewType.QUESTION;
        this.sortType = ViewType.QUESTION.toString();
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorFeedbackResultsQuestionTable>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : questionToResponseMap.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();
            
            questionPanels.add(buildQuestionTableAndResponseRows(question, responses, ""));
        }
        
    }
    
  
    /**
     * Creates {@code InstructorFeedbackResultsSectionPanel}s for sectionPanels.
     * 
     * Iterates through the responses and creates panels and questions for them. Keeps track 
     * of missing sections, teams and participants who do not have responses 
     * and create panels for these missing sections, teams and participants.
     * 
     * {@code bundle} should be set before this method
     * TODO: simplify the logic in this method
     */
    public void initForSectionPanelViews(InstructorAttributes instructor, 
                                    String selectedSection, String showStats, 
                                    String groupByTeam, ViewType view) {
        Assumption.assertNotNull(bundle);
        this.viewType = view;
        this.sortType = view.toString();
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            if (isAllSectionsSelected()) {
                buildSectionPanelsForForAjaxLoading(getSections());
            } else {
                buildSectionPanelWithErrorMessage();
            }
            
            return;
        }
        
        // Note that if the page needs to load by ajax, then responses may be empty too,
        // therefore the check for ajax to come before this
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > RESPONSE_LIMIT_FOR_COLLAPSING_PANEL);
        
        switch (viewType) {
            case RECIPIENT_GIVER_QUESTION:
                Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponsesForRGQ 
                    = bundle.getResponsesSortedByRecipientGiverQuestion();
    
                buildSectionPanelsForViewByParticipantParticipantQuestion(sortedResponsesForRGQ, viewType.additionalInfoId());
                break;
            case RECIPIENT_QUESTION_GIVER:
                Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponsesForRQG
                    = bundle.getResponsesSortedByRecipientQuestionGiver(true);
  
                buildSectionPanelsForViewByParticipantQuestionParticipant(sortedResponsesForRQG, viewType.additionalInfoId());
                break;
            case GIVER_QUESTION_RECIPIENT:
                Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponsesForGQR 
                    = bundle.getResponsesSortedByGiverQuestionRecipient(true);
   
                buildSectionPanelsForViewByParticipantQuestionParticipant(sortedResponsesForGQR, viewType.additionalInfoId());
                break;
            case GIVER_RECIPIENT_QUESTION:
                Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponsesForGRQ
                    = bundle.getResponsesSortedByGiverRecipientQuestion();
                buildSectionPanelsForViewByParticipantParticipantQuestion(sortedResponsesForGRQ, viewType.additionalInfoId());
                break;
            default:
                Assumption.fail();
        }
        
    }
    
    private void buildSectionPanelsForViewByParticipantParticipantQuestion(
                         Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponses,
                         String additionalInfoId) {
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        // Maintain previous section and previous team while iterating through the recipients
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        Set<String> sectionsWithResponses = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();      
          
        // Iterate through the primary participant
        int primaryParticipantIndex = this.getStartIndex();
        for (Entry<String, Map<String, List<FeedbackResponseAttributes>>> primaryToSecondaryParticipantToResponsesMap : 
                                                                              sortedResponses.entrySet()) {
            primaryParticipantIndex += 1;
            String primaryParticipantIdentifier = primaryToSecondaryParticipantToResponsesMap.getKey();
            
            String currentTeam = getCurrentTeam(bundle, primaryParticipantIdentifier);
            String currentSection = getCurrentSection(primaryToSecondaryParticipantToResponsesMap);
            
            boolean isDifferentTeam = !prevTeam.equals(currentTeam);
            boolean isDifferentSection = !prevSection.equals(currentSection);

            if (isDifferentTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    // construct missing participant panels for the previous team
                    buildMissingParticipantPanelsForTeam(
                        sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear(); 
                }
                
                teamsWithResponses.add(currentTeam);
                if (!isDifferentSection) { // add team to sectionPanel only if it's the correct section
                    sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
                }
            }
            
            if (isDifferentSection) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    finalizeBuildingSectionPanelWithoutStats(sectionPanel, prevSection);
                    buildMissingTeamAndParticipantPanelsForSection(
                            sectionPanel, prevSection, teamsWithResponses);
                    
                    // add to sectionPanels,
                    sectionPanels.put(prevSection, sectionPanel);
                    sectionsWithResponses.add(prevSection);
                    
                    // setup for next section
                    teamsWithResponses.clear();
                    teamsWithResponses.add(currentTeam);
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }
                
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }
            
            // Build participant panel for the current primary participant
            InstructorFeedbackResultsParticipantPanel recipientPanel
                    = buildGroupByParticipantPanel(primaryParticipantIdentifier, primaryToSecondaryParticipantToResponsesMap, 
                                                   additionalInfoId, primaryParticipantIndex);
            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, recipientPanel);
            
            teamMembersWithResponses.add(primaryParticipantIdentifier);

            prevTeam = currentTeam;
            prevSection = currentSection;
        }
        
        // for the last section with responses
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);
        
        teamsWithResponses.add(prevTeam);
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, prevSection, teamsWithResponses);
        
        finalizeBuildingSectionPanelWithoutStats(sectionPanel, prevSection);
        sectionPanels.put(prevSection, sectionPanel);   
        if (isAllSectionsSelected()) {
            sectionsWithResponses.add(prevSection); // for the last section having responses 
            buildSectionPanelsForMissingSections(sectionsWithResponses);
        }
    }
    
    /**
     * Constructs section panels for the {@code sortedResponses}.
     * 
     * Also builds team statistics tables for every team
     * @param sortedResponses
     * @param additionalInfoId
     */
    private void buildSectionPanelsForViewByParticipantQuestionParticipant(
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses,
                                    String additionalInfoId) {
       sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
       InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
       
       LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
           = viewType.isPrimaryGroupingOfGiverType() ? bundle.getQuestionResponseMapByGiverTeam()
                                                     : bundle.getQuestionResponseMapByRecipientTeam();
       
       // Maintain previous section and previous team while iterating through the recipients
       // initialize the previous section to "None"
       String prevSection = Const.DEFAULT_SECTION;
       String prevTeam = "";
       
       Set<String> sectionsWithResponses = new HashSet<String>();
       Set<String> teamsWithResponses = new LinkedHashSet<String>();
       Set<String> teamMembersWithResponses = new HashSet<String>();      
         
       // Iterate through the primary participant
       int primaryParticipantIndex = this.getStartIndex();
       for (Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> primaryToSecondaryParticipantToResponsesMap : 
                                                                             sortedResponses.entrySet()) {
           primaryParticipantIndex += 1;
           String primaryParticipantIdentifier = primaryToSecondaryParticipantToResponsesMap.getKey();
           
           String currentTeam = getCurrentTeam(bundle, primaryParticipantIdentifier);
           String currentSection = getCurrentSection(primaryToSecondaryParticipantToResponsesMap);
           
           boolean isDifferentTeam = !prevTeam.equals(currentTeam);
           boolean isDifferentSection = !prevSection.equals(currentSection);

           if (isDifferentTeam) {
               boolean isFirstTeam = prevTeam.isEmpty();
               if (!isFirstTeam) {
                   // construct missing participant panels for the previous team
                   buildMissingParticipantPanelsForTeam(
                       sectionPanel, prevTeam, teamMembersWithResponses);
                   teamMembersWithResponses.clear(); 
               }
               
               teamsWithResponses.add(currentTeam);
               if (!isDifferentSection) { // add team to sectionPanel only if it's the correct section
                   sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
               }
           }
           
           if (isDifferentSection) {
               boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
               if (!isFirstSection) {
                   // Finalize building of section panel,
                   finalizeBuildingSectionPanel(sectionPanel, prevSection, responsesGroupedByTeam, teamsWithResponses);
                   buildMissingTeamAndParticipantPanelsForSection(
                           sectionPanel, prevSection, teamsWithResponses);
                   
                   // add to sectionPanels,
                   sectionPanels.put(prevSection, sectionPanel);
                   sectionsWithResponses.add(prevSection);
                   
                   // setup for next section
                   teamsWithResponses.clear();
                   teamsWithResponses.add(currentTeam);
                   
                   sectionPanel = new InstructorFeedbackResultsSectionPanel();
               }
               
               sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
           }
           
           // Build participant panel for the current participant 
           InstructorFeedbackResultsParticipantPanel primaryParticipantPanel 
                   = buildGroupByQuestionPanel(primaryParticipantIdentifier, 
                                               primaryToSecondaryParticipantToResponsesMap,
                                               additionalInfoId, primaryParticipantIndex);
           
           addParticipantPanelToSectionPanel(sectionPanel, currentTeam, primaryParticipantPanel);
           
           teamMembersWithResponses.add(primaryParticipantIdentifier);

           prevTeam = currentTeam;
           prevSection = currentSection;
       }
       
       // for the last section with responses
       buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);
       
       teamsWithResponses.add(prevTeam);
       buildMissingTeamAndParticipantPanelsForSection(sectionPanel, prevSection, teamsWithResponses);
       
       finalizeBuildingSectionPanel(sectionPanel, prevSection, responsesGroupedByTeam, teamsWithResponses);
       sectionPanels.put(prevSection, sectionPanel);   
       if (isAllSectionsSelected()) {
           sectionsWithResponses.add(prevSection); // for the last section having responses 
           buildSectionPanelsForMissingSections(sectionsWithResponses);
       }
   }

    private InstructorFeedbackResultsGroupByParticipantPanel buildGroupByParticipantPanel(
                                    String primaryParticipantIdentifier, 
                                    Entry<String, Map<String, List<FeedbackResponseAttributes>>> recipientToGiverToResponsesMap,
                                    String additionalInfoId, int primaryParticipantIndex) {
        // first build secondary participant panels for the primary participant panel
        Map<String, List<FeedbackResponseAttributes>> giverToResponsesMap 
            = recipientToGiverToResponsesMap.getValue();
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels 
                             = buildSecondaryParticipantPanels(
                                        additionalInfoId, primaryParticipantIndex, giverToResponsesMap);
        
        // construct the primary participant panel
        String primaryParticipantNameWithTeamNameAppended 
            = bundle.appendTeamNameToName(bundle.getNameForEmail(primaryParticipantIdentifier), 
                                          bundle.getTeamNameForEmail(primaryParticipantIdentifier));
        
        
        boolean isTeam = bundle.rosterTeamNameMembersTable.containsKey(primaryParticipantIdentifier)
                      || primaryParticipantIdentifier.matches(Const.REGEXP_TEAM);
        String normalisedIdentifier = primaryParticipantIdentifier.matches(Const.REGEXP_TEAM) 
                                    ? primaryParticipantIdentifier.replace(Const.TEAM_OF_EMAIL_OWNER, "")
                                    : primaryParticipantIdentifier;
        
        boolean isStudent = bundle.isParticipantIdentifierStudent(normalisedIdentifier);
        
        String sectionName = bundle.getSectionFromRoster(primaryParticipantIdentifier);
        
        boolean isAllowedToModerate = (isStudent || isTeam) 
                && instructor.isAllowedForPrivilege(sectionName, feedbackSessionName, 
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        // Use the normalisedIdentifier instead of the original identifier to handle
        // Team responses where the primary giver identifier is "<email>'s Team"
        InstructorFeedbackResultsModerationButton moderationButton 
                = viewType.isPrimaryGroupingOfGiverType() && isAllowedToModerate 
                ? buildModerationButtonForGiver(null, normalisedIdentifier, "btn btn-primary btn-xs", "Moderate Responses")
                : null;
        InstructorFeedbackResultsGroupByParticipantPanel primaryParticipantPanel 
                = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                        primaryParticipantIdentifier, primaryParticipantNameWithTeamNameAppended, 
                                        secondaryParticipantPanels, moderationButton);
        
        return primaryParticipantPanel;
    }

    private List<InstructorFeedbackResultsSecondaryParticipantPanelBody> buildSecondaryParticipantPanels(
                                    String additionalInfoId, int primaryParticipantIndex,
                                    Map<String, List<FeedbackResponseAttributes>> secondaryParticipantToResponsesMap) {
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels = new ArrayList<>();
        
        int secondaryParticipantIndex = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> secondaryParticipantResponses 
                                                               : secondaryParticipantToResponsesMap.entrySet()) {
            secondaryParticipantIndex += 1;
            String secondaryParticipantIdentifier = secondaryParticipantResponses.getKey();
            String secondaryParticipantDisplayableName = bundle.getNameForEmail(secondaryParticipantIdentifier); 
            
            boolean isEmail = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, secondaryParticipantIdentifier).isEmpty();
            if (isEmail && !bundle.getTeamNameForEmail(secondaryParticipantIdentifier).isEmpty()) {
                secondaryParticipantDisplayableName += " (" + bundle.getTeamNameForEmail(secondaryParticipantIdentifier)
                                                     + ")";
            }
            List<InstructorFeedbackResultsResponsePanel> responsePanels 
                                     = buildResponsePanels(
                                            additionalInfoId, primaryParticipantIndex, 
                                            secondaryParticipantIndex, secondaryParticipantResponses);
          
            InstructorFeedbackResultsSecondaryParticipantPanelBody secondaryParticipantPanel 
                     = new InstructorFeedbackResultsSecondaryParticipantPanelBody(
                                        secondaryParticipantIdentifier, secondaryParticipantDisplayableName, 
                                        responsePanels, isEmail);
            
            // TODO this check can be improved to use isStudent
            secondaryParticipantPanel.setProfilePictureLink(isEmail
                                                          ? getProfilePictureLink(secondaryParticipantIdentifier)
                                                          : null);
            
            boolean isStudent = bundle.roster.getStudentForEmail(secondaryParticipantIdentifier) != null;
            boolean isVisibleTeam = bundle.rosterTeamNameMembersTable.containsKey(secondaryParticipantDisplayableName);
            boolean isShowingModerationButton = !viewType.isPrimaryGroupingOfGiverType() && (isStudent || isVisibleTeam);
            secondaryParticipantPanel.setModerationButton(isShowingModerationButton
                                                        ? buildModerationButtonForGiver(null, secondaryParticipantIdentifier, 
                                                                                        "btn btn-default btn-xs", 
                                                                                        "Moderate Responses")
                                                        : null);
            
            secondaryParticipantPanels.add(secondaryParticipantPanel);
        }
        
        return secondaryParticipantPanels;
    }

    private List<InstructorFeedbackResultsResponsePanel> buildResponsePanels(final String additionalInfoId,
                                    int primaryParticipantIndex, int secondaryRecipientIndex,
                                    Map.Entry<String, List<FeedbackResponseAttributes>> giverResponses) {
        List<InstructorFeedbackResultsResponsePanel> responsePanels = new ArrayList<>();
        
        List<FeedbackResponseAttributes> responses = giverResponses.getValue();
        for (int i = 0; i < responses.size(); i++) {
            FeedbackResponseAttributes response = responses.get(i);
            
            String questionId = response.feedbackQuestionId;
            FeedbackQuestionAttributes question = bundle.questions.get(questionId);
            String questionText = bundle.getQuestionText(questionId);
            
            int giverIndex     = viewType.isPrimaryGroupingOfGiverType() ? primaryParticipantIndex 
                                                                         : secondaryRecipientIndex;
            int recipientIndex = viewType.isPrimaryGroupingOfGiverType() ? secondaryRecipientIndex 
                                                                         : primaryParticipantIndex;
            
            String additionalInfoText 
                = question.getQuestionDetails().getQuestionAdditionalInfoHtml(
                                                            question.getQuestionNumber(), 
                                                            String.format(additionalInfoId, 
                                                                          giverIndex, recipientIndex));
            ElementTag rowAttributes = null;
            String displayableResponse = bundle.getResponseAnswerHtml(response, question);
            
            
            String giverName = bundle.getNameForEmail(response.giverEmail);
            String recipientName = bundle.getNameForEmail(response.recipientEmail);
            
            String giverTeam = bundle.getTeamNameForEmail(response.giverEmail);
            String recipientTeam = bundle.getTeamNameForEmail(response.recipientEmail);
            
            giverName = bundle.appendTeamNameToName(giverName, giverTeam);
            recipientName = bundle.appendTeamNameToName(recipientName, recipientTeam);
            
            List<FeedbackResponseComment> comments = buildResponseComments(giverName, recipientName, question, response);
            boolean isAllowedToSubmitSessionsInBothSection 
                = instructor.isAllowedForPrivilege(response.giverSection,
                          response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                  && instructor.isAllowedForPrivilege(response.recipientSection,
                          response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            
            InstructorFeedbackResultsResponsePanel responsePanel 
                = new InstructorFeedbackResultsResponsePanel(question, response, questionText, additionalInfoText, 
                                                             rowAttributes, displayableResponse, comments,
                                                             isAllowedToSubmitSessionsInBothSection);
            
            responsePanel.setFeedbackResponseCommentsIndexes(recipientIndex, giverIndex, i + 1);
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap 
                    = getResponseVisibilityMap(question);
            FeedbackResponseComment frcForAdding = setUpFeedbackResponseCommentAdd(question, response, 
                                            responseVisibilityMap, giverName, recipientName);
            
            responsePanel.setFrcForAdding(frcForAdding);
            
            responsePanels.add(responsePanel);
        }
        
        return responsePanels;
    }

    private InstructorFeedbackResultsGroupByQuestionPanel buildGroupByQuestionPanel(
            String participantIdentifier,
            Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> recipientToGiverToResponsesMap,
            String additionalInfoId, int participantIndex) {
        List<InstructorFeedbackResultsQuestionTable> questionTables = new ArrayList<InstructorFeedbackResultsQuestionTable>();
        
        int questionIndex = 0;
        for (Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForParticipantForQuestion : 
                                                                                 recipientToGiverToResponsesMap.getValue().entrySet()) {
            if (responsesForParticipantForQuestion.getValue().isEmpty()) {
                // participant has no responses for the current question
                continue;
            }
            
            questionIndex += 1;
            
            FeedbackQuestionAttributes currentQuestion = responsesForParticipantForQuestion.getKey();
            List<FeedbackResponseAttributes> responsesForQuestion = responsesForParticipantForQuestion.getValue();

            InstructorFeedbackResultsQuestionTable questionTable 
                = buildQuestionTableAndResponseRows(currentQuestion, responsesForQuestion,
                                                    String.format(additionalInfoId, participantIndex, questionIndex), 
                                                    participantIdentifier, true);
            questionTable.setBoldQuestionNumber(false);
            questionTables.add(questionTable);
      
        }
        
        InstructorFeedbackResultsQuestionTable.sortByQuestionNumber(questionTables);
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel;
        // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
        if (viewType.isPrimaryGroupingOfGiverType() && bundle.isParticipantIdentifierStudent(participantIdentifier)) {
            // Moderation button on the participant panels are only shown is the panel is a giver panel,
            // and if the participant is a student
            InstructorFeedbackResultsModerationButton moderationButton 
                                                   = buildModerationButtonForGiver(
                                                         null, participantIdentifier, "btn btn-primary btn-xs", 
                                                         "Moderate Responses");
            participantPanel = InstructorFeedbackResultsGroupByQuestionPanel
                                   .buildInstructorFeedbackResultsGroupByQuestionPanelWithModerationButton(
                                            participantIdentifier, bundle.getNameForEmail(participantIdentifier),
                                            questionTables, getProfilePictureLink(participantIdentifier), 
                                            true, moderationButton);
        } else {
            participantPanel = InstructorFeedbackResultsGroupByQuestionPanel
                                   .buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                                            questionTables, getProfilePictureLink(participantIdentifier), 
                                            viewType.isPrimaryGroupingOfGiverType(), participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier));
        }
        
        return participantPanel;
    }
    
    private void finalizeBuildingSectionPanelWithoutStats(InstructorFeedbackResultsSectionPanel sectionPanel,
                                                          String sectionName) {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> emptyResponseMap = new LinkedHashMap<>();
        LinkedHashSet<String> emptyTeamList = new LinkedHashSet<String>();
        finalizeBuildingSectionPanel(sectionPanel, sectionName, emptyResponseMap, emptyTeamList);
    }

    private void finalizeBuildingSectionPanel(
                 InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
                 LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                 Set<String> teamsWithResponses) {
        switch (viewType) {
            case GIVER_QUESTION_RECIPIENT:
            case RECIPIENT_QUESTION_GIVER:
                prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
                if (!responsesGroupedByTeam.isEmpty()) {
                    buildTeamsStatisticsTableForSectionPanel(sectionPanel, responsesGroupedByTeam, 
                                                             teamsWithResponses);
                }
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) 
                                                    ? DISPLAY_NAME_FOR_DEFAULT_SECTION 
                                                    : sectionName);
                sectionPanel.setDisplayingTeamStatistics(true);
                break;
            case RECIPIENT_GIVER_QUESTION:
            case GIVER_RECIPIENT_QUESTION:
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) 
                                                    ? DISPLAY_NAME_FOR_DEFAULT_SECTION
                                                    : sectionName);
                sectionPanel.setDisplayingTeamStatistics(false);
                break;
            default:
                Assumption.fail();
                break;
        }
    }
    
    private void buildMissingTeamAndParticipantPanelsForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
                                    Set<String> teamWithResponses) {
        boolean isWithModerationButton = viewType.isPrimaryGroupingOfGiverType();

        // update the teams for the previous section
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionName);
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(teamWithResponses);
        
        // create for every remaining team in the section, participantResultsPanels for every team member
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembersOfTeam = new ArrayList<String>(
                                                 bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            if (isWithModerationButton) {
                addMissingParticipantsForTeamToSectionPanelWithModerationButton(
                                                sectionPanel, teamWithoutResponses, teamMembersOfTeam);
            } else {
                addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(
                                                sectionPanel, teamWithoutResponses, teamMembersOfTeam);
            }
        }
        
    }

    private static String getCurrentTeam(FeedbackSessionResultsBundle bundle, String giverIdentifier) {
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
     * Uses the first response to get the current section
     * @param responses
     */
    private <K> String getCurrentSection(
                        Map.Entry<String, Map<K, List<FeedbackResponseAttributes>>> responses) {
        String currentSection = Const.DEFAULT_SECTION;
        // update current section
        // retrieve section from the first response of this user
        // TODO simplify by introducing more data structures into bundle
        for (Map.Entry<K, List<FeedbackResponseAttributes>> responsesFromGiverForQuestion : 
                                                            responses.getValue().entrySet()) {
            if (responsesFromGiverForQuestion.getValue().isEmpty()) {
                continue;
            }
            FeedbackResponseAttributes firstResponse = responsesFromGiverForQuestion.getValue().get(0);
            currentSection = viewType.isPrimaryGroupingOfGiverType() ? firstResponse.giverSection 
                                                              : firstResponse.recipientSection;
            break;
        }
        
        return currentSection;
    }
    
    private void buildMissingParticipantPanelsForTeam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String teamName,
                                    Set<String> teamMembersWithResponses) {
        boolean isDisplayingModerationButton = viewType.isPrimaryGroupingOfGiverType();
        
        Set<String> teamMembersEmail = new HashSet<String>();
        teamMembersEmail.addAll(bundle.getTeamMembersFromRoster(teamName));
        
        Set<String> teamMembersWithoutResponses = new HashSet<String>(teamMembersEmail);
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
        
        // Create missing participants panels for the previous team
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
        Collections.sort(sortedTeamMembersWithoutResponses);
        
        if (isDisplayingModerationButton) {
            addMissingParticipantsForTeamToSectionPanelWithModerationButton(sectionPanel, 
                                                        teamName, sortedTeamMembersWithoutResponses);
        } else {
            addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(sectionPanel, 
                                                        teamName, sortedTeamMembersWithoutResponses);
        }
        
    }

    private void buildSectionPanelsForMissingSections(Set<String> receivingSections) {
        Set<String> sectionsInCourse = bundle.rosterSectionTeamNameTable.keySet();
        Set<String> sectionsWithNoResponseReceived = new HashSet<String>(sectionsInCourse);
        sectionsWithNoResponseReceived.removeAll(receivingSections);
        
        List<String> sectionsWithoutResponsesList = new ArrayList<String>(sectionsWithNoResponseReceived);
        Collections.sort(sectionsWithoutResponsesList);
        
        InstructorFeedbackResultsSectionPanel sectionPanel;
        for (String sectionWithoutResponses: sectionsWithoutResponsesList) {
            sectionPanel = new InstructorFeedbackResultsSectionPanel();
            finalizeBuildingSectionPanelWithoutStats(sectionPanel, sectionWithoutResponses);
            sectionPanels.put(sectionWithoutResponses, sectionPanel);
            
            Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionWithoutResponses);
            List<String> teamsInSectionAsList = new ArrayList<String>(teamsInSection);
            
            Collections.sort(teamsInSectionAsList);
            
            for (String teamInMissingSection : teamsInSectionAsList) {
                List<String> teamMembers = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamInMissingSection));
                Collections.sort(teamMembers);
                
                if (viewType.isPrimaryGroupingOfGiverType()) {
                    addMissingParticipantsForTeamToSectionPanelWithModerationButton(
                                                    sectionPanel, teamInMissingSection, teamMembers);
                } else {
                    addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(
                                                    sectionPanel, teamInMissingSection, teamMembers);
                }
            }
        }
    }

    private void buildSectionPanelsForForAjaxLoading(List<String> sections) {
        setShouldCollapsed(true);
        
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
        sectionPanel.setSectionNameForDisplay(DISPLAY_NAME_FOR_DEFAULT_SECTION);
        sectionPanel.setLoadSectionResponsesByAjax(true);
        
        sectionPanels.put(Const.DEFAULT_SECTION, sectionPanel);  
    }
    
    private void buildSectionPanelWithErrorMessage() {
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        sectionPanel.setSectionName(selectedSection);
        sectionPanel.setSectionNameForDisplay(selectedSection);
        sectionPanel.setAbleToLoadResponses(false);
        
        sectionPanels.put(selectedSection, sectionPanel);
        
    }

    /**
     * Builds participant panels for the the specified team, and add to sectionPanel
     * @param sectionPanel
     * @param teamName
     * @param teamMembers
     */
    private void addMissingParticipantsForTeamToSectionPanelWithModerationButton(
                                                             InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorFeedbackResultsModerationButton moderationButton 
                                                   = buildModerationButtonForGiver(null, teamMember, "btn btn-default btn-xs",
                                                                                   "Moderate Responses");
            InstructorFeedbackResultsParticipantPanel giverPanel;
            
            if (!viewType.isSecondaryGroupingOfParticipantType()) {
                giverPanel 
                    = InstructorFeedbackResultsGroupByQuestionPanel
                        .buildInstructorFeedbackResultsGroupByQuestionPanelWithModerationButton(
                                                    teamMember, bundle.getFullNameFromRoster(teamMember),
                                                    new ArrayList<InstructorFeedbackResultsQuestionTable>(), 
                                                    getProfilePictureLink(teamMember), 
                                                    viewType.isPrimaryGroupingOfGiverType(), moderationButton);
            } else {
                String teamMemberNameWithTeamNameAppended = bundle.getFullNameFromRoster(teamMember) 
                                                   + " (" + bundle.getTeamNameFromRoster(teamMember) + ")";
                giverPanel 
                    = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                                teamMember, teamMemberNameWithTeamNameAppended, 
                                                new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>(), 
                                                moderationButton);
                                                                       
            }

            giverPanel.setHasResponses(false);
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }
    
    private void addMissingParticipantsForTeamToSectionPanelWithoutModerationButton(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, 
                                    String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            
            InstructorFeedbackResultsParticipantPanel giverPanel;
            
            if (!viewType.isSecondaryGroupingOfParticipantType()) {
                giverPanel = 
                InstructorFeedbackResultsGroupByQuestionPanel
                    .buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                        new ArrayList<InstructorFeedbackResultsQuestionTable>(), getProfilePictureLink(teamMember), 
                        viewType.isPrimaryGroupingOfGiverType(), teamMember, bundle.getFullNameFromRoster(teamMember));
                
            } else {
                String teamMemberWithTeamNameAppended = bundle.getFullNameFromRoster(teamMember) 
                                               + " (" + bundle.getTeamNameFromRoster(teamMember) + ")";
                giverPanel = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                               teamMember, teamMemberWithTeamNameAppended,
                                               new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>(), 
                                               null);
            }
            giverPanel.setHasResponses(false);
            
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }
    

    private void addParticipantPanelToSectionPanel(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String currentTeam,
                                    InstructorFeedbackResultsParticipantPanel giverPanel) {
        List<InstructorFeedbackResultsParticipantPanel> teamsMembersPanels;
        
        if (sectionPanel.getParticipantPanels().containsKey(currentTeam)) {
            teamsMembersPanels = sectionPanel.getParticipantPanels().get(currentTeam);
        } else {
            teamsMembersPanels = new ArrayList<InstructorFeedbackResultsParticipantPanel>();
        }
        
        teamsMembersPanels.add(giverPanel);
        sectionPanel.getParticipantPanels().put(currentTeam, teamsMembersPanels);
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
                     Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                     Set<String> teamsInSection) {
        Map<String, List<InstructorFeedbackResultsQuestionTable>> teamToStatisticsTables = new HashMap<String, List<InstructorFeedbackResultsQuestionTable>>();
        for (String team : teamsInSection) {
            if (!responsesGroupedByTeam.containsKey(team)) {
                continue;
            }
            
            List<InstructorFeedbackResultsQuestionTable> statisticsTablesForTeam 
                = new ArrayList<InstructorFeedbackResultsQuestionTable>();
            
            for (FeedbackQuestionAttributes question : bundle.questions.values()) {
                if (!responsesGroupedByTeam.get(team).containsKey(question)) {
                    continue;
                }
                
                List<FeedbackResponseAttributes> responsesGivenTeamAndQuestion 
                    = responsesGroupedByTeam.get(team).get(question);
        
                InstructorFeedbackResultsQuestionTable statsTable = buildQuestionTableWithoutResponseRows(
                                                                               question, responsesGivenTeamAndQuestion,
                                                                               "");
                statsTable.setCollapsible(false);
                
                if (!statsTable.getQuestionStatisticsTable().isEmpty()) {
                    statisticsTablesForTeam.add(statsTable);
                }
            }
        
            InstructorFeedbackResultsQuestionTable.sortByQuestionNumber(statisticsTablesForTeam);
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
                Assumption.fail("There should be no headers for the view type");
        }
    }

    
    private InstructorFeedbackResultsQuestionTable buildQuestionTableAndResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String additionalInfoId) {
        return buildQuestionTableAndResponseRows(question, responses, additionalInfoId, 
                                                 null, true);
    }
    
    /**
     * Builds question tables without response rows, but with stats
     * @param question
     * @param responses  responses to compute statistics for
     * @param additionalInfoId
     */
    private InstructorFeedbackResultsQuestionTable buildQuestionTableWithoutResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String additionalInfoId) {
        return buildQuestionTableAndResponseRows(question, responses, additionalInfoId, 
                                                 null, false);   
    }
                                    
    /**
     * Builds a question table for given question, and response rows for the given responses.
     *  
     * @param question
     * @param responses
     * @param additionalInfoId
     * @param participantIdentifier  for viewTypes * > Question > *, constructs missing response rows
     *                               only for the given participant
     * @param isShowingResponseRows  if false, hides the response rows 
     */
    private InstructorFeedbackResultsQuestionTable buildQuestionTableAndResponseRows(
                                                              FeedbackQuestionAttributes question,
                                                              List<FeedbackResponseAttributes> responses,
                                                              String additionalInfoId, 
                                                              String participantIdentifier, boolean isShowingResponseRows) {
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        String statisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, this, 
                                                                                 bundle, viewType.toString());

        List<ElementTag> columnTags = new ArrayList<ElementTag>();
        Map<String, Boolean> isSortable = new HashMap<String, Boolean>();
        boolean isCollapsible = true;
        List<InstructorFeedbackResultsResponseRow> responseRows = null;
        
        if (isShowingResponseRows) {
            switch (viewType) {
                case QUESTION:
                    buildTableColumnHeaderForQuestionView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestion(question, responses);
                    break;
                case GIVER_QUESTION_RECIPIENT:
                    buildTableColumnHeaderForGiverQuestionRecipientView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestionForSingleGiver(question, responses, 
                                                                              participantIdentifier);
                    isCollapsible = false;
                    break;
                case RECIPIENT_QUESTION_GIVER:
                    buildTableColumnHeaderForRecipientQuestionGiverView(columnTags, isSortable);
                    responseRows = buildResponseRowsForQuestionForSingleRecipient(question, responses, 
                                                                                  participantIdentifier);
                    isCollapsible = false;
                    break;
                default:
                    Assumption.fail("View type should not involve question tables");
            }
        }
        
        InstructorFeedbackResultsQuestionTable questionTable = new InstructorFeedbackResultsQuestionTable(this, 
                                                                        responses, statisticsTable, 
                                                                        responseRows, question, additionalInfoId, 
                                                                        columnTags, isSortable);
        questionTable.setShowResponseRows(isShowingResponseRows);
        questionTable.setCollapsible(isCollapsible);
        
        return questionTable;
    }

    private void buildTableColumnHeaderForQuestionView(List<ElementTag> columnTags, 
                                                       Map<String, Boolean> isSortable) {
        ElementTag giverElement 
            = new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,1)", "style", "width: 15%;");
        ElementTag giverTeamElement 
            = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag recipientElement 
            = new ElementTag("Recipient", "id", "button_sortToName", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag recipientTeamElement 
            = new ElementTag("Team", "id", "button_sortToTeam", "class", "button-sort-ascending", "onclick", 
                             "toggleSort(this,4)", "style", "width: 15%;");
        ElementTag responseElement 
            = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,5)");
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
        ElementTag recipientElement 
            = new ElementTag("Recipient", "id", "button_sortTo", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag recipientTeamElement 
            = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending", "onclick", 
                             "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag responseElement 
            = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,4)");

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
        ElementTag giverElement 
            = new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,2)", "style", "width: 15%;");
        ElementTag giverTeamElement 
            = new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending", "onclick", 
                             "toggleSort(this,3)", "style", "width: 15%;");
        ElementTag responseElement 
            = new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none", "onclick", 
                             "toggleSort(this,4)");
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
    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestion(FeedbackQuestionAttributes question,
                                                                                    List<FeedbackResponseAttributes> responses) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<InstructorFeedbackResultsResponseRow>();
        
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
                                    bundle.getNameForEmail(prevGiver), bundle.getTeamNameForEmail(prevGiver)));
                
                String giverIdentifier = (question.giverType == FeedbackParticipantType.TEAMS) ? 
                                         bundle.getFullNameFromRoster(response.giverEmail) :
                                         response.giverEmail;
                            
                possibleReceiversWithoutResponsesForGiver = bundle.getPossibleRecipients(question, giverIdentifier);
            }
            
            // keep track of possible recipients without a response from the current giver
            removeParticipantIdentifierFromList(question.recipientType, 
                                                possibleReceiversWithoutResponsesForGiver, response.recipientEmail);
            prevGiver = response.giverEmail;
            
            InstructorFeedbackResultsModerationButton moderationButton = bundle.isGiverVisible(response) 
                                                               ? buildModerationButtonForExistingResponse(question, response)
                                                               : null;
            InstructorFeedbackResultsResponseRow responseRow = new InstructorFeedbackResultsResponseRow(
                                                               bundle.getGiverNameForResponse(question, response), 
                                                               bundle.getTeamNameForEmail(response.giverEmail), 
                                                               bundle.getRecipientNameForResponse(question, response), 
                                                               bundle.getTeamNameForEmail(response.recipientEmail), 
                                                               bundle.getResponseAnswerHtml(response, question), 
                                                               moderationButton);
            configureResponseRow(question, prevGiver, response.recipientEmail, responseRow);
            responseRows.add(responseRow);
        }
        
        responseRows.addAll(getRemainingMissingResponseRows(question, possibleGiversWithoutResponses, 
                                                            possibleReceiversWithoutResponsesForGiver, 
                                                            prevGiver, viewType));
        
        return responseRows;
    }
    
    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestionForSingleGiver(FeedbackQuestionAttributes question,
                                                                                          List<FeedbackResponseAttributes> responses,
                                                                                          String giverIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, giverIdentifier, true);
    }
    
    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestionForSingleRecipient(FeedbackQuestionAttributes question,
                                                                                              List<FeedbackResponseAttributes> responses,
                                                                                              String recipientIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, recipientIdentifier, false);
    }
    
    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestionForSingleParticipant(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String participantIdentifier, boolean isFirstGroupedByGiver) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<InstructorFeedbackResultsResponseRow>();
        
        List<String> possibleParticipantsWithoutResponses = isFirstGroupedByGiver 
                                                          ? bundle.getPossibleRecipients(question, participantIdentifier)
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
            
            InstructorFeedbackResultsModerationButton moderationButton 
                                                               = bundle.isGiverVisible(response) 
                                                               ? buildModerationButtonForExistingResponse(question, response)
                                                               : null;
            
            InstructorFeedbackResultsResponseRow responseRow 
                = new InstructorFeedbackResultsResponseRow(
                                   bundle.getGiverNameForResponse(question, response), 
                                   bundle.getTeamNameForEmail(response.giverEmail), 
                                   bundle.getRecipientNameForResponse(question, response), 
                                   bundle.getTeamNameForEmail(response.recipientEmail), 
                                   bundle.getResponseAnswerHtml(response, question), 
                                   moderationButton);
            
            configureResponseRow(question, response.giverEmail, response.recipientEmail, responseRow);
                        
            responseRows.add(responseRow);
        }

        if (isFirstGroupedByGiver) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                            question, possibleParticipantsWithoutResponses, participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier), 
                                            bundle.getTeamNameForEmail(participantIdentifier)));
        } else {
            responseRows.addAll(buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                            question, possibleParticipantsWithoutResponses, participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier),
                                            bundle.getTeamNameForEmail(participantIdentifier)));
        }
        
        
        return responseRows;
    }
    

    private void configureResponseRow(FeedbackQuestionAttributes question,
                                      String giver, String recipient,
                                      InstructorFeedbackResultsResponseRow responseRow) {
        
        switch (viewType) {
            case QUESTION:
                responseRow.setGiverProfilePictureLink(
                                validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty() 
                              ? new Url(getProfilePictureLink(giver))
                              : null);
                
                responseRow.setRecipientProfilePictureLink(
                                validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty() 
                              ? new Url(getProfilePictureLink(recipient)) 
                              : null);
                responseRow.setActionsDisplayed(true);
                break;
            case GIVER_QUESTION_RECIPIENT:
                responseRow.setGiverDisplayed(false);
                responseRow.setGiverProfilePictureLink(null);
                responseRow.setRecipientProfilePictureAColumn(true);
                
                boolean isRecipientEmailValid 
                    = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty();
                responseRow.setRecipientProfilePictureLink(isRecipientEmailValid 
                                                         ? new Url(getProfilePictureLink(recipient))
                                                         : null);
                responseRow.setActionsDisplayed(false);
                break;
            case RECIPIENT_QUESTION_GIVER:
                responseRow.setRecipientDisplayed(false);
                responseRow.setGiverProfilePictureAColumn(true);
                
                boolean isGiverEmailValid 
                    = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty();
                responseRow.setGiverProfilePictureLink(isGiverEmailValid 
                                                     ? new Url(getProfilePictureLink(giver))
                                                     : null);
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
    private List<InstructorFeedbackResultsResponseRow> buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                                                    FeedbackQuestionAttributes question, 
                                                                    List<String> possibleReceivers, 
                                                                    String giverIdentifier,
                                                                    String giverName, String giverTeam) {
        List<InstructorFeedbackResultsResponseRow> missingResponses = new ArrayList<InstructorFeedbackResultsResponseRow>();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        
        for (String possibleRecipient : possibleReceivers) {
            String possibleRecipientName = bundle.getFullNameFromRoster(possibleRecipient);
            String possibleRecipientTeam = bundle.getTeamNameFromRoster(possibleRecipient);
            
            String textToDisplay 
                = questionDetails.getNoResponseTextInHtml(giverIdentifier, possibleRecipient, bundle, question);
            
            if (questionDetails.shouldShowNoResponseText(giverIdentifier, possibleRecipient, question)) {
                InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                         question, giverIdentifier, 
                                                                         "btn btn-default btn-xs", "Moderate Response");
                InstructorFeedbackResultsResponseRow missingResponse 
                    = new InstructorFeedbackResultsResponseRow(giverName, giverTeam, 
                                                               possibleRecipientName, possibleRecipientTeam, 
                                                               textToDisplay, moderationButton, true);

                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRow(question, giverIdentifier, possibleRecipient, missingResponse);
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
    private List<InstructorFeedbackResultsResponseRow> buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                    FeedbackQuestionAttributes question, 
                                    List<String> possibleGivers, String recipientIdentifier,
                                    String recipientName, String recipientTeam) {
        List<InstructorFeedbackResultsResponseRow> missingResponses = new ArrayList<InstructorFeedbackResultsResponseRow>();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        
        for (String possibleGiver : possibleGivers) {
            String possibleGiverName = bundle.getFullNameFromRoster(possibleGiver);
            String possibleGiverTeam = bundle.getTeamNameFromRoster(possibleGiver);
            
            String textToDisplay = questionDetails.getNoResponseTextInHtml(recipientIdentifier, possibleGiver, 
                                                                           bundle, question);
            
            if (questionDetails.shouldShowNoResponseText(possibleGiver, recipientIdentifier, question)) {
                InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                                 question, possibleGiver, 
                                                                                 "btn btn-default btn-xs", 
                                                                                 "Moderate Response");
                InstructorFeedbackResultsResponseRow missingResponse = new InstructorFeedbackResultsResponseRow(
                                                                                    possibleGiverName, possibleGiverTeam, 
                                                                                    recipientName, recipientTeam, 
                                                                                    textToDisplay, moderationButton, true);
                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRow(question, possibleGiver, recipientIdentifier, missingResponse);
                
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
    
    private List<InstructorFeedbackResultsResponseRow> getRemainingMissingResponseRows(
                                                FeedbackQuestionAttributes question,
                                                List<String> remainingPossibleGivers, List<String> possibleRecipientsForGiver, 
                                                String prevGiver, ViewType viewType) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<InstructorFeedbackResultsResponseRow>();
        
        if (possibleRecipientsForGiver != null) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                            question, possibleRecipientsForGiver,
                                            prevGiver, bundle.getNameForEmail(prevGiver), 
                                            bundle.getTeamNameForEmail(prevGiver)));
            
        }
        
        removeParticipantIdentifierFromList(question.giverType, remainingPossibleGivers, prevGiver);
            
        for (String possibleGiverWithNoResponses : remainingPossibleGivers) {
            if (!isAllSectionsSelected() && !bundle.getSectionFromRoster(possibleGiverWithNoResponses).equals(selectedSection)) {
                continue;
            }
            possibleRecipientsForGiver = bundle.getPossibleRecipients(question, possibleGiverWithNoResponses);
            
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                            question, possibleRecipientsForGiver, possibleGiverWithNoResponses, 
                                            bundle.getFullNameFromRoster(possibleGiverWithNoResponses),
                                            bundle.getTeamNameFromRoster(possibleGiverWithNoResponses)));
        }
        
        return responseRows;
    }
    

    private InstructorFeedbackResultsModerationButton buildModerationButtonForExistingResponse(FeedbackQuestionAttributes question,
                                                                      FeedbackResponseAttributes response) {
        return buildModerationButtonForGiver(question, response.giverEmail, "btn btn-default btn-xs", "Moderate Response");
    }
    
    private InstructorFeedbackResultsModerationButton buildModerationButtonForGiver(FeedbackQuestionAttributes question,
                                                                            String giverIdentifier, String className,
                                                                            String buttonText) {
        boolean isAllowedToModerate = instructor.isAllowedForPrivilege(bundle.getSectionFromRoster(giverIdentifier), 
                                                     getFeedbackSessionName(), 
                                                     Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isDisabled = !isAllowedToModerate;
        
        
        if (question != null) {
            giverIdentifier = question.giverType.isTeam() ? giverIdentifier.replace(Const.TEAM_OF_EMAIL_OWNER,"") 
                                                          : giverIdentifier;
        } else {
            giverIdentifier = giverIdentifier.matches(Const.REGEXP_TEAM) 
                            ? giverIdentifier.replace(Const.TEAM_OF_EMAIL_OWNER,"")
                            : giverIdentifier;
        }
        
        InstructorFeedbackResultsModerationButton moderationButton = new InstructorFeedbackResultsModerationButton(
                                                                    isAllowedToModerate, isDisabled, className,
                                                                    giverIdentifier, getCourseId(), 
                                                                    getFeedbackSessionName(), question, buttonText);
        return moderationButton;
   }
    


   
   private InstructorFeedbackResultsGroupByParticipantPanel buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                   String participantIdentifier, String participantName, 
                                   List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels, 
                                   InstructorFeedbackResultsModerationButton moderationButton) {
      
        InstructorFeedbackResultsGroupByParticipantPanel bySecondaryParticipantPanel = 
                                        new InstructorFeedbackResultsGroupByParticipantPanel(secondaryParticipantPanels);
        bySecondaryParticipantPanel.setParticipantIdentifier(participantIdentifier);
        bySecondaryParticipantPanel.setName(participantName);
        bySecondaryParticipantPanel.setGiver(viewType.isPrimaryGroupingOfGiverType());
        
        boolean isEmailValid = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
        bySecondaryParticipantPanel.setEmailValid(isEmailValid);
        
        Url profilePictureLink = new Url(isEmailValid 
                                       ? getProfilePictureLink(participantIdentifier)
                                       : null);
        bySecondaryParticipantPanel.setProfilePictureLink(profilePictureLink.toString());
        
        bySecondaryParticipantPanel.setModerationButton(moderationButton);
        
        bySecondaryParticipantPanel.setHasResponses(true);
        
        return bySecondaryParticipantPanel;
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
    
    private List<FeedbackResponseComment> buildResponseComments(String giverName, String recipientName,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response) {
        List<FeedbackResponseComment> comments = new ArrayList<FeedbackResponseComment>();
        List<FeedbackResponseCommentAttributes> frcAttributesList = bundle.responseComments.get(response.getId());
        if (frcAttributesList != null) {
            for (FeedbackResponseCommentAttributes frcAttributes : frcAttributesList) {
                comments.add(buildResponseComment(giverName, recipientName, question, response, frcAttributes));
            }
        }
        return comments;
    }
    
    private FeedbackResponseComment buildResponseComment(String giverName, String recipientName,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
            FeedbackResponseCommentAttributes frcAttributes) {
        boolean isInstructorGiver = instructor.email.equals(frcAttributes.giverEmail);
        boolean isInstructorWithPrivilegesToModify =
                instructor.isAllowedForPrivilege(
                        response.giverSection, response.feedbackSessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                && instructor.isAllowedForPrivilege(
                           response.recipientSection, response.feedbackSessionName,
                           Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isInstructorAllowedToModify = isInstructorGiver || isInstructorWithPrivilegesToModify;
        
        boolean isResponseVisibleToRecipient =
                question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
        
        boolean isResponseVisibleToGiverTeam =
                question.giverType != FeedbackParticipantType.INSTRUCTORS
                && question.giverType != FeedbackParticipantType.SELF
                && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        
        boolean isResponseVisibleToRecipientTeam =
                question.recipientType != FeedbackParticipantType.INSTRUCTORS
                && question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        
        boolean isResponseVisibleToStudents =
                question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
        
        boolean isResponseVisibleToInstructors =
                question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);

        return new FeedbackResponseComment(frcAttributes, frcAttributes.giverEmail, giverName, recipientName,
                getResponseCommentVisibilityString(frcAttributes, question),
                getResponseCommentGiverNameVisibilityString(frcAttributes, question),
                isResponseVisibleToRecipient, isResponseVisibleToGiverTeam, isResponseVisibleToRecipientTeam,
                isResponseVisibleToStudents, isResponseVisibleToInstructors,
                true, isInstructorAllowedToModify, isInstructorAllowedToModify);
    }
    
    private FeedbackResponseComment setUpFeedbackResponseCommentAdd(FeedbackQuestionAttributes question,
                        FeedbackResponseAttributes response, Map<FeedbackParticipantType, Boolean> responseVisibilityMap,
                        String giverName, String recipientName) {
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };
        
        List<FeedbackParticipantType> showCommentTo = new ArrayList<>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<>();
        for (FeedbackParticipantType type : relevantTypes) {
            if (isResponseCommentVisibleTo(question, type)) {
                showCommentTo.add(type);
            }
            if (isResponseCommentGiverNameVisibleTo(question, type)) {
                showGiverNameTo.add(type);
            }
        }
        
        FeedbackResponseCommentAttributes frca = new FeedbackResponseCommentAttributes(
                    question.courseId, question.feedbackSessionName, question.getFeedbackQuestionId(), response.getId());
        return new FeedbackResponseComment(frca, giverName, recipientName,
                getResponseCommentVisibilityString(question), getResponseCommentGiverNameVisibilityString(question),
                responseVisibilityMap.get(FeedbackParticipantType.RECEIVER),
                responseVisibilityMap.get(FeedbackParticipantType.OWN_TEAM_MEMBERS),
                responseVisibilityMap.get(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS),
                responseVisibilityMap.get(FeedbackParticipantType.STUDENTS),
                responseVisibilityMap.get(FeedbackParticipantType.INSTRUCTORS),
                showCommentTo, showGiverNameTo, true);
    }
    
    private Map<FeedbackParticipantType, Boolean> getResponseVisibilityMap(FeedbackQuestionAttributes question) {
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = new HashMap<>();
        boolean isResponseVisibleToGiver =
                question.isResponseVisibleTo(FeedbackParticipantType.GIVER);
        boolean isResponseVisibleToRecipient =
                question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
        boolean isResponseVisibleToGiverTeam =
                question.giverType != FeedbackParticipantType.INSTRUCTORS
                && question.giverType != FeedbackParticipantType.SELF
                && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        boolean isResponseVisibleToRecipientTeam =
                question.recipientType != FeedbackParticipantType.INSTRUCTORS
                && question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        boolean isResponseVisibleToStudents =
                question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
        boolean isResponseVisibleToInstructors =
                question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
        
        responseVisibilityMap.put(FeedbackParticipantType.GIVER, isResponseVisibleToGiver);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER, isResponseVisibleToRecipient);
        responseVisibilityMap.put(FeedbackParticipantType.OWN_TEAM_MEMBERS, isResponseVisibleToGiverTeam);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS, isResponseVisibleToRecipientTeam);
        responseVisibilityMap.put(FeedbackParticipantType.STUDENTS, isResponseVisibleToStudents);
        responseVisibilityMap.put(FeedbackParticipantType.INSTRUCTORS, isResponseVisibleToInstructors);
        
        return responseVisibilityMap;
    }
    
    // TODO remove this entirely and use PageData method directly
    public String getProfilePictureLink(String studentEmail) {
        return getStudentProfilePictureLink(StringHelper.encrypt(studentEmail),
                                            StringHelper.encrypt(instructor.courseId));
    }

    public void setBundle(FeedbackSessionResultsBundle bundle) {
        this.bundle = bundle;
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

    public List<InstructorFeedbackResultsQuestionTable> getQuestionPanels() {
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
               ? getInstructorFeedbackEditLink(bundle.feedbackSession.courseId, 
                                                      bundle.feedbackSession.feedbackSessionName)
               : null;
    }
    
    private String getInstructorFeedbackSessionResultsLink() {
        return getInstructorFeedbackResultsLink(bundle.feedbackSession.courseId, bundle.feedbackSession.feedbackSessionName);
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
                isGroupedByTeam(), sortType, getInstructorFeedbackSessionResultsLink(), getSections());
    }
    
    public InstructorFeedbackResultsNoResponsePanel getNoResponsePanel() {
        return new InstructorFeedbackResultsNoResponsePanel(bundle.responseStatus);
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setAjaxStatus(String ajaxStatus) {
        this.ajaxStatus = ajaxStatus;
    }

    public void setSessionResultsHtmlTableAsString(String sessionResultsHtmlTableAsString) {
        this.sessionResultsHtmlTableAsString = sessionResultsHtmlTableAsString;
    }
}
