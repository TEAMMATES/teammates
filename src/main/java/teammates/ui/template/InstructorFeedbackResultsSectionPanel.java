package teammates.ui.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorFeedbackResultsSectionPanel {
    private String panelClass = "panel-success";
    private boolean isGroupedByTeam;
    
    private String sectionName;
    private String arrowClass;
    
    private String statisticsHeaderText;
    private String detailedResponsesHeaderText;
    
    // A mapping from team name to a list of participant panels. Each participant panel is for one member of the team
    private Map<String, List<InstructorResultsParticipantPanel>> participantPanels;
    private Map<String, List<InstructorResultsQuestionTable>> teamStatisticsTable;
    private Map<String, Boolean> isTeamWithResponses;

    public InstructorFeedbackResultsSectionPanel() {
        isTeamWithResponses = new HashMap<String, Boolean>();
        participantPanels = new HashMap<String, List<InstructorResultsParticipantPanel>>();
    }
    
    
    public String getPanelClass() {
        return panelClass;
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public boolean isGroupedByTeam() {
        return isGroupedByTeam;
    }

    public void setGroupedByTeam(boolean isGroupedByTeam) {
        this.isGroupedByTeam = isGroupedByTeam;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getArrowClass() {
        return arrowClass;
    }

    public void setArrowClass(String arrowClass) {
        this.arrowClass = arrowClass;
    }

    public Map<String, List<InstructorResultsParticipantPanel>> getParticipantPanels() {
        return participantPanels;
    }

    public void setParticipantPanels(
                                    Map<String, List<InstructorResultsParticipantPanel>> participantPanels) {
        this.participantPanels = participantPanels; 
    }

    public Map<String, List<InstructorResultsQuestionTable>> getTeamStatisticsTable() {
        return teamStatisticsTable;
    }

    public void setTeamStatisticsTable(Map<String, List<InstructorResultsQuestionTable>> teamStatisticsTable) {
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
    

}
