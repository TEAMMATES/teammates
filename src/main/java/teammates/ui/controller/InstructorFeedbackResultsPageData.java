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

    private static final String MODERATE_RESPONSES_FOR_GIVER = "Moderate Responses";
    private static final String MODERATE_SINGLE_RESPONSE = "Moderate Response";
    
    // TODO find out why it's 500
    private static final int RESPONSE_LIMIT_FOR_COLLAPSING_PANEL = 500;
    private static final int RESPONDENTS_LIMIT_FOR_AUTOLOADING = 150;

    // isLargeNumberOfRespondents is an attribute used for testing the ui, for ViewType.Question 
    private boolean isLargeNumberOfRespondents = false;
    
    private FeedbackSessionResultsBundle bundle = null;
    private InstructorAttributes instructor = null;
    private List<String> sections = null;
    private String selectedSection = null;
    private String sortType = null;
    private String groupByTeam = null;
    private String showStats = null;
    private int startIndex = -1;
    private boolean isPanelsCollapsed;
    
    private FieldValidator validator = new FieldValidator();
    private String feedbackSessionName = null;
    
    private String displayableFsName = null;
    private String displayableCourseId = null;
    
    // used for html table ajax loading
    private String ajaxStatus = null;
    private String sessionResultsHtmlTableAsString = null;
    

    // for question view
    private List<InstructorFeedbackResultsQuestionTable> questionPanels;
    // for giver > question > recipient, recipient > question > giver,
    // giver > recipient > question, recipient > giver > question
    private LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels;
    
    private Map<FeedbackQuestionAttributes, FeedbackQuestionDetails> questionToDetailsMap = new HashMap<>();
    private Map<String, String> profilePictureLinks = new HashMap<>();
    
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
                case RECIPIENT_QUESTION_GIVER:
                    return "recipient-%s-question-%s";
                case GIVER_RECIPIENT_QUESTION:
                case RECIPIENT_GIVER_QUESTION:
                    return "giver-%s-recipient-%s";
                default:
                    return "";
            }
        }
    }
    
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
    }
    
    /**
     * Prepares question tables for viewing
     *  
     * {@code bundle} should be set before this method
     */
    public void initForViewByQuestion(InstructorAttributes instructor, 
                                      String selectedSection, String showStats, 
                                      String groupByTeam) {
        this.viewType = ViewType.QUESTION;
        this.sortType = ViewType.QUESTION.toString();
        initCommonVariables(instructor, selectedSection, showStats, groupByTeam);
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorFeedbackResultsQuestionTable>();
        
        // if there is more than one question, we omit generation of responseRows,
        // and load them by ajax question by question
        boolean isLoadingStructureOnly = questionToResponseMap.size() > 1;
                                        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : questionToResponseMap.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();
            
            InstructorFeedbackResultsQuestionTable questionPanel;
            if (isLoadingStructureOnly) {
                questionPanel = buildQuestionTableWithoutResponseRows(question, responses, ""); 
                questionPanel.setHasResponses(false);
            } else {
                questionPanel = buildQuestionTableAndResponseRows(question, responses, "");
            }
            
            questionPanels.add(questionPanel);
        }
        
    }

    private void initCommonVariables(InstructorAttributes instructor, String selectedSection,
                                    String showStats, String groupByTeam) {
        Assumption.assertNotNull(bundle);
        
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        for (FeedbackQuestionAttributes question : bundle.questions.values()) {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            questionToDetailsMap.put(question, questionDetails);
        }
        
        displayableFsName = sanitizeForHtml(bundle.feedbackSession.feedbackSessionName);
        displayableCourseId = sanitizeForHtml(bundle.feedbackSession.courseId);
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
        initCommonVariables(instructor, selectedSection, showStats, groupByTeam);
        
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
        
        this.isPanelsCollapsed = bundle.responses.size() > RESPONSE_LIMIT_FOR_COLLAPSING_PANEL;
        
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
            String currentSection = getCurrentSection(primaryToSecondaryParticipantToResponsesMap, viewType);
            
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
            }
            
            if (isDifferentSection) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    finalizeBuildingSectionPanelWithoutTeamStats(sectionPanel, prevSection);
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
            }
            
            if (isDifferentTeam) {
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }
            
            // Build participant panel for the current primary participant
            InstructorFeedbackResultsParticipantPanel recipientPanel
                    = buildGroupByParticipantPanel(primaryParticipantIdentifier, primaryToSecondaryParticipantToResponsesMap, 
                                                   additionalInfoId, primaryParticipantIndex);
            
            sectionPanel.addParticipantPanel(currentTeam, recipientPanel);
            
            teamMembersWithResponses.add(primaryParticipantIdentifier);

            prevTeam = currentTeam;
            prevSection = currentSection;
        }
        
        // for the last section with responses
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);
        
        teamsWithResponses.add(prevTeam);
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, prevSection, teamsWithResponses);
        
        finalizeBuildingSectionPanelWithoutTeamStats(sectionPanel, prevSection);
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
           String currentSection = getCurrentSection(primaryToSecondaryParticipantToResponsesMap, viewType);
           
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
           }
           
           if (isDifferentTeam) {
               sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
           }
           
           // Build participant panel for the current participant 
           InstructorFeedbackResultsParticipantPanel primaryParticipantPanel 
                   = buildGroupByQuestionPanel(primaryParticipantIdentifier, 
                                               primaryToSecondaryParticipantToResponsesMap,
                                               additionalInfoId, primaryParticipantIndex);
           
           sectionPanel.addParticipantPanel(currentTeam, primaryParticipantPanel);
           
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
        String primaryParticipantNameWithTeamName 
            = bundle.appendTeamNameToName(bundle.getNameForEmail(primaryParticipantIdentifier), 
                                          bundle.getTeamNameForEmail(primaryParticipantIdentifier));
        
        InstructorFeedbackResultsModerationButton moderationButton;
        if (viewType.isPrimaryGroupingOfGiverType()) {
            boolean isTeam = bundle.rosterTeamNameMembersTable.containsKey(primaryParticipantIdentifier)
                          || primaryParticipantIdentifier.matches(Const.REGEXP_TEAM);
            String normalisedIdentifier = primaryParticipantIdentifier.matches(Const.REGEXP_TEAM) 
                                        ? primaryParticipantIdentifier.replace(Const.TEAM_OF_EMAIL_OWNER, "")
                                        : primaryParticipantIdentifier;
            
            boolean isStudent = bundle.isParticipantIdentifierStudent(normalisedIdentifier);
            
            String sectionName = bundle.getSectionFromRoster(primaryParticipantIdentifier);
            
            boolean isAllowedToModerate = (isStudent || isTeam) 
                                       && instructor.isAllowedForPrivilege(
                                               sectionName, feedbackSessionName, 
                                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            // Use the normalisedIdentifier instead of the original identifier to handle
            // Team responses where the primary giver identifier is "<email>'s Team"
            moderationButton 
                    = isAllowedToModerate ? buildModerationButtonForGiver(null, normalisedIdentifier, 
                                                    "btn btn-primary btn-xs", 
                                                    MODERATE_RESPONSES_FOR_GIVER)
                                          : null;
        } else {
            moderationButton = null;
        }
        InstructorFeedbackResultsGroupByParticipantPanel primaryParticipantPanel 
                = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                        primaryParticipantIdentifier, primaryParticipantNameWithTeamName, 
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
                                            secondaryParticipantIndex, secondaryParticipantResponses.getValue());
          
            InstructorFeedbackResultsSecondaryParticipantPanelBody secondaryParticipantPanel 
                     = new InstructorFeedbackResultsSecondaryParticipantPanelBody(
                                        secondaryParticipantIdentifier, secondaryParticipantDisplayableName, 
                                        responsePanels);
            
            secondaryParticipantPanel
                .setProfilePictureLink(getProfilePictureIfEmailValid(secondaryParticipantIdentifier));
            
            if (!viewType.isPrimaryGroupingOfGiverType()) {
                boolean isStudent = bundle.roster.getStudentForEmail(secondaryParticipantIdentifier) != null;
                boolean isVisibleTeam = bundle.rosterTeamNameMembersTable.containsKey(secondaryParticipantDisplayableName);
                String sectionName = bundle.getSectionFromRoster(secondaryParticipantIdentifier);
                boolean isAllowedToModerate = (isStudent || isVisibleTeam) 
                                           && instructor.isAllowedForPrivilege(
                                                  sectionName, feedbackSessionName, 
                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                boolean isShowingModerationButton = (isStudent || isVisibleTeam) 
                                                 && isAllowedToModerate;
                secondaryParticipantPanel.setModerationButton(isShowingModerationButton
                                                            ? buildModerationButtonForGiver(null, secondaryParticipantIdentifier, 
                                                                                            "btn btn-default btn-xs", 
                                                                                            MODERATE_RESPONSES_FOR_GIVER)
                                                            : null);
            }
            
            secondaryParticipantPanels.add(secondaryParticipantPanel);
        }
        
        return secondaryParticipantPanels;
    }

    private List<InstructorFeedbackResultsResponsePanel> buildResponsePanels(final String additionalInfoId,
                                    int primaryParticipantIndex, int secondaryRecipientIndex,
                                    List<FeedbackResponseAttributes> giverResponses) {
        List<InstructorFeedbackResultsResponsePanel> responsePanels = new ArrayList<>();
        
        for (int responseIndex = 0; responseIndex < giverResponses.size(); responseIndex++) {
            FeedbackResponseAttributes response = giverResponses.get(responseIndex);
            
            String questionId = response.feedbackQuestionId;
            FeedbackQuestionAttributes question = bundle.questions.get(questionId);
            String questionText = bundle.getQuestionText(questionId);
            
            int giverIndex     = viewType.isPrimaryGroupingOfGiverType() ? primaryParticipantIndex 
                                                                         : secondaryRecipientIndex;
            int recipientIndex = viewType.isPrimaryGroupingOfGiverType() ? secondaryRecipientIndex 
                                                                         : primaryParticipantIndex;
            
            String additionalInfoText 
                = questionToDetailsMap.get(question).getQuestionAdditionalInfoHtml(
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
            
            responsePanel.setCommentsIndexes(recipientIndex, giverIndex, responseIndex + 1);
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);
            FeedbackResponseComment frcForAdding = buildFeedbackResponseCommentAddForm(question, response, 
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
                                                         MODERATE_RESPONSES_FOR_GIVER);
            participantPanel = new InstructorFeedbackResultsGroupByQuestionPanel(
                                            participantIdentifier, bundle.getNameForEmail(participantIdentifier),
                                            questionTables, 
                                            getStudentProfilePictureLink(participantIdentifier, instructor.courseId), 
                                            true, moderationButton);
        } else {
            participantPanel = new InstructorFeedbackResultsGroupByQuestionPanel(
                                            questionTables, 
                                            getStudentProfilePictureLink(participantIdentifier, instructor.courseId), 
                                            viewType.isPrimaryGroupingOfGiverType(), participantIdentifier, 
                                            bundle.getNameForEmail(participantIdentifier));
        }
        
        return participantPanel;
    }
    
    private void finalizeBuildingSectionPanelWithoutTeamStats(InstructorFeedbackResultsSectionPanel sectionPanel,
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
                sectionPanel.setDisplayingTeamStatistics(true);
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) 
                                                    ? DISPLAY_NAME_FOR_DEFAULT_SECTION 
                                                    : sectionName);
                break;
            case RECIPIENT_GIVER_QUESTION:
            case GIVER_RECIPIENT_QUESTION:
                sectionPanel.setDisplayingTeamStatistics(false);
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) 
                                                    ? DISPLAY_NAME_FOR_DEFAULT_SECTION
                                                    : sectionName);
                break;
            default:
                Assumption.fail();
                break;
        }
    }
    
    private void buildMissingTeamAndParticipantPanelsForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
                                    Set<String> teamWithResponses) {

        // update the teams for the previous section
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionName);
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(teamWithResponses);
        
        // create for every remaining team in the section, participantResultsPanels for every team member
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembersOfTeam = new ArrayList<String>(
                                                     bundle.getTeamMembersFromRoster(teamWithoutResponses));
            Collections.sort(teamMembersOfTeam);
            if (viewType.isPrimaryGroupingOfGiverType()) {
                addMissingParticipantsPanelsWithModerationButtonForTeam(
                                                sectionPanel, teamWithoutResponses, teamMembersOfTeam);
            } else {
                addMissingParticipantsPanelsWithoutModerationButtonForTeam(
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
    private static <K> String getCurrentSection(
                        Map.Entry<String, Map<K, List<FeedbackResponseAttributes>>> responses,
                        ViewType viewType) {
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
        
        Set<String> teamMembersEmail = new HashSet<String>();
        teamMembersEmail.addAll(bundle.getTeamMembersFromRoster(teamName));
        
        Set<String> teamMembersWithoutResponses = new HashSet<String>(teamMembersEmail);
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);
        
        // Create missing participants panels for the previous team
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<String>(teamMembersWithoutResponses);
        Collections.sort(sortedTeamMembersWithoutResponses);
        
        if (viewType.isPrimaryGroupingOfGiverType()) {
            addMissingParticipantsPanelsWithModerationButtonForTeam(sectionPanel, 
                                                        teamName, sortedTeamMembersWithoutResponses);
        } else {
            addMissingParticipantsPanelsWithoutModerationButtonForTeam(sectionPanel, 
                                                        teamName, sortedTeamMembersWithoutResponses);
        }
        
    }

    private void buildSectionPanelsForMissingSections(Set<String> sectionsWithResponses) {
        Set<String> sectionsInCourse = bundle.rosterSectionTeamNameTable.keySet();
        Set<String> sectionsWithoutResponse = new HashSet<String>(sectionsInCourse);
        sectionsWithoutResponse.removeAll(sectionsWithResponses);
        
        List<String> sectionsWithoutResponsesList = new ArrayList<String>(sectionsWithoutResponse);
        Collections.sort(sectionsWithoutResponsesList);
        
        InstructorFeedbackResultsSectionPanel sectionPanel;
        for (String sectionWithoutResponses: sectionsWithoutResponsesList) {
            sectionPanel = new InstructorFeedbackResultsSectionPanel();
            finalizeBuildingSectionPanelWithoutTeamStats(sectionPanel, sectionWithoutResponses);
            sectionPanels.put(sectionWithoutResponses, sectionPanel);
            
            Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionWithoutResponses);
            List<String> teamsInSectionAsList = new ArrayList<String>(teamsInSection);
            
            Collections.sort(teamsInSectionAsList);
            
            for (String teamInMissingSection : teamsInSectionAsList) {
                List<String> teamMembers = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamInMissingSection));
                Collections.sort(teamMembers);
                
                if (viewType.isPrimaryGroupingOfGiverType()) {
                    addMissingParticipantsPanelsWithModerationButtonForTeam(
                                                    sectionPanel, teamInMissingSection, teamMembers);
                } else {
                    addMissingParticipantsPanelsWithoutModerationButtonForTeam(
                                                    sectionPanel, teamInMissingSection, teamMembers);
                }
            }
        }
    }

    private void buildSectionPanelsForForAjaxLoading(List<String> sections) {
        this.isPanelsCollapsed = true;
        
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
    private void addMissingParticipantsPanelsWithModerationButtonForTeam(
                                                             InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorFeedbackResultsModerationButton moderationButton 
                                                   = buildModerationButtonForGiver(null, teamMember, "btn btn-default btn-xs",
                                                                                   MODERATE_RESPONSES_FOR_GIVER);
            InstructorFeedbackResultsParticipantPanel giverPanel;
            
            if (!viewType.isSecondaryGroupingOfParticipantType()) {
                giverPanel 
                    = new InstructorFeedbackResultsGroupByQuestionPanel(
                                                    teamMember, bundle.getFullNameFromRoster(teamMember),
                                                    new ArrayList<InstructorFeedbackResultsQuestionTable>(), 
                                                    getStudentProfilePictureLink(teamMember, instructor.courseId), 
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
            sectionPanel.addParticipantPanel(teamName, giverPanel);
        }
    }
    
    private void addMissingParticipantsPanelsWithoutModerationButtonForTeam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, 
                                    String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            
            InstructorFeedbackResultsParticipantPanel giverPanel;
            
            if (!viewType.isSecondaryGroupingOfParticipantType()) {
                giverPanel = 
                    new InstructorFeedbackResultsGroupByQuestionPanel(
                            new ArrayList<InstructorFeedbackResultsQuestionTable>(), 
                            getStudentProfilePictureLink(teamMember, instructor.courseId), 
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
            
            sectionPanel.addParticipantPanel(teamName, giverPanel);
        }
    }
    

    /**
     * Constructs InstructorFeedbackResultsQuestionTable containing statistics for each team.
     * The statistics tables are added to the sectionPanel.
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
                
                List<FeedbackResponseAttributes> responsesForTeamAndQuestion 
                    = responsesGroupedByTeam.get(team).get(question);
        
                InstructorFeedbackResultsQuestionTable statsTable = buildQuestionTableWithoutResponseRows(
                                                                               question, responsesForTeamAndQuestion,
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
        
        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);
        String studentEmail = (student != null) ? student.email : null;
        String statisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, studentEmail, 
                                                                                 bundle, viewType.toString());
        
        String questionText = questionDetails.getQuestionText();
        String additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, additionalInfoId);
        
        InstructorFeedbackResultsQuestionTable questionTable = new InstructorFeedbackResultsQuestionTable( 
                                                                        !responses.isEmpty(), statisticsTable, 
                                                                        responseRows, question, 
                                                                        questionText, additionalInfoText, 
                                                                        columnTags, isSortable);
        if (viewType == ViewType.QUESTION) {
            // setup classes, for loading responses by ajax
            // ajax_submit: user needs to click on the panel to load
            // ajax_auto: responses are loaded automatically
            questionTable.setAjaxClass(isLargeNumberOfResponses()
                                     ? " ajax_submit" 
                                     : " ajax_auto");
        }
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
                                                               bundle.getGiverNameForResponse(response), 
                                                               bundle.getTeamNameForEmail(response.giverEmail), 
                                                               bundle.getRecipientNameForResponse(response), 
                                                               bundle.getTeamNameForEmail(response.recipientEmail), 
                                                               bundle.getResponseAnswerHtml(response, question), 
                                                               moderationButton);
            configureResponseRow(question, prevGiver, response.recipientEmail, responseRow);
            responseRows.add(responseRow);
        }
        
        if (!responses.isEmpty()) {
            responseRows.addAll(getRemainingMissingResponseRows(question, possibleGiversWithoutResponses, 
                                                                possibleReceiversWithoutResponsesForGiver, 
                                                                prevGiver, viewType));
        }
        
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
                                   bundle.getGiverNameForResponse(response), 
                                   bundle.getTeamNameForEmail(response.giverEmail), 
                                   bundle.getRecipientNameForResponse(response), 
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
                responseRow.setGiverProfilePictureLink(getProfilePictureIfEmailValid(giver));
                responseRow.setRecipientProfilePictureLink(getProfilePictureIfEmailValid(recipient));
                
                responseRow.setActionsDisplayed(true);
                break;
            case GIVER_QUESTION_RECIPIENT:
                responseRow.setGiverDisplayed(false);
                responseRow.setGiverProfilePictureLink(null);
                responseRow.setRecipientProfilePictureAColumn(true);
                
                responseRow.setRecipientProfilePictureLink(getProfilePictureIfEmailValid(recipient));
                responseRow.setActionsDisplayed(false);
                break;
            case RECIPIENT_QUESTION_GIVER:
                responseRow.setRecipientDisplayed(false);
                responseRow.setGiverProfilePictureAColumn(true);
                
                responseRow.setGiverProfilePictureLink(getProfilePictureIfEmailValid(giver));
                responseRow.setActionsDisplayed(true);
                break;
            default:
                Assumption.fail();            
        }
    }

    // TODO consider using Url in future
    private String getProfilePictureIfEmailValid(String email) {
        // TODO the check for determining whether to show a profile picture 
        // can be improved to use isStudent
        boolean isEmailValid 
            = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email).isEmpty();
        return isEmailValid ? getStudentProfilePictureLink(email, instructor.courseId)
                            : null;
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
        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);
        
        for (String possibleRecipient : possibleReceivers) {            
            if (questionDetails.shouldShowNoResponseText(giverIdentifier, possibleRecipient, question)) {
                String textToDisplay 
                    = questionDetails.getNoResponseTextInHtml(giverIdentifier, possibleRecipient, bundle, question);
                String possibleRecipientName = bundle.getFullNameFromRoster(possibleRecipient);
                String possibleRecipientTeam = bundle.getTeamNameFromRoster(possibleRecipient);
                
                InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                         question, giverIdentifier, 
                                                                         "btn btn-default btn-xs", MODERATE_SINGLE_RESPONSE);
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
        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);
        
        for (String possibleGiver : possibleGivers) {
            String possibleGiverName = bundle.getFullNameFromRoster(possibleGiver);
            String possibleGiverTeam = bundle.getTeamNameFromRoster(possibleGiver);
            
            String textToDisplay = questionDetails.getNoResponseTextInHtml(recipientIdentifier, possibleGiver, 
                                                                           bundle, question);
            
            if (questionDetails.shouldShowNoResponseText(possibleGiver, recipientIdentifier, question)) {
                InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                                 question, possibleGiver, 
                                                                                 "btn btn-default btn-xs", 
                                                                                 MODERATE_SINGLE_RESPONSE);
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
        return buildModerationButtonForGiver(question, response.giverEmail, "btn btn-default btn-xs", MODERATE_SINGLE_RESPONSE);
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
                                                                            isDisabled, className,
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
        bySecondaryParticipantPanel.setIsGiver(viewType.isPrimaryGroupingOfGiverType());
        
        boolean isEmailValid = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
        bySecondaryParticipantPanel.setEmailValid(isEmailValid);
        
        
        bySecondaryParticipantPanel.setProfilePictureLink(getProfilePictureIfEmailValid(participantIdentifier));
        
        
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
        boolean isInstructorAllowedToEditAndDeleteComment = isInstructorGiver || isInstructorWithPrivilegesToModify;
        
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);
        
        FeedbackResponseComment frc = new FeedbackResponseComment(
                                        frcAttributes, frcAttributes.giverEmail, giverName, recipientName, 
                                        getResponseCommentVisibilityString(frcAttributes, question),
                                        getResponseCommentGiverNameVisibilityString(frcAttributes, question),
                                        responseVisibilityMap);
                                    
        if (isInstructorAllowedToEditAndDeleteComment) {
            frc.enableEdit();
            frc.enableDelete();
        }
  
        return frc;
    }

    
    private FeedbackResponseComment buildFeedbackResponseCommentAddForm(FeedbackQuestionAttributes question,
                        FeedbackResponseAttributes response, Map<FeedbackParticipantType, Boolean> responseVisibilityMap,
                        String giverName, String recipientName) {                        
        FeedbackResponseCommentAttributes frca = new FeedbackResponseCommentAttributes(
                                        question.courseId, question.feedbackSessionName, question.getFeedbackQuestionId(), response.getId());
                                
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };
        
        frca.showCommentTo = new ArrayList<FeedbackParticipantType>();
        frca.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        for (FeedbackParticipantType type : relevantTypes) {
            if (isResponseCommentVisibleTo(question, type)) {
                frca.showCommentTo.add(type);
            }
            if (isResponseCommentGiverNameVisibleTo(question, type)) {
                frca.showGiverNameTo.add(type);
            }
        }
        
        return new FeedbackResponseComment(frca, giverName, recipientName, 
                                           getResponseCommentVisibilityString(question),
                                           getResponseCommentGiverNameVisibilityString(question), responseVisibilityMap);
    }
    
    private Map<FeedbackParticipantType, Boolean> getResponseVisibilityMap(FeedbackQuestionAttributes question) {
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = new HashMap<>();
        
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };
        
        for (FeedbackParticipantType participantType : relevantTypes) {
            responseVisibilityMap.put(participantType, isResponseVisibleTo(participantType, question));
        }
        
        return responseVisibilityMap;
    }
    
    //TODO investigate and fix the differences between question.isResponseVisibleTo and this method
    private boolean isResponseVisibleTo(FeedbackParticipantType participantType, FeedbackQuestionAttributes question) {
        switch (participantType) {
            case GIVER:
                return question.isResponseVisibleTo(FeedbackParticipantType.GIVER);
            case INSTRUCTORS:
                return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
            case OWN_TEAM_MEMBERS:
                return question.giverType != FeedbackParticipantType.INSTRUCTORS
                       && question.giverType != FeedbackParticipantType.SELF
                       && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
            case RECEIVER:
                return question.recipientType != FeedbackParticipantType.SELF
                       && question.recipientType != FeedbackParticipantType.NONE
                       && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
            case RECEIVER_TEAM_MEMBERS:
                return question.recipientType != FeedbackParticipantType.INSTRUCTORS
                        && question.recipientType != FeedbackParticipantType.SELF
                        && question.recipientType != FeedbackParticipantType.NONE
                        && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
            case STUDENTS:
                return question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
            default:
                Assumption.fail("Invalid participant type");
                return false;
        }
    }
    
    private Map<String, InstructorFeedbackResultsModerationButton> buildModerateButtons() {
        Map<String, InstructorFeedbackResultsModerationButton> moderationButtons = new HashMap<>();
        for (String giverIdentifier : bundle.responseStatus.emailNameTable.keySet()) {
            boolean isStudent = bundle.isParticipantIdentifierStudent(giverIdentifier);
            
            if (!isStudent) {
                continue;
            }
            
            String sectionName = bundle.getSectionFromRoster(giverIdentifier);
            boolean isAllowedToModerate = isStudent 
                                       && instructor.isAllowedForPrivilege(
                                               sectionName, feedbackSessionName, 
                                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            
            InstructorFeedbackResultsModerationButton moderationButton = new InstructorFeedbackResultsModerationButton(
                                        !isAllowedToModerate, "btn btn-default btn-xs", giverIdentifier, 
                                        bundle.feedbackSession.courseId, bundle.feedbackSession.feedbackSessionName, 
                                        null, "Submit Responses");
            moderationButtons.put(giverIdentifier, moderationButton);
            
        }
        
        return moderationButtons;
    }
    
    @Override
    public String getStudentProfilePictureLink(String studentEmail, String courseId) {
        if (!profilePictureLinks.containsKey(studentEmail)) {
            profilePictureLinks.put(studentEmail, 
                                    super.getStudentProfilePictureLink(StringHelper.encrypt(studentEmail),
                                                                       StringHelper.encrypt(courseId)));
       
        }
        
        return profilePictureLinks.get(studentEmail);
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
        return displayableCourseId;
    }

    public String getFeedbackSessionName() {
        return displayableFsName;
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

    public List<InstructorFeedbackResultsQuestionTable> getQuestionPanels() {
        return questionPanels;
    }

    public Map<String, InstructorFeedbackResultsSectionPanel> getSectionPanels() {
        return sectionPanels;
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
        return new InstructorFeedbackResultsNoResponsePanel(bundle.responseStatus,
                                                            buildModerateButtons());
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
    
    public boolean isLargeNumberOfResponses() {
        return (viewType == ViewType.QUESTION && isLargeNumberOfRespondents() && isAllSectionsSelected())
             || !bundle.isComplete;
    }
    
    public boolean isLargeNumberOfRespondents() {
        int numRespondents = (bundle.feedbackSession.respondingInstructorList.size() 
                           + bundle.feedbackSession.respondingStudentList.size());
        return isLargeNumberOfRespondents 
            || numRespondents > RESPONDENTS_LIMIT_FOR_AUTOLOADING;
    }

    
    // Only used for testing the ui
    public void setLargeNumberOfRespondents(boolean needAjax) {
        this.isLargeNumberOfRespondents = needAjax;
    }
    
}
