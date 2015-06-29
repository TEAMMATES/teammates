package teammates.ui.template;

import java.util.List;
import java.util.Map;

public class FeedbackResultsSectionPanel {
    private String panelClass;
    private boolean isGroupedByTeam;
    
    private boolean sectionName;
    private String arrowClass;
    
    private Map<String, List<? extends FeedbackResultsParticipantPanel>> participantPanels;
    
    // question tables without displaying responses
    private List<InstructorResultsQuestionTable> teamStatisticsTables;
    private List<String> statisticsHeaders;
    private List<String> detailedResponsesHeaders;
    
    
}
