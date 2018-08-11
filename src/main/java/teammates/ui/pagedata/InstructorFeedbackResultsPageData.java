package teammates.ui.pagedata;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.datatransfer.InstructorFeedbackResultsPageViewType;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.FeedbackSessionPublishButton;
import teammates.ui.template.InstructorFeedbackResultsFilterPanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel;
import teammates.ui.template.InstructorFeedbackResultsModerationButton;
import teammates.ui.template.InstructorFeedbackResultsNoResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsParticipantPanel;
import teammates.ui.template.InstructorFeedbackResultsQuestionTable;
import teammates.ui.template.InstructorFeedbackResultsRemindButton;
import teammates.ui.template.InstructorFeedbackResultsResponsePanel;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;
import teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody;
import teammates.ui.template.InstructorFeedbackResultsSectionPanel;
import teammates.ui.template.InstructorFeedbackResultsSessionPanel;

public class InstructorFeedbackResultsPageData extends PageData {
    private static final String MODERATE_RESPONSES_FOR_GIVER = "Moderate Responses";
    private static final String MODERATE_SINGLE_RESPONSE = "Moderate Response";

    private static final int RESPONDENTS_LIMIT_FOR_AUTOLOADING = 150;

    private static int sectionId;
    private static Pattern sectionIdPattern = Pattern.compile("^section-(\\d+)");

    // isLargeNumberOfRespondents is an attribute used for testing the ui, for ViewType.Question
    private boolean isLargeNumberOfRespondents;

    private FeedbackSessionResultsBundle bundle;
    private InstructorAttributes instructor;
    private List<String> sections;
    private String selectedSection;
    private String sortType;
    private String groupByTeam;
    private String showStats;
    private boolean isMissingResponsesShown;
    private int startIndex = -1;

    private FieldValidator validator = new FieldValidator();
    private String feedbackSessionName;

    private String displayableFsName;
    private String displayableCourseId;

    // used for html table ajax loading
    private String ajaxStatus;
    private String sessionResultsHtmlTableAsString;

    // for question view
    private List<InstructorFeedbackResultsQuestionTable> questionPanels;
    // for giver > question > recipient, recipient > question > giver,
    // giver > recipient > question, recipient > giver > question
    private Map<String, InstructorFeedbackResultsSectionPanel> sectionPanels;

    private Map<FeedbackQuestionAttributes, FeedbackQuestionDetails> questionToDetailsMap = new HashMap<>();
    private Map<String, String> profilePictureLinks = new HashMap<>();

    // TODO multiple page data classes inheriting this for each view type,
    // rather than an enum determining behavior in many methods
    private InstructorFeedbackResultsPageViewType viewType;

    public InstructorFeedbackResultsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    /**
     * Prepares question tables for viewing.
     *
     * <p>{@code bundle} should be set before this method
     */
    public void initForViewByQuestion(InstructorAttributes instructor,
                                      String selectedSection, String showStats,
                                      String groupByTeam, boolean isMissingResponsesShown) {
        this.viewType = InstructorFeedbackResultsPageViewType.QUESTION;
        this.sortType = InstructorFeedbackResultsPageViewType.QUESTION.toString();
        initCommonVariables(instructor, selectedSection, showStats, groupByTeam, isMissingResponsesShown);

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap =
                bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<>();

        // if there is more than one question, we omit generation of responseRows,
        // and load them by ajax question by question
        boolean isLoadingStructureOnly = questionToResponseMap.size() > 1;

        questionToResponseMap.forEach((question, responses) -> {
            InstructorFeedbackResultsQuestionTable questionPanel;
            if (isLoadingStructureOnly) {
                questionPanel = buildQuestionTableWithoutResponseRows(question, responses, "");
                questionPanel.setHasResponses(false);
            } else {
                questionPanel = buildQuestionTableAndResponseRows(question, responses, "");
            }

            questionPanels.add(questionPanel);
        });
    }

    private void initCommonVariables(InstructorAttributes instructor, String selectedSection,
                                    String showStats, String groupByTeam, boolean isMissingResponsesShown) {
        Assumption.assertNotNull(bundle);

        this.instructor = instructor;
        this.selectedSection = selectedSection;
        this.showStats = showStats;
        this.groupByTeam = groupByTeam;
        this.isMissingResponsesShown = isMissingResponsesShown;

        for (FeedbackQuestionAttributes question : bundle.questions.values()) {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            questionToDetailsMap.put(question, questionDetails);
        }

        this.sections = getSectionsFromBundle();

        displayableFsName = sanitizeForHtml(bundle.feedbackSession.getFeedbackSessionName());
        displayableCourseId = sanitizeForHtml(bundle.feedbackSession.getCourseId());
    }

    private List<String> getSectionsFromBundle() {
        List<String> sectionNames = new ArrayList<>();
        for (String section : bundle.sectionsInCourse()) {
            if (!section.equals(Const.DEFAULT_SECTION)) {
                sectionNames.add(section);
            }
        }

        sectionNames.sort(null);
        return sectionNames;
    }

    /**
     * Creates {@code InstructorFeedbackResultsSectionPanel}s for sectionPanels.
     *
     * <p>Iterates through the responses and creates panels and questions for them. Keeps track
     * of missing sections, teams and participants who do not have responses
     * and create panels for these missing sections, teams and participants.
     *
     * {@code bundle} should be set before this method
     * TODO: simplify the logic in this method
     */
    public void initForSectionPanelViews(InstructorAttributes instructor,
                                    String selectedSection, String showStats,
                                    String groupByTeam, InstructorFeedbackResultsPageViewType view,
                                    boolean isMissingResponsesShown) {
        Assumption.assertNotNull(bundle);
        this.viewType = view;
        this.sortType = view.toString();
        initCommonVariables(instructor, selectedSection, showStats, groupByTeam,
                            isMissingResponsesShown);

        // results page to be loaded by ajax
        if (isAllSectionsSelected()) {
            if (bundle.isComplete) {
                buildSectionPanelsForForAjaxLoading(getSections(), viewType);
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

        switch (viewType) {
        case RECIPIENT_GIVER_QUESTION:
            Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponsesForRgq =
                    bundle.getResponsesSortedByRecipientGiverQuestion(true);

            buildSectionPanelForViewByParticipantParticipantQuestion(selectedSection,
                    sortedResponsesForRgq, viewType.additionalInfoId());
            break;
        case RECIPIENT_QUESTION_GIVER:
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponsesForRqg =
                    bundle.getResponsesSortedByRecipientQuestionGiver(true);

            buildSectionPanelForViewByParticipantQuestionParticipant(selectedSection,
                    sortedResponsesForRqg, viewType.additionalInfoId());
            break;
        case GIVER_QUESTION_RECIPIENT:
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedResponsesForGqr =
                    bundle.getResponsesSortedByGiverQuestionRecipient(true);

            buildSectionPanelForViewByParticipantQuestionParticipant(selectedSection,
                    sortedResponsesForGqr, viewType.additionalInfoId());
            break;
        case GIVER_RECIPIENT_QUESTION:
            Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponsesForGrq =
                    bundle.getResponsesSortedByGiverRecipientQuestion(true);

            buildSectionPanelForViewByParticipantParticipantQuestion(selectedSection,
                    sortedResponsesForGrq, viewType.additionalInfoId());
            break;
        default:
            Assumption.fail();
            break;
        }

    }

    private void buildSectionPanelForViewByParticipantParticipantQuestion(
                                    String section,
                                    Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedResponses,
                                    String additionalInfoId) {
        sectionPanels = new LinkedHashMap<>();
        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();

        String prevTeam = "";

        String sectionPrefix = String.format("section-%s-", getSectionPosition(section));

        Set<String> teamsWithResponses = new HashSet<>();
        Set<String> teamMembersWithResponses = new HashSet<>();

        // Iterate through the primary participant
        int primaryParticipantIndex = this.getStartIndex();
        for (Entry<String, Map<String, List<FeedbackResponseAttributes>>> primaryToSecondaryParticipantToResponsesMap
                : sortedResponses.entrySet()) {
            primaryParticipantIndex += 1;
            String primaryParticipantIdentifier = primaryToSecondaryParticipantToResponsesMap.getKey();

            String currentTeam = getCurrentTeam(bundle, primaryParticipantIdentifier);

            boolean isStudent = bundle.isParticipantIdentifierStudent(primaryParticipantIdentifier);
            String participantSection = bundle.getSectionFromRoster(primaryParticipantIdentifier);

            if (isStudent && !participantSection.equals(section)) {
                continue;
            }

            boolean isDifferentTeam = !prevTeam.equals(currentTeam);

            if (isDifferentTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    // construct missing participant panels for the previous team
                    buildMissingParticipantPanelsForTeam(
                            sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear();
                }

                teamsWithResponses.add(currentTeam);
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }

            // Build participant panel for the current primary participant
            InstructorFeedbackResultsParticipantPanel recipientPanel =
                    buildGroupByParticipantPanel(primaryParticipantIdentifier, primaryToSecondaryParticipantToResponsesMap,
                                                 sectionPrefix + additionalInfoId, primaryParticipantIndex);

            sectionPanel.addParticipantPanel(currentTeam, recipientPanel);

            teamMembersWithResponses.add(primaryParticipantIdentifier);

            prevTeam = currentTeam;
        }

        // for the last section with responses
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);

        teamsWithResponses.add(prevTeam);
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, section, teamsWithResponses);

        finalizeBuildingSectionPanelWithoutTeamStats(sectionPanel, section);
        sectionPanels.put(section, sectionPanel);
    }

