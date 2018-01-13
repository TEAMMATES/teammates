package teammates.ui.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InstructorFeedbackResultsSectionPanel {
    private String panelClass;

    private boolean isDisplayingMissingParticipants;
    private boolean isLoadSectionResponsesByAjax;
    private boolean isAbleToLoadResponses;

    private String sectionName;
    private String sectionNameForDisplay;

    private String statisticsHeaderText;
    private String detailedResponsesHeaderText;

    // A mapping from team name to a list of participant panels. Each participant panel is for one member of the team
    private Map<String, List<InstructorFeedbackResultsParticipantPanel>> participantPanels;

    private Map<String, Boolean> isDisplayingTeamStatistics;
    private Map<String, List<InstructorFeedbackResultsQuestionTable>> teamStatisticsTable;
    private Map<String, Boolean> isTeamWithResponses;

    public InstructorFeedbackResultsSectionPanel() {
        panelClass = "panel-success";
        isDisplayingMissingParticipants = true;

        isAbleToLoadResponses = true;
        isTeamWithResponses = new HashMap<>();
        participantPanels = new LinkedHashMap<>();
    }

    public InstructorFeedbackResultsSectionPanel(String name, String nameForDisplay, boolean loadByAjax) {
        this();
        sectionName = name;
        sectionNameForDisplay = nameForDisplay;
        isLoadSectionResponsesByAjax = loadByAjax;
    }

    /**
     * Adds a participant panel. The participant panel will not be grouped with any team panel.
     * @see #addParticipantPanel(String, InstructorFeedbackResultsParticipantPanel)
     */
    public void addParticipantPanel(InstructorFeedbackResultsParticipantPanel participantPanel) {
        addParticipantPanel("", participantPanel);
    }

    /**
     * Adds a participant panel. The participant panel is grouped with the team panel with team {@code currentTeam}.
     * @see #addParticipantPanel(InstructorFeedbackResultsParticipantPanel)
     */
    public void addParticipantPanel(String currentTeam,
                                    InstructorFeedbackResultsParticipantPanel giverPanel) {
        List<InstructorFeedbackResultsParticipantPanel> teamsMembersPanels =
                participantPanels.getOrDefault(currentTeam, new ArrayList<>());

        teamsMembersPanels.add(giverPanel);
        participantPanels.put(currentTeam, teamsMembersPanels);
    }

    public String getPanelClass() {
        return panelClass;
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Map<String, List<InstructorFeedbackResultsParticipantPanel>> getParticipantPanels() {
        return participantPanels;
    }

    public void setParticipantPanels(
                                    Map<String, List<InstructorFeedbackResultsParticipantPanel>> participantPanels) {
        this.participantPanels = participantPanels;
    }

    public Map<String, List<InstructorFeedbackResultsQuestionTable>> getTeamStatisticsTable() {
        return teamStatisticsTable;
    }

    public void setTeamStatisticsTable(Map<String, List<InstructorFeedbackResultsQuestionTable>> teamStatisticsTable) {
        this.teamStatisticsTable = teamStatisticsTable;
    }

    public String getStatisticsHeaderText() {
        return statisticsHeaderText;
    }

    public void setStatisticsHeaderText(String statisticsHeaderText) {
        this.statisticsHeaderText = statisticsHeaderText;
    }

    public String getDetailedResponsesHeaderText() {
        return detailedResponsesHeaderText;
    }

    public void setDetailedResponsesHeaderText(String detailedResponsesHeaderText) {
        this.detailedResponsesHeaderText = detailedResponsesHeaderText;
    }

    public Map<String, Boolean> getIsTeamWithResponses() {
        return isTeamWithResponses;
    }

    public void setIsTeamWithResponses(Map<String, Boolean> isTeamWithResponses) {
        this.isTeamWithResponses = isTeamWithResponses;
    }

    public boolean isDisplayingMissingParticipants() {
        return isDisplayingMissingParticipants;
    }

    public void setDisplayingMissingParticipants(boolean isDisplayingMissingParticipants) {
        this.isDisplayingMissingParticipants = isDisplayingMissingParticipants;
    }

    public Map<String, Boolean> getIsDisplayingTeamStatistics() {
        return isDisplayingTeamStatistics;
    }

    public void setDisplayingTeamStatistics(Map<String, Boolean> isDisplayingTeamStatistics) {
        this.isDisplayingTeamStatistics = isDisplayingTeamStatistics;
    }

    public boolean isLoadSectionResponsesByAjax() {
        return isLoadSectionResponsesByAjax;
    }

    public void setLoadSectionResponsesByAjax(boolean isLoadSectionResponsesByAjax) {
        this.isLoadSectionResponsesByAjax = isLoadSectionResponsesByAjax;
    }

    public String getSectionNameForDisplay() {
        return sectionNameForDisplay;
    }

    public void setSectionNameForDisplay(String sectionNameForDisplay) {
        this.sectionNameForDisplay = sectionNameForDisplay;
    }

    public boolean isAbleToLoadResponses() {
        return isAbleToLoadResponses;
    }

    public void setAbleToLoadResponses(boolean isUnableToLoadResponses) {
        this.isAbleToLoadResponses = isUnableToLoadResponses;
    }

    public List<InstructorFeedbackResultsParticipantPanel> getParticipantPanelsInSortedOrder() {
        List<InstructorFeedbackResultsParticipantPanel> sortedPanels = new ArrayList<>();
        for (Collection<InstructorFeedbackResultsParticipantPanel> participantsPanels : participantPanels.values()) {
            sortedPanels.addAll(participantsPanels);
        }
        sortedPanels.sort(null);

        return sortedPanels;
    }

}
