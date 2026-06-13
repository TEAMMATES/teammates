package teammates.ui.output;

import java.util.List;

import teammates.common.datatransfer.SessionLinksBundle;
import teammates.common.datatransfer.SessionResultLink;
import teammates.common.datatransfer.SessionSubmissionLink;

/**
 * The API output format for a user's feedback session links.
 */
public class SessionLinksData implements ApiOutput {

    private final String courseJoinLink;
    private final List<SessionSubmissionLink> submissionLinks;
    private final List<SessionResultLink> resultsLinks;

    public SessionLinksData(SessionLinksBundle sessionLinksBundle) {
        this.courseJoinLink = sessionLinksBundle.getCourseJoinLink();
        this.submissionLinks = sessionLinksBundle.getSubmissionLinks();
        this.resultsLinks = sessionLinksBundle.getResultsLinks();
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }

    public List<SessionSubmissionLink> getSubmissionLinks() {
        return submissionLinks;
    }

    public List<SessionResultLink> getResultsLinks() {
        return resultsLinks;
    }
}
