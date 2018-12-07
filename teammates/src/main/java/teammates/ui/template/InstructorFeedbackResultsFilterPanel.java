package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.SanitizationHelper;

public class InstructorFeedbackResultsFilterPanel {
    private boolean isStatsShown;
    private boolean isMissingResponsesShown;
    private String courseId;
    private String feedbackSessionName;
    private boolean isAllSectionsSelected;
    private String selectedSection;
    private boolean isGroupedByTeam;
    private String sortType;
    private String resultsLink;
    private List<String> sections;

    public InstructorFeedbackResultsFilterPanel(boolean isStatsShown,
                                    FeedbackSessionAttributes session, boolean isAllSectionsSelected,
                                    String selectedSection, boolean isGroupedByTeam, String sortType,
                                    String resultsLink, List<String> sections,
                                    boolean isMissingResponsesShown) {
        this.isStatsShown = isStatsShown;
        this.courseId = SanitizationHelper.sanitizeForHtml(session.getCourseId());
        this.feedbackSessionName = SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName());
        this.isAllSectionsSelected = isAllSectionsSelected;
        this.selectedSection = selectedSection;
        this.isGroupedByTeam = isGroupedByTeam;
        this.sortType = sortType;
        this.resultsLink = resultsLink;
        this.isMissingResponsesShown = isMissingResponsesShown;
        this.sections = sections;
    }

    public boolean isStatsShown() {
        return isStatsShown;
    }

    public boolean isMissingResponsesShown() {
        return isMissingResponsesShown;
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
