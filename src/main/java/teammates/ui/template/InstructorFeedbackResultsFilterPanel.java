package teammates.ui.template;

import java.util.ArrayList;
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
    private String selectedSectionDetail;
    private boolean isGroupedByTeam;
    private String sortType;
    private String resultsLink;
    private List<String> sections;
    private List<String> sectionDetails;

    public InstructorFeedbackResultsFilterPanel(boolean isStatsShown,
                                    FeedbackSessionAttributes session, boolean isAllSectionsSelected,
                                    String selectedSection, String selectedSectionDetail, boolean isGroupedByTeam,
                                    String sortType, String resultsLink,
                                    List<String> sections, boolean isMissingResponsesShown) {
        this.isStatsShown = isStatsShown;
        this.courseId = SanitizationHelper.sanitizeForHtml(session.getCourseId());
        this.feedbackSessionName = SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName());
        this.isAllSectionsSelected = isAllSectionsSelected;
        this.selectedSection = selectedSection;
        this.selectedSectionDetail = selectedSectionDetail;
        this.isGroupedByTeam = isGroupedByTeam;
        this.sortType = sortType;
        this.resultsLink = resultsLink;
        this.isMissingResponsesShown = isMissingResponsesShown;
        this.sections = sections;
        initializeSectionDetails();
    }

    private void initializeSectionDetails() {
        this.sectionDetails = new ArrayList<>();
        sectionDetails.add("");
        sectionDetails.add("Show response if the giver is in the selected section");
        sectionDetails.add("Show response if the evaluee is in the selected section");
        sectionDetails.add("Show response only if both are in the selected section");
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

    public String getSelectedSectionDetail() {
        return selectedSectionDetail;
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

    public List<String> getSectionDetails() {
        return sectionDetails;
    }

}