    /**
     * Constructs section panel for the {@code sortedResponses}.
     *
     * <p>Also builds team statistics tables for every team.
     */
    private void buildSectionPanelForViewByParticipantQuestionParticipant(String section,
                                Map<String, Map<FeedbackQuestionAttributes,
                                List<FeedbackResponseAttributes>>> sortedResponses, String additionalInfoId) {
        sectionPanels = new LinkedHashMap<>();

        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam =
                viewType.isPrimaryGroupingOfGiverType() ? bundle.getQuestionResponseMapByGiverTeam()
                                                        : bundle.getQuestionResponseMapByRecipientTeam();

        String prevTeam = "";

        String sectionPrefix = String.format("section-%s-", getSectionPosition(section));

        Set<String> teamsWithResponses = new LinkedHashSet<>();
        Set<String> teamMembersWithResponses = new HashSet<>();

        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();

        // Iterate through the primary participant
        int primaryParticipantIndex = this.getStartIndex();
        for (Entry<String,
                     Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                     primaryToSecondaryParticipantToResponsesMap
                : sortedResponses.entrySet()) {
            primaryParticipantIndex += 1;
            String primaryParticipantIdentifier = primaryToSecondaryParticipantToResponsesMap.getKey();

            boolean isStudent = bundle.isParticipantIdentifierStudent(primaryParticipantIdentifier);
            String participantSection = bundle.getSectionFromRoster(primaryParticipantIdentifier);

            if (isStudent && !participantSection.equals(section)) {
                continue;
            }

            String currentTeam = getCurrentTeam(bundle, primaryParticipantIdentifier);

            boolean isDifferentTeam = !prevTeam.equals(currentTeam);

            if (isDifferentTeam) {
                boolean isFirstTeam = prevTeam.isEmpty();
                if (!isFirstTeam) {
                    // construct missing participant panels for the previous team
                    buildMissingParticipantPanelsForTeam(
                            sectionPanel, prevTeam, teamMembersWithResponses);
                    teamMembersWithResponses.clear();
                }

                teamsWithResponses.add(currentTeam);
                sectionPanel.getIsTeamWithResponses().put(currentTeam, true);
            }

            // Build participant panel for the current participant
            InstructorFeedbackResultsParticipantPanel primaryParticipantPanel =
                    buildGroupByQuestionPanel(primaryParticipantIdentifier,
                                              primaryToSecondaryParticipantToResponsesMap,
                                              sectionPrefix + additionalInfoId, primaryParticipantIndex);

            sectionPanel.addParticipantPanel(currentTeam, primaryParticipantPanel);

            teamMembersWithResponses.add(primaryParticipantIdentifier);

            prevTeam = currentTeam;
        }

        // for the last section with responses
        buildMissingParticipantPanelsForTeam(sectionPanel, prevTeam, teamMembersWithResponses);

        teamsWithResponses.add(prevTeam);
        buildMissingTeamAndParticipantPanelsForSection(sectionPanel, section, teamsWithResponses);

        finalizeBuildingSectionPanel(sectionPanel, section, responsesGroupedByTeam, teamsWithResponses);
        sectionPanels.put(section, sectionPanel);
    }

    private InstructorFeedbackResultsGroupByParticipantPanel buildGroupByParticipantPanel(
            String primaryParticipantIdentifier,
            Entry<String, Map<String, List<FeedbackResponseAttributes>>> recipientToGiverToResponsesMap,
            String additionalInfoId, int primaryParticipantIndex) {
        // first build secondary participant panels for the primary participant panel
        Map<String, List<FeedbackResponseAttributes>> giverToResponsesMap =
                recipientToGiverToResponsesMap.getValue();
        List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels =
                buildSecondaryParticipantPanels(
                        additionalInfoId, primaryParticipantIndex, giverToResponsesMap);

        // construct the primary participant panel
        String primaryParticipantNameWithTeamName =
                bundle.appendTeamNameToName(bundle.getNameForEmail(primaryParticipantIdentifier),
                                            bundle.getTeamNameForEmail(primaryParticipantIdentifier));

        InstructorFeedbackResultsModerationButton moderationButton;
        if (viewType.isPrimaryGroupingOfGiverType()) {
            moderationButton =
                    buildModerationButtonForGiver(null, primaryParticipantIdentifier,
                                                  "btn btn-primary btn-xs",
                                                  MODERATE_RESPONSES_FOR_GIVER);
        } else {
            moderationButton = null;
        }
        return buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                        primaryParticipantIdentifier, primaryParticipantNameWithTeamName,
                        secondaryParticipantPanels, moderationButton);
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

            boolean isEmail = validator.getInvalidityInfoForEmail(secondaryParticipantIdentifier).isEmpty();
            String secondaryParticipantDisplayableName;
            if (isEmail && !bundle.getTeamNameForEmail(secondaryParticipantIdentifier).isEmpty()) {
                secondaryParticipantDisplayableName =
                        bundle.getNameForEmail(secondaryParticipantIdentifier)
                        + " (" + bundle.getTeamNameForEmail(secondaryParticipantIdentifier) + ")";
            } else {
                secondaryParticipantDisplayableName = bundle.getNameForEmail(secondaryParticipantIdentifier);
            }
            List<InstructorFeedbackResultsResponsePanel> responsePanels =
                    buildResponsePanels(additionalInfoId, primaryParticipantIndex,
                                        secondaryParticipantIndex, secondaryParticipantResponses.getValue());

            InstructorFeedbackResultsSecondaryParticipantPanelBody secondaryParticipantPanel =
                    new InstructorFeedbackResultsSecondaryParticipantPanelBody(
                            secondaryParticipantIdentifier, secondaryParticipantDisplayableName,
                            responsePanels);

            secondaryParticipantPanel
                .setProfilePictureLink(getProfilePictureIfEmailValid(secondaryParticipantIdentifier));

