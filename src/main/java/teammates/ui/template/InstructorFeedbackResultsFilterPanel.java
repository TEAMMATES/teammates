package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Sanitizer;

public class InstructorFeedbackResultsFilterPanel {
    private boolean isStatsShown;
    private boolean isCollapsed;
    private String courseId;
    private String feedbackSessionName;
    private boolean isAllSectionsSelected;
    private String selectedSection;
    private boolean isGroupedByTeam;
    private String sortType;
    private String resultsLink;
    private List<String> sections;
    
    public InstructorFeedbackResultsFilterPanel(boolean isStatsShown, boolean isCollapsed,
                                    FeedbackSessionAttributes session, boolean isAllSectionsSelected,
                                    String selectedSection, boolean isGroupedByTeam, String sortType,
                                    String resultsLink, List<String> sections) {
        this.isStatsShown = isStatsShown;
        this.isCollapsed = isCollapsed;
        this.courseId = Sanitizer.sanitizeForHtml(session.courseId);
        this.feedbackSessionName = Sanitizer.sanitizeForHtml(session.feedbackSessionName);
        this.isAllSectionsSelected = isAllSectionsSelected;
        this.selectedSection = selectedSection;
        this.isGroupedByTeam = isGroupedByTeam;
        this.sortType = sortType;
        this.resultsLink = resultsLink;
        
        List<String> sanitizedSections = new ArrayList<>();
        for (String s : sections) {
            sanitizedSections.add(Sanitizer.sanitizeForHtml(s));
        }
        this.sections = sanitizedSections;
    }

    public boolean isStatsShown() {
        return isStatsShown;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isAllSectionsSelected() {
        return isAllSectionsSelected;
    }
    
    public boolean isNoneSectionSelected() {
        return "None".equals(selectedSection);
    }

    public String getSelectedSection() {
        return selectedSection;
    }
    
    public boolean isGroupedByTeam() {
        return isGroupedByTeam;
    }

    public String getSortType() {
        return sortType;
    }
    
    public String getResultsLink() {
        return resultsLink;
    }

    public List<String> getSections() {
        return sections;
    }
    
}
