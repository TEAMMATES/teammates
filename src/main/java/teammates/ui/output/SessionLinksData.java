package teammates.ui.output;

import java.util.List;

/**
 * The API output format for a user's feedback session links.
 */
public class SessionLinksData implements ApiOutput {

    private final String courseJoinLink;
    private final List<SessionSubmissionLinkData> submissionLinks;
    private final List<SessionResultLinkData> resultsLinks;

    public SessionLinksData(String courseJoinLink, List<SessionSubmissionLinkData> submissionLinks,
            List<SessionResultLinkData> resultsLinks) {
        this.courseJoinLink = courseJoinLink;
        this.submissionLinks = submissionLinks;
        this.resultsLinks = resultsLinks;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }

    public List<SessionSubmissionLinkData> getSubmissionLinks() {
        return submissionLinks;
    }

    public List<SessionResultLinkData> getResultsLinks() {
        return resultsLinks;
    }
}