            if (!viewType.isPrimaryGroupingOfGiverType()) {
                String sectionName = bundle.getSectionFromRoster(secondaryParticipantIdentifier);
                boolean isAllowedToModerate = isAllowedToModerate(instructor, sectionName, feedbackSessionName);

                secondaryParticipantPanel.setModerationButton(
                        isAllowedToModerate
                        ? buildModerationButtonForGiver(null, secondaryParticipantIdentifier, "btn btn-default btn-xs",
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

            int giverIndex = viewType.isPrimaryGroupingOfGiverType() ? primaryParticipantIndex
                                                                     : secondaryRecipientIndex;
            int recipientIndex = viewType.isPrimaryGroupingOfGiverType() ? secondaryRecipientIndex
                                                                         : primaryParticipantIndex;

            String additionalInfoText =
                    questionToDetailsMap.get(question).getQuestionAdditionalInfoHtml(
                            question.getQuestionNumber(), String.format(
                                    additionalInfoId, giverIndex, recipientIndex));
            String displayableResponse = bundle.getResponseAnswerHtml(response, question);

            String giverName = bundle.getNameForEmail(response.giver);
            String recipientName = bundle.getNameForEmail(response.recipient);

            String giverTeam = bundle.getTeamNameForEmail(response.giver);
            String recipientTeam = bundle.getTeamNameForEmail(response.recipient);

            giverName = bundle.appendTeamNameToName(giverName, giverTeam);
            recipientName = bundle.appendTeamNameToName(recipientName, recipientTeam);

            List<FeedbackResponseCommentRow> instructorComments =
                    buildInstructorComments(giverName, recipientName, question, response);
            FeedbackResponseCommentRow feedbackParticipantComment =
                    buildFeedbackParticipantResponseCommentRow(question, bundle.responseComments,
                            response.getId(), false);

            boolean isAllowedToSubmitSessionsInBothSection =
                    instructor.isAllowedForPrivilege(response.giverSection,
                                                     response.feedbackSessionName,
                                                     Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                    && instructor.isAllowedForPrivilege(response.recipientSection,
                                                        response.feedbackSessionName,
                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);

            Matcher matcher = sectionIdPattern.matcher(additionalInfoId);
            if (matcher.find()) {
                sectionId = Integer.parseInt(matcher.group(1));
            }

            InstructorFeedbackResultsResponsePanel responsePanel =
                    new InstructorFeedbackResultsResponsePanel(
                            question, response, questionText, sectionId, additionalInfoText, null,
                            displayableResponse, instructorComments, isAllowedToSubmitSessionsInBothSection);
            responsePanel.setCommentsIndexes(recipientIndex, giverIndex, responseIndex + 1);
            if (question.getQuestionDetails().isFeedbackParticipantCommentsOnResponsesAllowed()) {
                responsePanel.setFeedbackParticipantComment(feedbackParticipantComment);
            }
            if (question.getQuestionDetails().isInstructorCommentsOnResponsesAllowed()) {
                FeedbackResponseCommentRow frcForAdding =
                        buildFeedbackResponseCommentFormForAdding(question, response.getId(), giverName,
                                recipientName, bundle.getTimeZone(), false);

                responsePanel.setFrcForAdding(frcForAdding);

            }
            responsePanels.add(responsePanel);
        }

        return responsePanels;
    }

    private InstructorFeedbackResultsGroupByQuestionPanel buildGroupByQuestionPanel(
            String participantIdentifier, Entry<String, Map<FeedbackQuestionAttributes,
            List<FeedbackResponseAttributes>>> recipientToGiverToResponsesMap,
            String additionalInfoId, int participantIndex) {
        List<InstructorFeedbackResultsQuestionTable> questionTables = new ArrayList<>();

        int questionIndex = 0;
        for (Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForParticipantForQuestion
                : recipientToGiverToResponsesMap.getValue().entrySet()) {
            if (responsesForParticipantForQuestion.getValue().isEmpty()) {
                // participant has no responses for the current question
                continue;
            }

            questionIndex += 1;

            FeedbackQuestionAttributes currentQuestion = responsesForParticipantForQuestion.getKey();
            List<FeedbackResponseAttributes> responsesForQuestion = responsesForParticipantForQuestion.getValue();

            InstructorFeedbackResultsQuestionTable questionTable =
                    buildQuestionTableAndResponseRows(currentQuestion, responsesForQuestion,
                                                      String.format(additionalInfoId, participantIndex, questionIndex),
                                                      participantIdentifier, true);
            questionTable.setBoldQuestionNumber(false);
            questionTables.add(questionTable);

        }

        InstructorFeedbackResultsQuestionTable.sortByQuestionNumber(questionTables);
        InstructorFeedbackResultsGroupByQuestionPanel participantPanel;
        // Construct InstructorFeedbackResultsGroupByQuestionPanel for the current giver
        if (viewType.isPrimaryGroupingOfGiverType() && (bundle.isParticipantIdentifierStudent(participantIdentifier)
                                                    || bundle.isParticipantIdentifierInstructor(participantIdentifier))) {
            // Moderation button on the participant panels are only shown is the panel is a giver panel,
            // and if the participant is a student
            InstructorFeedbackResultsModerationButton moderationButton =
                    buildModerationButtonForGiver(null, participantIdentifier, "btn btn-primary btn-xs",
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
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> emptyResponseMap =
                new LinkedHashMap<>();
        LinkedHashSet<String> emptyTeamList = new LinkedHashSet<>();
        finalizeBuildingSectionPanel(sectionPanel, sectionName, emptyResponseMap, emptyTeamList);
    }

    private void finalizeBuildingSectionPanel(
            InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
            Set<String> teamsWithResponses) {
        switch (viewType) {
        case GIVER_QUESTION_RECIPIENT:
        case RECIPIENT_QUESTION_GIVER:
            prepareHeadersForTeamPanelsInSectionPanel(sectionPanel);
            if (!responsesGroupedByTeam.isEmpty()) {
                buildTeamsStatisticsTableForSectionPanel(sectionPanel, responsesGroupedByTeam,
                                                         teamsWithResponses);
            }

            Map<String, Boolean> isTeamDisplayingStatistics = new HashMap<>();
            for (String team : teamsWithResponses) {
                // teamsWithResponses can include teams of anonymous student ("Anonymous student #'s Team")
                // and "-"
                isTeamDisplayingStatistics.put(team, isTeamVisible(team));
            }
            sectionPanel.setDisplayingTeamStatistics(isTeamDisplayingStatistics);
            sectionPanel.setSectionName(sectionName);
            sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION)
                                                ? Const.NO_SPECIFIC_SECTION
                                                : sectionName);
            break;
        case RECIPIENT_GIVER_QUESTION:
        case GIVER_RECIPIENT_QUESTION:

            sectionPanel.setSectionName(sectionName);
            sectionPanel.setSectionNameForDisplay(sectionName.equals(Const.DEFAULT_SECTION)
                                                ? Const.NO_SPECIFIC_SECTION
                                                : sectionName);
            break;
        default:
            Assumption.fail();
            break;
        }
    }

    private boolean isTeamVisible(String team) {
        return bundle.rosterTeamNameMembersTable.containsKey(team);
    }

    private void buildMissingTeamAndParticipantPanelsForSection(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String sectionName,
                                    Set<String> teamWithResponses) {

        // update the teams for the previous section
        Set<String> teamsInSection = bundle.getTeamsInSectionFromRoster(sectionName);
        Set<String> teamsWithoutResponses = new HashSet<>(teamsInSection);
        teamsWithoutResponses.removeAll(teamWithResponses);

        // create for every remaining team in the section, participantResultsPanels for every team member
        for (String teamWithoutResponses : teamsWithoutResponses) {
            List<String> teamMembers = new ArrayList<>(bundle.getTeamMembersFromRoster(teamWithoutResponses));
            teamMembers.sort(null);
            if (viewType.isPrimaryGroupingOfGiverType()) {
                addMissingParticipantsPanelsWithModerationButtonForTeam(
                                                sectionPanel, teamWithoutResponses, teamMembers);
            } else {
                addMissingParticipantsPanelsWithoutModerationButtonForTeam(
                                                sectionPanel, teamWithoutResponses, teamMembers);
            }
        }

    }

    private static String getCurrentTeam(FeedbackSessionResultsBundle bundle, String giverIdentifier) {
        String currentTeam;
        if (bundle.isParticipantIdentifierInstructor(giverIdentifier)) {
            currentTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            currentTeam = bundle.getTeamNameForEmail(giverIdentifier);
            if (currentTeam.isEmpty()) {
                currentTeam = bundle.getNameForEmail(giverIdentifier);
            }
        }

        return currentTeam;
    }

    private void buildMissingParticipantPanelsForTeam(
                                    InstructorFeedbackResultsSectionPanel sectionPanel, String teamName,
                                    Set<String> teamMembersWithResponses) {

        Set<String> teamMembersEmail = new HashSet<>();
        teamMembersEmail.addAll(bundle.getTeamMembersFromRoster(teamName));

        Set<String> teamMembersWithoutResponses = new HashSet<>(teamMembersEmail);
        teamMembersWithoutResponses.removeAll(teamMembersWithResponses);

        // Create missing participants panels for the previous team
        List<String> sortedTeamMembersWithoutResponses = new ArrayList<>(teamMembersWithoutResponses);
        sortedTeamMembersWithoutResponses.sort(null);

        if (viewType.isPrimaryGroupingOfGiverType()) {
            addMissingParticipantsPanelsWithModerationButtonForTeam(sectionPanel,
                                                        teamName, sortedTeamMembersWithoutResponses);
        } else {
            addMissingParticipantsPanelsWithoutModerationButtonForTeam(sectionPanel,
                                                        teamName, sortedTeamMembersWithoutResponses);
        }

    }

    private void buildSectionPanelsForForAjaxLoading(List<String> sections, InstructorFeedbackResultsPageViewType viewType) {
        sectionPanels = new LinkedHashMap<>();

        if (hasEntitiesInNoSpecificSection(viewType)) {
            InstructorFeedbackResultsSectionPanel sectionPanel =
                    new InstructorFeedbackResultsSectionPanel(Const.DEFAULT_SECTION, Const.NO_SPECIFIC_SECTION, true);

            sectionPanels.put(Const.DEFAULT_SECTION, sectionPanel);
        }

        for (String section : sections) {
            InstructorFeedbackResultsSectionPanel sectionPanel =
                    new InstructorFeedbackResultsSectionPanel(section, section, true);
            sectionPanels.put(section, sectionPanel);
        }
    }

    private int getSectionPosition(String name) {
        List<String> sections = getSectionsFromBundle();
        sections.add(0, Const.DEFAULT_SECTION);

        return sections.indexOf(name);
    }

    private void buildSectionPanelWithErrorMessage() {
        sectionPanels = new LinkedHashMap<>();

        InstructorFeedbackResultsSectionPanel sectionPanel = new InstructorFeedbackResultsSectionPanel();
        sectionPanel.setSectionName(selectedSection);
        sectionPanel.setSectionNameForDisplay(selectedSection);
        sectionPanel.setAbleToLoadResponses(false);

        sectionPanels.put(selectedSection, sectionPanel);

    }

    /**
     * Builds participant panels for the specified team, and add to sectionPanel.
     */
    private void addMissingParticipantsPanelsWithModerationButtonForTeam(
                                                             InstructorFeedbackResultsSectionPanel sectionPanel,
                                                             String teamName, List<String> teamMembers) {
        for (String teamMember : teamMembers) {
            InstructorFeedbackResultsModerationButton moderationButton =
                    buildModerationButtonForGiver(null, teamMember, "btn btn-default btn-xs",
                                                  MODERATE_RESPONSES_FOR_GIVER);
            InstructorFeedbackResultsParticipantPanel giverPanel;

            if (viewType.isSecondaryGroupingOfParticipantType()) {

                String teamMemberNameWithTeamNameAppended = bundle.getFullNameFromRoster(teamMember)
                                                + " (" + bundle.getTeamNameFromRoster(teamMember) + ")";
                giverPanel = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                 teamMember, teamMemberNameWithTeamNameAppended,
                                 new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>(),
                                 moderationButton);
            } else {
                giverPanel = new InstructorFeedbackResultsGroupByQuestionPanel(
                                teamMember, bundle.getFullNameFromRoster(teamMember),
                                new ArrayList<InstructorFeedbackResultsQuestionTable>(),
                                getStudentProfilePictureLink(teamMember, instructor.courseId),
                                viewType.isPrimaryGroupingOfGiverType(), moderationButton);
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

            if (viewType.isSecondaryGroupingOfParticipantType()) {
                String teamMemberWithTeamNameAppended = bundle.getFullNameFromRoster(teamMember)
                                                + " (" + bundle.getTeamNameFromRoster(teamMember) + ")";
                giverPanel = buildInstructorFeedbackResultsGroupBySecondaryParticipantPanel(
                                 teamMember, teamMemberWithTeamNameAppended,
                                 new ArrayList<InstructorFeedbackResultsSecondaryParticipantPanelBody>(),
                                 null);

            } else {
                giverPanel = new InstructorFeedbackResultsGroupByQuestionPanel(
                                 new ArrayList<InstructorFeedbackResultsQuestionTable>(),
                                 getStudentProfilePictureLink(teamMember, instructor.courseId),
                                 viewType.isPrimaryGroupingOfGiverType(), teamMember,
                                 bundle.getFullNameFromRoster(teamMember));
            }
            giverPanel.setHasResponses(false);

            sectionPanel.addParticipantPanel(teamName, giverPanel);
        }
    }

    /**
     * Constructs InstructorFeedbackResultsQuestionTable containing statistics for each team.
     * The statistics tables are added to the sectionPanel.
     */
    private void buildTeamsStatisticsTableForSectionPanel(
                     InstructorFeedbackResultsSectionPanel sectionPanel,
                     Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesGroupedByTeam,
                     Set<String> teamsInSection) {
        Map<String, List<InstructorFeedbackResultsQuestionTable>> teamToStatisticsTables = new HashMap<>();
        for (String team : teamsInSection) {
            // skip team if no responses,
            // or if the team is an anonymous student's team or an anonymous team, or is "-"
            if (!responsesGroupedByTeam.containsKey(team) || !isTeamVisible(team)) {
                continue;
            }

            List<InstructorFeedbackResultsQuestionTable> statisticsTablesForTeam = new ArrayList<>();

            for (FeedbackQuestionAttributes question : bundle.questions.values()) {
                if (!responsesGroupedByTeam.get(team).containsKey(question)) {
                    continue;
                }

                List<FeedbackResponseAttributes> responsesForTeamAndQuestion =
                        responsesGroupedByTeam.get(team).get(question);

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
            sectionPanel.setStatisticsHeaderText("Statistics for Received Responses");
            sectionPanel.setDetailedResponsesHeaderText("Detailed Responses");
            break;
        default:
            Assumption.fail("There should be no headers for the view type");
            break;
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
     * Builds question tables without response rows, but with stats.
     * @param responses  responses to compute statistics for
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
     * @param participantIdentifier  for viewTypes * > Question > *, constructs missing response rows
     *                               only for the given participant
     * @param isShowingResponseRows  if false, hides the response rows
     */
    private InstructorFeedbackResultsQuestionTable buildQuestionTableAndResponseRows(
                                                              FeedbackQuestionAttributes question,
                                                              List<FeedbackResponseAttributes> responses,
                                                              String additionalInfoId,
                                                              String participantIdentifier, boolean isShowingResponseRows) {

        List<ElementTag> columnTags = new ArrayList<>();
        Map<String, Boolean> isSortable = new HashMap<>();
        boolean isCollapsible = true;
        List<InstructorFeedbackResultsResponseRow> responseRows = null;

        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);
        boolean isFeedbackParticipantCommentsOnResponsesAllowed =
                questionDetails.isFeedbackParticipantCommentsOnResponsesAllowed();

        if (isShowingResponseRows) {
            switch (viewType) {
            case QUESTION:
                buildTableColumnHeaderForQuestionView(columnTags, isSortable,
                        isFeedbackParticipantCommentsOnResponsesAllowed);
                responseRows = buildResponseRowsForQuestion(question, responses);
                break;
            case GIVER_QUESTION_RECIPIENT:
                buildTableColumnHeaderForGiverQuestionRecipientView(columnTags, isSortable,
                        isFeedbackParticipantCommentsOnResponsesAllowed);
                responseRows = buildResponseRowsForQuestionForSingleGiver(question, responses, participantIdentifier);
                isCollapsible = false;
                break;
            case RECIPIENT_QUESTION_GIVER:
                buildTableColumnHeaderForRecipientQuestionGiverView(columnTags, isSortable,
                        isFeedbackParticipantCommentsOnResponsesAllowed);
                responseRows = buildResponseRowsForQuestionForSingleRecipient(question, responses, participantIdentifier);
                isCollapsible = false;
                break;
            default:
                Assumption.fail("View type should not involve question tables");
                break;
            }

            // If question specific sorting is not needed, responses are sorted
            // by default order (first by team name, then by display name)
            if (questionDetails.isQuestionSpecificSortingRequired()) {
                responseRows.sort(questionDetails.getResponseRowsSortOrder());
            } else {
                responseRows = InstructorFeedbackResultsResponseRow.sortListWithDefaultOrder(responseRows);
            }

        }

        String studentEmail = student == null ? null : student.email;
        String statisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, studentEmail,
                                                                                 bundle, viewType.toString());

        String questionText = questionDetails.getQuestionText();
        String additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, additionalInfoId);

        InstructorFeedbackResultsQuestionTable questionTable = new InstructorFeedbackResultsQuestionTable(
                                                                        !responses.isEmpty(), statisticsTable,
                                                                        responseRows, question,
                                                                        questionText, additionalInfoText,
                                                                        columnTags, isSortable);
        if (viewType == InstructorFeedbackResultsPageViewType.QUESTION) {
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

    private void buildTableColumnHeaderForQuestionView(List<ElementTag> columnTags, Map<String, Boolean> isSortable,
            boolean isFeedbackParticipantCommentsOnResponseAllowed) {
        ElementTag giverTeamElement =
                new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-none toggle-sort",
                        "style", "width: 10%; min-width: 67px;");
        ElementTag giverElement =
                new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none toggle-sort",
                        "style", "width: 10%; min-width: 65px;");
        ElementTag recipientTeamElement =
                new ElementTag("Team", "id", "button_sortToTeam", "class", "button-sort-ascending toggle-sort",
                        "style", "width: 10%; min-width: 67px;");
        ElementTag recipientElement =
                new ElementTag("Recipient", "id", "button_sortToName", "class", "button-sort-none toggle-sort",
                        "style", "width: 10%; min-width: 90px;");
        ElementTag responseElement =
                    new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none toggle-sort",
                            "style", "width: 45%; min-width: 95px;");

        ElementTag actionElement = new ElementTag("Actions", "class", "action-header",
                                                  "style", "width: 15%; min-width: 75px;");

        columnTags.add(giverTeamElement);
        columnTags.add(giverElement);
        columnTags.add(recipientTeamElement);
        columnTags.add(recipientElement);
        columnTags.add(responseElement);

        if (isFeedbackParticipantCommentsOnResponseAllowed) {
            ElementTag commentElement = new ElementTag("Comments", "style", "width: 20%; min-width: 95px;");
            responseElement.setAttribute("width", "25%");
            columnTags.add(commentElement);
            isSortable.put(commentElement.getContent(), false);
        }
        columnTags.add(actionElement);

        isSortable.put(giverElement.getContent(), true);
        isSortable.put(giverTeamElement.getContent(), true);
        isSortable.put(recipientElement.getContent(), true);
        isSortable.put(responseElement.getContent(), true);
        isSortable.put(actionElement.getContent(), false);
    }

    private void buildTableColumnHeaderForGiverQuestionRecipientView(List<ElementTag> columnTags,
            Map<String, Boolean> isSortable, boolean isFeedbackParticipantCommentsOnResponsesAllowed) {
        ElementTag photoElement = new ElementTag("Photo");
        ElementTag recipientTeamElement =
                new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending toggle-sort",
                        "style", "width: 15%; min-width: 67px;");
        ElementTag recipientElement =
                new ElementTag("Recipient", "id", "button_sortTo", "class", "button-sort-none toggle-sort",
                        "style", "width: 15%; min-width: 90px;");
        ElementTag responseElement =
                new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none toggle-sort",
                        "style", "min-width: 95px;");
        ElementTag actionElement = new ElementTag("Actions", "class", "action-header",
                "style", "width: 15%; min-width: 75px;");

