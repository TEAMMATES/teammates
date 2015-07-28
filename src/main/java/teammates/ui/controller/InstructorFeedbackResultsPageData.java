package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody;
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
    private static final String DISPLAY_NAME_FOR_DEFAULT_SECTION = "Not in a section";

    // TODO find out why it's 500
    private static final int RESPONSE_LIMIT_FOR_COLLAPSING_PANEL = 500;

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
    

    // for question view
    List<InstructorResultsQuestionTable> questionPanels;
    // for giver > question > recipient, recipient > question > giver...
    LinkedHashMap<String, InstructorFeedbackResultsSectionPanel> sectionPanels;
    
    // TODO multiple page data classes inheriting this for each view type, 
    // rather than an enum determining behavior in many methods
    ViewType viewType;
    enum ViewType {
        QUESTION, GIVER_QUESTION_RECIPIENT, RECIPIENT_QUESTION_GIVER, RECIPIENT_GIVER_QUESTION;
        
        public String toString() {
            // replace _ to - to keep it consistent with old behavior
            return name().toLowerCase().replaceAll("_", "-");
        }
        
        public boolean isFirstGroupedByGiver() {
            return this == GIVER_QUESTION_RECIPIENT;
        }
        
        public boolean isGroupedBySecondaryParticipant() {
            return this == RECIPIENT_GIVER_QUESTION;
        }
        
    }
    
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
        startIndex = -1;
    }
    
    public void initForViewByQuestion(InstructorAttributes instructor, 
                                      String selectedSection, String showStats, 
                                      String groupByTeam) {
        viewType = ViewType.QUESTION;
        
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorResultsQuestionTable>();
        
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
     * TODO: simplify the logic in this method
     */
    public void initForViewByGiverQuestionRecipient(InstructorAttributes instructor, 
                                                    String selectedSection, String showStats, 
                                                    String groupByTeam) {
        viewType = ViewType.GIVER_QUESTION_RECIPIENT;
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            if (isAllSectionsSelected()) {
                buildSectionPanelsForForAjaxLoading(sections);
            } else {
                buildSectionPanelWithErrorMessage();
            }
            
            return;
        }
        
        // Note that if the page needs to load by ajax, then responses will be empty too,
        // therefore the check whether the bundle needs to come before this
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > RESPONSE_LIMIT_FOR_COLLAPSING_PANEL);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByGiverQuestionRecipient(true);
        
        buildSectionPanelsForGiverQuestionRecipient(sortedResponses);
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
    public void initForViewByRecipientQuestionGiver(InstructorAttributes instructor, 
                                    String selectedSection, String showStats, 
                                    String groupByTeam) {
        viewType = ViewType.RECIPIENT_QUESTION_GIVER;
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            if (isAllSectionsSelected()) {
                buildSectionPanelsForForAjaxLoading(sections);
            } else {
                buildSectionPanelWithErrorMessage();
            }
            
            return;
        }
        
        // Note that if the page needs to load by ajax, then responses will be empty too,
        // therefore the check whether the bundle needs to come before this
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > RESPONSE_LIMIT_FOR_COLLAPSING_PANEL);
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByRecipientQuestionGiver(true);
       
        buildSectionPanelsForRecipientQuestionGiver(sortedResponses);
    }
    
    public void initForViewByRecipientGiverQuestion(InstructorAttributes instructor, 
                                    String selectedSection, String showStats, 
                                    String groupByTeam) {
        viewType = ViewType.RECIPIENT_GIVER_QUESTION;
        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        
        if (!bundle.isComplete) {
            // results page to be loaded by ajax instead 
            if (isAllSectionsSelected()) {
                buildSectionPanelsForForAjaxLoading(sections);
            } else {
                buildSectionPanelWithErrorMessage();
            }
            
            return;
        }
        
        // Note that if the page needs to load by ajax, then responses will be empty too,
        // therefore the check whether the bundle needs to come before this
        if (bundle.responses.isEmpty()) {
            // no responses, nothing to initialize
            return;
        }
        
        setShouldCollapsed(bundle.responses.size() > RESPONSE_LIMIT_FOR_COLLAPSING_PANEL);
        
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponses 
                     = bundle.getResponsesSortedByRecipient(true);
        buildSectionPanelsForRecipientGiverQuestion(sortedResponses);
        
    }

    /**
     * TODO  buildSectionPanelsFor.* should be the same method
     * @param sortedResponses
     */
    private void buildSectionPanelsForGiverQuestionRecipient(
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {
        final String additionalInfoId = "giver-%s-question-%s";
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByGiverTeam();
        
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        // Maintain previous section and previous team while iterating through the loop
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        // Used for displaying sections without responses for the course at the end
        Set<String> sectionsWithResponses = new HashSet<String>();
        // Used for displaying teams without responses for every section
        LinkedHashSet<String> teamsWithResponses = new LinkedHashSet<String>();
        // Used for displaying participants without responses for every team
        Set<String> teamMembersWithResponses = new HashSet<String>();                                
        
        int giverIndex = this.startIndex;
        // Iterates through the response givers
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver : 
                                                                    sortedResponses.entrySet()) {
            giverIndex += 1;
            String giverIdentifier = responsesFromGiver.getKey();
            
            String currentTeam = getCurrentTeam(bundle, giverIdentifier);
            String currentSection = getCurrentSection(responsesFromGiver);
            
            boolean isDifferentTeam = !prevTeam.equals(currentTeam);
            boolean isDifferentSection = !prevSection.equals(currentSection);

            // handle a change of team by building panels for missing participants
            // and setting the sectionPanel's flags for teams with responses
            if (isDifferentTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    buildMissingParticipantPanelsForTeam(
                                                    sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear(); 
                }
                
                teamsWithResponses.add(currentTeam);
                if (!isDifferentSection) {
                    sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
                }
            }
            
            // Change in section
            if (isDifferentSection) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    finalizeBuildingSectionPanel(sectionPanel, prevSection, responsesGroupedByTeam,
                                                 teamsWithResponses);
                    
                    // add sectionPanel to sectionPanels
                    buildMissingTeamAndParticipantPanelsForSection(sectionPanel, prevSection, teamsWithResponses);
                    sectionPanels.put(prevSection, sectionPanel);
                    sectionsWithResponses.add(prevSection);
                    
                    // and set up for next section
                    teamsWithResponses.clear();
                    teamsWithResponses.add(currentTeam);
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }
                
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }
            
            // For the current giver, constructs a panel 
            InstructorFeedbackResultsGroupByQuestionPanel giverPanel 
                    = buildParticipantGroupByQuestionPanel(giverIdentifier, responsesFromGiver,
                                                           additionalInfoId, giverIndex);
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, giverPanel);
            teamMembersWithResponses.add(giverIdentifier);
            
            prevTeam = currentTeam;
            prevSection = currentSection;
        }
        
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

    private void buildSectionPanelsForRecipientQuestionGiver(
                                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponses) {        
        final String additionalInfoId = "recipient-%s-question-%s";
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam 
                = bundle.getQuestionResponseMapByRecipientTeam();
        
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        // Maintain previous section and previous team while iterating through the loop
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        // Used for displaying sections without responses for the course at the end
        Set<String> sectionsWithResponses = new HashSet<String>();
        // Used for displaying teams without responses for every section
        LinkedHashSet<String> teamsWithResponses = new LinkedHashSet<String>();
        // Used for displaying participants without responses for every team
        Set<String> teamMembersWithResponses = new HashSet<String>();       
        
        // Iterate through the recipients
        int recipientIndex = this.startIndex;
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesToRecipient : 
                                                                    sortedResponses.entrySet()) {
            recipientIndex += 1;
            String recipientIdentifier = responsesToRecipient.getKey();
            
            String currentTeam = getCurrentTeam(bundle, recipientIdentifier);
            String currentSection = getCurrentSection(responsesToRecipient);
            
            boolean isDifferentTeam = !prevTeam.equals(currentTeam);
            boolean isDifferentSection = !prevSection.equals(currentSection);

            // handle a change of team by building panels for missing participants
            // and setting the sectionPanel's flags for teams with responses
            if (isDifferentTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    buildMissingParticipantPanelsForTeam(
                                                    sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear(); 
                }
                
                teamsWithResponses.add(currentTeam);
                if (!isDifferentSection) {
                    sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
                }
            }
            
            // Change in section
            if (isDifferentSection) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    finalizeBuildingSectionPanel(sectionPanel, prevSection, responsesGroupedByTeam,
                                                 teamsWithResponses);
                    
                    // add to sectionPanels
                    buildMissingTeamAndParticipantPanelsForSection(
                                                    sectionPanel, prevSection, teamsWithResponses);
                    sectionPanels.put(prevSection, sectionPanel);
                    
                    sectionsWithResponses.add(prevSection);
                    
                    // and set up for next section 
                    teamsWithResponses.clear();
                    teamsWithResponses.add(currentTeam);
                    
                    sectionPanel = new InstructorFeedbackResultsSectionPanel();
                }
                
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }
            // Construct panel for the current recipient
            InstructorFeedbackResultsGroupByQuestionPanel recipientPanel 
                    = buildParticipantGroupByQuestionPanel(recipientIdentifier, responsesToRecipient,
                                                           additionalInfoId, recipientIndex);
            
            // add constructed InstructorFeedbackResultsGroupByQuestionPanel into section's participantPanels            
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, recipientPanel);
            teamMembersWithResponses.add(recipientIdentifier);
            
            prevTeam = currentTeam;
            prevSection = currentSection;
        }
        
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

    private void buildSectionPanelsForRecipientGiverQuestion(
                                    Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponses) {
        final String additionalInfoId = "giver-%s-recipient-%s";
        sectionPanels = new LinkedHashMap<String, InstructorFeedbackResultsSectionPanel>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        
        // Maintain previous section and previous team while iterating through the recipients
        // initialize the previous section to "None"
        String prevSection = Const.DEFAULT_SECTION;
        String prevTeam = "";
        
        Set<String> sectionsWithResponses = new HashSet<String>();
        Set<String> teamsWithResponses = new HashSet<String>();
        Set<String> teamMembersWithResponses = new HashSet<String>();      
          
        // Iterate through the recipients
        int recipientIndex = this.startIndex;
        for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> recipientToGiverToResponsesMap : 
                                                                              sortedResponses.entrySet()) {
            recipientIndex += 1;
            String recipientIdentifier = recipientToGiverToResponsesMap.getKey();
            
            String currentTeam = getCurrentTeam(bundle, recipientIdentifier);
            String currentSection = getCurrentSection(recipientToGiverToResponsesMap);
            
            boolean isChangeOfTeam = !prevTeam.equals(currentTeam);
            boolean isChangeOfSection = !prevSection.equals(currentSection);

            if (isChangeOfTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    // construct missing participant panels for the previous team
                    buildMissingParticipantPanelsForTeam(
                        sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear(); 
                }
                
                teamsWithResponses.add(currentTeam);
                if (!isChangeOfSection) { // add team to sectionPanel only if it's the correct section
                    sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
                }
            }
            
            if (isChangeOfSection) {
                boolean isFirstSection = sectionPanel.getParticipantPanels().isEmpty();
                if (!isFirstSection) {
                    // Finalize building of section panel,
                    finaliseBuildingSectionPanelWithoutStats(sectionPanel, prevSection);
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
            
            // Build recipient panel for the current response's recipient
            InstructorFeedbackResultsGroupByParticipantPanel recipientPanel 
                    = buildGroupByParticipantPanel(recipientIdentifier, currentTeam, additionalInfoId, recipientIndex,
                                                   recipientToGiverToResponsesMap);
            addParticipantPanelToSectionPanel(sectionPanel, currentTeam, recipientPanel);
            
            teamMembersWithResponses.add(recipientIdentifier);

            prevTeam = currentTeam;
            prevSection = currentSection;
        }
        
        // for the last section with responses
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);
        
        teamsWithResponses.add(prevTeam);
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, prevSection, teamsWithResponses);
        
        finaliseBuildingSectionPanelWithoutStats(sectionPanel, prevSection);
        sectionPanels.put(prevSection, sectionPanel);   
        if (isAllSectionsSelected()) {
            sectionsWithResponses.add(prevSection); // for the last section having responses 
            buildSectionPanelsForMissingSections(sectionsWithResponses);
        }
    }

    private InstructorFeedbackResultsGroupByParticipantPanel buildGroupByParticipantPanel(
                                    String primaryParticipantIdentifier, String currentTeam,
                                    String additionalInfoId, int primaryParticipantIndex,
                                    Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> primaryToSecondaryToResponsesMap) {
        // first build secondary participant panels for the primary participant panel
        Map<String, List<FeedbackResponseAttributes>> giverToResponsesMap = primaryToSecondaryToResponsesMap.getValue();
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels 
                             = buildSecondaryParticipantPanels(
                                        additionalInfoId, primaryParticipantIndex, giverToResponsesMap);
        
        // construct the primary participant panel
        String primaryParticipantNameWithTeamNameAppended = bundle.appendTeamNameToName(bundle.getNameForEmail(primaryParticipantIdentifier), bundle.getTeamNameForEmail(primaryParticipantIdentifier));
        InstructorFeedbackResultsGroupByParticipantPanel recipientPanel 
                = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                        primaryParticipantIdentifier, primaryParticipantNameWithTeamNameAppended, 
                                        secondaryParticipantPanels, null);
        
        return recipientPanel;
    }

    private List<InstructorFeedbackResultsSecondaryParticipantPanelBody> buildSecondaryParticipantPanels(
                                    String additionalInfoId, int primaryParticipantIndex,
                                    Map<String, List<FeedbackResponseAttributes>> secondaryParticipantToResponsesMap) {
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels = new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>();
        
        int secondaryParticipantIndex = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> secondaryParticipantResponses : secondaryParticipantToResponsesMap.entrySet()) {
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
                                            additionalInfoId, primaryParticipantIndex, secondaryParticipantIndex, secondaryParticipantResponses);
          
            InstructorFeedbackResultsSecondaryParticipantPanelBody secondaryParticipantPanel 
                     = new InstructorFeedbackResultsSecondaryParticipantPanelBody(
                                        secondaryParticipantIdentifier, secondaryParticipantDisplayableName, 
                                        responsePanels, isEmail);
            
            secondaryParticipantPanel.setProfilePictureLink(getProfilePictureLink(secondaryParticipantIdentifier));
            secondaryParticipantPanel.setModerationButton(buildModerationButtonForGiver(
                                                              null, secondaryParticipantIdentifier, 
                                                              "btn btn-default btn-xs", "Moderate Responses"));
            boolean isVisibleTeam = bundle.rosterTeamNameMembersTable.containsKey(secondaryParticipantDisplayableName);
            secondaryParticipantPanel.setModerationButtonDisplayed(isEmail || isVisibleTeam);
            secondaryParticipantPanels.add(secondaryParticipantPanel);
            
        }
        return secondaryParticipantPanels;
    }

    private List<InstructorFeedbackResultsResponsePanel> buildResponsePanels(final String additionalInfoId,
                                    int recipientIndex, int giverIndex,
                                    Map.Entry<String, List<FeedbackResponseAttributes>> giverResponses) {
        List<InstructorFeedbackResultsResponsePanel> responsePanels = new ArrayList<InstructorFeedbackResultsResponsePanel>();
        
        for (FeedbackResponseAttributes response : giverResponses.getValue()) {
            String questionId = response.feedbackQuestionId;
            FeedbackQuestionAttributes question = bundle.questions.get(questionId);
            String questionText = bundle.getQuestionText(questionId);
            String additionalInfoText = question.getQuestionDetails().getQuestionAdditionalInfoHtml(
                                                                          question.getQuestionNumber(), 
                                                                          String.format(additionalInfoId, giverIndex, recipientIndex));
            ElementTag rowAttributes = null;
            String displayableResponse = bundle.getResponseAnswerHtml(response, question);
            InstructorFeedbackResultsResponsePanel responsePanel = new InstructorFeedbackResultsResponsePanel(
                                            question, questionText,
                                            additionalInfoText, rowAttributes,
                                            displayableResponse );
            
            responsePanels.add(responsePanel);
        }
        return responsePanels;
    }

    private InstructorFeedbackResultsGroupByQuestionPanel buildParticipantGroupByQuestionPanel(
                                    String participantIdentifier,
                                    Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesForParticipant,
                                    String additionalInfoId, int participantIndex) {
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

            InstructorResultsQuestionTable questionTable 
                = buildQuestionTableAndResponseRows(currentQuestion, responsesForQuestion,
                                                    String.format(additionalInfoId, participantIndex, questionIndex), 
                                                    participantIdentifier, true);
            questionTable.setBoldQuestionNumber(false);
            questionTables.add(questionTable);
      
        }
        
        InstructorResultsQuestionTable.sortByQuestionNumber(questionTables);
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel;
        // Moderation button on the participant panels are only shown is the panel is a giver panel,
        // and if the participant is a student
        if (viewType.isFirstGroupedByGiver() && bundle.isParticipantIdentifierStudent(participantIdentifier)) {
            // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
            InstructorResultsModerationButton moderationButton 
                                                   = buildModerationButtonForGiver(
                                                         null, participantIdentifier, "btn btn-primary btn-xs", 
                                                         "Moderate Responses");
            participantPanel = InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanelWithModerationButton(
                                            participantIdentifier, bundle.getNameForEmail(participantIdentifier),
                                            questionTables, getProfilePictureLink(participantIdentifier), 
                                            true, moderationButton);
        } else {
            participantPanel = InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                                            questionTables, getProfilePictureLink(participantIdentifier), 
                                            viewType.isFirstGroupedByGiver(), participantIdentifier, bundle.getNameForEmail(participantIdentifier));
        }
        
        return participantPanel;
    }
    
    private void finaliseBuildingSectionPanelWithoutStats(InstructorFeedbackResultsSectionPanel sectionPanel,
                                                          String sectionWithoutResponses) {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> emptyResponseMap = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        LinkedHashSet<String> emptyTeamList = new LinkedHashSet<String>();
        finalizeBuildingSectionPanel(sectionPanel, sectionWithoutResponses, emptyResponseMap, emptyTeamList);
    }

    private void finalizeBuildingSectionPanel(
                         InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
                         LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                         LinkedHashSet<String> teamsWithResponses) {
        switch (viewType) {
            case GIVER_QUESTION_RECIPIENT:
            case RECIPIENT_QUESTION_GIVER:
                prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
                if (!responsesGroupedByTeam.isEmpty()) {
                    buildTeamsStatisticsTableForSectionPanel(sectionPanel, responsesGroupedByTeam, 
                                                             teamsWithResponses);
                }
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) ? DISPLAY_NAME_FOR_DEFAULT_SECTION 
                                                                                                : sectionName);
                sectionPanel.setDisplayingTeamStatistics(true);
                break;
            case RECIPIENT_GIVER_QUESTION:
                sectionPanel.setSectionName(sectionName);
                sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION) ? "Not in a section" 
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
        boolean isWithModerationButton = viewType.isFirstGroupedByGiver();

        // update the teams for the previous section
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionName);
        Set<String> teamsWithoutResponses = new HashSet<String>(teamsInSection);
        teamsWithoutResponses.removeAll(teamWithResponses);
        
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
            currentSection = viewType.isFirstGroupedByGiver() ? firstResponse.giverSection 
                                                              : firstResponse.recipientSection;
            break;
        }
        
        return currentSection;
    }
    
    private void buildMissingParticipantPanelsForTeam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String teamName,
                                    Set<String> teamMembersWithResponses) {
        boolean isDisplayingModerationButton = viewType.isFirstGroupedByGiver();
        
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
            finaliseBuildingSectionPanelWithoutStats(sectionPanel, sectionWithoutResponses);
            sectionPanels.put(sectionWithoutResponses, sectionPanel);
            
            Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionWithoutResponses);
            List<String> teamsInSectionAsList = new ArrayList<String>(teamsInSection);
            
            Collections.sort(teamsInSectionAsList);
            
            for (String teamInMissingSection : teamsInSectionAsList) {
                List<String> teamMembers = new ArrayList<String>(bundle.getTeamMembersFromRoster(teamInMissingSection));
                Collections.sort(teamMembers);
                
                if (viewType.isFirstGroupedByGiver()) {
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
        
        // TODO 
        // Abstract out "All" sections into a boolean or enum instead. Otherwise this will cause problems in future
        // if there is ever a section named "All"
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

    private void addMissingParticipantsForTeamToSectionPanelWithModerationButton(
                                                             InstructorFeedbackResultsSectionPanel sectionPanel, 
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorResultsModerationButton moderationButton 
                                                   = buildModerationButtonForGiver(null, teamMember, "btn btn-default btn-xs",
                                                                                   "Moderate Responses");
            InstructorResultsParticipantPanel giverPanel;
            
            if (!viewType.isGroupedBySecondaryParticipant()) {
                giverPanel 
                    = InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanelWithModerationButton(
                                                    teamMember, bundle.getFullNameFromRoster(teamMember),
                                                    new ArrayList<InstructorResultsQuestionTable>(), getProfilePictureLink(teamMember), 
                                                    viewType.isFirstGroupedByGiver(), moderationButton);
            } else {
                giverPanel = 
                    buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                                    teamMember, bundle.getFullNameFromRoster(teamMember) + "(" + bundle.getTeamNameForEmail(teamMember) + ")", 
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
            
            InstructorResultsParticipantPanel giverPanel;
            
            if (!viewType.isGroupedBySecondaryParticipant()) {
                giverPanel = 
                InstructorFeedbackResultsGroupByQuestionPanel.buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                    new ArrayList<InstructorResultsQuestionTable>(), getProfilePictureLink(teamMember), 
                    viewType.isFirstGroupedByGiver(), teamMember, bundle.getFullNameFromRoster(teamMember));
                
            } else {
                giverPanel = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                                                  teamMember, bundle.getFullNameFromRoster(teamMember) + " (" + bundle.getTeamNameFromRoster(teamMember) + ")",
                                                                  new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>(), 
                                                                  null);
            }
            giverPanel.setHasResponses(false);
            
            addParticipantPanelToSectionPanel(sectionPanel, teamName, giverPanel);
        }
    }
    

    private void addParticipantPanelToSectionPanel(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String currentTeam,
                                    InstructorResultsParticipantPanel giverPanel) {
        List<InstructorResultsParticipantPanel> teamsMembersPanels;
        
        if (sectionPanel.getParticipantPanels().containsKey(currentTeam)) {
            teamsMembersPanels = sectionPanel.getParticipantPanels().get(currentTeam);
        } else {
            teamsMembersPanels = new ArrayList<InstructorResultsParticipantPanel>();
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
                     LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                     LinkedHashSet<String> teamsInSection) {
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
                                                                               "");
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
                Assumption.fail("There should be no headers for the view type");
        }
    }

    
    private InstructorResultsQuestionTable buildQuestionTableAndResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String additionalInfoId) {
        return buildQuestionTableAndResponseRows(
                                        question, responses,
                                        additionalInfoId, 
                                        null, true);   
    }
    
    /**
     * Builds question tables without response rows, but with stats
     * @param question
     * @param responses  responses to compute statistics for
     * @param additionalInfoId
     */
    private InstructorResultsQuestionTable buildQuestionTableWithoutResponseRows(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String additionalInfoId) {
        return buildQuestionTableAndResponseRows(
                                        question, responses,
                                        additionalInfoId, 
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
    private InstructorResultsQuestionTable buildQuestionTableAndResponseRows(
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
        List<InstructorResultsResponseRow> responseRows = null;
        
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
        
        InstructorResultsQuestionTable questionTable = new InstructorResultsQuestionTable(this, 
                                                            responses, statisticsTable, 
                                                            responseRows, question, additionalInfoId, 
                                                            columnTags, isSortable);
        questionTable.setShowResponseRows(isShowingResponseRows);
        questionTable.setCollapsible(isCollapsible);
        
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
                                                                            List<FeedbackResponseAttributes> responses) {
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
                                    bundle.getNameForEmail(prevGiver), bundle.getTeamNameForEmail(prevGiver)));
                
                String giverIdentifier = (question.giverType == FeedbackParticipantType.TEAMS) ? 
                                         bundle.getFullNameFromRoster(response.giverEmail) :
                                         response.giverEmail;
                            
                possibleReceiversWithoutResponsesForGiver = bundle.getPossibleRecipients(question, giverIdentifier);
            }
            
            // keep track of possible recipients without a response from the current giver
            removeParticipantIdentifierFromList(question.recipientType, possibleReceiversWithoutResponsesForGiver, response.recipientEmail);
            prevGiver = response.giverEmail;
            
            InstructorResultsModerationButton moderationButton = bundle.isGiverVisible(response) 
                                                               ? buildModerationButtonForExistingResponse(question, response)
                                                               : null;
            InstructorResultsResponseRow responseRow = new InstructorResultsResponseRow(
                                                               bundle.getGiverNameForResponse(question, response), bundle.getTeamNameForEmail(response.giverEmail), 
                                                               bundle.getRecipientNameForResponse(question, response), bundle.getTeamNameForEmail(response.recipientEmail), 
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
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleGiver(FeedbackQuestionAttributes question,
                                                                                          List<FeedbackResponseAttributes> responses,
                                                                                          String giverIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, giverIdentifier, true);
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleRecipient(FeedbackQuestionAttributes question,
                                                                                              List<FeedbackResponseAttributes> responses,
                                                                                              String recipientIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, recipientIdentifier, false);
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForQuestionForSingleParticipant(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String participantIdentifier, boolean isFirstGroupedByGiver) {
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
            
            InstructorResultsModerationButton moderationButton = bundle.isGiverVisible(response) 
                                                               ? buildModerationButtonForExistingResponse(question, response)
                                                               : null;
            
            InstructorResultsResponseRow responseRow = new InstructorResultsResponseRow(
                                   bundle.getGiverNameForResponse(question, response), bundle.getTeamNameForEmail(response.giverEmail), 
                                   bundle.getRecipientNameForResponse(question, response), bundle.getTeamNameForEmail(response.recipientEmail), 
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
                                                 InstructorResultsResponseRow responseRow) {
        
        switch (viewType) {
            case QUESTION:

                responseRow.setGiverProfilePictureLink(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty() 
                                                       ? new Url(getProfilePictureLink(giver))
                                                       : null);
                
                
                responseRow.setRecipientProfilePictureLink(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty() 
                                                           ? new Url(getProfilePictureLink(recipient)) 
                                                           : null);
                responseRow.setActionsDisplayed(true);
                break;
            case GIVER_QUESTION_RECIPIENT:
                responseRow.setGiverDisplayed(false);
                responseRow.setGiverProfilePictureLink(null);
                responseRow.setRecipientProfilePictureAColumn(true);
                responseRow.setRecipientProfilePictureLink(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipient).isEmpty() 
                                                           ? new Url(getProfilePictureLink(recipient))
                                                           : null);
                responseRow.setActionsDisplayed(false);
                break;
            case RECIPIENT_QUESTION_GIVER:
                responseRow.setRecipientDisplayed(false);
                responseRow.setGiverProfilePictureAColumn(true);
                responseRow.setGiverProfilePictureLink(validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giver).isEmpty() 
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
    private List<InstructorResultsResponseRow> buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                                                    FeedbackQuestionAttributes question, 
                                                                    List<String> possibleReceivers, 
                                                                    String giverIdentifier,
                                                                    String giverName, String giverTeam) {
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
    private List<InstructorResultsResponseRow> buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                    FeedbackQuestionAttributes question, 
                                    List<String> possibleGivers, String recipientIdentifier,
                                    String recipientName, String recipientTeam) {
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
    
    private List<InstructorResultsResponseRow> getRemainingMissingResponseRows(
                                                FeedbackQuestionAttributes question,
                                                List<String> remainingPossibleGivers, List<String> possibleRecipientsForGiver, 
                                                String prevGiver, ViewType viewType) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        
        if (possibleRecipientsForGiver != null) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(question, possibleRecipientsForGiver,
                                                                                   prevGiver, 
                                                                                   bundle.getNameForEmail(prevGiver), 
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
    

    private InstructorResultsModerationButton buildModerationButtonForExistingResponse(FeedbackQuestionAttributes question,
                                                                      FeedbackResponseAttributes response) {
        return buildModerationButtonForGiver(question, response.giverEmail, "btn btn-default btn-xs", "Moderate Response");
    }
    
    private InstructorResultsModerationButton buildModerationButtonForGiver(FeedbackQuestionAttributes question,
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
        
        InstructorResultsModerationButton moderationButton = new InstructorResultsModerationButton(
                                                                    isAllowedToModerate, isDisabled, className,
                                                                    giverIdentifier, getCourseId(), 
                                                                    getFeedbackSessionName(), question, buttonText);
        return moderationButton;
   }
    


   
   private InstructorFeedbackResultsGroupByParticipantPanel buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                   String participantIdentifier, String participantName, 
                                   List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels, 
                                   InstructorResultsModerationButton moderationButton) {
        boolean isEmailValid = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
        Url profilePictureLink = new Url(getProfilePictureLink(participantIdentifier));
        
      
        InstructorFeedbackResultsGroupByParticipantPanel bySecondaryParticipantPanel = 
                                        new InstructorFeedbackResultsGroupByParticipantPanel(secondaryParticipantPanels);
        bySecondaryParticipantPanel.setParticipantIdentifier(participantIdentifier);
        bySecondaryParticipantPanel.setName(participantName);
        bySecondaryParticipantPanel.setGiver(viewType.isFirstGroupedByGiver());
        
        bySecondaryParticipantPanel.setEmailValid(isEmailValid);
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
    
    // TODO remove this entirely and use PageData method directly
    public String getProfilePictureLink(String studentEmail) {
        return getStudentProfilePictureLink(StringHelper.encrypt(studentEmail),
                                            StringHelper.encrypt(instructor.courseId));
    }

    public static String getExceedingResponsesErrorMessage() {
        return EXCEEDING_RESPONSES_ERROR_MESSAGE;
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
                isGroupedByTeam(), sortType, getInstructorFeedbackSessionResultsLink(), sections);
    }
}