        columnTags.add(photoElement);
        columnTags.add(recipientTeamElement);
        columnTags.add(recipientElement);
        columnTags.add(responseElement);
        if (isFeedbackParticipantCommentsOnResponsesAllowed) {
            ElementTag columnElement = new ElementTag("Comments", "style", "width: 20%; min-width: 95px;");
            columnTags.add(columnElement);
            isSortable.put(columnElement.getContent(), false);
        }
        columnTags.add(actionElement);

        isSortable.put(photoElement.getContent(), false);
        isSortable.put(recipientTeamElement.getContent(), true);
        isSortable.put(recipientElement.getContent(), true);
        isSortable.put(responseElement.getContent(), true);
        isSortable.put(actionElement.getContent(), false);
    }

    private void buildTableColumnHeaderForRecipientQuestionGiverView(List<ElementTag> columnTags,
            Map<String, Boolean> isSortable, boolean isFeedbackParticipantCommentsOnResponsesAllowed) {
        ElementTag photoElement = new ElementTag("Photo");
        ElementTag giverTeamElement =
                new ElementTag("Team", "id", "button_sortFromTeam", "class", "button-sort-ascending toggle-sort",
                        "style", "width: 15%; min-width: 67px;");
        ElementTag giverElement =
                new ElementTag("Giver", "id", "button_sortFromName", "class", "button-sort-none toggle-sort",
                        "style", "width: 15%; min-width: 65px;");
        ElementTag responseElement =
                new ElementTag("Feedback", "id", "button_sortFeedback", "class", "button-sort-none toggle-sort",
                        "style", "min-width: 95px;");
        ElementTag actionElement =
                new ElementTag("Actions", "class", "action-header", "style", "width: 15%; min-width: 75px;");

        columnTags.add(photoElement);
        columnTags.add(giverTeamElement);
        columnTags.add(giverElement);
        columnTags.add(responseElement);
        if (isFeedbackParticipantCommentsOnResponsesAllowed) {
            ElementTag columnElement = new ElementTag("Comments", "style", "width: 20%; min-width: 95px;");
            columnTags.add(columnElement);
            isSortable.put(columnElement.getContent(), false);
        }
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
     * @param responses  existing responses for the question
     */
    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestion(
            FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<>();

        List<String> possibleGiversWithoutResponses = bundle.getPossibleGivers(question);
        List<String> possibleReceiversWithoutResponsesForGiver = new ArrayList<>();

        String prevGiver = "";
        Map<String, Integer> responseGiverRecipientIndex = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            if (!bundle.isGiverVisible(response) || !bundle.isRecipientVisible(response)) {
                possibleGiversWithoutResponses.clear();
                possibleReceiversWithoutResponsesForGiver.clear();
            }

            // keep track of possible givers who did not give a response
            removeParticipantIdentifierFromList(possibleGiversWithoutResponses, response.giver);

            boolean isNewGiver = !prevGiver.equals(response.giver);
            if (isNewGiver) {
                if (isMissingResponsesShown) {
                    responseRows.addAll(
                            buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                        question, possibleReceiversWithoutResponsesForGiver, prevGiver,
                                        bundle.getNameForEmail(prevGiver),
                                        bundle.getTeamNameForEmail(prevGiver)));
                }
                String giverIdentifier = response.giver;

                possibleReceiversWithoutResponsesForGiver = bundle.getPossibleRecipients(question, giverIdentifier);
            }

            // keep track of possible recipients without a response from the current giver
            removeParticipantIdentifierFromList(possibleReceiversWithoutResponsesForGiver, response.recipient);
            prevGiver = response.giver;

            InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForExistingResponse(
                                                                                question, response);
            boolean isFeedbackParticipantCommentsOnResponsesAllowed =
                    question.getQuestionDetails().isFeedbackParticipantCommentsOnResponsesAllowed();
            InstructorFeedbackResultsResponseRow responseRow =
                    new InstructorFeedbackResultsResponseRow(bundle.getGiverNameForResponse(response),
                            bundle.getTeamNameForEmail(response.giver), bundle.getRecipientNameForResponse(response),
                            bundle.getTeamNameForEmail(response.recipient), bundle.getResponseAnswerHtml(response, question),
                            moderationButton, isFeedbackParticipantCommentsOnResponsesAllowed);
            if (isFeedbackParticipantCommentsOnResponsesAllowed) {
                String comment = getFeedbackParticipantCommentText(response);
                responseRow.setFeedbackParticipantComment(comment);
            }
            configureResponseRow(prevGiver, response.recipient, responseRow);
            if (question.getQuestionDetails().isInstructorCommentsOnResponsesAllowed()) {
                responseGiverRecipientIndex.putIfAbsent(response.giver, responseGiverRecipientIndex.size() + 1);
                responseGiverRecipientIndex.putIfAbsent(response.recipient, responseGiverRecipientIndex.size() + 1);
                addCommentsToResponseRow(question, response, responseRow, responseGiverRecipientIndex);
            } else {
                responseRow.setInstructorCommentsOnResponsesAllowed(false);
            }

            responseRows.add(responseRow);
        }

        if (!responses.isEmpty()) {
            responseRows.addAll(getRemainingMissingResponseRows(question, possibleGiversWithoutResponses,
                                                                possibleReceiversWithoutResponsesForGiver,
                                                                prevGiver));
        }

        return responseRows;
    }

    private List<InstructorFeedbackResultsResponseRow>
            buildResponseRowsForQuestionForSingleGiver(FeedbackQuestionAttributes question,
                                                       List<FeedbackResponseAttributes> responses,
                                                       String giverIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, giverIdentifier, true);
    }

    private List<InstructorFeedbackResultsResponseRow>
            buildResponseRowsForQuestionForSingleRecipient(FeedbackQuestionAttributes question,
                                                           List<FeedbackResponseAttributes> responses,
                                                           String recipientIdentifier) {
        return buildResponseRowsForQuestionForSingleParticipant(question, responses, recipientIdentifier, false);
    }

    private List<InstructorFeedbackResultsResponseRow> buildResponseRowsForQuestionForSingleParticipant(
                                    FeedbackQuestionAttributes question,
                                    List<FeedbackResponseAttributes> responses,
                                    String participantIdentifier, boolean isFirstGroupedByGiver) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<>();

        List<String> possibleParticipantsWithoutResponses = isFirstGroupedByGiver
                                                          ? bundle.getPossibleRecipients(question, participantIdentifier)
                                                          : bundle.getPossibleGivers(question, participantIdentifier);

        for (FeedbackResponseAttributes response : responses) {
            if (!bundle.isGiverVisible(response) || !bundle.isRecipientVisible(response)) {
                possibleParticipantsWithoutResponses.clear();
            }

            // keep track of possible participant who did not give/receive a response to/from the participantIdentifier
            String participantWithResponse = isFirstGroupedByGiver ? response.recipient : response.giver;
            removeParticipantIdentifierFromList(possibleParticipantsWithoutResponses,
                                                participantWithResponse);

            InstructorFeedbackResultsModerationButton moderationButton =
                    buildModerationButtonForExistingResponse(question, response);

            boolean isFeedbackParticipantCommentsOnResponsesAllowed =
                    question.getQuestionDetails().isFeedbackParticipantCommentsOnResponsesAllowed();

            InstructorFeedbackResultsResponseRow responseRow =
                    new InstructorFeedbackResultsResponseRow(bundle.getGiverNameForResponse(response),
                            bundle.getTeamNameForEmail(response.giver), bundle.getRecipientNameForResponse(response),
                            bundle.getTeamNameForEmail(response.recipient), bundle.getResponseAnswerHtml(response, question),
                            moderationButton, isFeedbackParticipantCommentsOnResponsesAllowed);

            if (isFeedbackParticipantCommentsOnResponsesAllowed) {
                responseRow.setFeedbackParticipantComment(getFeedbackParticipantCommentText(response));
            }
            configureResponseRow(response.giver, response.recipient, responseRow);

            if (question.getQuestionDetails().isInstructorCommentsOnResponsesAllowed()) {
                List<String> responseGiverRecipientList = new ArrayList<>(bundle.emailNameTable.keySet());
                Collections.sort(responseGiverRecipientList);
                Map<String, Integer> responseGiverRecipientIndex = new HashMap<>();
                for (int i = 0; i < responseGiverRecipientList.size(); i++) {
                    responseGiverRecipientIndex.put(responseGiverRecipientList.get(i), i);
                }
                addCommentsToResponseRow(question, response, responseRow, responseGiverRecipientIndex);
            } else {
                responseRow.setInstructorCommentsOnResponsesAllowed(false);
            }

            responseRows.add(responseRow);
        }

        if (isMissingResponsesShown) {
            if (isFirstGroupedByGiver) {
                responseRows.addAll(
                        buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                                question, possibleParticipantsWithoutResponses,
                                                participantIdentifier,
                                                bundle.getNameForEmail(participantIdentifier),
                                                bundle.getTeamNameForEmail(participantIdentifier)));
            } else {
                responseRows.addAll(
                        buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                                question, possibleParticipantsWithoutResponses,
                                                participantIdentifier,
                                                bundle.getNameForEmail(participantIdentifier),
                                                bundle.getTeamNameForEmail(participantIdentifier)));
            }
        }

        return responseRows;
    }

    /**
     * Adds comments associated with response and response giver/recipient index to response row.
     *
     * @param question question associated with response
     * @param response response whose comments are to be added in responseRow
     * @param responseRow response row of response
     * @param responseGiverRecipientIndex map in which key is both response recipient and response giver
     *                                  and value is unique index.
     */
    private void addCommentsToResponseRow(FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
            InstructorFeedbackResultsResponseRow responseRow, Map<String, Integer> responseGiverRecipientIndex) {
        responseRow.setInstructorCommentsOnResponsesAllowed(true);
        String giverNameAndTeam = bundle.getNameForEmail(response.giver);
        String recipientNameAndTeam = bundle.getNameForEmail(response.recipient);

        String giverTeam = bundle.getTeamNameForEmail(response.giver);
        String recipientTeam = bundle.getTeamNameForEmail(response.recipient);

        giverNameAndTeam = bundle.appendTeamNameToName(giverNameAndTeam, giverTeam);
        recipientNameAndTeam = bundle.appendTeamNameToName(recipientNameAndTeam, recipientTeam);

        List<FeedbackResponseCommentRow> instructorComments =
                buildInstructorComments(giverNameAndTeam, recipientNameAndTeam, question, response);
        if (!instructorComments.isEmpty()) {
            responseRow.setInstructorComments(instructorComments);
        }
        FeedbackResponseCommentRow addCommentForm = buildFeedbackResponseCommentFormForAdding(question, response.getId(),
                giverNameAndTeam, recipientNameAndTeam, bundle.getTimeZone(), false);
        responseRow.setAddCommentButton(addCommentForm);

        int giverIndex = 0;
        if (responseGiverRecipientIndex.containsKey(response.giver)) {
            giverIndex = responseGiverRecipientIndex.get(response.giver);
        } else if (question.giverType == FeedbackParticipantType.TEAMS
                && responseGiverRecipientIndex.containsKey(response.giver + Const.TEAM_OF_EMAIL_OWNER)) {
            // Legacy data has "student's Team" as response giver when response giver is a team
            giverIndex = responseGiverRecipientIndex.get(response.giver + Const.TEAM_OF_EMAIL_OWNER);
        } else {
            Assumption.fail("Response giver is not in the bundle.");
        }
        int recipientIndex = 0;
        if (responseGiverRecipientIndex.containsKey(response.recipient)) {
            recipientIndex = responseGiverRecipientIndex.get(response.recipient);
        } else {
            Assumption.fail("Response recipient is not in the bundle.");
        }
        responseRow.setResponseRecipientIndex(recipientIndex);
        responseRow.setResponseGiverIndex(giverIndex);
    }

    private void configureResponseRow(String giver, String recipient,
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
            responseRow.setActionsDisplayed(true);
            break;
        case RECIPIENT_QUESTION_GIVER:
            responseRow.setRecipientDisplayed(false);
            responseRow.setGiverProfilePictureAColumn(true);

            responseRow.setGiverProfilePictureLink(getProfilePictureIfEmailValid(giver));
            responseRow.setActionsDisplayed(true);
            break;
        default:
            Assumption.fail();
            break;
        }
    }

    // TODO consider using Url in future
    private String getProfilePictureIfEmailValid(String email) {
        // TODO the check for determining whether to show a profile picture
        // can be improved to use isStudent
        boolean isEmailValid = validator.getInvalidityInfoForEmail(email).isEmpty();
        return isEmailValid ? getStudentProfilePictureLink(email, instructor.courseId)
                            : null;
    }

    /**
     * Construct missing response rows between the giver identified by {@code giverIdentifier} and
     * {@code possibleReceivers}.
     */
    private List<InstructorFeedbackResultsResponseRow> buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                                                    FeedbackQuestionAttributes question,
                                                                    List<String> possibleReceivers,
                                                                    String giverIdentifier,
                                                                    String giverName, String giverTeam) {
        List<InstructorFeedbackResultsResponseRow> missingResponses = new ArrayList<>();
        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);

        for (String possibleRecipient : possibleReceivers) {
            if (questionDetails.shouldShowNoResponseText(question)) {
                String textToDisplay = questionDetails.getNoResponseTextInHtml(
                                               giverIdentifier, possibleRecipient, bundle, question);
                String possibleRecipientName = bundle.getFullNameFromRoster(possibleRecipient);
                String possibleRecipientTeam = bundle.getTeamNameFromRoster(possibleRecipient);

                InstructorFeedbackResultsModerationButton moderationButton =
                        buildModerationButtonForGiver(question, giverIdentifier, "btn btn-default btn-xs",
                                                      MODERATE_SINGLE_RESPONSE);
                boolean isFeedbackParticipantCommentsOnResponsesAllowed =
                        question.getQuestionDetails().isFeedbackParticipantCommentsOnResponsesAllowed();
                InstructorFeedbackResultsResponseRow missingResponse =
                        new InstructorFeedbackResultsResponseRow(
                                giverName, giverTeam, possibleRecipientName, possibleRecipientTeam,
                                textToDisplay, moderationButton, true, isFeedbackParticipantCommentsOnResponsesAllowed);

                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRow(giverIdentifier, possibleRecipient, missingResponse);
                missingResponses.add(missingResponse);
            }
        }

        return missingResponses;
    }

    /**
     * Construct missing response rows between the recipient identified by {@code recipientIdentifier} and
     * {@code possibleGivers}.
     */
    private List<InstructorFeedbackResultsResponseRow> buildMissingResponseRowsBetweenRecipientAndPossibleGivers(
                                    FeedbackQuestionAttributes question,
                                    List<String> possibleGivers, String recipientIdentifier,
                                    String recipientName, String recipientTeam) {
        List<InstructorFeedbackResultsResponseRow> missingResponses = new ArrayList<>();
        FeedbackQuestionDetails questionDetails = questionToDetailsMap.get(question);

        for (String possibleGiver : possibleGivers) {
            String possibleGiverName = bundle.getFullNameFromRoster(possibleGiver);
            String possibleGiverTeam = bundle.getTeamNameFromRoster(possibleGiver);

            String textToDisplay = questionDetails.getNoResponseTextInHtml(recipientIdentifier, possibleGiver,
                                                                           bundle, question);
            boolean isFeedbackParticipantCommentsOnResponsesAllowed =
                    question.getQuestionDetails().isFeedbackParticipantCommentsOnResponsesAllowed();

            if (questionDetails.shouldShowNoResponseText(question)) {
                InstructorFeedbackResultsModerationButton moderationButton = buildModerationButtonForGiver(
                                                                                 question, possibleGiver,
                                                                                 "btn btn-default btn-xs",
                                                                                 MODERATE_SINGLE_RESPONSE);
                InstructorFeedbackResultsResponseRow missingResponse =
                        new InstructorFeedbackResultsResponseRow(possibleGiverName, possibleGiverTeam, recipientName,
                                recipientTeam, textToDisplay, moderationButton, true,
                                isFeedbackParticipantCommentsOnResponsesAllowed);
                missingResponse.setRowAttributes(new ElementTag("class", "pending_response_row"));
                configureResponseRow(possibleGiver, recipientIdentifier, missingResponse);

                missingResponses.add(missingResponse);
            }
        }

        return missingResponses;
    }

    /**
     * Given a participantIdentifier, remove it from participantIdentifierList.
     */
    private void removeParticipantIdentifierFromList(
                    List<String> participantIdentifierList, String participantIdentifier) {
        participantIdentifierList.remove(participantIdentifier);
    }

    private List<InstructorFeedbackResultsResponseRow> getRemainingMissingResponseRows(
            FeedbackQuestionAttributes question, List<String> remainingPossibleGivers,
            List<String> possibleRecipientsForGiver, String prevGiver) {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<>();

        if (possibleRecipientsForGiver != null && isMissingResponsesShown) {
            responseRows.addAll(buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                            question, possibleRecipientsForGiver,
                                            prevGiver, bundle.getNameForEmail(prevGiver),
                                            bundle.getTeamNameForEmail(prevGiver)));
        }

        removeParticipantIdentifierFromList(remainingPossibleGivers, prevGiver);

        for (String possibleGiverWithNoResponses : remainingPossibleGivers) {
            if (!isAllSectionsSelected()
                    && !bundle.getSectionFromRoster(possibleGiverWithNoResponses).equals(selectedSection)) {
                continue;
            }
            List<String> possibleRecipientsForRemainingGiver =
                                            bundle.getPossibleRecipients(question, possibleGiverWithNoResponses);
            if (isMissingResponsesShown) {
                responseRows.addAll(
                        buildMissingResponseRowsBetweenGiverAndPossibleRecipients(
                                    question,
                                    possibleRecipientsForRemainingGiver,
                                    possibleGiverWithNoResponses,
                                    bundle.getFullNameFromRoster(possibleGiverWithNoResponses),
                                    bundle.getTeamNameFromRoster(possibleGiverWithNoResponses)));
            }
        }

        return responseRows;
    }

    private InstructorFeedbackResultsModerationButton
            buildModerationButtonForExistingResponse(FeedbackQuestionAttributes question,
                                                     FeedbackResponseAttributes response) {
        boolean isGiverInstructor = question.giverType == FeedbackParticipantType.INSTRUCTORS;
        boolean isGiverStudentOrTeam = question.giverType == FeedbackParticipantType.STUDENTS
                                       || question.giverType == FeedbackParticipantType.TEAMS;

        if (isGiverStudentOrTeam || isGiverInstructor) {
            return buildModerationButtonForGiver(question, response.giver, "btn btn-default btn-xs",
                                                 MODERATE_SINGLE_RESPONSE);
        }
        return null;
    }

    /**
     * Gets moderation button for giver.
     *
     * @return
     * <ul>
     * <li>null if the participant is not visible</li>
     * <li>a disabled moderation button if the instructor does not have sufficient permissions</li>
     * <li>a working moderation button otherwise</li>
     * </ul>
     */
    private InstructorFeedbackResultsModerationButton buildModerationButtonForGiver(FeedbackQuestionAttributes question,
                                                                            String giverIdentifier, String className,
                                                                            String buttonText) {

        boolean isGiverInstructorOfCourse = bundle.roster.isInstructorOfCourse(giverIdentifier);
        boolean isGiverVisibleStudentOrTeam = isTeamVisible(giverIdentifier)
                                              || bundle.roster.isStudentInCourse(giverIdentifier);

        if (!isGiverVisibleStudentOrTeam && !isGiverInstructorOfCourse) {
            return null;
        }

        String sectionName = bundle.getSectionFromRoster(giverIdentifier);
        boolean isAllowedToModerate = isAllowedToModerate(instructor, sectionName, getFeedbackSessionName());
        boolean isDisabled = !isAllowedToModerate;
        String moderateFeedbackResponseLink = isGiverInstructorOfCourse
                                              ? Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE
                                              : Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE;
        moderateFeedbackResponseLink = addUserIdToUrl(moderateFeedbackResponseLink);

        return new InstructorFeedbackResultsModerationButton(isDisabled, className, giverIdentifier, getCourseId(),
                                                             getFeedbackSessionName(), question, buttonText,
                                                             moderateFeedbackResponseLink);
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

        boolean isEmailValid = validator.getInvalidityInfoForEmail(participantIdentifier).isEmpty();
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
        return new FeedbackSessionPublishButton(this,
                                                bundle.feedbackSession,
                                                Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                                                instructor,
                                                "btn-primary btn-block");
    }

    private List<FeedbackResponseCommentRow> buildInstructorComments(String giverName, String recipientName,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response) {
        List<FeedbackResponseCommentRow> instructorComments = new ArrayList<>();
        List<FeedbackResponseCommentAttributes> frcAttributesList = bundle.responseComments.get(response.getId());
        if (frcAttributesList != null) {
            for (FeedbackResponseCommentAttributes frcAttributes : frcAttributesList) {
                if (!frcAttributes.isCommentFromFeedbackParticipant) {
                    instructorComments.add(buildInstructorComment(giverName, recipientName, question,
                            response, frcAttributes));
                }
            }
        }
        return instructorComments;
    }

    private String getFeedbackParticipantCommentText(FeedbackResponseAttributes response) {
        List<FeedbackResponseCommentAttributes> frcAttributesList = bundle.responseComments.get(response.getId());
        if (frcAttributesList == null) {
            return "";
        }
        for (FeedbackResponseCommentAttributes frca : frcAttributesList) {
            if (frca.isCommentFromFeedbackParticipant) {
                return frca.getCommentAsHtmlString();
            }
        }
        return "";
    }

    private FeedbackResponseCommentRow buildInstructorComment(String giverName, String recipientName,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
            FeedbackResponseCommentAttributes frcAttributes) {
        boolean isInstructorGiver = instructor.email.equals(frcAttributes.commentGiver);
        boolean isInstructorWithPrivilegesToModify =
                instructor.isAllowedForPrivilege(
                        response.giverSection, response.feedbackSessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                && instructor.isAllowedForPrivilege(
                           response.recipientSection, response.feedbackSessionName,
                           Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isInstructorAllowedToEditAndDeleteComment = isInstructorGiver || isInstructorWithPrivilegesToModify;

        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);

        FeedbackResponseCommentRow frc =
                new FeedbackResponseCommentRow(frcAttributes, frcAttributes.commentGiver, giverName, recipientName,
                        getResponseCommentVisibilityString(frcAttributes, question),
                        getResponseCommentGiverNameVisibilityString(frcAttributes, question),
                        responseVisibilityMap, bundle.commentGiverEmailToNameTable, bundle.getTimeZone(), question);
        if (isInstructorAllowedToEditAndDeleteComment) {
            frc.enableEditDelete();
        }

        return frc;
    }

    private Map<String, InstructorFeedbackResultsModerationButton> buildModerateButtonsForNoResponsePanel() {
        Map<String, InstructorFeedbackResultsModerationButton> moderationButtons = new HashMap<>();
        for (String giverIdentifier : bundle.responseStatus.emailNameTable.keySet()) {
            boolean isStudent = bundle.isParticipantIdentifierStudent(giverIdentifier);

            if (!isStudent) {
                continue;
            }

            String sectionName = bundle.getSectionFromRoster(giverIdentifier);
            boolean isAllowedToModerate = isAllowedToModerate(instructor, sectionName, feedbackSessionName);
            String moderateFeedbackLink = addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE);

            InstructorFeedbackResultsModerationButton moderationButton =
                    new InstructorFeedbackResultsModerationButton(!isAllowedToModerate, "btn btn-default btn-xs",
                                                                  giverIdentifier,
                                                                  bundle.feedbackSession.getCourseId(),
                                                                  bundle.feedbackSession.getFeedbackSessionName(),
                                                                  null, "Submit Responses", moderateFeedbackLink);
            moderationButtons.put(giverIdentifier, moderationButton);

        }

        return moderationButtons;
    }

    private InstructorFeedbackResultsRemindButton buildRemindButtonForNoResponsePanel() {

        boolean isSessionClosed = bundle.getFeedbackSession().isClosed();
        String actionLink = addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE);
        actionLink = Url.addParamToUrl(actionLink, Const.ParamsNames.COURSE_ID, bundle.feedbackSession.getCourseId());
        actionLink = Url.addParamToUrl(actionLink, Const.ParamsNames.FEEDBACK_SESSION_NAME,
                bundle.feedbackSession.getFeedbackSessionName());
        actionLink = addSessionTokenToUrl(actionLink);

        return new InstructorFeedbackResultsRemindButton(isSessionClosed,
                "btn btn-default btn-xs btn-tm-actions remind-btn-no-response",
                bundle.feedbackSession.getCourseId(),
                bundle.feedbackSession.getFeedbackSessionName(),
                "Remind All", actionLink);
    }

    private String getRemindParticularStudentsLink() {
        String remindParticularStudentsLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS;

        String nextUrl = addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE);
        nextUrl = Url.addParamToUrl(nextUrl, Const.ParamsNames.COURSE_ID, bundle.feedbackSession.getCourseId());
        nextUrl = Url.addParamToUrl(nextUrl, Const.ParamsNames.FEEDBACK_SESSION_NAME,
                bundle.feedbackSession.getFeedbackSessionName());
        remindParticularStudentsLink = Url.addParamToUrl(remindParticularStudentsLink, Const.ParamsNames.NEXT_URL,
                nextUrl);
        remindParticularStudentsLink = addSessionTokenToUrl(remindParticularStudentsLink);
        return remindParticularStudentsLink;
    }

    @Override
    public String getStudentProfilePictureLink(String studentEmail, String courseId) {
        return profilePictureLinks.computeIfAbsent(studentEmail,
                key -> super.getStudentProfilePictureLink(StringHelper.encrypt(key),
                                                          StringHelper.encrypt(courseId)));
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
        return groupByTeam == null ? "null" : groupByTeam;
    }

    // TODO: swap groupByTeam to a normal boolean
    public boolean isGroupedByTeam() {
        return "on".equals(groupByTeam);
    }

    // TODO: swap showStats to a normal boolean
    private boolean isStatsShown() {
        return showStats != null;
    }

    public boolean isMissingResponsesShown() {
        return isMissingResponsesShown;
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

    public List<InstructorFeedbackResultsQuestionTable> getQuestionPanels() {
        return questionPanels;
    }

    public Map<String, InstructorFeedbackResultsSectionPanel> getSectionPanels() {
        return sectionPanels;
    }

    private String getInstructorFeedbackSessionEditLink() {
        return instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)
               ? getInstructorFeedbackEditLink(bundle.feedbackSession.getCourseId(),
                                                      bundle.feedbackSession.getFeedbackSessionName(), true)
               : null;
    }

    private String getInstructorFeedbackSessionResultsLink() {
        return getInstructorFeedbackResultsLink(bundle.feedbackSession.getCourseId(),
                                                bundle.feedbackSession.getFeedbackSessionName());
    }

    private boolean isAllowedToModerate(InstructorAttributes instructor, String sectionName, String feedbackSessionName) {
        return instructor.isAllowedForPrivilege(sectionName, feedbackSessionName,
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    public boolean isAllSectionsSelected() {
        return "All".equals(selectedSection);
    }

    // TODO: place below getter methods for template objects in some init method common to all views
    public InstructorFeedbackResultsSessionPanel getSessionPanel() {
        return new InstructorFeedbackResultsSessionPanel(
                bundle.feedbackSession, getInstructorFeedbackSessionEditLink(),
                getInstructorFeedbackSessionPublishAndUnpublishAction(), selectedSection,
                isMissingResponsesShown, isStatsShown());
    }

    public InstructorFeedbackResultsFilterPanel getFilterPanel() {
        return new InstructorFeedbackResultsFilterPanel(
                isStatsShown(), bundle.feedbackSession, isAllSectionsSelected(), selectedSection,
                isGroupedByTeam(), sortType, getInstructorFeedbackSessionResultsLink(),
                getSections(), isMissingResponsesShown);
    }

    public InstructorFeedbackResultsNoResponsePanel getNoResponsePanel() {
        return new InstructorFeedbackResultsNoResponsePanel(bundle.responseStatus,
                buildModerateButtonsForNoResponsePanel(),
                buildRemindButtonForNoResponsePanel(),
                getRemindParticularStudentsLink());
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
        boolean isQuestionViewType = viewType == InstructorFeedbackResultsPageViewType.QUESTION;
        return isQuestionViewType && isLargeNumberOfRespondents() && isAllSectionsSelected()
                || !bundle.isComplete;
    }

    public boolean isLargeNumberOfRespondents() {
        int numRespondents = bundle.feedbackSession.getRespondingInstructorList().size()
                           + bundle.feedbackSession.getRespondingStudentList().size();
        return isLargeNumberOfRespondents
            || numRespondents > RESPONDENTS_LIMIT_FOR_AUTOLOADING;
    }

    // Only used for testing the ui
    public void setLargeNumberOfRespondents(boolean needAjax) {
        this.isLargeNumberOfRespondents = needAjax;
    }

    /**
     * Checks if there are entities in No Specific Section.
     *
     * <ul>
     * <li>true if the course has teams in Default Section</li>
     * <li>true if view is GRQ or GQR and there is feedback from Instructors</li>
     * <li>true if view is RGQ or RQG and there is General Feedback or Feedback to Instructors</li>
     * <li>false otherwise</li>
     * </ul>
     */
    private boolean hasEntitiesInNoSpecificSection(InstructorFeedbackResultsPageViewType viewType) {
        boolean hasFeedbackFromInstructor = bundle.hasResponseFromInstructor();
        boolean hasFeedbackToInstructorOrGeneral = bundle.hasResponseToInstructorOrGeneral();
        boolean viewTypeIsRecipient = viewType == InstructorFeedbackResultsPageViewType.RECIPIENT_QUESTION_GIVER
                || viewType == InstructorFeedbackResultsPageViewType.RECIPIENT_GIVER_QUESTION;
        boolean viewTypeIsGiver = viewType == InstructorFeedbackResultsPageViewType.GIVER_QUESTION_RECIPIENT
                || viewType == InstructorFeedbackResultsPageViewType.GIVER_RECIPIENT_QUESTION;
        boolean hasTeamsInNoSection = bundle.getTeamsInSectionFromRoster(Const.DEFAULT_SECTION).size() > 0;

        return hasTeamsInNoSection
                || viewTypeIsGiver && hasFeedbackFromInstructor
                || viewTypeIsRecipient && hasFeedbackToInstructorOrGeneral;
    }

}
